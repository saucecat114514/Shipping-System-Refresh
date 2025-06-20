import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import SimpleTest from '@/views/SimpleTest.vue'
import SystemTest from '@/views/SystemTest.vue'
import MainLayout from '@/layout/MainLayout.vue'
import Dashboard from '@/views/Dashboard.vue'
import PortList from '@/views/Port/PortList.vue'
import PortMap from '@/views/Port/PortMap.vue'
import TestPortAPI from '@/views/TestPortAPI.vue'
import ShipList from '@/views/Ship/ShipList.vue'
import ShipManagement from '@/views/Ship/ShipManagement.vue'
import ShipTracking from '@/views/Ship/ShipTracking.vue'
// 航线管理模块
import RouteList from '@/views/Route/RouteList.vue'
import RoutePlanning from '@/views/Route/RoutePlanning.vue'
// 航次管理模块
import VoyageList from '@/views/Voyage/VoyageList.vue'
import VoyageSchedule from '@/views/Voyage/VoyageSchedule.vue'
// 订单管理模块
import OrderList from '@/views/Order/OrderList.vue'
import OrderCreate from '@/views/Order/OrderCreate.vue'
// 用户管理模块
import UserManagement from '@/views/User/UserManagement.vue'
// 系统配置模块
import SystemConfig from '@/views/Config/SystemConfig.vue'
// 连接测试模块
import TestConnection from '@/views/TestConnection.vue'

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
      path: '/system-test',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'systemTest',
          component: SystemTest
        }
      ]
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
      path: '/ships',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'shipList',
          component: ShipList
        },
        {
          path: 'management',
          name: 'shipManagement',
          component: ShipManagement
        },
        {
          path: 'tracking',
          name: 'shipTracking',
          component: ShipTracking
        }
      ]
    },
    {
      path: '/routes',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'routeList',
          component: RouteList
        },
        {
          path: 'planning',
          name: 'routePlanning',
          component: RoutePlanning
        }
      ]
    },
    {
      path: '/voyages',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'voyageList',
          component: VoyageList
        },
        {
          path: 'schedule',
          name: 'voyageSchedule',
          component: VoyageSchedule
        }
      ]
    },
    {
      path: '/orders',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'orderList',
          component: OrderList
        },
        {
          path: 'create',
          name: 'orderCreate',
          component: OrderCreate
        }
      ]
    },
    {
      path: '/users',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'userManagement',
          component: UserManagement
        }
      ]
    },
    {
      path: '/config',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'systemConfig',
          component: SystemConfig
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
    },
    {
      path: '/test-connection',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'testConnection',
          component: TestConnection
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
