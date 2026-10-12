import { fileURLToPath, URL } from 'node:url'
import path from 'path'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: {
        compilerOptions: {
          whitespace: 'preserve',
        },
      },
      reactivityTransform: true,
    }),
    AutoImport({
      // 已在 main.js 全量引入 element-plus 样式，这里关闭按需样式引入避免重复
      resolvers: [ElementPlusResolver({ importStyle: false })],
    }),
    Components({
      resolvers: [ElementPlusResolver({ importStyle: false })],
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  build: {
    // 单文件超过 800KB 才告警（拆分后主包应远低于此值）
    chunkSizeWarningLimit: 800,
    rollupOptions: {
      output: {
        // 把体积较大的第三方库拆成独立 chunk，避免主包过大、提升首屏加载与缓存命中率
        manualChunks(id) {
          if (!id.includes('node_modules')) {
            return undefined
          }
          const normalized = id.replace(/\\/g, '/')
          if (normalized.includes('element-plus') || normalized.includes('@element-plus')) {
            return 'element-plus'
          }
          if (normalized.includes('echarts') || normalized.includes('zrender')) {
            return 'echarts'
          }
          if (normalized.includes('@wangeditor')) {
            return 'wangeditor'
          }
          if (/node_modules\/(vue|@vue|vue-router|vue-demi|@vueuse)\//.test(normalized)) {
            return 'vue-vendor'
          }
          return 'vendor'
        }
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 自动导入定制化样式文件进行样式覆盖
        additionalData: `@use "@/assets/index.scss" as *;`
      }
    }
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    open: true,
    cors: true,
    force: true, // 强制依赖预构建
    hmr: {
      protocol: 'ws',
      host: 'localhost',
      port: 5173,
      clientPort: 5173,
      timeout: 5000,
      overlay: false,
      path: '@hmr',
    },
    watch: {
      usePolling: true,
      interval: 50,
      followSymlinks: true,
      ignored: ['**/node_modules/**', '**/.git/**'],
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '/api')  // 保持/api前缀
      }
    }
  }
})
