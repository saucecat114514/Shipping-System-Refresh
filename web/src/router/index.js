import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import SimpleTest from '@/views/SimpleTest.vue'
// import SystemTest from '@/views/SystemTest.vue'  // 系统测试模块已移除
import MainLayout from '@/layout/MainLayout.vue'
import Dashboard from '@/views/Dashboard.vue'
import PortList from '@/views/Port/PortList.vue'
import PortMap from '@/views/Port/PortMap.vue'
// import TestPortAPI from '@/views/TestPortAPI.vue'  // 测试端口API已移除
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
import OrderAssignment from '@/views/Order/OrderAssignment.vue'
// 用户管理模块
import UserManagement from '@/views/User/UserManagement.vue'
// 系统配置模块
import SystemConfig from '@/views/Config/SystemConfig.vue'
// 连接测试模块已移除
// import TestConnection from '@/views/TestConnection.vue'

// 用户端页面模块
import CustomerDashboard from '@/views/Customer/CustomerDashboard.vue'
import CustomerPortList from '@/views/Customer/Port/CustomerPortList.vue'
import CustomerShipList from '@/views/Customer/Ship/CustomerShipList.vue'
import CustomerRouteList from '@/views/Customer/CustomerRouteList.vue'
import CustomerVoyageList from '@/views/Customer/CustomerVoyageList.vue'
import CustomerMyOrders from '@/views/Customer/Order/CustomerMyOrders.vue'
import CustomerOrderEdit from '@/views/Customer/Order/CustomerOrderEdit.vue'
import CustomerOrderCreate from '@/views/Customer/Order/CustomerOrderCreate.vue'


// import QuickTest from '@/views/QuickTest.vue'
// import DebugPortList from '@/views/DebugPortList.vue'
// import RoleTest from '@/views/RoleTest.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/test',
      name: 'simpleTest',
      component: SimpleTest
    },
    // {
    //   path: '/quick-test',
    //   name: 'quickTest',
    //   component: QuickTest
    // },
    // {
    //   path: '/debug-port',
    //   name: 'debugPort',
    //   component: DebugPortList
    // },
    // {
    //   path: '/system-test',
    //   component: MainLayout,
    //   children: [
    //     {
    //       path: '',
    //       name: 'systemTest',
    //       component: SystemTest
    //     }
    //   ]
    // },
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
        }
        // 隐藏航次调度页面路由
        // {
        //   path: 'schedule',
        //   name: 'voyageSchedule',
        //   component: VoyageSchedule
        // }
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
        },
        {
          path: 'assignment',
          name: 'orderAssignment',
          component: OrderAssignment
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
    // {
    //   path: '/test-port',
    //   component: MainLayout,
    //   children: [
    //     {
    //       path: '',
    //       name: 'testPort',
    //       component: TestPortAPI
    //     }
    //   ]
    // },
    // {
    //   path: '/role-test',
    //   component: MainLayout,
    //   children: [
    //     {
    //       path: '',
    //       name: 'roleTest',
    //       component: RoleTest
    //     }
    //   ]
    // },
    {
      path: '/customer',
      component: MainLayout,
      redirect: '/customer/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'customerDashboard',
          component: CustomerDashboard
        },
        {
          path: 'ports',
          name: 'customerPortList',
          component: CustomerPortList
        },
        {
          path: 'ports/map',
          name: 'customerPortMap',
          component: PortMap
        },
        {
          path: 'ships',
          name: 'customerShipList',
          component: CustomerShipList
        },
        {
          path: 'ships/tracking',
          name: 'customerShipTracking',
          component: ShipTracking
        },
        {
          path: 'routes',
          name: 'customerRouteList',
          component: CustomerRouteList
        },
        {
          path: 'voyages',
          name: 'customerVoyageList',
          component: CustomerVoyageList
        },
        {
          path: 'orders',
          name: 'customerMyOrders',
          component: CustomerMyOrders
        },
        {
          path: 'orders/edit',
          name: 'customerOrderEdit',
          component: CustomerOrderEdit
        },
        {
          path: 'orders/create',
          name: 'customerOrderCreate',
          component: CustomerOrderCreate
        },
        {
          path: 'orders/assignment',
          name: 'customerOrderAssignment',
          component: OrderAssignment
        }
      ]
    }
  ]
})

// 路由守卫 (临时简化版本，用于调试)
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 允许访问的公开页面
  const publicPages = ['/login', '/test']
  
  // 如果是公开页面，直接允许访问
  if (publicPages.includes(to.path)) {
    next()
    return
  }
  
  // 如果访问登录页面
  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  
  // 临时允许所有页面访问（用于调试前端功能）
  // TODO: 生产环境需要恢复正常的权限检查
  console.log('路由守卫：允许访问', to.path, '，token状态：', !!token)
  next()
  
  // 原来的严格检查逻辑（如果需要可以恢复）
  /*
  if (token) {
    next()
  } else {
    next('/login')
  }
  */
})

export default router
