<template>
  <div class="route-planning">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="route-list">
          <template #header>
            <div class="card-header">
              <span>航线列表</span>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索航线"
                prefix-icon="Search"
                size="small"
              />
            </div>
          </template>
          <el-table :data="routes" style="width: 100%" size="small" height="calc(100vh - 200px)">
            <el-table-column prop="name" label="航线名称" show-overflow-tooltip />
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button link type="primary" @click="handleSelectRoute(scope.row)">
                  选择
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card class="map-card">
          <template #header>
            <div class="card-header">
              <span>航线规划</span>
              <div class="header-actions">
                <el-button-group>
                  <el-button type="primary" plain size="small" @click="handleSaveRoute">保存航线</el-button>
                  <el-button type="success" plain size="small" @click="handleOptimizeRoute">优化航线</el-button>
                  <el-button type="warning" plain size="small" @click="handleClearRoute">清除</el-button>
                </el-button-group>
              </div>
            </div>
          </template>
          <div class="map-container">
            <!-- 地图将在此处显示 -->
            <div class="map-placeholder">
              地图加载中...
            </div>
          </div>
        </el-card>

        <el-card v-if="selectedRoute" class="route-detail">
          <template #header>
            <div class="card-header">
              <span>航线详情</span>
              <el-tag>{{ selectedRoute.status }}</el-tag>
            </div>
          </template>

          <el-descriptions :column="3" border>
            <el-descriptions-item label="航线编号">{{ selectedRoute.code }}</el-descriptions-item>
            <el-descriptions-item label="航线名称">{{ selectedRoute.name }}</el-descriptions-item>
            <el-descriptions-item label="总航程">{{ selectedRoute.distance }} 海里</el-descriptions-item>
            <el-descriptions-item label="预计航行时间">{{ selectedRoute.duration }}</el-descriptions-item>
            <el-descriptions-item label="起始港口">{{ selectedRoute.startPort }}</el-descriptions-item>
            <el-descriptions-item label="目的港口">{{ selectedRoute.endPort }}</el-descriptions-item>
          </el-descriptions>

          <el-divider>途经港口</el-divider>
          
          <el-table :data="selectedRoute.waypoints" style="width: 100%" size="small">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="portName" label="港口名称" />
            <el-table-column prop="arrivalTime" label="预计到达" width="180" />
            <el-table-column prop="departureTime" label="预计离开" width="180" />
            <el-table-column prop="stayDuration" label="停留时间" width="100" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button link type="primary" @click="handleEditWaypoint(scope.row)">编辑</el-button>
                <el-button link type="danger" @click="handleRemoveWaypoint(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑途经港口对话框 -->
    <el-dialog
      v-model="waypointDialogVisible"
      :title="waypointDialogType === 'add' ? '添加途经港口' : '编辑途经港口'"
      width="500px"
    >
      <el-form ref="waypointFormRef" :model="waypointForm" :rules="waypointRules" label-width="100px">
        <el-form-item label="港口" prop="portName">
          <el-select v-model="waypointForm.portName" placeholder="请选择港口" style="width: 100%">
            <el-option v-for="port in ports" :key="port.code" :label="port.name" :value="port.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计到达" prop="arrivalTime">
          <el-date-picker
            v-model="waypointForm.arrivalTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预计离开" prop="departureTime">
          <el-date-picker
            v-model="waypointForm.departureTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="停留时间" prop="stayDuration">
          <el-input v-model="waypointForm.stayDuration" placeholder="例如：2小时" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="waypointDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveWaypoint">确定</el-button>
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
const waypointDialogVisible = ref(false)
const waypointDialogType = ref<'add' | 'edit'>('add')
const waypointFormRef = ref<FormInstance>()

// 表单数据
const waypointForm = reactive({
  portName: '',
  arrivalTime: '',
  departureTime: '',
  stayDuration: ''
})

// 表单验证规则
const waypointRules: FormRules = {
  portName: [{ required: true, message: '请选择港口', trigger: 'change' }],
  arrivalTime: [{ required: true, message: '请选择预计到达时间', trigger: 'change' }],
  departureTime: [{ required: true, message: '请选择预计离开时间', trigger: 'change' }],
  stayDuration: [{ required: true, message: '请输入停留时间', trigger: 'blur' }]
}

// 港口列表
const ports = [
  { code: 'SHA', name: '上海港' },
  { code: 'SIN', name: '新加坡港' },
  { code: 'QIN', name: '青岛港' },
  { code: 'PUS', name: '釜山港' }
]

// 模拟数据
const routes = [
  {
    code: 'R001',
    name: '上海-新加坡快线',
    startPort: '上海港',
    endPort: '新加坡港',
    distance: 2580,
    duration: '5天',
    status: '正常',
    waypoints: [
      {
        portName: '香港港',
        arrivalTime: '2024-01-17 10:00:00',
        departureTime: '2024-01-17 16:00:00',
        stayDuration: '6小时'
      },
      {
        portName: '马尼拉港',
        arrivalTime: '2024-01-18 14:00:00',
        departureTime: '2024-01-18 20:00:00',
        stayDuration: '6小时'
      }
    ]
  }
]

const selectedRoute = ref(routes[0])

// 处理选择航线
const handleSelectRoute = (route: any) => {
  selectedRoute.value = route
  // TODO: 在地图上显示航线
}

// 处理保存航线
const handleSaveRoute = () => {
  // TODO: 调用保存API
  ElMessage.success('航线保存成功')
}

// 处理优化航线
const handleOptimizeRoute = () => {
  // TODO: 调用优化API
  ElMessage.success('航线优化完成')
}

// 处理清除航线
const handleClearRoute = () => {
  ElMessageBox.confirm(
    '确定要清除当前航线吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    selectedRoute.value = null
    // TODO: 清除地图上的航线
    ElMessage.success('航线已清除')
  })
}

// 处理编辑途经港口
const handleEditWaypoint = (waypoint: any) => {
  waypointDialogType.value = 'edit'
  waypointDialogVisible.value = true
  Object.assign(waypointForm, waypoint)
}

// 处理删除途经港口
const handleRemoveWaypoint = (waypoint: any) => {
  ElMessageBox.confirm(
    `确定要删除途经港口"${waypoint.portName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 从航线中删除途经港口
    ElMessage.success('途经港口已删除')
  })
}

// 处理保存途经港口
const handleSaveWaypoint = async () => {
  if (!waypointFormRef.value) return
  
  await waypointFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 保存途经港口
      ElMessage.success(waypointDialogType.value === 'add' ? '添加成功' : '更新成功')
      waypointDialogVisible.value = false
    }
  })
}
</script>

<style scoped>
.route-planning {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.map-card {
  margin-bottom: 20px;
}

.map-container {
  height: 500px;
  background-color: #f5f7fa;
}

.map-placeholder {
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

.route-detail {
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