<template>
  <div class="profile">
    <TopBar />
    <div class="wrap">
      <div class="header">
        <el-avatar :src="user?.avatar" :size="80" />
        <div class="info">
          <h2>{{ user?.nickname || user?.username }}</h2>
          <p class="sig">{{ user?.signature || '这个人很懒,什么都没写' }}</p>
          <p class="username">@{{ user?.username }} · ID: {{ user?.id }}</p>
        </div>
        <div class="stats">
          <div><b>{{ user?.followCount || 0 }}</b><span>关注</span></div>
          <div><b>{{ user?.followerCount || 0 }}</b><span>粉丝</span></div>
          <div><b>{{ videos.length }}</b><span>作品</span></div>
        </div>
      </div>

      <div class="tab-bar" v-if="isOwn">
        <button :class="{ active: activeTab === 'public' }" @click="switchTab('public')">公开作品</button>
        <button :class="{ active: activeTab === 'private' }" @click="switchTab('private')">私密作品</button>
      </div>

      <div class="grid" v-if="displayVideos.length">
        <div v-for="v in displayVideos" :key="v.id" class="grid-item" @click="goDetail(v)">
          <img v-if="v.coverUrl" :src="v.coverUrl" />
          <img v-else-if="v.imageList && v.imageList.length" :src="v.imageList[0]" />
          <div v-else class="placeholder">▶</div>
          <div class="meta">
            <span>❤ {{ v.likeCount || 0 }}</span>
            <span>💬 {{ v.commentCount || 0 }}</span>
            <span v-if="v.music">🎵</span>
          </div>
          <div v-if="isOwn" class="actions" @click.stop>
            <el-button v-if="activeTab === 'public'" size="small" @click="handleHide(v)" type="warning">隐藏</el-button>
            <el-button v-if="activeTab === 'private'" size="small" @click="handleRestore(v)" type="success">恢复</el-button>
            <el-button size="small" @click="handleDelete(v)" type="danger">删除</el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty">暂无作品</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import TopBar from '@/components/TopBar.vue'

const route = useRoute()
const router = useRouter()
const user = ref(null)
const videos = ref([])
const privateVideos = ref([])
const activeTab = ref('public')

const isOwn = computed(() => {
  const currentUser = window.localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null
  return currentUser && user.value && currentUser.id === user.value.id
})

const displayVideos = computed(() => {
  return activeTab.value === 'public' ? videos.value : privateVideos.value
})

async function load () {
  const id = route.params.id || route.path.includes('/me')
  const userId = route.params.id || (window.localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')).id : null)
  if (!userId) return
  const [u, list] = await Promise.all([
    request.get('/user/info/' + userId),
    request.get('/video/byUser/' + userId, { params: { page: 1, size: 30 } })
  ])
  user.value = u.data
  videos.value = list.data
}

async function loadPrivate () {
  const userId = window.localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')).id : null
  if (!userId) return
  const res = await request.get('/video/private', { params: { page: 1, size: 30 } })
  privateVideos.value = res.data
}

function switchTab (tab) {
  activeTab.value = tab
  if (tab === 'private') {
    loadPrivate()
  }
}

function goDetail (v) {
  router.push({ path: '/', query: { id: v.id } })
}

async function handleHide (v) {
  await ElMessageBox.confirm('确定要隐藏该作品吗？隐藏后仅自己可见，可以随时恢复。', '确认隐藏', {
    confirmButtonText: '确定隐藏',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await request.post('/video/hide/' + v.id)
  videos.value = videos.value.filter(item => item.id !== v.id)
}

async function handleRestore (v) {
  await ElMessageBox.confirm('确定要恢复该作品吗？恢复后将重新公开可见。', '确认恢复', {
    confirmButtonText: '确定恢复',
    cancelButtonText: '取消',
    type: 'info'
  })
  await request.post('/video/restore/' + v.id)
  privateVideos.value = privateVideos.value.filter(item => item.id !== v.id)
}

async function handleDelete (v) {
  await ElMessageBox.confirm('确定要删除该作品吗？此操作不可撤销，视频及所有数据将永久清除。', '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'error'
  })
  await request.delete('/video/' + v.id)
  if (activeTab.value === 'public') {
    videos.value = videos.value.filter(item => item.id !== v.id)
  } else {
    privateVideos.value = privateVideos.value.filter(item => item.id !== v.id)
  }
}

onMounted(load)
watch(() => route.params.id, load)
</script>

<style scoped>
.profile { min-height: 100vh; background: #000; }
.wrap { max-width: 800px; margin: 0 auto; padding: 80px 16px 20px; color: #fff; }
.header { display: flex; align-items: center; gap: 20px; background: #1a1a1a; padding: 20px; border-radius: 12px; }
.info { flex: 1; }
h2 { margin: 0; }
.sig { color: #999; font-size: 13px; margin: 6px 0; }
.username { color: #666; font-size: 12px; }
.stats { display: flex; gap: 20px; text-align: center; }
.stats div b { display: block; font-size: 20px; }
.stats div span { color: #999; font-size: 12px; }
.grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 4px; margin-top: 20px; }
.grid-item { position: relative; aspect-ratio: 9/16; background: #1a1a1a; overflow: hidden; cursor: pointer; }
.grid-item img { width: 100%; height: 100%; object-fit: cover; }
.placeholder { color: #666; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 40px; }
.meta { position: absolute; left: 8px; bottom: 8px; font-size: 12px; color: #fff; display: flex; gap: 8px; }
.actions { position: absolute; top: 8px; right: 8px; display: flex; gap: 4px; background: rgba(0,0,0,0.8); padding: 4px; border-radius: 4px; }
.empty { text-align: center; color: #666; padding: 60px 0; }
.tab-bar { display: flex; gap: 16px; margin-top: 20px; border-bottom: 1px solid #333; }
.tab-bar button { background: none; border: none; color: #999; padding: 8px 0; cursor: pointer; font-size: 14px; }
.tab-bar button.active { color: #fff; border-bottom: 2px solid #fff; }
</style>
