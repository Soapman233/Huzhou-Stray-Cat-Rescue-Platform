<template>
  <div class="dashboard">
    <div class="hero">
      <div class="hero-title">管理员首页</div>
      <div class="hero-sub">领养与救助数据概览</div>
    </div>

    <el-row :gutter="12" class="cards">
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">领养申请总数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value">{{ stats.approved }}</div>
          <div class="stat-label">已通过</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value">{{ stats.rejected }}</div>
          <div class="stat-label">已拒绝</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-title">领养申请状态分布</div>
          <div ref="statusChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div class="chart-title">领养申请月度趋势</div>
          <div ref="monthChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import echarts from '../../utils/echarts'
import request from '@/utils/request'

const statusChartRef = ref(null)
const monthChartRef = ref(null)
let statusChart = null
let monthChart = null

const stats = ref({
  total: 0,
  pending: 0,
  approved: 0,
  rejected: 0
})

const normalizeStatus = (s) => {
  const v = (s || '').toString().toUpperCase()
  if (!v) return 'UNKNOWN'
  return v
}

const statusText = (code) => {
  const v = (code || '').toString().toUpperCase()
  if (v === 'PENDING') return '待审核'
  if (v === 'APPROVED') return '已通过'
  if (v === 'REJECTED') return '已拒绝'
  if (v === 'CANCELLED' || v === 'CANCELED') return '已取消'
  if (v === 'UNKNOWN') return '未知'
  return '其他'
}

const ym = (ts) => {
  if (!ts) return '未知'
  const d = new Date(ts)
  if (Number.isNaN(d.getTime())) return '未知'
  const y = d.getFullYear()
  const m = `${d.getMonth() + 1}`.padStart(2, '0')
  return `${y}-${m}`
}

const build = (records) => {
  const statusCounts = new Map()
  const monthCounts = new Map()
  for (const r of records) {
    const st = normalizeStatus(r.requestStatus || r.status)
    statusCounts.set(st, (statusCounts.get(st) || 0) + 1)
    const key = ym(r.applyTime)
    monthCounts.set(key, (monthCounts.get(key) || 0) + 1)
  }

  const total = records.length
  stats.value = {
    total,
    pending: statusCounts.get('PENDING') || 0,
    approved: statusCounts.get('APPROVED') || 0,
    rejected: statusCounts.get('REJECTED') || 0
  }

  const statusData = []
  const known = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'CANCELED', 'UNKNOWN']
  for (const k of known) {
    const v = statusCounts.get(k) || 0
    if (v > 0) statusData.push({ name: statusText(k), value: v })
  }
  let other = 0
  for (const [k, v] of statusCounts.entries()) {
    if (!known.includes(k)) {
      other += v || 0
    }
  }
  if (other > 0) statusData.push({ name: '其他', value: other })

  const monthKeys = Array.from(monthCounts.keys()).sort()
  const monthSeries = monthKeys.map(k => monthCounts.get(k) || 0)

  if (statusChart) {
    statusChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [
        {
          type: 'pie',
          radius: ['35%', '70%'],
          center: ['50%', '45%'],
          label: { formatter: '{b}: {c}' },
          data: statusData
        }
      ]
    })
  }

  if (monthChart) {
    monthChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '3%', top: 20, bottom: 30, containLabel: true },
      xAxis: { type: 'category', data: monthKeys },
      yAxis: { type: 'value' },
      series: [
        {
          type: 'bar',
          data: monthSeries,
          itemStyle: { color: '#111827' }
        }
      ]
    })
  }
}

const fetchData = async () => {
  try {
    const res = await request.get('/api/admin/dataset/adoptions', {
      params: { page: 1, size: 1000 }
    })
    if (res.data?.code !== '200') {
      ElMessage.error(res.data?.msg || '加载数据失败')
      build([])
      return
    }
    const records = res.data?.data?.records || []
    build(Array.isArray(records) ? records : [])
  } catch (e) {
    ElMessage.error('加载数据失败')
    build([])
  }
}

const onResize = () => {
  if (statusChart) statusChart.resize()
  if (monthChart) monthChart.resize()
}

onMounted(async () => {
  statusChart = echarts.init(statusChartRef.value)
  monthChart = echarts.init(monthChartRef.value)
  window.addEventListener('resize', onResize)
  await fetchData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  if (statusChart) statusChart.dispose()
  if (monthChart) monthChart.dispose()
})
</script>

<style scoped>
.hero {
  padding: 14px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #E6DCC8, #f5f7fa);
  margin-bottom: 12px;
}

.hero-title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}

.hero-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #4b5563;
}

.cards {
  margin-bottom: 12px;
}

.stat-card {
  border-radius: 12px;
}

.stat-value {
  font-size: 22px;
  font-weight: 900;
  color: #111827;
}

.stat-label {
  margin-top: 6px;
  color: #6b7280;
  font-size: 12px;
}

.chart-card {
  border-radius: 12px;
  margin-bottom: 12px;
}

.chart-title {
  font-weight: 800;
  color: #111827;
  margin-bottom: 10px;
}

.chart {
  height: 320px;
}
</style>
