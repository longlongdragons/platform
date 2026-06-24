<template>
  <div class="login">
    <div class="logo">短视频</div>
    <h2>登录</h2>
    <el-input v-model="username" placeholder="用户名" size="large" />
    <el-input v-model="password" placeholder="密码" type="password" show-password size="large" style="margin-top:12px" />
    <el-button type="primary" size="large" style="width:100%;margin-top:20px" :loading="loading" @click="submit">登录</el-button>
    <div class="link">
      还没有账号？<router-link to="/register">立即注册</router-link>
    </div>
    <div class="tip">测试账号: admin / 123456</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref('admin')
const password = ref('123456')
const loading = ref(false)

async function submit () {
  if (!username.value || !password.value) return ElMessage.warning('请填写完整')
  loading.value = true
  try {
    await userStore.login(username.value, password.value)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login { width: 360px; margin: 80px auto; padding: 30px; background: #1a1a1a; border-radius: 12px; }
.logo { text-align: center; font-size: 28px; font-weight: bold; margin-bottom: 20px; color: #fe2c55; }
h2 { color: #fff; text-align: center; margin-bottom: 20px; }
.link { text-align: center; margin-top: 14px; color: #999; }
.link a { color: #fe2c55; }
.tip { text-align: center; margin-top: 10px; color: #666; font-size: 12px; }
:deep(.el-input__wrapper) { background: #2a2a2a !important; box-shadow: none !important; }
:deep(.el-input__inner) { color: #fff !important; }
</style>
