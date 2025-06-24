<template>
  <div class="role-test">
    <el-card class="user-info-card">
      <h3>当前用户信息</h3>
      <p><strong>用户名:</strong> {{ userInfo.username }}</p>
      <p><strong>真实姓名:</strong> {{ userInfo.realName }}</p>
      <p><strong>角色:</strong> {{ roleDisplayName }}</p>
      <p><strong>邮箱:</strong> {{ userInfo.email }}</p>
    </el-card>

    <el-card class="permission-test-card">
      <h3>权限测试</h3>
      
      <div class="permission-section">
        <h4>用户管理权限</h4>
        <PermissionButton 
          type="primary" 
          :roles="['ADMIN']"
          permission="userManagement"
        >
          用户管理 (仅管理员)
        </PermissionButton>
      </div>

      <div class="permission-section">
        <h4>业务管理权限</h4>
        <PermissionButton 
          type="success" 
          :roles="['ADMIN', 'DISPATCHER']"
          permission="portCreate"
        >
          创建港口 (管理员/调度员)
        </PermissionButton>
        <PermissionButton 
          type="warning" 
          :roles="['ADMIN', 'DISPATCHER']"
          permission="shipEdit"
        >
          编辑船舶 (管理员/调度员)
        </PermissionButton>
      </div>

      <div class="permission-section">
        <h4>查看权限</h4>
        <PermissionButton 
          type="info" 
          :roles="['ADMIN', 'DISPATCHER', 'CUSTOMER']"
          permission="portView"
        >
          查看港口 (所有角色)
        </PermissionButton>
        <PermissionButton 
          type="info" 
          :roles="['ADMIN', 'DISPATCHER', 'CUSTOMER']"
          permission="orderView"
        >
          查看订单 (所有角色)
        </PermissionButton>
      </div>

      <div class="permission-section">
        <h4>客户权限</h4>
        <PermissionButton 
          type="primary" 
          :roles="['ADMIN', 'DISPATCHER', 'CUSTOMER']"
          permission="orderCreate"
        >
          创建订单 (所有角色)
        </PermissionButton>
      </div>
    </el-card>

    <el-card class="menu-test-card">
      <h3>菜单权限测试</h3>
      <div class="menu-list">
        <div 
          v-for="menu in filteredMenuItems" 
          :key="menu.title"
          class="menu-item"
        >
          <h4>{{ menu.title }}</h4>
          <ul v-if="menu.children">
            <li v-for="child in menu.children" :key="child.path">
              {{ child.title }} ({{ child.path }})
            </li>
          </ul>
          <p v-else>{{ menu.path }}</p>
        </div>
      </div>
    </el-card>

    <el-card class="role-switch-card">
      <h3>角色切换测试 (仅用于测试)</h3>
      <el-radio-group v-model="testRole" @change="switchRole">
        <el-radio label="ADMIN">系统管理员</el-radio>
        <el-radio label="DISPATCHER">航运调度员</el-radio>
        <el-radio label="CUSTOMER">客户</el-radio>
      </el-radio-group>
      <p class="warning-text">注意：这只是前端测试，实际权限由后端JWT控制</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PermissionButton from '@/components/PermissionButton.vue'
import { menuItems } from '@/config/menu'
import { PermissionManager, getCurrentUser, getCurrentUserRole } from '@/utils/permission'
import { USER_ROLE_LABELS } from '@/utils/constants'

// 用户信息
const userInfo = ref({
  username: '',
  realName: '',
  role: '',
  email: ''
})

// 测试角色
const testRole = ref('')

// 角色显示名称
const roleDisplayName = computed(() => {
  return USER_ROLE_LABELS[userInfo.value.role] || userInfo.value.role
})

// 根据角色过滤的菜单
const filteredMenuItems = computed(() => {
  if (!userInfo.value.role) {
    return []
  }
  return PermissionManager.filterMenuByRole(menuItems, userInfo.value.role)
})

// 获取用户信息
const getUserInfo = () => {
  const user = getCurrentUser()
  if (user) {
    userInfo.value = {
      username: user.username || '',
      realName: user.realName || '',
      role: user.role || '',
      email: user.email || ''
    }
    testRole.value = user.role || ''
  }
}

// 切换角色 (仅用于测试)
const switchRole = (newRole) => {
  const user = getCurrentUser()
  if (user) {
    user.role = newRole
    localStorage.setItem('user', JSON.stringify(user))
    userInfo.value.role = newRole
    ElMessage.success(`已切换到${USER_ROLE_LABELS[newRole]}角色`)
  }
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.role-test {
  padding: 20px;
}

.user-info-card,
.permission-test-card,
.menu-test-card,
.role-switch-card {
  margin-bottom: 20px;
}

.permission-section {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.permission-section h4 {
  margin: 0 0 10px 0;
  color: #606266;
}

.permission-section .el-button {
  margin-right: 10px;
  margin-bottom: 5px;
}

.menu-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.menu-item {
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.menu-item h4 {
  margin: 0 0 8px 0;
  color: #409eff;
}

.menu-item ul {
  margin: 0;
  padding-left: 20px;
}

.menu-item li {
  margin-bottom: 3px;
  color: #606266;
}

.warning-text {
  color: #e6a23c;
  font-size: 12px;
  margin-top: 10px;
}

.role-switch-card .el-radio {
  margin-right: 20px;
}
</style> 