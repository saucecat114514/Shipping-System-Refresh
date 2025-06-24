<template>
  <div class="test-voyage-order">
    <el-card>
      <template #header>
        <h3>🚢 航次管理和📦 订单管理功能测试</h3>
      </template>
      
      <!-- 步骤1: 测试航次管理 -->
      <div class="test-section">
        <h4>1. 航次管理测试</h4>
        <el-button @click="testVoyageAPI" :loading="testing.voyage">测试航次API</el-button>
        <el-button @click="toggleVoyageTable" :loading="testing.voyageTable">
          {{ showVoyageTable ? '隐藏' : '显示' }}航次列表
        </el-button>
        
        <div v-if="voyageResult" :class="['result', voyageResult.success ? 'success' : 'error']">
          <p>{{ voyageResult.message }}</p>
          <pre v-if="voyageResult.data">{{ JSON.stringify(voyageResult.data, null, 2) }}</pre>
        </div>

        <!-- 航次数据表格 -->
        <div v-if="showVoyageTable" class="table-container">
          <h5>航次数据列表</h5>
          <DataTable
            ref="voyageTableRef"
            :columns="voyageColumns"
            :load-data="loadVoyageData"
            :show-actions="false"
            :show-add="false"
          />
        </div>
      </div>

      <!-- 步骤2: 测试订单管理 -->
      <div class="test-section">
        <h4>2. 订单管理测试</h4>
        <el-button @click="testOrderAPI" :loading="testing.order">测试订单API</el-button>
        <el-button @click="toggleOrderTable" :loading="testing.orderTable">
          {{ showOrderTable ? '隐藏' : '显示' }}订单列表
        </el-button>
        
        <div v-if="orderResult" :class="['result', orderResult.success ? 'success' : 'error']">
          <p>{{ orderResult.message }}</p>
          <pre v-if="orderResult.data">{{ JSON.stringify(orderResult.data, null, 2) }}</pre>
        </div>

        <!-- 订单数据表格 -->
        <div v-if="showOrderTable" class="table-container">
          <h5>订单数据列表</h5>
          <DataTable
            ref="orderTableRef"
            :columns="orderColumns"
            :load-data="loadOrderData"
            :show-actions="false"
            :show-add="false"
          />
        </div>
      </div>

      <!-- 步骤3: 对比测试结果 -->
      <div class="test-section">
        <h4>3. 测试结果对比</h4>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>航次功能状态</template>
              <div class="status-item">
                <span>API连接：</span>
                <el-tag :type="voyageStatus.api ? 'success' : 'danger'">
                  {{ voyageStatus.api ? '✅ 正常' : '❌ 异常' }}
                </el-tag>
              </div>
              <div class="status-item">
                <span>数据加载：</span>
                <el-tag :type="voyageStatus.data ? 'success' : 'danger'">
                  {{ voyageStatus.data ? '✅ 正常' : '❌ 异常' }}
                </el-tag>
              </div>
              <div class="status-item">
                <span>表格显示：</span>
                <el-tag :type="voyageStatus.table ? 'success' : 'danger'">
                  {{ voyageStatus.table ? '✅ 正常' : '❌ 异常' }}
                </el-tag>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>订单功能状态</template>
              <div class="status-item">
                <span>API连接：</span>
                <el-tag :type="orderStatus.api ? 'success' : 'danger'">
                  {{ orderStatus.api ? '✅ 正常' : '❌ 异常' }}
                </el-tag>
              </div>
              <div class="status-item">
                <span>数据加载：</span>
                <el-tag :type="orderStatus.data ? 'success' : 'danger'">
                  {{ orderStatus.data ? '✅ 正常' : '❌ 异常' }}
                </el-tag>
              </div>
              <div class="status-item">
                <span>表格显示：</span>
                <el-tag :type="orderStatus.table ? 'success' : 'danger'">
                  {{ orderStatus.table ? '✅ 正常' : '❌ 异常' }}
                </el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 错误日志 -->
      <div class="test-section" v-if="errorLogs.length > 0">
        <h4>❌ 错误日志</h4>
        <div v-for="(log, index) in errorLogs" :key="index" class="error-log">
          <p><strong>{{ log.time }}</strong> [{{ log.type }}]: {{ log.message }}</p>
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
import { getVoyageList } from '@/api/voyage'
import { getOrderList } from '@/api/order'
import request from '@/utils/request'

// 状态管理
const testing = reactive({
  voyage: false,
  voyageTable: false,
  order: false,
  orderTable: false
})

const voyageResult = ref(null)
const orderResult = ref(null)
const showVoyageTable = ref(false)
const showOrderTable = ref(false)
const errorLogs = ref([])

// 功能状态
const voyageStatus = reactive({
  api: false,
  data: false,
  table: false
})

const orderStatus = reactive({
  api: false,
  data: false,
  table: false
})

// 添加错误日志
const addErrorLog = (type, message, details = null) => {
  errorLogs.value.push({
    time: new Date().toLocaleTimeString(),
    type,
    message,
    details
  })
}

// 航次列配置
const voyageColumns = [
  { prop: 'voyageNo', label: '航次编号', width: 150 },
  { prop: 'routeName', label: '航线名称', width: 150 },
  { prop: 'shipName', label: '船舶名称', width: 120 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'plannedDepartureTime', label: '计划出发', width: 160 }
]

// 订单列配置
const orderColumns = [
  { prop: 'orderNo', label: '订单编号', width: 180 },
  { prop: 'customerName', label: '客户名称', width: 150 },
  { prop: 'transportMode', label: '运输方式', width: 100 },
  { prop: 'status', label: '状态', width: 100 },
  { prop: 'createTime', label: '创建时间', width: 160 }
]

// 1. 测试航次API
const testVoyageAPI = async () => {
  testing.voyage = true
  try {
    console.log('开始测试航次API...')
    
    const response = await request.get('/voyages?page=1&size=5')
    console.log('航次API响应:', response)
    
    voyageResult.value = {
      success: true,
      message: `航次API调用成功！返回 ${response.total || response.length || 0} 条数据`,
      data: response
    }
    voyageStatus.api = true
    voyageStatus.data = !!(response.records || response.data || response)
    ElMessage.success('航次API测试成功')
  } catch (error) {
    console.error('航次API测试失败:', error)
    voyageResult.value = {
      success: false,
      message: `航次API调用失败: ${error.message}`,
      data: error.response?.data || error
    }
    voyageStatus.api = false
    voyageStatus.data = false
    addErrorLog('航次API', '测试失败', error.toString())
    ElMessage.error('航次API测试失败')
  } finally {
    testing.voyage = false
  }
}

// 2. 航次数据加载函数
const loadVoyageData = async (params) => {
  try {
    console.log('加载航次数据，参数:', params)
    const result = await getVoyageList(params)
    console.log('航次数据加载结果:', result)
    
    if (result && result.records) {
      voyageStatus.table = true
      return result
    } else if (result && Array.isArray(result)) {
      voyageStatus.table = true
      return {
        records: result,
        total: result.length
      }
    } else {
      voyageStatus.table = false
      addErrorLog('航次数据', '数据格式异常', JSON.stringify(result))
      return {
        records: [],
        total: 0
      }
    }
  } catch (error) {
    console.error('航次数据加载失败:', error)
    voyageStatus.table = false
    addErrorLog('航次数据', '加载失败', error.toString())
    throw error
  }
}

// 3. 切换航次表格显示
const toggleVoyageTable = () => {
  testing.voyageTable = true
  setTimeout(() => {
    showVoyageTable.value = !showVoyageTable.value
    testing.voyageTable = false
    ElMessage.info(showVoyageTable.value ? '显示航次表格' : '隐藏航次表格')
  }, 500)
}

// 4. 测试订单API
const testOrderAPI = async () => {
  testing.order = true
  try {
    console.log('开始测试订单API...')
    
    const response = await request.get('/orders?page=1&size=5')
    console.log('订单API响应:', response)
    
    orderResult.value = {
      success: true,
      message: `订单API调用成功！返回 ${response.total || response.length || 0} 条数据`,
      data: response
    }
    orderStatus.api = true
    orderStatus.data = !!(response.records || response.data || response)
    ElMessage.success('订单API测试成功')
  } catch (error) {
    console.error('订单API测试失败:', error)
    orderResult.value = {
      success: false,
      message: `订单API调用失败: ${error.message}`,
      data: error.response?.data || error
    }
    orderStatus.api = false
    orderStatus.data = false
    addErrorLog('订单API', '测试失败', error.toString())
    ElMessage.error('订单API测试失败')
  } finally {
    testing.order = false
  }
}

// 5. 订单数据加载函数
const loadOrderData = async (params) => {
  try {
    console.log('加载订单数据，参数:', params)
    const result = await getOrderList(params)
    console.log('订单数据加载结果:', result)
    
    if (result && result.records) {
      orderStatus.table = true
      return result
    } else if (result && Array.isArray(result)) {
      orderStatus.table = true
      return {
        records: result,
        total: result.length
      }
    } else {
      orderStatus.table = false
      addErrorLog('订单数据', '数据格式异常', JSON.stringify(result))
      return {
        records: [],
        total: 0
      }
    }
  } catch (error) {
    console.error('订单数据加载失败:', error)
    orderStatus.table = false
    addErrorLog('订单数据', '加载失败', error.toString())
    throw error
  }
}

// 6. 切换订单表格显示
const toggleOrderTable = () => {
  testing.orderTable = true
  setTimeout(() => {
    showOrderTable.value = !showOrderTable.value
    testing.orderTable = false
    ElMessage.info(showOrderTable.value ? '显示订单表格' : '隐藏订单表格')
  }, 500)
}
</script>

<style scoped>
.test-voyage-order {
  padding: 20px;
}

.test-section {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.test-section h4 {
  margin: 0 0 15px 0;
  color: #409EFF;
}

.test-section h5 {
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

.status-item {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
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