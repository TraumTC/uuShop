<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { showToast } from 'vant'
import axios from '../plugins/axios'

const router = useRouter()
const store = useStore()

// 数据
const totalQuantity = ref(0)
const totalPrice = ref(0)
const menuIndex = ref(0)
const logo = ref('../static/logo.jpg')
const data = ref('')

// 生命周期
onMounted(() => {
  store.state.index = 1
  store.state.selectedArray = []
  
  // 从后端获取商品数据
  axios.get(store.state.globalhost + 'product-service/buyer/product/list')
    .then(function (resp) {
      data.value = resp.data
      // 初始化商品数量为0
      if (data.value && data.value.length > 0) {
        data.value.forEach(menu => {
          menu.goods.forEach(item => {
            item.quantity = 0
          })
        })
      }
    })
    .catch(function (error) {
      console.error('获取商品数据失败:', error)
      showToast('获取商品数据失败')
    })
})

// 提交订单
const submit = () => {
  if (store.state.selectedArray.length === 0) {
    showToast('请选择商品')
    return
  }
  router.push('/info')
}

// 清空购物车
const clear = () => {
  totalQuantity.value = 0
  totalPrice.value = 0
  
  // 菜品的数量设置为0
  if (data.value && data.value.length > 0) {
    data.value.forEach(item => {
      item.goods.forEach(good => {
        good.quantity = 0
      })
    })
  }
  
  // 清空已选数组
  store.state.selectedArray = []
}

// 更改商品数量
const changeQuantity = (item, type) => {
  switch (type) {
    case "minus":
      if (item.quantity === 0) return
      item.quantity--
      totalQuantity.value--
      totalPrice.value -= item.price
      
      // 更新已选数组
      for (let i = 0; i < store.state.selectedArray.length; i++) {
        if (item.id === store.state.selectedArray[i].productId) {
          store.state.selectedArray[i].productQuantity = item.quantity
          if (item.quantity === 0) {
            store.state.selectedArray.splice(i, 1)
          }
          return
        }
      }
      break
    case "plus":
      item.quantity++
      if (item.quantity <= item.stock) {
        totalQuantity.value++
        totalPrice.value += item.price
      } else {
        item.quantity = item.stock
        showToast(item.name + '已被你抢空！')
        return
      }
      
      // 查询是否已经存在
      for (let i = 0; i < store.state.selectedArray.length; i++) {
        if (item.id === store.state.selectedArray[i].productId) {
          store.state.selectedArray[i].productQuantity = item.quantity
          return
        }
      }
      
      // 添加对象
      store.state.selectedArray.push({
        productId: item.id,
        productQuantity: item.quantity
      })
      break
  }
}
</script>

<template>
     <div class="mui-content"> 
         <div><img :src="logo" alt="" class="logo"/></div> 

         <div class="mui-row"> 
             <!-- 左侧菜单 --> 
             <div class="mui-col-xs-3" style="border-right: 1px solid #c8c7cc"> 
                 <div id="segmentedControls" class="mui-segmented-control mui-segmented-control-inverted mui-segmented-control-vertical"> 
                     <a class="mui-control-item" :class="{'menu-active':menuIndex==index1}" @click="menuIndex = index1" v-for="(item,index1) in data">{{item.name}}</a> 
                 </div> 
             </div> 
             <!-- 商品列表 --> 
             <div id="segmentedControlContents" class="mui-col-xs-9"> 
                 <div v-for="(menu,index) in data" v-show="index == menuIndex"> 
                     <div class="itembox mui-row" v-for="item in menu.goods"> 
                         <div class="mui-col-xs-3"> 
                             <img :src="item.icon"/> 
                         </div> 
                         <div class="mui-col-xs-9"> 
                             <div class="item"> 
                                 <div class="itemmain"> 
                                     {{item.name}} 
                                 </div> 
                                 <div class="itemsub"> 
                                     {{item.description}} 
                                 </div> 
                             </div> 
                             <div class="operation"> 
                                 <div class="operationPrice">￥{{item.price}}</div> 
                                 <div class="operationSelect"> 
                                     <span class="van-icon van-icon-minus operationMinus" @click="changeQuantity(item,'minus')"></span> 
                                     <span>{{item.quantity}}</span> 
                                     <span class="van-icon van-icon-plus operationPlus" @click="changeQuantity(item,'plus')"></span> 
                                 </div> 
                             </div> 
                         </div> 
                     </div> 
                 </div> 
             </div> 
         </div> 
         <!-- 底部菜单 --> 
         <div class="cart-box" id="cart-box"> 
             <div class="cart-container"> 
                 <div class="cell1"> 
                     <div class="cart-icon-box"> 
                         <span class="van-icon van-icon-cart"> 
                           <span class="van-badge" id="goodsNum">{{totalQuantity}}</span> 
                         </span> 
                     </div> 
                 </div> 
                 <div class="cell12"> 
                     ￥<span id="totalMoney">{{totalPrice}}</span> 
                 </div> 
                 <div class="cell2" @click="clear"> 
                     <span class="van-icon van-icon-delete"></span> 
                     清空购物车 
                 </div> 
                 <div class="cell3" @click="submit"> 
                     <span class="van-icon van-icon-check"></span> 
                     确认下单 
                 </div> 
             </div> 
         </div> 
     </div> 
</template>

<style scoped>
/* 全局样式 */
.mui-content {
  padding: 0;
  background-color: white;
}

/* logo样式 */
.logo {
  width: 100%;
  height: 100%;
}

/* 左侧菜单样式 */
.mui-segmented-control {
  background-color: white;
}

.mui-segmented-control .mui-control-item {
  line-height: 50px;
  width: 100%;
  color: #666;
}

.mui-segmented-control.mui-segmented-control-inverted .mui-control-item.mui-active {
  background-color: #fff;
}

#segmentedControlContents, #segmentedControls {
  background-color: white;
}

#segmentedControlContents {
  padding: 15px 0 0 10px;
}

/* 商品列表样式 */
.mui-row.mui-fullscreen > [class*="mui-col-"] {
  height: 100%;
}

.mui-col-xs-3 {
  overflow-y: auto;
  height: 100%;
  border-right: 1px solid #c8c7cc;
}

.itembox {
  margin: 0 0 16px;
  padding: 0 0 5px;
  border-bottom: 1px silver solid;
}

.itembox img {
  width: 60px;
  height: 60px;
  vertical-align: center;
}

.item {
  width: 250px;
  height: 40px;
  text-align: left;
}

.itemmain {
  font-size: 14px;
  font-weight: bold;
}

.itemsub {
  font-size: 13px;
  font-family: 'Helvetica Neue', Helvetica, sans-serif;
}

/* 操作区域样式 */
.operation {
  width: 220px;
  height: 20px;
  margin-top: 3px;
}

.operationPrice {
  float: left;
  width: 50px;
  height: 100%;
  font-size: 18px;
  font-weight: bold;
  color: red;
}

.operationType {
  float: left;
  position: relative;
  left: 10px;
  width: 60px;
  height: 100%;
}

.operationSelect {
  float: left;
  width: 110px;
  height: 100%;
  position: relative;
  left: 10px;
}

.operationMinus {
  color: red;
  float: left;
  margin-left: 10px;
  font-weight: bold;
  cursor: pointer;
}

.operationPlus {
  color: green;
  float: right;
  margin-right: 15px;
  font-weight: bold;
  cursor: pointer;
}

/* 菜单激活状态 */
.menu-active {
  color: red;
  background-color: #c8c7cc;
}

/* 底部购物车样式 */
.cart-box {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
}

.cart-container {
  display: flex;
  padding: 0;
  margin: 0;
  height: 51px;
}

.cell1, .cell2, .cell3 {
  display: inline-block;
}

.cell1 {
  flex: 2;
  background-color: #141d27;
  color: #cccccc;
}

.cell12 {
  flex: 2;
  background-color: #141d27;
  text-align: center;
  padding-right: 0;
  color: white;
  font-size: 17px;
  line-height: 51px;
}

.cell2 {
  flex: 3;
  background-color: #dd524d;
  text-align: center;
  padding-left: 0;
  margin: 0;
  color: white;
  font-size: 15px;
  line-height: 51px;
  cursor: pointer;
}

.cell3 {
  flex: 3;
  background-color: #007aff;
  text-align: center;
  padding-right: 10px;
  color: white;
  font-size: 15px;
  line-height: 51px;
  cursor: pointer;
}

.cart-icon-box {
  border: black 5px solid;
  border-radius: 50%;
  height: 50px;
  width: 50px;
  text-align: center;
  background-color: #6c757d;
  z-index: 100;
  position: relative;
  top: -15px;
  left: 10%;
}

.cart-icon-box .van-icon {
  font-size: 24px;
  color: #fff;
  line-height: 40px;
}

.van-badge {
  position: absolute;
  top: -5px;
  right: -10px;
  background-color: #ff4d4f;
  color: #fff;
  font-size: 12px;
  min-width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  border-radius: 10px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .item {
    width: 200px;
  }
  
  .operation {
    width: 180px;
  }
  
  .cell2, .cell3 {
    font-size: 13px;
  }
}
</style>