<template>
  <div class="user-management">
    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <el-input
            v-model="searchQuery"
            placeholder="搜索用户名/姓名/电话"
            clearable
            @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加用户</el-button>
      </div>
    </div>

    <!-- 用户列表 -->
    <el-table
        v-loading="loading"
        :data="filteredUsers"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="name" label="姓名" min-width="120" />
      <el-table-column prop="phone" label="电话" min-width="120" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">
          {{ row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'success'">
            {{ row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button-group>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
        width="500px"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="user-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option :value="1" label="男" />
            <el-option :value="2" label="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option value="user" label="普通用户" />
            <el-option value="admin" label="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介" prop="profile">
          <el-input v-model="form.profile" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 状态变量
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const users = ref([])
const selectedUsers = ref([])

// 表单相关
const formRef = ref(null)
const form = ref({
  username: '',
  name: '',
  password: '',
  phone: '',
  gender: 1,
  role: 'user',
  profile: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 计算属性：过滤后的用户列表
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(user =>
      user.username.toLowerCase().includes(query) ||
      user.name.toLowerCase().includes(query) ||
      user.phone.includes(query)
  )
})

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/users')
    if (response.data && response.data.code === '200' && Array.isArray(response.data.data)) {
      users.value = response.data.data
      total.value = response.data.data.length
    } else {
      users.value = []
      total.value = 0
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error('Failed to fetch users:', error)
  } finally {
    loading.value = false
  }
}

// 处理添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    username: '',
    name: '',
    password: '',
    phone: '',
    gender: 1,
    role: 'user',
    profile: ''
  }
  dialogVisible.value = true
}

// 处理编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 处理删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
      '确定要删除该用户吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await request.delete(`/api/users/delete?username=${row.username}`)
      ElMessage.success('删除成功')
      fetchUsers()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('Failed to delete user:', error)
    }
  }).catch(() => {})
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await request.post('/api/auth/register', form.value)
          ElMessage.success('添加成功')
        } else {
          await request.put('/api/users/update', form.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchUsers()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
        console.error('Failed to submit form:', error)
      }
    }
  })
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 处理多选
const handleSelectionChange = (val) => {
  selectedUsers.value = val
}

// 页面加载时获取用户列表
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-box {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.user-form {
  max-height: 500px;
  overflow-y: auto;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    gap: 10px;
  }

  .search-box {
    width: 100%;
  }

  .action-buttons {
    display: flex;
    justify-content: flex-end;
  }
}
</style>