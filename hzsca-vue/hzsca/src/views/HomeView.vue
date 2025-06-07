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
        <div style="display: flex; align-items: center; gap: 10px;">
          <button style="background-color: rgba(255,111,97,0.71); color: #FFF; border: none; padding: 8px 16px; border-radius: 20px; cursor: pointer;">
            立即探索
          </button>
          <button style="background-color: #b78c46; color: #FFF; border: none; padding: 8px 16px; border-radius: 20px; cursor: pointer;"@click="goToPage">
            <el-icon><Avatar /></el-icon>
          </button>
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

const router = useRouter();

const goToPage = () => {
  router.push('/hzsca/login');
};
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
</style>