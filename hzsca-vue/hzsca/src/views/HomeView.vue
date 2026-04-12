<template>
  <div class="common-layout">
    <!-- 头部开始 -->
    <div class="nav" style="background-color: #E6DCC8; width: 100%;">
      <nav class="nav" style="padding: 15px; display: flex; align-items: center; justify-content: space-between; height: 100%; max-width: 1200px; margin: 0 auto;">
        <div style="display: flex; align-items: center;">
          <div class="title" style="padding-right: 23px; font-weight: 700; font-size: 18px; color: #333;">
            <router-link to="/" style="text-decoration: none; color: #333;"> HZSCA </router-link>
          </div>
          <div style="display: flex; gap: 15px;">
            <router-link to="/" class="nav-link">主页</router-link>
            <router-link to="/hzsca/adopt" class="nav-link">领养</router-link>

            <!-- 活动 -->
            <el-dropdown trigger="click" @command="handleActivityCommand">
              <router-link to="/hzsca/activity" class="nav-link">活动</router-link>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="activityback" >
                    活动回顾
                  </el-dropdown-item>
                  <el-dropdown-item command="activityall" >
                    所有活动
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <router-link to="/hzsca/report" class="nav-link">工作报告</router-link>
            <router-link to="/hzsca/donate" class="nav-link">捐助我们</router-link>
            <!-- 管理员菜单 -->
            <template v-if="isAdmin">
              <el-dropdown trigger="click" @command="handleAdminCommand">
                <span class="nav-link admin-link">
                  管理后台
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="users">
                      <el-icon><User /></el-icon>用户管理
                    </el-dropdown-item>
                    <el-dropdown-item command="cats">
                      <el-icon><House /></el-icon>猫咪管理
                    </el-dropdown-item>
                    <el-dropdown-item command="catTraits">
                      <el-icon><CollectionTag /></el-icon>猫咪特征管理
                    </el-dropdown-item>
                    <el-dropdown-item command="activities">
                      <el-icon><Calendar /></el-icon>活动管理
                    </el-dropdown-item>
                    <el-dropdown-item command="adoptions">
                      <el-icon><Document /></el-icon>领养申请管理
                    </el-dropdown-item>
                    <el-dropdown-item command="rescues">
                      <el-icon><Warning /></el-icon>救助上报管理
                    </el-dropdown-item>
                    <el-dropdown-item command="dataset">
                      <el-icon><Download /></el-icon>数据导出
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>

          </div>
        </div>
        <div class="right-section">
          <template v-if="!isLoggedIn">
            <el-button type="primary" class="explore-btn" @click="goToLogin">立即探索</el-button>
          </template>
          <template v-else>
            <el-dropdown trigger="click" @command="handleCommand">
        <div class="avatar-container">
                <el-avatar v-if="userInfo.avatar" :src="getAvatarUrl(userInfo.avatar)" />
                <el-icon v-else><Avatar /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="personalInfo">
                    <el-icon><User /></el-icon>个人信息
                  </el-dropdown-item>
                  <el-dropdown-item v-if="isAdmin" command="admin" divided>
                    <el-icon><Setting /></el-icon>管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </nav>
    </div>
    <!-- 头部结束 -->

    <!-- 主体开始 -->
    <el-main>
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" :key="$route.fullPath" />
        </keep-alive>
      </router-view>
    </el-main>
    <!-- 主体结束 -->

    <!-- 底部开始 -->
    <div style="text-align: center; padding: 20px; color: #666;background-color: #E6DCC8;">
      © 2025 HZSCA. 版权所有。
    </div>
    <!-- 底部结束 -->
  </div>
</template>

<script setup>
import { Avatar, User, House, Setting, ArrowDown, SwitchButton, Calendar, Document, Warning, Download, CollectionTag } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../utils/request';
import config from '../config/config';

const router = useRouter();
const isLoggedIn = ref(false);
const userInfo = ref({
  avatar: '',
  name: '',
  role: ''
});

// 计算属性：是否为管理员
const isAdmin = computed(() => userInfo.value.role === 'admin');

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const storedUserInfo = localStorage.getItem('userInfo');
    if (storedUserInfo) {
      const parsedUserInfo = JSON.parse(storedUserInfo);
      userInfo.value = parsedUserInfo;
      return;
    }

    const username = localStorage.getItem('username');
    if (!username) {
      throw new Error('No username found');
    }

    const response = await request.get(`/api/users/current?username=${username}`);
    if (response.data && response.data.code === '200') {
      userInfo.value = response.data.data;
      localStorage.setItem('userInfo', JSON.stringify(response.data.data));
    } else {
      handleLogout();
    }
  } catch (err) {
    console.error('Failed to fetch user info:', err);
    handleLogout();
  }
};

// 检查登录状态
const checkLoginStatus = () => {
  const token = localStorage.getItem('token');
  const storedUserInfo = localStorage.getItem('userInfo');
  const username = localStorage.getItem('username');

  if (token && storedUserInfo) {
    isLoggedIn.value = true;
    userInfo.value = JSON.parse(storedUserInfo);
  } else {
    isLoggedIn.value = false;
    userInfo.value = {
      avatar: '',
      name: '',
      role: ''
    };
  }
};

// 处理退出登录
const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');
  localStorage.removeItem('username');
  isLoggedIn.value = false;
  userInfo.value = {
    avatar: '',
    name: '',
    role: ''
  };
};

// 获取头像URL
const getAvatarUrl = (avatar) => {
  if (!avatar) return '';
  if (avatar.startsWith('http')) return avatar;
  if (avatar.startsWith('data:image')) return avatar;
  return `${config.imageBaseURL}/${avatar}`;
};

// 处理用户菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'personalInfo':
      router.push('/hzsca/personalInfo');
      break;
    case 'admin':
      router.push('/hzsca/admin/dashboard');
      break;
    case 'logout':
      ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
      ).then(() => {
        handleLogout();
        ElMessage.success('已退出登录');
        router.push('/hzsca/home');
      }).catch(() => {
      });
      break;
  }
};

// 处理管理员菜单命令
const handleAdminCommand = (command) => {
  switch (command) {
    case 'users':
      router.push('/hzsca/admin/users');
      break;
    case 'cats':
      router.push('/hzsca/admin/cats');
      break;
    case 'catTraits':
      router.push('/hzsca/admin/cat-traits');
      break;
    case 'activities':
      router.push('/hzsca/admin/activities');
      break;
    case 'adoptions':
      router.push('/hzsca/admin/adoptions');
      break;
    case 'rescues':
      router.push('/hzsca/admin/rescues');
      break;
    case 'dataset':
      router.push('/hzsca/admin/dataset');
      break;
  }
};

// 活动菜单命令
const handleActivityCommand = (command) => {
  switch (command) {
    case 'activityback':
      router.push('/hzsca/activity/activityback');
      break;
    case 'activityall':
      router.push('/hzsca/activity/activityall');
      break;
  }
};


// 跳转到登录页
const goToLogin = () => {
  router.push('/hzsca/login');
};

// 监听 localStorage 变化
const handleStorageChange = (e) => {
  if (e.key === 'token') {
    checkLoginStatus();
  }
};

// 更新登录状态
const updateLoginStatus = () => {
  checkLoginStatus();
};

// 暴露更新函数给全局
window.updateLoginStatus = updateLoginStatus;

onMounted(() => {
  checkLoginStatus();
  window.addEventListener('login-success', updateLoginStatus);
  window.addEventListener('storage', handleStorageChange);
});

onUnmounted(() => {
  window.removeEventListener('login-success', updateLoginStatus);
  window.removeEventListener('storage', handleStorageChange);
  delete window.updateLoginStatus;
});
</script>

<style scoped>
.nav-link {
  position: relative;
  text-decoration: none;
  color: #666;
  font-size: 14px;
  padding: 5px 10px;
  transition: color 0.3s ease;
  display: flex;
  align-items: center;
}

.nav-link:hover {
  color: #000;
}

.nav-link::before,
.nav-link::after {
  content: '';
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  opacity: 0;
  transition: width 0.3s ease, opacity 0.3s ease;
  font-size: 1.2em;
  color: #333;
}

.nav-link::before {
  content: '[';
  left: 0;
}

.nav-link::after {
  content: ']';
  right: 0;
}

.nav-link:hover::before,
.nav-link:hover::after {
  width: 10px;
  opacity: 1;
}

.nav-link {
  padding-left: 10px;
  padding-right: 15px;
}

.admin-link {
  cursor: pointer;
}

.avatar-container {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.avatar-container:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

.right-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 下拉菜单图标样式 */
.el-dropdown-menu__item .el-icon {
  margin-right: 8px;
  vertical-align: middle;
}
</style>
