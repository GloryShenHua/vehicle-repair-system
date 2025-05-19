<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <!-- Replace the missing image with an icon -->
        <el-icon :size="60" class="logo-icon"><Van /></el-icon>
        <h1>车辆维修管理系统</h1>
      </div>
      
      <el-card class="login-card">
        <template #header>
          <div class="header-tabs">
            <el-radio-group v-model="activeTab" size="large">
              <el-radio-button label="login">登录</el-radio-button>
              <el-radio-button label="register">注册</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        
        <!-- 登录表单 -->
        <el-form v-if="activeTab === 'login'" :model="form" @submit.prevent="handleLogin" :rules="rules">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">登录</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 注册表单 -->
        <el-form v-else :model="registerForm" @submit.prevent="handleRegister" :rules="registerRules" ref="registerFormRef">
          <el-form-item prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock" show-password />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">注册</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { User, Lock, Van } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const activeTab = ref('login')
const loading = ref(false)
const registerFormRef = ref<FormInstance>()

const form = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await authStore.register({
          username: registerForm.username,
          password: registerForm.password
        })
        ElMessage.success('注册成功，请登录')
        activeTab.value = 'login'
        registerForm.username = ''
        registerForm.password = ''
        registerForm.confirmPassword = ''
      } catch (error: any) {
        if (error.response?.data?.message?.includes('exists')) {
          ElMessage.error('用户名已存在')
        } else {
          ElMessage.error('注册失败')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 450px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.logo-icon {
  margin-bottom: 15px;
  color: white;
}

.login-card {
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.header-tabs {
  display: flex;
  justify-content: center;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
  padding: 12px 0;
}
</style>