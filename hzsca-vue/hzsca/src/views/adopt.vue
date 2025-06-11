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
const DEFAULT_IMAGE = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4='

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
        const response = await request.get('/api/cats');
        console.log('API Response:', response);

        const result = response.data; // 获取 Result 对象
        if (!result || result.code !== "200") {
          throw new Error(result?.msg || '请求失败');
        }

        // 从 Result 对象中提取 data 数组
        const catData = result.data;
        console.log('Processed data:', catData);

        if (!Array.isArray(catData)) {
          console.error('Invalid data format:', catData);
          throw new Error('数据格式错误');
        }

        if (catData.length === 0) {
          error.value = '暂无可领养的猫咪';
          cats.value = [];
          return;
        }

        // 检查第一个猫咪对象的属性名
        if (catData.length > 0) {
          console.log('First cat object properties:', Object.keys(catData[0]));
          console.log('First cat ImageURL value:', catData[0].ImageURL);
          console.log('First cat raw object:', catData[0]);
        }

        cats.value = catData.filter(cat => cat && Object.keys(cat).length > 0);
        console.log('Final processed cats:', cats.value);

        if (cats.value.length === 0) {
          error.value = '暂无可领养的猫咪';
        }
      } catch (err) {
        console.error('Failed to fetch cats:', err);
        error.value = err.message || '获取数据失败，请稍后重试';
        cats.value = [];
      } finally {
        loading.value = false;
      }
    }

    // 获取图片URL
    const getImageUrl = (imageUrl) => {
      try {
        console.log('Processing image URL:', imageUrl);

        if (!imageUrl) {
          console.log('No image URL provided, using default image');
          return new URL(DEFAULT_IMAGE, import.meta.url).href;
        }

        // 检查是否是完整的 URL
        if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
          console.log('Using full URL:', imageUrl);
          return imageUrl;
        }

        // 检查是否是 Base64 数据
        if (imageUrl.startsWith('data:image')) {
          console.log('Using Base64 image data');
          return imageUrl;
        }

        // 处理相对路径
        const baseUrl = config?.imageBaseURL || 'http://localhost:8080';
        // 确保路径格式正确
        const cleanImageUrl = imageUrl.startsWith('/') ? imageUrl.substring(1) : imageUrl;
        const fullUrl = `${baseUrl}/${cleanImageUrl}`;
        console.log('Generated full URL:', fullUrl);
        return fullUrl;
      } catch (err) {
        console.error('Error processing image URL:', err);
        return new URL(DEFAULT_IMAGE, import.meta.url).href;
      }
    }

    // 处理图片加载错误
    const handleImageError = (e) => {
      console.log('Image load error, using default image');
      e.target.src = new URL(DEFAULT_IMAGE, import.meta.url).href;
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