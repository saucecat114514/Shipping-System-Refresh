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
export function getCustomerOrders(params) {
  // 从localStorage获取当前用户ID
  const userStr = localStorage.getItem('user')
  const token = localStorage.getItem('token')
  
  if (!userStr || !token) {
    return Promise.reject(new Error('用户未登录'))
  }
  
  try {
    const user = JSON.parse(userStr)
    const customerId = user.userId || user.id  // 兼容不同的字段名
    
    if (!customerId) {
      console.error('用户对象:', user)
      return Promise.reject(new Error('用户ID无效'))
    }
    
    return request({
      url: `/orders/customer/${customerId}/orders`,
      method: 'get',
      params
    })
  } catch (error) {
    console.error('解析用户信息失败:', error)
    return Promise.reject(new Error('用户信息解析失败'))
  }
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