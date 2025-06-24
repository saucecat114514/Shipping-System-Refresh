<template>
  <div class="order-assignment">
    <el-card>
      <template #header>
        <div class="card-header">
          <div>
            <h3>订单航次分配</h3>
            <p>为待分配航次的订单安排合适的航次</p>
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
          <el-form-item label="货物类型">
            <el-select
              v-model="searchForm.cargoType"
              placeholder="请选择货物类型"
              clearable
              style="width: 150px"
            >
              <el-option label="普通货物" value="普通货物" />
              <el-option label="危险品" value="危险品" />
              <el-option label="冷藏货物" value="冷藏货物" />
              <el-option label="液体货物" value="液体货物" />
              <el-option label="散装货物" value="散装货物" />
            </el-select>
          </el-form-item>
          <el-form-item label="是否加急">
            <el-select
              v-model="searchForm.isUrgent"
              placeholder="选择优先级"
              clearable
              style="width: 120px"
            >
              <el-option label="加急" :value="true" />
              <el-option label="普通" :value="false" />
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
        empty-text="暂无待分配订单"
      >
        <el-table-column prop="orderNumber" label="订单号" width="150" />
        <el-table-column label="客户" width="120">
          <template #default="{ row }">
            {{ row.customer?.username || '未知客户' }}
          </template>
        </el-table-column>
        <el-table-column prop="cargoName" label="货物名称" width="150" />
        <el-table-column prop="cargoType" label="货物类型" width="100" />
        <el-table-column prop="cargoWeight" label="重量(吨)" width="100" align="right" />
        <el-table-column prop="cargoVolume" label="体积(m³)" width="100" align="right" />
        <el-table-column label="港口信息" width="300">
          <template #default="{ row }">
            <div class="port-info">
              <div>出发：{{ getPortName(row.originPortId) }}</div>
              <div>目的：{{ getPortName(row.destinationPortId) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="是否加急" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isUrgent ? 'danger' : 'info'" size="small">
              {{ row.isUrgent ? '加急' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="assignVoyage(row)">
              <el-icon><Ship /></el-icon>
              分配航次
            </el-button>
            <el-button type="text" size="small" @click="viewOrderDetail(row)">
              <el-icon><View /></el-icon>
              详情
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

    <!-- 分配航次弹窗 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="分配航次"
      width="800px"
      @close="handleAssignDialogClose"
    >
      <div v-if="selectedOrder" class="assign-content">
        <!-- 订单信息 -->
        <el-descriptions title="订单信息" :column="2" border class="order-info">
          <el-descriptions-item label="订单号">
            {{ selectedOrder.orderNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="客户">
            {{ selectedOrder.customer?.username || '未知客户' }}
          </el-descriptions-item>
          <el-descriptions-item label="货物名称">
            {{ selectedOrder.cargoName }}
          </el-descriptions-item>
          <el-descriptions-item label="货物类型">
            {{ selectedOrder.cargoType }}
          </el-descriptions-item>
          <el-descriptions-item label="重量">
            {{ selectedOrder.cargoWeight }} 吨
          </el-descriptions-item>
          <el-descriptions-item label="体积">
            {{ selectedOrder.cargoVolume }} 立方米
          </el-descriptions-item>
          <el-descriptions-item label="出发港口">
            {{ getPortName(selectedOrder.originPortId) }}
          </el-descriptions-item>
          <el-descriptions-item label="目的港口">
            {{ getPortName(selectedOrder.destinationPortId) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 航次选择 -->
        <div class="voyage-selection">
          <h4>选择合适的航次</h4>
          <el-table 
            :data="availableVoyages" 
            v-loading="voyageLoading"
            @row-click="selectVoyage"
            highlight-current-row
            stripe
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="voyageNumber" label="航次编号" width="120" />
            <el-table-column label="航线" width="200">
              <template #default="{ row }">
                {{ row.route?.name || '未知航线' }}
              </template>
            </el-table-column>
            <el-table-column label="起止港口" width="250">
              <template #default="{ row }">
                <div>
                  {{ row.route?.originPort?.nameCn || '未知' }} → 
                  {{ row.route?.destinationPort?.nameCn || '未知' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="departureDate" label="发船时间" width="120" />
            <el-table-column prop="arrivalDate" label="到达时间" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getVoyageStatusType(row.status)" size="small">
                  {{ getVoyageStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmAssignment"
            :loading="assigning"
            :disabled="!selectedVoyage"
          >
            确认分配
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">
            {{ selectedOrder.orderNumber }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag type="warning">待分配航次</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="客户">
            {{ selectedOrder.customer?.username || '未知客户' }}
          </el-descriptions-item>
          <el-descriptions-item label="货物名称">
            {{ selectedOrder.cargoName }}
          </el-descriptions-item>
          <el-descriptions-item label="货物类型">
            {{ selectedOrder.cargoType }}
          </el-descriptions-item>
          <el-descriptions-item label="重量">
            {{ selectedOrder.cargoWeight }} 吨
          </el-descriptions-item>
          <el-descriptions-item label="体积">
            {{ selectedOrder.cargoVolume }} 立方米
          </el-descriptions-item>
          <el-descriptions-item label="是否加急">
            <el-tag :type="selectedOrder.isUrgent ? 'danger' : 'info'" size="small">
              {{ selectedOrder.isUrgent ? '加急' : '普通' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="出发港口">
            {{ getPortName(selectedOrder.originPortId) }}
          </el-descriptions-item>
          <el-descriptions-item label="目的港口">
            {{ getPortName(selectedOrder.destinationPortId) }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ selectedOrder.createdAt }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div v-if="selectedOrder.notes" class="notes-section">
          <h4>备注信息</h4>
          <p>{{ selectedOrder.notes }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Ship, View } from '@element-plus/icons-vue'
import { getPendingAssignmentOrders, assignVoyageToOrder } from '@/api/order'
import { getVoyageList } from '@/api/voyage'
import { getPortList } from '@/api/port'

// 加载状态
const loading = ref(false)
const voyageLoading = ref(false)
const assigning = ref(false)

// 弹窗状态
const assignDialogVisible = ref(false)
const detailDialogVisible = ref(false)

// 表格数据
const tableData = ref([])
const availableVoyages = ref([])
const portList = ref([])

// 选中的数据
const selectedOrder = ref(null)
const selectedVoyage = ref(null)

// 搜索表单
const searchForm = reactive({
  orderNumber: '',
  cargoType: '',
  isUrgent: null
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 初始化
onMounted(() => {
  loadOrderData()
  loadPorts()
})

// 加载待分配订单数据
const loadOrderData = async () => {
  try {
    loading.value = true
    const response = await getPendingAssignmentOrders({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    
    if (response.code === 200) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '加载订单数据失败')
    }
  } catch (error) {
    console.error('加载订单数据失败:', error)
    ElMessage.error('加载订单数据失败')
  } finally {
    loading.value = false
  }
}

// 加载港口列表
const loadPorts = async () => {
  try {
    const response = await getPortList({
      page: 1,
      size: 1000
    })
    if (response.code === 200) {
      portList.value = response.data.records || []
    }
  } catch (error) {
    console.error('加载港口列表失败:', error)
  }
}

// 根据港口ID获取港口名称
const getPortName = (portId) => {
  if (!portId) return '未选择'
  const port = portList.value.find(p => p.id === portId)
  return port ? `${port.nameCn} (${port.nameEn})` : '未知港口'
}

// 搜索处理
const handleSearch = () => {
  pagination.page = 1
  loadOrderData()
}

// 重置搜索
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  searchForm.isUrgent = null
  pagination.page = 1
  loadOrderData()
}

// 分页处理
const handleSizeChange = (val) => {
  pagination.size = val
  pagination.page = 1
  loadOrderData()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  loadOrderData()
}

// 查看订单详情
const viewOrderDetail = (order) => {
  selectedOrder.value = order
  detailDialogVisible.value = true
}

// 分配航次
const assignVoyage = async (order) => {
  selectedOrder.value = order
  selectedVoyage.value = null
  assignDialogVisible.value = true
  
  // 加载可用航次
  await loadAvailableVoyages(order)
}

// 加载可用航次
const loadAvailableVoyages = async (order) => {
  try {
    voyageLoading.value = true
    console.log('加载航次，订单港口信息:', {
      originPortId: order.originPortId,
      destinationPortId: order.destinationPortId
    })
    
    const response = await getVoyageList({
      page: 1,
      size: 100,
      status: 'PLANNED' // 只显示已计划的航次
    })
    
    if (response.code === 200) {
      console.log('所有航次数据:', response.data.records)
      
      // 过滤出符合港口要求的航次
      availableVoyages.value = (response.data.records || []).filter(voyage => {
        const isMatch = voyage.route && 
               voyage.route.originPort?.id === order.originPortId &&
               voyage.route.destinationPort?.id === order.destinationPortId
        
        console.log('航次匹配检查:', {
          voyageId: voyage.id,
          voyageNumber: voyage.voyageNumber,
          routeOriginPortId: voyage.route?.originPort?.id,
          routeDestinationPortId: voyage.route?.destinationPort?.id,
          orderOriginPortId: order.originPortId,
          orderDestinationPortId: order.destinationPortId,
          isMatch
        })
        
        return isMatch
      })
      
      console.log('筛选后的航次:', availableVoyages.value)
    }
  } catch (error) {
    console.error('加载航次数据失败:', error)
    ElMessage.error('加载航次数据失败')
  } finally {
    voyageLoading.value = false
  }
}

// 选择航次
const selectVoyage = (voyage) => {
  selectedVoyage.value = voyage
}

// 确认分配
const confirmAssignment = async () => {
  if (!selectedVoyage.value) {
    ElMessage.warning('请选择航次')
    return
  }

  try {
    assigning.value = true
    const response = await assignVoyageToOrder(selectedOrder.value.id, selectedVoyage.value.id)
    
    if (response.code === 200) {
      ElMessage.success('航次分配成功')
      assignDialogVisible.value = false
      loadOrderData()
    } else {
      ElMessage.error(response.msg || '分配航次失败')
    }
  } catch (error) {
    console.error('分配航次失败:', error)
    ElMessage.error('分配航次失败')
  } finally {
    assigning.value = false
  }
}

// 关闭分配弹窗
const handleAssignDialogClose = () => {
  selectedOrder.value = null
  selectedVoyage.value = null
  availableVoyages.value = []
}

// 航次状态类型
const getVoyageStatusType = (status) => {
  const statusMap = {
    'PLANNED': 'primary',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 航次状态文本
const getVoyageStatusText = (status) => {
  const statusMap = {
    'PLANNED': '已计划',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || '未知'
}
</script>

<style scoped>
.order-assignment {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.card-header h3 {
  margin: 0 0 5px 0;
  color: #303133;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
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

.port-info div {
  margin: 2px 0;
  font-size: 13px;
}

.assign-content {
  max-height: 500px;
  overflow-y: auto;
}

.order-info {
  margin-bottom: 20px;
}

.voyage-selection h4 {
  margin: 20px 0 10px 0;
  color: #303133;
}

.notes-section {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.notes-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.notes-section p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

:deep(.el-table) {
  margin-bottom: 10px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
}
</style> 