<template>
  <div>
    <!--走马灯开始-->
    <div class="block text-center">
      <el-carousel height="200px" motion-blur>
        <el-carousel-item v-for="item in 4" :key="item">
          <h3 class="small justify-center" text="2xl">{{ item }}</h3>
        </el-carousel-item>
      </el-carousel>
    </div>
    <!--走马灯结束-->

    <div class="adoption-section">
      <div class="content-wrapper">
        <!-- 标题 -->
        <div class="section-title">
          <span>待领养的猫猫</span>
        </div>
        <!-- 猫咪图片 -->
        <div class="image-gallery">
          <div v-for="cat in cats" :key="cat.id" class="image-placeholder">
            <img v-if="cat.imageUrl" :src="cat.imageUrl" alt="Cat Image" class="cat-image" />
          </div>
        </div>
        <!-- 统计区域 -->
        <div class="stats-box">
          <div class="stats-number">56</div>
          <div class="stats-label">待领养猫</div>
          <div class="stats-number">4915</div>
          <div class="stats-label">已领养</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 模拟猫咪数据（后端接口应替换此部分）
const cats = ref(Array(16).fill().map((_, index) => ({
  id: index,
  imageUrl: '' // 后端应提供图片URL
})));
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
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 30px;
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
  background-color: #1E4A6D;
  color: #FFF;
  padding: 30px 40px;
  border-radius: 8px;
  text-align: center;
  width: 300px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stats-number {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
}

.stats-label {
  font-size: 18px;
  font-weight: 400;
  margin-bottom: 20px;
}
</style>