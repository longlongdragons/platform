import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  { path: '/', component: () => import('@/views/Home.vue') },
  { path: '/login', component: () => import('@/views/Login.vue') },
  { path: '/register', component: () => import('@/views/Register.vue') },
  { path: '/publish', component: () => import('@/views/Publish.vue'), meta: { needLogin: true } },
  { path: '/profile/:id', component: () => import('@/views/Profile.vue') },
  { path: '/me', component: () => import('@/views/Profile.vue'), meta: { needLogin: true } },
  { path: '/search', component: () => import('@/views/Search.vue') },
  { path: '/liked', component: () => import('@/views/Liked.vue'), meta: { needLogin: true } },
  { path: '/favorited', component: () => import('@/views/Favorited.vue'), meta: { needLogin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.needLogin) {
    const store = useUserStore()
    if (!store.isLogin) return next('/login')
  }
  next()
})

export default router
