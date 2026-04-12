const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    cats: [],
    baseImageUrl: 'http://127.0.0.1:8080'
  },
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 })
    }
    this.fetchCats()
  },
  goRecommend() {
    if (!ensureLogin()) return
    wx.navigateTo({
      url: '/pages/recommend/index'
    })
  },
  goProfile() {
    if (!ensureLogin()) return
    wx.navigateTo({
      url: '/pages/profile/index'
    })
  },
  async fetchCats() {
    try {
      const data = await request({
        url: '/cats',
        method: 'GET'
      })
      if (data.code === '200') {
        this.setData({
          cats: data.data || []
        })
        return
      }
      wx.showToast({ title: data.msg || '获取失败', icon: 'none' })
    } catch (error) {
      wx.showToast({ title: '获取猫咪失败', icon: 'none' })
    }
  },
  goDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/cat-detail/index?id=${id}`
    })
  },
  goApply(e) {
    if (!ensureLogin()) return
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/apply/index?id=${id}`
    })
  }
})
