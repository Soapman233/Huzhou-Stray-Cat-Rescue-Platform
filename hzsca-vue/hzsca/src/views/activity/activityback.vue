<template>
  <!-- 保持原有模板不变 -->
  <div class="app-container">
    <!-- 头部区域 -->
    <div class="header">
      <h1><el-icon><StarFilled /></el-icon> 湖州学院猫咪关爱活动——活动回顾</h1>
      <p>我们致力于校园流浪猫救助、绝育和领养服务，为校园猫咪创造更好的生活环境</p>
    </div>

    <!-- 添加错误提示 -->
    <div v-if="error" class="error-message">
      <el-icon><WarningFilled /></el-icon>
      <div>
        <h3>数据加载错误</h3>
        <p>{{ error }}</p>
        <p>已使用模拟数据展示页面</p>
      </div>
    </div>

    <!-- 添加加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="30"><Loading /></el-icon>
      <span style="margin-left: 10px;">加载活动数据中...</span>
    </div>

    <!-- 原有活动列表代码... -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  StarFilled,
  Picture,
  Calendar,
  Clock,
  Location,
  InfoFilled,
  ArrowRight,
  CollectionTag,
  WarningFilled,
  Loading
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(3)
const loading = ref(true)
const error = ref(null)

// 活动数据 - 从后端API获取
const activities = ref([])

// 模拟数据
const mockActivities = [
  {
    id: 1,
    title: '湖州学院猫猫救助活动',
    date: '2025-06-10 至 2025-06-15',
    location: '湖州学院东校区广场',
    imageUrl: 'catactivity1.webp',
    detail: '模拟数据：湖州学院猫猫救助活动详情',
    status: 'ended'
  },
  // 其他模拟活动...
]

// 获取已结束的活动数据
const fetchActivities = async () => {
  try {
    // 添加超时控制
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 5000)

    const response = await fetch('http://localhost:8080/api/events/ended', {
      signal: controller.signal
    })

    clearTimeout(timeoutId)

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()
    activities.value = data
  } catch (err) {
    console.error('获取活动数据时出错:', err)
    error.value = `API连接错误: ${err.message}`
    activities.value = mockActivities // 使用模拟数据
    ElMessage.error('获取活动数据失败，已使用模拟数据')
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchActivities()
})

// 其余代码保持不变...
</script>

<style scoped>
/* 新增错误提示样式 */
.error-message {
  background-color: #ffebee;
  color: #b71c1c;
  padding: 15px;
  border-radius: 8px;
  margin: 20px 0;
  display: flex;
  align-items: center;
}

.error-message .el-icon {
  margin-right: 10px;
  font-size: 24px;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 40px;
}

/* 原有样式保持不变... */
</style>