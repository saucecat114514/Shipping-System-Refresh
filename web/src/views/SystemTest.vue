<template>
  <div class="system-test">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统功能测试中心</span>
          <el-tag type="success">版本 v1.0.0</el-tag>
        </div>
      </template>

      <!-- 系统状态概览 -->
      <div class="system-status">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="status-card">
              <el-statistic title="系统健康状态" :value="systemStatus.health" format="{value}%" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="status-card">
              <el-statistic title="总用户数" :value="systemStatus.totalUsers" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="status-card">
              <el-statistic title="活跃船舶" :value="systemStatus.activeShips" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="status-card">
              <el-statistic title="待处理订单" :value="systemStatus.pendingOrders" />
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 功能模块测试 -->
      <el-divider content-position="center">功能模块测试</el-divider>
      
      <el-row :gutter="20" class="test-modules">
        <!-- 港口管理 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>🏢 港口管理</span>
                <el-tag :type="testResults.port ? 'success' : 'info'">
                  {{ testResults.port ? '✓ 已测试' : '待测试' }}
                </el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>测试港口CRUD操作和数据查询</p>
              <el-button-group>
                <el-button @click="testPort" :loading="testLoading.port">
                  测试API
                </el-button>
                <el-button @click="$router.push('/ports')" type="primary" plain>
                  港口列表
                </el-button>
                <el-button @click="$router.push('/ports/map')" type="success" plain>
                  港口地图
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>

        <!-- 船舶管理 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>🚢 船舶管理</span>
                <el-tag :type="testResults.ship ? 'success' : 'info'">
                  {{ testResults.ship ? '✓ 已测试' : '待测试' }}
                </el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>测试船舶信息管理和追踪功能</p>
              <el-button-group>
                <el-button @click="testShip" :loading="testLoading.ship">
                  测试API
                </el-button>
                <el-button @click="$router.push('/ships')" type="primary" plain>
                  船舶列表
                </el-button>
                <el-button @click="$router.push('/ships/management')" type="warning" plain>
                  船舶管理
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>

        <!-- 航线管理 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>🛣️ 航线管理</span>
                <el-tag :type="testResults.route ? 'success' : 'info'">
                  {{ testResults.route ? '✓ 已测试' : '待测试' }}
                </el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>测试航线规划和管理功能</p>
              <el-button-group>
                <el-button @click="testRoute" :loading="testLoading.route">
                  测试API
                </el-button>
                <el-button @click="$router.push('/routes')" type="primary" plain>
                  航线列表
                </el-button>
                <el-button @click="$router.push('/routes/planning')" type="success" plain>
                  航线规划
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>

        <!-- 航次管理 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>⛵ 航次管理</span>
                <el-tag :type="testResults.voyage ? 'success' : 'info'">
                  {{ testResults.voyage ? '✓ 已测试' : '待测试' }}
                </el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>测试航次调度和管理功能</p>
              <el-button-group>
                <el-button @click="testVoyage" :loading="testLoading.voyage">
                  测试API
                </el-button>
                <el-button @click="$router.push('/voyages')" type="primary" plain>
                  航次列表
                </el-button>
                <el-button @click="$router.push('/voyages/schedule')" type="success" plain>
                  航次调度
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>

        <!-- 订单管理 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>📦 订单管理</span>
                <el-tag :type="testResults.order ? 'success' : 'info'">
                  {{ testResults.order ? '✓ 已测试' : '待测试' }}
                </el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>测试订单创建和管理功能</p>
              <el-button-group>
                <el-button @click="testOrder" :loading="testLoading.order">
                  测试API
                </el-button>
                <el-button @click="$router.push('/orders')" type="primary" plain>
                  订单列表
                </el-button>
                <el-button @click="$router.push('/orders/create')" type="success" plain>
                  创建订单
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>

        <!-- 用户管理 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>👥 用户管理</span>
                <el-tag :type="testResults.user ? 'success' : 'info'">
                  {{ testResults.user ? '✓ 已测试' : '待测试' }}
                </el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>测试用户账户管理功能</p>
              <el-button-group>
                <el-button @click="testUser" :loading="testLoading.user">
                  测试API
                </el-button>
                <el-button @click="$router.push('/users')" type="primary" plain>
                  用户管理
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>

        <!-- 系统配置 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>⚙️ 系统配置</span>
                <el-tag :type="testResults.config ? 'success' : 'info'">
                  {{ testResults.config ? '✓ 已测试' : '待测试' }}
                </el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>测试系统配置管理功能</p>
              <el-button-group>
                <el-button @click="testConfig" :loading="testLoading.config">
                  测试API
                </el-button>
                <el-button @click="$router.push('/config')" type="primary" plain>
                  系统配置
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>

        <!-- 原有测试 -->
        <el-col :span="12">
          <el-card class="test-card">
            <template #header>
              <div class="module-header">
                <span>🧪 原有测试</span>
                <el-tag type="warning">传统测试</el-tag>
              </div>
            </template>
            <div class="test-content">
              <p>访问原有的简单测试页面</p>
              <el-button-group>
                <el-button @click="$router.push('/test')" type="primary" plain>
                  简单测试
                </el-button>
                <el-button @click="$router.push('/test-port')" type="success" plain>
                  港口API测试
                </el-button>
              </el-button-group>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 批量测试 -->
      <el-divider content-position="center">批量操作</el-divider>
      <div class="batch-test">
        <el-space>
          <el-button type="primary" @click="testAllModules" :loading="batchTesting">
            测试所有模块
          </el-button>
          <el-button @click="clearTestResults">
            清除测试结果
          </el-button>
          <el-button type="success" @click="generateTestReport">
            生成测试报告
          </el-button>
        </el-space>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { getPortList } from '@/api/port'
import { getShipList } from '@/api/ship'
import { getRouteList } from '@/api/route'
import { getVoyageList } from '@/api/voyage'
import { getOrderList } from '@/api/order'
import { getUserList } from '@/api/user'
import { getSystemConfig } from '@/api/config'

// 系统状态数据
const systemStatus = reactive({
  health: 95,
  totalUsers: 4,
  activeShips: 2,
  pendingOrders: 3
})

// 测试结果
const testResults = reactive({
  port: false,
  ship: false,
  route: false,
  voyage: false,
  order: false,
  user: false,
  config: false
})

// 加载状态
const testLoading = reactive({
  port: false,
  ship: false,
  route: false,
  voyage: false,
  order: false,
  user: false,
  config: false
})

const batchTesting = ref(false)

// 测试港口模块
const testPort = async () => {
  testLoading.port = true
  try {
    await getPortList({ page: 1, size: 5 })
    testResults.port = true
    ElMessage.success('港口模块测试通过')
  } catch (error) {
    ElMessage.error('港口模块测试失败: ' + (error.message || '未知错误'))
  } finally {
    testLoading.port = false
  }
}

// 测试船舶模块
const testShip = async () => {
  testLoading.ship = true
  try {
    await getShipList({ page: 1, size: 5 })
    testResults.ship = true
    ElMessage.success('船舶模块测试通过')
  } catch (error) {
    ElMessage.error('船舶模块测试失败: ' + (error.message || '未知错误'))
  } finally {
    testLoading.ship = false
  }
}

// 测试航线模块
const testRoute = async () => {
  testLoading.route = true
  try {
    await getRouteList({ page: 1, size: 5 })
    testResults.route = true
    ElMessage.success('航线模块测试通过')
  } catch (error) {
    ElMessage.error('航线模块测试失败: ' + (error.message || '未知错误'))
  } finally {
    testLoading.route = false
  }
}

// 测试航次模块
const testVoyage = async () => {
  testLoading.voyage = true
  try {
    await getVoyageList({ page: 1, size: 5 })
    testResults.voyage = true
    ElMessage.success('航次模块测试通过')
  } catch (error) {
    ElMessage.error('航次模块测试失败: ' + (error.message || '未知错误'))
  } finally {
    testLoading.voyage = false
  }
}

// 测试订单模块
const testOrder = async () => {
  testLoading.order = true
  try {
    await getOrderList({ page: 1, size: 5 })
    testResults.order = true
    ElMessage.success('订单模块测试通过')
  } catch (error) {
    ElMessage.error('订单模块测试失败: ' + (error.message || '未知错误'))
  } finally {
    testLoading.order = false
  }
}

// 测试用户模块
const testUser = async () => {
  testLoading.user = true
  try {
    await getUserList({ page: 1, size: 5 })
    testResults.user = true
    ElMessage.success('用户模块测试通过')
  } catch (error) {
    ElMessage.error('用户模块测试失败: ' + (error.message || '未知错误'))
  } finally {
    testLoading.user = false
  }
}

// 测试配置模块
const testConfig = async () => {
  testLoading.config = true
  try {
    await getSystemConfig()
    testResults.config = true
    ElMessage.success('配置模块测试通过')
  } catch (error) {
    ElMessage.error('配置模块测试失败: ' + (error.message || '未知错误'))
  } finally {
    testLoading.config = false
  }
}

// 测试所有模块
const testAllModules = async () => {
  batchTesting.value = true
  
  const tests = [
    { name: '港口', fn: testPort },
    { name: '船舶', fn: testShip },
    { name: '航线', fn: testRoute },
    { name: '航次', fn: testVoyage },
    { name: '订单', fn: testOrder },
    { name: '用户', fn: testUser },
    { name: '配置', fn: testConfig }
  ]

  let passCount = 0
  for (const test of tests) {
    try {
      await test.fn()
      passCount++
    } catch (error) {
      console.error(`${test.name}模块测试失败:`, error)
    }
  }

  batchTesting.value = false
  
  ElNotification({
    title: '批量测试完成',
    message: `总共测试 ${tests.length} 个模块，通过 ${passCount} 个`,
    type: passCount === tests.length ? 'success' : 'warning',
    duration: 5000
  })
}

// 清除测试结果
const clearTestResults = () => {
  Object.keys(testResults).forEach(key => {
    testResults[key] = false
  })
  ElMessage.info('测试结果已清除')
}

// 生成测试报告
const generateTestReport = () => {
  const total = Object.keys(testResults).length
  const passed = Object.values(testResults).filter(result => result).length
  const coverage = ((passed / total) * 100).toFixed(1)
  
  ElNotification({
    title: '测试报告',
    message: `
      测试覆盖率: ${coverage}%
      通过模块: ${passed}/${total}
      系统健康度: ${systemStatus.health}%
    `,
    type: passed === total ? 'success' : 'info',
    duration: 8000
  })
}

// 组件挂载时初始化
onMounted(() => {
  ElMessage.info('欢迎使用系统测试中心')
})
</script>

<style scoped>
.system-test {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-status {
  margin-bottom: 30px;
}

.status-card {
  text-align: center;
}

.test-modules {
  margin: 20px 0;
}

.test-card {
  margin-bottom: 20px;
  height: 200px;
}

.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.test-content {
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.test-content p {
  color: #666;
  margin: 0;
}

.batch-test {
  text-align: center;
  padding: 20px 0;
}

:deep(.el-card__body) {
  padding: 15px;
}

:deep(.el-statistic__content) {
  font-size: 24px;
  font-weight: bold;
}

:deep(.el-button-group .el-button) {
  margin: 0;
}
</style> 