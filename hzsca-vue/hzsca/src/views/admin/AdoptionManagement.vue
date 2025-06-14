<template>
  <div class="adoption-management">
    <div class="page-header">
      <h2>领养申请管理</h2>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="申请人">
          <el-input v-model="searchForm.name" placeholder="请输入申请人姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table
          v-loading="loading"
          :data="adoptionList"
          border
          style="width: 100%"
      >
        <el-table-column prop="assessmentId" label="申请ID" min-width="90" />
        <el-table-column prop="name" label="申请人" min-width="100" />
        <el-table-column prop="familyIncome" label="家庭收入" min-width="120">
          <template #default="{ row }">
            {{ formatIncome(row.familyIncome) }}
          </template>
        </el-table-column>
        <el-table-column prop="hasOwnedPets" label="养过宠物" min-width="90">
          <template #default="{ row }">
            <el-tag :type="row.hasOwnedPets ? 'success' : 'info'">
              {{ row.hasOwnedPets ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="livingSpace" label="居住空间" min-width="100" />
        <el-table-column prop="petFriendly" label="宠物友好" min-width="90">
          <template #default="{ row }">
            <el-tag :type="row.petFriendly ? 'success' : 'warning'">
              {{ row.petFriendly ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" min-width="160">
          <template #default="{ row }">
            {{ row.createTime ? new Date(row.createTime).toLocaleString() : '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
          <template #default="{ row }">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
              <template v-if="row.status === 'PENDING'">
                <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
                <el-button type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
              </template>
              <template v-else-if="row.status === 'APPROVED'">
                <el-button type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
              </template>
              <template v-else-if="row.status === 'REJECTED'">
                <el-button type="success" size="small" @click="handleApprove(row)">重新通过</el-button>
              </template>
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

    <!-- 查看详情对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="申请详情"
        width="50%"
    >
      <div v-if="currentApplication" class="application-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请ID">{{ currentApplication.assessmentId }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentApplication.name }}</el-descriptions-item>
          <el-descriptions-item label="家庭收入">{{ formatIncome(currentApplication.familyIncome) }}</el-descriptions-item>
          <el-descriptions-item label="养过宠物">
            <el-tag :type="currentApplication.hasOwnedPets ? 'success' : 'info'">
              {{ currentApplication.hasOwnedPets ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="家中吸烟">
            <el-tag :type="currentApplication.smoker ? 'danger' : 'success'">
              {{ currentApplication.smoker ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="家中哮喘">
            <el-tag :type="currentApplication.asthma ? 'danger' : 'success'">
              {{ currentApplication.asthma ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="动物毛过敏">
            <el-tag :type="currentApplication.allergy ? 'danger' : 'success'">
              {{ currentApplication.allergy ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="居住空间">{{ currentApplication.livingSpace }}</el-descriptions-item>
          <el-descriptions-item label="宠物友好">
            <el-tag :type="currentApplication.petFriendly ? 'success' : 'warning'">
              {{ currentApplication.petFriendly ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentApplication.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <template v-if="currentApplication">
            <template v-if="currentApplication.status === 'PENDING'">
              <el-button type="success" @click="handleApprove(currentApplication)">通过申请</el-button>
              <el-button type="danger" @click="handleReject(currentApplication)">拒绝申请</el-button>
            </template>
            <template v-else-if="currentApplication.status === 'APPROVED'">
              <el-button type="danger" @click="handleReject(currentApplication)">拒绝申请</el-button>
            </template>
            <template v-else-if="currentApplication.status === 'REJECTED'">
              <el-button type="success" @click="handleApprove(currentApplication)">重新通过</el-button>
            </template>
            <el-tag :type="getStatusType(currentApplication.status)">
              {{ getStatusText(currentApplication.status) }}
            </el-tag>
          </template>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据
const loading = ref(false)
const adoptionList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentApplication = ref(null)
const searchForm = ref({
  name: ''
})

// 获取领养申请列表
const fetchAdoptionList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/adoption-assessments', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        name: searchForm.value.name || ''
      }
    })
    console.log('Response data:', response.data); // 调试日志

    if (response.data.code === '200' && response.data.data) {
      const { records, total: totalCount, current, size } = response.data.data;

      // 确保records是数组
      if (Array.isArray(records)) {
        adoptionList.value = records;
        total.value = totalCount || 0;
        currentPage.value = current || 1;
        pageSize.value = size || 10;
      } else {
        console.error('Invalid records format:', records);
        ElMessage.error('数据格式错误');
      }
    } else {
      console.error('Invalid response format:', response.data);
      ElMessage.error(response.data.message || '获取数据失败');
    }
  } catch (error) {
    console.error('获取领养申请列表失败:', error);
    if (error.response) {
      ElMessage.error(error.response.data.message || '获取领养申请列表失败');
    } else {
      ElMessage.error('网络错误，请稍后重试');
    }
  } finally {
    loading.value = false;
  }
}

// 格式化收入
const formatIncome = (income) => {
  return income ? `¥${income.toLocaleString()}` : '未填写'
}

// 查看详情
const handleView = (row) => {
  currentApplication.value = row
  dialogVisible.value = true
}

// 通过申请
const handleApprove = async (row) => {
  try {
    const confirmMessage = row.status === 'REJECTED'
        ? '确定要重新通过该领养申请吗？'
        : '确定要通过该领养申请吗？';

    await ElMessageBox.confirm(confirmMessage, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })

    const response = await request.put(`/api/adoption-assessments/${row.assessmentId}/approve`, {
      status: 'APPROVED',
      assessmentId: row.assessmentId
    });

    if (response.data.code === '200') {
      ElMessage.success(row.status === 'REJECTED' ? '已重新通过申请' : '已通过申请');
      // 更新本地状态
      if (currentApplication.value && currentApplication.value.assessmentId === row.assessmentId) {
        currentApplication.value.status = 'APPROVED';
      }
      await fetchAdoptionList();
      dialogVisible.value = false;
    } else {
      console.error('通过申请失败:', response.data);
      ElMessage.error(response.data.message || '通过申请失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('通过申请出错:', error);
      if (error.response) {
        console.error('错误响应:', error.response.data);
        ElMessage.error(error.response.data.message || '通过申请失败');
      } else if (error.request) {
        console.error('请求未收到响应');
        ElMessage.error('网络错误，请稍后重试');
      } else {
        console.error('请求配置错误:', error.message);
        ElMessage.error('请求配置错误');
      }
    }
  }
}

// 拒绝申请
const handleReject = async (row) => {
  try {
    const confirmMessage = row.status === 'APPROVED'
        ? '确定要撤销通过并拒绝该领养申请吗？'
        : '确定要拒绝该领养申请吗？';

    await ElMessageBox.confirm(confirmMessage, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await request.put(`/api/adoption-assessments/${row.assessmentId}/reject`, {
      status: 'REJECTED',
      assessmentId: row.assessmentId
    });

    if (response.data.code === '200') {
      ElMessage.success(row.status === 'APPROVED' ? '已撤销通过并拒绝申请' : '已拒绝申请');
      // 更新本地状态
      if (currentApplication.value && currentApplication.value.assessmentId === row.assessmentId) {
        currentApplication.value.status = 'REJECTED';
      }
      await fetchAdoptionList();
      dialogVisible.value = false;
    } else {
      console.error('拒绝申请失败:', response.data);
      ElMessage.error(response.data.message || '拒绝申请失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝申请出错:', error);
      if (error.response) {
        console.error('错误响应:', error.response.data);
        ElMessage.error(error.response.data.message || '拒绝申请失败');
      } else if (error.request) {
        console.error('请求未收到响应');
        ElMessage.error('网络错误，请稍后重试');
      } else {
        console.error('请求配置错误:', error.message);
        ElMessage.error('请求配置错误');
      }
    }
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchAdoptionList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value.name = ''
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAdoptionList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAdoptionList()
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'PENDING':
      return 'warning';
    case 'APPROVED':
      return 'success';
    case 'REJECTED':
      return 'danger';
    default:
      return 'info';
  }
};

// 获取状态显示文本
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '待审核';
    case 'APPROVED':
      return '已通过';
    case 'REJECTED':
      return '已拒绝';
    default:
      return '未知';
  }
};

// 初始化
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

:deep(.el-descriptions) {
  margin-bottom: 20px;
}

:deep(.el-tag) {
  margin: 0 4px;
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