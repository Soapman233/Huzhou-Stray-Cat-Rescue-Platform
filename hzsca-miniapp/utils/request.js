const BASE_URL = 'http://127.0.0.1:8080/api'

function request({ url, method = 'GET', data = {}, header = {} }) {
  const app = getApp()
  const rawToken = app?.globalData?.token || wx.getStorageSync('token') || ''
  let token = ''
  if (typeof rawToken === 'string') {
    token = rawToken.trim()
  } else if (rawToken && typeof rawToken === 'object') {
    const t = rawToken.token
    token = typeof t === 'string' ? t.trim() : ''
  }
  const pureToken = token.toLowerCase().startsWith('bearer ') ? token.slice('bearer '.length).trim() : token
  const authHeader = pureToken ? `Bearer ${pureToken}` : ''
  let fullUrl = `${BASE_URL}${url}`
  if (pureToken && !fullUrl.includes('access_token=')) {
    fullUrl += (fullUrl.includes('?') ? '&' : '?') + `access_token=${encodeURIComponent(pureToken)}`
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: fullUrl,
      method,
      timeout: 20000,
      data,
      header: {
        'content-type': 'application/json',
        ...header,
        ...(authHeader ? { Authorization: authHeader } : {})
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
