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

export const menuItems = [
  {
    title: '仪表盘',
    icon: House,
    path: '/dashboard'
  },
  {
    title: '港口管理',
    icon: Location,
    children: [
      {
        title: '港口列表',
        path: '/ports'
      },
      {
        title: '港口地图',
        path: '/ports/map'
      }
    ]
  },
  {
    title: '船舶管理',
    icon: Ship,
    children: [
      {
        title: '船舶列表',
        path: '/ships'
      },
      {
        title: '船舶管理',
        path: '/ships/management'
      },
      {
        title: '船舶追踪',
        path: '/ships/tracking'
      }
    ]
  },
  {
    title: '航线管理',
    icon: MapLocation,
    children: [
      {
        title: '航线列表',
        path: '/routes'
      },
      {
        title: '航线规划',
        path: '/routes/planning'
      }
    ]
  },
  {
    title: '航次管理',
    icon: Calendar,
    children: [
      {
        title: '航次列表',
        path: '/voyages'
      },
      {
        title: '航次调度',
        path: '/voyages/schedule'
      }
    ]
  },
  {
    title: '订单管理',
    icon: Document,
    children: [
      {
        title: '订单列表',
        path: '/orders'
      },
      {
        title: '创建订单',
        path: '/orders/create'
      }
    ]
  },
  {
    title: '用户管理',
    icon: User,
    path: '/users'
  },
  {
    title: '系统配置',
    icon: Setting,
    path: '/config'
  },
  {
    title: '系统测试',
    icon: Setting,
    path: '/system-test'
  },
  {
    title: '连接测试',
    icon: Connection,
    path: '/test-connection'
  }
] 