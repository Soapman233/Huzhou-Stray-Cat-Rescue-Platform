const { request } = require('../../utils/request')

Page({
  data: {
    // 支付配置
    mock: true,
    minYuan: 1,
    maxYuan: 50000,
    totalDonatedYuan: '0.00',

    // 表单
    presets: [10, 30, 50, 100],
    amountYuan: 30,
    customMode: false,
    customAmount: '',
    remark: '',

    // 状态
    submitting: false,
    order: null,
    statusText: '',
    statusType: '', // pending / success / closed
    payUrl: ''
  },

  onLoad() {
    this.loadConfig()
  },

  onShow() {
    this.loadConfig()
  },

  /** 读取支付配置（是否 Mock 模式、金额上下限、累计捐助） */
  loadConfig() {
    request({ url: '/payment/config' })
      .then((res) => {
        if (res && res.code === '200' && res.data) {
          const d = res.data
          this.setData({
            mock: !!d.mock,
            minYuan: (d.minAmount || 100) / 100,
            maxYuan: (d.maxAmount || 5000000) / 100,
            totalDonatedYuan: ((d.totalDonated || 0) / 100).toFixed(2)
          })
        }
      })
      .catch(() => {
        // 配置读取失败时保持默认（Mock），不阻断页面
      })
  },

  selectPreset(e) {
    const value = Number(e.currentTarget.dataset.value)
    this.setData({ customMode: false, amountYuan: value, customAmount: '' })
  },

  enableCustom() {
    this.setData({ customMode: true })
  },

  onCustomInput(e) {
    this.setData({ customAmount: e.detail.value })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  /** 当前生效金额（元） */
  currentAmount() {
    const raw = this.data.customMode ? this.data.customAmount : this.data.amountYuan
    const n = Number(raw)
    return isNaN(n) ? 0 : n
  },

  onSubmit() {
    if (this.data.submitting) {
      return
    }
    const amount = this.currentAmount()
    if (!(amount > 0)) {
      wx.showToast({ title: '请选择或输入捐助金额', icon: 'none' })
      return
    }
    if (amount < this.data.minYuan || amount > this.data.maxYuan) {
      wx.showToast({
        title: `金额需在 ${this.data.minYuan} ~ ${this.data.maxYuan} 元之间`,
        icon: 'none'
      })
      return
    }

    wx.showModal({
      title: '确认捐助信息',
      content: `捐助金额：¥${amount.toFixed(2)}\n支付方式：${this.data.mock ? '模拟支付（演示）' : '支付宝'}`,
      confirmText: '确认捐助',
      cancelText: '再想想',
      success: (r) => {
        if (r.confirm) {
          this.createOrder(amount)
        }
      }
    })
  },

  createOrder(amount) {
    this.setData({ submitting: true })
    wx.showLoading({ title: '正在创建订单…', mask: true })

    request({
      url: '/payment/orders',
      method: 'POST',
      data: {
        amount: Math.round(amount * 100), // 转「分」
        purpose: 'donate',
        remark: this.data.remark || null
      }
    })
      .then((res) => {
        wx.hideLoading()
        this.setData({ submitting: false })

        if (!res || res.code !== '200' || !res.data) {
          wx.showToast({ title: (res && res.msg) || '创建订单失败', icon: 'none' })
          return
        }
        const order = res.data
        this.setData({
          order,
          payUrl: order.payUrl || '',
          statusText: '待支付',
          statusType: 'pending'
        })
        if (order.mock) {
          wx.showToast({ title: '已创建演示订单', icon: 'none' })
        } else {
          wx.showModal({
            title: '请完成支付',
            content: '小程序内暂不支持直接跳转支付宝收银台，请复制下方链接并在浏览器中打开完成支付。',
            showCancel: false
          })
        }
      })
      .catch((err) => {
        wx.hideLoading()
        this.setData({ submitting: false })
        const msg = (err && err.data && err.data.msg) || '创建订单失败，请稍后重试'
        wx.showToast({ title: msg, icon: 'none' })
      })
  },

  /** 模拟支付（仅 Mock 模式） */
  onMockPay() {
    const order = this.data.order
    if (!order || this.data.submitting) {
      return
    }
    this.setData({ submitting: true })
    wx.showLoading({ title: '支付中…', mask: true })

    request({ url: `/payment/orders/${order.outTradeNo}/mock/pay`, method: 'POST' })
      .then((res) => {
        wx.hideLoading()
        this.setData({ submitting: false })
        if (res && res.code === '200' && res.data) {
          this.applyOrder(res.data)
          wx.showToast({ title: '捐助成功', icon: 'success' })
        } else {
          wx.showToast({ title: (res && res.msg) || '支付失败', icon: 'none' })
        }
      })
      .catch(() => {
        wx.hideLoading()
        this.setData({ submitting: false })
        wx.showToast({ title: '支付失败，请稍后重试', icon: 'none' })
      })
  },

  /** 刷新订单状态（真实模式用于主动查单） */
  onRefresh() {
    const order = this.data.order
    if (!order) {
      return
    }
    wx.showLoading({ title: '查询中…', mask: true })
    request({ url: `/payment/orders/${order.outTradeNo}/sync`, method: 'POST' })
      .then((res) => {
        wx.hideLoading()
        if (res && res.code === '200' && res.data) {
          this.applyOrder(res.data)
          if (this.data.statusType !== 'success') {
            wx.showToast({ title: '暂未查询到支付结果', icon: 'none' })
          }
        }
      })
      .catch(() => {
        wx.hideLoading()
        wx.showToast({ title: '查询失败', icon: 'none' })
      })
  },

  /** 把订单状态映射到页面展示状态 */
  applyOrder(order) {
    const st = order.status
    let statusText = '待支付'
    let statusType = 'pending'
    if (st === 'PAID' || st === 'FINISHED') {
      statusText = '支付成功'
      statusType = 'success'
    } else if (st === 'CLOSED' || st === 'FAILED') {
      statusText = '订单已关闭'
      statusType = 'closed'
    } else if (st === 'PAYING') {
      statusText = '支付中'
    }
    this.setData({ order, statusText, statusType })
    this.loadConfig()
  },

  /** 复制收银台链接（真实模式） */
  onCopyPayUrl() {
    if (!this.data.payUrl) {
      return
    }
    wx.setClipboardData({
      data: this.data.payUrl,
      success() {
        wx.showToast({ title: '链接已复制', icon: 'success' })
      }
    })
  },

  /** 重置，再来一单 */
  onReset() {
    this.setData({
      order: null,
      remark: '',
      statusText: '',
      statusType: '',
      payUrl: '',
      customMode: false,
      customAmount: ''
    })
  }
})
