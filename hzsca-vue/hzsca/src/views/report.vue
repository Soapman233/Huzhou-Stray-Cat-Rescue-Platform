<template>
  <div class="annual-report">
    <el-card class="report-card">
      <!-- 报告标题 -->
      <div class="report-header">
        <h1>
          <el-icon><Calendar /></el-icon>
          2023年度流浪猫领餐工作报告
        </h1>
        <p class="subtitle">湖州流浪猫救助协会 - 为流浪猫提供温暖的食物和关爱</p>
      </div>

      <!-- 关键数据统计 -->
      <div class="stats-container">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.title">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon" :style="{ color: stat.color }">
                <component :is="stat.icon" />
              </div>
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 月度数据表格 -->
      <div class="table-section">
        <h2>
          <el-icon><DataBoard /></el-icon>
          2023年度流浪猫领餐数据统计
        </h2>

        <el-table
            :data="monthlyData"
            border
            style="width: 100%"
            class="report-table"
            :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        >
          <el-table-column prop="month" label="月份" width="100" align="center" fixed />
          <el-table-column label="成功领餐" prop="success" align="center" />
          <el-table-column label="待领餐" prop="pending" align="center" />
          <el-table-column label="助餐区" prop="assistance" align="center" />
        </el-table>
      </div>

      <!-- 数据可视化图表 -->
      <div class="chart-section">
        <h2>
          <el-icon><DataAnalysis /></el-icon>
          领餐数据趋势分析
        </h2>
        <el-row :gutter="20">
          <el-col :xs="24">
            <el-card shadow="hover">
              <div ref="successChart" style="height: 300px;"></div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 总结与展望 -->
      <div class="conclusion-section">
        <el-card shadow="never" class="conclusion-card">
          <h2>
            <el-icon><Comment /></el-icon>
            年度总结与展望
          </h2>
          <p>2023年，我们共为流浪猫提供了<span class="highlight">1,284份餐食</span>，成功领餐<span class="highlight">428次</span>。</p>
          <p>感谢<span class="highlight">62名志愿者</span>的辛勤付出，以及所有爱心人士的支持。</p>
          <p>2024年，我们将扩大助餐区覆盖范围，目标是增加<span class="highlight">30%的服务能力</span>。</p>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import echarts from '../utils/echarts'
import {
  Calendar,
  DataBoard,
  DataAnalysis,
  Comment,
  Food,
  Bowl,
  User,
  Sunny
} from '@element-plus/icons-vue'

// 关键统计数据
const stats = ref([
  { icon: Food, title: '成功领餐总数', value: '428', color: '#67C23A' },
  { icon: Bowl, title: '提供餐食总数', value: '1,284', color: '#409EFF' },
  { icon: User, title: '志愿者人数', value: '62', color: '#E6A23C' },
  { icon: Sunny, title: '服务天数', value: '365', color: '#F56C6C' }
])

// 月度数据（仅猫）
const monthlyData = ref([
  { month: '1月', success: 32, pending: 2, assistance: 5 },
  { month: '2月', success: 28, pending: 3, assistance: 4 },
  { month: '3月', success: 35, pending: 1, assistance: 6 },
  { month: '4月', success: 30, pending: 2, assistance: 5 },
  { month: '5月', success: 38, pending: 1, assistance: 7 },
  { month: '6月', success: 42, pending: 0, assistance: 8 },
  { month: '7月', success: 45, pending: 0, assistance: 9 },
  { month: '8月', success: 40, pending: 1, assistance: 7 },
  { month: '9月', success: 36, pending: 2, assistance: 6 },
  { month: '10月', success: 39, pending: 1, assistance: 7 },
  { month: '11月', success: 44, pending: 0, assistance: 8 },
  { month: '12月', success: 49, pending: 0, assistance: 10 }
])

// 图表引用
const successChart = ref(null)

// 初始化图表
onMounted(() => {
  renderSuccessChart()
})

const renderSuccessChart = () => {
  const chart = echarts.init(successChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['成功领餐', '待领餐', '助餐区']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: monthlyData.value.map(item => item.month)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '成功领餐',
        type: 'bar',
        data: monthlyData.value.map(item => item.success),
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '待领餐',
        type: 'bar',
        data: monthlyData.value.map(item => item.pending),
        itemStyle: {
          color: '#E6A23C'
        }
      },
      {
        name: '助餐区',
        type: 'bar',
        data: monthlyData.value.map(item => item.assistance),
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  chart.setOption(option)

  // 响应式调整
  window.addEventListener('resize', () => {
    chart.resize()
  })
}
</script>

<style scoped>
.annual-report {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.report-card {
  border-radius: 12px;
  overflow: hidden;
}

.report-header {
  text-align: center;
  padding: 30px 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f2 100%);
  margin-bottom: 20px;
  border-radius: 8px;
}

.report-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.report-header h1 .el-icon {
  margin-right: 10px;
  font-size: 32px;
}

.subtitle {
  font-size: 16px;
  color: #606266;
}

.stats-container {
  margin: 30px 0;
}

.stat-card {
  text-align: center;
  padding: 20px 0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 36px;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 10px 0;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.table-section {
  margin: 40px 0;
}

.table-section h2 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.table-section h2 .el-icon {
  margin-right: 10px;
  color: #409EFF;
}

.report-table {
  margin-top: 20px;
}

.chart-section {
  margin: 40px 0;
}

.chart-section h2 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.chart-section h2 .el-icon {
  margin-right: 10px;
  color: #409EFF;
}

.conclusion-section {
  margin: 40px 0;
}

.conclusion-card {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
}

.conclusion-card h2 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.conclusion-card h2 .el-icon {
  margin-right: 10px;
  color: #E6A23C;
}

.conclusion-card p {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  margin-bottom: 15px;
}

.highlight {
  color: #409EFF;
  font-weight: bold;
}

@media (max-width: 768px) {
  .report-header h1 {
    font-size: 22px;
  }

  .subtitle {
    font-size: 14px;
  }

  .stat-value {
    font-size: 20px;
  }
}
</style>