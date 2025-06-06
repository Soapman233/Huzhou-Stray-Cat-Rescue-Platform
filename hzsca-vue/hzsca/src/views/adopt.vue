<template>
  <!-- 头部开始 -->
  <div class="header-section" :style="{ height: carouselHeight }">
    <img :src="bgImage" alt="Header Background" class="header-image" />
    <span class="header-text">待领养猫猫</span>
  </div>
  <!-- 头部结束 -->

  <div>

  </div>

</template>

<script setup>
import { computed } from 'vue';

const bgImage = new URL('/src/assets/header-bg.jpg', import.meta.url).href;

const carouselHeight = computed(() => {
  const aspectRatio = 650 / 150;
  const width = window.innerWidth > 1200 ? 1200 : window.innerWidth;
  return `${width / aspectRatio}px`;
});
window.addEventListener('resize', () => {
  carouselHeight.value;
});
</script>

<style scoped>
.header-section {
  position: relative;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.header-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.6;
  z-index: 0;
}

.header-text {
  font-size: 75px;
  font-weight: 700;
  color: #235f91;
  text-align: center;
  z-index: 1;
}
</style>