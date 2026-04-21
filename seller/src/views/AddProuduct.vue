<template>
  <div style="margin-top: 60px; margin-left: 130px; width: 600px; height: 500px; border: 0px solid red;">
    <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="100px" class="demo-ruleForm">
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="ruleForm.productName"></el-input>
      </el-form-item>
      <el-form-item label="商品价格" prop="productPrice">
        <el-input v-model="ruleForm.productPrice"></el-input>
      </el-form-item>
      <el-form-item label="商品库存" prop="productStock">
        <el-input v-model.number="ruleForm.productStock"></el-input>
      </el-form-item>
      <el-form-item label="商品描述" prop="productDescription">
        <el-input v-model="ruleForm.productDescription"></el-input>
      </el-form-item>
      <el-form-item label="商品图标" prop="productIcon">
        <el-input v-model="ruleForm.productIcon"></el-input>
      </el-form-item>
      <el-form-item label="商品分类" prop="categoryType">
        <el-select v-model="ruleForm.categoryType" placeholder="请选择分类" style="width: 100%">
          <el-option v-for="item in category" :key="item.type" :label="item.name" :value="item.type"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品状态" prop="productStatus">
        <el-switch
          v-model="ruleForm.productStatus"
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
        <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
        <el-button @click="resetForm('ruleForm')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const API_BASE_URL = 'http://localhost:8086/'
const ruleForm = reactive({
  productName: '',
  productPrice: '',
  productStock: '',
  productDescription: '',
  productIcon: '',
  categoryType: '',
  productStatus: 0
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
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 15, message: '长度在 1 到 15 个字符', trigger: 'blur' }
  ],
  productPrice: [
    { required: true, validator: valiNumber, trigger: 'blur' }
  ],
  productStock: [
    { required: true, message: '请输入商品库存', trigger: 'blur' },
    { type: 'number', message: '库存必须为数值' }
  ],
  productDescription: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { max: 10, message: '长度不能超过10个字符', trigger: 'blur' }
  ],
  productIcon: [
    { required: true, message: '请输入商品图标链接', trigger: 'blur' }
  ],
  categoryType: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  productStatus: [
    { required: true, message: '请选择商品状态', trigger: 'change' }
  ]
}

const submitForm = (formName) => {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      // 转换数据结构，将categoryType转换为category对象
      const submitData = {
        name: ruleForm.productName,
        price: ruleForm.productPrice,
        stock: ruleForm.productStock,
        description: ruleForm.productDescription,
        icon: ruleForm.productIcon,
        status: ruleForm.productStatus,
        category: {
          categoryType: ruleForm.categoryType
        }
      }
      
      console.log('提交的商品数据:', submitData)
      axios.post(API_BASE_URL + 'product-service/seller/product/add', submitData).then(function(resp) {
        console.log('添加商品响应:', resp.data)
        if (resp.data.code == 0) {
          ElMessage.success(ruleForm.productName + '添加成功')
          router.push('/home/productManage')
        } else {
          ElMessage.error('添加失败: ' + (resp.data.message || resp.data.msg || '未知错误'))
        }
      }).catch(function(error) {
        console.error('添加商品失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      })
    }
  })
}

const resetForm = (formName) => {
  ruleFormRef.value.resetFields()
}

onMounted(() => {
  axios.get(API_BASE_URL + 'product-service/seller/product/findAllProductCategory').then(function(resp) {
    category.value = resp.data.data.content
  })
})
</script>

<style scoped>

</style>