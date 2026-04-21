<template>
  <el-container class="home_container">
    <el-header>
      <div class="home_title">UUShop社区优选平台后台管理系统</div>
      <div class="home_userinfoContainer">
        <el-avatar :src="admin.imgUrl" v-if="admin.imgUrl"></el-avatar>
        <el-avatar v-else>{{ admin.name?.charAt(0) || '管' }}</el-avatar>
        <el-dropdown style="position: relative;top: -10px;left: 10px;">
                  <span class="el-dropdown-link home_userinfo">
                    {{ admin.name || '管理员' }}<el-icon class="home_userinfo"><ArrowDown /></el-icon>
                  </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>

      <el-aside width="200px" style="background-color: #fff">

        <el-menu router :default-active="activeMenu">
          <template v-for="(item, index) in menuList" :key="item.name">
            <el-sub-menu v-if="item.children && item.children.length > 0" :index="(index + 1).toString()">
              <template #title>
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.name }}</span>
              </template>
              <el-menu-item
                v-for="child in item.children"
                :key="child.name"
                :index="child.path"
                v-show="child.show"
              >
                <el-icon v-if="child.icon">
                  <component :is="child.icon" />
                </el-icon>
                <span>{{ child.name }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item
              v-else
              :index="item.path"
              v-show="item.show"
            >
              <el-icon v-if="item.icon">
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.name }}</span>
            </el-menu-item>
          </template>
        </el-menu>

      </el-aside>

      <el-container>
        <el-main>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
          <router-view />
        </el-main>
        <el-footer>@V1.0-Spring Cloud Alibaba + Vue</el-footer>
      </el-container>

    </el-container>

  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, markRaw } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Goods, Menu, Plus, Grid, DocumentCopy, Finished, DataLine, TrendCharts, QuestionFilled, ArrowDown } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const API_BASE_URL = 'http://localhost:8086/'

// 菜单数据
const menuList = ref([
  {
    path: '/home',
    name: '商品模块',
    icon: markRaw(Goods),
    show: true,
    children: [
      {
        path: '/home/productManage',
        name: '商品管理',
        icon: markRaw(Menu),
        show: true
      },
      {
        path: '/home/addProduct',
        name: '添加商品',
        icon: markRaw(Plus),
        show: true
      },
      {
        path: '/home/editProduct',
        name: '修改商品',
        show: false
      }
    ]
  },
  {
    path: '/home',
    name: '订单模块',
    icon: markRaw(Grid),
    show: true,
    children: [
      {
        path: '/home/orderManage',
        name: '订单管理',
        icon: markRaw(DocumentCopy),
        show: true
      }
    ]
  },
  {
    path: '/home',
    name: '数据统计',
    icon: markRaw(Finished),
    show: true,
    children: [
      {
        path: '/home/bar',
        name: '总销量',
        show: true,
        icon: markRaw(DataLine)
      },
      {
        path: '/home/basicLine',
        name: '日销量',
        show: true,
        icon: markRaw(TrendCharts)
      },
      {
        path: '/home/stackedLine',
        name: '销量明细',
        show: true,
        icon: markRaw(QuestionFilled)
      }
    ]
  }
])

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path || '/home/productManage'
})

// 当前路由名称
const currentRouteName = computed(() => {
  const path = route.path
  for (const item of menuList.value) {
    if (item.children) {
      for (const child of item.children) {
        if (child.path === path) {
          return child.name
        }
      }
    }
  }
  return '首页'
})

// 管理员信息
const admin = ref({})

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('access-admin')
    router.push('/login')
  }).catch(() => {
    // 取消操作
  })
}

onMounted(() => {
  // 检查登录状态
  const token = localStorage.getItem('access-admin')
  if (!token) {
    router.push('/login')
    return
  }
  
  // 获取管理员信息
  try {
    const adminInfo = JSON.parse(token)
    admin.value = adminInfo
    
    // 校验token合法性
    axios({
      url: API_BASE_URL + 'account-service/admin/checkToken',
      method: 'get',
      headers: {
        token: adminInfo.token
      }
    }).then((response) => {
      if (response.data.code === -1) {
        ElMessage.error(response.data.msg)
        localStorage.removeItem('access-admin')
        router.push('/login')
      }
    }).catch((error) => {
      console.error('Token校验失败:', error)
    })
  } catch (error) {
    console.error('解析管理员信息失败:', error)
    localStorage.removeItem('access-admin')
    router.push('/login')
  }
})
</script>

<style scoped>
.home_container {
  height: 100vh;
  position: absolute;
  top: 0px;
  left: 0px;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.el-container {
  flex: 1;
  display: flex;
}

/* 嵌套的el-container（包含aside和main） */
.el-container > .el-container {
  flex-direction: row;
}

/* 最内层的el-container（包含main和footer） */
.el-container > .el-container > .el-container {
  flex-direction: column;
}

.el-header {
  background-color: #20a0ff;
  color: #333;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.el-aside {
  background-color: #ECECEC;
}

.el-main {
  background-color: #fff;
  color: #000;
  text-align: center;
  padding: 20px;
  flex: 1;
}

.el-footer {
  background-color: #ea7e53;
  color: #fff;
  font-size: 16px;
  line-height: 60px;
  text-align: center;
}

.home_title {
  color: #fff;
  font-size: 22px;
  display: inline;
}

.home_userinfo {
  color: #fff;
  cursor: pointer;
}

.home_userinfoContainer {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.el-breadcrumb {
  margin-bottom: 20px;
  text-align: left;
}

.el-menu {
  border-right: none;
}
</style>