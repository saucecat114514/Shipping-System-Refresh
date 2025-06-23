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
        <el-form-item>
          <div class="register-link">
            还没有账号？<el-link type="primary" @click="goToRegister">立即注册</el-link>
          </div>
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
        
        const token = response.data.token;
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(response.data));
        ElMessage.success('登录成功');
        router.push('/dashboard');
      } catch (error) {
        ElMessage.error('用户名或密码错误');
      }
    }
  });
};

const goToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #333;
}

.el-button {
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 10px;
  color: #666;
}
</style>
