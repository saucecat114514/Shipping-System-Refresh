<template>
  <div class="customer-order-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <div>
            <h3>编辑订单</h3>
            <p>修改订单信息，只有待处理和已确认状态的订单可以编辑</p>
          </div>
          <div class="header-actions">
            <el-button @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        v-loading="loading"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单编号">
              <el-input v-model="formData.orderNumber" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单状态">
              <el-tag :type="getStatusType(formData.status)">
                {{ getStatusText(formData.status) }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物名称" prop="cargoName">
              <el-input 
                v-model="formData.cargoName" 
                placeholder="请输入货物名称"
                :disabled="!canEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物类型" prop="cargoType">
              <el-select 
                v-model="formData.cargoType" 
                placeholder="请选择货物类型"
                style="width: 100%"
                :disabled="!canEdit"
              >
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
              <el-input-number
                v-model="formData.cargoWeight"
                :min="0.1"
                :max="10000"
                :precision="2"
                :step="0.1"
                style="width: 100%"
                :disabled="!canEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物体积(立方米)" prop="cargoVolume">
              <el-input-number
                v-model="formData.cargoVolume"
                :min="0.1"
                :max="10000"
                :precision="2"
                :step="0.1"
                style="width: 100%"
                :disabled="!canEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否加急">
              <el-switch
                v-model="formData.isUrgent"
                active-text="加急"
                inactive-text="普通"
                :disabled="!canEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前航次">
              <el-input 
                :value="formData.voyage?.voyageNumber || '未分配'"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注信息">
          <el-input
            v-model="formData.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
            :disabled="!canEdit"
          />
        </el-form-item>

        <!-- 价格信息 -->
        <el-divider content-position="left">价格信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="基础运费">
              <el-input 
                :value="formData.basePrice ? `¥${formData.basePrice}` : '未计算'"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="附加费用">
              <el-input 
                :value="formData.additionalFees ? `¥${formData.additionalFees}` : '¥0'"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总价">
              <el-input 
                :value="formData.totalPrice ? `¥${formData.totalPrice}` : '未计算'"
                disabled
                style="font-weight: bold;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleSubmit"
            :loading="submitting"
            :disabled="!canEdit"
          >
            保存修改
          </el-button>
          <el-button @click="goBack">取消</el-button>
          <el-button 
            v-if="canEdit && formData.voyageId"
            type="warning"
            @click="recalculatePrice"
            :loading="calculating"
          >
            重新计算价格
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getOrderDetail, updateOrder, calculateOrderPrice } from '@/api/order'

const router = useRouter()
const route = useRoute()

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)
const submitting = ref(false)
const calculating = ref(false)

// 表单数据
const formData = reactive({
  id: null,
  orderNumber: '',
  cargoName: '',
  cargoType: '',
  cargoWeight: null,
  cargoVolume: null,
  isUrgent: false,
  notes: '',
  status: '',
  basePrice: null,
  additionalFees: null,
  totalPrice: null,
  voyageId: null,
  voyage: null
})

// 表单验证规则
const formRules = {
  cargoName: [
    { required: true, message: '请输入货物名称', trigger: 'blur' }
  ],
  cargoType: [
    { required: true, message: '请选择货物类型', trigger: 'change' }
  ],
  cargoWeight: [
    { required: true, message: '请输入货物重量', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '重量必须大于0.1吨', trigger: 'blur' }
  ],
  cargoVolume: [
    { required: true, message: '请输入货物体积', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '体积必须大于0.1立方米', trigger: 'blur' }
  ]
}

// 是否可以编辑
const canEdit = computed(() => {
  return ['PENDING', 'CONFIRMED'].includes(formData.status)
})

// 状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'info',
    'IN_TRANSIT': 'primary',
    'DELIVERED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待处理',
    'CONFIRMED': '已确认',
    'IN_TRANSIT': '运输中',
    'DELIVERED': '已送达',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

// 加载订单详情
const loadOrderDetail = async () => {
  const orderId = route.query.orderId
  if (!orderId) {
    ElMessage.error('订单ID不能为空')
    goBack()
    return
  }

  try {
    loading.value = true
    const result = await getOrderDetail(orderId)
    
    if (result.code === 200) {
      const order = result.data
      Object.assign(formData, {
        id: order.id,
        orderNumber: order.orderNumber,
        cargoName: order.cargoName,
        cargoType: order.cargoType,
        cargoWeight: order.cargoWeight,
        cargoVolume: order.cargoVolume,
        isUrgent: order.isUrgent,
        notes: order.notes,
        status: order.status,
        basePrice: order.basePrice,
        additionalFees: order.additionalFees,
        totalPrice: order.totalPrice,
        voyageId: order.voyageId,
        voyage: order.voyage
      })
    } else {
      ElMessage.error(result.msg || '加载订单详情失败')
      goBack()
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
    goBack()
  } finally {
    loading.value = false
  }
}

// 重新计算价格
const recalculatePrice = async () => {
  if (!formData.voyageId) {
    ElMessage.warning('订单未分配航次，无法计算价格')
    return
  }

  try {
    calculating.value = true
    const priceRequest = {
      cargoWeight: formData.cargoWeight,
      cargoVolume: formData.cargoVolume,
      cargoType: formData.cargoType,
      isUrgent: formData.isUrgent,
      selectedVoyageId: formData.voyageId
    }

    const result = await calculateOrderPrice(priceRequest)
    
    if (result.code === 200) {
      const priceInfo = result.data
      formData.basePrice = priceInfo.basePrice
      formData.additionalFees = priceInfo.additionalFees
      formData.totalPrice = priceInfo.totalPrice
      ElMessage.success('价格重新计算完成')
    } else {
      ElMessage.error(result.msg || '价格计算失败')
    }
  } catch (error) {
    console.error('价格计算失败:', error)
    ElMessage.error('价格计算失败')
  } finally {
    calculating.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!canEdit.value) {
    ElMessage.warning('当前状态的订单不允许编辑')
    return
  }

  try {
    await formRef.value?.validate()
    
    await ElMessageBox.confirm(
      '确认要保存对订单的修改吗？',
      '确认修改',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    submitting.value = true
    
    const updateData = {
      cargoName: formData.cargoName,
      cargoType: formData.cargoType,
      cargoWeight: formData.cargoWeight,
      cargoVolume: formData.cargoVolume,
      isUrgent: formData.isUrgent,
      notes: formData.notes,
      basePrice: formData.basePrice,
      additionalFees: formData.additionalFees,
      totalPrice: formData.totalPrice
    }

    const result = await updateOrder(formData.id, updateData)
    
    if (result.code === 200) {
      ElMessage.success('订单修改成功')
      goBack()
    } else {
      ElMessage.error(result.msg || '订单修改失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('订单修改失败:', error)
      ElMessage.error('订单修改失败')
    }
  } finally {
    submitting.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.push('/customer/orders')
}

// 初始化
onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.customer-order-edit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.card-header h3 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.header-actions {
  flex-shrink: 0;
  margin-left: 20px;
}

.el-form {
  max-width: 800px;
}

.el-divider {
  margin: 30px 0 20px 0;
}

@media (max-width: 768px) {
  .customer-order-edit {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    margin-left: 0;
    margin-top: 10px;
  }
}
</style> 