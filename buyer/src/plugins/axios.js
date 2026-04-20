import axios from "axios";

// 创建 axios 实例
const _axios = axios.create({
  // 基础URL配置
  baseURL: import.meta.env.VITE_API_BASE_URL || "",
  // 超时时间
  timeout: 10000,
  // 跨域请求时是否携带凭证
  withCredentials: true,
  // 默认请求头
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
_axios.interceptors.request.use(
  function(config) {
    // 从本地存储获取 token
    const token = localStorage.getItem('token');
    if (token) {
      // 添加认证头
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  function(error) {
    // 处理请求错误
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
_axios.interceptors.response.use(
  function(response) {
    // 处理响应数据
    return response.data;
  },
  function(error) {
    // 处理响应错误
    console.error('响应错误:', error);
    
    // 处理不同类型的错误
    if (error.response) {
      // 服务器返回错误状态码
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          console.error('未授权，请重新登录');
          // 这里可以添加跳转到登录页的逻辑
          break;
        case 403:
          // 禁止访问
          console.error('禁止访问');
          break;
        case 404:
          // 资源不存在
          console.error('请求的资源不存在');
          break;
        case 500:
          // 服务器错误
          console.error('服务器内部错误');
          break;
        default:
          console.error(`请求失败: ${error.response.status}`);
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      console.error('网络错误，请检查网络连接');
    } else {
      // 请求配置错误
      console.error('请求配置错误:', error.message);
    }
    
    return Promise.reject(error);
  }
);

// Vue 3 插件
const axiosPlugin = {
  install(app) {
    // 全局属性
    app.config.globalProperties.$axios = _axios;
    // 全局可访问
    app.provide('axios', _axios);
    // 挂载到 window 上（可选）
    window.axios = _axios;
  }
};

export default axios;