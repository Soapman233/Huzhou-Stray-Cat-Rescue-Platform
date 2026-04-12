const { request } = require('../../utils/request')
const { saveLogin } = require('../../utils/auth')

Page({
  handleWxLogin() {
    wx.login({
      success: async (loginRes) => {
        if (!loginRes.code) {
          wx.showToast({ title: '获取 code 失败', icon: 'none' })
          return
        }
        try {
          const data = await request({
            url: '/wx/login',
            method: 'POST',
            data: {
              code: loginRes.code
            }
          })

          if (data.code === '200') {
            saveLogin(data.data)
            wx.showToast({ title: '登录成功', icon: 'success' })
            wx.switchTab({
              url: '/pages/home/index'
            })
            return
          }

          wx.showToast({ title: data.msg || '登录失败', icon: 'none' })
        } catch (error) {
          wx.showToast({ title: error?.errMsg || '登录失败', icon: 'none' })
        }
      },
      fail() {
        wx.showToast({ title: '微信登录失败', icon: 'none' })
      }
    })
  }
})
