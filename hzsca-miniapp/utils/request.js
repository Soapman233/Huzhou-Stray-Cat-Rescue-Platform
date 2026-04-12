const BASE_URL = 'http://127.0.0.1:8080/api'

function request({ url, method = 'GET', data = {}, header = {} }) {
  const app = getApp()
  const token = app?.globalData?.token || wx.getStorageSync('token') || ''

  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}${url}`,
      method,
      timeout: 20000,
      data,
      header: {
        'content-type': 'application/json',
        ...header,
        ...(token ? { Authorization: `Bearer ${token}` } : {})
      },
      success(res) {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data)
          return
        }
        reject(res)
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

module.exports = {
  request
}
