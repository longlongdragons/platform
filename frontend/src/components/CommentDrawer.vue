<template>
  <el-drawer v-model="visible" :title="`${video?.commentCount || 0} 条评论`" direction="bdr" size="60%">
    <div class="list">
      <div v-for="c in comments" :key="c.id" class="comment">
        <el-avatar :src="c.author?.avatar" :size="36" />
        <div class="body">
          <div class="name">{{ c.author?.nickname || c.author?.username }}</div>
          <div class="content">{{ c.content }}</div>
          <div class="meta">{{ formatTime(c.createTime) }}</div>
        </div>
      </div>
      <div v-if="!comments.length" class="empty">暂无评论, 来抢沙发吧~</div>
    </div>
    <div class="input-bar">
      <el-input v-model="text" placeholder="发条友善的评论吧" @keyup.enter="submit" />
      <el-button type="primary" @click="submit">发送</el-button>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, watch } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const props = defineProps({ visible: Boolean, video: Object })
const emit = defineEmits(['update:visible'])
const visible = ref(props.visible)
watch(() => props.visible, v => visible.value = v)
watch(visible, v => emit('update:visible', v))

const userStore = useUserStore()
const comments = ref([])
const text = ref('')

watch(() => props.video, async (v) => {
  if (v) await loadComments()
}, { immediate: true })

async function loadComments () {
  if (!props.video) return
  const res = await request.get('/comment/list', { params: { videoId: props.video.id, page: 1, size: 50 } })
  comments.value = res.data.list
}

async function submit () {
  if (!text.value.trim()) return
  if (!userStore.isLogin) return ElMessage.warning('请先登录')
  try {
    await request.post('/comment/add', {
      videoId: props.video.id, content: text.value
    })
    text.value = ''
    await loadComments()
    if (props.video) props.video.commentCount = (props.video.commentCount || 0) + 1
    ElMessage.success('评论成功')
  } catch (e) { ElMessage.error(e.message) }
}

function formatTime (t) {
  if (!t) return ''
  return new Date(t).toLocaleString()
}
</script>

<style scoped>
.list { padding: 0 16px 80px; overflow-y: auto; height: calc(100% - 60px); }
.comment { display: flex; gap: 10px; padding: 12px 0; border-bottom: 1px solid #2a2a2a; }
.body { flex: 1; }
.name { color: #999; font-size: 12px; }
.content { color: #fff; margin: 4px 0; }
.meta { color: #666; font-size: 12px; }
.empty { text-align: center; color: #666; padding: 40px 0; }
.input-bar { position: absolute; bottom: 0; left: 0; right: 0; display: flex; gap: 8px; padding: 10px 16px;
  background: #1a1a1a; border-top: 1px solid #2a2a2a; }
:deep(.el-input__wrapper) { background: #2a2a2a !important; box-shadow: none !important; }
:deep(.el-input__inner) { color: #fff !important; }
:deep(.el-drawer) { background: #1a1a1a !important; color: #fff !important; }
:deep(.el-drawer__header) { color: #fff !important; margin-bottom: 0 !important; }
</style>
