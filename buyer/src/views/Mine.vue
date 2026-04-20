<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { showConfirmDialog } from 'vant'

const router = useRouter()
const store = useStore()

// 用户信息
const user = ref({
  mobile: '123456'
})

// 初始化
store.state.index = 3
// 从本地存储获取用户信息
const userInfo = localStorage.getItem('access-user')
if (userInfo) {
  user.value = JSON.parse(userInfo)
}

// 退出登录
const logout = () => {
  showConfirmDialog({
    title: '确认退出',
    message: '确定退出吗?',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(() => {
      // 移除本地存储的用户信息
      localStorage.removeItem('access-user')
      // 跳转到首页
      router.replace({ path: '/' })
    })
    .catch(() => {
      // 取消退出
    })
}
</script>

<template>
  <div class="mine-container">
    <div class="mine-content">
      <van-cell-group>
        <van-cell>
          <template #icon>
            <van-icon name="user" size="24" />
          </template>
          <template #default>
            <p class="user-mobile">{{ user.mobile }}</p>
          </template>
        </van-cell>
      </van-cell-group>
      
      <br />
      
      <van-cell-group>
        <van-cell is-link to="/order" title="我的订单" />
        <van-cell is-link to="/about" title="关于点餐平台" />
      </van-cell-group>
      
      <br /><br /><br />
      
      <div class="quit-btn">
        <van-button type="danger" block @click="logout" style="background-color: #ffb248; border: 1px solid #ffb248;">退出登录</van-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.mine-container {
  min-height: 100vh;
  background-color: #efeff4;
  padding: 10px;
}

.mine-content {
  width: 100%;
}

.user-mobile {
  margin: 0;
  font-size: 16px;
}

.quit-btn {
  margin: 0 auto;
  width: 95%;
}

.van-cell-group {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.van-cell {
  border-bottom: 1px solid #f0f0f0;
}

.van-cell:last-child {
  border-bottom: none;
}
</style>