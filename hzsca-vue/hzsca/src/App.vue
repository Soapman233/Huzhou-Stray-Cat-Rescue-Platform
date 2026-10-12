<template>
  <!-- 按需引入后不再全局 app.use(ElementPlus)，中文语言包通过 ConfigProvider 指定 -->
  <el-config-provider :locale="zhCn">
    <RouterView />
  </el-config-provider>
</template>
<script setup>
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import axios from "axios";

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
});

request.interceptors.response.use(
    response => response.data,
    error => {
      return Promise.reject(error);
    }
);
</script>
