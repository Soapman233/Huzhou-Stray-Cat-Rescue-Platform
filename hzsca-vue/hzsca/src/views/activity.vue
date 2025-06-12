<template>
  <div class="app-container">
    <!-- 头部区域 -->
    <div class="header">
      <h1><el-icon><StarFilled /></el-icon> 湖州学院猫咪关爱活动——最新活动</h1>
      <p>我们致力于校园流浪猫救助、绝育和领养服务，为校园猫咪创造更好的生活环境</p>
    </div>

    <!-- 活动列表 -->
    <div class="activity-container">
      <!-- 活动卡片 -->
      <div
          v-for="activity in activities"
          :key="activity.id"
          class="activity-card-wrapper"
          @click="goToDetail(activity.id)"
      >
        <div class="image-section">
          <img :src="getImageUrl(activity.imageUrl)" :alt="activity.title">
        </div>

        <div class="content-section">
          <div>
            <h3 class="card-title">
              <el-icon>
                <Calendar/>
              </el-icon>
              {{ activity.title }}
              <span v-if="activity.status === 'ongoing'" class="badge badge-new">正在进行</span>
              <span v-if="activity.status === 'upcoming'" class="badge badge-upcoming">即将开始</span>
            </h3>

            <div class="info-item date">
              <el-icon>
                <Clock/>
              </el-icon>
              <div class="info-text">时间：{{ activity.date }}</div>
            </div>

            <div class="info-item location">
              <el-icon>
                <Location/>
              </el-icon>
              <div class="info-text">地点：{{ activity.location }}</div>
            </div>

            <div class="detail-text">
              <el-icon>
                <InfoFilled/>
              </el-icon>
              {{ activity.detail }}
            </div>
          </div>

          <div class="footer">
            <div class="cat-id">活动编号: {{ activity.id }}</div>
            <button class="view-btn" @click.stop="goToDetail(activity.id)">
              查看详情
              <el-icon>
                <ArrowRight/>
              </el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="activities.length === 0" description="暂无活动信息">
        <template #image>
          <el-icon :size="60">
            <Picture/>
          </el-icon>
        </template>
        <p>当前没有即将举行的猫咪关爱活动，请稍后再来查看</p>
      </el-empty>
    </div>

    <!-- 活动详情弹窗 -->
    <el-dialog
        v-model="detailVisible"
        :title="currentActivity?.title"
        width="70%"
        top="5vh"
    >
      <div class="detail-content" v-if="currentActivity">
        <div class="image-section">
          <img :src="getImageUrl(currentActivity.imageUrl)" :alt="currentActivity.title" class="main-image">
        </div>

        <div class="info-section">
          <div class="info-row">
            <el-icon>
              <Clock/>
            </el-icon>
            <span class="label">活动时间：</span>
            <span>{{ currentActivity.date }}</span>
          </div>

          <div class="info-row">
            <el-icon>
              <Location/>
            </el-icon>
            <span class="label">活动地点：</span>
            <span>{{ currentActivity.location }}</span>
          </div>

          <div class="info-row">
            <el-icon>
              <CollectionTag/>
            </el-icon>
            <span class="label">活动状态：</span>
            <el-tag :type="currentActivity.status === 'ongoing' ? 'danger' : 'success'">
              {{ currentActivity.status === 'ongoing' ? '正在进行' : '即将开始' }}
            </el-tag>
          </div>

          <div class="detail-text">
            <h3>
              <el-icon>
                <InfoFilled/>
              </el-icon>
              活动详情
            </h3>
            <p>{{ currentActivity.detail }}</p>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref} from 'vue'

// 活动数据
const activities = ref([
  {
    id: 1,
    title: '湖州学院猫猫救助活动',
    date: '2025-06-10 至 2025-06-15',
    location: '湖州学院东校区广场',
    imageUrl: 'catactivity1.webp',
    detail: '湖州学院将于2025年6月10日举办猫猫救助活动。活动内容包括为流浪猫提供食物、医疗援助和领养机会。旨在提高公众对流浪猫的关注，并为它们提供温暖的家。欢迎志愿者和领养者参与，共同为流浪猫创造更好的生活条件。',
    status: 'ongoing'
  },
  {
    id: 2,
    title: '流浪猫TNR计划',
    date: '2025-06-20 09:00-17:00',
    location: '湖州学院动物保护中心',
    imageUrl: 'catactivity2.webp',
    detail: '2025年6月20日，湖州学院将开展流浪猫TNR项目，即捕捉、绝育、放归。通过科学方法控制流浪猫数量，减少其对生态环境的影响。活动需要志愿者协助，共同为流浪猫的未来努力。',
    status: 'upcoming'
  },
  {
    id: 3,
    title: '猫咪健康检查与疫苗接种',
    date: '2025-06-25 13:00-16:00',
    location: '湖州学院动物医疗中心',
    imageUrl: 'catactivity3.webp',
    detail: '湖州学院于2025年6月25日组织猫咪健康检查与疫苗接种活动，为流浪猫提供全面健康检查和必要的疫苗接种服务。活动旨在预防疾病，保障猫咪健康，欢迎志愿者和动物保护组织参与。',
    status: 'upcoming'
  },
])

// 详情弹窗控制
const detailVisible = ref(false)
const currentActivity = ref(null)

// 获取图片URL
const getImageUrl = (name) => {
  return new URL(`./assets/images/${name}`, import.meta.url).href
}

// 查看详情
const goToDetail = (id) => {
  currentActivity.value = activities.value.find(item => item.id === id)
  detailVisible.value = true
}
</script>

<style scoped>
.app-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.header h1 {
  color: #5d4037;
  font-size: 32px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header h1 .el-icon {
  margin-right: 10px;
  color: #ff9800;
}

.header p {
  color: #78909c;
  font-size: 16px;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.activity-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.activity-card-wrapper {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  display: flex;
  height: 220px;
  cursor: pointer;
}

.activity-card-wrapper:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.image-section {
  width: 35%;
  overflow: hidden;
}

.image-section img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.activity-card-wrapper:hover .image-section img {
  transform: scale(1.05);
}

.content-section {
  width: 65%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #5d4037;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.card-title .el-icon {
  margin-right: 10px;
  color: #ff9800;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  color: #546e7a;
}

.info-item .el-icon {
  min-width: 24px;
  color: #78909c;
  margin-right: 10px;
  margin-top: 2px;
}

.info-text {
  flex: 1;
  font-size: 14px;
}

.info-item.date {
  color: #e91e63;
}

.info-item.location {
  color: #3f51b5;
}

.detail-text {
  background: #f5f5f5;
  padding: 12px 15px;
  border-radius: 10px;
  font-size: 13px;
  color: #607d8b;
  line-height: 1.6;
  margin-top: 10px;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.badge {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-left: 10px;
}

.badge-new {
  background: #ff5252;
  color: white;
}

.badge-upcoming {
  background: #4caf50;
  color: white;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.cat-id {
  font-size: 12px;
  color: #78909c;
  background: #f5f7fa;
  padding: 3px 10px;
  border-radius: 4px;
}

.view-btn {
  background: #ff9800;
  color: white;
  border: none;
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}

.view-btn:hover {
  background: #f57c00;
  transform: translateY(-2px);
}

.view-btn .el-icon {
  margin-left: 5px;
  font-size: 12px;
}

.el-empty p {
  color: #90a4ae;
  margin-top: 10px;
}

/* 详情弹窗样式 */
.detail-content {
  padding: 20px;
}

.main-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 12px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
}

.info-row .el-icon {
  margin-right: 10px;
  color: #78909c;
}

.label {
  font-weight: 600;
  color: #5d4037;
  margin-right: 10px;
  min-width: 80px;
}

.detail-text {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.detail-text h3 {
  color: #5d4037;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.detail-text h3 .el-icon {
  margin-right: 10px;
  color: #ff9800;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .activity-card-wrapper {
    flex-direction: column;
    height: auto;
  }

  .image-section, .content-section {
    width: 100%;
  }

  .image-section {
    height: 200px;
  }
}
</style>