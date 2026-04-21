<template>
  <div>
    <el-form label-width="100px" class="demo-ruleForm">
      <el-form-item label="关键字：" prop="keyWord">
        <div style="display: flex; align-items: center; gap: 10px;">
          <el-input v-model="keyWord" placeholder="请输入商品关键字" style="width: 230px;"></el-input>
          <el-button type="primary" @click="submitForm()">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <span>分类检索：</span>
          <el-select v-model="selectedCategory" placeholder="请选择商品分类" style="width: 230px;">
            <el-option v-for="item in category" :key="item.type" :label="item.name" :value="item.type"></el-option>
          </el-select>
          <el-button type="primary" @click="categoryForm()">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <div style="flex: 1;"></div>
          <el-button @click="importExcel" type="success" style="margin-right: 5px;">
            <el-icon><Upload /></el-icon>
            导入Excel
          </el-button>
          <el-button @click="exportExcel" type="success">
            <el-icon><Download /></el-icon>
            导出Excel
          </el-button>
        </div>
      </el-form-item>
    </el-form>

    <el-dialog title="导入文件" v-model="dialogTableVisible" width="30%">
      <el-upload
        class="upload-demo"
        drag
        :action="importUrl"
        multiple
        :on-success="importSuccess"
        :on-error="importError"
        accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">只能上传xls/xlsx</div>
        </template>
      </el-upload>
    </el-dialog>

    <el-table
      :data="tableData"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column
        fixed
        prop="id"
        label="编号"
        width="80"
      >
      </el-table-column>
      <el-table-column
        prop="name"
        label="商品名称"
        width="230"
      >
      </el-table-column>
      <el-table-column
        prop="price"
        label="商品价格"
        width="80"
      >
      </el-table-column>
      <el-table-column
        prop="stock"
        label="商品库存"
        width="80"
      >
      </el-table-column>
      <el-table-column
        prop="description"
        label="商品描述"
        width="200"
      >
      </el-table-column>
      <el-table-column label="商品图标" width="100">
        <template v-slot="scope">
          <img 
            :src="scope.row.icon || '/favicon.svg'" 
            width="60" 
            @error="handleImageError" 
          />
        </template>
      </el-table-column>
      <el-table-column label="上架" width="150">
        <template v-slot="scope">
          <el-switch
            v-model="scope.row.status"
            active-color="#13ce66"
            active-text="上架"
            inactive-color="#ff4949"
            inactive-text="下架"
            @change="changeSwitch(scope.row)"
          >
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column
        prop="categoryName"
        label="商品分类"
        width="80"
      >
      </el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button
            size="small"
            @click="edit(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="del(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination 
      style="margin-top: 20px; float: right" 
      background 
      layout="prev, pager, next" 
      :page-size="pageSize" 
      :total="total" 
      v-model:current-page="currentPage"
      :hide-on-single-page="true"
    >
    </el-pagination>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search, Download, Upload } from '@element-plus/icons-vue'

const router = useRouter()
const API_BASE_URL = 'http://localhost:8086/'

const tableData = ref([])
const pageSize = ref(5)
const total = ref(0)
const keyWord = ref('')
const currentPage = ref(1)
const dialogTableVisible = ref(false)
const category = ref([])
const selectedCategory = ref('')

const importUrl = computed(() => {
  const token = JSON.parse(localStorage.getItem('access-admin'))?.token
  return API_BASE_URL + 'product-service/seller/product/import' + (token ? '?token=' + token : '')
})

const importSuccess = (data) => {
  if (data.code == 0) {
    ElMessageBox.alert('数据导入成功！', '', {
      confirmButtonText: '确定',
      callback: action => {
        dialogTableVisible.value = false
        location.reload()
      }
    })
  }
  if (data.code == -1) {
    ElMessageBox.alert('数据导入失败！', '', {
      confirmButtonText: '确定',
      callback: action => {
        dialogTableVisible.value = false
        location.reload()
      }
    })
  }
}

const importError = () => {
  ElMessageBox.alert('数据导入失败', '', {
    confirmButtonText: '确定',
    callback: action => {
      dialogTableVisible.value = false
      location.reload()
    }
  })
}

const exportExcel = () => {
  const token = JSON.parse(localStorage.getItem('access-admin'))?.token
  if (!token) {
    ElMessage.error('未登录，请先登录')
    return
  }
  
  window.location.href = API_BASE_URL + 'product-service/seller/product/export?token=' + token
}

const importExcel = () => {
  dialogTableVisible.value = true
}



const submitForm = () => {
  // 让翻页复原
  currentPage.value = 1
  selectedCategory.value = ''
  if (keyWord.value == '') {
    axios.get(API_BASE_URL + 'product-service/seller/product/list/1/' + pageSize.value).then(function (resp) {
      if (resp.data && resp.data.data) {
        tableData.value = resp.data.data.content || []
        pageSize.value = resp.data.data.size
        total.value = resp.data.data.total
      } else {
        tableData.value = []
        pageSize.value = 5
        total.value = 0
      }
    })
  } else {
    axios.get(API_BASE_URL + 'product-service/seller/product/like/' + keyWord.value + '/1/' + pageSize.value).then(function (resp) {
      if (resp.data && resp.data.data) {
        tableData.value = resp.data.data.content || []
        pageSize.value = resp.data.data.size
        total.value = resp.data.data.total
      } else {
        tableData.value = []
        pageSize.value = 5
        total.value = 0
      }
    })
  }
}

const categoryForm = () => {
  // 让翻页复原
  currentPage.value = 1
  keyWord.value = ''
  if (selectedCategory.value == '') {
    ElMessage({
      showClose: true,
      message: '请选择商品分类',
      type: 'error'
    })
    return
  }
  axios.get(API_BASE_URL + 'product-service/seller/product/findByCategory/' + selectedCategory.value + '/1/' + pageSize.value).then(function (resp) {
    if (resp.data && resp.data.data) {
      tableData.value = resp.data.data.content || []
      pageSize.value = resp.data.data.size
      total.value = resp.data.data.total
    } else {
      tableData.value = []
      pageSize.value = 5
      total.value = 0
    }
  })
}

const page = () => {
  if (keyWord.value != '') {
    axios.get(API_BASE_URL + 'product-service/seller/product/like/' + keyWord.value + '/' + currentPage.value + '/' + pageSize.value).then(function (resp) {
      if (resp.data && resp.data.data) {
        tableData.value = resp.data.data.content || []
        pageSize.value = resp.data.data.size
        total.value = resp.data.data.total
      } else {
        tableData.value = []
        pageSize.value = 5
        total.value = 0
      }
    })
  } else if (selectedCategory.value != '') {
    axios.get(API_BASE_URL + 'product-service/seller/product/findByCategory/' + selectedCategory.value + '/' + currentPage.value + '/' + pageSize.value).then(function (resp) {
      if (resp.data && resp.data.data) {
        tableData.value = resp.data.data.content || []
        pageSize.value = resp.data.data.size
        total.value = resp.data.data.total
      } else {
        tableData.value = []
        pageSize.value = 5
        total.value = 0
      }
    })
  } else {
    axios.get(API_BASE_URL + 'product-service/seller/product/list/' + currentPage.value + '/' + pageSize.value).then(function (resp) {
      if (resp.data && resp.data.data) {
        tableData.value = resp.data.data.content || []
        pageSize.value = resp.data.data.size
        total.value = resp.data.data.total
      } else {
        tableData.value = []
        pageSize.value = 5
        total.value = 0
      }
    })
  }
}

const edit = (row) => {
  router.push('/home/editProduct?id=' + row.id)
}

const del = (row) => {
  ElMessageBox.confirm('确认删除【' + row.name + '】吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(API_BASE_URL + 'product-service/seller/product/delete/' + row.id).then(function (resp) {
      if (resp.data.code == 0) {
        ElMessageBox.alert('【' + row.name + '】已删除', '', {
          confirmButtonText: '确定',
          callback: action => {
            location.reload()
          }
        })
      }
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除'
    })
  })
}

const changeSwitch = (data) => {
  axios.put(API_BASE_URL + 'product-service/seller/product/updateStatus/' + data.id + '/' + data.status).then(function (resp) {
    if (resp.data.data == true) {
      ElMessage({
        showClose: true,
        message: '【' + data.name + '】已上架',
        type: 'success'
      })
    } else {
      ElMessage({
        showClose: true,
        message: '【' + data.name + '】已下架',
        type: 'error'
      })
    }
  })
}

// 处理图片加载失败
const handleImageError = (event) => {
  event.target.src = '/favicon.svg'
}

// 监听页码变化
watch(currentPage, (newPage) => {
  page()
})

onMounted(() => {
  // 获取商品列表
  axios.get(API_BASE_URL + 'product-service/seller/product/list/1/' + pageSize.value).then(function (resp) {
    if (resp.data && resp.data.data) {
      tableData.value = resp.data.data.content || []
      pageSize.value = resp.data.data.size
      total.value = resp.data.data.total
    } else {
      tableData.value = []
      pageSize.value = 5
      total.value = 0
    }
  })
  // 获取分类数据
  axios.get(API_BASE_URL + 'product-service/seller/product/findAllProductCategory').then(function (resp) {
    if (resp.data && resp.data.data) {
      category.value = resp.data.data.content || []
    } else {
      category.value = []
    }
  })
})
</script>

<style scoped>

</style>