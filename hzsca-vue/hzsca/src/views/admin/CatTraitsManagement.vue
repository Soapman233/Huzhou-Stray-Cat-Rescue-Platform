<template>
  <div class="cat-traits-management">
    <div class="header">
      <div class="title">猫咪特征管理</div>
      <div class="sub">维护猫咪性格、活动量、亲人度、掉毛等级等特征，用于小程序端智能推荐</div>
    </div>

    <div class="action-bar">
      <el-input
          v-model="searchQuery"
          placeholder="搜索猫咪名称/品种"
          clearable
          class="search"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
      <el-button :icon="Refresh" @click="reload">刷新</el-button>
    </div>

    <el-table
        v-loading="loading"
        :data="filteredCats"
        border
        style="width: 100%"
    >
      <el-table-column prop="AnimalID" label="ID" width="70" />
      <el-table-column prop="Name" label="名称" min-width="120" />
      <el-table-column prop="Breed" label="品种" min-width="120" />
      <el-table-column label="特征概览" min-width="260">
        <template #default="{ row }">
          <div class="traits-line">
            <el-tag v-if="traitsMap[row.AnimalID]" type="success" effect="dark">已配置</el-tag>
            <el-tag v-else type="info" effect="plain">未配置</el-tag>
            <template v-if="traitsMap[row.AnimalID]">
              <el-tag effect="plain" class="tag">活动 {{ traitsMap[row.AnimalID].activityLevel }}/5</el-tag>
              <el-tag effect="plain" class="tag">亲人 {{ traitsMap[row.AnimalID].affectionLevel }}/5</el-tag>
              <el-tag effect="plain" class="tag">掉毛 {{ traitsMap[row.AnimalID].sheddingLevel }}/5</el-tag>
              <el-tag :type="traitsMap[row.AnimalID].childFriendly ? 'success' : 'danger'" effect="plain" class="tag">
                {{ traitsMap[row.AnimalID].childFriendly ? '适合孩子' : '不适合孩子' }}
              </el-tag>
              <el-tag :type="traitsMap[row.AnimalID].petFriendly ? 'success' : 'danger'" effect="plain" class="tag">
                {{ traitsMap[row.AnimalID].petFriendly ? '适合同住宠物' : '不适合同住宠物' }}
              </el-tag>
            </template>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEditor(row)">编辑特征</el-button>
        </template>
      </el-table-column>
    </el-table>

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

    <el-drawer v-model="drawerVisible" size="520px" :with-header="false">
      <div class="drawer-header">
        <div class="drawer-title">编辑特征</div>
        <div class="drawer-sub">{{ currentCat?.Name }}（ID: {{ currentCat?.AnimalID }}）</div>
      </div>
      <el-form ref="formRef" :model="form" label-width="120px" class="traits-form">
        <el-form-item label="性格倾向">
          <el-select v-model="form.personality" placeholder="请选择">
            <el-option label="任意" value="ANY" />
            <el-option label="粘人" value="CUDDLY" />
            <el-option label="独立" value="INDEPENDENT" />
            <el-option label="活泼" value="PLAYFUL" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动量(1-5)">
          <el-slider v-model="form.activityLevel" :min="1" :max="5" show-stops />
        </el-form-item>
        <el-form-item label="亲人度(1-5)">
          <el-slider v-model="form.affectionLevel" :min="1" :max="5" show-stops />
        </el-form-item>
        <el-form-item label="掉毛等级(1-5)">
          <el-slider v-model="form.sheddingLevel" :min="1" :max="5" show-stops />
        </el-form-item>
        <el-form-item label="空间需求(1-3)">
          <el-radio-group v-model="form.spaceNeedLevel">
            <el-radio :value="1">小</el-radio>
            <el-radio :value="2">中</el-radio>
            <el-radio :value="3">大</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="适合有孩子家庭">
          <el-switch v-model="form.childFriendly" />
        </el-form-item>
        <el-form-item label="适合与其他宠物同住">
          <el-switch v-model="form.petFriendly" />
        </el-form-item>
        <el-form-item label="夜间更活跃">
          <el-switch v-model="form.nocturnal" />
        </el-form-item>
        <el-form-item label="新手友好">
          <el-switch v-model="form.beginnerFriendly" />
        </el-form-item>
        <el-form-item label="需要特殊照护">
          <el-switch v-model="form.specialCareNeeded" />
        </el-form-item>
        <el-form-item label="环境偏好">
          <el-select v-model="form.environmentPreference" placeholder="请选择">
            <el-option label="任意" value="ANY" />
            <el-option label="安静" value="QUIET" />
            <el-option label="热闹" value="LIVELY" />
          </el-select>
        </el-form-item>
      </el-form>
      <div class="drawer-footer">
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveTraits">保存</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const saving = ref(false)
const cats = ref([])
const traitsMap = ref({})
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const drawerVisible = ref(false)
const currentCat = ref(null)
const formRef = ref(null)
const form = ref({
  personality: 'ANY',
  activityLevel: 3,
  affectionLevel: 3,
  childFriendly: true,
  petFriendly: true,
  sheddingLevel: 3,
  nocturnal: false,
  spaceNeedLevel: 2,
  beginnerFriendly: true,
  specialCareNeeded: false,
  environmentPreference: 'ANY'
})

const filteredCats = computed(() => {
  const list = cats.value || []
  if (!searchQuery.value) {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return list.slice(start, end)
  }
  const q = searchQuery.value.toLowerCase()
  const filtered = list.filter(c =>
    (c.Name || '').toLowerCase().includes(q) ||
    (c.Breed || '').toLowerCase().includes(q)
  )
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

const handleSearch = () => {
  currentPage.value = 1
}

const handleSizeChange = () => {
  currentPage.value = 1
}

const handleCurrentChange = () => {}

const reload = async () => {
  loading.value = true
  try {
    const [catRes, traitRes] = await Promise.all([
      request.get('/api/cats'),
      request.get('/api/admin/cat-traits')
    ])

    const catList = Array.isArray(catRes.data?.data) ? catRes.data.data : (catRes.data?.data?.data || [])
    cats.value = Array.isArray(catList) ? catList : []
    total.value = cats.value.length

    const list = Array.isArray(traitRes.data?.data) ? traitRes.data.data : []
    const map = {}
    for (const t of list) {
      if (t && t.catId != null) {
        map[t.catId] = t
      }
    }
    traitsMap.value = map
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const openEditor = async (cat) => {
  currentCat.value = cat
  drawerVisible.value = true
  saving.value = false
  const id = cat?.AnimalID
  if (!id) {
    return
  }
  try {
    const res = await request.get(`/api/admin/cat-traits/${id}`)
    const t = res.data?.data
    if (t) {
      form.value = {
        personality: t.personality || 'ANY',
        activityLevel: t.activityLevel || 3,
        affectionLevel: t.affectionLevel || 3,
        childFriendly: t.childFriendly !== false,
        petFriendly: t.petFriendly !== false,
        sheddingLevel: t.sheddingLevel || 3,
        nocturnal: t.nocturnal === true,
        spaceNeedLevel: t.spaceNeedLevel || 2,
        beginnerFriendly: t.beginnerFriendly !== false,
        specialCareNeeded: t.specialCareNeeded === true,
        environmentPreference: t.environmentPreference || 'ANY'
      }
    } else {
      form.value = {
        personality: 'ANY',
        activityLevel: 3,
        affectionLevel: 3,
        childFriendly: true,
        petFriendly: true,
        sheddingLevel: 3,
        nocturnal: false,
        spaceNeedLevel: 2,
        beginnerFriendly: true,
        specialCareNeeded: false,
        environmentPreference: 'ANY'
      }
    }
  } catch (e) {
    ElMessage.error('加载特征失败')
  }
}

const saveTraits = async () => {
  const id = currentCat.value?.AnimalID
  if (!id) {
    return
  }
  saving.value = true
  try {
    const res = await request.put(`/api/admin/cat-traits/${id}`, form.value)
    if (res.data?.code === '200') {
      const t = res.data?.data
      if (t && t.catId != null) {
        traitsMap.value = {
          ...traitsMap.value,
          [t.catId]: t
        }
      }
      ElMessage.success('保存成功')
      drawerVisible.value = false
      return
    }
    ElMessage.error(res.data?.msg || '保存失败')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  reload()
})
</script>

<style scoped>
.cat-traits-management {
  padding: 16px;
}
.header {
  padding: 14px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffe8c5, #e7f2ff);
  margin-bottom: 14px;
}
.title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
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
.search {
  max-width: 360px;
}
.traits-line {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}
.tag {
  margin-left: 0;
}
.pagination {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}
.drawer-header {
  padding: 16px 16px 10px;
  border-bottom: 1px solid var(--el-border-color);
  background: linear-gradient(135deg, #1d4ed8, #06b6d4);
  color: #fff;
}
.drawer-title {
  font-size: 18px;
  font-weight: 700;
}
.drawer-sub {
  margin-top: 6px;
  font-size: 13px;
  opacity: 0.9;
}
.traits-form {
  padding: 14px 16px 70px;
}
.drawer-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 16px;
  border-top: 1px solid var(--el-border-color);
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  background: #fff;
}
</style>
