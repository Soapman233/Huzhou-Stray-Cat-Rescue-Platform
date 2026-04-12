function saveLogin(data) {
  const app = getApp()
  const token = data.token || ''
  const user = data.user || null
  app.globalData.token = token
  app.globalData.userInfo = user
  wx.setStorageSync('token', token)
  wx.setStorageSync('userInfo', user)
}

function ensureLogin() {
  const token = wx.getStorageSync('token')
  if (!token) {
    wx.navigateTo({
      url: '/pages/login/index'
    })
    return false
  }
  return true
}

function logout() {
  const app = getApp()
  app.globalData.token = ''
  app.globalData.userInfo = null
  wx.removeStorageSync('token')
  wx.removeStorageSync('userInfo')
}

module.exports = {
  saveLogin,
  ensureLogin,
  logout
}
