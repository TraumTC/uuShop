<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

// 计算当前激活的标签
const activeTab = computed(() => {
  return store.state.index.toString()
})

// 切换标签
const switchTab = (index, path) => {
  store.state.index = index
  window.location.href = '#' + path
}

// 返回
const back = () => {
  history.go(-1)
}
</script>

<template>
  <div id="app">
    <van-nav-bar title="UU优选" id="head" left-text="返回" @click-left="back" />
    <router-view/>
    <van-tabbar v-model="activeTab" id="bar-tab">
      <van-tabbar-item icon="shopping-cart" :to="'/cart'" @click="switchTab(1, '/cart')">
        购买
      </van-tabbar-item>
      <van-tabbar-item icon="list" :to="'/order'" @click="switchTab(2, '/order')">
        订单
      </van-tabbar-item>
      <van-tabbar-item icon="user" :to="'/mine'" @click="switchTab(3, '/mine')">
        我的
        <template #badge>
          <van-badge>1</van-badge>
        </template>
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<style>
#head {
  background-color: #ffb248;
}

#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  min-height: 100vh;
  padding-bottom: 50px;
}

#bar-tab {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
}

.van-tabbar-item--active {
  color: rgb(64, 130, 252);
}
</style>
