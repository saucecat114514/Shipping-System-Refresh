<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="260px" class="sidebar">
      <div class="logo">
        <h3>航运管理系统</h3>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF"
        router
      >
        <!-- 动态菜单渲染 -->
        <template v-for="item in filteredMenuItems">
          <!-- 有子菜单的项 -->
          <el-sub-menu 
            v-if="item.children && item.children.length > 0" 
            :key="'sub-' + item.title"
            :index="item.title"
          >
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item 
              v-for="child in item.children" 
              :key="child.path" 
              :index="child.path"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 单个菜单项 -->
          <el-menu-item 
            v-else 
            :key="'item-' + item.path"
            :index="item.path"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumbTitle">{{ breadcrumbTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 用户角色显示 -->
          <el-tag 
            :type="roleTagType" 
            size="small" 
            class="role-tag"
          >
            {{ roleDisplayName }}
          </el-tag>
          
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userInfo.realName || userInfo.username || '用户' }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <el-icon><InfoFilled /></el-icon>
                  {{ userInfo.email || '未设置邮箱' }}
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主体内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, ArrowDown, SwitchButton, InfoFilled } from '@element-plus/icons-vue'

import { menuItems } from '../config/menu'
import { PermissionManager } from '../utils/permission'
import { USER_ROLES, USER_ROLE_LABELS } from '../utils/constants'

const route = useRoute()
const router = useRouter()

// 用户信息
const userInfo = ref({
  username: '',
  realName: '',
  role: '',
  email: ''
})

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 根据用户角色过滤菜单
const filteredMenuItems = computed(() => {
  console.log('=== 菜单过滤调试 ===')
  console.log('用户角色:', userInfo.value.role)
  console.log('原始菜单数量:', menuItems.length)
  
  if (!userInfo.value.role) {
    console.log('用户角色为空，返回空菜单')
    return []
  }
  
  const filtered = PermissionManager.filterMenuByRole(menuItems, userInfo.value.role)
  console.log('过滤后菜单数量:', filtered.length)
  console.log('过滤后菜单:', filtered.map(item => ({ title: item.title, path: item.path })))
  
  return filtered
})

// 角色显示名称
const roleDisplayName = computed(() => {
  return USER_ROLE_LABELS[userInfo.value.role] || userInfo.value.role
})

// 角色标签类型
const roleTagType = computed(() => {
  switch (userInfo.value.role) {
    case USER_ROLES.ADMIN:
      return 'danger'
    case USER_ROLES.DISPATCHER:
      return 'warning'
    case USER_ROLES.CUSTOMER:
      return 'info'
    default:
      return ''
  }
})

// 面包屑标题
const breadcrumbTitle = computed(() => {
  const titleMap = {
    '/dashboard': '数据面板',
    '/ports': '港口列表',
    '/ports/map': '港口地图',
    '/ships': '船舶列表',
    '/ships/management': '船舶管理',
    '/ships/tracking': '船舶追踪',
    '/routes': '航线列表',
    '/routes/planning': '航线规划',
    '/voyages': '航次列表',
    '/voyages/schedule': '航次调度',
    '/orders': '订单列表',
    '/orders/create': '创建订单',
    '/users': '用户管理',
    '/config': '系统配置',
    '/system-test': '系统测试',
    '/test-connection': '连接测试'
  }
  return titleMap[route.path] || ''
})

// 获取用户信息
const getUserInfo = () => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // 从localStorage获取用户信息
      const savedUserInfo = localStorage.getItem('user')
      if (savedUserInfo) {
        const userData = JSON.parse(savedUserInfo)
        userInfo.value = {
          username: userData.username || '',
          realName: userData.realName || '',
          role: userData.role || '',
          email: userData.email || ''
        }
        
        // 用户信息加载完成后再检查权限
        nextTick(() => {
          checkPermission()
        })
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取用户信息失败，跳转到登录页
      logout()
    }
  } else {
    // 没有token，跳转到登录页
    router.push('/login')
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('退出登录成功')
  router.push('/login')
}

// 权限检查
const checkPermission = () => {
  const currentPath = route.path
  const userRole = userInfo.value.role
  
  // 如果用户角色还没加载，暂时不检查权限
  if (!userRole) {
    return
  }
  
  // 对于/dashboard路径，所有登录用户都可以访问
  if (currentPath === '/dashboard') {
    return
  }
  
  // 检查当前页面是否在用户有权限的菜单中
  const hasAccess = checkPathAccess(filteredMenuItems.value, currentPath)
  
  if (!hasAccess && currentPath !== '/login' && currentPath !== '/register') {
    ElMessage.error('您没有权限访问该页面')
    router.push('/dashboard')
  }
}

// 递归检查路径访问权限
const checkPathAccess = (menuItems, targetPath) => {
  for (const item of menuItems) {
    if (item.path === targetPath) {
      return true
    }
    if (item.children && checkPathAccess(item.children, targetPath)) {
      return true
    }
  }
  return false
}

onMounted(() => {
  getUserInfo()
})

// 监听路由变化，检查权限
import { watch } from 'vue'
watch(() => route.path, () => {
  // 确保用户信息已加载后再检查权限
  if (userInfo.value.role) {
    checkPermission()
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  border-right: solid 1px #e6e6e6;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: solid 1px #434a50;
}

.logo h3 {
  color: #fff;
  margin: 0;
  font-size: 16px;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu .el-sub-menu__title {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-tag {
  margin-right: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  color: #606266;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.main-content {
  padding: 0;
  background-color: #f4f6f8;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 64px !important;
  }
  
  .logo h3 {
    display: none;
  }
  
  .sidebar-menu .el-sub-menu__title span,
  .sidebar-menu .el-menu-item span {
    display: none;
  }
  
  .role-tag {
    display: none;
  }
}
</style> 