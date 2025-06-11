<template>
  <div class="cat-management">
    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <el-input
            v-model="searchQuery"
            placeholder="搜索猫咪名称/品种"
            clearable
            @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加猫咪</el-button>
      </div>
    </div>

    <!-- 猫咪列表 -->
    <el-table
        v-loading="loading"
        :data="filteredCats"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="breed" label="品种" min-width="120" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">
          {{ row.gender === 1 ? '公' : '母' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'available' ? 'success' : 'info'">
            {{ row.status === 'available' ? '待领养' : '已领养' }}
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

    <!-- 猫咪表单对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '添加猫咪' : '编辑猫咪'"
        width="500px"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="cat-form"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="0" :max="20" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option :value="1" label="公" />
            <el-option :value="2" label="母" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option value="available" label="待领养" />
            <el-option value="adopted" label="已领养" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-upload
              class="avatar-uploader"
              action="/api/upload"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
          >
            <img v-if="form.image" :src="form.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
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
const cats = ref([])
const selectedCats = ref([])

// 表单相关
const formRef = ref(null)
const form = ref({
  name: '',
  breed: '',
  age: 0,
  gender: 1,
  status: 'available',
  description: '',
  image: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入猫咪名称', trigger: 'blur' }
  ],
  breed: [
    { required: true, message: '请输入猫咪品种', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入猫咪年龄', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择猫咪性别', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择猫咪状态', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入猫咪描述', trigger: 'blur' }
  ]
}

// 计算属性：过滤后的猫咪列表
const filteredCats = computed(() => {
  if (!searchQuery.value) return cats.value
  const query = searchQuery.value.toLowerCase()
  return cats.value.filter(cat =>
      cat.name.toLowerCase().includes(query) ||
      cat.breed.toLowerCase().includes(query)
  )
})

// 获取猫咪列表
const fetchCats = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/cats')
    if (response.data && response.data.code === '200' && Array.isArray(response.data.data)) {
      cats.value = response.data.data
      total.value = response.data.data.length
    } else {
      cats.value = []
      total.value = 0
    }
  } catch (error) {
    ElMessage.error('获取猫咪列表失败')
    console.error('Failed to fetch cats:', error)
  } finally {
    loading.value = false
  }
}

// 处理添加猫咪
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    name: '',
    breed: '',
    age: 0,
    gender: 1,
    status: 'available',
    description: '',
    image: ''
  }
  dialogVisible.value = true
}

// 处理编辑猫咪
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 处理删除猫咪
const handleDelete = (row) => {
  ElMessageBox.confirm(
      '确定要删除该猫咪吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await request.delete(`/api/cats/delete?id=${row.id}`)
      ElMessage.success('删除成功')
      fetchCats()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('Failed to delete cat:', error)
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
          await request.post('/api/cats/add', form.value)
          ElMessage.success('添加成功')
        } else {
          await request.put('/api/cats/update', form.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchCats()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
        console.error('Failed to submit form:', error)
      }
    }
  })
}

// 处理图片上传
const handleUploadSuccess = (response) => {
  if (response.code === '200') {
    form.value.image = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
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
  selectedCats.value = val
}

// 页面加载时获取猫咪列表
onMounted(() => {
  fetchCats()
})
</script>

<style scoped>
.cat-management {
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

.cat-form {
  max-height: 500px;
  overflow-y: auto;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
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