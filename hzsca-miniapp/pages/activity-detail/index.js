const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    id: null,
    activity: {},
    isVolunteer: false,
    baseImageUrl: 'http://127.0.0.1:8080'
  },
  onLoad(query) {
    const id = Number(query?.id)
    this.setData({ id: Number.isFinite(id) ? id : null })
  },
  onShow() {
    const token = wx.getStorageSync('token')
    const user = wx.getStorageSync('userInfo') || {}
    this.setData({
      isVolunteer: !!token && (user.role || '') === 'volunteer'
    })
    this.fetch()
  },
  async fetch() {
    const id = this.data.id
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    try {
      const res = await request({
        url: '/activity/list',
        method: 'GET'
      })
      if (res.code === '200') {
        const list = Array.isArray(res.data) ? res.data : []
        const hit = list.find(a => a && Number(a.id) === id)
        if (hit) {
          this.setData({ activity: hit })
          return
        }
        wx.showToast({ title: '活动不存在', icon: 'none' })
        return
      }
      wx.showToast({ title: res.msg || '加载失败', icon: 'none' })
    } catch (e) {
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },
  goBack() {
    wx.navigateBack()
  },
  async join() {
    if (!ensureLogin()) return
    const user = wx.getStorageSync('userInfo') || {}
    if ((user.role || '') !== 'volunteer') {
      wx.showModal({
        title: '需要志愿者身份',
        content: '请先申请成为志愿者并等待管理员审核通过。审核通过后请重新登录获取身份。',
        showCancel: false
      })
      return
    }
    const id = this.data.id
    try {
      const res = await request({
        url: `/activity/join/${id}`,
        method: 'POST'
      })
      if (res.code === '200') {
        wx.showToast({ title: '已提交报名，等待审核', icon: 'success' })
        return
      }
      wx.showToast({ title: res.msg || '预约失败', icon: 'none' })
    } catch (e) {
      wx.showToast({ title: e?.errMsg || '预约失败', icon: 'none' })
    }
  }
})
