<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { showConfirmDialog, showToast } from 'vant'
import axios from '../plugins/axios'

const router = useRouter()
const route = useRoute()
const store = useStore()

// 订单数据
const data = ref('')
const payStatus = ref(0)
const orderStatus = ref(0)

// 生命周期
onMounted(() => {
  store.state.index = 2
  
  // 从后端获取订单详情
  const userId = JSON.parse(window.localStorage.getItem('access-user')).userId
  const orderId = route.query.orderId
  
  axios.get(store.state.globalhost + 'order-service/buyer/order/detail/' + userId + '/' + orderId)
    .then(function (resp) {
      data.value = resp.data
      payStatus.value = resp.data.payStatus
      orderStatus.value = resp.data.orderStatus
    })
    .catch(function (error) {
      console.error('获取订单详情失败:', error)
      showToast('获取订单详情失败')
    })
})

// 转换订单状态
const transformOrderStatus = (status) => {
  switch (status) {
    case 0: return '新订单'
    case 1: return '已完成'
    case 2: return '已取消'
    default: return '未知状态'
  }
}

// 转换支付状态
const transformPayStatus = (status) => {
  switch (status) {
    case 0: return '未支付'
    case 1: return '已支付'
    default: return '未知状态'
  }
}

// 日期格式化
const dateFormat = (time) => {
  const date = new Date(time)
  const year = date.getFullYear()
  const month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1
  const day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate()
  const hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours()
  const minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()
  const seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds()
  return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds
}

// 取消订单
const cancelOrder = () => {
  showConfirmDialog({
    title: '确认取消',
    message: '确定取消订单吗?',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(() => {
      const userId = JSON.parse(window.localStorage.getItem('access-user')).userId
      const orderId = route.query.orderId
      
      axios.put(store.state.globalhost + 'order-service/buyer/order/cancel/' + userId + '/' + orderId)
        .then(function (resp) {
          if (resp.code === 0) {
            showToast('取消成功')
            setTimeout(() => {
              window.location.reload()
            }, 500)
          }
        })
        .catch(function (error) {
          console.error('取消订单失败:', error)
          showToast('取消订单失败')
        })
    })
    .catch(() => {
      // 取消操作
    })
}

// 去支付
const toPay = () => {
  router.push('/pay?orderAmount=' + data.value.orderAmount + '&orderId=' + route.query.orderId)
}
</script>

<template>
  <div class="order-detail-container">
    <div v-if="data" class="order-card">
      <div class="order-info">
        <div class="info-item">
          <span class="info-label">订单号</span>
          <span class="info-value">{{ data.orderId }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">收货地址</span>
          <div class="info-value address-info">
            <div>{{ data.buyerAddress }}</div>
            <div>{{ data.buyerName }} {{ data.buyerPhone }}</div>
          </div>
        </div>
        <div class="info-item">
          <span class="info-label">订单价格</span>
          <span class="info-value">{{ data.orderAmount }}元</span>
        </div>
        <div class="info-item">
          <span class="info-label">下单时间</span>
          <span class="info-value">{{ dateFormat(data.createTime) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">订单状态</span>
          <span class="info-value status-new">{{ transformOrderStatus(data.orderStatus) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">支付状态</span>
          <span class="info-value status-unpaid">{{ transformPayStatus(data.payStatus) }}</span>
        </div>
      </div>
      
      <div class="order-goods" v-for="item in data.orderDetailList" :key="item.productId">
        <div class="goods-image">
          <img :src="item.productIcon" :alt="item.productName" />
        </div>
        <div class="goods-name">{{ item.productName }}</div>
        <div class="goods-quantity">× {{ item.productQuantity }}</div>
        <div class="goods-price">￥{{ item.productPrice * item.productQuantity }}</div>
      </div>
      
      <div class="action-buttons">
        <div 
          class="button pay-button" 
          @click="toPay()"
          v-show="payStatus == 0 && orderStatus == 0"
        >
          确认付款
        </div>
        <div 
          class="button cancel-button" 
          @click="cancelOrder()"
          v-show="orderStatus == 0"
        >
          取消订单
        </div>
      </div>
    </div>
    <div v-else class="loading">
      <van-loading type="spinner" color="#1989fa" />
    </div>
  </div>
</template>

<style scoped>
.order-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 0;
}

.order-card {
  background-color: #fff;
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.order-info {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #666;
}

.info-value {
  font-size: 14px;
  color: #333;
  text-align: right;
}

.address-info {
  text-align: right;
  line-height: 1.4;
  flex: 1;
  margin-left: 20px;
}

.status-new {
  color: red;
  font-weight: bold;
}

.status-unpaid {
  color: red;
  font-weight: bold;
}

.order-goods {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.order-goods:last-child {
  border-bottom: none;
}

.goods-image {
  width: 80px;
  height: 80px;
  margin-right: 15px;
  flex-shrink: 0;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.goods-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  line-height: 1.4;
}

.goods-quantity {
  margin: 0 20px;
  font-size: 14px;
  color: #666;
}

.goods-price {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  flex-shrink: 0;
}

.action-buttons {
  padding: 15px;
}

.button {
  display: block;
  width: 100%;
  height: 44px;
  line-height: 44px;
  text-align: center;
  border-radius: 8px;
  font-size: 16px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pay-button {
  background-color: #52c41a;
  color: #fff;
}

.cancel-button {
  background-color: #fa8c16;
  color: #fff;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}
</style>