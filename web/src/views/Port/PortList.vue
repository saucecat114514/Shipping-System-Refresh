<template>
  <div class="port-list">
    <DataTable
      ref="dataTableRef"
      :columns="columns"
      :search-config="searchConfig"
      :load-data="loadPortData"
      :delete-data="deletePortData"
      @add="handleAdd"
      @edit="handleEdit"
    >
      <!-- 自定义列插槽 -->
      <template #coordinates="{ row }">
        <span>{{ row.longitude }}, {{ row.latitude }}</span>
      </template>
    </DataTable>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
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
            <el-form-item label="港口名称(中)" prop="nameCn">
              <el-input v-model="form.nameCn" placeholder="请输入中文名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="港口名称(英)" prop="nameEn">
              <el-input v-model="form.nameEn" placeholder="请输入英文名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="港口代码" prop="code">
              <el-input v-model="form.code" placeholder="如: CNSHA" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属国家" prop="country">
              <el-input v-model="form.country" placeholder="请输入国家名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input-number
                v-model="form.longitude"
                :precision="6"
                :step="0.000001"
                :min="-180"
                :max="180"
                style="width: 100%"
                placeholder="请输入经度"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input-number
                v-model="form.latitude"
                :precision="6"
                :step="0.000001"
                :min="-90"
                :max="90"
                style="width: 100%"
                placeholder="请输入纬度"
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
import { getPortList, createPort, updatePort, deletePort } from '@/api/port'

// 表格列配置
const columns = [
  { prop: 'nameCn', label: '港口名称(中)', minWidth: 120 },
  { prop: 'nameEn', label: '港口名称(英)', minWidth: 150 },
  { prop: 'code', label: '港口代码', width: 100 },
  { prop: 'country', label: '所属国家', width: 120 },
  { prop: 'coordinates', label: '坐标位置', width: 180, slot: 'coordinates' },
  { prop: 'createdAt', label: '创建时间', width: 160 }
]

// 搜索配置
const searchConfig = [
  {
    prop: 'name',
    label: '港口名称',
    type: 'input',
    placeholder: '请输入港口名称'
  },
  {
    prop: 'country',
    label: '国家',
    type: 'input',
    placeholder: '请输入国家名称'
  },
  {
    prop: 'code',
    label: '港口代码',
    type: 'input',
    placeholder: '请输入港口代码'
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
  nameCn: '',
  nameEn: '',
  code: '',
  country: '',
  longitude: null,
  latitude: null
})

// 表单验证规则
const rules = {
  nameCn: [
    { required: true, message: '请输入港口中文名称', trigger: 'blur' }
  ],
  nameEn: [
    { required: true, message: '请输入港口英文名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入港口代码', trigger: 'blur' },
    { pattern: /^[A-Z]{5}$/, message: '港口代码格式为5位大写字母', trigger: 'blur' }
  ],
  country: [
    { required: true, message: '请输入所属国家', trigger: 'blur' }
  ],
  longitude: [
    { required: true, message: '请输入经度', trigger: 'blur' },
    { type: 'number', message: '经度必须是数字', trigger: 'blur' }
  ],
  latitude: [
    { required: true, message: '请输入纬度', trigger: 'blur' },
    { type: 'number', message: '纬度必须是数字', trigger: 'blur' }
  ]
}

// 数据加载函数
const loadPortData = async (params) => {
  try {
    const result = await getPortList(params)
    return result
  } catch (error) {
    console.error('加载港口数据失败:', error)
    throw error
  }
}

// 删除数据函数
const deletePortData = async (id) => {
  try {
    await deletePort(id)
  } catch (error) {
    console.error('删除港口失败:', error)
    throw error
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增港口'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑港口'
  isEdit.value = true
  
  // 填充表单数据
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (isEdit.value) {
        await updatePort(form.id, form)
        ElMessage.success('更新港口成功')
      } else {
        await createPort(form)
        ElMessage.success('创建港口成功')
      }
      
      dialogVisible.value = false
      // 刷新表格数据
      dataTableRef.value?.refresh()
    } catch (error) {
      console.error('保存港口失败:', error)
      ElMessage.error('保存失败，请重试')
    }
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.keys(form).forEach(key => {
    if (key === 'id') {
      form[key] = null
    } else if (typeof form[key] === 'string') {
      form[key] = ''
    } else {
      form[key] = null
    }
  })
}

// 表格引用
const dataTableRef = ref()
</script>

<style scoped>
.port-list {
  height: 100%;
}
</style> 