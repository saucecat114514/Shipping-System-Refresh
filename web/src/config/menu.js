import {
  House,
  Ship,
  Location,
  MapLocation,
  Van,
  Calendar,
  Document,
  Setting
} from '@element-plus/icons-vue'

export const menuItems = [
  {
    title: '仪表盘',
    icon: House,
    path: '/'
  },
  {
    title: '船舶管理',
    icon: Ship,
    children: [
      {
        title: '船舶追踪',
        path: '/ships/tracking'
      }
    ]
  },
  {
    title: '港口管理',
    icon: Location,
    children: [
      {
        title: '港口地图',
        path: '/ports/map'
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
  }
] 