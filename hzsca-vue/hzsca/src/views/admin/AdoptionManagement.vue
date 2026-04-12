<template>
  <div class="adoption-management">
    <div class="page-header">
      <h2>领养申请管理</h2>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 160px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已取消" value="CANCELED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table v-loading="loading" :data="adoptionList" border style="width: 100%">
        <el-table-column prop="requestId" label="申请ID" min-width="90" />
        <el-table-column prop="wxNickname" label="申请人" min-width="120" />
        <el-table-column prop="animalName" label="猫咪" min-width="120" />
        <el-table-column prop="animalStatus" label="猫咪状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="getAnimalStatusType(row.animalStatus)">
              {{ getAnimalStatusText(row.animalStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" min-width="90" />
        <el-table-column prop="riskLevel" label="风险等级" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelType(row.riskLevel)">
              {{ row.riskLevel || '未计算' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="申请状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" min-width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="210" fixed="right">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
              <el-button v-if="row.status === 'PENDING'" type="success" size="small" @click="handleApprove(row)">通过</el-button>
              <el-button v-if="row.status === 'PENDING'" type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

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

    <el-dialog v-model="dialogVisible" title="申请详情" width="70%">
      <div v-if="currentApplication" class="application-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请ID">{{ currentApplication.request?.id }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusType(currentApplication.request?.status)">
              {{ getStatusText(currentApplication.request?.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentApplication.wxUser?.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="猫咪名称">{{ currentApplication.cat?.Name || currentApplication.cat?.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="评分">{{ currentApplication.request?.score ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">
            <el-tag :type="getRiskLevelType(currentApplication.request?.riskLevel)">
              {{ currentApplication.request?.riskLevel || '未计算' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="风险解释" :span="2">
            {{ currentApplication.request?.riskReasons || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="家庭收入">{{ formatIncome(currentApplication.form?.familyIncome) }}</el-descriptions-item>
          <el-descriptions-item label="养宠经验">{{ formatBoolean(currentApplication.form?.hasOwnedPets) }}</el-descriptions-item>
          <el-descriptions-item label="吸烟">{{ formatBoolean(currentApplication.form?.smoker) }}</el-descriptions-item>
          <el-descriptions-item label="哮喘">{{ formatBoolean(currentApplication.form?.asthma) }}</el-descriptions-item>
          <el-descriptions-item label="过敏">{{ formatBoolean(currentApplication.form?.allergy) }}</el-descriptions-item>
          <el-descriptions-item label="居住空间">{{ currentApplication.form?.livingSpace || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工作时长">{{ currentApplication.form?.workHours ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="接受回访">{{ formatBoolean(currentApplication.form?.acceptReturnVisit) }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentApplication.form?.remarks || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核原因" :span="2">{{ currentApplication.request?.auditReason || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button v-if="currentApplication?.request?.status === 'PENDING'" type="success" @click="handleApprove(currentApplication.request)">
            通过申请
          </el-button>
          <el-button v-if="currentApplication?.request?.status === 'PENDING'" type="danger" @click="handleReject(currentApplication.request)">
            拒绝申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const adoptionList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentApplication = ref(null)
const searchForm = ref({
  status: ''
})

const fetchAdoptionList = async () => {
  loading.value = true
  try {
    const response = await request.get('/admin/adoption-requests', {
      params: {
        status: searchForm.value.status || '',
        page: currentPage.value,
        size: pageSize.value
      }
    })

    if (response.data?.code === '200') {
      const data = response.data.data || {}
      adoptionList.value = data.records || []
      total.value = data.total || 0
      currentPage.value = data.current || 1
      pageSize.value = data.size || 10
      return
    }

    ElMessage.error(response.data?.msg || '获取领养申请列表失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || error.message || '获取领养申请列表失败')
  } finally {
    loading.value = false
  }
}

const fetchDetail = async (requestId) => {
  const response = await request.get(`/admin/adoption-requests/${requestId}`)
  if (response.data?.code === '200') {
    currentApplication.value = response.data.data
    dialogVisible.value = true
    return
  }
  ElMessage.error(response.data?.msg || '获取详情失败')
}

const handleView = async (row) => {
  await fetchDetail(row.requestId)
}

const handleApprove = async (row) => {
  const requestId = row.requestId || row.id
  try {
    const { value } = await ElMessageBox.prompt('请输入审核备注（可选）', '通过申请', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '通过原因/备注'
    })
    const response = await request.post(`/admin/adoption-requests/${requestId}/approve`, {
      reason: value || ''
    })
    if (response.data?.code === '200') {
      ElMessage.success('已通过申请')
      dialogVisible.value = false
      await fetchAdoptionList()
      return
    }
    ElMessage.error(response.data?.msg || '通过申请失败')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.msg || error.message || '通过申请失败')
    }
  }
}

const handleReject = async (row) => {
  const requestId = row.requestId || row.id
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝申请', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '拒绝原因',
      inputValidator: (val) => !!val || '请填写拒绝原因'
    })
    const response = await request.post(`/admin/adoption-requests/${requestId}/reject`, {
      reason: value
    })
    if (response.data?.code === '200') {
      ElMessage.success('已拒绝申请')
      dialogVisible.value = false
      await fetchAdoptionList()
      return
    }
    ElMessage.error(response.data?.msg || '拒绝申请失败')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.msg || error.message || '拒绝申请失败')
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchAdoptionList()
}

const resetSearch = () => {
  searchForm.value.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAdoptionList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAdoptionList()
}

const formatIncome = (income) => {
  if (income === null || income === undefined || income === '') return '未填写'
  return `¥${Number(income).toLocaleString()}`
}

const formatBoolean = (value) => value === true ? '是' : value === false ? '否' : '-'

const formatDateTime = (value) => value ? new Date(value).toLocaleString() : '-'

const getStatusType = (status) => {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  if (status === 'PENDING') return 'warning'
  return 'info'
}

const getStatusText = (status) => {
  if (status === 'APPROVED') return '已通过'
  if (status === 'REJECTED') return '已拒绝'
  if (status === 'PENDING') return '待审核'
  if (status === 'CANCELED') return '已取消'
  return '未知'
}

const getAnimalStatusType = (status) => {
  if (status === 'ADOPTED') return 'success'
  if (status === 'PENDING') return 'warning'
  if (status === 'MEDICAL' || status === 'HOLD') return 'danger'
  return 'info'
}

const getAnimalStatusText = (status) => {
  if (status === 'ADOPTABLE') return '可领养'
  if (status === 'PENDING') return '申请中'
  if (status === 'ADOPTED') return '已领养'
  if (status === 'MEDICAL') return '治疗中'
  if (status === 'HOLD') return '暂缓'
  return status || '未知'
}

const getRiskLevelType = (level) => {
  if (level === 'LOW') return 'success'
  if (level === 'MEDIUM') return 'warning'
  if (level === 'HIGH') return 'danger'
  return 'info'
}

onMounted(() => {
  fetchAdoptionList()
})
</script>

<style scoped>
.adoption-management {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
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

.table-container {
  flex: 1;
  overflow: hidden;
}

:deep(.el-table) {
  height: 100%;
}

:deep(.el-table__body-wrapper) {
  overflow-y: auto;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.application-detail {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.operation-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  white-space: nowrap;
}
</style>
