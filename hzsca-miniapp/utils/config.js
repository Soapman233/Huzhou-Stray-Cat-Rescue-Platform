/**
 * 小程序全局配置
 *
 * BASE_URL 为后端接口基地址：
 * - 本地调试：微信开发者工具需在「详情 → 本地设置」勾选「不校验合法域名」；
 * - 真机预览 / 正式发布：必须替换为已在小程序后台配置的 HTTPS 合法域名，
 *   例如 'https://api.your-domain.com/api'，否则真机将无法请求。
 */
const BASE_URL = 'http://127.0.0.1:8080/api'

module.exports = {
  BASE_URL
}
