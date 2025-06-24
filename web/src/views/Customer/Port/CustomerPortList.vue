<template>
  <div class="customer-port-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>港口信息</h3>
          <p>查看全球港口信息和位置坐标</p>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="港口名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入港口名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="国家">
            <el-input
              v-model="searchForm.country"
              placeholder="请输入国家名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="港口代码">
            <el-input
              v-model="searchForm.code"
              placeholder="请输入港口代码"
              clearable
              style="width: 200px"
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
        empty-text="暂无港口数据"
      >
        <el-table-column prop="nameCn" label="港口名称(中)" min-width="120" />
        <el-table-column prop="nameEn" label="港口名称(英)" min-width="150" />
        <el-table-column prop="code" label="港口代码" width="100" />
        <el-table-column prop="country" label="所属国家" width="120" />
        <el-table-column label="坐标位置" width="200">
          <template #default="{ row }">
            <span>{{ formatCoordinates(row.longitude, row.latitude) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="更新时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="text" @click="viewPortDetail(row)">
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

    <!-- 港口详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="港口详情"
      width="500px"
    >
      <div v-if="selectedPort" class="port-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="港口名称(中)">
            {{ selectedPort.nameCn }}
          </el-descriptions-item>
          <el-descriptions-item label="港口名称(英)">
            {{ selectedPort.nameEn }}
          </el-descriptions-item>
          <el-descriptions-item label="港口代码">
            {{ selectedPort.code }}
          </el-descriptions-item>
          <el-descriptions-item label="所属国家">
            {{ selectedPort.country }}
          </el-descriptions-item>
          <el-descriptions-item label="经度">
            {{ selectedPort.longitude }}°
          </el-descriptions-item>
          <el-descriptions-item label="纬度">
            {{ selectedPort.latitude }}°
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ selectedPort.createdAt }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="port-actions">
          <el-button type="primary" @click="goToPortMap">
            <el-icon><Location /></el-icon>
            在地图中查看
          </el-button>
          <el-button @click="createOrderWithPort">
            <el-icon><Plus /></el-icon>
            以此港口创建订单
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
import { Search, Refresh, View, Location, Plus } from '@element-plus/icons-vue'
import { getPortList } from '@/api/port'

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  name: '',
  country: '',
  code: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 详情弹窗
const detailVisible = ref(false)
const selectedPort = ref(null)

// 格式化坐标显示
const formatCoordinates = (longitude, latitude) => {
  if (!longitude || !latitude) return '-'
  return `${longitude}°, ${latitude}°`
}

// 加载港口数据
const loadPortData = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    const result = await getPortList(params)
    
    if (result.code === 200) {
      tableData.value = result.data.records || []
      pagination.total = result.data.total || 0
    }
  } catch (error) {
    console.error('加载港口数据失败:', error)
    ElMessage.error('加载港口数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadPortData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    country: '',
    code: ''
  })
  pagination.page = 1
  loadPortData()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadPortData()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  loadPortData()
}

// 查看港口详情
const viewPortDetail = (port) => {
  selectedPort.value = port
  detailVisible.value = true
}

// 跳转到港口地图
const goToPortMap = () => {
  detailVisible.value = false
  // 可以传递选中的港口坐标到地图页面
  router.push({
    path: '/customer/ports/map',
    query: {
      lat: selectedPort.value.latitude,
      lng: selectedPort.value.longitude,
      name: selectedPort.value.nameCn
    }
  })
}

// 以此港口创建订单
const createOrderWithPort = () => {
  detailVisible.value = false
  router.push({
    path: '/customer/orders/create',
    query: {
      portId: selectedPort.value.id,
      portName: selectedPort.value.nameCn
    }
  })
}

// 初始化
onMounted(() => {
  loadPortData()
})
</script>

<style scoped>
.customer-port-list {
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

.port-detail {
  margin-bottom: 20px;
}

.port-actions {
  margin-top: 20px;
  text-align: center;
}

.port-actions .el-button {
  margin: 0 10px;
}

.el-table {
  margin-bottom: 20px;
}

.el-table :deep(.el-table__header-wrapper) {
  background-color: #fafafa;
}

@media (max-width: 768px) {
  .customer-port-list {
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