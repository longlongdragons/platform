<template>
  <div class="search">
    <TopBar />
    <div class="wrap">
      <el-input v-model="keyword" placeholder="搜索视频/标签" size="large" @keyup.enter="search">
        <template #append><el-button @click="search">搜索</el-button></template>
      </el-input>

      <div class="grid" v-if="results.length">
        <div v-for="v in results" :key="v.id" class="card" @click="open(v)">
          <div class="cover">
            <img v-if="v.coverUrl" :src="v.coverUrl" />
            <video v-else-if="v.videoUrl" :src="v.videoUrl" />
            <div v-else class="ph">▶</div>
          </div>
          <div class="t">{{ v.title }}</div>
          <div class="author">@{{ v.author?.nickname || v.author?.username }}</div>
        </div>
      </div>
      <div v-else-if="keyword" class="empty">没有找到相关视频</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import TopBar from '@/components/TopBar.vue'

const router = useRouter()
const keyword = ref('')
const results = ref([])

async function search () {
  if (!keyword.value.trim()) return
  const res = await request.get('/video/search', {
    params: { keyword: keyword.value, page: 1, size: 30 }
  })
  results.value = res.data
}

function open (v) {
  router.push({ path: '/', query: { id: v.id } })
}
</script>

<style scoped>
.search { min-height: 100vh; background: #000; }
.wrap { max-width: 800px; margin: 0 auto; padding: 80px 16px 20px; color: #fff; }
:deep(.el-input__wrapper) { background: #2a2a2a !important; box-shadow: none !important; }
:deep(.el-input__inner) { color: #fff !important; }
.grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-top: 20px; }
.card { cursor: pointer; }
.cover { aspect-ratio: 9/16; background: #1a1a1a; border-radius: 8px; overflow: hidden; }
.cover img, .cover video { width: 100%; height: 100%; object-fit: cover; }
.ph { height: 100%; display: flex; align-items: center; justify-content: center; color: #666; font-size: 40px; }
.t { margin-top: 6px; font-size: 14px; }
.author { color: #999; font-size: 12px; }
.empty { text-align: center; color: #666; padding: 60px 0; }
</style>
