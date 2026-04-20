<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 跳转到购物车页面
const goToCart = () => {
  router.push('/cart')
}

// 跳转到个人中心页面
const goToMine = () => {
  router.push('/mine')
}
</script>

<template>
  <div class="home-container">
    <div class="home-header">
      <h1 class="home-title">欢迎使用点餐平台</h1>
    </div>
    <div class="home-content">
      <van-grid columns="2" gap="20">
        <van-grid-item>
          <div class="grid-item" @click="goToCart">
            <van-icon name="shopping-cart" size="48" color="#1989fa" />
            <div class="grid-item-text">购物车</div>
          </div>
        </van-grid-item>
        <van-grid-item>
          <div class="grid-item" @click="goToMine">
            <van-icon name="user" size="48" color="#1989fa" />
            <div class="grid-item-text">个人中心</div>
          </div>
        </van-grid-item>
      </van-grid>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.home-header {
  background-color: #1989fa;
  color: #fff;
  padding: 20px;
  text-align: center;
}

.home-title {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.home-content {
  flex: 1;
  padding: 40px 20px;
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 150px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.grid-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.grid-item-text {
  margin-top: 10px;
  font-size: 16px;
  color: #333;
}
</style>