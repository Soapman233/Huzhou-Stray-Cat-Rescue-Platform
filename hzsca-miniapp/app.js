App({
  globalData: {
    token: '',
    userInfo: null
  },
  onLaunch() {
    const token = wx.getStorageSync('token') || ''
    const userInfo = wx.getStorageSync('userInfo') || null
    this.globalData.token = token
    this.globalData.userInfo = userInfo
  }
})
