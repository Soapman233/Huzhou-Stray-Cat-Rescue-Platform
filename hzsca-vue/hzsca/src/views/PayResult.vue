<template>
  <div class="result-container">
    <div class="result-card">
      <!-- 加载中 -->
      <template v-if="loading && !order">
        <el-icon class="status-icon is-loading"><Loading /></el-icon>
        <h1 class="status-title">正在查询订单…</h1>
        <p class="status-desc">请稍候，正在获取支付结果</p>
      </template>

      <!-- 支付成功 -->
      <template v-else-if="isSuccess">
        <el-icon class="status-icon success"><CircleCheckFilled /></el-icon>
        <h1 class="status-title">捐助成功，感谢您的爱心 🐾</h1>
        <p class="status-desc">您的善意将为流浪猫带来实实在在的帮助</p>
      </template>

      <!-- 已关闭 -->
      <template v-else-if="isClosed">
        <el-icon class="status-icon closed"><CircleCloseFilled /></el-icon>
        <h1 class="status-title">订单已关闭</h1>
        <p class="status-desc">该订单已关闭或超时，您可以重新发起捐助</p>
      </template>

      <!-- 处理中 -->
      <template v-else>
        <el-icon class="status-icon processing"><Clock /></el-icon>
        <h1 class="status-title">支付处理中</h1>
        <p class="status-desc">
          {{ timedOut ? '尚未收到支付结果，请稍后手动刷新' : '正在等待支付结果，请勿关闭页面…' }}
        </p>
      </template>

      <!-- 订单信息 -->
      <div v-if="order" class="order-detail">
        <div class="detail-row">
          <span>订单号</span>
          <span class="mono">{{ order.outTradeNo }}</span>
        </div>
        <div class="detail-row">
          <span>捐助金额</span>
          <strong class="amount">¥ {{ ((order.amount || 0) / 100).toFixed(2) }}</strong>
        </div>
        <div class="detail-row">
          <span>订单状态</span>
          <el-tag :type="statusTagType" effect="plain">{{ statusText }}</el-tag>
        </div>
        <div class="detail-row">
          <span>支付渠道</span>
          <span>{{ order.channel === 'MOCK' ? '模拟支付（演示）' : '支付宝' }}</span>
        </div>
        <div v-if="order.remark" class="detail-row">
          <span>留言</span>
          <span>{{ order.remark }}</span>
        </div>
      </div>

      <!-- 操作区 -->
      <div class="actions">
        <template v-if="isMock && !isSuccess && !isClosed">
          <el-button type="primary" size="large" :loading="paying" @click="doMockPay">
            模拟支付成功（演示）
          </el-button>
        </template>
        <template v-else-if="!isSuccess && !isClosed">
          <el-button type="primary" size="large" :loading="refreshing" @click="refresh">
            刷新支付结果
          </el-button>
        </template>

        <el-button size="large" @click="goDonate">
          {{ isSuccess ? '再次捐助' : '返回捐助页' }}
        </el-button>
      </div>

      <p v-if="isMock" class="mock-tip">
        当前为 Mock 演示模式：未接入真实支付，点击上方按钮可直接模拟支付成功。
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { Loading, CircleCheckFilled, CircleCloseFilled, Clock } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const outTradeNo = ref(route.query.out_trade_no || '')
const order = ref(null)
const loading = ref(true)
const refreshing = ref(false)
const paying = ref(false)
const isMock = ref(false)
const timedOut = ref(false)

let timer = null
let elapsed = 0
const POLL_INTERVAL = 3000
const POLL_MAX = 60000

const SUCCESS_STATES = ['PAID', 'FINISHED']
const CLOSED_STATES = ['CLOSED', 'FAILED']

const isSuccess = computed(() => order.value && SUCCESS_STATES.includes(order.value.status))
const isClosed = computed(() => order.value && CLOSED_STATES.includes(order.value.status))

const statusText = computed(() => {
  if (!order.value) return '-'
  if (SUCCESS_STATES.includes(order.value.status)) return '支付成功'
  if (order.value.status === 'CLOSED') return '已关闭'
  if (order.value.status === 'FAILED') return '支付失败'
  return '处理中'
})

const statusTagType = computed(() => {
  if (isSuccess.value) return 'success'
  if (isClosed.value) return 'danger'
  return 'warning'
})

const loadOrder = async () => {
  if (!outTradeNo.value) {
    ElMessage.error('缺少订单号')
    loading.value = false
    return
  }
  try {
    const res = await request.get(`/api/payment/orders/${outTradeNo.value}`)
    const body = res.data || {}
    if (body.code === '200' && body.data) {
      order.value = body.data
    } else {
      ElMessage.error(body.msg || '订单不存在')
    }
  } catch (e) {
    ElMessage.error('查询订单失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

/** 主动向后端同步一次支付状态（真实模式下会向支付宝查单） */
const syncOrder = async () => {
  try {
    const res = await request.post(`/api/payment/orders/${outTradeNo.value}/sync`)
    const body = res.data || {}
    if (body.code === '200' && body.data) {
      order.value = body.data
    }
  } catch (e) {
    // 同步失败静默处理，等待下一次轮询
  }
}

const startPolling = () => {
  stopPolling()
  elapsed = 0
  timer = setInterval(async () => {
    elapsed += POLL_INTERVAL
    if (isSuccess.value || isClosed.value) {
      stopPolling()
      return
    }
    await syncOrder()
    if (isSuccess.value || isClosed.value) {
      stopPolling()
      return
    }
    if (elapsed >= POLL_MAX) {
      timedOut.value = true
      stopPolling()
    }
  }, POLL_INTERVAL)
}

const stopPolling = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

const refresh = async () => {
  refreshing.value = true
  timedOut.value = false
  await syncOrder()
  refreshing.value = false
  if (isSuccess.value) {
    ElMessage.success('已确认支付成功')
  } else if (!isClosed.value) {
    ElMessage.info('暂未查询到支付结果，请稍后再试')
  }
}

const doMockPay = async () => {
  paying.value = true
  try {
    const res = await request.post(`/api/payment/orders/${outTradeNo.value}/mock/pay`)
    const body = res.data || {}
    if (body.code === '200' && body.data) {
      order.value = body.data
      ElMessage.success('模拟支付成功')
      stopPolling()
    } else {
      ElMessage.error(body.msg || '模拟支付失败')
    }
  } catch (e) {
    ElMessage.error('模拟支付失败，请稍后重试')
  } finally {
    paying.value = false
  }
}

const goDonate = () => router.push('/hzsca/donate')

onMounted(async () => {
  try {
    const cfg = await request.get('/api/payment/config')
    if (cfg.data?.code === '200' && cfg.data.data) {
      isMock.value = !!cfg.data.data.mock
    }
  } catch (e) {
    // 忽略，默认按真实模式处理
  }
  await loadOrder()
  if (!isSuccess.value && !isClosed.value) {
    startPolling()
  }
})

onUnmounted(stopPolling)
</script>

<style scoped>
.result-container {
  max-width: 640px;
  margin: 40px auto;
  padding: 0 20px;
}

.result-card {
  background: #fff;
  border: 1px solid #f0e6d8;
  border-radius: 18px;
  padding: 40px 32px;
  text-align: center;
  box-shadow: 0 6px 24px rgba(93, 64, 55, 0.08);
}

.status-icon {
  font-size: 72px;
  margin-bottom: 16px;
}

.status-icon.success {
  color: #7ba05b;
}

.status-icon.closed {
  color: #c05746;
}

.status-icon.processing {
  color: #e09f52;
}

.status-title {
  font-size: 24px;
  color: #5d4037;
  margin-bottom: 10px;
}

.status-desc {
  color: #8d7b6a;
  font-size: 15px;
  margin-bottom: 26px;
}

.order-detail {
  text-align: left;
  background: #fdfaf5;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 26px;
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 9px 0;
  color: #6d5a4a;
  font-size: 14px;
}

.detail-row + .detail-row {
  border-top: 1px dashed #efe4d5;
}

.mono {
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  color: #8d7b6a;
}

.amount {
  font-size: 20px;
  color: #d97706;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.mock-tip {
  margin-top: 18px;
  font-size: 13px;
  color: #b08968;
  line-height: 1.6;
}
</style>
