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
            <router-link to="/hzsca/activity" class="nav-link">活动</router-link>
            <router-link to="/hzsca/report" class="nav-link">工作报告</router-link>
            <router-link to="/hzsca/donate" class="nav-link">捐助我们</router-link>
          </div>
        </div>
        <div class="right-section">
          <el-button v-if="!isLoggedIn" type="primary" class="explore-btn" @click="goToLogin">立即探索</el-button>
          <div class="avatar-container" @click="handleAvatarClick">
            <el-avatar v-if="isLoggedIn && userInfo.avatar" :src="getAvatarUrl(userInfo.avatar)" />
            <el-icon v-else><Avatar /></el-icon>
          </div>
        </div>
      </nav>
    </div>
    <!-- 头部结束 -->

    <!-- 主体开始 -->
    <el-main>
      <router-view></router-view>
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
import { Avatar } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';
import request from '../utils/request';
import config from '../config/config';

const router = useRouter();
const isLoggedIn = ref(false);
const userInfo = ref({
  avatar: '',
  name: ''
});

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // 首先尝试从 localStorage 获取用户信息
    const storedUserInfo = localStorage.getItem('userInfo');
    if (storedUserInfo) {
      const parsedUserInfo = JSON.parse(storedUserInfo);
      userInfo.value = parsedUserInfo;
      return;
    }

    // 如果本地没有用户信息，则从服务器获取
    const username = localStorage.getItem('username');
    if (!username) {
      throw new Error('No username found');
    }

    const response = await request.get(`/api/users/current?username=${username}`);
    if (response.data && response.data.code === '200') {
      userInfo.value = response.data.data;
      // 更新本地存储的用户信息
      localStorage.setItem('userInfo', JSON.stringify(response.data.data));
    } else {
      // 如果获取不到用户信息，可能是token过期或无效
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      localStorage.removeItem('username');
      isLoggedIn.value = false;
    }
  } catch (err) {
    console.error('Failed to fetch user info:', err);
    // 如果请求失败，清除token并更新登录状态
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('username');
    isLoggedIn.value = false;
  }
};

// 检查登录状态
const checkLoginStatus = () => {
  const token = localStorage.getItem('token');
  const storedUserInfo = localStorage.getItem('userInfo');
  const username = localStorage.getItem('username');

  if (token && storedUserInfo && username) {
    isLoggedIn.value = true;
    userInfo.value = JSON.parse(storedUserInfo);
  } else {
    isLoggedIn.value = false;
    userInfo.value = {
      avatar: '',
      name: ''
    };
  }
};

// 获取头像URL
const getAvatarUrl = (avatar) => {
  if (!avatar) return '';
  if (avatar.startsWith('http')) return avatar;
  if (avatar.startsWith('data:image')) return avatar;
  return `${config.imageBaseURL}/${avatar}`;
};

// 处理头像点击
const handleAvatarClick = () => {
  if (isLoggedIn.value) {
    router.push('/hzsca/personalInfo');
  } else {
    router.push('/login');
  }
};

// 跳转到登录页
const goToLogin = () => {
  router.push('/login');
};

// 监听 localStorage 中 token 的变化
const handleStorageChange = (e) => {
  if (e.key === 'token') {
    checkLoginStatus();
  }
};

// 创建一个自定义事件来处理登录状态更新
const updateLoginStatus = () => {
  checkLoginStatus();
};

// 将更新函数暴露给全局，以便其他组件可以调用
window.updateLoginStatus = updateLoginStatus;

onMounted(() => {
  checkLoginStatus();
  // 添加登录成功事件监听
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
</style>