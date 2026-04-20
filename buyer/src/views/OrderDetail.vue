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
    <van-card v-if="data" class="order-card">
      <van-cell-group>
        <van-cell title="订单号" :value="data.orderId" />
        <van-cell title="收货地址">
          <template #default>
            <div class="address-info">
              <div>{{ data.buyerAddress }}</div>
              <div>{{ data.buyerName }} {{ data.buyerPhone }}</div>
            </div>
          </template>
        </van-cell>
        <van-cell title="订单价格" :value="data.orderAmount + '元'" />
        <van-cell title="下单时间" :value="dateFormat(data.createTime)" />
        <van-cell title="订单状态">
          <template #default>
            <span class="order-status">{{ transformOrderStatus(data.orderStatus) }}</span>
          </template>
        </van-cell>
        <van-cell title="支付状态">
          <template #default>
            <span class="order-status">{{ transformPayStatus(data.payStatus) }}</span>
          </template>
        </van-cell>
        <van-cell title="订单商品">
          <template #default>
            <div class="order-goods" v-for="item in data.orderDetailList" :key="item.productId">
              <div class="goods-image">
                <img :src="item.productIcon" :alt="item.productName" />
              </div>
              <div class="goods-name">{{ item.productName }}</div>
              <div class="goods-quantity">
                <van-icon name="close" size="16" />
                {{ item.productQuantity }}
              </div>
              <div class="goods-price">￥{{ item.productPrice * item.productQuantity }}</div>
            </div>
          </template>
        </van-cell>
      </van-cell-group>
      
      <div class="action-buttons">
        <van-button 
          type="primary" 
          block 
          @click="toPay()"
          v-show="payStatus == 0 && orderStatus == 0"
        >
          确认付款
        </van-button>
        <van-button 
          type="warning" 
          block 
          @click="cancelOrder()"
          v-show="orderStatus == 0"
        >
          取消订单
        </van-button>
      </div>
    </van-card>
    <div v-else class="loading">
      <van-loading type="spinner" color="#1989fa" />
    </div>
  </div>
</template>

<style scoped>
.order-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 10px;
}

.order-card {
  border-radius: 8px;
  overflow: hidden;
}

.address-info {
  text-align: right;
  line-height: 1.4;
}

.order-status {
  font-weight: bold;
  color: red;
}

.order-goods {
  display: flex;
  align-items: center;
  margin: 10px 0;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-goods:last-child {
  border-bottom: none;
}

.goods-image {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-name {
  flex: 1;
  line-height: 40px;
}

.goods-quantity {
  width: 60px;
  line-height: 40px;
  text-align: center;
}

.goods-price {
  width: 80px;
  line-height: 40px;
  text-align: right;
  font-size: 15px;
  font-weight: bold;
}

.action-buttons {
  margin-top: 20px;
  padding: 0 15px 15px;
}

.action-buttons .van-button {
  margin-bottom: 10px;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.van-cell {
  border-bottom: 1px solid #f0f0f0;
}

.van-cell:last-child {
  border-bottom: none;
}
</style>