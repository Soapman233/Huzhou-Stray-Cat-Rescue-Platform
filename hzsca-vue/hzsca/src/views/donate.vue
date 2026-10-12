<template>
  <div class="donate-container">
    <div class="header">
      <h1><el-icon><StarFilled /></el-icon> 流浪猫救助捐款</h1>
      <p>您的每一份爱心都能改变一只流浪猫的命运</p>
    </div>

    <!-- ============ 爱心捐助 ============ -->
    <div class="donate-card">
      <div class="donate-card__head">
        <h2><el-icon><Wallet /></el-icon> 爱心捐助</h2>
        <el-tag v-if="paymentConfig.mock" type="warning" effect="plain" size="small">
          Mock 演示模式
        </el-tag>
      </div>

      <el-alert
        v-if="paymentConfig.mock"
        type="warning"
        :closable="false"
        show-icon
        title="当前未接入真实支付通道"
        description="后端未配置支付宝沙箱密钥，捐助流程将以「模拟支付」方式演示，不会产生任何真实扣款。"
        class="mock-alert"
      />

      <div class="stat-line">
        <span>累计收到爱心捐助</span>
        <strong>¥ {{ (paymentConfig.totalDonated / 100).toFixed(2) }}</strong>
      </div>

      <div class="amount-label">选择捐助金额</div>
      <div class="amount-presets">
        <button
          v-for="preset in presets"
          :key="preset"
          class="amount-chip"
          :class="{ active: amountYuan === preset && !customMode }"
          type="button"
          @click="selectPreset(preset)"
        >
          ¥ {{ preset }}
        </button>
        <button
          class="amount-chip"
          :class="{ active: customMode }"
          type="button"
          @click="customMode = true"
        >
          自定义
        </button>
      </div>

      <div v-if="customMode" class="custom-amount">
        <el-input-number
          v-model="amountYuan"
          :min="minYuan"
          :max="maxYuan"
          :precision="2"
          :step="1"
          controls-position="right"
          class="custom-input"
        />
        <span class="unit">元</span>
      </div>

      <div class="remark-line">
        <el-input
          v-model="remark"
          maxlength="50"
          show-word-limit
          placeholder="留言 / 昵称（选填，将记录在订单中）"
        />
      </div>

      <div class="tip-line">
        单笔捐助金额范围：¥{{ minYuan }} ~ ¥{{ maxYuan }}
      </div>

      <el-button
        type="primary"
        size="large"
        class="submit-btn"
        :loading="submitting"
        :disabled="!canSubmit"
        @click="confirmVisible = true"
      >
        {{ submitting ? '正在创建订单…' : `确认捐助 ¥ ${Number(amountYuan || 0).toFixed(2)}` }}
      </el-button>

      <p class="safe-tip">
        <el-icon><Lock /></el-icon>
        支付过程由支付宝安全承载，平台不接触您的支付凭证
      </p>
    </div>

    <div class="content-section">
      <div class="purpose-section">
        <h2><el-icon><InfoFilled /></el-icon> 救助目的</h2>
        <p>我们湖州拯救流浪猫协会致力于为无家可归的流浪猫提供:</p>
        <ul>
          <li>日常食物和清洁饮水</li>
          <li>必要的医疗救助</li>
          <li>绝育手术控制数量</li>
          <li>寻找合适的领养家庭</li>
          <li>冬季避寒场所建设</li>
        </ul>
        <p>目前我们已经救助了超过200只流浪猫，为150只猫咪找到了温暖的家。</p>
      </div>

      <div class="thanks-section">
        <h2><el-icon><Present /></el-icon> 感谢您的捐助</h2>
        <p>您的慷慨捐助将直接用于改善流浪猫的生活条件。我们承诺：</p>
        <div class="promise-card">
          <el-icon><Finished /></el-icon>
          <span>每一分钱都会用在猫咪身上</span>
        </div>
        <div class="promise-card">
          <el-icon><Document /></el-icon>
          <span>每月公布详细的资金使用报告</span>
        </div>
        <div class="promise-card">
          <el-icon><Camera /></el-icon>
          <span>定期更新救助猫咪的照片和视频</span>
        </div>
      </div>

      <div class="transparency-section">
        <h2><el-icon><DataLine /></el-icon> 财务透明</h2>
        <p>我们坚信公开透明是获得信任的基础：</p>
        <el-timeline>
          <el-timeline-item timestamp="每月5号" placement="top">
            <el-card>
              <h4>月度财务报告</h4>
              <p>详细列出所有收入和支出明细</p>
            </el-card>
          </el-timeline-item>
          <el-timeline-item timestamp="每季度末" placement="top">
            <el-card>
              <h4>季度救助报告</h4>
              <p>展示救助成果和猫咪现状</p>
            </el-card>
          </el-timeline-item>
          <el-timeline-item timestamp="每年12月" placement="top">
            <el-card>
              <h4>年度审计报告</h4>
              <p>由第三方机构审计的完整财务报告</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <div class="contact-section">
        <h2><el-icon><Phone /></el-icon> 联系我们</h2>
        <p>如有任何疑问或想了解更多救助详情，欢迎随时联系我们：</p>
        <div class="contact-methods">
          <div class="contact-card">
            <el-icon><Iphone /></el-icon>
            <span>电话：0572-2888 9999</span>
          </div>
          <div class="contact-card">
            <el-icon><Message /></el-icon>
            <span>邮箱：rescue@hzcats.org</span>
          </div>
          <div class="contact-card">
            <el-icon><Location /></el-icon>
            <span>地址：湖州市吴兴区爱猫路88号</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 下单确认 -->
    <el-dialog v-model="confirmVisible" title="确认捐助信息" width="420px" append-to-body>
      <div class="confirm-body">
        <div class="confirm-row">
          <span>捐助金额</span>
          <strong class="amount-strong">¥ {{ Number(amountYuan || 0).toFixed(2) }}</strong>
        </div>
        <div class="confirm-row">
          <span>支付方式</span>
          <span>{{ paymentConfig.mock ? '模拟支付（演示）' : '支付宝' }}</span>
        </div>
        <div v-if="remark" class="confirm-row">
          <span>留言</span>
          <span>{{ remark }}</span>
        </div>
        <p class="confirm-tip">
          {{ paymentConfig.mock
            ? '点击确认后将创建订单并进入模拟支付流程。'
            : '点击确认后将跳转到支付宝收银台完成支付。' }}
        </p>
      </div>
      <template #footer>
        <el-button @click="confirmVisible = false">再想想</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">确认捐助</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import {
  StarFilled, InfoFilled, Present, Finished, Document, Camera,
  DataLine, Phone, Iphone, Message, Location, Wallet, Lock
} from '@element-plus/icons-vue'

const router = useRouter()

const presets = [10, 30, 50, 100]
const amountYuan = ref(30)
const customMode = ref(false)
const remark = ref('')
const submitting = ref(false)
const confirmVisible = ref(false)

const paymentConfig = ref({
  mock: true,
  minAmount: 100,
  maxAmount: 5000000,
  totalDonated: 0
})

const minYuan = computed(() => paymentConfig.value.minAmount / 100)
const maxYuan = computed(() => paymentConfig.value.maxAmount / 100)
const canSubmit = computed(() => {
  const v = Number(amountYuan.value)
  return v >= minYuan.value && v <= maxYuan.value
})

const selectPreset = (v) => {
  customMode.value = false
  amountYuan.value = v
}

const loadConfig = async () => {
  try {
    const res = await request.get('/api/payment/config')
    if (res.data?.code === '200' && res.data.data) {
      paymentConfig.value = { ...paymentConfig.value, ...res.data.data }
    }
  } catch (e) {
    // 配置读取失败时保持默认（Mock）配置，不阻断页面
    console.warn('读取支付配置失败，使用默认演示配置')
  }
}

const submitOrder = async () => {
  if (!canSubmit.value) {
    ElMessage.warning(`捐助金额需在 ¥${minYuan.value} ~ ¥${maxYuan.value} 之间`)
    return
  }
  submitting.value = true
  try {
    const amountFen = Math.round(Number(amountYuan.value) * 100)
    const res = await request.post('/api/payment/orders', {
      amount: amountFen,
      purpose: 'donate',
      remark: remark.value || null
    })

    const body = res.data || {}
    if (body.code !== '200' || !body.data) {
      ElMessage.error(body.msg || '创建订单失败，请稍后重试')
      return
    }

    const order = body.data
    confirmVisible.value = false

    if (order.mock) {
      // Mock 模式：直接进入结果页，由结果页发起「模拟支付」
      ElMessage.info('已创建演示订单，正在跳转…')
      router.push({ path: '/hzsca/donate/result', query: { out_trade_no: order.outTradeNo } })
      return
    }

    if (!order.payUrl) {
      ElMessage.error('未获取到收银台地址，请稍后重试')
      return
    }
    // 真实模式：跳转支付宝收银台
    ElMessage.success('正在跳转支付宝收银台…')
    window.location.href = order.payUrl
  } catch (e) {
    const msg = e?.response?.data?.msg || '创建订单失败，请检查网络后重试'
    ElMessage.error(msg)
  } finally {
    submitting.value = false
  }
}

onMounted(loadConfig)
</script>

<style scoped>
.donate-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
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
  font-size: 18px;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

/* ===== 捐助卡片 ===== */
.donate-card {
  background: #fff;
  border: 1px solid #f0e6d8;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 40px;
  box-shadow: 0 4px 18px rgba(93, 64, 55, 0.06);
}

.donate-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.donate-card__head h2 {
  margin-bottom: 0;
}

.mock-alert {
  margin-bottom: 16px;
}

.stat-line {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fdf6ec;
  border-radius: 10px;
  color: #6d4c41;
  margin-bottom: 20px;
}

.stat-line strong {
  font-size: 22px;
  color: #d97706;
}

.amount-label {
  color: #5d4037;
  font-weight: 600;
  margin-bottom: 12px;
}

.amount-presets {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.amount-chip {
  min-width: 96px;
  padding: 12px 18px;
  border: 1.5px solid #e0d6c8;
  background: #fff;
  border-radius: 10px;
  font-size: 16px;
  color: #5d4037;
  cursor: pointer;
  transition: all 0.2s ease;
}

.amount-chip:hover {
  border-color: #d9a86c;
  color: #b45309;
}

.amount-chip.active {
  border-color: #d97706;
  background: #fdf3e3;
  color: #b45309;
  font-weight: 600;
}

.custom-amount {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.custom-input {
  width: 200px;
}

.unit {
  color: #78909c;
}

.remark-line {
  margin-bottom: 10px;
}

.tip-line {
  font-size: 13px;
  color: #9e9e9e;
  margin-bottom: 18px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 17px;
  font-weight: 600;
  background: linear-gradient(135deg, #f0a44b, #d97706);
  border: none;
}

.safe-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 12px;
  font-size: 13px;
  color: #9e9e9e;
}

.confirm-body .confirm-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
  color: #5d4037;
}

.amount-strong {
  font-size: 20px;
  color: #d97706;
}

.confirm-tip {
  margin-top: 14px;
  font-size: 13px;
  color: #9e9e9e;
  line-height: 1.6;
}

/* ===== 原有内容区 ===== */
.content-section {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

h2 {
  color: #5d4037;
  font-size: 24px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

h2 .el-icon {
  margin-right: 10px;
  color: #ff9800;
}

.purpose-section ul {
  margin: 15px 0;
  padding-left: 20px;
}

.purpose-section li {
  margin-bottom: 8px;
  color: #546e7a;
}

.promise-card {
  display: flex;
  align-items: center;
  padding: 15px;
  margin: 10px 0;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.promise-card .el-icon {
  margin-right: 10px;
  color: #4caf50;
  font-size: 20px;
}

.contact-methods {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 20px;
}

.contact-card {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background-color: #e3f2fd;
  border-radius: 8px;
  min-width: 250px;
}

.contact-card .el-icon {
  margin-right: 10px;
  color: #2196f3;
  font-size: 20px;
}

@media (max-width: 768px) {
  .header {
    padding: 20px;
  }

  .contact-methods {
    flex-direction: column;
  }

  .contact-card {
    width: 100%;
  }

  .amount-chip {
    flex: 1 1 40%;
    min-width: 0;
  }
}
</style>
