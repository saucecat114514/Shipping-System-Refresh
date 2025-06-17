import request from '@/utils/request'

// 获取船舶列表
export function getShipList(params) {
  return request({
    url: '/ships',
    method: 'get',
    params
  })
}

// 获取所有船舶(下拉选择用)
export function getAllShips() {
  return request({
    url: '/ships/all',
    method: 'get'
  })
}

// 获取船舶详情
export function getShipDetail(id) {
  return request({
    url: `/ships/${id}`,
    method: 'get'
  })
}

// 获取船舶当前状态
export function getShipStatus(id) {
  return request({
    url: `/ships/${id}/status`,
    method: 'get'
  })
}

// 创建船舶
export function createShip(data) {
  return request({
    url: '/ships',
    method: 'post',
    data
  })
}

// 更新船舶
export function updateShip(id, data) {
  return request({
    url: `/ships/${id}`,
    method: 'put',
    data
  })
}

// 删除船舶
export function deleteShip(id) {
  return request({
    url: `/ships/${id}`,
    method: 'delete'
  })
} 