<template>
  <div class="simple-test">
    <h1>测试页面</h1>
    <p>如果您看到这个页面，说明Vue路由工作正常！</p>
    <el-button type="primary" @click="showMessage">点击测试</el-button>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const showMessage = () => {
  ElMessage.success('Element Plus 工作正常！')
}
</script>

<style scoped>
.simple-test {
  padding: 40px;
  text-align: center;
}

h1 {
  color: #409eff;
  margin-bottom: 20px;
}

p {
  margin-bottom: 30px;
  font-size: 16px;
}
</style> 