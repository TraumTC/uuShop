<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

// 计算当前激活的标签
const activeTab = computed({
  get: () => {
    // 索引从0开始，所以需要减1
    return (store.state.index - 1).toString()
  },
  set: (value) => {
    // 索引从0开始，所以需要加1
    store.state.index = parseInt(value) + 1
  }
})

// 切换标签
const switchTab = (index, path) => {
  store.state.index = index
  router.push(path)
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
    <van-tabbar v-model="activeTab" style="z-index: 1001;">
      <van-tabbar-item icon="shopping-cart" :to="'/cart'">
        购买
      </van-tabbar-item>
      <van-tabbar-item icon="list" :to="'/order'">
        订单
      </van-tabbar-item>
      <van-tabbar-item icon="user" :to="'/mine'">
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



.van-tabbar-item--active {
  color: rgb(64, 130, 252);
}
</style>
