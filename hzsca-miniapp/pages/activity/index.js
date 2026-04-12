const { request } = require('../../utils/request')

Page({
  data: {
    loading: false,
    isLoggedIn: false,
    isVolunteer: false,
    filter: 'ALL',
    keyword: '',
    raw: [],
    list: []
  },
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 })
    }
    const token = wx.getStorageSync('token')
    const user = wx.getStorageSync('userInfo') || {}
    this.setData({
      isLoggedIn: !!token,
      isVolunteer: (user.role || '') === 'volunteer'
    })
    this.reload()
  },
  goLogin() {
    wx.navigateTo({ url: '/pages/login/index' })
  },
  goVolunteerApply() {
    wx.navigateTo({ url: '/pages/volunteer-apply/index' })
  },
  onKeyword(e) {
    this.setData({ keyword: e.detail.value })
    this.applyFilter()
  },
  setFilter(e) {
    const f = e.currentTarget.dataset.filter
    this.setData({ filter: f })
    this.applyFilter()
  },
  async reload() {
    this.setData({ loading: true })
    try {
      const res = await request({
        url: '/activity/list',
        method: 'GET'
      })
      if (res.code === '200') {
        const list = Array.isArray(res.data) ? res.data : []
        this.setData({ raw: list })
        this.applyFilter()
        return
      }
      wx.showToast({ title: res.msg || '加载失败', icon: 'none' })
      this.setData({ raw: [], list: [] })
    } catch (e) {
      wx.showToast({ title: '加载失败', icon: 'none' })
      this.setData({ raw: [], list: [] })
    } finally {
      this.setData({ loading: false })
    }
  },
  applyFilter() {
    const raw = this.data.raw || []
    const f = this.data.filter
    const k = (this.data.keyword || '').toLowerCase()
    const filtered = raw.filter(a => {
      if (!a) return false
      if (f !== 'ALL' && a.status !== f) return false
      if (!k) return true
      const t = `${a.title || ''} ${a.detail || ''} ${a.location || a.Location || ''}`.toLowerCase()
      return t.includes(k)
    })
    this.setData({ list: filtered })
  },
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    if (!id) return
    wx.navigateTo({
      url: `/pages/activity-detail/index?id=${id}`
    })
  }
})
