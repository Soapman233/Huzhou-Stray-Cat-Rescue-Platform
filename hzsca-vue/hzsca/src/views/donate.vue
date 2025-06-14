<template>
  <div class="donate-container">
    <div class="header">
      <h1><el-icon><StarFilled /></el-icon> 流浪猫救助捐款</h1>
      <p>您的每一份爱心都能改变一只流浪猫的命运</p>
    </div>

    <div class="content-section">
      <div class="purpose-section">
        <h2><el-icon><InfoFilled /></el-icon> 救助目的</h2>
        <p>我们湖州拯救流浪猫协会致力于为无家可归的流浪猫提供:</p>
        <ul>
          <li>日常食物和清洁饮水</li>
          <li>必要的医疗救助</li>
          <li>绝育手术控制数量</li>
          <li>寻找合适的领养家庭</li>
          <li>冬季避寒场所建设</li>
        </ul>
        <p>目前我们已经救助了超过200只流浪猫，为150只猫咪找到了温暖的家。</p>
      </div>

      <div class="thanks-section">
        <h2><el-icon><Present /></el-icon> 感谢您的捐助</h2>
        <p>您的慷慨捐助将直接用于改善流浪猫的生活条件。我们承诺：</p>
        <div class="promise-card">
          <el-icon><Finished /></el-icon>
          <span>每一分钱都会用在猫咪身上</span>
        </div>
        <div class="promise-card">
          <el-icon><Document /></el-icon>
          <span>每月公布详细的资金使用报告</span>
        </div>
        <div class="promise-card">
          <el-icon><Camera /></el-icon>
          <span>定期更新救助猫咪的照片和视频</span>
        </div>
      </div>

      <div class="transparency-section">
        <h2><el-icon><DataLine /></el-icon> 财务透明</h2>
        <p>我们坚信公开透明是获得信任的基础：</p>
        <el-timeline>
          <el-timeline-item timestamp="每月5号" placement="top">
            <el-card>
              <h4>月度财务报告</h4>
              <p>详细列出所有收入和支出明细</p>
            </el-card>
          </el-timeline-item>
          <el-timeline-item timestamp="每季度末" placement="top">
            <el-card>
              <h4>季度救助报告</h4>
              <p>展示救助成果和猫咪现状</p>
            </el-card>
          </el-timeline-item>
          <el-timeline-item timestamp="每年12月" placement="top">
            <el-card>
              <h4>年度审计报告</h4>
              <p>由第三方机构审计的完整财务报告</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <div class="contact-section">
        <h2><el-icon><Phone /></el-icon> 联系我们</h2>
        <p>如有任何疑问或想了解更多救助详情，欢迎随时联系我们：</p>
        <div class="contact-methods">
          <div class="contact-card">
            <el-icon><Iphone /></el-icon>
            <span>电话：0572-2888 9999</span>
          </div>
          <div class="contact-card">
            <el-icon><Message /></el-icon>
            <span>邮箱：rescue@hzcats.org</span>
          </div>
          <div class="contact-card">
            <el-icon><Location /></el-icon>
            <span>地址：湖州市吴兴区爱猫路88号</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  StarFilled,
  InfoFilled,
  Present,
  Finished,
  Document,
  Camera,
  DataLine,
  Phone,
  Iphone,
  Message,
  Location
} from '@element-plus/icons-vue'
</script>

<style scoped>
.donate-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.header h1 {
  color: #5d4037;
  font-size: 32px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header h1 .el-icon {
  margin-right: 10px;
  color: #ff9800;
}

.header p {
  color: #78909c;
  font-size: 18px;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

h2 {
  color: #5d4037;
  font-size: 24px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

h2 .el-icon {
  margin-right: 10px;
  color: #ff9800;
}

.purpose-section ul {
  margin: 15px 0;
  padding-left: 20px;
}

.purpose-section li {
  margin-bottom: 8px;
  color: #546e7a;
}

.promise-card {
  display: flex;
  align-items: center;
  padding: 15px;
  margin: 10px 0;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.promise-card .el-icon {
  margin-right: 10px;
  color: #4caf50;
  font-size: 20px;
}

.contact-methods {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 20px;
}

.contact-card {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background-color: #e3f2fd;
  border-radius: 8px;
  min-width: 250px;
}

.contact-card .el-icon {
  margin-right: 10px;
  color: #2196f3;
  font-size: 20px;
}

@media (max-width: 768px) {
  .header {
    padding: 20px;
  }

  .contact-methods {
    flex-direction: column;
  }

  .contact-card {
    width: 100%;
  }
}
</style>