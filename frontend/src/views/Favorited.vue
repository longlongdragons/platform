<template>
  <div class="list">
    <TopBar />
    <div class="wrap">
      <h2>我的收藏</h2>
      <div class="grid" v-if="videos.length">
        <div v-for="v in videos" :key="v.id" class="card" @click="open(v)">
          <div class="cover">
            <img v-if="v.coverUrl" :src="v.coverUrl" />
            <video v-else-if="v.videoUrl" :src="v.videoUrl" />
            <div v-else class="ph">▶</div>
          </div>
          <div class="t">{{ v.title }}</div>
        </div>
      </div>
      <div v-else class="empty">还没有收藏视频</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import TopBar from '@/components/TopBar.vue'

const router = useRouter()
const videos = ref([])
onMounted(async () => {
  const res = await request.get('/video/favorited', { params: { page: 1, size: 30 } })
  videos.value = res.data
})
function open (v) { router.push({ path: '/', query: { id: v.id } }) }
</script>

<style scoped>
.list { min-height: 100vh; background: #000; }
.wrap { max-width: 800px; margin: 0 auto; padding: 80px 16px 20px; color: #fff; }
h2 { margin-bottom: 16px; }
.grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.card { cursor: pointer; }
.cover { aspect-ratio: 9/16; background: #1a1a1a; border-radius: 8px; overflow: hidden; }
.cover img, .cover video { width: 100%; height: 100%; object-fit: cover; }
.ph { height: 100%; display: flex; align-items: center; justify-content: center; color: #666; font-size: 40px; }
.t { margin-top: 6px; font-size: 14px; }
.empty { text-align: center; color: #666; padding: 60px 0; }
</style>
