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

export const menuItems = [
  {
    title: '仪表盘',
    icon: House,
    path: '/dashboard',
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
  },
  {
    title: '港口管理',
    icon: Location,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER],
    children: [
      {
        title: '港口列表',
        path: '/ports',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
      },
      {
        title: '港口地图',
        path: '/ports/map',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
      }
    ]
  },
  {
    title: '船舶管理',
    icon: Ship,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER],
    children: [
      {
        title: '船舶列表',
        path: '/ships',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
      },
      {
        title: '船舶管理',
        path: '/ships/management',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER]
      },
      {
        title: '船舶追踪',
        path: '/ships/tracking',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
      }
    ]
  },
  {
    title: '航线管理',
    icon: MapLocation,
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER],
    children: [
      {
        title: '航线列表',
        path: '/routes',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
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
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER],
    children: [
      {
        title: '航次列表',
        path: '/voyages',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
      }
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
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER],
    children: [
      {
        title: '订单列表',
        path: '/orders',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
      },
      {
        title: '创建订单',
        path: '/orders/create',
        roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
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
    title: '权限测试',
    icon: User,
    path: '/role-test',
    roles: [USER_ROLES.ADMIN, USER_ROLES.DISPATCHER, USER_ROLES.CUSTOMER]
  },
  {
    title: '连接测试',
    icon: Connection,
    path: '/test-connection',
    roles: [USER_ROLES.ADMIN]
  }
] 