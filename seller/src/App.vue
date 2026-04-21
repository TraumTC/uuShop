<template>
  <div id="app">
    <router-view/>
    <audio hidden id="notice" :src="music"></audio>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const websock = ref(null)
const music = ref('alert.mp3')

const initWebSocket = () => {
  // 检查是否已登录
  const token = localStorage.getItem('access-admin')
  if (!token) return
  
  // 使用与参考项目相同的WebSocket地址
  websock.value = new WebSocket('ws://localhost:8082/webSocket')
  websock.value.onmessage = webSocketOnMessage
  websock.value.onopen = webSocketOnOpen
  websock.value.onerror = websocketonerror
  websock.value.onclose = webSocketClose
}

const webSocketOnOpen = (event) => {
  console.log('建立连接')
}

const webSocketOnMessage = (event) => {
  document.getElementById('notice').play()
  ElMessageBox.alert('有新的订单', '消息', {
    confirmButtonText: '确定',
    callback: action => {
      // 检查是否已登录
      const token = localStorage.getItem('access-admin')
      if (token) {
        router.push('/orderManage')
      } else {
        router.push('/login')
      }
    }
  })
}

const webSocketClose = (event) => {
  console.log('连接关闭')
}

const websocketonerror = (event) => {
  console.log('连接错误', event)
  // 连接错误时尝试重连
  setTimeout(() => {
    initWebSocket()
  }, 3000)
}

// 监听路由变化，登录后初始化WebSocket
watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/home')) {
    initWebSocket()
  }
})

onMounted(() => {
  // 检查是否已登录
  const token = localStorage.getItem('access-admin')
  if (token) {
    initWebSocket()
  }
})

onUnmounted(() => {
  if (websock.value) {
    websock.value.close()
  }
})
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
  min-height: 100vh;
}
</style>