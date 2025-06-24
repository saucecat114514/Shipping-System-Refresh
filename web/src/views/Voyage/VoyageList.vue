<template>
  <div class="voyage-list">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadVoyageData"
      :delete-data="deleteVoyageData"
      :show-actions="false"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #shipName="{ row }">
        {{ row.ship ? row.ship.name : '未分配船舶' }}
      </template>
      
      <template #route="{ row }">
        {{ row.route ? row.route.name : '未分配航线' }}
      </template>
      
      <template #deadweightTonnage="{ row }">
        {{ row.ship ? (row.ship.deadweightTonnage || 0) : 0 }}
      </template>
      
      <template #status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusLabel(row.status) }}
        </el-tag>
      </template>

      <template #extraActions="{ row }">
        <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
        <el-button 
          link 
          type="primary" 
          size="small"
          @click="handleEdit(row)" 
          v-if="row.status === 'PLANNED'">
          编辑
        </el-button>
        <el-button 
          link 
          type="success" 
          size="small"
          @click="handleStart(row)" 
          v-if="row.status === 'PLANNED'">
          开始
        </el-button>
        <el-button 
          link 
          type="danger" 
          size="small"
          @click="handleCancel(row)" 
          v-if="row.status === 'PLANNED'">
          取消
        </el-button>
        <el-button 
          link 
          type="danger" 
          size="small"
          @click="handleDeleteVoyage(row)">
          删除
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
            <el-form-item label="航次编号" prop="voyageNo">
              <el-input v-model="form.voyageNo" placeholder="系统自动生成" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="航线" prop="routeId">
              <el-select v-model="form.routeId" placeholder="请选择航线" style="width: 100%">
                <el-option 
                  v-for="route in routes" 
                  :key="route.id" 
                  :label="route.name" 
                  :value="route.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="船舶" prop="shipId">
              <el-select v-model="form.shipId" placeholder="请选择船舶" style="width: 100%">
                <el-option 
                  v-for="ship in ships" 
                  :key="ship.id" 
                  :label="ship.name" 
                  :value="ship.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="计划中" value="PLANNED" />
                <el-option label="进行中" value="IN_PROGRESS" />
                <el-option label="已完成" value="COMPLETED" />
                <el-option label="已取消" value="CANCELLED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划出发时间" prop="plannedDepartureTime">
              <el-date-picker
                v-model="form.plannedDepartureTime"
                type="datetime"
                placeholder="选择出发时间"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划到达时间" prop="plannedArrivalTime">
              <el-date-picker
                v-model="form.plannedArrivalTime"
                type="datetime"
                placeholder="选择到达时间"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="载货量(吨)" prop="cargoWeight">
              <el-input-number
                v-model="form.cargoWeight"
                :min="0"
                :step="100"
                style="width: 100%"
                placeholder="载货量"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计收入" prop="estimatedRevenue">
              <el-input-number
                v-model="form.estimatedRevenue"
                :min="0"
                :step="1000"
                style="width: 100%"
                placeholder="预计收入"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="form.notes"
            type="textarea"
            :rows="3"
            placeholder="航次备注"
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
import { getVoyageList, createVoyage, updateVoyage, deleteVoyage, updateVoyageStatus } from '@/api/voyage'
import { getAllRoutes } from '@/api/route'
import { getAllShips } from '@/api/ship'

// 表格列配置
const columns = [
  { prop: 'voyageNumber', label: '航次编号', width: 140, minWidth: 140 },
  { prop: 'route', label: '航线', width: 180, minWidth: 150, slot: 'route' },
  { prop: 'shipName', label: '船舶名称', width: 120, minWidth: 100, slot: 'shipName' },
  { prop: 'status', label: '状态', width: 80, minWidth: 80, slot: 'status' },
  { prop: 'departureDate', label: '计划出发', width: 140, minWidth: 120 },
  { prop: 'arrivalDate', label: '计划到达', width: 140, minWidth: 120 },
  { prop: 'deadweightTonnage', label: '载重量(吨)', width: 110, minWidth: 100, align: 'right', slot: 'deadweightTonnage' },
  { prop: 'extraActions', label: '航次操作', width: 240, minWidth: 200, slot: 'extraActions', fixed: 'right' }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'voyageNumber',
    label: '航次编号',
    type: 'input',
    placeholder: '请输入航次编号'
  },
  {
    prop: 'shipName',
    label: '船舶名称',
    type: 'input',
    placeholder: '请输入船舶名称'
  },
  {
    prop: 'status',
    label: '航次状态',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '计划中', value: 'PLANNED' },
      { label: '进行中', value: 'IN_PROGRESS' },
      { label: '已完成', value: 'COMPLETED' },
      { label: '已取消', value: 'CANCELLED' }
    ]
  },
  {
    prop: 'routeId',
    label: '航线',
    type: 'select',
    placeholder: '请选择航线',
    options: []
  }
]

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const dataTableRef = ref()

// 基础数据
const routes = ref([])
const ships = ref([])

// 表单数据
const form = reactive({
  id: null,
  voyageNo: '',
  routeId: null,
  shipId: null,
  status: 'PLANNED',
  plannedDepartureTime: '',
  plannedArrivalTime: '',
  cargoWeight: null,
  estimatedRevenue: null,
  notes: ''
})

// 表单验证规则
const rules = {
  routeId: [
    { required: true, message: '请选择航线', trigger: 'change' }
  ],
  shipId: [
    { required: true, message: '请选择船舶', trigger: 'change' }
  ],
  plannedDepartureTime: [
    { required: true, message: '请选择计划出发时间', trigger: 'change' }
  ],
  plannedArrivalTime: [
    { required: true, message: '请选择计划到达时间', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    'PLANNED': '计划中',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PLANNED': 'warning',
    'IN_PROGRESS': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 数据加载函数
const loadVoyageData = async (params) => {
  try {
    console.log('加载航次数据，参数:', params)
    const result = await getVoyageList(params)
    console.log('航次API原始响应:', result)
    
    // DataTable组件已经处理了result.data解包，这里直接返回即可
    return result
  } catch (error) {
    console.error('加载航次数据失败:', error)
    throw error
  }
}

// 删除数据函数
const deleteVoyageData = async (id) => {
  try {
    await deleteVoyage(id)
  } catch (error) {
    console.error('删除航次失败:', error)
    throw error
  }
}

// 加载基础数据
const loadBaseData = async () => {
  try {
    console.log('开始加载基础数据...')
    
    // 加载航线列表
    const routeResult = await getAllRoutes()
    console.log('航线API响应:', routeResult)
    
    // 处理不同的响应格式
    if (routeResult && routeResult.code === 200) {
      routes.value = routeResult.data || []
    } else if (Array.isArray(routeResult)) {
      routes.value = routeResult
    } else {
      routes.value = []
    }
    console.log('航线数据:', routes.value)
    
    // 验证航线数据
    if (routes.value.length === 0) {
      console.warn('航线数据为空')
      ElMessage.warning('暂无可用航线，请先添加航线')
    }
    
    // 更新搜索配置中的航线选项
    const routeSearchConfig = searchConfig.find(item => item.prop === 'routeId')
    if (routeSearchConfig) {
      routeSearchConfig.options = routes.value.map(route => ({
        label: route.name || `航线${route.id}`,
        value: route.id
      }))
    }
    
    // 加载船舶列表
    const shipResult = await getAllShips()
    console.log('船舶API响应:', shipResult)
    
    // 处理不同的响应格式
    if (shipResult && shipResult.code === 200) {
      ships.value = shipResult.data || []
    } else if (Array.isArray(shipResult)) {
      ships.value = shipResult
    } else {
      ships.value = []
    }
    console.log('船舶数据:', ships.value)
    
    // 验证船舶数据
    if (ships.value.length === 0) {
      console.warn('船舶数据为空')
      ElMessage.warning('暂无可用船舶，请先添加船舶')
    }
  } catch (error) {
    console.error('加载基础数据失败:', error)
    ElMessage.error('加载基础数据失败，请刷新页面重试')
  }
}

// 新增
const handleAdd = async () => {
  dialogTitle.value = '新增航次'
  isEdit.value = false
  // 生成航次编号
  const now = new Date()
  const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '')
  const timeStr = now.getTime().toString().slice(-4)
  form.voyageNo = `V${dateStr}${timeStr}`
  
  // 如果基础数据为空，重新加载
  if (routes.value.length === 0 || ships.value.length === 0) {
    console.log('基础数据为空，重新加载...')
    await loadBaseData()
  }
  
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑航次'
  isEdit.value = true
  
  // 将后端数据映射到前端表单字段
  Object.assign(form, {
    id: row.id,
    voyageNo: row.voyageNumber || row.voyageNo,
    routeId: row.routeId,
    shipId: row.shipId,
    status: row.status,
    plannedDepartureTime: row.departureDate,
    plannedArrivalTime: row.arrivalDate,
    cargoWeight: row.cargoWeight,
    estimatedRevenue: row.estimatedRevenue,
    notes: row.notes
  })
  
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  // TODO: 实现查看详情功能
  ElMessage.info('查看功能待实现')
}

// 开始航次
const handleStart = (row) => {
  const voyageNumber = row.voyageNumber || row.voyageNo || '未知航次'
  
  ElMessageBox.confirm(
    `确定要开始航次"${voyageNumber}"吗？`,
    '确认开始',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateVoyageStatus(row.id, 'IN_PROGRESS')
      ElMessage.success('航次已开始')
      if (dataTableRef.value) {
        dataTableRef.value.refresh()
      }
    } catch (error) {
      console.error('开始航次失败:', error)
      ElMessage.error('开始航次失败')
    }
  })
}

// 取消航次
const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确定要取消航次"${row.voyageNumber || row.voyageNo}"吗？`,
    '确认取消',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      console.log('调用状态更新API - ID:', row.id, '状态: CANCELLED')
      await updateVoyageStatus(row.id, 'CANCELLED')
      ElMessage.success('航次已取消')
      if (dataTableRef.value) {
        dataTableRef.value.refresh()
      }
    } catch (error) {
      console.error('取消航次失败:', error)
      ElMessage.error('取消航次失败')
    }
  })
}

// 删除航次
const handleDeleteVoyage = (row) => {
  ElMessageBox.confirm(
    `确定要删除航次"${row.voyageNumber || row.voyageNo}"吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteVoyage(row.id)
      ElMessage.success('航次已删除')
      if (dataTableRef.value) {
        dataTableRef.value.refresh()
      }
    } catch (error) {
      ElMessage.error('删除航次失败')
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
          voyageNumber: form.voyageNo,  // 前端字段映射到后端字段
          routeId: form.routeId,
          shipId: form.shipId,
          departureDate: form.plannedDepartureTime,  // 前端字段映射到后端字段
          arrivalDate: form.plannedArrivalTime,      // 前端字段映射到后端字段
          status: form.status || 'PLANNED'  // 确保状态有默认值
        }
        
        console.log('提交的数据:', requestData)
        
        if (isEdit.value) {
          await updateVoyage(form.id, requestData)
          ElMessage.success('更新成功')
        } else {
          await createVoyage(requestData)
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
    voyageNo: '',
    routeId: null,
    shipId: null,
    status: 'PLANNED',
    plannedDepartureTime: '',
    plannedArrivalTime: '',
    cargoWeight: null,
    estimatedRevenue: null,
    notes: ''
  })
}

// 组件挂载时加载基础数据
onMounted(() => {
  loadBaseData()
})
</script>

<style scoped>
.voyage-list {
  height: 100%;
  width: 100%;
}

/* 确保DataTable组件在容器中正确显示 */
:deep(.data-table) {
  height: 100%;
}

/* 优化表格内按钮的间距 */
:deep(.table-container .el-button + .el-button) {
  margin-left: 8px;
}

/* 确保固定列的阴影效果 */
:deep(.el-table__fixed-right) {
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
}
</style> 