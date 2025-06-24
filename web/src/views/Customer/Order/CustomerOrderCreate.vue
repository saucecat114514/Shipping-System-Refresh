<template>
  <div class="customer-order-create">
    <el-card>
      <template #header>
        <div class="card-header">
          <div>
            <h3>创建新订单</h3>
            <p>请选择出发港口和目的港口，填写货物信息。航次将由管理员为您分配。</p>
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
        <!-- 基本信息 -->
        <el-divider>基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单编号">
              <el-input 
                v-model="formData.orderNumber" 
                placeholder="系统自动生成"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 港口选择 -->
        <el-divider>港口选择</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出发港口" prop="originPortId">
              <el-select 
                v-model="formData.originPortId" 
                placeholder="请选择出发港口"
                style="width: 100%"
                filterable
                @change="handlePortChange"
              >
                <el-option
                  v-for="port in portList"
                  :key="port.id"
                  :label="`${port.nameCn} (${port.nameEn})`"
                  :value="port.id"
                >
                  <div style="display: flex; justify-content: space-between;">
                    <span>{{ port.nameCn }}</span>
                    <span style="color: #8492a6; font-size: 13px;">{{ port.nameEn }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的港口" prop="destinationPortId">
              <el-select 
                v-model="formData.destinationPortId" 
                placeholder="请选择目的港口"
                style="width: 100%"
                filterable
                @change="handlePortChange"
              >
                <el-option
                  v-for="port in portList"
                  :key="port.id"
                  :label="`${port.nameCn} (${port.nameEn})`"
                  :value="port.id"
                >
                  <div style="display: flex; justify-content: space-between;">
                    <span>{{ port.nameCn }}</span>
                    <span style="color: #8492a6; font-size: 13px;">{{ port.nameEn }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 货物信息 -->
        <el-divider>货物信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物名称" prop="cargoName">
              <el-input 
                v-model="formData.cargoName" 
                placeholder="请输入货物名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物类型" prop="cargoType">
              <el-select 
                v-model="formData.cargoType" 
                placeholder="请选择货物类型"
                style="width: 100%"
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
          />
        </el-form-item>

        <!-- 提示信息 -->
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>1. 请选择合适的出发港口和目的港口</p>
            <p>2. 航次将由我们的管理员根据您的货物类型和港口选择为您分配</p>
            <p>3. 运费将在分配航次后自动计算</p>
            <p>4. 您可以在"我的订单"中查看订单状态和航次分配情况</p>
          </template>
        </el-alert>

        <el-form-item style="margin-top: 30px;">
          <el-button 
            type="primary" 
            @click="handleSubmit"
            :loading="submitting"
            size="large"
          >
            提交订单
          </el-button>
          <el-button @click="goBack" size="large">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { createCustomerOrder } from '@/api/order'
import { getPortList } from '@/api/port'

const router = useRouter()

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 港口列表
const portList = ref([])

// 表单数据
const formData = reactive({
  orderNumber: '',
  originPortId: null,
  destinationPortId: null,
  cargoName: '',
  cargoType: '',
  cargoWeight: null,
  cargoVolume: null,
  isUrgent: false,
  notes: ''
})

// 表单验证规则
const formRules = {
  originPortId: [
    { required: true, message: '请选择出发港口', trigger: 'change' }
  ],
  destinationPortId: [
    { required: true, message: '请选择目的港口', trigger: 'change' }
  ],
  cargoName: [
    { required: true, message: '请输入货物名称', trigger: 'blur' },
    { min: 2, max: 100, message: '货物名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  cargoType: [
    { required: true, message: '请选择货物类型', trigger: 'change' }
  ],
  cargoWeight: [
    { required: true, message: '请输入货物重量', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '货物重量必须大于0.1吨', trigger: 'blur' }
  ],
  cargoVolume: [
    { required: true, message: '请输入货物体积', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '货物体积必须大于0.1立方米', trigger: 'blur' }
  ]
}

// 初始化
onMounted(() => {
  loadPorts()
  generateOrderNumber()
})

// 生成订单编号
const generateOrderNumber = () => {
  const now = new Date()
  const timestamp = now.getTime()
  formData.orderNumber = `ORD${timestamp}`
}

// 加载港口列表
const loadPorts = async () => {
  try {
    loading.value = true
    const response = await getPortList({
      page: 1,
      size: 1000 // 获取所有港口
    })
    if (response.code === 200) {
      portList.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载港口列表失败:', error)
    ElMessage.error('加载港口列表失败')
  } finally {
    loading.value = false
  }
}

// 港口选择变化处理
const handlePortChange = () => {
  // 确保出发港口和目的港口不相同
  if (formData.originPortId && formData.destinationPortId && 
      formData.originPortId === formData.destinationPortId) {
    ElMessage.warning('出发港口和目的港口不能相同')
    formData.destinationPortId = null
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    if (formData.originPortId === formData.destinationPortId) {
      ElMessage.error('出发港口和目的港口不能相同')
      return
    }

    submitting.value = true

    const response = await createCustomerOrder(formData)
    if (response.code === 200) {
      ElMessage.success('订单创建成功！请等待管理员分配航次。')
      goBack()
    } else {
      ElMessage.error(response.msg || '创建订单失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败')
  } finally {
    submitting.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}
</script>

<style scoped>
.customer-order-create {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.card-header h3 {
  margin: 0 0 5px 0;
  color: #303133;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  flex-shrink: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: #409eff;
}

:deep(.el-alert) {
  margin: 20px 0;
}

:deep(.el-alert__content) {
  line-height: 1.6;
}

:deep(.el-alert__content p) {
  margin: 4px 0;
}
</style> 