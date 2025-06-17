<template>
  <div class="voyage-schedule">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="ship-list">
          <template #header>
            <div class="card-header">
              <span>船舶列表</span>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索船舶"
                prefix-icon="Search"
                size="small"
              />
            </div>
          </template>
          <el-table :data="ships" style="width: 100%" size="small" height="calc(100vh - 200px)">
            <el-table-column prop="name" label="船舶名称" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getShipStatusType(scope.row.status)">
                  {{ getShipStatusLabel(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button link type="primary" @click="handleSelectShip(scope.row)">
                  选择
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card class="schedule-card">
          <template #header>
            <div class="card-header">
              <span>航次调度</span>
              <div class="header-actions">
                <el-button-group>
                  <el-button type="primary" plain size="small" @click="handleSaveSchedule">保存调度</el-button>
                  <el-button type="success" plain size="small" @click="handleOptimizeSchedule">优化调度</el-button>
                  <el-button type="warning" plain size="small" @click="handleClearSchedule">清除</el-button>
                </el-button-group>
              </div>
            </div>
          </template>

          <!-- 甘特图将在此处显示 -->
          <div class="gantt-container">
            <div class="gantt-placeholder">
              甘特图加载中...
            </div>
          </div>
        </el-card>

        <el-card v-if="selectedShip" class="ship-schedule">
          <template #header>
            <div class="card-header">
              <span>{{ selectedShip.name }} - 调度详情</span>
              <el-tag>{{ getShipStatusLabel(selectedShip.status) }}</el-tag>
            </div>
          </template>

          <el-descriptions :column="3" border>
            <el-descriptions-item label="船舶编号">{{ selectedShip.code }}</el-descriptions-item>
            <el-descriptions-item label="船舶类型">{{ selectedShip.type }}</el-descriptions-item>
            <el-descriptions-item label="载重吨位">{{ selectedShip.capacity }}吨</el-descriptions-item>
            <el-descriptions-item label="当前位置">{{ selectedShip.currentLocation }}</el-descriptions-item>
            <el-descriptions-item label="目的港口">{{ selectedShip.destination }}</el-descriptions-item>
            <el-descriptions-item label="预计到达">{{ selectedShip.eta }}</el-descriptions-item>
          </el-descriptions>

          <el-divider>已安排航次</el-divider>
          
          <el-table :data="selectedShip.voyages" style="width: 100%" size="small">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="voyageNo" label="航次编号" width="150" />
            <el-table-column label="航线" min-width="200">
              <template #default="scope">
                {{ scope.row.startPort }} → {{ scope.row.endPort }}
              </template>
            </el-table-column>
            <el-table-column prop="plannedDepartureTime" label="计划出发" width="180" />
            <el-table-column prop="plannedArrivalTime" label="计划到达" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getVoyageStatusType(scope.row.status)">
                  {{ getVoyageStatusLabel(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button link type="primary" @click="handleEditVoyage(scope.row)">编辑</el-button>
                <el-button link type="danger" @click="handleRemoveVoyage(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑航次对话框 -->
    <el-dialog
      v-model="voyageDialogVisible"
      :title="voyageDialogType === 'add' ? '添加航次' : '编辑航次'"
      width="600px"
    >
      <el-form ref="voyageFormRef" :model="voyageForm" :rules="voyageRules" label-width="100px">
        <el-form-item label="航线" prop="routeId">
          <el-select v-model="voyageForm.routeId" placeholder="请选择航线" style="width: 100%">
            <el-option v-for="route in routes" :key="route.id" :label="route.name" :value="route.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划出发" prop="plannedDepartureTime">
          <el-date-picker
            v-model="voyageForm.plannedDepartureTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="计划到达" prop="plannedArrivalTime">
          <el-date-picker
            v-model="voyageForm.plannedArrivalTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input
            v-model="voyageForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="voyageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveVoyage">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

// 搜索关键词
const searchKeyword = ref('')

// 对话框相关
const voyageDialogVisible = ref(false)
const voyageDialogType = ref<'add' | 'edit'>('add')
const voyageFormRef = ref<FormInstance>()

// 表单数据
const voyageForm = reactive({
  routeId: '',
  plannedDepartureTime: '',
  plannedArrivalTime: '',
  remarks: ''
})

// 表单验证规则
const voyageRules: FormRules = {
  routeId: [{ required: true, message: '请选择航线', trigger: 'change' }],
  plannedDepartureTime: [{ required: true, message: '请选择计划出发时间', trigger: 'change' }],
  plannedArrivalTime: [{ required: true, message: '请选择计划到达时间', trigger: 'change' }]
}

// 航线列表
const routes = [
  { id: 1, name: '上海-新加坡快线' },
  { id: 2, name: '青岛-釜山快线' }
]

// 模拟数据
const ships = [
  {
    code: 'SHIP001',
    name: '远洋1号',
    type: '集装箱船',
    capacity: 10000,
    status: 'IN_PORT',
    currentLocation: '上海港',
    destination: '-',
    eta: '-',
    voyages: [
      {
        voyageNo: 'V202401150001',
        startPort: '上海港',
        endPort: '新加坡港',
        plannedDepartureTime: '2024-01-20 10:00:00',
        plannedArrivalTime: '2024-01-25 10:00:00',
        status: 'NOT_STARTED'
      }
    ]
  },
  {
    code: 'SHIP002',
    name: '远洋2号',
    type: '集装箱船',
    capacity: 8000,
    status: 'AT_SEA',
    currentLocation: '东海',
    destination: '釜山港',
    eta: '2024-01-16 14:00:00',
    voyages: []
  }
]

const selectedShip = ref(ships[0])

// 获取船舶状态标签
const getShipStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    'IN_PORT': '在港',
    'AT_SEA': '航行中',
    'MAINTENANCE': '维护中',
    'OUT_OF_SERVICE': '停运'
  }
  return statusMap[status] || status
}

// 获取船舶状态类型
const getShipStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'IN_PORT': 'success',
    'AT_SEA': 'primary',
    'MAINTENANCE': 'warning',
    'OUT_OF_SERVICE': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取航次状态标签
const getVoyageStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取航次状态类型
const getVoyageStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'NOT_STARTED': 'warning',
    'IN_PROGRESS': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 处理选择船舶
const handleSelectShip = (ship: any) => {
  selectedShip.value = ship
  // TODO: 在甘特图上高亮显示该船舶的调度
}

// 处理保存调度
const handleSaveSchedule = () => {
  // TODO: 调用保存API
  ElMessage.success('调度保存成功')
}

// 处理优化调度
const handleOptimizeSchedule = () => {
  // TODO: 调用优化API
  ElMessage.success('调度优化完成')
}

// 处理清除调度
const handleClearSchedule = () => {
  ElMessageBox.confirm(
    '确定要清除当前调度吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    selectedShip.value = null
    // TODO: 清除甘特图上的调度
    ElMessage.success('调度已清除')
  })
}

// 处理编辑航次
const handleEditVoyage = (voyage: any) => {
  voyageDialogType.value = 'edit'
  voyageDialogVisible.value = true
  // TODO: 根据航线获取routeId
  Object.assign(voyageForm, {
    routeId: 1,
    plannedDepartureTime: voyage.plannedDepartureTime,
    plannedArrivalTime: voyage.plannedArrivalTime,
    remarks: voyage.remarks || ''
  })
}

// 处理删除航次
const handleRemoveVoyage = (voyage: any) => {
  ElMessageBox.confirm(
    `确定要删除航次"${voyage.voyageNo}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 从调度中删除航次
    ElMessage.success('航次已删除')
  })
}

// 处理保存航次
const handleSaveVoyage = async () => {
  if (!voyageFormRef.value) return
  
  await voyageFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 保存航次
      ElMessage.success(voyageDialogType.value === 'add' ? '添加成功' : '更新成功')
      voyageDialogVisible.value = false
    }
  })
}
</script>

<style scoped>
.voyage-schedule {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-card {
  margin-bottom: 20px;
}

.gantt-container {
  height: 500px;
  background-color: #f5f7fa;
}

.gantt-placeholder {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 16px;
}

:deep(.el-card__body) {
  padding: 15px;
}

.ship-schedule {
  margin-top: 20px;
}

:deep(.el-descriptions) {
  margin-bottom: 20px;
}

.el-divider {
  margin: 24px 0;
}

:deep(.el-input) {
  width: 180px;
}
</style> 