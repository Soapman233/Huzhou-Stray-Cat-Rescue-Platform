<template>
  <div class="volunteer-review">
    <div class="header">
      <div class="title">志愿者申请审核</div>
      <div class="sub">审核通过后用户将获得志愿者身份，可预约参加活动</div>
    </div>

    <div class="action-bar">
      <el-select v-model="status" placeholder="筛选状态" style="width: 180px" @change="reload">
        <el-option label="全部" value="" />
        <el-option label="待审核" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已拒绝" value="REJECTED" />
      </el-select>
      <el-button @click="reload">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="records" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="applicantType" label="来源" width="90" />
      <el-table-column prop="name" label="姓名" min-width="120" />
      <el-table-column prop="phone" label="手机" min-width="120" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="tagType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="爱心经历/原因" min-width="260">
        <template #default="{ row }">
          <div class="text">{{ row.experience || '-' }}</div>
          <div class="text subtext">{{ row.motivation || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
              type="success"
              link
              :disabled="row.status !== 'PENDING'"
              @click="approve(row)"
          >通过</el-button>
          <el-button
              type="danger"
              link
              :disabled="row.status !== 'PENDING'"
              @click="reject(row)"
          >拒绝</el-button>
          <el-button type="primary" link @click="openParticipants">查看报名</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="reload"
          @current-change="reload"
      />
    </div>

    <el-dialog v-model="participantsVisible" title="活动报名人员" width="840px">
      <div class="participants-actions">
        <el-input v-model="activityId" placeholder="输入活动ID" style="width: 220px" />
        <el-select v-model="pStatus" placeholder="筛选状态" style="width: 160px">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="fetchParticipants">查询</el-button>
      </div>
      <el-table :data="participants" border style="width: 100%" v-loading="participantsLoading">
        <el-table-column prop="activityId" label="活动ID" width="90" />
        <el-table-column prop="participantType" label="来源" width="90" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="tagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="用户" min-width="220">
          <template #default="{ row }">
            <div>{{ row.user?.nickname || row.user?.name || row.user?.username || '-' }}</div>
            <div class="subtext">ID: {{ row.participantId }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="报名时间" min-width="160" />
        <el-table-column label="审核" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
                type="success"
                link
                :disabled="row.status !== 'PENDING'"
                @click="approveParticipation(row)"
            >通过</el-button>
            <el-button
                type="danger"
                link
                :disabled="row.status !== 'PENDING'"
                @click="rejectParticipation(row)"
            >拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const status = ref('')

const participantsVisible = ref(false)
const participantsLoading = ref(false)
const activityId = ref('')
const pStatus = ref('')
const participants = ref([])

const tagType = (s) => {
  if (s === 'PENDING') return 'warning'
  if (s === 'APPROVED') return 'success'
  if (s === 'REJECTED') return 'danger'
  return 'info'
}

const reload = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/admin/volunteers/applications', {
      params: { status: status.value || undefined, page: page.value, size: size.value }
    })
    if (res.data?.code === '200') {
      records.value = res.data.data.records || []
      total.value = res.data.data.total || 0
      return
    }
    ElMessage.error(res.data?.msg || '加载失败')
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const approve = async (row) => {
  try {
    await ElMessageBox.confirm('通过后用户将获得志愿者身份，是否继续？', '确认', {
      confirmButtonText: '通过',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch (e) {
    return
  }
  try {
    const res = await request.post(`/api/admin/volunteers/applications/${row.id}/approve`)
    if (res.data?.code === '200') {
      ElMessage.success('已通过')
      await reload()
      return
    }
    ElMessage.error(res.data?.msg || '操作失败')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const reject = async (row) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该申请吗？', '确认', {
      confirmButtonText: '拒绝',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch (e) {
    return
  }
  try {
    const res = await request.post(`/api/admin/volunteers/applications/${row.id}/reject`)
    if (res.data?.code === '200') {
      ElMessage.success('已拒绝')
      await reload()
      return
    }
    ElMessage.error(res.data?.msg || '操作失败')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const openParticipants = () => {
  participantsVisible.value = true
  participants.value = []
  participantsLoading.value = false
}

const fetchParticipants = async () => {
  const id = Number(activityId.value)
  if (!Number.isFinite(id) || id <= 0) {
    ElMessage.error('请输入有效活动ID')
    return
  }
  participantsLoading.value = true
  try {
    const res = await request.get(`/api/admin/volunteers/activities/${id}/participants`, {
      params: { status: pStatus.value || undefined }
    })
    if (res.data?.code === '200') {
      participants.value = res.data.data.records || []
      return
    }
    ElMessage.error(res.data?.msg || '查询失败')
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    participantsLoading.value = false
  }
}

const approveParticipation = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过该报名申请？', '确认', {
      confirmButtonText: '通过',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch (e) {
    return
  }
  try {
    const res = await request.post(`/api/admin/volunteers/participations/${row.id}/approve`)
    if (res.data?.code === '200') {
      ElMessage.success('已通过')
      await fetchParticipants()
      return
    }
    ElMessage.error(res.data?.msg || '操作失败')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const rejectParticipation = async (row) => {
  try {
    await ElMessageBox.confirm('确认拒绝该报名申请？', '确认', {
      confirmButtonText: '拒绝',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch (e) {
    return
  }
  try {
    const res = await request.post(`/api/admin/volunteers/participations/${row.id}/reject`)
    if (res.data?.code === '200') {
      ElMessage.success('已拒绝')
      await fetchParticipants()
      return
    }
    ElMessage.error(res.data?.msg || '操作失败')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  reload()
})
</script>

<style scoped>
.header {
  padding: 14px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #E6DCC8, #f5f7fa);
  margin-bottom: 12px;
}
.title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}
.sub {
  margin-top: 6px;
  font-size: 13px;
  color: #4b5563;
}
.action-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
.text {
  color: #334155;
  line-height: 1.5;
}
.subtext {
  margin-top: 4px;
  color: #64748b;
  font-size: 12px;
}
.participants-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}
</style>
