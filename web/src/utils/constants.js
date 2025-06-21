// API 基础配置
export const API_BASE_URL = 'http://localhost:8080/api'

// 用户角色枚举
export const USER_ROLES = {
  ADMIN: 'ADMIN',
  DISPATCHER: 'DISPATCHER', 
  CUSTOMER: 'CUSTOMER'
}

export const USER_ROLE_LABELS = {
  ADMIN: '系统管理员',
  DISPATCHER: '调度员',
  CUSTOMER: '客户'
}

// 用户状态枚举
export const USER_STATUS = {
  DISABLED: 0,
  ENABLED: 1
}

export const USER_STATUS_LABELS = {
  0: '禁用',
  1: '启用'
}

// 船舶状态枚举
export const SHIP_STATUS = {
  DOCKED: 0,     // 停泊
  SAILING: 1,    // 航行中
  ANCHORED: 2,   // 锚泊
  MAINTENANCE: 3 // 维修
}

export const SHIP_STATUS_LABELS = {
  0: '停泊',
  1: '航行中',
  2: '锚泊',
  3: '维修'
}

// 航次状态枚举
export const VOYAGE_STATUS = {
  PLANNED: 'PLANNED',
  IN_PROGRESS: 'IN_PROGRESS',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED'
}

export const VOYAGE_STATUS_LABELS = {
  PLANNED: '计划中',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

// 订单状态枚举
export const ORDER_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  IN_TRANSIT: 'IN_TRANSIT',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED'
}

export const ORDER_STATUS_LABELS = {
  PENDING: '待确认',
  CONFIRMED: '已确认',
  IN_TRANSIT: '运输中',
  DELIVERED: '已送达',
  CANCELLED: '已取消'
}

// 货物类型枚举
export const CARGO_TYPES = [
  { value: '普通货物', label: '普通货物' },
  { value: '危险品', label: '危险品' },
  { value: '冷藏货物', label: '冷藏货物' },
  { value: '超重货物', label: '超重货物' },
  { value: '易碎品', label: '易碎品' }
]

// 分页默认配置
export const PAGINATION = {
  PAGE_SIZE: 10,
  PAGE_SIZES: [10, 20, 50, 100]
}

// 表格配置
export const TABLE_CONFIG = {
  HEIGHT: 'calc(100vh - 280px)',
  MAX_HEIGHT: 600
}

// 日期格式
export const DATE_FORMAT = {
  DATE: 'YYYY-MM-DD',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  TIME: 'HH:mm:ss'
}

// HTTP状态码
export const HTTP_STATUS = {
  SUCCESS: 200,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500
}

// 响应状态码
export const RESPONSE_CODE = {
  SUCCESS: 200,
  ERROR: 500
}

// Element Plus 配置
export const EL_CONFIG = {
  SIZE: 'default',
  LOCALE: 'zh-cn'
}

// 地图配置 
export const MAP_CONFIG = {
  // 高德地图配置
  AMAP: {
    KEY: 'f2163136e1dae2878a9962e856a5f125', // 高德地图API密钥
    SECURITY_KEY: '667209110967108e9147938bfc123095', // 高德地图安全密钥
    VERSION: '2.0',
    PLUGINS: ['AMap.Geocoder', 'AMap.AutoComplete', 'AMap.PlaceSearch', 'AMap.DistrictSearch']
  },
  // 默认中心点 (中国上海)
  CENTER: [121.4648, 31.2304],
  ZOOM: 5
} 