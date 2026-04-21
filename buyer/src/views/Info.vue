<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { showToast } from 'vant'
import axios from '../plugins/axios'

const router = useRouter()
const store = useStore()

// 表单数据
const name = ref('')
const phone = ref('')
const address = ref('')

// 提交订单
const submit = () => {
  // 表单验证
  if (!name.value) {
    showToast('请输入姓名')
    return
  }
  if (!phone.value) {
    showToast('请输入电话')
    return
  }
  if (!address.value) {
    showToast('请输入地址')
    return
  }

  // 构建订单数据
  const orderForm = {
    name: name.value,
    phone: phone.value,
    address: address.value,
    id: JSON.parse(window.localStorage.getItem('access-user')).userId,
    items: store.state.selectedArray
  }

  // 显示加载状态
  const loading = showToast({
    message: 'Loading...',
    type: 'loading',
    forbidClick: true,
    duration: 0
  })

  // 提交订单
  axios.post(store.state.globalhost + 'order-service/buyer/order/create', orderForm)
    .then(function(resp) {
      loading.close()
      if (resp.code === 0) {
        showToast('下单成功！')
        setTimeout(() => {
          // 跳转到订单详情页
          router.push('/orderDetail?orderId=' + resp.data.orderId)
        }, 1000)
      }
    })
    .catch(function(error) {
      loading.close()
      console.error('提交订单失败:', error)
      showToast('提交订单失败')
    })
}
</script>

<template>
  <div class="info-container">
    <van-form @submit="submit">
      <van-cell-group>
        <van-field
          v-model="name"
          label="姓名"
          placeholder="请输入收件人姓名"
          required
        />
        <van-field
          v-model="phone"
          label="电话"
          placeholder="请输入联系电话"
          required
        />
        <van-field
          v-model="address"
          label="地址"
          placeholder="请输入送货地址"
          required
        />
      </van-cell-group>
      <div class="submit-btn">
        <van-button type="primary" block @click="submit">确认订单</van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
.info-container {
  padding: 10px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.submit-btn {
  margin-top: 30px;
  padding: 0 10px;
}

.van-cell-group {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.van-field {
  border-bottom: 1px solid #f0f0f0;
}

.van-field:last-child {
  border-bottom: none;
}
</style>