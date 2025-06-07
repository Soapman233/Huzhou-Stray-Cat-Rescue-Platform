<template>
  <!-- 头部开始 -->
  <div class="header-section" :style="{ height: carouselHeight }">
    <img :src="bgImage" alt="Header Background" class="header-image" />
    <span class="header-text">待领养猫猫</span>
  </div>
  <!-- 头部结束 -->

  <div>
    <div class="row">
      <div v-for="cat in cats" :key="cat.id" class="col-md-3 cat-card" @click="goToDetail(cat.id)">
        <img :src="cat.imageUrl" alt="cat image" class="img-fluid">
        <p>{{ cat.name }}</p>
        <p>{{ cat.gender }} - {{ cat.age }} 岁</p>
      </div>
    </div>
  </div>

</template>

<script setup>
import { computed } from 'vue';
import axios from 'axios';
import '/src/assets/Adopt.css';
const bgImage = new URL('/src/assets/header-bg.jpg', import.meta.url).href;

const carouselHeight = computed(() => {
  const aspectRatio = 650 / 150;
  const width = window.innerWidth > 1200 ? 1200 : window.innerWidth;
  return `${width / aspectRatio}px`;
});
window.addEventListener('resize', () => {
  carouselHeight.value;
});

export default {
  data() {
    return {
      cats: []
    }
  },
  mounted() {
    this.fetchCats();
  },
  methods: {
    fetchCats() {
      axios.get('/cats').then(response => {
        this.cats = response.data;
      });
    },
    goToDetail(id) {
      this.$router.push(`/cats/${id}`);
    }
  }
}
</script>

<style scoped>

</style>