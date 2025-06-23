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
              <el-input 
                v-model="orderForm.orderNumber" 
                placeholder="系统自动生成" 
                disabled 
              />
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

        <!-- 航次信息 -->
        <el-divider>航次信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="选择航次" prop="selectedVoyageId">
              <el-select 
                v-model="orderForm.selectedVoyageId" 
                placeholder="请选择航次" 
                style="width: 100%"
                filterable
                @change="handleVoyageChange"
              >
                <el-option
                  v-for="voyage in voyageList"
                  :key="voyage.id"
                  :label="`${voyage.voyageNumber} - ${voyage.route?.name || '未知航线'}`"
                  :value="voyage.id"
                >
                  <div style="display: flex; justify-content: space-between;">
                    <span>{{ voyage.voyageNumber }}</span>
                    <span style="color: #8492a6; font-size: 13px;">
                      {{ voyage.route?.name }} 
                      ({{ voyage.route?.distanceNm }}海里)
                      {{ voyage.departureDate ? new Date(voyage.departureDate).toLocaleDateString() : '' }}
                    </span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="selectedVoyage">
          <el-col :span="6">
            <el-form-item label="航次编号">
              <el-input 
                :value="selectedVoyage.voyageNumber" 
                placeholder="选择航次后显示" 
                disabled 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="出发港口">
              <el-input 
                :value="selectedVoyage.route?.originPort?.nameCn || '未知'" 
                placeholder="选择航次后显示" 
                disabled 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="目的港口">
              <el-input 
                :value="selectedVoyage.route?.destinationPort?.nameCn || '未知'" 
                placeholder="选择航次后显示" 
                disabled 
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="航程距离">
              <el-input 
                :value="selectedVoyage.route?.distanceNm ? `${selectedVoyage.route.distanceNm} 海里` : ''" 
                placeholder="选择航次后显示" 
                disabled 
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 其他选项 -->
        <el-divider>其他选项</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否加急">
              <el-switch v-model="orderForm.isUrgent" @change="calculateTotalPrice" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 费用信息 -->
        <el-divider>费用信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="基础运费(元)">
              <el-input 
                v-model="priceInfo.basePrice" 
                placeholder="系统自动计算" 
                disabled 
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="附加费用(元)">
              <el-input 
                v-model="priceInfo.additionalFees" 
                placeholder="系统自动计算" 
                disabled 
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总费用(元)">
              <el-input 
                v-model="priceInfo.totalPrice" 
                placeholder="系统自动计算" 
                disabled 
                class="total-price-input"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <div class="price-tips">
              <el-icon><InfoFilled /></el-icon>
              <span>费用说明：基础运费 = 货物重量 × 航线距离 × 费率(0.5)；附加费用根据货物类型计算；加急订单费率上浮20%</span>
              <div v-if="selectedVoyage" style="margin-top: 4px; font-size: 12px;">
                当前航次：{{ selectedVoyage.voyageNumber }}，航线：{{ selectedVoyage.route?.name }}，距离：{{ selectedVoyage.route?.distanceNm }}海里
              </div>
            </div>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { getCurrentUser } from '../../api/auth'
import { createOrder } from '../../api/order'
import { getVoyageList, getVoyageDetailWithPorts } from '../../api/voyage'

const formRef = ref()

// 是否为客户角色
const isCustomerRole = ref(false)

// 航次相关数据
const voyageList = ref([])
const selectedVoyage = ref(null)

// 表单数据
const orderForm = reactive({
  orderNumber: '',
  customerName: '',
  cargoName: '',
  cargoType: '',
  cargoWeight: 0,
  cargoVolume: 0,
  selectedVoyageId: null,
  isUrgent: false,
  notes: ''
})

// 价格信息
const priceInfo = reactive({
  basePrice: '0.00',
  additionalFees: '0.00',
  totalPrice: '0.00'
})

// 表单验证规则
const rules = {
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  cargoName: [{ required: true, message: '请输入货物名称', trigger: 'blur' }],
  cargoType: [{ required: true, message: '请选择货物类型', trigger: 'change' }],
  cargoWeight: [{ required: true, message: '请输入货物重量', trigger: 'blur' }],
  cargoVolume: [{ required: true, message: '请输入货物体积', trigger: 'blur' }],
  selectedVoyageId: [{ required: true, message: '请选择航次', trigger: 'change' }]
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

// 计算总费用（基于选中的航次）
const calculateTotalPrice = () => {
  // 检查必要字段是否已填写
  if (!orderForm.cargoWeight || orderForm.cargoWeight <= 0 || 
      !orderForm.cargoVolume || orderForm.cargoVolume <= 0 || 
      !orderForm.cargoType || !selectedVoyage.value) {
    // 重置价格信息
    priceInfo.basePrice = '0.00'
    priceInfo.additionalFees = '0.00'
    priceInfo.totalPrice = '0.00'
    return
  }

  // 本地计算价格（使用选中航次的航线距离）
  const baseRate = 0.5
  const distance = selectedVoyage.value.route?.distanceNm || 500 // 使用航次关联航线的海里距离
  
  // 基础运价 = 货物重量 × 航程距离 × 基础费率
  let basePrice = orderForm.cargoWeight * distance * baseRate
  
  // 加急订单费率上浮20%
  if (orderForm.isUrgent) {
    basePrice = basePrice * 1.2
  }
  
  // 计算附加费用
  let additionalFees = 0
  switch (orderForm.cargoType) {
    case '危险品':
      // 危险品附加费：按重量计算固定费用（每吨100元）
      additionalFees = orderForm.cargoWeight * 100
      break
    case '冷藏货物':
      // 冷藏货物附加费：每吨50元
      additionalFees = orderForm.cargoWeight * 50
      break
    case '液体货物':
      // 液体货物附加费：每吨30元
      additionalFees = orderForm.cargoWeight * 30
      break
    default:
      additionalFees = 0
  }
  
  // 计算总价格
  const totalPrice = basePrice + additionalFees
  
  // 更新价格信息
  priceInfo.basePrice = basePrice.toFixed(2)
  priceInfo.additionalFees = additionalFees.toFixed(2)
  priceInfo.totalPrice = totalPrice.toFixed(2)
}

// 处理航次选择变化
const handleVoyageChange = async (voyageId) => {
  if (voyageId) {
    try {
      // 获取航次的详细信息（包含港口信息）
      const response = await getVoyageDetailWithPorts(voyageId)
      if (response.code === 200 && response.data) {
        selectedVoyage.value = response.data
        console.log('航次详细信息:', selectedVoyage.value)
      } else {
        // 如果获取详细信息失败，使用列表中的基本信息
        selectedVoyage.value = voyageList.value.find(voyage => voyage.id === voyageId)
        ElMessage.warning('获取航次详细信息失败，使用基本信息')
      }
    } catch (error) {
      console.error('获取航次详细信息失败:', error)
      // 使用列表中的基本信息作为后备
      selectedVoyage.value = voyageList.value.find(voyage => voyage.id === voyageId)
      ElMessage.warning('获取航次详细信息失败，使用基本信息')
    }
  } else {
    selectedVoyage.value = null
  }
  calculateTotalPrice()
}

// 加载航次列表
const loadVoyageList = async () => {
  try {
    const response = await getVoyageList({ 
      page: 1, 
      size: 100, 
      status: 'PLANNED' // 只获取计划中的航次
    })
    if (response.code === 200 && response.data) {
      voyageList.value = response.data.records || response.data || []
    }
  } catch (error) {
    console.error('加载航次列表失败:', error)
    ElMessage.error('加载航次列表失败')
  }
}

// 监听关键字段变化，自动计算费用
watch([
  () => orderForm.cargoWeight,
  () => orderForm.cargoVolume,
  () => orderForm.cargoType,
  () => orderForm.selectedVoyageId,
  () => orderForm.isUrgent
], () => {
  calculateTotalPrice()
}, { deep: true })

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
  await loadVoyageList()
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
    
    // 重置航次选择
    selectedVoyage.value = null
    orderForm.selectedVoyageId = null
    
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

/* 禁用状态的输入框样式 */
:deep(.el-input.is-disabled .el-input__inner) {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #606266;
  cursor: not-allowed;
}

/* 总费用输入框特殊样式 */
:deep(.total-price-input .el-input__inner) {
  background-color: #f0f9ff;
  border-color: #409EFF;
  color: #409EFF;
  font-weight: bold;
  font-size: 16px;
}

/* 价格提示样式 */
.price-tips {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: #f4f4f5;
  border-radius: 4px;
  color: #606266;
  font-size: 13px;
  margin-top: 8px;
}

.price-tips .el-icon {
  margin-right: 8px;
  color: #909399;
}
</style> 