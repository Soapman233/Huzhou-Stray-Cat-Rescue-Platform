<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="aside">
      <div class="brand">
        <div class="logo">HZSCA</div>
        <div class="subtitle">管理后台</div>
      </div>
      <el-menu
          :default-active="active"
          class="menu"
          router
      >
        <el-menu-item index="/hzsca/admin/dashboard">仪表盘</el-menu-item>
        <el-menu-item index="/hzsca/admin/cats">猫咪管理</el-menu-item>
        <el-menu-item index="/hzsca/admin/cat-traits">猫咪特征</el-menu-item>
        <el-menu-item index="/hzsca/admin/activities">活动管理</el-menu-item>
        <el-menu-item index="/hzsca/admin/volunteers">志愿者审核</el-menu-item>
        <el-menu-item index="/hzsca/admin/adoptions">领养申请</el-menu-item>
        <el-menu-item index="/hzsca/admin/rescues">救助上报</el-menu-item>
        <el-menu-item index="/hzsca/admin/dataset">数据导出</el-menu-item>
        <el-menu-item index="/hzsca/admin/users">用户管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <div class="page-title">{{ title }}</div>
        </div>
        <div class="header-right">
          <el-button class="logout" @click="onLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const active = computed(() => route.path)
const title = computed(() => route.meta?.title || '管理后台')

const onLogout = () => {
  ElMessageBox.confirm('确定要退出管理员登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('username')
    ElMessage.success('已退出登录')
    router.push('/hzsca/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.aside {
  background: #111827;
  color: #ffffff;
}

.brand {
  padding: 18px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 1px;
}

.subtitle {
  margin-top: 6px;
  font-size: 12px;
  opacity: 0.8;
}

.menu {
  border-right: none;
  background: transparent;
}

:deep(.menu .el-menu-item) {
  color: rgba(255, 255, 255, 0.86);
}

:deep(.menu .el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.10);
  color: #ffffff;
}

:deep(.menu .el-menu-item.is-active) {
  background: rgba(230, 220, 200, 0.20);
  color: #ffffff;
  font-weight: 800;
}

.header {
  background: #E6DCC8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.page-title {
  font-size: 16px;
  font-weight: 800;
  color: #111827;
}

.logout {
  background: #111827;
  color: #ffffff;
  border: none;
}

.main {
  padding: 16px;
}
</style>
