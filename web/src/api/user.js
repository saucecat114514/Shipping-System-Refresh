import request from '@/utils/request'

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

// 重置用户密码
export function resetUserPassword(id, data) {
  return request({
    url: `/users/${id}/reset-password`,
    method: 'put',
    data
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取所有用户（下拉选择用）
export function getAllUsers() {
  return request({
    url: '/users/all',
    method: 'get'
  })
} 