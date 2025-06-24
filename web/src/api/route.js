import request from '@/utils/request'

// 获取航线列表
export function getRouteList(params) {
  return request({
    url: '/routes',
    method: 'get',
    params
  })
}

// 获取所有航线(下拉选择用)
export function getAllRoutes() {
  return request({
    url: '/routes/all',
    method: 'get'
  })
}

// 获取航线详情
export function getRouteDetail(id) {
  return request({
    url: `/routes/${id}`,
    method: 'get'
  })
}

// 创建航线
export function createRoute(data) {
  return request({
    url: '/routes',
    method: 'post',
    data
  })
}

// 更新航线
export function updateRoute(id, data) {
  return request({
    url: `/routes/${id}`,
    method: 'put',
    data
  })
}

// 删除航线
export function deleteRoute(id) {
  return request({
    url: `/routes/${id}`,
    method: 'delete'
  })
}

// 计算航线距离
export function calculateRouteDistance(data) {
  return request({
    url: '/routes/calculate-distance',
    method: 'post',
    data
  })
} 