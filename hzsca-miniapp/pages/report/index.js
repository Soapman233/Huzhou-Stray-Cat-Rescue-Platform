const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    form: {
      address: '',
      description: '',
      imageUrls: '',
      urgency: '1',
      lat: '',
      lng: ''
    }
  },
  onShow() {
    ensureLogin()
  },
  handleInput(e) {
    const { field } = e.currentTarget.dataset
    this.setData({
      [`form.${field}`]: e.detail.value
    })
  },
  chooseLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.setData({
          'form.lat': res.latitude,
          'form.lng': res.longitude
        })
        wx.showToast({ title: '定位成功', icon: 'success' })
      },
      fail: () => {
        wx.showToast({ title: '定位失败', icon: 'none' })
      }
    })
  },
  async submitReport() {
    if (!ensureLogin()) return
    try {
      const data = await request({
        url: '/rescue-reports',
        method: 'POST',
        data: {
          ...this.data.form,
          urgency: Number(this.data.form.urgency || 1)
        }
      })
      if (data.code === '200') {
        wx.showToast({ title: '上报成功', icon: 'success' })
        this.setData({
          form: {
            address: '',
            description: '',
            imageUrls: '',
            urgency: '1',
            lat: '',
            lng: ''
          }
        })
        return
      }
      wx.showToast({ title: data.msg || '提交失败', icon: 'none' })
    } catch (error) {
      wx.showToast({ title: '提交失败', icon: 'none' })
    }
  }
})
