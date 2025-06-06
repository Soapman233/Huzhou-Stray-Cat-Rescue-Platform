<template>
  <div>
    <!-- 走马灯开始 -->
    <div class="block text-center">
      <el-carousel height="500px" motion-blur>
        <el-carousel-item v-for="item in banners" :key="item.id">
          <img :src="item.imageUrl" alt="Banner" style="width: 100%; height: 100%; object-fit: cover;" />
        </el-carousel-item>
      </el-carousel>
    </div>
    <!-- 走马灯结束 -->

    <div class="adoption-section">
      <div class="content-wrapper">
        <!-- 标题 -->
        <div class="section-title">
          <span>待领养的猫猫</span>
        </div>
        <!-- 加载状态 -->
        <div v-if="cats.length === 0" class="loading">加载中...</div>
        <!-- 猫咪图片 -->
        <div v-else class="image-gallery">
          <div v-for="cat in cats" :key="cat.id" class="image-placeholder">
            <img
                v-if="cat.imageUrl"
                :src="cat.imageUrl"
                alt="Cat Image"
                class="cat-image"
                @error="cat.imageUrl = 'https://placekitten.com/150/150'"
            />
          </div>
        </div>
        <!-- 统计区域 -->
        <div class="stats-box">
          <div class="stats-item">
            <div class="stats-number">56</div>
            <div class="stats-label">待领养猫</div>
          </div>
          <div class="stats-item">
            <div class="stats-number">4915</div>
            <div class="stats-label">已领养</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// 轮播图数据（使用本地图片）
const banners = ref([
  { id: 1, imageUrl: new URL('/src/assets/banners/banner1.jpg', import.meta.url).href },
  { id: 2, imageUrl: new URL('/src/assets/banners/banner2.jpg', import.meta.url).href },
  { id: 3, imageUrl: new URL('/src/assets/banners/banner3.jpg', import.meta.url).href },
  { id: 4, imageUrl: new URL('/src/assets/banners/banner4.jpg', import.meta.url).href }
]);

// 猫咪数据（占位符，建议替换为后端接口）
const cats = ref([]);

// 模拟从后端获取数据（可替换为真实接口）
onMounted(async () => {
  try {
    // 假设后端接口为 https://your-api-endpoint/cats
    // const response = await axios.get('https://your-api-endpoint/cats');
    // cats.value = response.data.map(cat => ({
    //   id: cat.id,
    //   imageUrl: cat.imageUrl
    // }));

    // 临时使用占位符图片
    cats.value = Array(16).fill().map((_, index) => ({
      id: index,
      imageUrl: `https://placekitten.com/150/150?image=${index + 1}`
    }));
  } catch (error) {
    console.error('Failed to fetch cat data:', error);
    // 失败时使用占位符
    cats.value = Array(16).fill().map((_, index) => ({
      id: index,
      imageUrl: `https://placekitten.com/150/150?image=${index + 1}`
    }));
  }
});
</script>

<style scoped>
.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
  text-align: center;
}

.adoption-section {
  background-color: #F7F9FC;
  padding: 20px 0;
  position: relative;
  overflow: hidden;
}

.adoption-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20"><circle cx="5" cy="5" r="1" fill="#E0E7F1"/><rect x="10" y="10" width="2" height="2" fill="#E0E7F1"/></svg>');
  background-repeat: repeat;
  opacity: 0.3;
  pointer-events: none;
}

.content-wrapper {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-title {
  margin-bottom: 20px;
}

.section-title span {
  font-size: 20px;
  font-weight: 600;
  color: #1E4A6D;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
  width: 100%;
}

.image-placeholder {
  width: 150px;
  height: 150px;
  border: 2px solid #D3E2F0;
  border-radius: 8px;
  background-color: #F5F5F5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.cat-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.stats-box {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background-color: #1E4A6D;
  color: #FFF;
  padding: 20px;
  border-radius: 8px;
  width: 300px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stats-item {
  text-align: center;
}

.stats-number {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.stats-label {
  font-size: 16px;
  font-weight: 400;
}

.loading {
  font-size: 16px;
  color: #1E4A6D;
  text-align: center;
  margin: 20px 0;
}
</style>