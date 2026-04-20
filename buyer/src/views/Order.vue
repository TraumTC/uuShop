<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import axios from '../plugins/axios'

const store = useStore()

// 订单数据
const data = ref('')

// 生命周期
onMounted(() => {
  store.state.index = 2
  
  // 从后端获取订单列表
  const userId = JSON.parse(window.localStorage.getItem('access-user')).userId
  axios.get(store.state.globalhost + 'order-service/buyer/order/list/' + userId + '/1/100')
    .then(function (resp) {
      data.value = resp.data
    })
    .catch(function (error) {
      console.error('获取订单列表失败:', error)
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
</script>

<template>
  <div class="order-container">
    <van-cell-group v-if="data && data.length > 0">
      <van-cell 
        v-for="item in data" 
        :key="item.orderId"
        is-link 
        :to="'/orderDetail?orderId=' + item.orderId"
      >
        <template #title>
          <div class="order-item">
            <div class="order-header">
              <span class="order-id">订单号：{{ item.orderId }}</span>
              <span class="order-price">￥ <span class="price">{{ item.orderAmount }}</span></span>
            </div>
            <div class="order-footer">
              <span class="order-time">{{ dateFormat(item.createTime) }}</span>
              <span class="order-status">{{ transformOrderStatus(item.orderStatus) }}</span>
            </div>
          </div>
        </template>
      </van-cell>
    </van-cell-group>
    <div v-else class="empty-order">
      <van-empty description="暂无订单" />
    </div>
  </div>
</template>

<style scoped>
.order-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 10px;
}

.order-item {
  width: 100%;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.order-id {
  font-size: 14px;
}

.order-price {
  font-size: 14px;
}

.price {
  color: red;
  font-size: 18px;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
}

.order-time {
  color: #c8cbcf;
  font-size: 12px;
}

.order-status {
  color: cornflowerblue;
  font-size: 14px;
}

.empty-order {
  padding: 50px 0;
  text-align: center;
}

.van-cell-group {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.van-cell {
  border-bottom: 1px solid #f0f0f0;
  padding: 15px;
}

.van-cell:last-child {
  border-bottom: none;
}
</style>