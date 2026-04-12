<template>
  <div class="rescue-management">
    <div class="page-header">
      <h2>救助上报管理</h2>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 160px">
            <el-option label="待处理" value="OPEN" />
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="loading" :data="reportList" border style="width: 100%">
      <el-table-column prop="reportId" label="编号" min-width="90" />
      <el-table-column prop="wxNickname" label="上报人" min-width="120" />
      <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
      <el-table-column prop="urgency" label="紧急程度" min-width="90" />
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="上报时间" min-width="170">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="210" fixed="right">
        <template #default="{ row }">
          <div class="operation-buttons">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'OPEN'" type="warning" size="small" @click="handleAssign(row)">指派</el-button>
            <el-button v-if="row.status !== 'CLOSED'" type="success" size="small" @click="handleClose(row)">关闭</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <el-dialog v-model="dialogVisible" title="救助上报详情" width="60%">
      <el-descriptions v-if="currentReport" :column="2" border>
        <el-descriptions-item label="编号">{{ currentReport.reportId }}</el-descriptions-item>
        <el-descriptions-item label="上报人">{{ currentReport.wxNickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReport.status)">
            {{ getStatusText(currentReport.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">{{ currentReport.urgency || '-' }}</el-descriptions-item>
        <el-descriptions-item label="经度">{{ currentReport.lng ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="纬度">{{ currentReport.lat ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ currentReport.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentReport.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ currentReport.handleResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="图片" :span="2">{{ currentReport.imageUrls || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const reportList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentReport = ref(null)
const searchForm = ref({
  status: ''
})

const fetchList = async () => {
  loading.value = true
  try {
    const response = await request.get('/admin/rescue-reports', {
      params: {
        status: searchForm.value.status || '',
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (response.data?.code === '200') {
      const data = response.data.data || {}
      reportList.value = data.records || []
      total.value = data.total || 0
      currentPage.value = data.current || 1
      pageSize.value = data.size || 10
      return
    }
    ElMessage.error(response.data?.msg || '获取救助上报列表失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || error.message || '获取救助上报列表失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  currentReport.value = row
  dialogVisible.value = true
}

const handleAssign = async (row) => {
  try {
    await ElMessageBox.confirm('确定指派当前管理员处理该上报吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await request.post(`/admin/rescue-reports/${row.reportId}/assign`, {})
    if (response.data?.code === '200') {
      ElMessage.success('指派成功')
      fetchList()
      return
    }
    ElMessage.error(response.data?.msg || '指派失败')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.msg || error.message || '指派失败')
    }
  }
}

const handleClose = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入处理结果（可选）', '关闭上报', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '处理结果'
    })
    const response = await request.post(`/admin/rescue-reports/${row.reportId}/close`, {
      handleResult: value || ''
    })
    if (response.data?.code === '200') {
      ElMessage.success('关闭成功')
      fetchList()
      dialogVisible.value = false
      return
    }
    ElMessage.error(response.data?.msg || '关闭失败')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.msg || error.message || '关闭失败')
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchList()
}

const resetSearch = () => {
  searchForm.value.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchList()
}

const formatDateTime = (value) => value ? new Date(value).toLocaleString() : '-'

const getStatusType = (status) => {
  if (status === 'OPEN') return 'danger'
  if (status === 'IN_PROGRESS') return 'warning'
  if (status === 'CLOSED') return 'success'
  return 'info'
}

const getStatusText = (status) => {
  if (status === 'OPEN') return '待处理'
  if (status === 'IN_PROGRESS') return '处理中'
  if (status === 'CLOSED') return '已关闭'
  return status || '未知'
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.rescue-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #333;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.operation-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  white-space: nowrap;
}
</style>
