import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import VehicleView from '../views/VehicleView.vue'
import WorkOrderView from '../views/WorkOrderView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/vehicles',
      name: 'vehicles',
      component: VehicleView,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: WorkOrderView,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // 如果需要登录但未认证，重定向到登录页
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    // 如果已登录但访问登录页，重定向到首页
    next('/')
  } else {
    next()
  }
})

export default router
