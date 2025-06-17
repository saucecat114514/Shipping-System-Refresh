import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import SimpleTest from '@/views/SimpleTest.vue'
import MainLayout from '@/layout/MainLayout.vue'
import Dashboard from '@/views/Dashboard.vue'
import PortList from '@/views/Port/PortList.vue'
import PortMap from '@/views/Port/PortMap.vue'
import TestPortAPI from '@/views/TestPortAPI.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/test',
      name: 'simpleTest',
      component: SimpleTest
    },
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/dashboard',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'dashboard',
          component: Dashboard
        }
      ]
    },
    {
      path: '/ports',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'portList',
          component: PortList
        },
        {
          path: 'map',
          name: 'portMap',
          component: PortMap
        }
      ]
    },
    {
      path: '/test-port',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'testPort',
          component: TestPortAPI
        }
      ]
    }
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 允许访问的公开页面
  const publicPages = ['/login', '/test']
  
  // 如果访问登录页面
  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else if (publicPages.includes(to.path)) {
    // 公开页面直接允许访问
    next()
  } else {
    // 访问其他页面需要token
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
