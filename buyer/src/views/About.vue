<script setup>
import { ref } from 'vue'

// 关于信息
const aboutInfo = ref({
  title: '点餐平台',
  version: '1.0.0',
  description: '一个便捷的在线点餐系统，为您提供优质的餐饮服务',
  features: [
    '在线点餐',
    '便捷支付',
    '订单管理',
    '个人中心'
  ],
  contact: {
    phone: '15970599000',
    email: 'service@example.com'
  }
})
</script>

<template>
  <div class="about-container">
    <van-card class="about-card">
      <template #title>
        <h2 class="about-title">{{ aboutInfo.title }}</h2>
      </template>
      <div class="about-content">
        <div class="about-item">
          <span class="about-label">版本：</span>
          <span class="about-value">{{ aboutInfo.version }}</span>
        </div>
        <div class="about-item">
          <span class="about-label">描述：</span>
          <span class="about-value">{{ aboutInfo.description }}</span>
        </div>
        <div class="about-item">
          <span class="about-label">功能：</span>
          <ul class="feature-list">
            <li v-for="(feature, index) in aboutInfo.features" :key="index">
              {{ feature }}
            </li>
          </ul>
        </div>
        <div class="about-item">
          <span class="about-label">联系我们：</span>
          <div class="contact-info">
            <div>电话：{{ aboutInfo.contact.phone }}</div>
            <div>邮箱：{{ aboutInfo.contact.email }}</div>
          </div>
        </div>
      </div>
    </van-card>
  </div>
</template>

<style scoped>
.about-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 10px;
}

.about-card {
  border-radius: 8px;
  overflow: hidden;
}

.about-title {
  text-align: center;
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.about-content {
  padding: 15px;
}

.about-item {
  margin-bottom: 20px;
}

.about-label {
  display: inline-block;
  width: 80px;
  font-weight: bold;
  font-size: 14px;
}

.about-value {
  font-size: 14px;
}

.feature-list {
  margin: 0;
  padding-left: 20px;
}

.feature-list li {
  font-size: 14px;
  margin-bottom: 5px;
}

.contact-info {
  font-size: 14px;
  line-height: 1.5;
}
</style>