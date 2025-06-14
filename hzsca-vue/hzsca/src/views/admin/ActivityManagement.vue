<template>
  <div class="activity-management">
    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <el-input
            v-model="searchQuery"
            placeholder="搜索活动标题"
            clearable
            @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加活动</el-button>
      </div>
    </div>

    <!-- 活动列表 -->
    <el-table
        v-loading="loading"
        :data="filteredActivities"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image
              v-if="row.imgurl"
              :src="getImageUrl(row.imgurl)"
              :preview-src-list="[getImageUrl(row.imgurl)]"
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
      <el-table-column prop="title" label="标题" min-width="120" />
      <el-table-column prop="date" label="时间" min-width="160">
        <template #default="{ row }">
          {{ new Date(row.date).toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="Location" label="地点" min-width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
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

    <!-- 活动表单对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '添加活动' : '编辑活动'"
        width="500px"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="activity-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="时间" prop="date">
          <el-date-picker
              v-model="form.date"
              type="datetime"
              placeholder="选择活动时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="地点" prop="Location">
          <el-input v-model="form.Location" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择活动状态">
            <el-option value="upcoming" label="即将开始" />
            <el-option value="ongoing" label="进行中" />
            <el-option value="ended" label="已结束" />
          </el-select>
        </el-form-item>
        <el-form-item label="详情" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片" prop="imgurl">
          <el-upload
              class="avatar-uploader"
              :action="baseUrl + '/api/activity/upload'"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              :headers="getAuthHeaders()"
          >
            <img v-if="form.imgurl" :src="getImageUrl(form.imgurl)" class="avatar" />
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
const activities = ref([])
const selectedActivities = ref([])

// 表单相关
const formRef = ref(null)
const form = ref({
  id: null,
  title: '',
  date: '',
  Location: '',
  status: 'upcoming',
  detail: '',
  imgurl: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择活动时间', trigger: 'change' }
  ],
  Location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择活动状态', trigger: 'change' }
  ],
  detail: [
    { required: true, message: '请输入活动详情', trigger: 'blur' }
  ]
}

// 状态类型和文本
const getStatusType = (status) => {
  const types = {
    'upcoming': 'warning',
    'ongoing': 'success',
    'ended': 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'upcoming': '即将开始',
    'ongoing': '进行中',
    'ended': '已结束'
  }
  return texts[status] || status
}

// 计算属性：过滤后的活动列表
const filteredActivities = computed(() => {
  if (!searchQuery.value) return activities.value
  const query = searchQuery.value.toLowerCase()
  return activities.value.filter(activity =>
      activity.title.toLowerCase().includes(query)
  )
})

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/activity/list')
    if (response.data && response.data.code === '200') {
      activities.value = response.data.data || []
      total.value = activities.value.length
    } else {
      activities.value = []
      total.value = 0
      console.error('Invalid response format:', response.data)
    }
  } catch (error) {
    ElMessage.error('获取活动列表失败')
    console.error('Failed to fetch activities:', error)
  } finally {
    loading.value = false
  }
}

// 处理添加活动
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    id: null,
    title: '',
    date: '',
    Location: '',
    status: 'upcoming',
    detail: '',
    imgurl: ''
  }
  dialogVisible.value = true
}

// 处理编辑活动
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = {
    id: row.id,
    title: row.title,
    date: row.date,
    Location: row.Location,
    status: row.status,
    detail: row.detail,
    imgurl: row.imgurl
  }
  dialogVisible.value = true
}

// 处理删除活动
const handleDelete = (row) => {
  ElMessageBox.confirm(
      '确定要删除该活动吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await request.delete(`/api/activity/delete/${row.id}`)
      ElMessage.success('删除成功')
      fetchActivities()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('Failed to delete activity:', error)
    }
  }).catch(() => {})
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          id: form.value.id,
          title: form.value.title,
          date: form.value.date,
          Location: form.value.Location,
          status: form.value.status,
          detail: form.value.detail,
          imgurl: form.value.imgurl
        }

        if (dialogType.value === 'add') {
          await request.post('/api/activity/add', submitData)
          ElMessage.success('添加成功')
        } else {
          await request.put('/api/activity/update', submitData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchActivities()
      } catch (error) {
        console.error('Submit error:', error.response?.data || error)
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }
  })
}

// 处理图片上传
const handleUploadSuccess = (response) => {
  if (response.code === '200') {
    form.value.imgurl = response.data
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
  selectedActivities.value = val
}

// 获取认证头信息
const getAuthHeaders = () => {
  const token = localStorage?.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
}

// 页面加载时获取活动列表
onMounted(() => {
  fetchActivities()
})
</script>

<style scoped>
.activity-management {
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

.activity-form {
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
  width: 50px;
  height: 50px;
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>