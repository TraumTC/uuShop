<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { showToast } from 'vant'
import axios from '../plugins/axios'

const router = useRouter()
const route = useRoute()
const store = useStore()

// 订单金额
const orderAmount = ref('')

// 生命周期
onMounted(() => {
  // 从路由参数获取订单金额
  orderAmount.value = route.query.orderAmount
})

// 付款
const pay = () => {
  // 显示加载状态
  const loading = showToast({
    message: '正在支付...',
    type: 'loading',
    forbidClick: true,
    duration: 0
  })

  // 获取用户ID和订单ID
  const userId = JSON.parse(window.localStorage.getItem('access-user')).userId
  const orderId = route.query.orderId

  // 发送支付请求
  axios.put(store.state.globalhost + 'order-service/buyer/order/pay/' + userId + '/' + orderId)
    .then(function (resp) {
      loading.close()
      showToast('支付成功')
      setTimeout(() => {
        // 跳转到订单详情页
        router.push('/orderDetail?orderId=' + orderId)
      }, 500)
    })
    .catch(function (error) {
      loading.close()
      console.error('支付失败:', error)
      showToast('支付失败')
    })
}
</script>

<template>
  <div class="pay-container">
    <div class="pay-card">
      <div class="pay-title">付款</div>
      <div class="pay-item">
        <span class="pay-label">支付方式</span>
        <div class="pay-method">
          <van-icon name="wechat" size="24" color="#07C160" />
        </div>
      </div>
      <div class="pay-item">
        <span class="pay-label">金额</span>
        <span class="pay-amount">{{ orderAmount }}</span>
      </div>
      <div class="pay-button" @click="pay()">
        付款
      </div>
    </div>
  </div>
</template>

<style scoped>
.pay-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 15px;
}

.pay-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.pay-title {
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.pay-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.pay-item:last-child {
  border-bottom: none;
}

.pay-label {
  font-size: 14px;
  color: #666;
}

.pay-method {
  display: flex;
  align-items: center;
}

.pay-amount {
  font-size: 16px;
  color: #333;
  font-weight: bold;
}

.pay-button {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #52c41a;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  height: 44px;
  margin: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pay-button::after {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #fff;
  border-radius: 50%;
  margin-left: 10px;
  border-top-color: transparent;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.pay-button:hover {
  background-color: #73d13d;
}
</style>