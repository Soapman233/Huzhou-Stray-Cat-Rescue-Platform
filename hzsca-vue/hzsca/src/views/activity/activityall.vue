<template>
  <div class="app-container">
    <!-- 头部区域 -->
    <div class="header">
      <h1><el-icon><StarFilled /></el-icon> 湖州学院猫咪关爱活动——所有活动</h1>
      <p>我们致力于校园流浪猫救助、绝育和领养服务，为校园猫咪创造更好的生活环境</p>
    </div>

    <!-- 活动列表 -->
    <div class="activity-container">
      <!-- 活动卡片 - 只显示当前页的活动 -->
      <div
          v-for="activity in paginatedActivities"
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
              <el-icon><Calendar /></el-icon>
              {{ activity.title }}
              <span v-if="activity.status === 'ongoing'" class="badge badge-new">正在进行</span>
              <span v-if="activity.status === 'upcoming'" class="badge badge-upcoming">即将开始</span>
              <span v-if="activity.status === 'ended'" class="badge badge-ended">已结束</span>
            </h3>

            <div class="info-item date">
              <el-icon><Clock /></el-icon>
              <div class="info-text">时间：{{ activity.date }}</div>
            </div>

            <div class="info-item location">
              <el-icon><Location /></el-icon>
              <div class="info-text">地点：{{ activity.location }}</div>
            </div>

            <div class="detail-text">
              <el-icon><InfoFilled /></el-icon> {{ activity.detail }}
            </div>
          </div>

          <div class="footer">
            <div class="cat-id">活动编号: {{ activity.id }}</div>
            <button class="view-btn" @click.stop="goToDetail(activity.id)">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="activities.length === 0" description="暂无活动信息">
        <template #image>
          <el-icon :size="60"><Picture /></el-icon>
        </template>
        <p>当前没有活动记录</p>
      </el-empty>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5]"
          :small="false"
          :disabled="false"
          :background="true"
          layout="total, prev, pager, next, jumper"
          :total="activities.length"
      />
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
            <el-icon><Clock /></el-icon>
            <span class="label">活动时间：</span>
            <span>{{ currentActivity.date }}</span>
          </div>

          <div class="info-row">
            <el-icon><Location /></el-icon>
            <span class="label">活动地点：</span>
            <span>{{ currentActivity.location }}</span>
          </div>

          <div class="info-row">
            <el-icon><CollectionTag /></el-icon>
            <span class="label">活动状态：</span>
            <el-tag :type="getStatusTagType(currentActivity.status)">
              {{ getStatusText(currentActivity.status) }}
            </el-tag>
          </div>

          <div class="detail-text">
            <h3><el-icon><InfoFilled /></el-icon> 活动详情</h3>
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
import { ref, computed } from 'vue'
import {
  StarFilled,
  Picture,
  Calendar,
  Clock,
  Location,
  InfoFilled,
  ArrowRight,
  CollectionTag
} from '@element-plus/icons-vue'

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(5)

// 活动数据 - 从后端API获取
const activities = ref([])

// 获取活动数据
const fetchActivities = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/events')
    if (!response.ok) {
      throw new Error('获取活动数据失败')
    }
    const data = await response.json()
    activities.value = data
  } catch (error) {
    console.error('获取活动数据时出错:', error)
    ElMessage.error('获取活动数据失败，请稍后重试')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchActivities()
})

// 计算当前页显示的活动
const paginatedActivities = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return activities.value.slice(start, end)
})

// 详情弹窗控制
const detailVisible = ref(false)
const currentActivity = ref(null)

// 获取图片URL
const getImageUrl = (name) => {
  return new URL(`./assets/images/${name}`, import.meta.url).href
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch(status) {
    case 'ongoing': return 'danger'
    case 'upcoming': return 'success'
    case 'ended': return 'info'
    default: return ''
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch(status) {
    case 'ongoing': return '正在进行'
    case 'upcoming': return '即将开始'
    case 'ended': return '已结束'
    default: return ''
  }
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
  min-height: 1200px; /* 保持高度一致，避免分页跳动 */
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

.badge-ended {
  background: #9e9e9e;
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

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
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

  .activity-container {
    min-height: auto;
  }
}
</style>