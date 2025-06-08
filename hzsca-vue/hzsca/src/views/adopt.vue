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
                <h5 class="card-title">{{ cat.Name || '未知名字' }}</h5>
                <p class="card-desc">{{ cat.Gender || '未知' }} - {{ cat.Age || '未知' }} 岁</p>
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

// 默认图片（使用本地图片）
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
        console.log('Fetched cats data (raw):', JSON.stringify(data, null, 2));

        if (!data) {
          error.value = '暂无可领养的猫咪';
          return;
        }

        if (Array.isArray(data)) {
          // 检查第一个猫咪对象的属性名
          if (data.length > 0) {
            console.log('First cat object properties:', Object.keys(data[0]));
            console.log('First cat ImageURL value:', data[0].ImageURL);
            console.log('First cat raw object:', data[0]);
          }

          cats.value = data.filter(cat => cat && Object.keys(cat).length > 0);
          console.log('Processed cats data (detailed):', JSON.stringify(cats.value, null, 2));
          if (cats.value.length === 0) {
            error.value = '暂无可领养的猫咪';
          }
        } else {
          error.value = '数据格式错误';
        }
      } catch (err) {
        console.error('Failed to fetch cats:', err);
        error.value = err.message || '获取数据失败，请稍后重试';
      } finally {
        loading.value = false;
      }
    }

    // 获取图片URL
    const getImageUrl = (imageUrl) => {
      try {
        console.log('getImageUrl called with:', imageUrl);
        if (!imageUrl) {
          console.log('No image URL provided for cat, using default image');
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

    // 处理图片加载错误
    const handleImageError = (e) => {
      e.target.src = new URL(DEFAULT_IMAGE, import.meta.url).href
    }

    // 跳转到详情页
    const goToDetail = (id) => {
      console.log('Attempting to navigate to cat details with ID:', id);
      if (!id) {
        console.warn('Invalid cat ID:', id);
        return;
      }
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
.adopt-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 1rem;
}

.content-wrapper {
  max-width: 1500px;
  margin: 0 auto;
  padding: 1rem;
}

.error-alert {
  margin-bottom: 1rem;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 1.25rem;
  width: 100%;
}

.cat-grid-item {
  width: 100%;
}

.cat-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
  height: 100%;
}

.cat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.img-wrapper {
  position: relative;
  padding-top: 100%;
  overflow: hidden;
  background-color: #f8f9fa;
}

.card-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  padding: 0.75rem;
}

.card-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-desc {
  margin: 0.25rem 0 0;
  font-size: 0.875rem;
  color: #666;
}

.header-section {
  position: relative;
  width: 100%;
  overflow: hidden;
}

.header-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 2.5rem;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  z-index: 1;
}

/* 响应式布局 */
@media (max-width: 1400px) {
  .cat-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 1200px) {
  .cat-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .cat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1rem;
  }

  .content-wrapper {
    padding: 0.75rem;
  }

  .header-text {
    font-size: 2rem;
  }

  .card-title {
    font-size: 0.9rem;
  }

  .card-desc {
    font-size: 0.8rem;
  }
}

@media (max-width: 576px) {
  .cat-grid {
    grid-template-columns: minmax(0, 1fr);
  }

  .main-content {
    padding: 0.5rem;
  }

  .content-wrapper {
    padding: 0.5rem;
  }
}
</style>