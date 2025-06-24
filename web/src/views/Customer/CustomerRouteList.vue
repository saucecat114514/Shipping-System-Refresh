<template>
  <div class="customer-route-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>航线信息</h3>
          <p>查看可用航线和详细信息</p>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="航线名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入航线名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="航线编号">
            <el-input
              v-model="searchForm.routeNumber"
              placeholder="请输入航线编号"
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
              <el-option label="启用" :value="1" />
              <el-option label="停用" :value="0" />
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
        empty-text="暂无航线数据"
      >
        <el-table-column prop="routeNumber" label="航线编号" width="120" />
        <el-table-column prop="name" label="航线名称" width="180" />
        <el-table-column label="航线" width="250">
          <template #default="{ row }">
            {{ row.originPort ? row.originPort.nameCn : '未知港口' }} → {{ row.destinationPort ? row.destinationPort.nameCn : '未知港口' }}
          </template>
        </el-table-column>
        <el-table-column prop="distance" label="航程(公里)" width="130" align="right" />
        <el-table-column prop="distanceNm" label="航程(海里)" width="120" align="right" />
        <el-table-column prop="estimatedDuration" label="预计时间(小时)" width="140" align="right" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" @click="viewRouteDetail(row)">
              <el-icon><View /></el-icon>
              查看详情
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

    <!-- 航线详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="航线详情"
      width="600px"
    >
      <div v-if="selectedRoute" class="route-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="航线编号">
            {{ selectedRoute.routeNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="航线名称">
            {{ selectedRoute.name }}
          </el-descriptions-item>
          <el-descriptions-item label="起始港口">
            {{ selectedRoute.originPort ? selectedRoute.originPort.nameCn : '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="目标港口">
            {{ selectedRoute.destinationPort ? selectedRoute.destinationPort.nameCn : '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="航程距离">
            {{ selectedRoute.distance }} 公里
          </el-descriptions-item>
          <el-descriptions-item label="航程海里">
            {{ selectedRoute.distanceNm }} 海里
          </el-descriptions-item>
          <el-descriptions-item label="预计时间">
            {{ selectedRoute.estimatedDuration }} 小时
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedRoute.status === 1 ? 'success' : 'danger'">
              {{ selectedRoute.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="航线描述" :span="2">
            {{ selectedRoute.description || '暂无描述' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ selectedRoute.createdAt }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="route-actions">
          <el-button type="primary" @click="createOrderWithRoute">
            <el-icon><Plus /></el-icon>
            基于此航线创建订单
          </el-button>
          <el-button @click="viewRouteOnMap">
            <el-icon><Location /></el-icon>
            在地图中查看
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
import { Search, Refresh, View, Plus, Location } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  name: '',
  routeNumber: '',
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 详情弹窗
const detailVisible = ref(false)
const selectedRoute = ref(null)

// 加载航线数据
const loadRouteData = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    // 使用包含港口信息的接口
    const result = await request({
      url: '/routes/with-ports',
      method: 'get',
      params
    })
    
    if (result.code === 200) {
      tableData.value = result.data.records || []
      pagination.total = result.data.total || 0
    }
  } catch (error) {
    console.error('加载航线数据失败:', error)
    ElMessage.error('加载航线数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadRouteData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    routeNumber: '',
    status: null
  })
  pagination.page = 1
  loadRouteData()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadRouteData()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  loadRouteData()
}

// 查看航线详情
const viewRouteDetail = (route) => {
  selectedRoute.value = route
  detailVisible.value = true
}

// 基于此航线创建订单
const createOrderWithRoute = () => {
  detailVisible.value = false
  router.push({
    path: '/customer/orders/create',
    query: {
      routeId: selectedRoute.value.id,
      routeName: selectedRoute.value.name,
      originPortId: selectedRoute.value.originPortId,
      destinationPortId: selectedRoute.value.destinationPortId
    }
  })
}

// 在地图中查看航线
const viewRouteOnMap = () => {
  detailVisible.value = false
  // 可以传递航线的起始和终点坐标到地图页面
  if (selectedRoute.value.originPort && selectedRoute.value.destinationPort) {
    router.push({
      path: '/customer/ports/map',
      query: {
        originLat: selectedRoute.value.originPort.latitude,
        originLng: selectedRoute.value.originPort.longitude,
        destinationLat: selectedRoute.value.destinationPort.latitude,
        destinationLng: selectedRoute.value.destinationPort.longitude,
        routeName: selectedRoute.value.name
      }
    })
  } else {
    ElMessage.warning('航线港口信息不完整，无法在地图中显示')
  }
}

// 初始化
onMounted(() => {
  loadRouteData()
})
</script>

<style scoped>
.customer-route-list {
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

.route-detail {
  margin-bottom: 20px;
}

.route-actions {
  margin-top: 20px;
  text-align: center;
}

.route-actions .el-button {
  margin: 0 10px;
}

.el-table {
  margin-bottom: 20px;
}

.el-table :deep(.el-table__header-wrapper) {
  background-color: #fafafa;
}

@media (max-width: 768px) {
  .customer-route-list {
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