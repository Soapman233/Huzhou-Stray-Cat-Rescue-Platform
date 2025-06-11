<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="form-header">
        <h2>用户登录</h2>
        <p>欢迎回来，请登录您的账号</p>
      </div>
      <form @submit.prevent="handleLogin" class="floating-form">
        <div class="input-group">
          <input id="username" v-model.trim="loginForm.username" type="text" autocomplete="off" @input="validateInput" required />
          <label for="username">用户名</label>
          <span class="highlight"></span>
        </div>
        <div class="input-group">
          <input id="password" v-model.trim="loginForm.password" type="password" autocomplete="off" @input="validateInput" required />
          <label for="password">密码</label>
          <span class="highlight"></span>
        </div>
        <div class="error-message" v-if="errorMsg">{{ errorMsg }}</div>
        <button type="submit" class="submit-btn" :disabled="!isFormValid">
          <span>登录</span>
          <i class="arrow-icon"></i>
        </button>
        <div class="form-footer">
          <span>还没有账号？</span>
          <a href="/hzsca/regist">立即注册</a>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  role: 'user'  // 默认为普通用户，登录时根据用户名判断是否为管理员
})

const errorMsg = ref('')
const isFormValid = ref(false)

// 输入验证
const validateInput = () => {
  // 基本验证
  if (loginForm.username && loginForm.password) {
    isFormValid.value = true
    errorMsg.value = ''
  } else {
    isFormValid.value = false
  }
}

// 登录处理
const handleLogin = async () => {
  // 防止XSS攻击
  const xssPattern = /(~|\{|\}|"|'|<|>|\?)/g;
  if (xssPattern.test(loginForm.username) || xssPattern.test(loginForm.password)) {
    errorMessage('警告:输入内容包含非法字符');
    return;
  }

  try {
    // 先尝试管理员登录
    const adminResponse = await request.post('/api/auth/login', {
      ...loginForm,
      role: 'admin'
    });
    console.log('Admin login response:', adminResponse);

    if (adminResponse.data.success) {
      // 管理员登录成功
      const adminData = adminResponse.data.user;
      console.log('Admin data:', adminData);

      // 存储管理员信息，确保所有必要字段都有值
      const adminInfo = {
        username: adminData.username || adminData.UserName || '',
        UserID: adminData.id || adminData.UserID || adminData.userId || '',
        name: adminData.name || adminData.UserName || adminData.username || '',
        role: 'admin',
        avatar: adminData.avatar || '',
        gender: adminData.gender || 1,
        phone: adminData.phone || '',
        profile: adminData.profile || '',
        email: adminData.email || ''
      };

      // 验证管理员信息完整性
      if (!adminInfo.username || !adminInfo.UserID) {
        console.error('Incomplete admin data:', adminData);
        throw new Error('管理员信息不完整，请联系系统管理员');
      }

      console.log('Storing admin info:', adminInfo);
      localStorage.setItem('userInfo', JSON.stringify(adminInfo));
      localStorage.setItem('token', adminResponse.data.token || 'logged-in');
      localStorage.setItem('username', adminInfo.username);

      ElMessage.success('管理员登录成功');
      window.dispatchEvent(new Event('login-success'));

      if (window.updateLoginStatus) {
        window.updateLoginStatus();
      }

      router.push('/hzsca/admin/users');
      return;
    }

    // 如果不是管理员，尝试普通用户登录
    const userResponse = await request.post('/api/auth/login', {
      ...loginForm,
      role: 'user'
    });
    console.log('User login response:', userResponse);

    if (userResponse.data.success) {
      // 普通用户登录成功
      const userData = userResponse.data.user;
      console.log('Raw user data:', userData);

      // 构建标准化的用户信息对象，确保所有必要字段都有默认值
      const userInfo = {
        username: userData.Username || userData.username || '',  // 优先使用 Username
        UserID: userData.UserID || userData.userID || '',  // 优先使用 UserID
        name: userData.Name || userData.name || userData.Username || userData.username || '',
        role: 'user',
        avatar: userData.Avatar || userData.avatar || '',
        gender: userData.Gender || userData.gender || 1,
        phone: userData.Phone || userData.phone || '',
        profile: userData.Profile || userData.profile || '',
        email: userData.Email || userData.email || ''
      };

      // 打印详细的用户数据信息
      console.log('User data fields:', {
        Username: userData.Username,
        username: userData.username,
        UserID: userData.UserID,
        userID: userData.userID,
        Name: userData.Name,
        name: userData.name
      });

      // 验证必要字段
      if (!userInfo.username) {
        console.error('Missing username in user data:', userData);
        throw new Error('用户名信息缺失，请重试');
      }

      if (!userInfo.UserID) {
        console.error('Missing UserID in user data:', userData);
        throw new Error('用户ID信息缺失，请重试');
      }

      // 存储用户信息前先打印日志
      console.log('Storing user info:', userInfo);

      // 存储用户信息
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
      localStorage.setItem('token', userResponse.data.token || 'logged-in');
      localStorage.setItem('username', userInfo.username);

      // 验证存储是否成功
      const storedInfo = localStorage.getItem('userInfo');
      console.log('Stored user info:', storedInfo);

      ElMessage.success('登录成功');
      window.dispatchEvent(new Event('login-success'));

      if (window.updateLoginStatus) {
        window.updateLoginStatus();
      }

      router.push('/');
    } else {
      throw new Error(userResponse.data.message || '登录失败');
    }
  } catch (error) {
    console.error('Login error details:', error);
    console.error('Login error response:', error.response);
    errorMessage(error.response?.data?.message || error.message || '登录失败，请检查用户名和密码');
  }
};

// 错误提示
const errorMessage = (text) => {
  errorMsg.value = text
  setTimeout(() => {
    errorMsg.value = ''
  }, 3000)
}

onMounted(() => {
  validateInput()
})

</script>

<style scoped>
.login-wrapper {
  min-height: 93.5vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 480px;
  background: white;
  border-radius: 20px;
  padding: 40px;
  padding-right: 80px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  color: #2c3e50;
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 700;
}

.form-header p {
  color: #95a5a6;
  font-size: 16px;
}

.floating-form .input-group {
  position: relative;
  margin-bottom: 30px;
}

.input-group input {
  width: 100%;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: transparent;
}

.input-group label {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  background: white;
  padding: 0 5px;
  color: #95a5a6;
  font-size: 16px;
  transition: all 0.3s ease;
  pointer-events: none;
}

.input-group input:focus,
.input-group input:valid {
  border-color: #3498db;
}

.input-group input:focus + label,
.input-group input:valid + label {
  top: 0;
  font-size: 14px;
  color: #3498db;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  margin-left: 15px;
  background: linear-gradient(to right, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.arrow-icon {
  border: solid white;
  border-width: 0 2px 2px 0;
  display: inline-block;
  padding: 3px;
  transform: rotate(-45deg);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #95a5a6;
}

.form-footer a {
  color: #3498db;
  text-decoration: none;
  margin-left: 5px;
  font-weight: 600;
}

.form-footer a:hover {
  text-decoration: underline;
}

.error-message {
  color: #f56c6c;
  font-size: 14px;
  text-align: center;
  margin-bottom: 20px;
}

@media (max-width: 480px) {
  .login-container {
    padding: 20px;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .input-group input {
    padding: 12px;
  }
}

@media (max-width: 768px) {
  .login-container {
    max-width: 400px;
    padding: 30px;
  }

  .form-header h2 {
    font-size: 28px;
  }

  .form-header p {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 20px;
    margin: 10px;
    max-width: 100%;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .form-header p {
    font-size: 14px;
  }

  .input-group input {
    padding: 12px;
    font-size: 14px;
  }

  .input-group label {
    font-size: 14px;
  }

  .submit-btn {
    padding: 12px;
    font-size: 16px;
  }
}

@media (max-width: 320px) {
  .login-container {
    padding: 15px;
  }

  .form-header h2 {
    font-size: 20px;
  }

  .input-group {
    margin-bottom: 20px;
  }
}
</style>