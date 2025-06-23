import { USER_ROLES } from './constants'

/**
 * 权限管理工具类
 */
export class PermissionManager {
  
  /**
   * 检查用户是否有指定权限
   * @param {string} userRole 用户角色
   * @param {string[]} allowedRoles 允许的角色列表
   * @returns {boolean}
   */
  static hasPermission(userRole, allowedRoles) {
    if (!userRole || !allowedRoles || allowedRoles.length === 0) {
      return false
    }
    return allowedRoles.includes(userRole)
  }

  /**
   * 检查用户是否是管理员
   * @param {string} userRole 用户角色
   * @returns {boolean}
   */
  static isAdmin(userRole) {
    return userRole === USER_ROLES.ADMIN
  }

  /**
   * 检查用户是否是调度员
   * @param {string} userRole 用户角色
   * @returns {boolean}
   */
  static isDispatcher(userRole) {
    return userRole === USER_ROLES.DISPATCHER
  }

  /**
   * 检查用户是否是客户
   * @param {string} userRole 用户角色
   * @returns {boolean}
   */
  static isCustomer(userRole) {
    return userRole === USER_ROLES.CUSTOMER
  }

  /**
   * 根据角色过滤菜单项
   * @param {Array} menuItems 菜单项列表
   * @param {string} userRole 用户角色
   * @returns {Array}
   */
  static filterMenuByRole(menuItems, userRole) {
    if (!userRole || !menuItems) {
      return []
    }

    const filtered = []
    
    for (const item of menuItems) {
      // 检查菜单项权限
      if (item.roles && !this.hasPermission(userRole, item.roles)) {
        continue
      }

      // 创建新的菜单项对象
      const newItem = { ...item }

      // 如果有子菜单，递归过滤
      if (item.children && item.children.length > 0) {
        const filteredChildren = this.filterMenuByRole(item.children, userRole)
        
        // 如果子菜单为空，则隐藏父菜单
        if (filteredChildren.length === 0) {
          continue
        }
        
        newItem.children = filteredChildren
      }

      filtered.push(newItem)
    }
    
    return filtered
  }

  /**
   * 获取用户可见的操作按钮
   * @param {Array} actions 操作列表
   * @param {string} userRole 用户角色
   * @returns {Array}
   */
  static getVisibleActions(actions, userRole) {
    if (!userRole || !actions) {
      return []
    }

    return actions.filter(action => {
      return !action.roles || this.hasPermission(userRole, action.roles)
    })
  }
}

/**
 * 角色权限配置
 */
export const ROLE_PERMISSIONS = {
  // 系统管理员权限
  [USER_ROLES.ADMIN]: {
    // 用户管理
    userManagement: true,
    userCreate: true,
    userEdit: true,
    userDelete: true,
    userView: true,
    
    // 港口管理
    portManagement: true,
    portCreate: true,
    portEdit: true,
    portDelete: true,
    portView: true,
    
    // 船舶管理
    shipManagement: true,
    shipCreate: true,
    shipEdit: true,
    shipDelete: true,
    shipView: true,
    
    // 航线管理
    routeManagement: true,
    routeCreate: true,
    routeEdit: true,
    routeDelete: true,
    routeView: true,
    
    // 航次管理
    voyageManagement: true,
    voyageCreate: true,
    voyageEdit: true,
    voyageDelete: true,
    voyageView: true,
    
    // 订单管理
    orderManagement: true,
    orderCreate: true,
    orderEdit: true,
    orderDelete: true,
    orderView: true,
    orderConfirm: true,
    
    // 系统配置
    systemConfig: true,
    configEdit: true,
    configView: true
  },

  // 航运调度员权限
  [USER_ROLES.DISPATCHER]: {
    // 用户管理 - 无权限
    userManagement: false,
    
    // 港口管理
    portManagement: true,
    portCreate: true,
    portEdit: true,
    portDelete: true,
    portView: true,
    
    // 船舶管理
    shipManagement: true,
    shipCreate: true,
    shipEdit: true,
    shipDelete: true,
    shipView: true,
    
    // 航线管理
    routeManagement: true,
    routeCreate: true,
    routeEdit: true,
    routeDelete: true,
    routeView: true,
    
    // 航次管理
    voyageManagement: true,
    voyageCreate: true,
    voyageEdit: true,
    voyageDelete: true,
    voyageView: true,
    
    // 订单管理
    orderManagement: true,
    orderCreate: true,
    orderEdit: true,
    orderDelete: false, // 调度员不能删除订单
    orderView: true,
    orderConfirm: true,
    
    // 系统配置 - 无权限
    systemConfig: false
  },

  // 客户权限
  [USER_ROLES.CUSTOMER]: {
    // 用户管理 - 无权限
    userManagement: false,
    
    // 港口管理 - 只读
    portManagement: false,
    portView: true,
    
    // 船舶管理 - 只读
    shipManagement: false,
    shipView: true,
    
    // 航线管理 - 只读
    routeManagement: false,
    routeView: true,
    
    // 航次管理 - 只读
    voyageManagement: false,
    voyageView: true,
    
    // 订单管理 - 有限权限
    orderManagement: false,
    orderCreate: true, // 客户可以创建订单
    orderEdit: false,  // 客户不能编辑订单
    orderDelete: false, // 客户不能删除订单
    orderView: true,   // 客户只能查看自己的订单
    orderConfirm: false,
    
    // 系统配置 - 无权限
    systemConfig: false
  }
}

/**
 * 检查用户是否有指定权限
 * @param {string} userRole 用户角色
 * @param {string} permission 权限名称
 * @returns {boolean}
 */
export function hasPermission(userRole, permission) {
  if (!userRole || !permission) {
    return false
  }
  
  const rolePermissions = ROLE_PERMISSIONS[userRole]
  return rolePermissions ? rolePermissions[permission] === true : false
}

/**
 * 获取当前用户信息
 * @returns {Object|null}
 */
export function getCurrentUser() {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : null
}

/**
 * 获取当前用户角色
 * @returns {string|null}
 */
export function getCurrentUserRole() {
  const user = getCurrentUser()
  return user ? user.role : null
} 