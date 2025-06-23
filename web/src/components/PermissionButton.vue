<template>
  <el-button
    v-if="hasUserPermission"
    v-bind="$attrs"
    @click="$emit('click', $event)"
  >
    <slot />
  </el-button>
</template>

<script setup>
import { computed } from 'vue'
import { hasPermission, getCurrentUserRole } from '../utils/permission'

const props = defineProps({
  permission: {
    type: String,
    required: true
  },
  roles: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['click'])

// 检查用户是否有权限
const hasUserPermission = computed(() => {
  const userRole = getCurrentUserRole()
  
  if (!userRole) {
    return false
  }
  
  // 如果指定了roles，检查用户角色是否在允许的角色列表中
  if (props.roles && props.roles.length > 0) {
    return props.roles.includes(userRole)
  }
  
  // 如果指定了permission，检查用户是否有该权限
  if (props.permission) {
    return hasPermission(userRole, props.permission)
  }
  
  return false
})
</script> 