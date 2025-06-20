<template>
  <div class="ship-list">
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
      <template #status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
      
      <template #position="{ row }">
        <span v-if="row.currentLongitude && row.currentLatitude">
          {{ row.currentLongitude.toFixed(4) }}, {{ row.currentLatitude.toFixed(4) }}
        </span>
        <span v-else class="text-gray">未知位置</span>
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
            <el-form-item label="船舶名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入船舶名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="船舶类型(中)" prop="typeCn">
              <el-select v-model="form.typeCn" placeholder="请选择船舶类型" style="width: 100%">
                <el-option label="集装箱船" value="集装箱船" />
                <el-option label="散货船" value="散货船" />
                <el-option label="油轮" value="油轮" />
                <el-option label="客轮" value="客轮" />
                <el-option label="货船" value="货船" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="船舶类型(英)" prop="typeEn">
              <el-input v-model="form.typeEn" placeholder="如: Container Ship" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="船旗国" prop="flag">
              <el-input v-model="form.flag" placeholder="请输入船旗国" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="MMSI" prop="mmsi">
              <el-input v-model="form.mmsi" placeholder="海事移动业务标识" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IMO编号" prop="imoNumber">
              <el-input v-model="form.imoNumber" placeholder="国际海事组织编号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总吨位(GT)" prop="grossTonnage">
              <el-input-number
                v-model="form.grossTonnage"
                :precision="2"
                :min="0"
                style="width: 100%"
                placeholder="总吨位"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="载重吨位(DWT)" prop="deadweightTonnage">
              <el-input-number
                v-model="form.deadweightTonnage"
                :precision="2"
                :min="0"
                style="width: 100%"
                placeholder="载重吨位"
              />
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
  { prop: 'typeCn', label: '船舶类型', width: 120 },
  { prop: 'flag', label: '船旗国', width: 100 },
  { prop: 'mmsi', label: 'MMSI', width: 120 },
  { prop: 'grossTonnage', label: '总吨位(GT)', width: 120 },
  { prop: 'deadweightTonnage', label: '载重吨位(DWT)', width: 130 },
  { prop: 'status', label: '状态', width: 100, slot: 'status' },
  { prop: 'position', label: '当前位置', width: 150, slot: 'position' },
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
      { label: '客轮', value: '客轮' },
      { label: '货船', value: '货船' }
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
  deadweightTonnage: null
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入船舶名称', trigger: 'blur' }
  ],
  typeCn: [
    { required: true, message: '请选择船舶类型', trigger: 'change' }
  ],
  typeEn: [
    { required: true, message: '请输入英文类型', trigger: 'blur' }
  ],
  flag: [
    { required: true, message: '请输入船旗国', trigger: 'blur' }
  ],
  mmsi: [
    { required: true, message: '请输入MMSI', trigger: 'blur' },
    { pattern: /^\d{9}$/, message: 'MMSI应为9位数字', trigger: 'blur' }
  ],
  imoNumber: [
    { required: true, message: '请输入IMO编号', trigger: 'blur' }
  ],
  grossTonnage: [
    { required: true, message: '请输入总吨位', trigger: 'blur' }
  ],
  deadweightTonnage: [
    { required: true, message: '请输入载重吨位', trigger: 'blur' }
  ]
}

// 状态处理函数
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',      // 停泊
    1: 'success',   // 航行中
    2: 'warning',   // 锚泊
    3: 'danger'     // 维修
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: '停泊',
    1: '航行中',
    2: '锚泊',
    3: '维修'
  }
  return textMap[status] || '未知'
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
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑船舶'
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
          await updateShip(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createShip(form)
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
    name: '',
    typeCn: '',
    typeEn: '',
    flag: '',
    mmsi: '',
    imoNumber: '',
    grossTonnage: null,
    deadweightTonnage: null
  })
}

const dataTableRef = ref()
</script>

<style scoped>
.ship-list {
  height: 100%;
}

.text-gray {
  color: #999;
}
</style> 