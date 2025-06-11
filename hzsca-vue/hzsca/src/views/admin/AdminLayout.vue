<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <div class="top-bar">
      <div class="left-section">
        <div class="logo-container">
          <span>HZSCA 管理后台</span>
        </div>
        <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            class="admin-menu"
            background-color="#1E4A6D"
            text-color="#fff"
            active-text-color="#ffd04b"
            router
        >
          <el-menu-item index="/hzsca/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/hzsca/admin/cats">
            <el-icon><House /></el-icon>
            <span>猫咪管理</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="right-section">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/hzsca/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>管理后台</el-breadcrumb-item>
          <el-breadcrumb-item>{{ currentPage }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="user-info">
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              <el-avatar :size="32" :src="userInfo.avatar" />
              <span>{{ userInfo.name }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToPersonalInfo">个人信息</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <div class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, House } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

// 计算当前激活的菜单项
const activeMenu = computed(() => route.path)

// 计算当前页面标题
const currentPage = computed(() => {
  switch (route.path) {
    case '/hzsca/admin/users':
      return '用户管理'
    case '/hzsca/admin/cats':
      return '猫咪管理'
    default:
      return '管理后台'
  }
})

// 跳转到个人信息页面
const goToPersonalInfo = () => {
  router.push('/hzsca/personalInfo')
}

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/hzsca/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-bar {
  background-color: #1E4A6D;
  color: white;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  box-shadow: 0 2px 4px rgba(0,21,41,.08);
}

.left-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 32px;
  height: 32px;
}

.admin-menu {
  border: none;
  background-color: transparent !important;
}

.admin-menu :deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
  color: #fff;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background-color: #173A57 !important;
  color: #ffd04b;
}

.admin-menu :deep(.el-menu-item:hover) {
  background-color: #173A57 !important;
}

.right-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.right-section :deep(.el-breadcrumb) {
  color: #fff;
}

.right-section :deep(.el-breadcrumb__item) {
  color: #fff;
}

.right-section :deep(.el-breadcrumb__inner) {
  color: #fff;
}

.right-section :deep(.el-breadcrumb__separator) {
  color: #fff;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
}

.user-dropdown span {
  margin-left: 8px;
}

.main-content {
  flex: 1;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-content {
  background-color: white;
  border-radius: 4px;
  padding: 20px;
  min-height: calc(100vh - 100px);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .top-bar {
    flex-direction: column;
    height: auto;
    padding: 10px;
  }

  .left-section {
    flex-direction: column;
    width: 100%;
  }

  .admin-menu {
    width: 100%;
  }

  .right-section {
    width: 100%;
    justify-content: space-between;
    margin-top: 10px;
  }
}
</style>