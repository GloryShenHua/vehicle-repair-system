<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <template #header>
        <h2>欢迎使用车辆维修管理系统</h2>
      </template>
      <div class="dashboard">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
            <el-card shadow="hover" class="dashboard-card">
              <template #header>
                <div class="card-header">
                  <h3>车辆管理</h3>
                  <el-button type="primary" @click="router.push('/vehicles')">查看</el-button>
                </div>
              </template>
              <div class="card-content">
                <el-icon :size="50"><Van /></el-icon>
                <p>管理您的车辆信息</p>
              </div>
            </el-card>
          </el-col>
          
          <!-- 其他卡片保持不变 -->
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Van, Document, PieChart } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.home-container {
  height: 100%;
  padding: 10px;
  overflow-y: auto;
}

.welcome-card {
  margin-bottom: 15px;
}

.dashboard {
  margin-top: 10px;
}

.dashboard-card {
  height: auto;
  min-height: 160px;
  margin-bottom: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .home-container {
    padding: 5px;
  }
  
  .dashboard-card {
    min-height: 140px;
  }
}
</style>
