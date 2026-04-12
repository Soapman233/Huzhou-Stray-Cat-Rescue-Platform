const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    saving: false,
    form: {
      nickname: '',
      avatar: '',
      city: '',
      province: '',
      country: ''
    }
  },
  onShow() {
    if (!ensureLogin()) return
    this.fetch()
  },
  async fetch() {
    try {
      const res = await request({
        url: '/wx/me',
        method: 'GET'
      })
      if (res.code === '200' && res.data) {
        this.setData({
          form: {
            nickname: res.data.nickname || '',
            avatar: res.data.avatar || '',
            city: res.data.city || '',
            province: res.data.province || '',
            country: res.data.country || ''
          }
        })
      }
    } catch (e) {}
  },
  onInput(e) {
    const key = e.currentTarget.dataset.key
    this.setData({
      [`form.${key}`]: e.detail.value
    })
  },
  async save() {
    this.setData({ saving: true })
    try {
      const res = await request({
        url: '/wx/me',
        method: 'PUT',
        data: this.data.form
      })
      if (res.code === '200') {
        wx.showToast({ title: '保存成功', icon: 'success' })
        const user = res.data
        if (user) {
          wx.setStorageSync('userInfo', user)
          const app = getApp()
          app.globalData.userInfo = user
        }
        wx.navigateBack()
        return
      }
      wx.showToast({ title: res.msg || '保存失败', icon: 'none' })
    } catch (e) {
      wx.showToast({ title: e?.errMsg || '保存失败', icon: 'none' })
    } finally {
      this.setData({ saving: false })
    }
  }
})

