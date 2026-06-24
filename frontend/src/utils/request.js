import axios from 'axios'
import { useUserStore } from '@/store/user'
import router from '@/router'

const instance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

instance.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers['Authorization'] = userStore.token
  }
  return config
})

instance.interceptors.response.use(
  res => {
    const data = res.data
    if (data && data.code !== undefined && data.code !== 0) {
      // 401 跳转登录
      if (data.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }
      return Promise.reject(data)
    }
    return data
  },
  err => {
    if (err.response && err.response.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    }
    return Promise.reject({ code: err.response?.status || 500, message: err.message })
  }
)

export default instance
