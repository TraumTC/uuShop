<script setup>
import { ref, onMounted, computed, watch } from 'vue'
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
const logo = ref('/logo.jpg')
const data = ref('')
const cartVisible = ref(false)
const quantityMap = ref({})

// 生命周期
onMounted(() => {
  store.state.index = 1
  store.state.selectedArray = []
  
  // 从后端获取商品数据
  const apiUrl = 'http://localhost:8086/product-service/buyer/product/list'
  axios.get(apiUrl)
    .then(function (resp) {
      data.value = resp.data
      // 过滤只显示上架状态的商品
      if (data.value && data.value.length > 0) {
        data.value = data.value.map(menu => {
          const filteredGoods = menu.goods.filter(item => {
            const status = Number(item.status)
            return status === 1 // 1表示上架状态
          })
          return {
            ...menu,
            goods: filteredGoods
          }
        })
        // 过滤掉没有商品的分类
        data.value = data.value.filter(menu => menu.goods.length > 0)
        // 初始化商品数量为0
        data.value.forEach(menu => {
          menu.goods.forEach(item => {
            quantityMap.value[item.id] = 0
          })
        })
      } else {
        showToast('暂无商品数据')
      }
    })
    .catch(function (error) {
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
  
  // 清空商品数量
  if (data.value && data.value.length > 0) {
    data.value.forEach(item => {
      item.goods.forEach(good => {
        quantityMap.value[good.id] = 0
      })
    })
  }
  
  // 清空已选数组
  store.state.selectedArray = []
  
  // 隐藏购物车
  cartVisible.value = false
}

// 切换购物车显示状态
const toggleCart = () => {
  cartVisible.value = !cartVisible.value
}

// 更改商品数量
const changeQuantity = (item, type) => {
  switch (type) {
    case "minus":
      if (quantityMap.value[item.id] === 0) return
      quantityMap.value[item.id]--
      totalQuantity.value--
      totalPrice.value = parseFloat((totalPrice.value - item.price).toFixed(2))
      
      // 更新已选数组
      for (let i = 0; i < store.state.selectedArray.length; i++) {
        if (item.id === store.state.selectedArray[i].productId) {
          store.state.selectedArray[i].productQuantity = quantityMap.value[item.id]
          if (quantityMap.value[item.id] === 0) {
            store.state.selectedArray.splice(i, 1)
          }
          break
        }
      }
      
      // 当商品数量为0时隐藏购物车
      if (totalQuantity.value === 0) {
        cartVisible.value = false
      }
      break
    case "plus":
      quantityMap.value[item.id]++
      if (quantityMap.value[item.id] <= item.stock) {
        totalQuantity.value++
        totalPrice.value = parseFloat((totalPrice.value + item.price).toFixed(2))
        // 有商品时显示购物车
        cartVisible.value = true
      } else {
        quantityMap.value[item.id] = item.stock
        showToast(item.name + '已被你抢空！')
        return
      }
      
      // 查询是否已经存在
      for (let i = 0; i < store.state.selectedArray.length; i++) {
        if (item.id === store.state.selectedArray[i].productId) {
          store.state.selectedArray[i].productQuantity = quantityMap.value[item.id]
          return
        }
      }
      
      // 添加对象
      store.state.selectedArray.push({
        productId: item.id,
        productQuantity: quantityMap.value[item.id]
      })
      break
  }
}
</script>

<template>
     <div class="mui-content"> 
         <div><img :src="logo" alt="" class="logo"/></div> 

         <div class="main-container">
             <!-- 左侧分类 -->
             <div class="category-sidebar">
                 <div class="category-item" 
                     v-for="(item, index) in data" 
                     :key="index"
                     :class="{'category-active': menuIndex == index}"
                     @click="menuIndex = index"
                 >
                     {{item.name}}
                 </div>
             </div>
             
             <!-- 右侧商品列表 -->
             <div class="goods-container">
                 <div v-for="(menu, index) in data" v-show="index == menuIndex">
                     <div class="itembox" v-for="item in menu.goods">
                         <div class="item-image">
                             <img :src="item.icon" @error="$event.target.src='data:image/svg+xml;utf8,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2280%22 height=%2280%22 viewBox=%220 0 80 80%22%3E%3Crect width=%2280%22 height=%2280%22 fill=%22%23f5f5f5%22/%3E%3Ctext x=%2240%22 y=%2245%22 text-anchor=%22middle%22 fill=%22%23999%22 font-size=%2212%22%3E图片加载失败%3C/text%3E%3C/svg%3E'"/>
                         </div>
                         <div class="item-info">
                             <div class="item-name">{{item.name}}</div>
                             <div class="item-desc">{{item.description}}</div>
                             <div class="item-price">￥{{item.price}}</div>
                         </div>
                         <div class="item-operation">
                             <span class="van-icon van-icon-minus operationMinus" @click="changeQuantity(item,'minus')"></span>
                             <span class="item-quantity">{{quantityMap[item.id] || 0}}</span>
                             <span class="van-icon van-icon-plus operationPlus" @click="changeQuantity(item,'plus')"></span>
                         </div>
                     </div>
                 </div>
             </div>
         </div> 
         <!-- 底部菜单 --> 
         <div class="cart-box" id="cart-box" v-show="cartVisible || totalQuantity > 0"> 
             <div class="cart-container"> 
                 <div class="cell1" @click="toggleCart"> 
                     <div class="cart-icon-box"> 
                         <span class="van-icon van-icon-cart"> 
                           <span class="van-badge" id="goodsNum">{{totalQuantity}}</span> 
                         </span> 
                     </div> 
                 </div> 
                 <div class="cell12" v-show="cartVisible"> 
                     ￥<span id="totalMoney">{{totalPrice.toFixed(2)}}</span> 
                 </div> 
                 <div class="cell2" @click="clear" v-show="cartVisible"> 
                     <span class="van-icon van-icon-delete"></span> 
                     清空购物车 
                 </div> 
                 <div class="cell3" @click="submit" v-show="cartVisible"> 
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

/* 主容器样式 */
.main-container {
  display: flex;
  height: calc(100vh - 130px);
  width: 100%;
}

/* 左侧分类样式 */
.category-sidebar {
  width: 80px;
  min-width: 80px;
  max-width: 80px;
  background-color: #f5f5f5;
  border-right: 1px solid #e0e0e0;
  overflow-y: auto;
  flex-shrink: 0;
}

/* 商品列表样式 */
.goods-container {
  flex: 1;
  padding: 10px;
  background-color: #f5f5f5;
  overflow-y: auto;
}

.category-item {
  padding: 15px 10px;
  text-align: center;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #e0e0e0;
}

.category-item:hover {
  background-color: #e8e8e8;
}

.category-active {
  color: #ffb248 !important;
  background-color: #fff !important;
  font-weight: bold;
  border-left: 3px solid #ffb248;
}

/* 商品列表样式 */
.goods-container {
  padding: 10px;
  background-color: #f5f5f5;
}

.itembox {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.item-image {
  flex: 0 0 80px;
  margin-right: 10px;
}

.item-image img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.item-operation {
  display: flex;
  align-items: center;
  margin-left: 10px;
}

.item-quantity {
  margin: 0 15px;
  font-size: 16px;
  min-width: 20px;
  text-align: center;
}

.operationMinus {
  color: #ff4d4f;
  font-size: 20px;
  cursor: pointer;
}

.operationPlus {
  color: #52c41a;
  font-size: 20px;
  cursor: pointer;
}

/* 底部购物车样式 */
.cart-box {
  position: fixed;
  bottom: 50px;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #141d27;
}

.cart-container {
  display: flex;
  padding: 0;
  margin: 0;
  height: 51px;
  align-items: center;
}

.cell1, .cell2, .cell3 {
  display: inline-block;
}

.cell1 {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.cell12 {
  flex: 2;
  text-align: center;
  color: white;
  font-size: 17px;
  line-height: 51px;
  transition: all 0.3s ease;
}

.cell2 {
  flex: 3;
  background-color: #dd524d;
  text-align: center;
  color: white;
  font-size: 15px;
  line-height: 51px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cell3 {
  flex: 3;
  background-color: #007aff;
  text-align: center;
  color: white;
  font-size: 15px;
  line-height: 51px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cart-icon-box {
  border: 2px solid #fff;
  border-radius: 50%;
  height: 40px;
  width: 40px;
  text-align: center;
  background-color: #ffb248;
  position: relative;
  top: -10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-icon-box .van-icon {
  font-size: 20px;
  color: #fff;
}

.van-badge {
  position: absolute;
  top: -5px;
  right: -5px;
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