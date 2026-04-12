const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    list: []
  },
  onShow() {
    if (!ensureLogin()) return
    this.fetchList()
  },
  async fetchList() {
    try {
      const data = await request({
        url: '/adoption-requests/my',
        method: 'GET'
      })
      if (data.code === '200') {
        this.setData({
          list: data.data || []
        })
        return
      }
      wx.showToast({ title: data.msg || '获取失败', icon: 'none' })
    } catch (error) {
      wx.showToast({ title: '获取申请失败', icon: 'none' })
    }
  }
})
