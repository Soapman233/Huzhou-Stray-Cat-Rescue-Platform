<template>
  <!-- 头部开始 -->
  <div class="header-section" :style="{ height: carouselHeight }">
    <img :src="bgImage" alt="Header Background" class="header-image" />
    <span class="header-text">猫猫资料</span>
  </div>
  <!-- 头部结束 -->
  <div class="detail-container" v-if="cat">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="cat-detail">
      <div class="image-section">
        <img :src="getImageUrl(cat)" :alt="cat.Name || '猫咪图片'" @error="handleImageError" class="cat-image">
        <div class="debug-info" style="display: none;">
          <p>Image URL: {{ getImageUrl(cat) }}</p>
          <p>Base URL: {{ config.baseUrl }}</p>
          <p>Image Path: {{ cat.ImageURL }}</p>
        </div>
      </div>
      <div class="info-section">
        <h1><img src="/src/assets/paw.png" class="paw"> {{ cat.Name || '暂无名字' }}</h1>
        <div class="info-grid">
          <div class="info-item">
            <strong>编号：</strong> {{ cat.AnimalID }}
          </div>
          <div class="info-item">
            <strong>性别：</strong> {{ cat.Gender || '未知' }}
          </div>
          <div class="info-item">
            <strong>年龄：</strong> {{ cat.Age ? `${cat.Age} 岁` : '未知' }}
          </div>
          <div class="info-item">
            <strong>品种：</strong> {{ cat.Breed || '未知' }}
          </div>
          <div class="info-item">
            <strong>健康状态：</strong> {{ cat.HealthStatus || '暂无记录' }}
          </div>
          <div class="info-item">
            <strong>来源：</strong> {{ cat.Source || '未知' }}
          </div>
        </div>
        <div class="description" v-if="cat.Description">
          <h2><img src="/src/assets/paw.png" class="paw"> 详细描述</h2>
          <p>{{ cat.Description }}</p>
        </div>
      </div>
    </div>

    <div style="padding-top: 20px">
      <el-button type="primary" plain @click="goBack">返 回</el-button>
      <el-button type="primary" plain @click="goToApply">申 请 领 养</el-button>
    </div>


  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '../utils/request';
import config from '../config/config';
import '/src/assets/AdoptDetail.css';

const route = useRoute();
const router = useRouter();
const cat = ref(null);
const loading = ref(true);
const error = ref(null);

// 背景图片
const bgImage = new URL('/src/assets/animals-cat-banner.jpg', import.meta.url).href
// 计算属性：动态计算轮播图高度
const carouselHeight = computed(() => {
  const aspectRatio = 650 / 150
  const width = window.innerWidth > 1200 ? 1200 : window.innerWidth
  return `${width / aspectRatio}px`
})

    const getImageUrl = (cat) => {
      if (!cat?.ImageURL) {
        return '/src/assets/default-cat.jpg';
      }
      if (cat.ImageURL.startsWith('http')) {
        return cat.ImageURL;
      }
      const fullUrl = `${config.baseURL}${cat.ImageURL}`;
      return fullUrl;
    };
    const handleImageError = (e) => {
      e.target.src = '/src/assets/default-cat.jpg';
    };

    const fetchCat = async () => {
      if (!route.params.id) {
        error.value = '未找到猫咪ID';
        loading.value = false;
        return;
      }

      loading.value = true;
      error.value = null;

      try {
        const response = await request.get(`/api/cats/${route.params.id}`);
        if (!response) {
          throw new Error('未找到猫咪信息');
        }
        if (!response.AnimalID) {
          throw new Error('返回的数据格式不正确');
        }

        cat.value = response;
      } catch (err) {
        error.value = err.message || '获取猫咪信息失败，请稍后重试';
        cat.value = null;
      } finally {
        loading.value = false;
      }
    };

    const goBack = () => {
      router.push('/hzsca/adopt');
    };

    const goToApply = () => {
      router.push(`/hzsca/adopt/apply/${route.params.id}`);
    };

    onMounted(fetchCat);

</script>

<style scoped>

</style>