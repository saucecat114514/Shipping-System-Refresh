<template>
  <div class="voyage-list">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadVoyageData"
      :delete-data="deleteVoyageData"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #route="{ row }">
        {{ row.startPort }} → {{ row.endPort }}
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
          v-if="row.status === 'NOT_STARTED'">
          编辑
        </el-button>
        <el-button 
          link 
          type="success" 
          @click="handleStart(row)" 
          v-if="row.status === 'NOT_STARTED'">
          开始
        </el-button>
        <el-button 
          link 
          type="danger" 
          @click="handleCancel(row)" 
          v-if="row.status === 'NOT_STARTED'">
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
                <el-option label="未开始" value="NOT_STARTED" />
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
import { getVoyageList, createVoyage, updateVoyage, deleteVoyage } from '@/api/voyage'
import { getRouteList } from '@/api/route'
import { getShipList } from '@/api/ship'

// 表格列配置
const columns = [
  { prop: 'voyageNo', label: '航次编号', width: 150 },
  { prop: 'routeName', label: '航线名称', width: 150 },
  { prop: 'shipName', label: '船舶名称', width: 120 },
  { prop: 'route', label: '航线', width: 200, slot: 'route' },
  { prop: 'status', label: '状态', width: 100, slot: 'status' },
  { prop: 'plannedDepartureTime', label: '计划出发', width: 160 },
  { prop: 'plannedArrivalTime', label: '计划到达', width: 160 },
  { prop: 'cargoWeight', label: '载货量(吨)', width: 120, align: 'right' },
  { prop: 'actions', label: '操作', width: 250, slot: 'actions', fixed: 'right' }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'voyageNo',
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
      { label: '未开始', value: 'NOT_STARTED' },
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
  status: 'NOT_STARTED',
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
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'NOT_STARTED': 'warning',
    'IN_PROGRESS': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 数据加载函数
const loadVoyageData = async (params) => {
  try {
    const result = await getVoyageList(params)
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
    // 加载航线列表
    const routeResult = await getRouteList({ page: 1, size: 1000 })
    routes.value = routeResult.data || []
    
    // 更新搜索配置中的航线选项
    const routeSearchConfig = searchConfig.find(item => item.prop === 'routeId')
    if (routeSearchConfig) {
      routeSearchConfig.options = routes.value.map(route => ({
        label: route.name,
        value: route.id
      }))
    }
    
    // 加载船舶列表
    const shipResult = await getShipList({ page: 1, size: 1000 })
    ships.value = shipResult.data || []
  } catch (error) {
    console.error('加载基础数据失败:', error)
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增航次'
  isEdit.value = false
  // 生成航次编号
  const now = new Date()
  const dateStr = now.toISOString().slice(0, 10).replace(/-/g, '')
  const timeStr = now.getTime().toString().slice(-4)
  form.voyageNo = `V${dateStr}${timeStr}`
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑航次'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  // TODO: 实现查看详情功能
  ElMessage.info('查看功能待实现')
}

// 开始航次
const handleStart = (row) => {
  ElMessageBox.confirm(
    `确定要开始航次"${row.voyageNo}"吗？`,
    '确认开始',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateVoyage(row.id, { ...row, status: 'IN_PROGRESS' })
      ElMessage.success('航次已开始')
      if (dataTableRef.value) {
        dataTableRef.value.refresh()
      }
    } catch (error) {
      ElMessage.error('开始航次失败')
    }
  })
}

// 取消航次
const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确定要取消航次"${row.voyageNo}"吗？`,
    '确认取消',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateVoyage(row.id, { ...row, status: 'CANCELLED' })
      ElMessage.success('航次已取消')
      if (dataTableRef.value) {
        dataTableRef.value.refresh()
      }
    } catch (error) {
      ElMessage.error('取消航次失败')
    }
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateVoyage(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createVoyage(form)
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
    voyageNo: '',
    routeId: null,
    shipId: null,
    status: 'NOT_STARTED',
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
}
</style> 