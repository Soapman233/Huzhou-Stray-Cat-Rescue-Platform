const { request } = require('../../utils/request')
const { ensureLogin } = require('../../utils/auth')

Page({
  data: {
    loading: false,
    form: {
      residenceCity: '',
      housingType: 'RENT',
      homeSizeLevel: 2,
      hasChildren: false,
      hasOtherPets: false,
      allergy: false,
      maxSheddingLevel: 5,
      dailyCompanionshipHours: 2,
      workSchedule: 'DAY',
      budgetLevel: 2,
      experienceLevel: 0,
      personalityPreference: 'ANY',
      acceptSpecialCare: false
    }
  },
  onShow() {
    if (!ensureLogin()) return
    this.fetch()
  },
  async fetch() {
    this.setData({ loading: true })
    try {
      const res = await request({
        url: '/adoption-profile/me',
        method: 'GET'
      })
      if (res.code === '200') {
        const profile = res.data?.profile
        if (profile) {
          this.setData({
            form: {
              residenceCity: profile.residenceCity || '',
              housingType: profile.housingType || 'RENT',
              homeSizeLevel: profile.homeSizeLevel || 2,
              hasChildren: profile.hasChildren === true,
              hasOtherPets: profile.hasOtherPets === true,
              allergy: profile.allergy === true,
              maxSheddingLevel: profile.maxSheddingLevel || 5,
              dailyCompanionshipHours: profile.dailyCompanionshipHours ?? 2,
              workSchedule: profile.workSchedule || 'DAY',
              budgetLevel: profile.budgetLevel || 2,
              experienceLevel: profile.experienceLevel ?? 0,
              personalityPreference: profile.personalityPreference || 'ANY',
              acceptSpecialCare: profile.acceptSpecialCare === true
            }
          })
        }
        return
      }
      wx.showToast({ title: res.msg || '加载失败', icon: 'none' })
    } catch (e) {
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },
  onCityInput(e) {
    this.setData({ 'form.residenceCity': e.detail.value })
  },
  onHousingChange(e) {
    this.setData({ 'form.housingType': e.detail.value })
  },
  onHomeSizeChange(e) {
    this.setData({ 'form.homeSizeLevel': Number(e.detail.value) })
  },
  onHasChildrenChange(e) {
    this.setData({ 'form.hasChildren': e.detail.value })
  },
  onHasOtherPetsChange(e) {
    this.setData({ 'form.hasOtherPets': e.detail.value })
  },
  onHoursChange(e) {
    this.setData({ 'form.dailyCompanionshipHours': Number(e.detail.value) })
  },
  onScheduleChange(e) {
    this.setData({ 'form.workSchedule': e.detail.value })
  },
  onAllergyChange(e) {
    const v = e.detail.value
    this.setData({ 'form.allergy': v })
    if (!v) {
      this.setData({ 'form.maxSheddingLevel': 5 })
    }
  },
  onMaxSheddingChange(e) {
    this.setData({ 'form.maxSheddingLevel': Number(e.detail.value) })
  },
  onBudgetChange(e) {
    this.setData({ 'form.budgetLevel': Number(e.detail.value) })
  },
  onExperienceChange(e) {
    this.setData({ 'form.experienceLevel': Number(e.detail.value) })
  },
  onPreferenceChange(e) {
    this.setData({ 'form.personalityPreference': e.detail.value })
  },
  onAcceptSpecialCareChange(e) {
    this.setData({ 'form.acceptSpecialCare': e.detail.value })
  },
  async save() {
    const f = this.data.form
    if (!f.residenceCity || !f.housingType || f.dailyCompanionshipHours == null) {
      wx.showToast({ title: '请完善必填项', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    try {
      const res = await request({
        url: '/adoption-profile/me',
        method: 'PUT',
        data: f
      })
      if (res.code === '200') {
        wx.showToast({ title: '保存成功', icon: 'success' })
        return
      }
      wx.showToast({ title: res.msg || '保存失败', icon: 'none' })
    } catch (e) {
      wx.showToast({ title: e?.errMsg || '保存失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  }
})

