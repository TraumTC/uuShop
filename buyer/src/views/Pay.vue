<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { showToast, showLoading, hideLoading } from 'vant'
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
  showLoading({
    message: '正在支付...',
    forbidClick: true
  })

  // 获取用户ID和订单ID
  const userId = JSON.parse(window.localStorage.getItem('access-user')).userId
  const orderId = route.query.orderId

  // 发送支付请求
  axios.put(store.state.globalhost + 'order-service/buyer/order/pay/' + userId + '/' + orderId)
    .then(function (resp) {
      hideLoading()
      showToast('支付成功')
      setTimeout(() => {
        // 跳转到订单详情页
        router.push('/orderDetail?orderId=' + orderId)
      }, 500)
    })
    .catch(function (error) {
      hideLoading()
      console.error('支付失败:', error)
      showToast('支付失败')
    })
}
</script>

<template>
  <div class="pay-container">
    <van-card class="pay-card">
      <van-cell-group>
        <van-cell title="付款" />
        <van-cell title="支付方式">
          <template #default>
            <div class="pay-method">
              <van-icon name="wechat" size="24" color="#07C160" />
              <span>微信支付</span>
            </div>
          </template>
        </van-cell>
      </van-cell-group>
      
      <van-cell-group class="pay-amount">
        <van-field
          label="金额"
          v-model="orderAmount"
          readonly
        />
        <div class="pay-btn">
          <van-button type="primary" block @click="pay()">付款</van-button>
        </div>
      </van-cell-group>
    </van-card>
  </div>
</template>

<style scoped>
.pay-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 10px;
}

.pay-card {
  border-radius: 8px;
  overflow: hidden;
}

.pay-method {
  display: flex;
  align-items: center;
}

.pay-method span {
  margin-left: 10px;
  font-size: 14px;
}

.pay-amount {
  margin-top: 20px;
}

.pay-btn {
  margin-top: 30px;
  padding: 0 15px 15px;
}

.van-cell {
  border-bottom: 1px solid #f0f0f0;
}

.van-cell:last-child {
  border-bottom: none;
}
</style>