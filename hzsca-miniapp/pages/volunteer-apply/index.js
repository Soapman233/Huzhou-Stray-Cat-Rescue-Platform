const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    loading: false,
    form: {
      name: '',
      phone: '',
      age: 18,
      experience: '',
      motivation: ''
    }
  },
  onShow() {
    if (!ensureLogin()) return
    const user = wx.getStorageSync('userInfo') || {}
    this.setData({
      form: {
        name: user.nickname || '',
        phone: '',
        age: 18,
        experience: '',
        motivation: ''
      }
    })
  },
  onInput(e) {
    const key = e.currentTarget.dataset.key
    this.setData({
      [`form.${key}`]: e.detail.value
    })
  },
  async submit() {
    if (!this.data.form.name) {
      wx.showToast({ title: '请填写姓名', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    try {
      const res = await request({
        url: '/volunteer/apply',
        method: 'POST',
        data: {
          ...this.data.form,
          age: Number(this.data.form.age) || 0
        }
      })
      if (res.code === '200') {
        wx.showModal({
          title: '提交成功',
          content: '申请已提交，请等待管理员审核。审核通过后请重新登录获取志愿者身份。',
          showCancel: false,
          success: () => {
            wx.navigateBack()
          }
        })
        return
      }
      wx.showToast({ title: res.msg || '提交失败', icon: 'none' })
    } catch (e) {
      wx.showToast({ title: e?.errMsg || '提交失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  }
})

