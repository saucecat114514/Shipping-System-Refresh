<template>
  <div class="customer-my-orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>我的订单</h3>
          <p>管理您的订单，查看订单状态和详情</p>
          <div class="header-actions">
            <el-button type="primary" @click="createNewOrder">
              <el-icon><Plus /></el-icon>
              创建新订单
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="订单号">
            <el-input
              v-model="searchForm.orderNumber"
              placeholder="请输入订单号"
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
              <el-option label="待处理" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
              <el-option label="运输中" value="SHIPPING" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="searchForm.createDate"
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
        empty-text="暂无订单数据"
      >
        <el-table-column prop="orderNumber" label="订单号" width="150" />
        <el-table-column prop="cargoType" label="货物类型" width="120" />
        <el-table-column prop="cargoWeight" label="重量(吨)" width="100" align="right" />
        <el-table-column prop="cargoVolume" label="体积(立方米)" width="120" align="right" />
        <el-table-column label="航线" width="200">
          <template #default="{ row }">
            {{ row.originPortName || '未知' }} → {{ row.destinationPortName || '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价(元)" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="viewOrderDetail(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button 
              type="text" 
              @click="editOrder(row)"
              :disabled="!canEdit(row.status)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              type="text" 
              style="color: #f56c6c;"
              @click="cancelOrder(row)"
              :disabled="!canCancel(row.status)"
            >
              <el-icon><Close /></el-icon>
              取消
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

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="700px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">
            {{ selectedOrder.orderNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedOrder.status)">
              {{ getStatusText(selectedOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="货物类型">
            {{ selectedOrder.cargoType }}
          </el-descriptions-item>
          <el-descriptions-item label="货物名称">
            {{ selectedOrder.cargoName }}
          </el-descriptions-item>
          <el-descriptions-item label="重量">
            {{ selectedOrder.cargoWeight }} 吨
          </el-descriptions-item>
          <el-descriptions-item label="体积">
            {{ selectedOrder.cargoVolume }} 立方米
          </el-descriptions-item>
          <el-descriptions-item label="起始港口">
            {{ selectedOrder.originPortName || '未分配' }}
          </el-descriptions-item>
          <el-descriptions-item label="目标港口">
            {{ selectedOrder.destinationPortName || '未分配' }}
          </el-descriptions-item>
          <el-descriptions-item label="预计发船时间">
            {{ selectedOrder.voyage?.departureDate || '未安排' }}
          </el-descriptions-item>
          <el-descriptions-item label="预计到达时间">
            {{ selectedOrder.voyage?.arrivalDate || '未安排' }}
          </el-descriptions-item>
          <el-descriptions-item label="船舶名称">
            {{ selectedOrder.shipName || '未分配' }}
          </el-descriptions-item>
          <el-descriptions-item label="航次编号">
            {{ selectedOrder.voyage?.voyageNumber || '未分配' }}
          </el-descriptions-item>
          <el-descriptions-item label="基础运费">
            ¥{{ selectedOrder.basePrice }}
          </el-descriptions-item>
          <el-descriptions-item label="附加费用">
            ¥{{ selectedOrder.additionalFees }}
          </el-descriptions-item>
          <el-descriptions-item label="总价">
            <span style="font-size: 16px; font-weight: bold; color: #e6a23c;">
              ¥{{ selectedOrder.totalPrice }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ selectedOrder.createdAt }}
          </el-descriptions-item>
          <el-descriptions-item label="备注信息" :span="2">
            {{ selectedOrder.notes || '暂无备注' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="order-actions">
          <el-button 
            type="primary" 
            @click="editOrder(selectedOrder)"
            :disabled="!canEdit(selectedOrder.status)"
          >
            <el-icon><Edit /></el-icon>
            编辑订单
          </el-button>
          <el-button 
            type="danger" 
            @click="cancelOrder(selectedOrder)"
            :disabled="!canCancel(selectedOrder.status)"
          >
            <el-icon><Close /></el-icon>
            取消订单
          </el-button>
          <el-button @click="trackOrder(selectedOrder)">
            <el-icon><Position /></el-icon>
            追踪订单
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, Refresh, View, Edit, Close, Plus, Position 
} from '@element-plus/icons-vue'
import { getCustomerOrders, cancelOrder as cancelOrderApi } from '@/api/order'

const router = useRouter()

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  orderNumber: '',
  status: '',
  createDate: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 详情弹窗
const detailVisible = ref(false)
const selectedOrder = ref(null)

// 状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'info',
    'SHIPPING': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待处理',
    'CONFIRMED': '已确认',
    'SHIPPING': '运输中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

// 检查是否可以编辑
const canEdit = (status) => {
  return ['PENDING', 'CONFIRMED'].includes(status)
}

// 检查是否可以取消
const canCancel = (status) => {
  return ['PENDING', 'CONFIRMED'].includes(status)
}

// 存储所有订单数据（用于前端分页和筛选）
const allOrders = ref([])

// 加载订单数据
const loadOrderData = async () => {
  try {
    loading.value = true
    
    // 获取当前用户ID
    const userStr = localStorage.getItem('user')
    if (!userStr) {
      ElMessage.error('未找到用户信息，请重新登录')
      return
    }
    
    let userId
    try {
      const user = JSON.parse(userStr)
      userId = user.userId || user.id
      if (!userId) {
        ElMessage.error('用户ID缺失，请重新登录')
        return
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
      ElMessage.error('用户信息格式错误，请重新登录')
      return
    }
    
    // 准备状态参数
    const status = searchForm.status || null
    
    // 调用API获取订单数据
    const result = await getCustomerOrders(userId, status)
    
    if (result.code === 200) {
      allOrders.value = result.data || []
      applyFiltersAndPagination()
    }
  } catch (error) {
    console.error('加载订单数据失败:', error)
    ElMessage.error('加载订单数据失败')
  } finally {
    loading.value = false
  }
}

// 应用筛选和分页
const applyFiltersAndPagination = () => {
  let orders = [...allOrders.value]
  
  // 前端进行筛选
  if (searchForm.orderNumber) {
    orders = orders.filter(order => 
      order.orderNumber && order.orderNumber.includes(searchForm.orderNumber)
    )
  }
  
  if (searchForm.createDate && searchForm.createDate.length === 2) {
    const startDate = new Date(searchForm.createDate[0])
    const endDate = new Date(searchForm.createDate[1])
    orders = orders.filter(order => {
      const orderDate = new Date(order.createdAt)
      return orderDate >= startDate && orderDate <= endDate
    })
  }
  
  // 分页处理
  pagination.total = orders.length
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  tableData.value = orders.slice(start, end)
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  applyFiltersAndPagination()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    orderNumber: '',
    status: '',
    createDate: null
  })
  pagination.page = 1
  loadOrderData()  // 重置需要重新加载数据
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  applyFiltersAndPagination()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.page = page
  applyFiltersAndPagination()  // 重新应用分页
}

// 查看订单详情
const viewOrderDetail = (order) => {
  selectedOrder.value = order
  detailVisible.value = true
}

// 编辑订单
const editOrder = (order) => {
  if (detailVisible.value) {
    detailVisible.value = false
  }
  
  router.push({
    path: '/customer/orders/edit',
    query: {
      orderId: order.id
    }
  })
}

// 取消订单
const cancelOrder = async (order) => {
  if (detailVisible.value) {
    detailVisible.value = false
  }
  
  try {
    await ElMessageBox.confirm(
      `确认要取消订单 ${order.orderNumber} 吗？`,
      '取消订单',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await cancelOrderApi(order.id)
    
    if (result.code === 200) {
      ElMessage.success('订单取消成功')
      loadOrderData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 追踪订单
const trackOrder = (order) => {
  if (detailVisible.value) {
    detailVisible.value = false
  }
  
  router.push({
    path: '/customer/ships/tracking',
    query: {
      orderId: order.id,
      orderNumber: order.orderNumber
    }
  })
}

// 创建新订单
const createNewOrder = () => {
  router.push('/customer/orders/create')
}



// 初始化
onMounted(() => {
  loadOrderData()
})
</script>

<style scoped>
.customer-my-orders {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.header-actions {
  flex-shrink: 0;
  margin-left: 20px;
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

.order-detail {
  margin-bottom: 20px;
}

.order-actions {
  margin-top: 20px;
  text-align: center;
}

.order-actions .el-button {
  margin: 0 10px;
}

.el-table {
  margin-bottom: 20px;
}

.el-table :deep(.el-table__header-wrapper) {
  background-color: #fafafa;
}

@media (max-width: 768px) {
  .customer-my-orders {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    margin-left: 0;
    margin-top: 15px;
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