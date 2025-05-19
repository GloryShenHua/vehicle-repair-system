<template>
  <div class="vehicle-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>车辆管理</h2>
          <el-button type="primary" @click="dialogVisible = true">添加车辆</el-button>
        </div>
      </template>
      
      <el-table :data="vehicles" stripe style="width: 100%">
        <el-table-column prop="make" label="品牌" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="year" label="年份" />
        <el-table-column prop="licensePlate" label="车牌号" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" size="small" @click="createOrder(scope.row.id)">创建工单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加车辆对话框 -->
    <el-dialog v-model="dialogVisible" title="添加车辆" width="30%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="品牌">
          <el-input v-model="form.make" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="年份">
          <el-input v-model="form.year" type="number" />
        </el-form-item>
        <el-form-item label="车牌号">
          <el-input v-model="form.licensePlate" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addVehicle">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getVehicles, createVehicle } from '@/api/vehicle'
import { createWorkOrder } from '@/api/workOrder'
import type { Vehicle } from '@/types'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const vehicles = ref<Vehicle[]>([])
const dialogVisible = ref(false)
const form = ref({
  make: '',
  model: '',
  year: 2023,
  licensePlate: '',
  userId: 1 // 默认设置为1，或者根据实际情况调整
})

// 添加 fetchVehicles 函数定义
const fetchVehicles = async () => {
  try {
    const response = await getVehicles()
    vehicles.value = response.data
  } catch (error) {
    ElMessage.error('获取车辆列表失败')
  }
}

onMounted(async () => {
  // 安全地访问用户ID
  if (authStore.user && 'id' in authStore.user) {
    form.value.userId = authStore.user.id
  }
  await fetchVehicles()
})

const addVehicle = async () => {
  try {
    // 安全地访问用户ID
    if (!form.value.userId && authStore.user && 'id' in authStore.user) {
      form.value.userId = authStore.user.id
    }
    
    // 其余代码保持不变
    await createVehicle(form.value)
    ElMessage.success('添加车辆成功')
    dialogVisible.value = false
    await fetchVehicles()
    
    // 重置表单，但保留 userId
    const userId = form.value.userId
    form.value = {
      make: '',
      model: '',
      year: 2023,
      licensePlate: '',
      userId: userId
    }
  } catch (error) {
    ElMessage.error('添加车辆失败')
  }
}

const createOrder = async (vehicleId: number) => {
  try {
    const response = await createWorkOrder(vehicleId)
    ElMessage.success('创建工单成功')
    router.push(`/orders/${response.data.id}`)
  } catch (error) {
    ElMessage.error('创建工单失败')
  }
}
</script>

<style scoped>
.vehicle-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>