const { ensureLogin, logout } = require('../../utils/auth')
const { request } = require('../../utils/request')

Page({
  data: {
    isLoggedIn: false,
    userInfo: {}
  },
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 3 })
    }
    const token = wx.getStorageSync('token')
    const local = wx.getStorageSync('userInfo') || {}
    this.setData({
      isLoggedIn: !!token,
      userInfo: local || {}
    })
    if (token) {
      this.fetchMe()
    }
  },
  async fetchMe() {
    try {
      const res = await request({
        url: '/wx/me',
        method: 'GET'
      })
      if (res.code === '200' && res.data) {
        wx.setStorageSync('userInfo', res.data)
        const app = getApp()
        app.globalData.userInfo = res.data
        this.setData({ userInfo: res.data })
      }
    } catch (e) {}
  },
  goLogin() {
    wx.navigateTo({ url: '/pages/login/index' })
  },
  goMe() {
    if (!ensureLogin()) return
    wx.navigateTo({ url: '/pages/me/index' })
  },
  goProfile() {
    if (!ensureLogin()) return
    wx.navigateTo({ url: '/pages/profile/index' })
  },
  goRecommend() {
    if (!ensureLogin()) return
    wx.navigateTo({ url: '/pages/recommend/index' })
  },
  goApplications() {
    if (!ensureLogin()) return
    wx.navigateTo({ url: '/pages/my-applications/index' })
  },
  goRescueReport() {
    if (!ensureLogin()) return
    wx.navigateTo({ url: '/pages/report/index' })
  },
  goDonate() {
    wx.navigateTo({ url: '/pages/donate/index' })
  },
  goWorkReport() {
    wx.navigateTo({ url: '/pages/work-report/index' })
  },
  doLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      confirmText: '退出',
      cancelText: '取消',
      success: (r) => {
        if (!r.confirm) return
        logout()
        this.setData({ isLoggedIn: false, userInfo: {} })
        wx.switchTab({ url: '/pages/home/index' })
      }
    })
  }
})
