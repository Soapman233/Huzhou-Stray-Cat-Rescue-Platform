<template>
  <!-- 头部开始 -->
  <div class="header-section" :style="{ height: carouselHeight }">
    <img :src="bgImage" alt="Header Background" class="header-image" />
    <span class="header-text">领养猫咪申请表及同意书</span>
  </div>
  <!-- 头部结束 -->

  <!-- 申请表开始 -->
  <div class="form-container">
    <div class="form-wrapper">
      <el-form
          :model="form"
          label-width="140px"
          class="application-form"
          :rules="rules"
          ref="formRef"
      >
        <!-- 基本信息部分 -->
        <h2 class="form-section-title">基本信息</h2>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入您的姓名" />
        </el-form-item>

        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
              v-model="form.birthDate"
              type="date"
              placeholder="请选择出生日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="身份证号码" prop="idCardNumber">
          <el-input v-model="form.idCardNumber" placeholder="请输入身份证号码" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入电子邮箱" />
        </el-form-item>

        <el-form-item label="居住地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细居住地址" />
        </el-form-item>

        <!-- 评估信息部分 -->
        <h2 class="form-section-title">评估信息</h2>
        <el-form-item label="家庭月收入" prop="familyIncome">
          <el-select v-model="form.familyIncome" placeholder="请选择家庭月收入范围">
            <el-option label="5000元以下" value="<5000" />
            <el-option label="5000-10000元" value="5000-10000" />
            <el-option label="10000-20000元" value="10000-20000" />
            <el-option label="20000元以上" value=">20000" />
          </el-select>
        </el-form-item>

        <el-form-item label="是否养过宠物" prop="hasOwnedPets">
          <el-radio-group v-model="form.hasOwnedPets">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="居住空间" prop="livingSpace">
          <el-input
              type="textarea"
              v-model="form.livingSpace"
              placeholder="请描述您的居住环境（房屋面积、是否有阳台等）"
              :rows="3"
          />
        </el-form-item>

        <!-- 健康信息部分 -->
        <h2 class="form-section-title">健康信息</h2>
        <el-form-item label="家中是否有人吸烟" prop="smoker">
          <el-radio-group v-model="form.smoker">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="家中是否有人哮喘" prop="asthma">
          <el-radio-group v-model="form.asthma">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="是否对猫毛过敏" prop="allergy">
          <el-radio-group v-model="form.allergy">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 承诺部分 -->
        <h2 class="form-section-title">领养承诺</h2>
        <el-form-item label="领养承诺" prop="agreement">
          <el-checkbox v-model="form.agreement">
            我承诺以上信息真实有效，并且我会善待猫咪，提供良好的生活环境，遵守领养协议的各项规定
          </el-checkbox>
        </el-form-item>

        <el-form-item class="form-buttons">
          <el-button type="primary" @click="submitForm(formRef)">提交申请</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
  <!-- 申请表结束 -->

  <div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '../utils/request';
import config from '../config/config';
import '/src/assets/AdoptDetail.css';

const route = useRoute();
const router = useRouter();
const formRef = ref(null);

// 背景图片
const bgImage = new URL('/src/assets/animals-cat-banner.jpg', import.meta.url).href
// 计算属性：动态计算轮播图高度
const carouselHeight = computed(() => {
  const aspectRatio = 650 / 150
  const width = window.innerWidth > 1200 ? 1200 : window.innerWidth
  return `${width / aspectRatio}px`
})

// 表单数据
const form = reactive({
  name: '',
  birthDate: '',
  idCardNumber: '',
  phone: '',
  email: '',
  address: '',
  familyIncome: '',
  hasOwnedPets: false,
  livingSpace: '',
  smoker: false,
  asthma: false,
  allergy: false,
  petFriendly: true,  // 默认为宠物友好
  agreement: false
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  idCardNumber: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入居住地址', trigger: 'blur' }
  ],
  familyIncome: [
    { required: true, message: '请选择家庭收入范围', trigger: 'change' }
  ],
  livingSpace: [
    { required: true, message: '请描述居住环境', trigger: 'blur' }
  ],
  agreement: [
    { required: true, message: '请阅读并同意领养承诺', trigger: 'change' }
  ]
}

// 提交表单
const submitForm = async (formEl) => {
  if (!formEl) return;

  await formEl.validate(async (valid, fields) => {
    if (valid) {
      try {
        // 获取当前用户信息并添加调试日志
        const userInfoRaw = localStorage.getItem('userInfo');
        console.log('Raw userInfo from localStorage:', userInfoRaw);

        let userInfo;
        try {
          userInfo = JSON.parse(userInfoRaw);
          console.log('Parsed userInfo:', userInfo);
        } catch (e) {
          console.error('Error parsing userInfo:', e);
          ElMessage.error('用户信息解析失败');
          return;
        }

        if (!userInfo) {
          console.error('No userInfo found in localStorage');
          ElMessage.error('请先登录');
          router.push('/login');
          return;
        }

        if (!userInfo.UserID) {  // 只检查大写的 UserID
          console.error('No UserID found in userInfo:', userInfo);
          ElMessage.error('用户信息不完整，请重新登录');
          router.push('/login');
          return;
        }

        // 1. 创建领养人记录
        const adopterData = {
          userId: userInfo.UserID,  // 使用大写的 UserID
          name: form.name,
          age: calculateAge(form.birthDate),
          idCardNumber: form.idCardNumber,
          phone: form.phone,
          email: form.email,
          address: form.address,
          birthDate: form.birthDate
        };

        console.log('Submitting adopter data:', adopterData);  // 调试日志

        const adopterResponse = await request.post('/api/adopters', adopterData);
        console.log('Adopter response:', adopterResponse);  // 调试日志

        if (!adopterResponse.data.success) {
          throw new Error(adopterResponse.data.message || '创建领养人信息失败');
        }

        const adopterId = adopterResponse.data.id;

        // 2. 创建评估记录
        const assessmentData = {
          adopterId: adopterId,
          name: form.name,
          familyIncome: parseFamilyIncome(form.familyIncome),
          hasOwnedPets: form.hasOwnedPets,
          smoker: form.smoker,
          asthma: form.asthma,
          allergy: form.allergy,
          livingSpace: form.livingSpace,
          petFriendly: form.petFriendly
        };

        console.log('Submitting assessment data:', assessmentData);  // 调试日志

        const assessmentResponse = await request.post('/api/adoption-assessments', assessmentData);
        console.log('Assessment response:', assessmentResponse);  // 调试日志

        if (!assessmentResponse.data.success) {
          throw new Error(assessmentResponse.data.message || '创建评估记录失败');
        }

        ElMessage.success('申请提交成功！');
        router.push('/hzsca/adopt');
      } catch (error) {
        console.error('Error:', error);
        ElMessage.error(error.message || '提交失败，请稍后重试');
      }
    } else {
      console.log('Validation failed:', fields);
      ElMessage.error('请填写所有必填项');
    }
  });
}

// 计算年龄
const calculateAge = (birthDate) => {
  const birth = new Date(birthDate);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const monthDiff = today.getMonth() - birth.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--;
  }

  return age;
}

// 解析家庭收入为数值
const parseFamilyIncome = (income) => {
  switch (income) {
    case '<5000':
      return 4999.99;
    case '5000-10000':
      return 7500.00;
    case '10000-20000':
      return 15000.00;
    case '>20000':
      return 20000.00;
    default:
      return 0.00;
  }
}

// 返回上一页
const goBack = () => {
  router.back();
}
</script>

<style scoped>
.header-section {
  position: relative;
  width: 100%;
  overflow: hidden;
  margin-bottom: 2rem;
}

.header-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 2.5rem;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.form-container {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 2rem 1rem;
  background-color: #f8f9fa;
}

.form-wrapper {
  width: 100%;
  max-width: 800px;
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.application-form {
  width: 100%;
}

.form-section-title {
  color: #1E4A6D;
  font-size: 1.5rem;
  margin: 2rem 0 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e9ecef;
}

.form-section-title:first-child {
  margin-top: 0;
}

.form-buttons {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.el-textarea__inner) {
  min-height: 80px !important;
}

:deep(.el-radio-group) {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}

@media (max-width: 768px) {
  .form-wrapper {
    padding: 1rem;
  }

  .header-text {
    font-size: 1.8rem;
  }

  :deep(.el-form-item__label) {
    text-align: left;
    float: none;
    display: block;
    margin-bottom: 8px;
  }

  .el-form-item {
    margin-bottom: 22px;
  }

  .form-section-title {
    font-size: 1.2rem;
  }
}
</style>