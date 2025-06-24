<template>
    <div class="order-list">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadOrderData"
      :delete-data="deleteOrderData"
      :show-actions="false"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #customerName="{ row }">
        {{ row.customer ? row.customer.realName : '未知客户' }}
      </template>
      
      <template #departureDate="{ row }">
        {{ row.voyage ? (row.voyage.departureDate ? row.voyage.departureDate.substring(0, 10) : '未安排') : '无航次' }}
      </template>

      <template #status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusLabel(row.status) }}
        </el-tag>
      </template>

      <template #extraActions="{ row }">
        <el-button 
          link 
          type="primary" 
          size="small"
          @click="handleEdit(row)" 
          v-if="row.status === 'PENDING'">
          编辑
        </el-button>
        <el-button 
          link 
          type="success" 
          size="small"
          @click="handleConfirm(row)" 
          v-if="row.status === 'PENDING'">
          确认
        </el-button>
        <el-button 
          link 
          type="danger" 
          size="small"
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
            <el-form-item label="货物名称" prop="cargoName">
              <el-input v-model="form.cargoName" placeholder="请输入货物名称" />
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
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="普通" value="NORMAL" />
                <el-option label="紧急" value="URGENT" />
                <el-option label="加急" value="HIGH" />
              </el-select>
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
  { prop: 'orderNumber', label: '订单编号', width: 180, minWidth: 160 },
  { prop: 'customerName', label: '客户名称', width: 150, minWidth: 120, slot: 'customerName' },
  { prop: 'cargoName', label: '货物名称', width: 150, minWidth: 120 },
  { prop: 'cargoType', label: '货物类型', width: 120, minWidth: 100 },
  { prop: 'cargoWeight', label: '货物重量(吨)', width: 120, minWidth: 100, align: 'right' },
  { prop: 'status', label: '状态', width: 100, minWidth: 80, slot: 'status' },
  { prop: 'departureDate', label: '预计发货', width: 150, minWidth: 120, slot: 'departureDate' },
  { prop: 'totalPrice', label: '运费', width: 100, minWidth: 80, align: 'right' },
  { prop: 'createdAt', label: '创建时间', width: 160, minWidth: 140 },
  { prop: 'extraActions', label: '操作', width: 200, minWidth: 160, slot: 'extraActions', fixed: 'right' }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'orderNumber',
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
  cargoName: '',
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
  cargoName: [
    { required: true, message: '请输入货物名称', trigger: 'blur' }
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
    console.log('加载订单数据，参数:', params)
    const result = await getOrderList(params)
    console.log('订单API原始响应:', result)
    
    // DataTable组件已经处理了result.data解包，这里直接返回即可
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
    ports.value = result.records || result.data || result || []
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
  
  // 将后端数据映射到前端表单字段
  Object.assign(form, {
    id: row.id,
    orderNo: row.orderNumber || row.orderNo || '',
    customerName: row.customer ? row.customer.realName : (row.customerName || ''),
    contactPerson: row.contactPerson || '',
    contactPhone: row.contactPhone || '',
    startPortId: row.startPortId || null,
    endPortId: row.endPortId || null,
    transportMode: row.transportMode || 'SEA',
    cargoName: row.cargoName || '',
    cargoType: row.cargoType || '',
    cargoWeight: row.cargoWeight || null,
    cargoVolume: row.cargoVolume || null,
    expectedDeliveryDate: row.expectedDeliveryDate || '',
    status: row.status || 'PENDING',
    shippingCost: row.totalPrice || row.shippingCost || null,
    priority: row.priority || (row.isUrgent ? 'URGENT' : 'NORMAL'),
    specialRequirements: row.notes || row.specialRequirements || ''
  })
  
  console.log('编辑订单 - 原始数据:', row)
  console.log('编辑订单 - 映射后的表单数据:', form)
  
  dialogVisible.value = true
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
        // 构建符合后端API要求的数据格式
        const requestData = {
          orderNumber: form.orderNo,
          cargoName: form.cargoName || form.cargoType,
          cargoType: form.cargoType,
          cargoWeight: form.cargoWeight,
          cargoVolume: form.cargoVolume,
          status: form.status,
          isUrgent: form.priority === 'URGENT',
          totalPrice: form.shippingCost,
          basePrice: form.shippingCost, // 暂时设为相同
          additionalFees: 0,
          notes: form.specialRequirements,
          // 如果有其他字段，可以在这里添加
          customerId: null, // 需要根据customerName查找客户ID
          voyageId: null    // 需要根据航线信息设置
        }
        
        console.log('提交的数据:', requestData)
        
        if (isEdit.value) {
          await updateOrder(form.id, requestData)
          ElMessage.success('更新成功')
        } else {
          await createOrder(requestData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        // 刷新列表
        if (dataTableRef.value) {
          dataTableRef.value.refresh()
        }
      } catch (error) {
        console.error('提交失败:', error)
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
    cargoName: '',
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
  width: 100%;
}

/* 确保DataTable组件在容器中正确显示 */
:deep(.data-table) {
  height: 100%;
}

/* 优化表格内按钮的间距 */
:deep(.el-button + .el-button) {
  margin-left: 8px;
}

/* 确保固定列的阴影效果 */
:deep(.el-table__fixed-right) {
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
}
</style> 