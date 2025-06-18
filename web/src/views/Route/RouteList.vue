<template>
  <div class="route-list">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadRouteData"
      :delete-data="deleteRouteData"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #route="{ row }">
        {{ row.startPort }} → {{ row.endPort }}
      </template>
      
      <template #status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ row.status }}
        </el-tag>
      </template>
    </DataTable>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
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
            <el-form-item label="航线编号" prop="code">
              <el-input v-model="form.code" placeholder="请输入航线编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="航线名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入航线名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="起始港口" prop="startPortId">
              <el-select v-model="form.startPortId" placeholder="请选择起始港口" style="width: 100%">
                <el-option v-for="port in ports" :key="port.id" :label="port.name" :value="port.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的港口" prop="endPortId">
              <el-select v-model="form.endPortId" placeholder="请选择目的港口" style="width: 100%">
                <el-option v-for="port in ports" :key="port.id" :label="port.name" :value="port.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="航程(海里)" prop="distance">
              <el-input-number
                v-model="form.distance"
                :min="0"
                :step="100"
                style="width: 100%"
                placeholder="航程"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计时间" prop="estimatedDuration">
              <el-input v-model="form.estimatedDuration" placeholder="如: 5天" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" value="ACTIVE" />
                <el-option label="维护中" value="MAINTENANCE" />
                <el-option label="已停用" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大载重(吨)" prop="maxCapacity">
              <el-input-number
                v-model="form.maxCapacity"
                :min="0"
                :step="1000"
                style="width: 100%"
                placeholder="最大载重"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="航线描述"
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
import { ElMessage } from 'element-plus'
import DataTable from '@/components/DataTable.vue'
import { getRouteList, createRoute, updateRoute, deleteRoute } from '@/api/route'
import { getPortList } from '@/api/port'

// 表格列配置
const columns = [
  { prop: 'code', label: '航线编号', width: 120 },
  { prop: 'name', label: '航线名称', width: 180 },
  { prop: 'route', label: '航线', width: 250, slot: 'route' },
  { prop: 'distance', label: '航程(海里)', width: 120, align: 'right' },
  { prop: 'estimatedDuration', label: '预计时间', width: 120 },
  { prop: 'maxCapacity', label: '最大载重(吨)', width: 130, align: 'right' },
  { prop: 'status', label: '状态', width: 100, slot: 'status' },
  { prop: 'createdAt', label: '创建时间', width: 160 }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'name',
    label: '航线名称',
    type: 'input',
    placeholder: '请输入航线名称'
  },
  {
    prop: 'code',
    label: '航线编号',
    type: 'input',
    placeholder: '请输入航线编号'
  },
  {
    prop: 'status',
    label: '状态',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '正常', value: 'ACTIVE' },
      { label: '维护中', value: 'MAINTENANCE' },
      { label: '已停用', value: 'INACTIVE' }
    ]
  }
]

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const dataTableRef = ref()

// 港口列表
const ports = ref([])

// 表单数据
const form = reactive({
  id: null,
  code: '',
  name: '',
  startPortId: null,
  endPortId: null,
  distance: null,
  estimatedDuration: '',
  maxCapacity: null,
  status: 'ACTIVE',
  description: ''
})

// 表单验证规则
const rules = {
  code: [
    { required: true, message: '请输入航线编号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入航线名称', trigger: 'blur' }
  ],
  startPortId: [
    { required: true, message: '请选择起始港口', trigger: 'change' }
  ],
  endPortId: [
    { required: true, message: '请选择目的港口', trigger: 'change' }
  ],
  distance: [
    { required: true, message: '请输入航程', trigger: 'blur' }
  ],
  estimatedDuration: [
    { required: true, message: '请输入预计时间', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 状态处理函数
const getStatusType = (status) => {
  const typeMap = {
    'ACTIVE': 'success',
    'MAINTENANCE': 'warning',
    'INACTIVE': 'danger'
  }
  return typeMap[status] || 'info'
}

// 数据加载函数
const loadRouteData = async (params) => {
  try {
    const result = await getRouteList(params)
    return result
  } catch (error) {
    console.error('加载航线数据失败:', error)
    throw error
  }
}

// 删除数据函数
const deleteRouteData = async (id) => {
  try {
    await deleteRoute(id)
  } catch (error) {
    console.error('删除航线失败:', error)
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
  dialogTitle.value = '新增航线'
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑航线'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateRoute(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createRoute(form)
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
    code: '',
    name: '',
    startPortId: null,
    endPortId: null,
    distance: null,
    estimatedDuration: '',
    maxCapacity: null,
    status: 'ACTIVE',
    description: ''
  })
}

// 组件挂载时加载港口列表
onMounted(() => {
  loadPorts()
})
</script>

<style scoped>
.route-list {
  height: 100%;
}
</style> 