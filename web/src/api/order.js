import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

// 获取客户的订单列表
export function getCustomerOrders(customerId, status) {
  return request({
    url: `/orders/customer/${customerId}/orders`,
    method: 'get',
    params: { status }
  })
}

// 统计订单数量（按状态）
export function countOrdersByStatus(status) {
  return request({
    url: '/orders/count-by-status',
    method: 'get',
    params: { status }
  })
}

// 获取客户订单统计信息
export function getCustomerOrderStats(customerId) {
  return request({
    url: `/orders/customer/${customerId}/stats`,
    method: 'get'
  })
}

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

// 客户创建订单（只选择港口）
export const createCustomerOrder = (data) => {
  return request({
    url: '/orders/customer-create',
    method: 'post',
    data
  })
}

// 获取待分配航次的订单（管理员使用）
export const getPendingAssignmentOrders = (params) => {
  return request({
    url: '/orders/pending-assignment',
    method: 'get',
    params
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

// 取消订单（客户端专用）
export function cancelOrder(id) {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'put'
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

// 分配航次给订单
export function assignVoyageToOrder(orderId, voyageId) {
  return request({
    url: `/orders/${orderId}/assign-voyage`,
    method: 'patch',
    params: {
      voyageId: voyageId
    }
  })
}

// 确认订单（分配航次并计算运价）
export function confirmOrder(orderId, voyageId) {
  return request({
    url: `/orders/${orderId}/confirm`,
    method: 'post',
    params: {
      voyageId: voyageId
    }
  })
}

// 批量更新订单状态
export function batchUpdateOrderStatus(voyageId, status) {
  return request({
    url: '/orders/batch-update-status',
    method: 'patch',
    params: {
      voyageId: voyageId,
      status: status
    }
  })
} 