<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
onMounted(() => {
  if (userStore.isLogin) userStore.fetchMe().catch(() => {})
})
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
html, body, #app { width: 100%; height: 100%; }
body { font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", sans-serif;
  background: #000; color: #fff; }
a { color: inherit; text-decoration: none; }
img { display: block; }
</style>
