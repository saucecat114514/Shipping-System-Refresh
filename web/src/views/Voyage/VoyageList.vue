<template>
  <div class="voyage-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">航次管理</span>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索航次号/船舶名称"
              prefix-icon="Search"
              style="width: 200px; margin-left: 16px"
              size="small"
            />
          </div>
          <el-button type="primary" @click="handleCreateVoyage">
            <el-icon><Plus /></el-icon>新增航次
          </el-button>
        </div>
      </template>

      <!-- 过滤条件 -->
      <el-form :inline="true" class="filter-form">
        <el-form-item label="航次状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="未开始" value="NOT_STARTED" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="航线">
          <el-select v-model="filters.routeId" placeholder="全部航线" clearable>
            <el-option v-for="route in routes" :key="route.id" :label="route.name" :value="route.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="船舶">
          <el-select v-model="filters.shipId" placeholder="全部船舶" clearable>
            <el-option v-for="ship in ships" :key="ship.id" :label="ship.name" :value="ship.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划时间">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 航次列表 -->
      <el-table :data="voyages" style="width: 100%" border>
        <el-table-column prop="voyageNo" label="航次编号" width="150" />
        <el-table-column prop="routeName" label="航线名称" width="150" />
        <el-table-column prop="shipName" label="船舶名称" width="120" />
        <el-table-column label="航线" min-width="200">
          <template #default="scope">
            {{ scope.row.startPort }} → {{ scope.row.endPort }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plannedDepartureTime" label="计划出发" width="180" />
        <el-table-column prop="plannedArrivalTime" label="计划到达" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(scope.row)" v-if="scope.row.status === 'NOT_STARTED'">编辑</el-button>
            <el-button link type="success" @click="handleStart(scope.row)" v-if="scope.row.status === 'NOT_STARTED'">开始</el-button>
            <el-button link type="danger" @click="handleCancel(scope.row)" v-if="scope.row.status === 'NOT_STARTED'">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'

const router = useRouter()

// 搜索和过滤
const searchKeyword = ref('')
const filters = reactive({
  status: '',
  routeId: '',
  shipId: '',
  dateRange: []
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟数据
const routes = [
  { id: 1, name: '上海-新加坡快线' },
  { id: 2, name: '青岛-釜山快线' }
]

const ships = [
  { id: 1, name: '远洋1号' },
  { id: 2, name: '远洋2号' }
]

const voyages = [
  {
    voyageNo: 'V202401150001',
    routeName: '上海-新加坡快线',
    shipName: '远洋1号',
    startPort: '上海港',
    endPort: '新加坡港',
    status: 'NOT_STARTED',
    plannedDepartureTime: '2024-01-20 10:00:00',
    plannedArrivalTime: '2024-01-25 10:00:00'
  },
  {
    voyageNo: 'V202401150002',
    routeName: '青岛-釜山快线',
    shipName: '远洋2号',
    startPort: '青岛港',
    endPort: '釜山港',
    status: 'IN_PROGRESS',
    plannedDepartureTime: '2024-01-21 14:00:00',
    plannedArrivalTime: '2024-01-22 14:00:00'
  }
]

// 获取状态标签
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'NOT_STARTED': 'warning',
    'IN_PROGRESS': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 处理分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // TODO: 重新加载数据
}

// 处理搜索
const handleSearch = () => {
  // TODO: 调用搜索API
  console.log('搜索条件:', {
    keyword: searchKeyword.value,
    ...filters
  })
}

// 处理重置
const handleReset = () => {
  searchKeyword.value = ''
  Object.assign(filters, {
    status: '',
    routeId: '',
    shipId: '',
    dateRange: []
  })
}

// 处理创建航次
const handleCreateVoyage = () => {
  router.push('/voyages/create')
}

// 处理查看航次
const handleView = (row: any) => {
  // TODO: 实现查看详情功能
  console.log('查看航次:', row)
}

// 处理编辑航次
const handleEdit = (row: any) => {
  // TODO: 实现编辑功能
  console.log('编辑航次:', row)
}

// 处理开始航次
const handleStart = (row: any) => {
  ElMessageBox.confirm(
    `确定要开始航次"${row.voyageNo}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // TODO: 调用开始航次API
    ElMessage.success('航次已开始')
  })
}

// 处理取消航次
const handleCancel = (row: any) => {
  ElMessageBox.confirm(
    `确定要取消航次"${row.voyageNo}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用取消API
    ElMessage.success('航次已取消')
  })
}
</script>

<style scoped>
.voyage-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.filter-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 