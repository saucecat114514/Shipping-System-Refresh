import request from '@/utils/request'

// 获取系统配置
export function getSystemConfig() {
  return request({
    url: '/config',
    method: 'get'
  })
}

// 更新系统配置
export function updateSystemConfig(data) {
  return request({
    url: '/config',
    method: 'put',
    data
  })
}

// 获取指定配置项
export function getConfigByKey(key) {
  return request({
    url: `/config/${key}`,
    method: 'get'
  })
}

// 更新指定配置项
export function updateConfigByKey(key, data) {
  return request({
    url: `/config/${key}`,
    method: 'put',
    data
  })
}

// 重置配置为默认值
export function resetConfigToDefault() {
  return request({
    url: '/config/reset',
    method: 'post'
  })
} 