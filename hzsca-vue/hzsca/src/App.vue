<template>
  <RouterView />
</template>
<script setup>
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