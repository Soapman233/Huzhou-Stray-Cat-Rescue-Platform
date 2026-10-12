import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 仅引入 Element Plus 样式；组件本身由 unplugin-vue-components 按需自动引入，
// 不再使用 `app.use(ElementPlus)`，避免把整个组件库打进首屏包。
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(router)

app.mount('#app')
