<template>
  <div class="order-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>工单管理</h2>
        </div>
      </template>
      
      <el-table :data="orders" stripe style="width: 100%">
        <el-table-column prop="id" label="工单号" width="80" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewOrder(scope.row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 工单详情对话框 -->
    <el-dialog v-model="dialogVisible" title="工单详情" width="60%">
      <template v-if="currentOrder">
        <div class="order-detail">
          <h3>基本信息</h3>
          <p>工单号：{{ currentOrder.id }}</p>
          <p>状态：{{ getStatusText(currentOrder.status) }}</p>
          <p>创建时间：{{ currentOrder.createdAt }}</p>
          
          <el-divider />
          
          <div class="status-update">
            <h3>更新状态</h3>
            <el-select v-model="newStatus" placeholder="选择状态">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <el-button type="primary" @click="updateStatus">更新</el-button>
          </div>
          
          <el-divider />
          
          <div class="materials">
            <div class="section-header">
              <h3>材料清单</h3>
              <el-button type="primary" size="small" @click="materialDialogVisible = true">添加材料</el-button>
            </div>
            <el-table :data="currentOrder.materials || []" stripe>
              <el-table-column prop="name" label="名称" />
              <el-table-column prop="quantity" label="数量" />
              <el-table-column prop="price" label="单价" />
              <el-table-column label="总价">
                <template #default="scope">
                  {{ (scope.row.quantity * scope.row.price).toFixed(2) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <el-divider />
          
          <div class="work-hours">
            <div class="section-header">
              <h3>工时记录</h3>
              <el-button type="primary" size="small" @click="hourDialogVisible = true">添加工时</el-button>
            </div>
            <el-table :data="currentOrder.workHours || []" stripe>
              <el-table-column prop="description" label="描述" />
              <el-table-column prop="hours" label="工时" />
              <el-table-column prop="rate" label="费率" />
              <el-table-column label="总价">
                <template #default="scope">
                  {{ (scope.row.hours * scope.row.rate).toFixed(2) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 添加材料对话框 -->
    <el-dialog v-model="materialDialogVisible" title="添加材料" width="30%">
      <el-form :model="materialForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="materialForm.name" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="materialForm.quantity" type="number" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model="materialForm.price" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="materialDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addMaterial">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加工时对话框 -->
    <el-dialog v-model="hourDialogVisible" title="添加工时" width="30%">
      <el-form :model="hourForm" label-width="80px">
        <el-form-item label="描述">
          <el-input v-model="hourForm.description" />
        </el-form-item>
        <el-form-item label="工时">
          <el-input v-model="hourForm.hours" type="number" />
        </el-form-item>
        <el-form-item label="费率">
          <el-input v-model="hourForm.rate" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="hourDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addWorkHour">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updateOrderStatus, addMaterial as addMaterialApi, addWorkHour as addWorkHourApi } from '@/api/workOrder'
import type { WorkOrder, WorkOrderStatus } from '@/types'

const orders = ref<WorkOrder[]>([])
const currentOrder = ref<WorkOrder | null>(null)
const dialogVisible = ref(false)
const materialDialogVisible = ref(false)
const hourDialogVisible = ref(false)
const newStatus = ref('')

const materialForm = ref({
  name: '',
  quantity: 1,
  price: 0
})

const hourForm = ref({
  description: '',
  hours: 1,
  rate: 0
})

const statusOptions = [
  { value: 'PENDING', label: '待处理' },
  { value: 'IN_PROGRESS', label: '处理中' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' }
]

onMounted(async () => {
  await fetchOrders()
})

const fetchOrders = async () => {
  try {
    // 这里需要添加获取工单列表的API
    // const response = await getWorkOrders()
    // orders.value = response.data
    orders.value = [] // 临时占位
  } catch (error) {
    ElMessage.error('获取工单列表失败')
  }
}

const getStatusType = (status: WorkOrderStatus) => {
  switch (status) {
    case 'PENDING': return 'info'
    case 'IN_PROGRESS': return 'warning'
    case 'COMPLETED': return 'success'
    case 'CANCELLED': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: WorkOrderStatus) => {
  switch (status) {
    case 'PENDING': return '待处理'
    case 'IN_PROGRESS': return '处理中'
    case 'COMPLETED': return '已完成'
    case 'CANCELLED': return '已取消'
    default: return '未知'
  }
}

const viewOrder = async (id: number) => {
  // 这里需要添加获取工单详情的API
  // const response = await getWorkOrder(id)
  // currentOrder.value = response.data
  currentOrder.value = orders.value.find(order => order.id === id) || null
  dialogVisible.value = true
}

const updateStatus = async () => {
  if (!currentOrder.value || !newStatus.value) return
  
  try {
    await updateOrderStatus(currentOrder.value.id, newStatus.value as WorkOrderStatus)
    ElMessage.success('状态更新成功')
    if (currentOrder.value) {
      currentOrder.value.status = newStatus.value as WorkOrderStatus
    }
    await fetchOrders()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

const addMaterial = async () => {
  if (!currentOrder.value) return
  
  try {
    await addMaterialApi(currentOrder.value.id, materialForm.value)
    ElMessage.success('添加材料成功')
    materialDialogVisible.value = false
    // 重新获取工单详情
    await viewOrder(currentOrder.value.id)
    // 重置表单
    materialForm.value = {
      name: '',
      quantity: 1,
      price: 0
    }
  } catch (error) {
    ElMessage.error('添加材料失败')
  }
}

const addWorkHour = async () => {
  if (!currentOrder.value) return
  
  try {
    await addWorkHourApi(currentOrder.value.id, hourForm.value)
    ElMessage.success('添加工时成功')
    hourDialogVisible.value = false
    // 重新获取工单详情
    await viewOrder(currentOrder.value.id)
    // 重置表单
    hourForm.value = {
      description: '',
      hours: 1,
      rate: 0
    }
  } catch (error) {
    ElMessage.error('添加工时失败')
  }
}
</script>

<style scoped>
.order-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-detail {
  padding: 10px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.status-update {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>