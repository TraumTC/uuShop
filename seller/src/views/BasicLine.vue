<template>
  <div id="myChart" :style="{ width: '100%', height: '500px' }"></div>
</template>

<script setup>
import { onMounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const API_BASE_URL = 'http://localhost:8086/'

const drawLine = (data) => {
  const myChart = echarts.init(document.getElementById('myChart'))
  myChart.setOption({
    color: '#ea7e53',
    xAxis: {
      type: 'category',
      data: data.names
    },
    tooltip: {},
    yAxis: {
      type: 'value'
    },
    series: [{
      data: data.values,
      type: 'line'
    }]
  })
}

onMounted(() => {
  axios.get(API_BASE_URL + 'order-service/seller/order/basicLineSale').then(function (response) {
    drawLine(response.data.data)
  })
})
</script>

<style scoped>

</style>