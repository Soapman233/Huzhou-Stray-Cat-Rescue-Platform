<template>
  <div class="dataset-export">
    <div class="page-header">
      <h2>数据导出</h2>
      <p>用于毕业设计实验的数据查看与导出</p>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="行为数据" name="behaviors">
        <el-form :inline="true" :model="behaviorForm" class="search-form">
          <el-form-item label="事件">
            <el-select v-model="behaviorForm.event" placeholder="全部事件" clearable style="width: 180px">
              <el-option label="申请" value="apply" />
              <el-option label="救助上报" value="rescue_report" />
              <el-option label="浏览" value="view" />
              <el-option label="点击" value="click" />
              <el-option label="收藏" value="favorite" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchBehaviors">查询</el-button>
            <el-button @click="exportBehaviors">导出当前结果</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="behaviorLoading" :data="behaviorList" border style="width: 100%">
          <el-table-column prop="behaviorId" label="编号" min-width="90" />
          <el-table-column prop="event" label="事件" min-width="120" />
          <el-table-column prop="wxNickname" label="用户" min-width="120" />
          <el-table-column prop="animalName" label="猫咪" min-width="120" />
          <el-table-column prop="requestStatus" label="申请状态" min-width="100" />
          <el-table-column prop="reportStatus" label="上报状态" min-width="100" />
          <el-table-column prop="extra" label="扩展字段" min-width="180" show-overflow-tooltip />
          <el-table-column prop="createTime" label="时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="领养数据" name="adoptions">
        <el-form :inline="true" :model="adoptionForm" class="search-form">
          <el-form-item label="状态">
            <el-select v-model="adoptionForm.status" placeholder="全部状态" clearable style="width: 180px">
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
              <el-option label="已取消" value="CANCELED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchAdoptions">查询</el-button>
            <el-button @click="exportAdoptions">导出当前结果</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="adoptionLoading" :data="adoptionList" border style="width: 100%">
          <el-table-column prop="requestId" label="申请ID" min-width="100" />
          <el-table-column prop="requestStatus" label="状态" min-width="100" />
          <el-table-column prop="animalName" label="猫咪" min-width="120" />
          <el-table-column prop="wxNickname" label="用户" min-width="120" />
          <el-table-column prop="score" label="评分" min-width="90" />
          <el-table-column prop="riskLevel" label="风险等级" min-width="100" />
          <el-table-column prop="familyIncome" label="收入" min-width="110" />
          <el-table-column prop="workHours" label="工时" min-width="90" />
          <el-table-column prop="applyTime" label="申请时间" min-width="170">
            <template #default="{ row }">
              {{ formatDateTime(row.applyTime) }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('behaviors')

const behaviorLoading = ref(false)
const adoptionLoading = ref(false)
const behaviorList = ref([])
const adoptionList = ref([])
const behaviorForm = ref({ event: '' })
const adoptionForm = ref({ status: '' })

const fetchBehaviors = async () => {
  behaviorLoading.value = true
  try {
    const response = await request.get('/admin/dataset/behaviors', {
      params: {
        event: behaviorForm.value.event || '',
        page: 1,
        size: 500
      }
    })
    if (response.data?.code === '200') {
      behaviorList.value = response.data.data?.records || []
      return
    }
    ElMessage.error(response.data?.msg || '获取行为数据失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || error.message || '获取行为数据失败')
  } finally {
    behaviorLoading.value = false
  }
}

const fetchAdoptions = async () => {
  adoptionLoading.value = true
  try {
    const response = await request.get('/admin/dataset/adoptions', {
      params: {
        status: adoptionForm.value.status || '',
        page: 1,
        size: 500
      }
    })
    if (response.data?.code === '200') {
      adoptionList.value = response.data.data?.records || []
      return
    }
    ElMessage.error(response.data?.msg || '获取领养数据失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || error.message || '获取领养数据失败')
  } finally {
    adoptionLoading.value = false
  }
}

const downloadJson = (filename, data) => {
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

const exportBehaviors = () => {
  downloadJson('user-behaviors.json', behaviorList.value)
  ElMessage.success('行为数据已导出')
}

const exportAdoptions = () => {
  downloadJson('adoption-dataset.json', adoptionList.value)
  ElMessage.success('领养数据已导出')
}

const formatDateTime = (value) => value ? new Date(value).toLocaleString() : '-'

onMounted(() => {
  fetchBehaviors()
  fetchAdoptions()
})
</script>

<style scoped>
.dataset-export {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.search-form {
  margin-bottom: 20px;
}
</style>
