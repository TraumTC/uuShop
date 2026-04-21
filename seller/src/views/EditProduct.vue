<template>
  <div style="margin-top: 60px; margin-left: 130px; width: 600px; height: 500px; border: 0px solid red;">
    <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="100px" class="demo-ruleForm">
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="ruleForm.name"></el-input>
      </el-form-item>
      <el-form-item label="商品价格" prop="price">
        <el-input v-model="ruleForm.price"></el-input>
      </el-form-item>
      <el-form-item label="商品库存" prop="stock">
        <el-input v-model.number="ruleForm.stock"></el-input>
      </el-form-item>
      <el-form-item label="商品描述" prop="description">
        <el-input v-model="ruleForm.description"></el-input>
      </el-form-item>
      <el-form-item label="商品图标" prop="icon">
        <el-input v-model="ruleForm.icon"></el-input>
      </el-form-item>

      <el-form-item label="商品分类" prop="categoryType">
        <el-select v-model="ruleForm.categoryType" placeholder="请选择分类" style="width: 100%">
          <el-option v-for="item in category" :key="item.type" :label="item.name" :value="item.type"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="商品状态" prop="status">
        <el-switch
          v-model="ruleForm.status"
          active-color="#13ce66"
          active-text="上架"
          :active-value="1"
          inactive-color="#ff4949"
          inactive-text="下架"
          :inactive-value="0"
        >
        </el-switch>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm()">修改</el-button>
        <el-button @click="resetForm()">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const API_BASE_URL = 'http://localhost:8086/'
const ruleForm = ref({
  name: '',
  price: '',
  stock: '',
  description: '',
  icon: '',
  categoryType: '',
  status: 0
})
const category = ref([])
const ruleFormRef = ref(null)

const valiNumber = (rule, value, callback) => {
  let reg = /^[+-]?(0|([1-9]\d*))(\.\d+)?$/g
  if (value === '') {
    callback(new Error('请输入商品价格'))
  } else if (!reg.test(value)) {
    callback(new Error('价格必须为数值'))
  } else {
    callback()
  }
}

const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 15, message: '长度在 1 到 15 个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, validator: valiNumber, trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入商品库存', trigger: 'blur' },
    { type: 'number', message: '库存必须为数值' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { max: 10, message: '长度不能超过10个字符', trigger: 'blur' }
  ],
  icon: [
    { required: true, message: '请输入商品图标链接', trigger: 'blur' }
  ],
  categoryType: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择商品状态', trigger: 'change' }
  ]
}

const submitForm = () => {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      // 转换数据结构，将categoryType转换为category对象
      const submitData = {
        ...ruleForm.value,
        category: {
          categoryType: ruleForm.value.categoryType
        }
      }
      // 删除categoryType字段，避免重复
      delete submitData.categoryType
      
      console.log('提交的商品数据:', submitData)
      axios.put(API_BASE_URL + 'product-service/seller/product/update', submitData)
        .then(function(resp) {
          console.log('修改商品响应:', resp)
          console.log('响应数据:', resp.data)
          
          // 检查响应结构
          if (resp.data && typeof resp.data === 'object') {
            if (resp.data.code === 0) {
              ElMessage.success(ruleForm.value.name + '修改成功')
              router.push('/home/productManage')
            } else {
              const errorMessage = resp.data.message || resp.data.msg || '修改失败'
              ElMessage.error('修改失败: ' + errorMessage)
            }
          } else {
            console.error('响应结构异常:', resp.data)
            ElMessage.error('服务器响应异常，请稍后重试')
          }
        })
        .catch(function(error) {
          console.error('修改商品失败:', error)
          console.error('错误详情:', error.response)
          const errorMessage = error.response?.data?.message || error.message || '网络错误'
          ElMessage.error('修改失败: ' + errorMessage)
        })
    }
  })
}

const resetForm = () => {
  ruleFormRef.value.resetFields()
}

onMounted(() => {
  // 获取分类数据
  axios.get(API_BASE_URL + 'product-service/seller/product/findAllProductCategory').then(function(resp) {
    category.value = resp.data.data.content
  })
  // 获取商品详情
  axios.get(API_BASE_URL + 'product-service/seller/product/findById/' + route.query.id).then(function(resp) {
    const product = resp.data.data
    // 确保categoryType属性存在
    if (product.category && product.category.categoryType) {
      product.categoryType = product.category.categoryType
      delete product.category
    }
    ruleForm.value = product
  })
})
</script>

<style scoped>

</style>