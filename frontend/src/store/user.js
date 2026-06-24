import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLogin = computed(() => !!token.value)

  async function login (username, password) {
    const res = await request.post('/user/login', { username, password })
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
    return res.data
  }

  async function register (username, password, nickname) {
    return await request.post('/user/register', { username, password, nickname })
  }

  async function fetchMe () {
    const res = await request.get('/user/info')
    user.value = res.data
    localStorage.setItem('user', JSON.stringify(user.value))
    return res.data
  }

  function logout () {
    request.post('/user/logout').catch(() => {})
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLogin, login, register, fetchMe, logout }
})
