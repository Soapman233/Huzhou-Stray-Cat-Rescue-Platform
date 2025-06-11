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
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image
              v-if="row.ImageURL"
              :src="getImageUrl(row.ImageURL)"
              :preview-src-list="[getImageUrl(row.ImageURL)]"
              fit="cover"
              style="width: 50px; height: 50px; border-radius: 4px;"
              @error="(e) => {
              console.error('Image load error:', e);
              e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WbvueJhzwvdGV4dD48L3N2Zz4=';
            }"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <el-icon v-else><Picture /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="Name" label="名称" min-width="120" />
      <el-table-column prop="Breed" label="品种" min-width="120" />
      <el-table-column prop="Age" label="年龄" width="80" />
      <el-table-column prop="Gender" label="性别" width="80">
        <template #default="{ row }">
          {{ row.Gender === '雄性' ? '公' : '母' }}
        </template>
      </el-table-column>
      <el-table-column prop="Source" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.Source === '流浪' ? 'success' : 'info'">
            {{ row.Source === '流浪' ? '待领养' : '已领养' }}
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
          <el-input-number v-model="form.age" :min="0" :max="20" :precision="1" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option value="雄性" label="公" />
            <el-option value="雌性" label="母" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="source">
          <el-select v-model="form.source" placeholder="请选择状态">
            <el-option value="流浪" label="待领养" />
            <el-option value="家养" label="已领养" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康状况" prop="healthStatus">
          <el-input v-model="form.healthStatus" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <el-upload
              class="avatar-uploader"
              :action="baseUrl + '/api/cats/upload'"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              :headers="getAuthHeaders()"
          >
            <img v-if="form.imageUrl" :src="getImageUrl(form.imageUrl)" class="avatar" />
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { Search, Plus, Picture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 添加基础URL配置
const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 处理图片URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('data:image')) return url
  // 确保路径格式正确，移除开头的斜杠
  const cleanUrl = url.startsWith('/') ? url.substring(1) : url
  return `${baseUrl}/${cleanUrl}`
}

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
  animalId: null,
  name: '',
  breed: '',
  age: 0,
  gender: '雄性',
  source: '流浪',
  healthStatus: '健康',
  description: '',
  imageUrl: '',
  species: '猫'
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
  source: [
    { required: true, message: '请选择猫咪状态', trigger: 'change' }
  ],
  healthStatus: [
    { required: true, message: '请输入健康状况', trigger: 'blur' }
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
      (cat.Name || '').toLowerCase().includes(query) ||
      (cat.Breed || '').toLowerCase().includes(query)
  )
})

// 获取猫咪列表
const fetchCats = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/cats')
    console.log('Cat list response:', response)  // 添加日志
    if (response.data && response.data.code === '200') {
      // 适配后端返回的数据格式
      const catList = Array.isArray(response.data.data) ? response.data.data :
          (response.data.data && Array.isArray(response.data.data.data) ? response.data.data.data : [])
      cats.value = catList
      total.value = catList.length
      console.log('Processed cat list:', cats.value)  // 添加日志
    } else {
      cats.value = []
      total.value = 0
      console.error('Invalid response format:', response.data)  // 添加日志
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
    gender: '雄性',
    source: '流浪',
    healthStatus: '健康',
    description: '',
    imageUrl: '',
    species: '猫'
  }
  dialogVisible.value = true
}

// 处理编辑猫咪
const handleEdit = (row) => {
  try {
    console.log('Editing cat row data:', row)  // 添加详细的行数据日志

    if (!row) {
      console.error('No row data provided for editing')
      ElMessage.error('编辑失败：没有数据')
      return
    }

    dialogType.value = 'edit'

    // 保存原始 AnimalID 用于更新操作
    form.value = {
      animalId: row.AnimalID, // 添加 animalId 字段
      name: row.Name || '',
      breed: row.Breed || '',
      age: parseFloat(row.Age) || 0, // 确保 age 是数字类型
      gender: row.Gender || '雄性',
      source: row.Source || '流浪',
      healthStatus: row.HealthStatus || '健康',
      description: row.Description || '',
      imageUrl: row.ImageURL || '',
      species: row.Species || '猫'
    }

    console.log('Form data after mapping:', form.value)  // 添加表单数据日志

    // 确保必要字段存在
    if (!form.value.animalId) {
      console.warn('Warning: AnimalID is missing in the form data')
    }

    dialogVisible.value = true

    // 在下一个 tick 中检查对话框是否正确打开
    nextTick(() => {
      if (!dialogVisible.value) {
        console.error('Dialog failed to open')
        ElMessage.error('打开编辑窗口失败')
      }
    })
  } catch (error) {
    console.error('Error in handleEdit:', error)
    ElMessage.error('编辑操作失败')
  }
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
      await request.delete(`/api/cats/delete?id=${row.AnimalID}`)
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
        // 构建提交数据，确保字段名与后端一致
        const submitData = {
          Name: form.value.name,
          Breed: form.value.breed,
          Age: form.value.age,
          Gender: form.value.gender,
          Source: form.value.source,
          HealthStatus: form.value.healthStatus,
          Description: form.value.description,
          ImageURL: form.value.imageUrl,
          Species: form.value.species
        }

        if (dialogType.value === 'add') {
          console.log('Adding new cat:', submitData)
          await request.post('/api/cats/add', submitData)
          ElMessage.success('添加成功')
        } else {
          // 编辑时添加 AnimalID
          submitData.AnimalID = form.value.animalId // 使用保存的 animalId
          console.log('Updating cat:', submitData)
          await request.put('/api/cats/update', submitData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchCats()
      } catch (error) {
        console.error('Submit error:', error.response?.data || error)
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    } else {
      console.log('Form validation failed')
    }
  })
}

// 处理图片上传
const handleUploadSuccess = (response) => {
  if (response.code === '200') {
    form.value.imageUrl = response.data
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

// 添加 token 获取函数
const getAuthHeaders = () => {
  const token = localStorage?.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
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

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 20px;
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