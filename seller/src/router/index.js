import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import AddProduct from '../views/AddProuduct.vue'
import ProductManage from '../views/ProductManage.vue'
import Bar from '../views/Bar.vue'
import BasicLine from '../views/BasicLine.vue'
import EditProduct from '../views/EditProduct.vue'
import Error from '../views/Error.vue'
import OrderManage from '../views/OrderManage.vue'
import StackedLine from '../views/StackedLine.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    name: '商品模块',
    component: Home,
    icon: 'Goods',
    show: true,
    redirect: '/home/productManage',
    children: [
      {
        path: 'productManage',
        name: '商品管理',
        icon: 'Menu',
        show: true,
        component: ProductManage
      },
      {
        path: 'addProduct',
        name: '添加商品',
        icon: 'Plus',
        show: true,
        component: AddProduct
      },
      {
        path: 'editProduct',
        name: '修改商品',
        show: false,
        component: EditProduct
      }
    ]
  },
  {
    path: '/home',
    name: '订单模块',
    component: Home,
    icon: 'Grid',
    show: true,
    children: [
      {
        path: 'orderManage',
        name: '订单管理',
        icon: 'DocumentCopy',
        show: true,
        component: OrderManage
      }
    ]
  },
  {
    path: '/home',
    name: '数据统计',
    component: Home,
    icon: 'Finished',
    show: true,
    children: [
      {
        path: 'bar',
        name: '总销量',
        show: true,
        icon: 'DataLine',
        component: Bar
      },
      {
        path: 'basicLine',
        name: '日销量',
        show: true,
        icon: 'TrendCharts',
        component: BasicLine
      },
      {
        path: 'stackedLine',
        name: '销量明细',
        show: true,
        icon: 'QuestionFilled',
        component: StackedLine
      }
    ]
  },
  {
    path: '/login',
    name: '登录',
    show: false,
    component: Login
  },
  {
    path: '/error',
    name: '错误',
    show: false,
    component: Error
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('access-admin')
  
  // 登录页面和错误页面不需要token
  if (to.path === '/login' || to.path === '/error') {
    next()
    return
  }
  
  // 其他页面需要token
  if (!token) {
    next('/login')
    return
  }
  
  next()
})

export default router