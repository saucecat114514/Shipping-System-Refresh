<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">订单管理</span>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索订单号/客户名称"
              prefix-icon="Search"
              style="width: 200px; margin-left: 16px"
              size="small"
            />
          </div>
          <el-button type="primary" @click="handleCreateOrder">
            <el-icon><Plus /></el-icon>新增订单
          </el-button>
        </div>
      </template>

      <!-- 过滤条件 -->
      <el-form :inline="true" class="filter-form">
        <el-form-item label="订单状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="运输中" value="IN_TRANSIT" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="运输方式">
          <el-select v-model="filters.transportMode" placeholder="全部方式" clearable>
            <el-option label="海运" value="SEA" />
            <el-option label="空运" value="AIR" />
            <el-option label="陆运" value="LAND" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
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

      <!-- 订单列表 -->
      <el-table :data="orders" style="width: 100%" border>
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="customerName" label="客户名称" width="150" />
        <el-table-column label="运输路线" min-width="200">
          <template #default="scope">
            {{ scope.row.startPort }} → {{ scope.row.endPort }}
          </template>
        </el-table-column>
        <el-table-column prop="transportMode" label="运输方式" width="100">
          <template #default="scope">
            <el-tag>{{ getTransportModeLabel(scope.row.transportMode) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedDeliveryDate" label="预计发货日期" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(scope.row)" v-if="scope.row.status === 'PENDING'">编辑</el-button>
            <el-button link type="danger" @click="handleCancel(scope.row)" v-if="scope.row.status === 'PENDING'">取消</el-button>
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
  transportMode: '',
  dateRange: []
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟数据
const orders = [
  {
    orderNo: 'ORD202401150001',
    customerName: '张三物流',
    startPort: '上海港',
    endPort: '新加坡港',
    transportMode: 'SEA',
    status: 'PENDING',
    expectedDeliveryDate: '2024-01-20',
    createTime: '2024-01-15 10:30:00'
  },
  {
    orderNo: 'ORD202401150002',
    customerName: '李四货运',
    startPort: '青岛港',
    endPort: '釜山港',
    transportMode: 'SEA',
    status: 'CONFIRMED',
    expectedDeliveryDate: '2024-01-21',
    createTime: '2024-01-15 11:20:00'
  }
]

// 获取运输方式标签
const getTransportModeLabel = (mode: string) => {
  const modeMap: Record<string, string> = {
    'SEA': '海运',
    'AIR': '空运',
    'LAND': '陆运'
  }
  return modeMap[mode] || mode
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'IN_TRANSIT': '运输中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'PENDING': 'warning',
    'CONFIRMED': 'primary',
    'IN_TRANSIT': 'success',
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
    transportMode: '',
    dateRange: []
  })
}

// 处理创建订单
const handleCreateOrder = () => {
  router.push('/orders/create')
}

// 处理查看订单
const handleView = (row: any) => {
  // TODO: 实现查看详情功能
  console.log('查看订单:', row)
}

// 处理编辑订单
const handleEdit = (row: any) => {
  // TODO: 实现编辑功能
  console.log('编辑订单:', row)
}

// 处理取消订单
const handleCancel = (row: any) => {
  ElMessageBox.confirm(
    `确定要取消订单"${row.orderNo}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用取消API
    ElMessage.success('订单已取消')
  }).catch(() => {
    // 取消操作
  })
}
</script>

<style scoped>
.order-list {
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