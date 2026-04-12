const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    loading: false,
    list: []
  },
  onShow() {
    if (!ensureLogin()) return
    this.fetch()
  },
  async fetch() {
    this.setData({ loading: true })
    try {
      const res = await request({
        url: '/match/recommendations?limit=10',
        method: 'GET'
      })
      if (res.code === '200') {
        const list = Array.isArray(res.data) ? res.data : []
        this.setData({ list })
        return
      }
      this.setData({ list: [] })
      if (res.code === '400') {
        wx.showModal({
          title: '需要完善档案',
          content: res.msg || '请先完善领养档案',
          confirmText: '去完善',
          cancelText: '取消',
          success: (r) => {
            if (r.confirm) {
              wx.navigateTo({ url: '/pages/profile/index' })
            }
          }
        })
        return
      }
      wx.showToast({ title: res.msg || '获取推荐失败', icon: 'none' })
    } catch (e) {
      this.setData({ list: [] })
      wx.showToast({ title: e?.errMsg || '获取推荐失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },
  goProfile() {
    wx.navigateTo({ url: '/pages/profile/index' })
  },
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    if (!id) return
    wx.navigateTo({
      url: `/pages/cat-detail/index?id=${id}`
    })
  }
})

