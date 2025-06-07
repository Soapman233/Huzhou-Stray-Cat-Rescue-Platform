<template>
  <div class="container">
    <h1>{{ cat.name }}</h1>
    <img :src="cat.imageUrl" alt="cat image" class="img-fluid">
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
import axios from 'axios';

export default {
  data() {
    return {
      cat: {}
    }
  },
  mounted() {
    this.fetchCat();
  },
  methods: {
    fetchCat() {
      const id = this.$route.params.id;
      axios.get(`/cats/${id}`).then(response => {
        this.cat = response.data;
      });
    }
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