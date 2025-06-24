import request from '@/utils/request'

// 获取航次列表
export function getVoyageList(params) {
  return request({
    url: '/voyages/with-details',
    method: 'get',
    params
  })
}

// 创建航次
export function createVoyage(data) {
  return request({
    url: '/voyages',
    method: 'post',
    data
  })
}

// 更新航次
export function updateVoyage(id, data) {
  return request({
    url: `/voyages/${id}`,
    method: 'put',
    data
  })
}

// 删除航次
export function deleteVoyage(id) {
  return request({
    url: `/voyages/${id}`,
    method: 'delete'
  })
}

// 获取航次详情
export function getVoyageDetail(id) {
  return request({
    url: `/voyages/${id}`,
    method: 'get'
  })
}

// 获取航次详情（包含港口信息）
export function getVoyageDetailWithPorts(id) {
  return request({
    url: `/voyages/${id}/details`,
    method: 'get'
  })
}

// 更新航次状态
export function updateVoyageStatus(id, status) {
  return request({
    url: `/voyages/${id}/status`,
    method: 'patch',
    params: { status }
  })
} 