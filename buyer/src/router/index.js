import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Cart from "../views/Cart"
import Info from "../views/Info"
import OrderDetail from "../views/OrderDetail"
import Pay from "../views/Pay"
import Order from "../views/Order"
import Mine from "../views/Mine"
import About from "../views/About"
import Login from "../views/Login"
import Register from "../views/Register"
import axios from "../plugins/axios"
import { showToast } from 'vant'

const routes = [
  {
    path: '/',
    component: Cart
  },
  {
    path: '/cart',
    component: Cart
  },
  {
    path: '/info',
    component: Info
  },
  {
    path: '/orderDetail',
    component: OrderDetail
  },
  {
    path: '/pay',
    component: Pay
  },
  {
    path: '/order',
    component: Order
  },
  {
    path: '/mine',
    component: Mine
  },
  {
    path: '/about',
    component: About
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 解决路由跳转错误
const originalPush = router.push
router.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/login')) {
    window.localStorage.removeItem('access-user')
    next()
  } else if(to.path.startsWith('/register')){
    next()
  }
  else {
    let user = JSON.parse(window.localStorage.getItem('access-user'))
    if (!user) {
      next({path: '/login'})
    } else {
      // 校验token合法性
      axios({
        url:'http://localhost:8686/account-service/user/checkToken',
        method:'get',
        headers:{
          token:user.token
        }
      }).then((response) => {
        if(response.code == -1){
          showToast('登录超时！请重新登录！')
          next({path: '/login'})
        }
      })
      next()
    }
  }
})

export default router