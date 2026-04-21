<template>

</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()

onMounted(() => {
  ElMessageBox.alert('登录信息失效！', '提示', {
    confirmButtonText: '确定'
  }).then(() => {
    localStorage.removeItem('access-admin')
    router.replace({ path: '/login' })
  })
})
</script>

<style scoped>

</style>