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
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>数据面板</span>
        </el-menu-item>
        
        <el-sub-menu index="ports">
          <template #title>
            <el-icon><MapLocation /></el-icon>
            <span>港口管理</span>
          </template>
          <el-menu-item index="/ports">港口列表</el-menu-item>
          <el-menu-item index="/ports/map">港口地图</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="ships">
          <template #title>
            <el-icon><Ship /></el-icon>
            <span>船舶管理</span>
          </template>
          <el-menu-item index="/ships">船舶追踪</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/routes">
          <el-icon><Guide /></el-icon>
          <span>航线管理</span>
        </el-menu-item>
        
        <el-menu-item index="/voyages">
          <el-icon><Calendar /></el-icon>
          <span>航次管理</span>
        </el-menu-item>
        
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        
        <el-menu-item index="/test-port">
          <el-icon><Tools /></el-icon>
          <span>API测试</span>
        </el-menu-item>
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
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userInfo.realName || userInfo.username || '用户' }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Odometer,
  MapLocation,
  Ship,
  Guide,
  Calendar,
  List,
  Tools,
  User,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 用户信息
const userInfo = ref({
  username: '',
  realName: '',
  role: ''
})

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 面包屑标题
const breadcrumbTitle = computed(() => {
  const titleMap = {
    '/dashboard': '数据面板',
    '/ports': '港口列表',
    '/ports/map': '港口地图',
    '/ships': '船舶追踪',
    '/routes': '航线管理',
    '/voyages': '航次管理',
    '/orders': '订单管理',
    '/test-port': 'API测试'
  }
  return titleMap[route.path] || ''
})

// 获取用户信息
const getUserInfo = () => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // 这里可以调用API获取用户信息，目前使用localStorage存储的信息
      const savedUserInfo = localStorage.getItem('userInfo')
      if (savedUserInfo) {
        userInfo.value = JSON.parse(savedUserInfo)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('退出登录成功')
  router.push('/login')
}

onMounted(() => {
  getUserInfo()
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
}
</style> 