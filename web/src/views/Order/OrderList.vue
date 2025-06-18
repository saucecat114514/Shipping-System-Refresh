<template>
  <div class="order-list">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadOrderData"
      :delete-data="deleteOrderData"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #route="{ row }">
        {{ row.startPort }} → {{ row.endPort }}
      </template>
      
      <template #transportMode="{ row }">
        <el-tag>{{ getTransportModeLabel(row.transportMode) }}</el-tag>
      </template>

      <template #status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusLabel(row.status) }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <el-button link type="primary" @click="handleView(row)">查看</el-button>
        <el-button 
          link 
          type="primary" 
          @click="handleEdit(row)" 
          v-if="row.status === 'PENDING'">
          编辑
        </el-button>
        <el-button 
          link 
          type="success" 
          @click="handleConfirm(row)" 
          v-if="row.status === 'PENDING'">
          确认
        </el-button>
        <el-button 
          link 
          type="danger" 
          @click="handleCancel(row)" 
          v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'">
          取消
        </el-button>
      </template>
    </DataTable>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单编号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="系统自动生成" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="form.customerName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="起始港口" prop="startPortId">
              <el-select v-model="form.startPortId" placeholder="请选择起始港口" style="width: 100%">
                <el-option 
                  v-for="port in ports" 
                  :key="port.id" 
                  :label="port.name" 
                  :value="port.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的港口" prop="endPortId">
              <el-select v-model="form.endPortId" placeholder="请选择目的港口" style="width: 100%">
                <el-option 
                  v-for="port in ports" 
                  :key="port.id" 
                  :label="port.name" 
                  :value="port.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="运输方式" prop="transportMode">
              <el-select v-model="form.transportMode" placeholder="请选择运输方式" style="width: 100%">
                <el-option label="海运" value="SEA" />
                <el-option label="空运" value="AIR" />
                <el-option label="陆运" value="LAND" />
                <el-option label="多式联运" value="MULTIMODAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物类型" prop="cargoType">
              <el-input v-model="form.cargoType" placeholder="如: 集装箱、散货等" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物重量(吨)" prop="cargoWeight">
              <el-input-number
                v-model="form.cargoWeight"
                :min="0"
                :step="0.1"
                :precision="2"
                style="width: 100%"
                placeholder="货物重量"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物体积(m³)" prop="cargoVolume">
              <el-input-number
                v-model="form.cargoVolume"
                :min="0"
                :step="0.1"
                :precision="2"
                style="width: 100%"
                placeholder="货物体积"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计发货日期" prop="expectedDeliveryDate">
              <el-date-picker
                v-model="form.expectedDeliveryDate"
                type="date"
                placeholder="选择发货日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="待确认" value="PENDING" />
                <el-option label="已确认" value="CONFIRMED" />
                <el-option label="运输中" value="IN_TRANSIT" />
                <el-option label="已完成" value="COMPLETED" />
                <el-option label="已取消" value="CANCELLED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="运费" prop="shippingCost">
              <el-input-number
                v-model="form.shippingCost"
                :min="0"
                :step="100"
                :precision="2"
                style="width: 100%"
                placeholder="运费"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="普通" value="NORMAL" />
                <el-option label="紧急" value="URGENT" />
                <el-option label="加急" value="HIGH" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="特殊要求" prop="specialRequirements">
          <el-input
            v-model="form.specialRequirements"
            type="textarea"
            :rows="3"
            placeholder="特殊运输要求或备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import DataTable from '@/components/DataTable.vue'
import { getOrderList, createOrder, updateOrder, deleteOrder } from '@/api/order'
import { getPortList } from '@/api/port'

// 表格列配置
const columns = [
  { prop: 'orderNo', label: '订单编号', width: 180 },
  { prop: 'customerName', label: '客户名称', width: 150 },
  { prop: 'route', label: '运输路线', width: 200, slot: 'route' },
  { prop: 'transportMode', label: '运输方式', width: 100, slot: 'transportMode' },
  { prop: 'cargoWeight', label: '货物重量(吨)', width: 120, align: 'right' },
  { prop: 'status', label: '状态', width: 100, slot: 'status' },
  { prop: 'expectedDeliveryDate', label: '预计发货', width: 120 },
  { prop: 'shippingCost', label: '运费', width: 100, align: 'right' },
  { prop: 'createTime', label: '创建时间', width: 160 },
  { prop: 'actions', label: '操作', width: 250, slot: 'actions', fixed: 'right' }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'orderNo',
    label: '订单编号',
    type: 'input',
    placeholder: '请输入订单编号'
  },
  {
    prop: 'customerName',
    label: '客户名称',
    type: 'input',
    placeholder: '请输入客户名称'
  },
  {
    prop: 'status',
    label: '订单状态',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '待确认', value: 'PENDING' },
      { label: '已确认', value: 'CONFIRMED' },
      { label: '运输中', value: 'IN_TRANSIT' },
      { label: '已完成', value: 'COMPLETED' },
      { label: '已取消', value: 'CANCELLED' }
    ]
  },
  {
    prop: 'transportMode',
    label: '运输方式',
    type: 'select',
    placeholder: '请选择运输方式',
    options: [
      { label: '海运', value: 'SEA' },
      { label: '空运', value: 'AIR' },
      { label: '陆运', value: 'LAND' },
      { label: '多式联运', value: 'MULTIMODAL' }
    ]
  }
]

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const dataTableRef = ref()

// 基础数据
const ports = ref([])

// 表单数据
const form = reactive({
  id: null,
  orderNo: '',
  customerName: '',
  contactPerson: '',
  contactPhone: '',
  startPortId: null,
  endPortId: null,
  transportMode: 'SEA',
  cargoType: '',
  cargoWeight: null,
  cargoVolume: null,
  expectedDeliveryDate: '',
  status: 'PENDING',
  shippingCost: null,
  priority: 'NORMAL',
  specialRequirements: ''
})

// 表单验证规则
const rules = {
  customerName: [
    { required: true, message: '请输入客户名称', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  startPortId: [
    { required: true, message: '请选择起始港口', trigger: 'change' }
  ],
  endPortId: [
    { required: true, message: '请选择目的港口', trigger: 'change' }
  ],
  transportMode: [
    { required: true, message: '请选择运输方式', trigger: 'change' }
  ],
  cargoType: [
    { required: true, message: '请输入货物类型', trigger: 'blur' }
  ],
  cargoWeight: [
    { required: true, message: '请输入货物重量', trigger: 'blur' }
  ],
  expectedDeliveryDate: [
    { required: true, message: '请选择预计发货日期', trigger: 'change' }
  ]
}

// 获取运输方式标签
const getTransportModeLabel = (mode) => {
  const modeMap = {
    'SEA': '海运',
    'AIR': '空运',
    'LAND': '陆运',
    'MULTIMODAL': '多式联运'
  }
  return modeMap[mode] || mode
}

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'IN_TRANSIT': '运输中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'primary',
    'IN_TRANSIT': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 数据加载函数
const loadOrderData = async (params) => {
  try {
    const result = await getOrderList(params)
    return result
  } catch (error) {
    console.error('加载订单数据失败:', error)
    throw error
  }
}

// 删除数据函数
const deleteOrderData = async (id) => {
  try {
    await deleteOrder(id)
  } catch (error) {
    console.error('删除订单失败:', error)
    throw error
  }
}

// 加载港口列表
const loadPorts = async () => {
  try {
    const result = await getPortList({ page: 1, size: 1000 })
    ports.value = result.data || []
  } catch (error) {
    console.error('加载港口列表失败:', error)
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增订单'
  isEdit.value = false
  // 生成订单编号
  const now = new Date()
  const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '')
  const timeStr = now.getTime().toString().slice(-4)
  form.orderNo = `ORD${dateStr}${timeStr}`
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑订单'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  // TODO: 实现查看详情功能
  ElMessage.info('查看功能待实现')
}

// 确认订单
const handleConfirm = (row) => {
  ElMessageBox.confirm(
    `确定要确认订单"${row.orderNo}"吗？`,
    '确认订单',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateOrder(row.id, { ...row, status: 'CONFIRMED' })
      ElMessage.success('订单已确认')
      if (dataTableRef.value) {
        dataTableRef.value.refresh()
      }
    } catch (error) {
      ElMessage.error('确认订单失败')
    }
  })
}

// 取消订单
const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确定要取消订单"${row.orderNo}"吗？`,
    '确认取消',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateOrder(row.id, { ...row, status: 'CANCELLED' })
      ElMessage.success('订单已取消')
      if (dataTableRef.value) {
        dataTableRef.value.refresh()
      }
    } catch (error) {
      ElMessage.error('取消订单失败')
    }
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateOrder(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createOrder(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        // 刷新列表
        if (dataTableRef.value) {
          dataTableRef.value.refresh()
        }
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    id: null,
    orderNo: '',
    customerName: '',
    contactPerson: '',
    contactPhone: '',
    startPortId: null,
    endPortId: null,
    transportMode: 'SEA',
    cargoType: '',
    cargoWeight: null,
    cargoVolume: null,
    expectedDeliveryDate: '',
    status: 'PENDING',
    shippingCost: null,
    priority: 'NORMAL',
    specialRequirements: ''
  })
}

// 组件挂载时加载港口列表
onMounted(() => {
  loadPorts()
})
</script>

<style scoped>
.order-list {
  height: 100%;
}
</style> 