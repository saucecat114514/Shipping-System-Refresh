import {
  House,
  Ship,
  Location,
  MapLocation,
  Van,
  Calendar,
  Document,
  Setting,
  User,
  Connection
} from '@element-plus/icons-vue'

import { USER_ROLES } from '../utils/constants'

// 管理员/调度员菜单配置
export const adminMenuItems = [
  {
    title: '仪表盘',
    icon: House,
    path: '/dashboard',
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
  },
  {
    title: '港口管理',
    icon: Location,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER],
    children: [
      {
        title: '港口列表',
        path: '/ports',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      },
      {
        title: '港口地图',
        path: '/ports/map',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      }
    ]
  },
  {
    title: '船舶管理',
    icon: Ship,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER],
    children: [
      {
        title: '船舶列表',
        path: '/ships',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      },
      {
        title: '船舶管理',
        path: '/ships/management',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      },
      {
        title: '船舶追踪',
        path: '/ships/tracking',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      }
    ]
  },
  {
    title: '航线管理',
    icon: MapLocation,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER],
    children: [
      {
        title: '航线列表',
        path: '/routes',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      },
      {
        title: '航线规划',
        path: '/routes/planning',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      }
    ]
  },
  {
    title: '航次管理',
    icon: Calendar,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER],
    children: [
      {
        title: '航次列表',
        path: '/voyages',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      },
      // 隐藏航次调度页面
      // {
      //   title: '航次调度',
      //   path: '/voyages/schedule',
      //   roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      // }
    ]
  },
  {
    title: '订单管理',
    icon: Document,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER],
    children: [
      {
        title: '订单列表',
        path: '/orders',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      },
      {
        title: '创建订单',
        path: '/orders/create',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      }
    ]
  },
  {
    title: '用户管理',
    icon: User,
    path: '/users',
    roles: [USER_ROLES.ADMIN]
  },
  {
    title: '系统配置',
    icon: Setting,
    path: '/config',
    roles: [USER_ROLES.ADMIN]
  },
  {
    title: '系统测试',
    icon: Setting,
    path: '/system-test',
    roles: [USER_ROLES.ADMIN]
  },
  {
    title: '连接测试',
    icon: Connection,
    path: '/test-connection',
    roles: [USER_ROLES.ADMIN]
  }
]

// 客户端菜单配置
export const customerMenuItems = [
  {
    title: '主页',
    icon: House,
    path: '/customer/dashboard',
    roles: [USER_ROLES.CUSTOMER]
  },
  {
    title: '港口',
    icon: Location,
    roles: [USER_ROLES.CUSTOMER],
    children: [
      {
        title: '港口列表',
        path: '/customer/ports',
        roles: [USER_ROLES.CUSTOMER]
      },
      {
        title: '港口地图',
        path: '/customer/ports/map',
        roles: [USER_ROLES.CUSTOMER]
      }
    ]
  },
  {
    title: '船舶',
    icon: Ship,
    roles: [USER_ROLES.CUSTOMER],
    children: [
      {
        title: '船舶列表',
        path: '/customer/ships',
        roles: [USER_ROLES.CUSTOMER]
      },
      {
        title: '船舶地图',
        path: '/customer/ships/tracking',
        roles: [USER_ROLES.CUSTOMER]
      }
    ]
  },
  {
    title: '航线',
    icon: MapLocation,
    path: '/customer/routes',
    roles: [USER_ROLES.CUSTOMER]
  },
  {
    title: '航次',
    icon: Calendar,
    path: '/customer/voyages',
    roles: [USER_ROLES.CUSTOMER]
  },
  {
    title: '我的订单',
    icon: Document,
    path: '/customer/orders',
    roles: [USER_ROLES.CUSTOMER]
  },
  {
    title: '创建订单',
    icon: Van,
    path: '/customer/orders/create',
    roles: [USER_ROLES.CUSTOMER]
  }
]

// 兼容性保持，默认导出管理员菜单
export const menuItems = adminMenuItems

// 根据用户角色获取菜单
export const getMenuByRole = (userRole) => {
  if (userRole === USER_ROLES.CUSTOMER) {
    return customerMenuItems
  } else {
    return adminMenuItems
  }
} 