<template>
  <div class="home">
    <TopBar />

    <div class="feed-wrap">
      <!-- 单视频详情 -->
      <div v-if="singleVideo" class="feed">
        <div class="item" :key="singleVideo.id">
          <video
            v-if="singleVideo.type === 1 && singleVideo.videoUrl"
            :src="singleVideo.videoUrl"
            :poster="singleVideo.coverUrl"
            loop
            muted
            playsinline
            autoplay
            @ended="onEnd(singleVideo)"
            @click="togglePlay($event)"
          />
          <div v-else class="image-list">
            <img v-if="singleVideo.coverUrl" :src="singleVideo.coverUrl" class="cover" />
            <img v-for="(img, i) in (singleVideo.imageList || [])" :key="i" :src="img" />
            <div v-if="!singleVideo.coverUrl && (!singleVideo.imageList || !singleVideo.imageList.length)" class="img-placeholder">图文</div>
          </div>

          <div class="mask"></div>

          <div class="actions">
            <div class="action" @click="goProfile(singleVideo.userId)">
              <el-avatar :src="singleVideo.author?.avatar" :size="48" />
            </div>
            <div class="action like-action" @click="toggleLike(singleVideo)">
              <div class="icon-btn like-btn" :class="{ active: singleVideo.liked }" :style="{ transform: singleVideo.liked ? 'scale(1.2)' : 'scale(1)' }">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M12 21s-7-4.35-7-10a4.5 4.5 0 0 1 8-2.85A4.5 4.5 0 0 1 19 11c0 5.65-7 10-7 10z"/>
                </svg>
              </div>
              <span>{{ formatNum(singleVideo.likeCount || 0) }}</span>
            </div>
            <div class="action" @click="openComment(singleVideo)">
              <div class="icon-btn">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                </svg>
              </div>
              <span>{{ formatNum(singleVideo.commentCount || 0) }}</span>
            </div>
            <div class="action" @click="toggleFav(singleVideo)">
              <div class="icon-btn fav-btn" :class="{ active: singleVideo.favorited }">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                </svg>
              </div>
              <span>{{ formatNum(singleVideo.favoriteCount || 0) }}</span>
            </div>
            <div class="action" @click="shareVideo(singleVideo)">
              <div class="icon-btn">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92s2.92-1.31 2.92-2.92-1.31-2.92-2.92-2.92z"/>
                </svg>
              </div>
              <span>分享</span>
            </div>
          </div>

          <div class="info">
            <div class="back-btn" @click="clearSingle">← 返回</div>
            <div class="author">@{{ singleVideo.author?.nickname || singleVideo.author?.username }}</div>
            <div class="title">{{ singleVideo.title }}</div>
            <div class="content" v-if="singleVideo.content">{{ singleVideo.content }}</div>
            <div class="tags" v-if="singleVideo.tags">
              <span class="tag" v-for="t in singleVideo.tags.split(',')" :key="t">#{{ t }}</span>
            </div>
            <div class="music" v-if="singleVideo.music">🎵 {{ singleVideo.music.name }} - {{ singleVideo.music.artist }}</div>
            <div class="music" v-else>🎵 原声音乐</div>
            <div class="subtitle" v-if="singleVideo.subtitle">📝 {{ singleVideo.subtitle }}</div>
          </div>
        </div>
      </div>

      <!-- 视频流列表 -->
      <div class="feed" v-else-if="videos.length">
        <div class="item" v-for="v in videos" :key="v.id">
          <video
            v-if="v.type === 1 && v.videoUrl"
            :src="v.videoUrl"
            :poster="v.coverUrl"
            loop
            muted
            playsinline
            autoplay
            @ended="onEnd(v)"
            @click="togglePlay($event)"
          />
          <div v-else class="image-list">
            <img v-if="v.coverUrl" :src="v.coverUrl" class="cover" />
            <img v-for="(img, i) in (v.imageList || [])" :key="i" :src="img" />
            <div v-if="!v.coverUrl && (!v.imageList || !v.imageList.length)" class="img-placeholder">图文</div>
          </div>

          <div class="mask"></div>

          <div class="actions">
            <div class="action" @click="goProfile(v.userId)">
              <el-avatar :src="v.author?.avatar" :size="48" />
            </div>
            <div class="action like-action" @click="toggleLike(v)">
              <div class="icon-btn like-btn" :class="{ active: v.liked }" :style="{ transform: v.liked ? 'scale(1.2)' : 'scale(1)' }">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M12 21s-7-4.35-7-10a4.5 4.5 0 0 1 8-2.85A4.5 4.5 0 0 1 19 11c0 5.65-7 10-7 10z"/>
                </svg>
              </div>
              <span>{{ formatNum(v.likeCount || 0) }}</span>
            </div>
            <div class="action" @click="openComment(v)">
              <div class="icon-btn">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                </svg>
              </div>
              <span>{{ formatNum(v.commentCount || 0) }}</span>
            </div>
            <div class="action" @click="toggleFav(v)">
              <div class="icon-btn fav-btn" :class="{ active: v.favorited }">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                </svg>
              </div>
              <span>{{ formatNum(v.favoriteCount || 0) }}</span>
            </div>
            <div class="action" @click="shareVideo(v)">
              <div class="icon-btn">
                <svg viewBox="0 0 24 24" width="36" height="36" fill="currentColor">
                  <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92s2.92-1.31 2.92-2.92-1.31-2.92-2.92-2.92z"/>
                </svg>
              </div>
              <span>分享</span>
            </div>
          </div>

          <div class="info">
            <div class="author">@{{ v.author?.nickname || v.author?.username }}</div>
            <div class="title">{{ v.title }}</div>
            <div class="content" v-if="v.content">{{ v.content }}</div>
            <div class="tags" v-if="v.tags">
              <span class="tag" v-for="t in v.tags.split(',')" :key="t">#{{ t }}</span>
            </div>
            <div class="music" v-if="v.music">🎵 {{ v.music.name }} - {{ v.music.artist }}</div>
            <div class="music" v-else>🎵 原声音乐</div>
            <div class="subtitle" v-if="v.subtitle">📝 {{ v.subtitle }}</div>
          </div>
        </div>
      </div>
      <div v-else class="empty">
        <p>暂无视频, <router-link to="/publish">发布一个</router-link> 吧</p>
      </div>
    </div>

    <div v-if="commentVisible" class="comment-popup" @click.self="closeComment">
      <div class="comment-mask"></div>
      <div class="comment-panel">
        <div class="comment-header">
          <span>{{ currentVideo?.commentCount || 0 }} 条评论</span>
          <span class="close-btn" @click="closeComment">✕</span>
        </div>
        <div class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <el-avatar :src="c.author?.avatar" :size="40" />
            <div class="comment-body">
              <div class="comment-name">{{ c.author?.nickname || c.author?.username }}</div>
              <div class="comment-content">{{ c.content }}</div>
              <div class="comment-meta">{{ formatTime(c.createTime) }}</div>
            </div>
          </div>
          <div v-if="!comments.length" class="comment-empty">暂无评论, 来抢沙发吧~</div>
        </div>
        <div class="comment-input-bar">
          <el-input v-model="commentText" placeholder="说点什么..." @keyup.enter="submitComment" />
          <el-button type="primary" :disabled="!commentText.trim()" @click="submitComment">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import TopBar from '@/components/TopBar.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const videos = ref([])
const lastId = ref(null)
const loading = ref(false)
const commentVisible = ref(false)
const currentVideo = ref(null)
const singleVideo = ref(null)
const comments = ref([])
const commentText = ref('')

function formatNum (num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

async function load () {
  if (loading.value) return
  loading.value = true
  try {
    const res = await request.get('/video/feed', {
      params: { lastId: lastId.value, type: null, size: 10 }
    })
    if (res.data && res.data.length) {
      lastId.value = res.data[res.data.length - 1].id
      videos.value.push(...res.data)
    }
  } finally {
    loading.value = false
  }
}

async function loadSingle (id) {
  try {
    const res = await request.get('/video/detail/' + id)
    singleVideo.value = res.data
    videos.value = []
  } catch (e) {
    ElMessage.error('视频不存在')
    singleVideo.value = null
    route.query = {}
    load()
  }
}

function onEnd (v) {
  v && (v._ended = true)
}

function togglePlay (e) {
  const v = e.target
  if (v.paused) v.play(); else v.pause()
}

async function toggleLike (v) {
  if (!userStore.isLogin) return router.push('/login')
  try {
    const res = await request.post('/interact/like', null, { params: { videoId: v.id } })
    v.liked = res.data.liked
    v.likeCount = (v.likeCount || 0) + (res.data.liked ? 1 : -1)
    if (singleVideo.value && singleVideo.value.id === v.id) {
      singleVideo.value.liked = res.data.liked
      singleVideo.value.likeCount = v.likeCount
    }
  } catch (e) { ElMessage.error(e.message) }
}

async function toggleFav (v) {
  if (!userStore.isLogin) return router.push('/login')
  try {
    const res = await request.post('/interact/favorite', null, { params: { videoId: v.id } })
    v.favorited = res.data.favorited
    v.favoriteCount = (v.favoriteCount || 0) + (res.data.favorited ? 1 : -1)
    if (singleVideo.value && singleVideo.value.id === v.id) {
      singleVideo.value.favorited = res.data.favorited
      singleVideo.value.favoriteCount = v.favoriteCount
    }
  } catch (e) { ElMessage.error(e.message) }
}

async function toggleFollow (_v) {
  if (!userStore.isLogin) return router.push('/login')
  try {
    const res = await request.post('/follow/toggle', null, { params: { followId: _v.userId } })
    _v.followed = res.data.followed
    if (singleVideo.value && singleVideo.value.id === _v.id) {
      singleVideo.value.followed = res.data.followed
    }
  } catch (e) { ElMessage.error(e.message) }
}

function shareVideo (v) {
  ElMessage.info('分享功能开发中...')
}

async function openComment (v) {
  currentVideo.value = v
  commentVisible.value = true
  await loadComments()
}

function closeComment () {
  commentVisible.value = false
  commentText.value = ''
}

async function loadComments () {
  if (!currentVideo.value) return
  const res = await request.get('/comment/list', { params: { videoId: currentVideo.value.id, page: 1, size: 50 } })
  comments.value = res.data.list || []
}

async function submitComment () {
  if (!commentText.value.trim()) return
  if (!userStore.isLogin) return ElMessage.warning('请先登录')
  try {
    await request.post('/comment/add', {
      videoId: currentVideo.value.id, content: commentText.value
    })
    commentText.value = ''
    await loadComments()
    if (currentVideo.value) currentVideo.value.commentCount = (currentVideo.value.commentCount || 0) + 1
    if (singleVideo.value && singleVideo.value.id === currentVideo.value.id) {
      singleVideo.value.commentCount = currentVideo.value.commentCount
    }
  } catch (e) { ElMessage.error(e.message) }
}

function formatTime (t) {
  if (!t) return ''
  return new Date(t).toLocaleString()
}

function goProfile (id) { router.push('/profile/' + id) }

function onScroll () {
  if (singleVideo.value) return
  if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 200) {
    load()
  }
}

function clearSingle () {
  singleVideo.value = null
  route.query = {}
  lastId.value = null
  videos.value = []
  load()
}

onMounted(() => {
  if (route.query.id) {
    loadSingle(route.query.id)
  } else {
    load()
  }
  window.addEventListener('scroll', onScroll)
})

watch(() => route.query.id, (id) => {
  if (id) loadSingle(id)
})

onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.home { background: #000; min-height: 100vh; }
.feed-wrap { padding-top: 56px; }
.feed { max-width: 480px; margin: 0 auto; }
.item { position: relative; height: calc(100vh - 56px); overflow: hidden; background: #111; }
video { width: 100%; height: 100%; object-fit: cover; }
.image-list { width: 100%; height: 100%; overflow-y: auto; background: #000; }
.image-list img { width: 100%; }
.image-list img.cover { display: block; }
.img-placeholder { color: #fff; text-align: center; padding: 80px 0; }
.mask { position: absolute; inset: 0; background: linear-gradient(transparent 60%, rgba(0,0,0,.7)); pointer-events: none; }

.actions { position: absolute; right: 10px; bottom: 100px; display: flex; flex-direction: column; gap: 24px; align-items: center; }
.action { display: flex; flex-direction: column; align-items: center; gap: 6px; color: #fff; font-size: 12px; }
.icon-btn { width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; color: #fff; transition: all 0.2s ease; }
.icon-btn:hover { transform: scale(1.1); }

.like-btn { color: #fff; transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.like-btn.active { color: #fe2c55; }

.fav-btn { color: #fff; }
.fav-btn.active { color: #ffd700; }

.info { position: absolute; left: 12px; right: 80px; bottom: 20px; color: #fff; }
.author { font-weight: 600; margin-bottom: 6px; }
.title { font-size: 15px; margin-bottom: 4px; }
.content { font-size: 13px; opacity: .9; margin-bottom: 6px; line-height: 1.4; }
.tag { color: #6db1ff; margin-right: 6px; }
.music { font-size: 12px; opacity: .7; }
.empty { color: #999; text-align: center; padding: 100px 0; }
.empty a { color: #fe2c55; }

.comment-popup { position: fixed; inset: 0; z-index: 1000; display: flex; flex-direction: column; justify-content: flex-end; }
.comment-mask { position: absolute; inset: 0; background: rgba(0,0,0,.6); }
.comment-panel { position: relative; background: #1a1a1a; border-radius: 20px 20px 0 0; max-height: 70vh; display: flex; flex-direction: column; animation: slideUp 0.3s ease; }

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.comment-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #333; font-size: 16px; font-weight: 600; }
.close-btn { color: #999; cursor: pointer; padding: 8px; }

.comment-list { flex: 1; overflow-y: auto; padding: 16px; }
.comment-item { display: flex; gap: 12px; padding: 12px 0; border-bottom: 1px solid #2a2a2a; }
.comment-body { flex: 1; }
.comment-name { color: #fff; font-size: 14px; font-weight: 500; margin-bottom: 4px; }
.comment-content { color: #fff; font-size: 13px; line-height: 1.5; margin-bottom: 4px; }
.comment-meta { color: #666; font-size: 12px; }
.comment-empty { text-align: center; color: #666; padding: 40px 0; }

.comment-input-bar { display: flex; gap: 10px; padding: 12px 16px; background: #1a1a1a; border-top: 1px solid #333; }
:deep(.comment-input-bar .el-input__wrapper) { background: #2a2a2a !important; box-shadow: none !important; }
:deep(.comment-input-bar .el-input__inner) { color: #fff !important; }
</style>