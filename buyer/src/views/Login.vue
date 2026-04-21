<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { showToast } from 'vant'
import axios from '../plugins/axios'

const router = useRouter()
const store = useStore()

// 表单数据
const customer_num = ref('15970599000')
const customer_password = ref('123456')

// 初始化
store.state.index = 1

// 登录
const login = () => {
  // 表单验证
  if (!customer_num.value) {
    showToast('请输入账号')
    return
  }
  if (!customer_password.value) {
    showToast('请输入密码')
    return
  }

  // 显示加载状态
  const loading = showToast({
    message: '正在登录...',
    type: 'loading',
    forbidClick: true,
    duration: 0
  })

  // 构建登录数据
  const user = {
    mobile: customer_num.value,
    password: customer_password.value
  }

  // 发送登录请求
  axios.get(store.state.globalhost + 'account-service/user/login', { params: user })
    .then(function (response) {
      loading.close()
      if (response.code === -1) {
        showToast(response.msg)
        return
      }
      if (response.code === 0) {
        // 保存用户信息到本地存储
        localStorage.setItem('access-user', JSON.stringify(response.data))
        // 跳转到购物车页面
        router.replace({ path: '/cart' })
      }
    })
    .catch(function (error) {
      loading.close()
      console.error('登录失败:', error)
      showToast('登录失败')
    })
}
</script>

<template>
  <div class="login-container">
    <div class="login-content">
      <van-form @submit="login">
        <van-cell-group>
          <van-field
            v-model="customer_num"
            label="手机"
            placeholder="请输入账号"
            required
          />
          <van-field
            v-model="customer_password"
            label="密码"
            type="password"
            placeholder="请输入密码"
            required
          />
        </van-cell-group>
        <div class="login-btn">
          <van-button type="primary" block @click="login" style="background-color: #ffb248; border: 1px solid #ffb248;">登录</van-button>
        </div>
        <div class="link-area">
          <router-link to="/register">注册账号</router-link>
        </div>
      </van-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  background-color: #efeff4;
  display: flex;
  flex-direction: column;
}

.login-content {
  padding: 20px 15px;
  flex: 1;
}

.van-cell-group {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 25px;
}

.van-field {
  border-bottom: 1px solid #f0f0f0;
}

.van-field:last-child {
  border-bottom: none;
}

.login-btn {
  margin-bottom: 25px;
}

.link-area {
  text-align: center;
}

.link-area a {
  color: #007aff;
  text-decoration: none;
}
</style>