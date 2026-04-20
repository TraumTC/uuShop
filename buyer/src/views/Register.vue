<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { showToast } from 'vant'
import axios from '../plugins/axios'

const router = useRouter()
const store = useStore()

// 表单数据
const customer_num = ref('')
const customer_code = ref('')
const customer_password = ref('')

// 初始化
store.state.index = 1

// 发送验证码
const code = () => {
  if (!customer_num.value) {
    showToast('请输入手机号')
    return
  }

  // 发送验证码请求
  axios.get(store.state.globalhost + 'sms-service/sms/send/' + customer_num.value)
    .then(function (resp) {
      if (resp.code === 0) {
        showToast('短信发送成功')
      } else {
        showToast(resp.msg)
      }
    })
    .catch(function (error) {
      console.error('发送验证码失败:', error)
      showToast('发送验证码失败')
    })
}

// 注册
const register = () => {
  // 表单验证
  if (!customer_num.value) {
    showToast('请输入账号')
    return
  }
  if (!customer_code.value) {
    showToast('请输入验证码')
    return
  }
  if (!customer_password.value) {
    showToast('请输入密码')
    return
  }

  // 构建注册数据
  const user = {
    mobile: customer_num.value,
    code: customer_code.value,
    password: customer_password.value
  }

  // 发送注册请求
  axios.post(store.state.globalhost + 'account-service/user/register', user)
    .then(function (response) {
      if (response.code === 0) {
        showToast('注册成功')
        setTimeout(() => {
          // 跳转到登录页面
          router.push('/login')
        }, 1000)
      } else {
        showToast('注册失败: ' + response.msg)
      }
    })
    .catch(function (error) {
      console.error('注册失败:', error)
      showToast('注册失败')
    })
}
</script>

<template>
  <div class="register-container">
    <div class="register-content">
      <van-form @submit="register">
        <van-cell-group>
          <van-field
            v-model="customer_num"
            label="手机"
            placeholder="请输入账号"
            required
          />
          <van-field
            v-model="customer_code"
            label="验证码"
            placeholder="请输入验证码"
            required
          >
            <template #button>
              <span class="code-btn" @click="code">发送验证码</span>
            </template>
          </van-field>
          <van-field
            v-model="customer_password"
            label="密码"
            type="password"
            placeholder="请输入密码"
            required
          />
        </van-cell-group>
        <div class="register-btn">
          <van-button type="primary" block @click="register" style="background-color: #ffb248; border: 1px solid #ffb248;">注册</van-button>
        </div>
        <div class="link-area">
          <router-link to="/login">登录</router-link>
        </div>
      </van-form>
    </div>
  </div>
</template>

<style scoped>
.disable {
  color: #2ac845;
}

.register-container {
  min-height: 100vh;
  background-color: #efeff4;
  display: flex;
  flex-direction: column;
}

.register-content {
  padding: 20px 15px;
  flex: 1;
}

.van-cell-group {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-top: 10px;
  margin-bottom: 25px;
}

.van-cell-group:first-child {
  margin-top: 20px;
}

.van-field {
  border-bottom: 1px solid #f0f0f0;
}

.van-field:last-child {
  border-bottom: none;
}

.code-btn {
  color: #007aff;
  font-size: 14px;
  padding: 0 10px;
}

.register-btn {
  margin-top: 25px;
  margin-bottom: 25px;
}

.link-area {
  display: block;
  margin-top: 25px;
  text-align: center;
}

.link-area a {
  color: #007aff;
  text-decoration: none;
}

.spliter {
  color: #bbb;
  padding: 0px 8px;
}

.oauth-area {
  position: absolute;
  bottom: 20px;
  left: 0px;
  text-align: center;
  width: 100%;
  padding: 0px;
  margin: 0px;
}

.oauth-area .oauth-btn {
  display: inline-block;
  width: 50px;
  height: 50px;
  background-size: 30px 30px;
  background-position: center center;
  background-repeat: no-repeat;
  margin: 0px 20px;
  border: solid 1px #ddd;
  border-radius: 25px;
}

.oauth-area .oauth-btn:active {
  border: solid 1px #aaa;
}

.oauth-area .oauth-btn.disabled {
  background-color: #ddd;
}
</style>