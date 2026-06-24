<template>
  <div class="publish">
    <TopBar />
    <div class="wrap">
      <h2>发布作品</h2>
      <el-tabs v-model="type" @tab-change="onTabChange">
        <el-tab-pane label="上传视频" name="1" />
        <el-tab-pane label="图文动态" name="2" />
      </el-tabs>

      <!-- 视频 -->
      <div v-if="type === '1'" class="upload-box">
        <el-upload drag :auto-upload="false" :on-change="onVideoChange" :show-file-list="false" accept="video/*">
          <div v-if="!videoFile" class="ph">
            <el-icon style="font-size:48px"><Plus /></el-icon>
            <p>点击或拖拽上传视频</p>
          </div>
          <video v-else :src="videoPreview" controls class="preview" />
        </el-upload>
        <el-progress v-if="uploading" :percentage="progress" />
      </div>

      <!-- 图文 -->
      <div v-else class="upload-box">
        <el-upload list-type="picture-card" :auto-upload="false" :on-change="onImageChange" :file-list="imageFiles" multiple>
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div class="preview-imgs">
          <img v-for="(url, i) in imagePreviews" :key="i" :src="url" />
        </div>
      </div>

      <el-form label-width="80px" style="margin-top: 20px">
        <el-form-item label="标题">
          <el-input v-model="form.title" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.content" type="textarea" :rows="3" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="逗号分隔, 如: 美食,旅行,生活" />
        </el-form-item>
        <el-form-item label="背景音乐">
          <el-select v-model="form.musicId" placeholder="选择背景音乐" clearable>
            <el-option v-for="m in musicList" :key="m.id" :label="m.name + ' - ' + m.artist" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="字幕">
          <el-input v-model="form.subtitle" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="每行一句字幕，自动按时间显示" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">发布</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import TopBar from '@/components/TopBar.vue'

const router = useRouter()
const type = ref('1')
const form = reactive({ title: '', content: '', tags: '', musicId: null, subtitle: '' })
const videoFile = ref(null)
const videoPreview = ref('')
const imageFiles = ref([])
const imagePreviews = ref([])
const progress = ref(0)
const uploading = ref(false)
const submitting = ref(false)
const musicList = ref([])

const CHUNK_SIZE = 2 * 1024 * 1024

async function loadMusic () {
  try {
    const res = await request.get('/music/list')
    musicList.value = res.data || []
  } catch (e) {
    console.error('加载音乐列表失败', e)
  }
}

function onTabChange () {
  videoFile.value = null
  videoPreview.value = ''
  imageFiles.value = []
  imagePreviews.value = []
}

function onVideoChange (file) {
  videoFile.value = file.raw
  videoPreview.value = URL.createObjectURL(file.raw)
}

function onImageChange (file) {
  imageFiles.value.push(file.raw)
  imagePreviews.value.push(URL.createObjectURL(file.raw))
}

async function uploadVideoInChunks (file) {
  const total = Math.ceil(file.size / CHUNK_SIZE)
  const filename = file.name
  const initRes = await request.post('/upload/init', null, {
    params: { filename, totalSize: file.size, chunkCount: total }
  })
  const uploadId = initRes.data.uploadId

  for (let i = 0; i < total; i++) {
    const start = i * CHUNK_SIZE
    const end = Math.min(start + CHUNK_SIZE, file.size)
    const chunk = file.slice(start, end)
    const fd = new FormData()
    fd.append('file', chunk)
    await request.post('/upload/chunk', fd, {
      params: { uploadId, index: i },
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: e => {
        progress.value = Math.round(((i * CHUNK_SIZE + e.loaded) / file.size) * 100)
      }
    })
  }
  const mergeRes = await request.post('/upload/merge', null, {
    params: { uploadId, filename, subDir: 'videos' }
  })
  return mergeRes.data.url
}

async function uploadImage (file) {
  const fd = new FormData()
  fd.append('file', file)
  const res = await request.post('/upload/simple', fd, {
    params: { subDir: 'images' },
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return res.data.url
}

async function submit () {
  if (!form.title) return ElMessage.warning('请填写标题')
  submitting.value = true
  try {
    let videoUrl = ''
    let images = []
    if (type.value === '1') {
      if (!videoFile.value) return ElMessage.warning('请上传视频')
      uploading.value = true
      videoUrl = await uploadVideoInChunks(videoFile.value)
      uploading.value = false
    } else {
      if (!imageFiles.value.length) return ElMessage.warning('请上传图片')
      images = await Promise.all(imageFiles.value.map(uploadImage))
    }
    await request.post('/video/publish', {
      title: form.title, content: form.content, tags: form.tags,
      videoUrl, type: Number(type.value), images,
      musicId: form.musicId, subtitle: form.subtitle
    })
    ElMessage.success('发布成功')
    router.push('/me')
  } catch (e) {
    ElMessage.error(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadMusic)

onUnmounted(() => {
  if (videoPreview.value) URL.revokeObjectURL(videoPreview.value)
  imagePreviews.value.forEach(u => URL.revokeObjectURL(u))
})
</script>

<style scoped>
.publish { min-height: 100vh; background: #000; }
.wrap { max-width: 600px; margin: 0 auto; padding: 80px 16px 20px; color: #fff; }
h2 { margin-bottom: 20px; }
.upload-box { background: #1a1a1a; padding: 20px; border-radius: 8px; }
.ph { color: #999; padding: 40px 0; text-align: center; }
.preview { width: 100%; max-height: 400px; }
.preview-imgs { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
.preview-imgs img { width: 100px; height: 100px; object-fit: cover; }
:deep(.el-tabs__item) { color: #999 !important; }
:deep(.el-tabs__item.is-active) { color: #fe2c55 !important; }
:deep(.el-tabs__active-bar) { background-color: #fe2c55 !important; }
:deep(.el-textarea__inner), :deep(.el-input__wrapper), :deep(.el-select__wrapper) { background: #2a2a2a !important; color: #fff !important; box-shadow: none !important; }
:deep(.el-form-item__label) { color: #fff !important; }
:deep(.el-select-dropdown) { background: #2a2a2a !important; }
:deep(.el-select-dropdown__item) { color: #fff !important; }
</style>