<template>
  <div class="debug-port">
    <el-card>
      <template #header>
        <h3>🔧 港口功能调试</h3>
      </template>
      
      <!-- 步骤1: 测试API连接 -->
      <div class="debug-section">
        <h4>1. 测试港口API</h4>
        <el-button @click="testPortAPI" :loading="testing.api">测试获取港口列表</el-button>
        <div v-if="apiResult" :class="['result', apiResult.success ? 'success' : 'error']">
          <p>{{ apiResult.message }}</p>
          <pre v-if="apiResult.data">{{ JSON.stringify(apiResult.data, null, 2) }}</pre>
        </div>
      </div>

      <!-- 步骤2: 测试DataTable组件 -->
      <div class="debug-section">
        <h4>2. 测试DataTable组件</h4>
        <el-button @click="toggleDataTable" :loading="testing.table">
          {{ showDataTable ? '隐藏' : '显示' }}DataTable
        </el-button>
        
        <!-- 显示简化的DataTable -->
        <div v-if="showDataTable" class="table-container">
          <h5>简化版DataTable测试</h5>
          <DataTable
            ref="debugTableRef"
            :columns="simpleColumns"
            :load-data="loadSimpleData"
            :show-actions="false"
            :show-add="false"
          />
        </div>
      </div>

      <!-- 步骤3: 测试完整港口功能 -->
      <div class="debug-section">
        <h4>3. 测试完整港口功能</h4>
        <el-button @click="testFullPortList" :loading="testing.full">
          测试完整港口列表功能
        </el-button>
        
        <div v-if="showFullTable" class="table-container">
          <h5>完整港口列表</h5>
          <DataTable
            ref="fullTableRef"
            :columns="fullColumns"
            :search-config="searchConfig"
            :load-data="loadPortData"
            :delete-data="deletePortData"
            @add="handleAdd"
            @edit="handleEdit"
          >
            <template #coordinates="{ row }">
              <span>{{ row.longitude }}, {{ row.latitude }}</span>
            </template>
          </DataTable>
        </div>
      </div>

      <!-- 错误日志 -->
      <div class="debug-section" v-if="errorLogs.length > 0">
        <h4>❌ 错误日志</h4>
        <div v-for="(log, index) in errorLogs" :key="index" class="error-log">
          <p><strong>{{ log.time }}</strong>: {{ log.message }}</p>
          <pre v-if="log.details">{{ log.details }}</pre>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import DataTable from '@/components/DataTable.vue'
import { getPortList, deletePort } from '@/api/port'
import request from '@/utils/request'

// 状态管理
const testing = reactive({
  api: false,
  table: false,
  full: false
})

const apiResult = ref(null)
const showDataTable = ref(false)
const showFullTable = ref(false)
const errorLogs = ref([])

// 添加错误日志
const addErrorLog = (message, details = null) => {
  errorLogs.value.push({
    time: new Date().toLocaleTimeString(),
    message,
    details
  })
}

// 简化的列配置
const simpleColumns = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'name', label: '名称', minWidth: 120 },
  { prop: 'status', label: '状态', width: 100 }
]

// 完整的列配置
const fullColumns = [
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
  }
]

// 1. 测试港口API
const testPortAPI = async () => {
  testing.api = true
  try {
    console.log('开始测试港口API...')
    
    // 直接调用API
    const response = await request.get('/ports?page=1&size=5')
    console.log('API响应:', response)
    
    apiResult.value = {
      success: true,
      message: `API调用成功！返回 ${response.total || response.length || 0} 条数据`,
      data: response
    }
    ElMessage.success('港口API测试成功')
  } catch (error) {
    console.error('港口API测试失败:', error)
    apiResult.value = {
      success: false,
      message: `API调用失败: ${error.message}`,
      data: error.response?.data || error
    }
    addErrorLog('港口API测试失败', error.toString())
    ElMessage.error('港口API测试失败')
  } finally {
    testing.api = false
  }
}

// 2. 简单数据加载函数
const loadSimpleData = async () => {
  try {
    console.log('加载简单测试数据...')
    // 返回模拟数据
    const mockData = {
      records: [
        { id: 1, name: '测试港口1', status: '正常' },
        { id: 2, name: '测试港口2', status: '正常' },
        { id: 3, name: '测试港口3', status: '正常' }
      ],
      total: 3
    }
    console.log('简单数据加载成功:', mockData)
    return mockData
  } catch (error) {
    console.error('简单数据加载失败:', error)
    addErrorLog('简单数据加载失败', error.toString())
    throw error
  }
}

// 切换DataTable显示
const toggleDataTable = () => {
  testing.table = true
  setTimeout(() => {
    showDataTable.value = !showDataTable.value
    testing.table = false
    if (showDataTable.value) {
      ElMessage.info('显示简化DataTable')
    } else {
      ElMessage.info('隐藏DataTable')
    }
  }, 500)
}

// 3. 完整港口数据加载函数
const loadPortData = async (params) => {
  try {
    console.log('加载港口数据，参数:', params)
    const result = await getPortList(params)
    console.log('港口数据加载成功:', result)
    return result
  } catch (error) {
    console.error('加载港口数据失败:', error)
    addErrorLog('港口数据加载失败', error.toString())
    throw error
  }
}

// 删除港口数据
const deletePortData = async (id) => {
  try {
    console.log('删除港口:', id)
    await deletePort(id)
    console.log('港口删除成功')
  } catch (error) {
    console.error('删除港口失败:', error)
    addErrorLog('删除港口失败', error.toString())
    throw error
  }
}

// 处理新增
const handleAdd = () => {
  ElMessage.info('触发新增事件')
  console.log('handleAdd 被调用')
}

// 处理编辑
const handleEdit = (row) => {
  ElMessage.info(`触发编辑事件: ${row.nameCn || row.name}`)
  console.log('handleEdit 被调用:', row)
}

// 测试完整港口功能
const testFullPortList = () => {
  testing.full = true
  setTimeout(() => {
    showFullTable.value = !showFullTable.value
    testing.full = false
    if (showFullTable.value) {
      ElMessage.info('显示完整港口列表')
    } else {
      ElMessage.info('隐藏完整港口列表')
    }
  }, 500)
}
</script>

<style scoped>
.debug-port {
  padding: 20px;
}

.debug-section {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.debug-section h4 {
  margin: 0 0 15px 0;
  color: #409EFF;
}

.debug-section h5 {
  margin: 15px 0 10px 0;
  color: #666;
}

.result {
  margin-top: 10px;
  padding: 10px;
  border-radius: 4px;
  font-size: 14px;
}

.result.success {
  background-color: #f0f9ff;
  border: 1px solid #67c23a;
  color: #67c23a;
}

.result.error {
  background-color: #fef0f0;
  border: 1px solid #f56c6c;
  color: #f56c6c;
}

.table-container {
  margin-top: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 15px;
  background-color: #fafafa;
}

.error-log {
  margin: 10px 0;
  padding: 10px;
  background-color: #fef0f0;
  border: 1px solid #f56c6c;
  border-radius: 4px;
}

.error-log pre {
  margin: 5px 0 0 0;
  font-size: 12px;
  color: #666;
}

.el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style> 