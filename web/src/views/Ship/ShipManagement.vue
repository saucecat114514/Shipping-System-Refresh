<template>
  <div class="ship-management">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadShipData"
      :delete-data="deleteShipData"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #tonnage="{ row }">
        <div>
          <div>总吨: {{ row.grossTonnage }}t</div>
          <div>载重: {{ row.deadweightTonnage }}t</div>
        </div>
      </template>
      
      <template #status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
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
            <el-form-item label="船舶名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入船舶名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="船籍" prop="flag">
              <el-input v-model="form.flag" placeholder="请输入船籍国家" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="船舶类型(中)" prop="typeCn">
              <el-select v-model="form.typeCn" placeholder="请选择船舶类型" style="width: 100%">
                <el-option label="集装箱船" value="集装箱船" />
                <el-option label="散货船" value="散货船" />
                <el-option label="油轮" value="油轮" />
                <el-option label="滚装船" value="滚装船" />
                <el-option label="冷藏船" value="冷藏船" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="船舶类型(英)" prop="typeEn">
              <el-select v-model="form.typeEn" placeholder="请选择船舶类型" style="width: 100%">
                <el-option label="Container Ship" value="Container Ship" />
                <el-option label="Bulk Carrier" value="Bulk Carrier" />
                <el-option label="Oil Tanker" value="Oil Tanker" />
                <el-option label="RoRo Ship" value="RoRo Ship" />
                <el-option label="Reefer Ship" value="Reefer Ship" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="MMSI" prop="mmsi">
              <el-input v-model="form.mmsi" placeholder="如: 413123456" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IMO编号" prop="imoNumber">
              <el-input v-model="form.imoNumber" placeholder="如: IMO1234567" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总吨位(GT)" prop="grossTonnage">
              <el-input-number
                v-model="form.grossTonnage"
                :precision="2"
                :step="1000"
                :min="0"
                style="width: 100%"
                placeholder="请输入总吨位"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="载重吨位(DWT)" prop="deadweightTonnage">
              <el-input-number
                v-model="form.deadweightTonnage"
                :precision="2"
                :step="1000"
                :min="0"
                style="width: 100%"
                placeholder="请输入载重吨位"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="船舶状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择船舶状态" style="width: 100%">
                <el-option label="停泊" :value="0" />
                <el-option label="航行中" :value="1" />
                <el-option label="锚泊" :value="2" />
                <el-option label="维修" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import DataTable from '@/components/DataTable.vue'
import { getShipList, createShip, updateShip, deleteShip } from '@/api/ship'

// 表格列配置
const columns = [
  { prop: 'name', label: '船舶名称', minWidth: 120 },
  { prop: 'typeCn', label: '船舶类型', width: 100 },
  { prop: 'flag', label: '船籍', width: 100 },
  { prop: 'mmsi', label: 'MMSI', width: 120 },
  { prop: 'imoNumber', label: 'IMO编号', width: 120 },
  { prop: 'tonnage', label: '吨位信息', width: 150, slot: 'tonnage' },
  { prop: 'status', label: '状态', width: 100, slot: 'status' },
  { prop: 'createdAt', label: '创建时间', width: 160 }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'name',
    label: '船舶名称',
    type: 'input',
    placeholder: '请输入船舶名称'
  },
  {
    prop: 'type',
    label: '船舶类型',
    type: 'select',
    placeholder: '请选择船舶类型',
    options: [
      { label: '集装箱船', value: '集装箱船' },
      { label: '散货船', value: '散货船' },
      { label: '油轮', value: '油轮' },
      { label: '滚装船', value: '滚装船' },
      { label: '冷藏船', value: '冷藏船' }
    ]
  },
  {
    prop: 'status',
    label: '状态',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '停泊', value: 0 },
      { label: '航行中', value: 1 },
      { label: '锚泊', value: 2 },
      { label: '维修', value: 3 }
    ]
  }
]

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 表单数据
const form = reactive({
  id: null,
  name: '',
  typeCn: '',
  typeEn: '',
  flag: '',
  mmsi: '',
  imoNumber: '',
  grossTonnage: null,
  deadweightTonnage: null,
  status: 0
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入船舶名称', trigger: 'blur' }
  ],
  typeCn: [
    { required: true, message: '请选择船舶类型(中)', trigger: 'change' }
  ],
  typeEn: [
    { required: true, message: '请选择船舶类型(英)', trigger: 'change' }
  ],
  flag: [
    { required: true, message: '请输入船籍', trigger: 'blur' }
  ],
  mmsi: [
    { required: true, message: '请输入MMSI', trigger: 'blur' },
    { pattern: /^\d{9}$/, message: 'MMSI格式为9位数字', trigger: 'blur' }
  ],
  imoNumber: [
    { required: true, message: '请输入IMO编号', trigger: 'blur' },
    { pattern: /^IMO\d{7}$/, message: 'IMO编号格式为IMO+7位数字', trigger: 'blur' }
  ],
  grossTonnage: [
    { required: true, message: '请输入总吨位', trigger: 'blur' },
    { type: 'number', message: '总吨位必须是数字', trigger: 'blur' }
  ],
  deadweightTonnage: [
    { required: true, message: '请输入载重吨位', trigger: 'blur' },
    { type: 'number', message: '载重吨位必须是数字', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择船舶状态', trigger: 'change' }
  ]
}

// 状态相关方法
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 停泊
    1: 'success', // 航行中
    2: 'warning', // 锚泊
    3: 'danger'   // 维修
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    0: '停泊',
    1: '航行中',
    2: '锚泊',
    3: '维修'
  }
  return statusMap[status] || '未知'
}

// 数据加载函数
const loadShipData = async (params) => {
  try {
    const result = await getShipList(params)
    return result
  } catch (error) {
    console.error('加载船舶数据失败:', error)
    throw error
  }
}

// 删除数据函数
const deleteShipData = async (id) => {
  try {
    await deleteShip(id)
  } catch (error) {
    console.error('删除船舶失败:', error)
    throw error
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增船舶'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑船舶'
  isEdit.value = true
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  form.status = parseInt(row.status) // 确保状态值为数字
  dialogVisible.value = true
}

// 数据表格引用
const dataTableRef = ref()

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    const formData = { ...form }
    
    // 确保status字段正确转换为数字
    if (formData.status === null || formData.status === undefined || formData.status === '') {
      formData.status = 0 // 默认为停泊状态
    } else {
      formData.status = parseInt(formData.status)
    }
    
    // 调试日志
    console.log('提交的表单数据:', formData)
    
    if (isEdit.value) {
      await updateShip(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createShip(formData)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    // 刷新表格数据
    dataTableRef.value?.loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败: ' + (error.response?.data?.msg || error.message))
  }
}

// 重置表单
const resetForm = () => {
  Object.keys(form).forEach(key => {
    if (key === 'status') {
      form[key] = 0 // 默认为停泊状态
    } else if (key === 'grossTonnage' || key === 'deadweightTonnage') {
      form[key] = null
    } else if (key === 'id') {
      form[key] = null
    } else {
      form[key] = ''
    }
  })
  formRef.value?.clearValidate()
  console.log('表单重置后:', form)
}
</script>

<style scoped>
.ship-management {
  padding: 20px;
}
</style>
