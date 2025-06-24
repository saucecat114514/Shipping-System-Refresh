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
        {{ row.originPort ? row.originPort.nameCn : '未知港口' }} → {{ row.destinationPort ? row.destinationPort.nameCn : '未知港口' }}
      </template>
      
      <template #status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ row.status === 1 ? '启用' : '停用' }}
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
            <el-form-item label="航线编号" prop="routeNumber">
              <el-input v-model="form.routeNumber" placeholder="系统自动生成" readonly />
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
            <el-form-item label="起始港口" prop="originPortId">
              <el-select 
                v-model="form.originPortId" 
                placeholder="请选择起始港口" 
                style="width: 100%"
                @change="handlePortChange"
              >
                <el-option 
                  v-for="port in ports" 
                  :key="port.id" 
                  :label="`${port.nameCn} (${port.code})`" 
                  :value="port.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目的港口" prop="destinationPortId">
              <el-select 
                v-model="form.destinationPortId" 
                placeholder="请选择目的港口" 
                style="width: 100%"
                @change="handlePortChange"
              >
                <el-option 
                  v-for="port in ports" 
                  :key="port.id" 
                  :label="`${port.nameCn} (${port.code})`" 
                  :value="port.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="航程(公里)" prop="distance">
              <el-input
                v-model="form.distance"
                placeholder="系统自动计算"
                readonly
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="航程(海里)" prop="distanceNm">
              <el-input
                v-model="form.distanceNm"
                placeholder="系统自动计算"
                readonly
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计时间(小时)" prop="estimatedDuration">
              <el-input
                v-model="form.estimatedDuration"
                placeholder="系统自动计算"
                readonly
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item>
              <el-button 
                type="primary" 
                @click="calculateDistance"
                :disabled="!form.originPortId || !form.destinationPortId"
                style="width: 100%"
              >
                {{ form.distance ? '重新计算距离和时间' : '计算距离和时间' }}
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
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
import { getAllPorts } from '@/api/port'
import request from '@/utils/request'

// 表格列配置
const columns = [
  { prop: 'routeNumber', label: '航线编号', width: 120 },
  { prop: 'name', label: '航线名称', width: 180 },
  { prop: 'route', label: '航线', width: 250, slot: 'route' },
  { prop: 'distance', label: '航程(公里)', width: 130, align: 'right' },
  { prop: 'distanceNm', label: '航程(海里)', width: 120, align: 'right' },
  { prop: 'estimatedDuration', label: '预计时间(小时)', width: 120, align: 'right' },
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
    prop: 'routeNumber',
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
      { label: '启用', value: 1 },
      { label: '停用', value: 0 }
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
  routeNumber: '',
  name: '',
  originPortId: null,
  destinationPortId: null,
  distance: null,
  distanceNm: null,
  estimatedDuration: null,
  status: 1,
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入航线名称', trigger: 'blur' }
  ],
  originPortId: [
    { required: true, message: '请选择起始港口', trigger: 'change' }
  ],
  destinationPortId: [
    { required: true, message: '请选择目的港口', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 状态处理函数
const getStatusType = (status) => {
  const typeMap = {
    1: 'success',
    0: 'danger'
  }
  return typeMap[status] || 'info'
}

// 数据加载函数
const loadRouteData = async (params) => {
  try {
    // 使用包含港口信息的接口
    const result = await request({
      url: '/routes/with-ports',
      method: 'get',
      params
    })
    return result
  } catch (error) {
    console.error('加载航线数据失败:', error)
    // 如果新接口失败，尝试使用原有接口
    try {
      const fallbackResult = await getRouteList(params)
      return fallbackResult
    } catch (fallbackError) {
      console.error('备用接口也失败:', fallbackError)
      throw error
    }
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
    const result = await getAllPorts()
    ports.value = result.data || []
    console.log('港口列表加载成功:', ports.value.length)
  } catch (error) {
    console.error('加载港口列表失败:', error)
    // 如果新接口失败，尝试使用原有的港口接口
    try {
      const fallbackResult = await getPortList({ page: 1, size: 1000 })
      ports.value = fallbackResult.data || []
      console.log('备用港口列表加载成功:', ports.value.length)
    } catch (fallbackError) {
      console.error('备用港口接口也失败:', fallbackError)
      ElMessage.error('加载港口列表失败，请检查网络连接')
    }
  }
}

// 新增
const handleAdd = async () => {
  dialogTitle.value = '新增航线'
  isEdit.value = false
  resetForm()
  
  // 打开对话框前重新加载港口列表
  await loadPorts()
  
  dialogVisible.value = true
  
  // 添加调试信息
  console.log('新增对话框打开，当前港口数量:', ports.value.length)
  if (ports.value.length > 0) {
    console.log('第一个港口:', ports.value[0])
  }
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑航线'
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    routeNumber: row.routeNumber,
    name: row.name,
    originPortId: row.originPortId,
    destinationPortId: row.destinationPortId,
    distance: row.distance,
    distanceNm: row.distanceNm,
    estimatedDuration: row.estimatedDuration,
    status: row.status,
    description: row.description
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          name: form.name,
          originPortId: form.originPortId,
          destinationPortId: form.destinationPortId,
          distance: form.distance,
          distanceNm: form.distanceNm,
          estimatedDuration: form.estimatedDuration,
          status: form.status,
          description: form.description
        }
        
        // 编辑时包含航线编号
        if (isEdit.value) {
          submitData.routeNumber = form.routeNumber
        }

        if (isEdit.value) {
          await updateRoute(form.id, submitData)
          ElMessage.success('更新成功')
        } else {
          await createRoute(submitData)
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
    routeNumber: '',
    name: '',
    originPortId: null,
    destinationPortId: null,
    distance: null,
    distanceNm: null,
    estimatedDuration: null,
    status: 1,
    description: ''
  })
}

// 组件挂载时加载港口列表
onMounted(() => {
  loadPorts()
})

// 处理港口变化
const handlePortChange = () => {
  // 当两个港口都选择后，自动计算距离
  if (form.originPortId && form.destinationPortId && form.originPortId !== form.destinationPortId) {
    // 延迟执行，确保界面更新后再计算
    setTimeout(() => {
      calculateDistance()
    }, 100)
  } else if (form.originPortId === form.destinationPortId && form.originPortId) {
    ElMessage.warning('起始港口和目的港口不能相同')
    form.distance = null
    form.distanceNm = null
    form.estimatedDuration = null
  } else {
    // 清空计算结果
    form.distance = null
    form.distanceNm = null
    form.estimatedDuration = null
  }
}

// 计算距离
const calculateDistance = async () => {
  if (!form.originPortId || !form.destinationPortId) {
    ElMessage.warning('请先选择起始港口和目的港口')
    return
  }
  
  if (form.originPortId === form.destinationPortId) {
    ElMessage.warning('起始港口和目的港口不能相同')
    return
  }
  
  try {
    // 获取两个港口的坐标信息
    const originPort = ports.value.find(p => p.id === form.originPortId)
    const destinationPort = ports.value.find(p => p.id === form.destinationPortId)
    
    if (!originPort || !destinationPort) {
      ElMessage.error('无法获取港口坐标信息')
      return
    }
    
    // 使用Haversine公式计算距离
    const distance = calculateHaversineDistance(
      originPort.latitude, originPort.longitude,
      destinationPort.latitude, destinationPort.longitude
    )
    
    // 转换为海里 (1公里 = 0.539957海里)
    const distanceNm = distance * 0.539957
    
    // 计算预计航行时间 (假设平均速度为12节)
    const estimatedHours = distanceNm / 12
    
    // 更新表单数据
    form.distance = Math.round(distance * 100) / 100  // 保留两位小数
    form.distanceNm = Math.round(distanceNm * 100) / 100  // 保留两位小数
    form.estimatedDuration = Math.round(estimatedHours * 100) / 100  // 保留两位小数
    
    ElMessage.success(`距离计算完成：${form.distance} 公里 (${form.distanceNm} 海里)，预计时间：${form.estimatedDuration} 小时`)
  } catch (error) {
    console.error('计算距离失败:', error)
    ElMessage.error('计算距离失败')
  }
}

// Haversine距离计算公式
const calculateHaversineDistance = (lat1, lon1, lat2, lon2) => {
  const R = 6371 // 地球半径，单位：公里
  const dLat = toRadians(lat2 - lat1)
  const dLon = toRadians(lon2 - lon1)
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

// 角度转弧度
const toRadians = (degrees) => {
  return degrees * (Math.PI / 180)
}
</script>

<style scoped>
.route-list {
  height: 100%;
}
</style> 