<template>
  <RouterView />
</template>
<script setup>
// 1. 添加axios配置
import axios from "axios";

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
});

// 2. 统一错误处理
request.interceptors.response.use(
    response => response.data,
    error => {
      // 错误提示
      return Promise.reject(error);
    }
);
</script>