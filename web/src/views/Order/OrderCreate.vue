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
            <el-form-item label="订单编号" prop="orderNo">
              <el-input v-model="orderForm.orderNo" placeholder="系统自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="orderForm.customerName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 货物信息 -->
        <el-divider>货物信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物类型" prop="cargoType">
              <el-select v-model="orderForm.cargoType" placeholder="请选择货物类型" style="width: 100%">
                <el-option label="普通货物" value="NORMAL" />
                <el-option label="危险品" value="DANGEROUS" />
                <el-option label="冷藏货物" value="REFRIGERATED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物重量(吨)" prop="weight">
              <el-input-number v-model="orderForm.weight" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物体积(m³)" prop="volume">
              <el-input-number v-model="orderForm.volume" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="集装箱数量" prop="containerCount">
              <el-input-number v-model="orderForm.containerCount" :min="1" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 运输信息 -->
        <el-divider>运输信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="起始港口" prop="startPort">
              <el-select v-model="orderForm.startPort" placeholder="请选择起始港口" style="width: 100%">
                <el-option v-for="port in ports" :key="port.code" :label="port.name" :value="port.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的港口" prop="endPort">
              <el-select v-model="orderForm.endPort" placeholder="请选择目的港口" style="width: 100%">
                <el-option v-for="port in ports" :key="port.code" :label="port.name" :value="port.code" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="期望发货日期" prop="expectedDeliveryDate">
              <el-date-picker
                v-model="orderForm.expectedDeliveryDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运输方式" prop="transportMode">
              <el-select v-model="orderForm.transportMode" placeholder="请选择运输方式" style="width: 100%">
                <el-option label="海运" value="SEA" />
                <el-option label="空运" value="AIR" />
                <el-option label="陆运" value="LAND" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 备注信息 -->
        <el-divider>备注信息</el-divider>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="orderForm.remarks"
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

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const formRef = ref<FormInstance>()

// 表单数据
const orderForm = reactive({
  orderNo: '',
  customerName: '',
  cargoType: '',
  weight: 0,
  volume: 0,
  containerCount: 1,
  startPort: '',
  endPort: '',
  expectedDeliveryDate: '',
  transportMode: 'SEA',
  remarks: ''
})

// 表单验证规则
const rules: FormRules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  cargoType: [{ required: true, message: '请选择货物类型', trigger: 'change' }],
  weight: [{ required: true, message: '请输入货物重量', trigger: 'blur' }],
  volume: [{ required: true, message: '请输入货物体积', trigger: 'blur' }],
  containerCount: [{ required: true, message: '请输入集装箱数量', trigger: 'blur' }],
  startPort: [{ required: true, message: '请选择起始港口', trigger: 'change' }],
  endPort: [{ required: true, message: '请选择目的港口', trigger: 'change' }],
  expectedDeliveryDate: [{ required: true, message: '请选择期望发货日期', trigger: 'change' }],
  transportMode: [{ required: true, message: '请选择运输方式', trigger: 'change' }]
}

// 港口列表
const ports = [
  { code: 'SHA', name: '上海港' },
  { code: 'SIN', name: '新加坡港' },
  { code: 'QIN', name: '青岛港' },
  { code: 'PUS', name: '釜山港' }
]

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用创建订单API
      ElMessage.success('订单创建成功')
    }
  })
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
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