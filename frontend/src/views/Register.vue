<template>
  <div class="register">
    <h2>注册</h2>
    <el-input v-model="username" placeholder="用户名(3-20位)" size="large" />
    <el-input v-model="nickname" placeholder="昵称(可选)" size="large" style="margin-top:12px" />
    <el-input v-model="password" placeholder="密码(6-32位)" type="password" show-password size="large" style="margin-top:12px" />
    <el-button type="primary" size="large" style="width:100%;margin-top:20px" :loading="loading" @click="submit">注册</el-button>
    <div class="link">
      已有账号？<router-link to="/login">去登录</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref(''); const password = ref(''); const nickname = ref(''); const loading = ref(false)

async function submit () {
  if (!username.value || !password.value) return ElMessage.warning('请填写完整')
  if (username.value.length < 3) return ElMessage.warning('用户名至少3位')
  if (password.value.length < 6) return ElMessage.warning('密码至少6位')
  loading.value = true
  try {
    await userStore.register(username.value, password.value, nickname.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register { width: 360px; margin: 80px auto; padding: 30px; background: #1a1a1a; border-radius: 12px; }
h2 { color: #fff; text-align: center; margin-bottom: 20px; }
.link { text-align: center; margin-top: 14px; color: #999; }
.link a { color: #fe2c55; }
:deep(.el-input__wrapper) { background: #2a2a2a !important; box-shadow: none !important; }
:deep(.el-input__inner) { color: #fff !important; }
</style>
