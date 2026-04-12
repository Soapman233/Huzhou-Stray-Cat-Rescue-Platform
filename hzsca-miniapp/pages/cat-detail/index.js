const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    id: '',
    cat: null,
    baseImageUrl: 'http://127.0.0.1:8080'
  },
  onLoad(options) {
    this.setData({
      id: options.id || ''
    })
    this.fetchDetail()
  },
  async fetchDetail() {
    if (!this.data.id) return
    try {
      const data = await request({
        url: `/cats/${this.data.id}`,
        method: 'GET'
      })
      if (data.code === '200') {
        this.setData({
          cat: data.data
        })
        return
      }
      wx.showToast({ title: data.msg || '获取失败', icon: 'none' })
    } catch (error) {
      wx.showToast({ title: '获取详情失败', icon: 'none' })
    }
  },
  goApply() {
    if (!ensureLogin()) return
    wx.navigateTo({
      url: `/pages/apply/index?id=${this.data.id}`
    })
  }
})
