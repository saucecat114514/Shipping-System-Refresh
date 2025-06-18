import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

// 更新订单
export function updateOrder(id, data) {
  return request({
    url: `/orders/${id}`,
    method: 'put',
    data
  })
}

// 删除订单
export function deleteOrder(id) {
  return request({
    url: `/orders/${id}`,
    method: 'delete'
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(id, data) {
  return request({
    url: `/orders/${id}/status`,
    method: 'put',
    data
  })
}

// 计算订单运价
export function calculateOrderPrice(data) {
  return request({
    url: '/orders/calculate-price',
    method: 'post',
    data
  })
} 