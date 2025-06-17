<template>
  <div class="test-port-api">
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <span>港口管理API测试</span>
          <el-button type="primary" @click="runAllTests">运行所有测试</el-button>
        </div>
      </template>

      <div class="test-section">
        <h4>1. 获取港口列表测试</h4>
        <el-button @click="testGetPortList" :loading="loading.getList">测试获取列表</el-button>
        <div class="test-result" v-if="results.getList">
          <el-tag :type="results.getList.success ? 'success' : 'danger'">
            {{ results.getList.success ? '成功' : '失败' }}
          </el-tag>
          <pre>{{ JSON.stringify(results.getList.data, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h4>2. 创建港口测试</h4>
        <el-button @click="testCreatePort" :loading="loading.create">测试创建港口</el-button>
        <div class="test-result" v-if="results.create">
          <el-tag :type="results.create.success ? 'success' : 'danger'">
            {{ results.create.success ? '成功' : '失败' }}
          </el-tag>
          <pre>{{ JSON.stringify(results.create.data, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h4>3. 更新港口测试</h4>
        <el-button @click="testUpdatePort" :loading="loading.update" :disabled="!testPortId">
          测试更新港口
        </el-button>
        <div class="test-result" v-if="results.update">
          <el-tag :type="results.update.success ? 'success' : 'danger'">
            {{ results.update.success ? '成功' : '失败' }}
          </el-tag>
          <pre>{{ JSON.stringify(results.update.data, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h4>4. 删除港口测试</h4>
        <el-button @click="testDeletePort" :loading="loading.delete" :disabled="!testPortId" type="danger">
          测试删除港口
        </el-button>
        <div class="test-result" v-if="results.delete">
          <el-tag :type="results.delete.success ? 'success' : 'danger'">
            {{ results.delete.success ? '成功' : '失败' }}
          </el-tag>
          <pre>{{ JSON.stringify(results.delete.data, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h4>5. API连通性测试</h4>
        <el-button @click="testAPIConnection" :loading="loading.connection">测试API连接</el-button>
        <div class="test-result" v-if="results.connection">
          <el-tag :type="results.connection.success ? 'success' : 'danger'">
            {{ results.connection.success ? '连接正常' : '连接失败' }}
          </el-tag>
          <div>{{ results.connection.message }}</div>
        </div>
      </div>

      <div class="test-summary" v-if="testComplete">
        <h4>测试总结</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="测试总数">{{ totalTests }}</el-descriptions-item>
          <el-descriptions-item label="成功数量">{{ successCount }}</el-descriptions-item>
          <el-descriptions-item label="失败数量">{{ failCount }}</el-descriptions-item>
          <el-descriptions-item label="成功率">{{ (successCount / totalTests * 100).toFixed(1) }}%</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getPortList, createPort, updatePort, deletePort } from '@/api/port'
import request from '@/utils/request'

// 加载状态
const loading = reactive({
  getList: false,
  create: false,
  update: false,
  delete: false,
  connection: false
})

// 测试结果
const results = reactive({
  getList: null,
  create: null,
  update: null,
  delete: null,
  connection: null
})

// 测试用的港口ID
const testPortId = ref(null)
const testComplete = ref(false)

// 计算测试统计
const totalTests = computed(() => {
  return Object.keys(results).filter(key => results[key] !== null).length
})

const successCount = computed(() => {
  return Object.values(results).filter(result => result && result.success).length
})

const failCount = computed(() => {
  return totalTests.value - successCount.value
})

// 测试API连接
const testAPIConnection = async () => {
  loading.connection = true
  try {
    await request.get('/test/health')
    results.connection = {
      success: true,
      message: 'API服务器连接正常'
    }
    ElMessage.success('API连接测试成功')
  } catch (error) {
    results.connection = {
      success: false,
      message: `连接失败: ${error.message}`,
      error: error
    }
    ElMessage.error('API连接测试失败')
  } finally {
    loading.connection = false
  }
}

// 测试获取港口列表
const testGetPortList = async () => {
  loading.getList = true
  try {
    const data = await getPortList({ page: 1, size: 5 })
    results.getList = {
      success: true,
      data: data,
      message: '获取港口列表成功'
    }
    ElMessage.success('获取港口列表测试成功')
  } catch (error) {
    results.getList = {
      success: false,
      data: null,
      error: error.message
    }
    ElMessage.error('获取港口列表测试失败')
  } finally {
    loading.getList = false
  }
}

// 测试创建港口
const testCreatePort = async () => {
  loading.create = true
  const testPort = {
    nameCn: '测试港口',
    nameEn: 'Test Port',
    code: 'TSTPT',
    country: '测试国家',
    longitude: 116.397428,
    latitude: 39.90923
  }
  
  try {
    const data = await createPort(testPort)
    testPortId.value = data.id
    results.create = {
      success: true,
      data: data,
      message: '创建港口成功'
    }
    ElMessage.success('创建港口测试成功')
  } catch (error) {
    results.create = {
      success: false,
      data: null,
      error: error.message
    }
    ElMessage.error('创建港口测试失败')
  } finally {
    loading.create = false
  }
}

// 测试更新港口
const testUpdatePort = async () => {
  if (!testPortId.value) {
    ElMessage.warning('请先创建测试港口')
    return
  }
  
  loading.update = true
  const updateData = {
    nameCn: '测试港口(已更新)',
    nameEn: 'Test Port (Updated)',
    code: 'TSTPT',
    country: '测试国家',
    longitude: 116.397428,
    latitude: 39.90923
  }
  
  try {
    const data = await updatePort(testPortId.value, updateData)
    results.update = {
      success: true,
      data: data,
      message: '更新港口成功'
    }
    ElMessage.success('更新港口测试成功')
  } catch (error) {
    results.update = {
      success: false,
      data: null,
      error: error.message
    }
    ElMessage.error('更新港口测试失败')
  } finally {
    loading.update = false
  }
}

// 测试删除港口
const testDeletePort = async () => {
  if (!testPortId.value) {
    ElMessage.warning('请先创建测试港口')
    return
  }
  
  loading.delete = true
  try {
    await deletePort(testPortId.value)
    results.delete = {
      success: true,
      data: { id: testPortId.value },
      message: '删除港口成功'
    }
    testPortId.value = null
    ElMessage.success('删除港口测试成功')
  } catch (error) {
    results.delete = {
      success: false,
      data: null,
      error: error.message
    }
    ElMessage.error('删除港口测试失败')
  } finally {
    loading.delete = false
  }
}

// 运行所有测试
const runAllTests = async () => {
  ElMessage.info('开始运行所有测试...')
  testComplete.value = false
  
  // 清空之前的结果
  Object.keys(results).forEach(key => {
    results[key] = null
  })
  
  // 依次运行测试
  await testAPIConnection()
  await new Promise(resolve => setTimeout(resolve, 500))
  
  await testGetPortList()
  await new Promise(resolve => setTimeout(resolve, 500))
  
  await testCreatePort()
  await new Promise(resolve => setTimeout(resolve, 500))
  
  if (testPortId.value) {
    await testUpdatePort()
    await new Promise(resolve => setTimeout(resolve, 500))
    
    await testDeletePort()
  }
  
  testComplete.value = true
  ElMessage.success('所有测试已完成')
}
</script>

<style scoped>
.test-port-api {
  padding: 20px;
}

.test-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.test-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.test-result {
  margin-top: 15px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.test-result pre {
  margin-top: 10px;
  padding: 10px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.test-summary {
  margin-top: 30px;
  padding: 20px;
  background-color: #f0f9ff;
  border-radius: 6px;
}

.test-summary h4 {
  margin: 0 0 15px 0;
  color: #303133;
}
</style> 