<template>
  <div class="user-management">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadUserData"
      :delete-data="deleteUserData"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #role="{ row }">
        <el-tag :type="getRoleType(row.role)">
          {{ getRoleText(row.role) }}
        </el-tag>
      </template>
      
      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </template>
    </DataTable>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetFormData"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
                <el-option label="系统管理员" value="ADMIN" />
                <el-option label="调度员" value="DISPATCHER" />
                <el-option label="客户" value="CUSTOMER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="!isEdit">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="form.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog
      v-model="resetPasswordVisible"
      title="重置密码"
      width="400px"
    >
      <el-form
        ref="resetFormRef"
        :model="resetForm"
        :rules="resetRules"
        label-width="100px"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="resetForm.newPassword" 
            type="password" 
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmNewPassword">
          <el-input 
            v-model="resetForm.confirmNewPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="resetPasswordVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPassword">
          确认重置
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import DataTable from '@/components/DataTable.vue'
import { getUserList, createUser, updateUser, deleteUser, resetUserPassword } from '@/api/user'

// 表格列配置
const columns = [
  { prop: 'username', label: '用户名', minWidth: 120 },
  { prop: 'realName', label: '真实姓名', width: 120 },
  { prop: 'email', label: '邮箱', minWidth: 180 },
  { prop: 'role', label: '角色', width: 120, slot: 'role' },
  { prop: 'status', label: '状态', width: 100, slot: 'status' },
  { prop: 'createdAt', label: '创建时间', width: 160 },
  { 
    prop: 'actions', 
    label: '操作', 
    width: 120, 
    slot: 'actions',
    actions: [
      { 
        label: '重置密码', 
        type: 'warning',
        action: 'resetPassword'
      }
    ]
  }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'username',
    label: '用户名',
    type: 'input',
    placeholder: '请输入用户名'
  },
  {
    prop: 'realName',
    label: '真实姓名',
    type: 'input',
    placeholder: '请输入真实姓名'
  },
  {
    prop: 'role',
    label: '角色',
    type: 'select',
    placeholder: '请选择角色',
    options: [
      { label: '系统管理员', value: 'ADMIN' },
      { label: '调度员', value: 'DISPATCHER' },
      { label: '客户', value: 'CUSTOMER' }
    ]
  },
  {
    prop: 'status',
    label: '状态',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '启用', value: '1' },
      { label: '禁用', value: '0' }
    ]
  }
]

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 重置密码弹窗
const resetPasswordVisible = ref(false)
const resetFormRef = ref()
const currentUserId = ref(null)

// 表单数据
const form = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  role: '',
  password: '',
  confirmPassword: '',
  status: 1
})

// 重置密码表单
const resetForm = reactive({
  newPassword: '',
  confirmNewPassword: ''
})

// 自定义密码确认验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 自定义重置密码确认验证
const validateConfirmNewPassword = (rule, value, callback) => {
  if (value !== resetForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 重置密码验证规则
const resetRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmNewPassword, trigger: 'blur' }
  ]
}

// 角色和状态相关方法
const getRoleType = (role) => {
  const roleMap = {
    'ADMIN': 'danger',
    'DISPATCHER': 'warning', 
    'CUSTOMER': 'success'
  }
  return roleMap[role] || 'info'
}

const getRoleText = (role) => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'DISPATCHER': '调度员',
    'CUSTOMER': '客户'
  }
  return roleMap[role] || role
}

// 数据加载函数
const loadUserData = async (params) => {
  try {
    const result = await getUserList(params)
    return result
  } catch (error) {
    console.error('加载用户数据失败:', error)
    throw error
  }
}

// 删除数据函数
const deleteUserData = async (id) => {
  try {
    await deleteUser(id)
  } catch (error) {
    console.error('删除用户失败:', error)
    throw error
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增用户'
  isEdit.value = false
  resetFormData()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  Object.keys(form).forEach(key => {
    if (key !== 'password' && key !== 'confirmPassword') {
      form[key] = row[key]
    }
  })
  dialogVisible.value = true
}

// 重置密码
const handleResetPasswordAction = (row) => {
  currentUserId.value = row.id
  resetForm.newPassword = ''
  resetForm.confirmNewPassword = ''
  resetPasswordVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    const formData = { ...form }
    
    if (isEdit.value) {
      // 编辑时移除密码字段
      delete formData.password
      delete formData.confirmPassword
      await updateUser(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createUser(formData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    // 刷新表格数据
    const dataTableRef = ref()
    dataTableRef.value?.loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  }
}

// 处理重置密码
const handleResetPassword = async () => {
  try {
    await resetFormRef.value?.validate()
    
    await resetUserPassword(currentUserId.value, {
      newPassword: resetForm.newPassword
    })
    
    ElMessage.success('密码重置成功')
    resetPasswordVisible.value = false
  } catch (error) {
    console.error('重置密码失败:', error)
    ElMessage.error('重置密码失败')
  }
}

// 重置表单
const resetFormData = () => {
  Object.keys(form).forEach(key => {
    if (key === 'status') {
      form[key] = 1
    } else {
      form[key] = ''
    }
  })
  formRef.value?.clearValidate()
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}
</style> 