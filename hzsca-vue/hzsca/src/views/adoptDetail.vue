<template>
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
        <h1><img src="/src/assets/paw.png" class="paw" alt=""> {{ cat.Name || '暂无名字' }}</h1>
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
            <strong>健康状态：</strong> {{ cat.HealthStatus || '暂无记录' }}
          </div>
          <div class="info-item">
            <strong>品种：</strong> {{ cat.Breed || '暂无记录' }}
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
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '../utils/request';
import config from '../config/config';

const route = useRoute();
const router = useRouter();
const cat = ref(null);
const loading = ref(true);
const error = ref(null);

const getImageUrl = (cat) => {
  try {
    console.log('Processing cat image in detail:', cat);
    if (!cat?.ImageURL) {
      console.log('No image URL provided, using default image');
      return new URL('/src/assets/default-cat.jpg', import.meta.url).href;
    }
    if (cat.ImageURL.startsWith('http')) {
      console.log('Using full URL:', cat.ImageURL);
      return cat.ImageURL;
    }
    const fullUrl = `${config.imageBaseURL}/${cat.ImageURL}`;
    console.log('Generated full URL:', fullUrl);
    return fullUrl;
  } catch (err) {
    console.error('Error processing image URL:', err);
    return new URL('/src/assets/default-cat.jpg', import.meta.url).href;
  }
};

const handleImageError = (e) => {
  console.log('Image load error, using default image');
  e.target.src = new URL('/src/assets/default-cat.jpg', import.meta.url).href;
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
    console.log('Fetching cat with ID:', route.params.id);
    const response = await request.get(`/api/cats/${route.params.id}`);
    console.log('API Response:', response);

    if (response.data && response.data.code === '200' && response.data.data) {
      cat.value = response.data.data;
      console.log('Final cat data:', cat.value);
    } else {
      throw new Error(response.data?.msg || '未找到猫咪信息');
    }
  } catch (err) {
    console.error('Failed to fetch cat:', err);
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
.detail-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
}

.error {
  color: #dc3545;
}

.cat-detail {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 2rem;
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-section {
  width: 100%;
}

.cat-image {
  width: 100%;
  max-width: 500px;
  height: 400px;
  border-radius: 8px;
  object-fit: cover;
  background-color: #f5f5f5;
}

.info-section {
  padding: 1rem;
}

.info-section h1 {
  margin-bottom: 1.5rem;
  color: #1E4A6D;
  font-size: 2rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.info-item {
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.description {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}

.description h2 {
  color: #1E4A6D;
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.debug-info {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #666;
}

.paw  {
  width: 25px;
  height: 25px;
  object-fit: contain;
}

@media (max-width: 768px) {
  .cat-detail {
    grid-template-columns: 1fr;
  }

  .image-section {
    text-align: center;
  }

  .cat-image {
    max-width: 100%;
  }
}
</style>