<template>
  <div class="test-container">
    <el-card>
      <template #header>
        <h3>前后端连接测试</h3>
      </template>
      
      <div class="test-section">
        <h4>1. 后端服务状态</h4>
        <el-button @click="testBackendHealth" :loading="testing.health">
          测试后端健康状态
        </el-button>
        <div v-if="results.health" class="result" :class="results.health.success ? 'success' : 'error'">
          {{ results.health.message }}
        </div>
      </div>

      <div class="test-section">
        <h4>2. 认证接口测试</h4>
        <el-button @click="testLogin" :loading="testing.login">
          测试登录接口
        </el-button>
        <div v-if="results.login" class="result" :class="results.login.success ? 'success' : 'error'">
          {{ results.login.message }}
        </div>
      </div>

      <div class="test-section">
        <h4>3. 数据接口测试</h4>
        <el-button @click="testUserList" :loading="testing.users">
          测试用户列表接口
        </el-button>
        <div v-if="results.users" class="result" :class="results.users.success ? 'success' : 'error'">
          {{ results.users.message }}
        </div>
      </div>

      <div class="test-section">
        <h4>4. 完整连接测试</h4>
        <el-button @click="runAllTests" :loading="runningAll" type="primary">
          运行所有测试
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { login } from '@/api/auth'
import { getUserList } from '@/api/user'

const testing = reactive({
  health: false,
  login: false,
  users: false
})

const results = reactive({
  health: null,
  login: null,
  users: null
})

const runningAll = ref(false)

// 测试后端健康状态
const testBackendHealth = async () => {
  testing.health = true
  try {
    const response = await request.get('/test/health')
    results.health = {
      success: true,
      message: `后端服务正常运行 - ${response.message || '健康检查通过'}`
    }
    ElMessage.success('后端连接成功')
  } catch (error) {
    results.health = {
      success: false,
      message: `后端连接失败: ${error.message}`
    }
    ElMessage.error('后端连接失败')
  } finally {
    testing.health = false
  }
}

// 测试登录接口
const testLogin = async () => {
  testing.login = true
  try {
    const response = await login({
      username: 'admin',
      password: 'admin123'
    })
    results.login = {
      success: true,
      message: `登录成功 - 用户: ${response.username}, 角色: ${response.role}`
    }
    ElMessage.success('登录接口测试成功')
  } catch (error) {
    results.login = {
      success: false,
      message: `登录失败: ${error.message}`
    }
    ElMessage.error('登录接口测试失败')
  } finally {
    testing.login = false
  }
}

// 测试用户列表接口  
const testUserList = async () => {
  testing.users = true
  try {
    const response = await getUserList({ page: 1, size: 5 })
    results.users = {
      success: true,
      message: `用户数据获取成功 - 共 ${response.total || response.length} 条记录`
    }
    ElMessage.success('用户接口测试成功')
  } catch (error) {
    results.users = {
      success: false,
      message: `用户接口失败: ${error.message}`
    }
    ElMessage.error('用户接口测试失败')
  } finally {
    testing.users = false
  }
}

// 运行所有测试
const runAllTests = async () => {
  runningAll.value = true
  
  await testBackendHealth()
  await new Promise(resolve => setTimeout(resolve, 500))
  
  await testLogin()
  await new Promise(resolve => setTimeout(resolve, 500))
  
  await testUserList()
  
  runningAll.value = false
  
  const allSuccess = results.health?.success && results.login?.success && results.users?.success
  if (allSuccess) {
    ElMessage.success('🎉 所有测试通过！前后端连接正常')
  } else {
    ElMessage.warning('⚠️ 部分测试失败，请检查相关配置')
  }
}
</script>

<style scoped>
.test-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.test-section {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.test-section h4 {
  margin: 0 0 10px 0;
  color: #409EFF;
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
</style> 