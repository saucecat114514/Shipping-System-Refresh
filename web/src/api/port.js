import request from '@/utils/request'

// 获取港口列表
export function getPortList(params) {
  return request({
    url: '/ports',
    method: 'get',
    params
  })
}

// 获取所有港口(下拉选择用)
export function getAllPorts() {
  return request({
    url: '/ports/all',
    method: 'get'
  })
}

// 获取港口详情
export function getPortDetail(id) {
  return request({
    url: `/ports/${id}`,
    method: 'get'
  })
}

// 创建港口
export function createPort(data) {
  return request({
    url: '/ports',
    method: 'post',
    data
  })
}

// 更新港口
export function updatePort(id, data) {
  return request({
    url: `/ports/${id}`,
    method: 'put',
    data
  })
}

// 删除港口
export function deletePort(id) {
  return request({
    url: `/ports/${id}`,
    method: 'delete'
  })
} 