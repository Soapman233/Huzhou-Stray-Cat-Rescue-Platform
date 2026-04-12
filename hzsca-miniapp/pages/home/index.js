const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    cats: [],
    baseImageUrl: 'http://127.0.0.1:8080'
  },
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 })
    }
    this.fetchCats()
  },
  async fetchCats() {
    try {
      const res = await request({
        url: '/cats',
        method: 'GET'
      })
      if (res.code === '200') {
        const list = Array.isArray(res.data) ? res.data : []
        this.setData({ cats: list.slice(0, 6) })
        return
      }
      this.setData({ cats: [] })
    } catch (e) {
      this.setData({ cats: [] })
    }
  },
  goCats() {
    wx.switchTab({ url: '/pages/cats/index' })
  },
  goActivity() {
    wx.switchTab({ url: '/pages/activity/index' })
  },
  goRecommend() {
    if (!ensureLogin()) return
    wx.navigateTo({ url: '/pages/recommend/index' })
  },
  goProfile() {
    if (!ensureLogin()) return
    wx.navigateTo({ url: '/pages/profile/index' })
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
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    if (!id) return
    wx.navigateTo({
      url: `/pages/cat-detail/index?id=${id}`
    })
  }
})
