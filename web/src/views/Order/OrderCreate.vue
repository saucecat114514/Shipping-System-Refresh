<template>
  <div class="order-create">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>创建订单</span>
        </div>
      </template>

      <el-form ref="formRef" :model="orderForm" :rules="rules" label-width="120px">
        <!-- 基本信息 -->
        <el-divider>基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单编号" prop="orderNumber">
              <el-input v-model="orderForm.orderNumber" placeholder="请输入订单编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input 
                v-model="orderForm.customerName" 
                placeholder="请输入客户姓名" 
                :disabled="isCustomerRole"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 货物信息 -->
        <el-divider>货物信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物名称" prop="cargoName">
              <el-input v-model="orderForm.cargoName" placeholder="请输入货物名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物类型" prop="cargoType">
              <el-select v-model="orderForm.cargoType" placeholder="请选择货物类型" style="width: 100%">
                <el-option label="普通货物" value="普通货物" />
                <el-option label="危险品" value="危险品" />
                <el-option label="冷藏货物" value="冷藏货物" />
                <el-option label="液体货物" value="液体货物" />
                <el-option label="散装货物" value="散装货物" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物重量(吨)" prop="cargoWeight">
              <el-input-number v-model="orderForm.cargoWeight" :min="0.1" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物体积(m³)" prop="cargoVolume">
              <el-input-number v-model="orderForm.cargoVolume" :min="0.1" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 其他选项 -->
        <el-divider>其他选项</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否加急">
              <el-switch v-model="orderForm.isUrgent" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 备注信息 -->
        <el-divider>备注信息</el-divider>
        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="orderForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交订单</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '../../api/auth'
import { createOrder } from '../../api/order'

const formRef = ref()

// 是否为客户角色
const isCustomerRole = ref(false)

// 表单数据
const orderForm = reactive({
  orderNumber: '',
  customerName: '',
  cargoName: '',
  cargoType: '',
  cargoWeight: 0,
  cargoVolume: 0,
  isUrgent: false,
  notes: ''
})

// 表单验证规则
const rules = {
  orderNumber: [{ required: true, message: '请输入订单编号', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  cargoName: [{ required: true, message: '请输入货物名称', trigger: 'blur' }],
  cargoType: [{ required: true, message: '请选择货物类型', trigger: 'change' }],
  cargoWeight: [{ required: true, message: '请输入货物重量', trigger: 'blur' }],
  cargoVolume: [{ required: true, message: '请输入货物体积', trigger: 'blur' }]
}

// 生成订单编号
const generateOrderNumber = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  
  orderForm.orderNumber = `ORD${year}${month}${day}${hours}${minutes}${seconds}`
}

// 初始化用户信息
const initUserInfo = async () => {
  try {
    // 获取当前用户信息
    const response = await getCurrentUser()
    if (response.code === 200 && response.data) {
      const currentUser = response.data
      
      // 如果是客户角色，自动填入真实姓名
      if (currentUser.role === 'CUSTOMER') {
        isCustomerRole.value = true
        orderForm.customerName = currentUser.realName || currentUser.username
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 初始化页面
onMounted(async () => {
  await initUserInfo()
  generateOrderNumber()
})

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await createOrder(orderForm)
        if (response.code === 200) {
          ElMessage.success('订单创建成功')
          handleReset()
        } else {
          ElMessage.error(response.msg || '订单创建失败')
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        ElMessage.error('订单创建失败')
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
    
    // 重新设置默认值
    initUserInfo()
    generateOrderNumber()
  }
}
</script>

<style scoped>
.order-create {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-divider {
  margin: 24px 0;
}

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}
</style> 