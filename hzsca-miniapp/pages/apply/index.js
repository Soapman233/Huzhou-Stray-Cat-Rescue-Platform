const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    animalId: '',
    form: {
      familyIncome: '',
      livingSpace: '',
      workHours: '',
      hasOwnedPets: false,
      smoker: false,
      asthma: false,
      allergy: false,
      acceptReturnVisit: true,
      remarks: ''
    }
  },
  onLoad(options) {
    if (!ensureLogin()) return
    this.setData({
      animalId: options.id || ''
    })
  },
  handleInput(e) {
    const { field } = e.currentTarget.dataset
    const { value } = e.detail
    this.setData({
      [`form.${field}`]: value
    })
  },
  handleSwitch(e) {
    const { field } = e.currentTarget.dataset
    this.setData({
      [`form.${field}`]: e.detail.value
    })
  },
  async submitApply() {
    try {
      const payload = {
        animalId: Number(this.data.animalId),
        ...this.data.form,
        familyIncome: this.data.form.familyIncome ? Number(this.data.form.familyIncome) : null,
        workHours: this.data.form.workHours ? Number(this.data.form.workHours) : null
      }
      const data = await request({
        url: '/adoption-requests',
        method: 'POST',
        data: payload
      })
      if (data.code === '200') {
        wx.showToast({ title: '申请成功', icon: 'success' })
        wx.switchTab({
          url: '/pages/my-applications/index'
        })
        return
      }
      wx.showToast({ title: data.msg || '提交失败', icon: 'none' })
    } catch (error) {
      wx.showToast({ title: '提交失败', icon: 'none' })
    }
  }
})
