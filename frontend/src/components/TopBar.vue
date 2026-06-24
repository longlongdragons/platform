<template>
  <div class="topbar">
    <div class="logo" @click="$router.push('/')">🎬 短视频</div>
    <div class="search" @click="$router.push('/search')">
      <el-input placeholder="搜索视频/用户" readonly />
    </div>
    <div class="user" v-if="userStore.isLogin">
      <el-dropdown @command="onCmd">
        <el-avatar :src="userStore.user?.avatar" :size="32" />
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="me">个人主页</el-dropdown-item>
            <el-dropdown-item command="liked">我的点赞</el-dropdown-item>
            <el-dropdown-item command="favorited">我的收藏</el-dropdown-item>
            <el-dropdown-item command="publish">发布</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div class="login-btn" v-else>
      <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

function onCmd (c) {
  if (c === 'me') router.push('/me')
  else if (c === 'liked') router.push('/liked')
  else if (c === 'favorited') router.push('/favorited')
  else if (c === 'publish') router.push('/publish')
  else if (c === 'logout') {
    userStore.logout()
    ElMessage.success('已退出')
    router.push('/login')
  }
}
</script>

<style scoped>
.topbar { position: fixed; top: 0; left: 0; right: 0; height: 56px; background: #1a1a1a; display: flex; align-items: center;
  padding: 0 16px; z-index: 100; gap: 16px; border-bottom: 1px solid #2a2a2a; }
.logo { font-weight: 700; font-size: 18px; color: #fe2c55; cursor: pointer; }
.search { flex: 1; max-width: 400px; }
:deep(.el-input__wrapper) { background: #2a2a2a !important; box-shadow: none !important; }
:deep(.el-input__inner) { color: #fff !important; cursor: pointer; }
.user, .login-btn { margin-left: auto; }
</style>
