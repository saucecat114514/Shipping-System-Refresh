<template>
  <div class="customer-ship-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>船舶信息</h3>
          <p>查看船舶详细信息和当前状态</p>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="船舶名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入船舶名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="船舶类型">
            <el-select
              v-model="searchForm.type"
              placeholder="请选择船舶类型"
              clearable
              style="width: 200px"
            >
              <el-option label="集装箱船" value="CONTAINER" />
              <el-option label="散货船" value="BULK" />
              <el-option label="油轮" value="TANKER" />
              <el-option label="客船" value="PASSENGER" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 200px"
            >
              <el-option label="可用" value="AVAILABLE" />
              <el-option label="航行中" value="SAILING" />
              <el-option label="维护中" value="MAINTENANCE" />
            </el-select>
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
        empty-text="暂无船舶数据"
      >
        <el-table-column prop="name" label="船舶名称" min-width="120" />
        <el-table-column prop="imoNumber" label="IMO编号" width="120" />
        <el-table-column prop="typeCn" label="船舶类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.typeCn || getShipTypeText(row.typeEn) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deadweightTonnage" label="载重量(吨)" width="120" align="right" />
        <el-table-column prop="grossTonnage" label="总吨位(吨)" width="120" align="right" />
        <el-table-column prop="flag" label="船籍" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="登记时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="text" @click="viewShipDetail(row)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button type="text" @click="trackShip(row)">
              <el-icon><Position /></el-icon>
              实时追踪
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

    <!-- 船舶详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="船舶详情"
      width="600px"
    >
      <div v-if="selectedShip" class="ship-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="船舶名称">
            {{ selectedShip.name }}
          </el-descriptions-item>
          <el-descriptions-item label="IMO编号">
            {{ selectedShip.imoNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="船舶类型">
            {{ selectedShip.typeCn || getShipTypeText(selectedShip.typeEn) }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedShip.status)">
              {{ getStatusText(selectedShip.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="载重量">
            {{ selectedShip.deadweightTonnage }} 吨
          </el-descriptions-item>
          <el-descriptions-item label="总吨位">
            {{ selectedShip.grossTonnage }} 吨
          </el-descriptions-item>
          <el-descriptions-item label="船籍">
            {{ selectedShip.flag }}
          </el-descriptions-item>
          <el-descriptions-item label="MMSI">
            {{ selectedShip.mmsi }}
          </el-descriptions-item>
          <el-descriptions-item label="当前经度">
            {{ selectedShip.currentLongitude }}°
          </el-descriptions-item>
          <el-descriptions-item label="当前纬度">
            {{ selectedShip.currentLatitude }}°
          </el-descriptions-item>
          <el-descriptions-item label="当前航速">
            {{ selectedShip.currentSpeed }} 节
          </el-descriptions-item>
          <el-descriptions-item label="登记时间">
            {{ selectedShip.createdAt }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="ship-actions">
          <el-button type="primary" @click="trackShip(selectedShip)">
            <el-icon><Position /></el-icon>
            实时追踪
          </el-button>
          <el-button @click="createOrderWithShip">
            <el-icon><Plus /></el-icon>
            指定此船舶创建订单
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
import { Search, Refresh, View, Position, Plus } from '@element-plus/icons-vue'
import { getShipList } from '@/api/ship'

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  status: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 详情弹窗
const detailVisible = ref(false)
const selectedShip = ref(null)

// 船舶类型文本
const getShipTypeText = (type) => {
  const typeMap = {
    'CONTAINER': '集装箱船',
    'BULK': '散货船',
    'TANKER': '油轮',
    'PASSENGER': '客船',
    'CARGO': '货船'
  }
  return typeMap[type] || type
}

// 状态类型
const getStatusType = (status) => {
  const typeMap = {
    'AVAILABLE': 'success',
    'SAILING': 'primary',
    'MAINTENANCE': 'warning',
    'DOCKED': 'info'
  }
  return typeMap[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const textMap = {
    'AVAILABLE': '可用',
    'SAILING': '航行中',
    'MAINTENANCE': '维护中',
    'DOCKED': '停靠'
  }
  return textMap[status] || status
}

// 加载船舶数据
const loadShipData = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    const result = await getShipList(params)
    
    if (result.code === 200) {
      tableData.value = result.data.records || []
      pagination.total = result.data.total || 0
    }
  } catch (error) {
    console.error('加载船舶数据失败:', error)
    ElMessage.error('加载船舶数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadShipData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    type: '',
    status: ''
  })
  pagination.page = 1
  loadShipData()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadShipData()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  loadShipData()
}

// 查看船舶详情
const viewShipDetail = (ship) => {
  selectedShip.value = ship
  detailVisible.value = true
}

// 追踪船舶
const trackShip = (ship) => {
  if (detailVisible.value) {
    detailVisible.value = false
  }
  router.push({
    path: '/customer/ships/tracking',
    query: {
      shipId: ship.id,
      shipName: ship.name
    }
  })
}

// 指定此船舶创建订单
const createOrderWithShip = () => {
  detailVisible.value = false
  router.push({
    path: '/customer/orders/create',
    query: {
      shipId: selectedShip.value.id,
      shipName: selectedShip.value.name
    }
  })
}

// 初始化
onMounted(() => {
  loadShipData()
})
</script>

<style scoped>
.customer-ship-list {
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

.ship-detail {
  margin-bottom: 20px;
}

.ship-actions {
  margin-top: 20px;
  text-align: center;
}

.ship-actions .el-button {
  margin: 0 10px;
}

.el-table {
  margin-bottom: 20px;
}

.el-table :deep(.el-table__header-wrapper) {
  background-color: #fafafa;
}

@media (max-width: 768px) {
  .customer-ship-list {
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