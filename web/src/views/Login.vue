<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-title">航运管理系统登录</div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>


<script setup>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { login } from '@/api/auth';

const router = useRouter();
const loginFormRef = ref();
const loginForm = reactive({
  username: '',
  password: ''
});

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
};

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await login({
          username: loginForm.username,
          password: loginForm.password
        });
        
        const token = response.token;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(response));
        ElMessage.success('登录成功');
        router.push('/dashboard');
      } catch (error) {
        ElMessage.error('用户名或密码错误');
      }
    }
  });
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  background-color: #ffffff;
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
  font-size: 24px;
  color: #333333;
}

:deep(.el-form-item__label) {
  color: #666666;
  font-size: 14px;
}

:deep(.el-input) {
  width: 100%;
}

:deep(.el-button) {
  width: 100%;
  margin-top: 10px;
  background-color: #0071e3;
  color: #ffffff;
  border: none;
  border-radius: 6px;
}

:deep(.el-button:hover) {
  background-color: #0050b3;
}
</style>
