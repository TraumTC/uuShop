<template>
  <div class="login-container">
    <el-form
      :model="ruleForm"
      :rules="rules"
      status-icon
      ref="ruleFormRef"
      label-position="left"
      label-width="0px"
      class="demo-ruleForm login-page"
    >
      <h3 class="title">系统登录</h3>
      <el-form-item prop="username">
        <el-input
          type="text"
          v-model="ruleForm.username"
          auto-complete="off"
          placeholder="用户名"
        ></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          type="password"
          v-model="ruleForm.password"
          auto-complete="off"
          placeholder="密码"
        ></el-input>
      </el-form-item>
      <el-form-item style="width: 100%;">
        <el-button
          type="primary"
          style="width: 100%;"
          @click="handleSubmit"
          :loading="logining"
        >
          登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const API_BASE_URL = 'http://localhost:8086/'
const ruleForm = reactive({
  username: 'admin',
  password: '123123'
})
const logining = ref(false)
const ruleFormRef = ref(null)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleSubmit = () => {
  // 简化验证逻辑，直接提交表单
  logining.value = true
  axios.get(API_BASE_URL + 'account-service/admin/login', { params: ruleForm }).then(function (response) {
    logining.value = false
    if (response.data.code == -1) {
      ElMessageBox.alert(response.data.msg, '提示', {
        confirmButtonText: '确定'
      })
    }
    if (response.data.code == 0) {
      localStorage.setItem('access-admin', JSON.stringify(response.data.data))
      router.replace({ path: '/home' })
    }
  }).catch(function (error) {
    logining.value = false
    console.error('登录请求失败:', error)
    ElMessageBox.alert('网络错误，请稍后重试', '提示', {
      confirmButtonText: '确定'
    })
  })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.login-page {
  border-radius: 5px;
  width: 350px;
  padding: 35px 35px 15px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.title {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

label.el-checkbox.rememberme {
  margin: 0px 0px 15px;
  text-align: left;
}
</style>