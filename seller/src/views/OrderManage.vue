<template>
  <div>
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      :cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
      :header-cell-style="{ textAlign: 'center', verticalAlign: 'middle' }"
      size="small"
    >
      <el-table-column
        fixed
        prop="orderId"
        label="订单编号"
        width="280"
      >
      </el-table-column>
      <el-table-column
        prop="buyerName"
        label="买家姓名"
        width="100"
      >
      </el-table-column>
      <el-table-column
        prop="buyerPhone"
        label="联系电话"
        width="130"
      >
      </el-table-column>
      <el-table-column
        prop="buyerAddress"
        label="收货地址"
        width="130"
      >
      </el-table-column>
      <el-table-column
        prop="orderAmount"
        label="订单总价"
        width="80"
      >
      </el-table-column>
      <el-table-column
        label="订单状态"
        width="80"
      >
        <template v-slot="scope">
          {{ transformOrderStatus(scope.row.orderStatus) }}
        </template>
      </el-table-column>
      <el-table-column
        label="支付状态"
        width="80"
      >
        <template v-slot="scope">
          {{ transformPayStatus(scope.row.payStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间"
                       width="160">
        <template v-slot="scope">
          {{ dateFormat(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template v-slot="scope">
          <el-button
            :disabled="scope.row.orderStatus == 2 || scope.row.orderStatus == 1"
            size="small"
            type="danger"
            @click="cancel(scope.row)"
          >
            取消
          </el-button>
          <el-button
            :disabled="scope.row.orderStatus == 1 || scope.row.orderStatus == 2"
            size="small"
            type="success"
            @click="finish(scope.row)"
          >
            完成
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
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessageBox } from 'element-plus'

const API_BASE_URL = 'http://localhost:8086/'
const tableData = ref(null)
const pageSize = ref(5)
const total = ref(null)
const currentPage = ref(1)

const transformOrderStatus = (status) => {
  switch (status) {
    case 0: return '新订单'; break
    case 1: return '已完成'; break
    case 2: return '已取消'; break
  }
}

const transformPayStatus = (status) => {
  switch (status) {
    case 0: return '未支付'; break
    case 1: return '已支付'; break
  }
}

const dateFormat = (time) => {
  const date = new Date(time)
  const year = date.getFullYear()
  const month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1
  const day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate()
  const hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours()
  const minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()
  const seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds()
  return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds
}

const getAuthHeaders = () => {
  const token = JSON.parse(localStorage.getItem('access-admin'))?.token
  return { headers: { token: token } }
}

const page = () => {
  axios.get(API_BASE_URL + 'order-service/seller/order/list/' + currentPage.value + '/' + pageSize.value, getAuthHeaders()).then(function (resp) {
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

const cancel = (row) => {
  axios.put(API_BASE_URL + 'order-service/seller/order/cancel/' + row.orderId, {}, getAuthHeaders()).then(function (resp) {
    if (resp.data.code == 1) {
      ElMessageBox.alert('订单【' + row.orderId + '】状态异常', '错误', {
        confirmButtonText: '确定'
      })
    }
    if (resp.data.code == 0) {
      ElMessageBox.alert('订单【' + row.orderId + '】已取消', '成功', {
        confirmButtonText: '确定',
        callback: action => {
          location.reload()
        }
      })
    }
  }).catch(function (error) {
    console.error('取消订单失败:', error)
    ElMessageBox.alert('取消订单失败，请稍后重试', '错误', {
      confirmButtonText: '确定'
    })
  })
}

const finish = (row) => {
  if (row.payStatus == 0) {
    ElMessageBox.alert('订单【' + row.orderId + '】未支付，无法完成该订单', '错误', {
      confirmButtonText: '确定'
    })
  } else if (row.orderStatus == 2) {
    ElMessageBox.alert('订单【' + row.orderId + '】已取消，无法完成该订单', '错误', {
      confirmButtonText: '确定'
    })
  } else {
    axios.put(API_BASE_URL + 'order-service/seller/order/finish/' + row.orderId, {}, getAuthHeaders()).then(function (resp) {
      if (resp.data.code == 1) {
        ElMessageBox.alert('订单【' + row.orderId + '】状态异常', '错误', {
          confirmButtonText: '确定'
        })
      }
      if (resp.data.code == 0) {
        ElMessageBox.alert('订单【' + row.orderId + '】已完成', '成功', {
          confirmButtonText: '确定',
          callback: action => {
            location.reload()
          }
        })
      }
    }).catch(function (error) {
      console.error('完成订单失败:', error)
      ElMessageBox.alert('完成订单失败，请稍后重试', '错误', {
        confirmButtonText: '确定'
      })
    })
  }
}

// 监听页码变化
watch(currentPage, (newPage) => {
  page()
})

onMounted(() => {
  axios.get(API_BASE_URL + 'order-service/seller/order/list/1/' + pageSize.value, getAuthHeaders()).then(function (resp) {
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
})
</script>

<style scoped>

</style>