<template>
  <div id="myChart" :style="{ width: '100%', height: '550px' }"></div>
</template>

<script setup>
import { onMounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const API_BASE_URL = 'http://localhost:8086/'

const drawLine = (data) => {
  const myChart = echarts.init(document.getElementById('myChart'))
  myChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: data.names
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    toolbox: {
      feature: {
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: data.datas
  })
}

onMounted(() => {
  axios.get(API_BASE_URL + 'order-service/seller/order/stackedLineSale').then(function (response) {
    drawLine(response.data.data)
  })
})
</script>

<style scoped>

</style>