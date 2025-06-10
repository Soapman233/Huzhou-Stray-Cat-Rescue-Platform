<template>
  <div class="detail-container">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="user-detail">
      <div class="image-section">
        <el-upload
            v-if="isEditing"
            class="avatar-uploader"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
        >
          <img v-if="userInfo.avatar" :src="getImageUrl(userInfo.avatar)" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <img
            v-else
            :src="getImageUrl(userInfo.avatar)"
            :alt="userInfo.name"
            class="avatar"
        >
      </div>

      <div class="info-section">
        <div class="header-actions">
          <h1>个人信息</h1>
          <div class="action-buttons">
            <el-button v-if="!isEditing" type="primary" @click="startEditing">编辑信息</el-button>
            <template v-else>
              <el-button type="success" @click="saveChanges">保存</el-button>
              <el-button @click="cancelEditing">取消</el-button>
            </template>
          </div>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <strong>用户名：</strong>
            <span>{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <strong>昵称：</strong>
            <template v-if="isEditing">
              <el-input v-model="editForm.name" placeholder="请输入昵称" />
            </template>
            <span v-else>{{ userInfo.name }}</span>
          </div>
          <div class="info-item">
            <strong>电话：</strong>
            <template v-if="isEditing">
              <el-input v-model="editForm.phone" placeholder="请输入电话号码" />
            </template>
            <span v-else>{{ userInfo.phone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <strong>性别：</strong>
            <template v-if="isEditing">
              <el-select v-model="editForm.gender" placeholder="请选择性别">
                <el-option :value="1" label="男" />
                <el-option :value="2" label="女" />
              </el-select>
            </template>
            <span v-else>{{ userInfo.gender === 1 ? '男' : '女' }}</span>
          </div>
          <div class="info-item">
            <strong>权限：</strong>
            <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'primary'">
              {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </div>
        </div>

        <div class="description">
          <h2>个人简介</h2>
          <template v-if="isEditing">
            <el-input
                v-model="editForm.profile"
                type="textarea"
                :rows="4"
                placeholder="请输入个人简介"
            />
          </template>
          <p v-else>{{ userInfo.profile || '暂无简介' }}</p>
        </div>

        <div class="danger-zone">
          <h2>危险操作</h2>
          <div class="danger-buttons">
            <el-button type="danger" @click="showDeleteConfirm">注销账户</el-button>
            <el-button type="warning" @click="handleLogout">退出登录</el-button>
          </div>
        </div>

        <div class="action-footer">
          <el-button @click="goBack">返回</el-button>
        </div>
      </div>
    </div>
  </div>

  <!-- 注销确认对话框 -->
  <el-dialog
      v-model="deleteDialogVisible"
      title="确认注销账户"
      width="30%"
  >
    <span>注销账户后，所有数据将被永久删除且无法恢复。确定要继续吗？</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleDeleteAccount">确定注销</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../utils/request';
import config from '../config/config';
import { Plus } from '@element-plus/icons-vue';

const router = useRouter();
const loading = ref(true);
const error = ref(null);
const isEditing = ref(false);
const deleteDialogVisible = ref(false);

// 用户信息
const userInfo = ref({
  UserID: '',
  username: '',
  name: '',
  phone: '',
  gender: 1,
  profile: '',
  role: 'user',
  avatar: ''
});

// 编辑表单
const editForm = ref({
  name: '',
  phone: '',
  gender: 1,
  profile: '',
  avatar: ''
});

// 获取用户信息
const fetchUserInfo = async () => {
  loading.value = true;
  error.value = null;
  try {
    // 从 localStorage 获取用户信息
    const storedUserInfo = localStorage.getItem('userInfo');
    if (!storedUserInfo) {
      throw new Error('未找到用户信息，请重新登录');
    }

    const parsedUserInfo = JSON.parse(storedUserInfo);
    console.log('Loaded user info from storage:', parsedUserInfo);  // 调试日志

    if (!parsedUserInfo || !parsedUserInfo.username) {
      throw new Error('用户信息无效，请重新登录');
    }

    // 更新用户信息，确保所有字段都有默认值
    userInfo.value = {
      UserID: parsedUserInfo.UserID || '',
      username: parsedUserInfo.username,
      name: parsedUserInfo.name || parsedUserInfo.username,
      phone: parsedUserInfo.phone || '',
      gender: parsedUserInfo.gender || 1,
      profile: parsedUserInfo.profile || '',
      role: parsedUserInfo.role || 'user',
      avatar: parsedUserInfo.avatar || ''
    };

    // 初始化编辑表单
    Object.assign(editForm.value, {
      name: userInfo.value.name,
      phone: userInfo.value.phone,
      gender: userInfo.value.gender,
      profile: userInfo.value.profile,
      avatar: userInfo.value.avatar
    });

    // 从服务器获取最新信息
    try {
      const response = await request.get(`/api/users/current?username=${userInfo.value.username}`);
      console.log('Server response:', response);  // 调试日志

      if (response.data && response.data.code === '200' && response.data.data) {
        const serverUserInfo = response.data.data;
        // 合并服务器返回的数据，保留本地数据作为默认值
        const updatedUserInfo = {
          ...userInfo.value,
          UserID: serverUserInfo.UserID || serverUserInfo.userId || serverUserInfo.id || userInfo.value.UserID,
          name: serverUserInfo.name || userInfo.value.name,
          phone: serverUserInfo.phone || userInfo.value.phone,
          gender: serverUserInfo.gender || userInfo.value.gender,
          profile: serverUserInfo.profile || userInfo.value.profile,
          avatar: serverUserInfo.avatar || userInfo.value.avatar,
          role: serverUserInfo.role || userInfo.value.role
        };

        // 更新状态和存储
        userInfo.value = updatedUserInfo;
        localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo));

        // 更新编辑表单
        Object.assign(editForm.value, {
          name: updatedUserInfo.name,
          phone: updatedUserInfo.phone,
          gender: updatedUserInfo.gender,
          profile: updatedUserInfo.profile,
          avatar: updatedUserInfo.avatar
        });
      }
    } catch (serverErr) {
      console.warn('Failed to fetch server data:', serverErr);
      // 继续使用本地数据
    }
  } catch (err) {
    console.error('Error in fetchUserInfo:', err);
    error.value = err.message;
    ElMessage.error(err.message);
    // 清除无效的用户信息并重定向到登录页
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('username');
    router.push('/login');
  } finally {
    loading.value = false;
  }
};

// 获取图片URL
const getImageUrl = (avatar) => {
  if (!avatar) return '/src/assets/default-avatar.png';
  if (avatar.startsWith('http')) return avatar;
  if (avatar.startsWith('data:image')) return avatar;
  return `${config.imageBaseURL}/${avatar}`;
};

// 开始编辑
const startEditing = () => {
  isEditing.value = true;
  Object.assign(editForm.value, {
    name: userInfo.value.name,
    phone: userInfo.value.phone,
    gender: userInfo.value.gender,
    profile: userInfo.value.profile
  });
};

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false;
  editForm.value = {
    name: userInfo.value.name,
    phone: userInfo.value.phone,
    gender: userInfo.value.gender,
    profile: userInfo.value.profile
  };
};

// 保存修改
const saveChanges = async () => {
  try {
    // 获取最新的用户信息
    const currentUserInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!currentUserInfo || !currentUserInfo.username) {
      throw new Error('No valid user information found');
    }

    // 构建更新数据
    const updateData = {
      username: currentUserInfo.username,
      name: editForm.value.name,
      phone: editForm.value.phone,
      gender: editForm.value.gender,
      profile: editForm.value.profile,
      avatar: editForm.value.avatar || currentUserInfo.avatar
    };

    console.log('Updating user with data:', updateData);

    const response = await request.put('/api/users/update', updateData);
    console.log('Update response:', response);

    if (response.data && response.data.code === '200') {
      // 更新成功
      const updatedUserInfo = {
        ...currentUserInfo,
        ...updateData
      };

      // 更新本地存储和状态
      localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo));
      userInfo.value = updatedUserInfo;

      ElMessage.success('保存成功');
      isEditing.value = false;
    } else {
      throw new Error(response.data?.msg || '保存失败');
    }
  } catch (err) {
    console.error('Error saving changes:', err);
    ElMessage.error(err.message || '保存失败');
  }
};

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJpgOrPng) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }
  return true;
};

// 头像上传成功的回调
const handleAvatarSuccess = (response) => {
  editForm.value.avatar = response.url;
  userInfo.value.avatar = response.url;
};

// 显示删除确认对话框
const showDeleteConfirm = () => {
  deleteDialogVisible.value = true;
};

// 处理账户注销
const handleDeleteAccount = async () => {
  try {
    if (!userInfo.value.username) {
      throw new Error('Username not found');
    }

    const response = await request.delete(`/api/users/delete?username=${userInfo.value.username}`);

    if (response.data && response.data.code === '200') {
      ElMessage.success('账户已注销');
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      router.push('/login');
    } else {
      throw new Error(response.data?.msg || 'Failed to delete account');
    }
  } catch (err) {
    console.error('Error deleting account:', err);
    ElMessage.error(err.response?.data?.msg || '注销失败');
  }
  deleteDialogVisible.value = false;
};

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除本地存储的用户信息
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('username');

    // 触发登录状态更新
    window.dispatchEvent(new Event('login-success'));

    // 更新全局登录状态
    if (window.updateLoginStatus) {
      window.updateLoginStatus();
    }

    // 显示成功消息
    ElMessage.success('已成功退出登录');

    // 跳转到登录页
    router.push('/login');
  }).catch(() => {
    // 用户取消退出登录
  });
};

// 返回上一页
const goBack = () => {
  router.back();
};

onMounted(fetchUserInfo);
</script>

<style scoped>
.detail-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
}

.error {
  color: #dc3545;
}

.user-detail {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 2rem;
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-section {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.avatar {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  background-color: #f5f5f5;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.info-section {
  padding: 1rem;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header-actions h1 {
  margin: 0;
  color: #1E4A6D;
  font-size: 2rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.info-item {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.description {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}

.description h2 {
  color: #1E4A6D;
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.danger-zone {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}

.danger-zone h2 {
  color: #dc3545;
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.danger-buttons {
  display: flex;
  gap: 1rem;
}

.action-footer {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .user-detail {
    grid-template-columns: 1fr;
  }

  .image-section {
    margin-bottom: 2rem;
  }

  .header-actions {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .danger-buttons {
    flex-direction: column;
  }

  .danger-buttons .el-button {
    width: 100%;
    margin-bottom: 0.5rem;
  }
}
</style>