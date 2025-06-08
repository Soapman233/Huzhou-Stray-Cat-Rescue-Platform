<template>
  <div class="container">
    <h1>{{ cat.name }}</h1>
    <img :src="`${config.imageBaseURL}/cats/${cat.imageUrl}`" alt="cat image" class="img-fluid">
    <p><strong>性别：</strong> {{ cat.gender }}</p>
    <p><strong>年龄：</strong> {{ cat.age }} 岁</p>
    <p><strong>性情：</strong> {{ cat.temperament }}</p>
    <p><strong>健康状态：</strong> {{ cat.healthStatus }}</p>
    <p><strong>来源：</strong> {{ cat.source }}</p>
    <p><strong>已植入芯片：</strong> {{ cat.microchipped ? '是' : '否' }}</p>
    <p><strong>已绝育：</strong> {{ cat.sterilized ? '是' : '否' }}</p>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue';
import {useRoute} from 'vue-router';
import request from '../utils/request';
import config from '../config/config';

const route = useRoute();
const cat = ref({});

export default {
  setup() {
    const fetchCat = async () => {
      const id = route.params.id;
      const response = await request.get(`/api/cats/${id}`);
      cat.value = response.data;
    };

    onMounted(() => {
      fetchCat();
    });

    return {
      cat,
      config
    };
  }
}
</script>

<style>
img {
  max-width: 300px;
  height: auto;
  margin-bottom: 20px;
}
</style>