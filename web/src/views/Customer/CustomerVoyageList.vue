<template>
  <div class="customer-voyage-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>航次信息</h3>
          <p>查看可用航次和时间安排</p>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="航次编号">
            <el-input
              v-model="searchForm.voyageNumber"
              placeholder="请输入航次编号"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="船舶名称">
            <el-input
              v-model="searchForm.shipName"
              placeholder="请输入船舶名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 150px"
            >
              <el-option label="计划中" value="PLANNED" />
              <el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="出发时间">
            <el-date-picker
              v-model="searchForm.departureDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 数据表格 -->
      <el-table 
        :data="tableData" 
        stripe 
        style="width: 100%"
        v-loading="loading"
        empty-text="暂无航次数据"
      >
        <el-table-column prop="voyageNumber" label="航次编号" width="120" />
        <el-table-column label="船舶名称" width="120">
          <template #default="{ row }">
            {{ getShipName(row) }}
          </template>
        </el-table-column>
        <el-table-column label="航线名称" width="180">
          <template #default="{ row }">
            {{ getRouteName(row) }}
          </template>
        </el-table-column>
        <el-table-column label="出发港口" width="120">
          <template #default="{ row }">
            {{ getPortName(row, 'origin') }}
          </template>
        </el-table-column>
        <el-table-column label="到达港口" width="120">
          <template #default="{ row }">
            {{ getPortName(row, 'destination') }}
          </template>
        </el-table-column>
        <el-table-column prop="departureDate" label="出发时间" width="160" />
        <el-table-column prop="arrivalDate" label="到达时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="text" @click="viewVoyageDetail(row)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button 
              type="text" 
              @click="bookVoyage(row)"
              :disabled="row.status !== 'PLANNED'"
            >
              <el-icon><Plus /></el-icon>
              预订
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="pagination.page"
          :page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 航次详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="航次详情"
      width="600px"
    >
      <div v-if="selectedVoyage" class="voyage-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="航次编号">
            {{ selectedVoyage.voyageNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedVoyage.status)">
              {{ getStatusText(selectedVoyage.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="船舶名称">
            {{ getShipName(selectedVoyage) }}
          </el-descriptions-item>
          <el-descriptions-item label="航线名称">
            {{ getRouteName(selectedVoyage) }}
          </el-descriptions-item>
          <el-descriptions-item label="出发港口">
            {{ getPortName(selectedVoyage, 'origin') }}
          </el-descriptions-item>
          <el-descriptions-item label="到达港口">
            {{ getPortName(selectedVoyage, 'destination') }}
          </el-descriptions-item>
          <el-descriptions-item label="出发时间">
            {{ selectedVoyage.departureDate }}
          </el-descriptions-item>
          <el-descriptions-item label="到达时间">
            {{ selectedVoyage.arrivalDate }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ selectedVoyage.createdAt }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ selectedVoyage.updatedAt }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="voyage-actions">
          <el-button 
            type="primary" 
            @click="bookVoyage(selectedVoyage)"
            :disabled="selectedVoyage.status !== 'PLANNED'"
          >
            <el-icon><Plus /></el-icon>
            预订此航次
          </el-button>
          <el-button @click="trackVoyage(selectedVoyage)">
            <el-icon><Position /></el-icon>
            追踪航次
          </el-button>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, View, Plus, Position } from '@element-plus/icons-vue'
import { getVoyageList } from '@/api/voyage'

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  voyageNumber: '',
  shipName: '',
  status: '',
  departureDate: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 详情弹窗
const detailVisible = ref(false)
const selectedVoyage = ref(null)

// 状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PLANNED': 'info',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const textMap = {
    'PLANNED': '计划中',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

// 获取港口名称
const getPortName = (row, type) => {
  if (!row.route) return '未知'
  
  if (type === 'origin') {
    return row.route.originPort ? row.route.originPort.nameCn : '未知'
  } else if (type === 'destination') {
    return row.route.destinationPort ? row.route.destinationPort.nameCn : '未知'
  }
  
  return '未知'
}

// 检查航次数据结构，如果没有关联信息则尝试其他字段
const getShipName = (voyage) => {
  if (voyage.ship && voyage.ship.name) {
    return voyage.ship.name
  }
  // 兼容可能的其他字段名
  return voyage.shipName || '未分配'
}

const getRouteName = (voyage) => {
  if (voyage.route && voyage.route.name) {
    return voyage.route.name
  }
  // 兼容可能的其他字段名
  return voyage.routeName || '未分配'
}

// 加载航次数据
const loadVoyageData = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    // 处理日期范围
    if (searchForm.departureDate && searchForm.departureDate.length === 2) {
      params.startDate = searchForm.departureDate[0]
      params.endDate = searchForm.departureDate[1]
      delete params.departureDate
    }
    
    const result = await getVoyageList(params)
    
    if (result.code === 200) {
      tableData.value = result.data.records || []
      pagination.total = result.data.total || 0
    }
  } catch (error) {
    console.error('加载航次数据失败:', error)
    ElMessage.error('加载航次数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadVoyageData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    voyageNumber: '',
    shipName: '',
    status: '',
    departureDate: null
  })
  pagination.page = 1
  loadVoyageData()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadVoyageData()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  loadVoyageData()
}

// 查看航次详情
const viewVoyageDetail = (voyage) => {
  selectedVoyage.value = voyage
  detailVisible.value = true
}

// 预订航次
const bookVoyage = (voyage) => {
  if (detailVisible.value) {
    detailVisible.value = false
  }
  
  router.push({
    path: '/customer/orders/create',
    query: {
      voyageId: voyage.id,
      voyageNumber: voyage.voyageNumber,
      shipId: voyage.shipId,
      routeId: voyage.routeId
    }
  })
}

// 追踪航次
const trackVoyage = (voyage) => {
  if (detailVisible.value) {
    detailVisible.value = false
  }
  
  router.push({
    path: '/customer/ships/tracking',
    query: {
      shipId: voyage.shipId,
      voyageId: voyage.id,
      voyageNumber: voyage.voyageNumber
    }
  })
}

// 初始化
onMounted(() => {
  loadVoyageData()
})
</script>

<style scoped>
.customer-voyage-list {
  padding: 20px;
}

.card-header h3 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.voyage-detail {
  margin-bottom: 20px;
}

.voyage-actions {
  margin-top: 20px;
  text-align: center;
}

.voyage-actions .el-button {
  margin: 0 10px;
}

.el-table {
  margin-bottom: 20px;
}

.el-table :deep(.el-table__header-wrapper) {
  background-color: #fafafa;
}

@media (max-width: 768px) {
  .customer-voyage-list {
    padding: 10px;
  }
  
  .search-area .el-form {
    display: flex;
    flex-direction: column;
  }
  
  .search-area .el-form-item {
    margin-bottom: 10px;
  }
  
  .pagination-wrapper {
    justify-content: center;
  }
}
</style> 