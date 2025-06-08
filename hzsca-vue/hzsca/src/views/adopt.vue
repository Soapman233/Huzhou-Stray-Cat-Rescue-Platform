<template>
  <div class="adopt-container">
    <!-- 头部开始 -->
    <div class="header-section" :style="{ height: carouselHeight }">
      <img :src="bgImage" alt="Header Background" class="header-image" />
      <span class="header-text">待领养猫猫</span>
    </div>
    <!-- 头部结束 -->

    <div class="main-content">
      <div v-loading="loading"
           element-loading-text="正在加载数据..."
           class="content-wrapper">
        <!-- 错误提示 -->
        <el-alert
            v-if="error"
            :title="error"
            type="error"
            show-icon
            :closable="false"
            class="error-alert"
        />

        <!-- 数据展示 -->
        <div v-if="!loading && !error" class="cat-grid">
          <div v-for="(cat, index) in cats" :key="index" class="cat-grid-item">
            <el-card class="cat-card" @click="goToDetail(cat.AnimalID)" shadow="hover">
              <div class="img-wrapper">
                <img :src="getImageUrl(cat.ImageURL)"
                     class="card-img"
                     alt="cat image"
                     @error="handleImageError">
              </div>
              <div class="card-content">
                <h5 class="card-title"><img src="/src/assets/paw.png" class="paw"> {{ cat.Name || '未知名字' }}</h5>
                <p class="card-desc">{{ cat.Gender || '未知' }}    {{ cat.Age || '未知' }} 岁</p>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 无数据提示 -->
        <el-empty
            v-if="!loading && !error && (!cats || cats.length === 0)"
            description="暂无可领养的猫猫"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import config from '../config/config'
import '/src/assets/AdoptView.css';

const DEFAULT_IMAGE = '/src/assets/default-cat.jpg'

export default {
  name: 'AdoptView',
  setup() {
    // 背景图片
    const bgImage = new URL('/src/assets/header-bg.jpg', import.meta.url).href

    // 响应式数据
    const cats = ref([])
    const loading = ref(false)
    const error = ref('')

    // 获取路由实例
    const router = useRouter()

    // 计算属性：动态计算轮播图高度
    const carouselHeight = computed(() => {
      const aspectRatio = 650 / 150
      const width = window.innerWidth > 1200 ? 1200 : window.innerWidth
      return `${width / aspectRatio}px`
    })

    // 获取猫咪数据
    const fetchCats = async () => {
      loading.value = true;
      error.value = '';
      try {
        const data = await request.get('/api/cats');

        if (!data) {
          error.value = '暂无可领养的猫咪';
          return;
        }

        if (Array.isArray(data)) {
          cats.value = data.filter(cat => cat && Object.keys(cat).length > 0);
          if (cats.value.length === 0) {
            error.value = '暂无可领养的猫咪';
          }
        } else {
          error.value = '数据格式错误';
        }
      } catch (err) {
        error.value = err.message || '获取数据失败，请稍后重试';
      } finally {
        loading.value = false;
      }
    }

    // 获取图片URL
    const getImageUrl = (imageUrl) => {
      try {
        console.log('图片Url : ', imageUrl);
        if (!imageUrl) {
          console.log('没有获取到相关图片Url，将使用默认图片进行替代');
          return new URL(DEFAULT_IMAGE, import.meta.url).href;
        }
        const baseUrl = config?.imageBaseURL || 'http://localhost:8080';
        // 移除可能存在的重复的 /cats/ 前缀
        const cleanImageUrl = imageUrl.replace(/^\/cats\//, '');
        const fullUrl = `${baseUrl}/cats/${cleanImageUrl}`;
        console.log('Generated image URL:', fullUrl, 'from original:', imageUrl);
        return fullUrl;
      } catch (err) {
        console.error('Error generating image URL:', err);
        return DEFAULT_IMAGE;
      }
    }
    const handleImageError = (e) => {
      e.target.src = new URL(DEFAULT_IMAGE, import.meta.url).href
    }

    // 跳转到详情页
    const goToDetail = (id) => {
      router.push(`/hzsca/adopt/${id}`);
    }
    // 在组件挂载时获取数据
    onMounted(fetchCats)
    return {
      bgImage,
      cats,
      loading,
      error,
      carouselHeight,
      handleImageError,
      goToDetail,
      getImageUrl
    }
  }
}
</script>

<style scoped>

</style>