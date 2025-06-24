<template>
  <div class="customer-dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，{{ userInfo.username }}！</h2>
            <p>您的物流运输管理助手</p>
          </div>
          <div class="welcome-actions">
            <el-button type="primary" @click="goToCreateOrder">
              <el-icon><Plus /></el-icon>
              创建新订单
            </el-button>
            <el-button @click="goToMyOrders">
              <el-icon><Document /></el-icon>
              我的订单
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 数据概览 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon orders">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalOrders }}</div>
                <div class="stat-label">总订单数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon pending">
                <el-icon><InfoFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.pendingOrders }}</div>
                <div class="stat-label">待处理订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon shipping">
                <el-icon><Ship /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.shippingOrders }}</div>
                <div class="stat-label">运输中订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon completed">
                <el-icon><View /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.completedOrders }}</div>
                <div class="stat-label">已完成订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近订单 -->
    <div class="recent-section">
      <el-card>
        <template #header>
          <div class="section-header">
            <h3>最近订单</h3>
            <el-button type="text" @click="goToMyOrders">查看全部</el-button>
          </div>
        </template>
        <el-table :data="recentOrders" stripe style="width: 100%" empty-text="暂无订单">
          <el-table-column prop="orderNumber" label="订单号" width="150" />
          <el-table-column prop="route" label="航线" width="200" />
          <el-table-column prop="cargoType" label="货物类型" width="120" />
          <el-table-column prop="weight" label="重量(吨)" width="100" align="right" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="150" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="text" @click="viewOrder(row.id)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 快速入口 -->
    <div class="quick-actions">
      <el-card>
        <template #header>
          <h3>快速入口</h3>
        </template>
        <el-row :gutter="20">
          <el-col :span="4">
            <div class="quick-action-item" @click="goToPortList">
              <el-icon class="action-icon"><Location /></el-icon>
              <div class="action-text">港口查询</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="quick-action-item" @click="goToShipList">
              <el-icon class="action-icon"><Ship /></el-icon>
              <div class="action-text">船舶查询</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="quick-action-item" @click="goToRouteList">
              <el-icon class="action-icon"><Search /></el-icon>
              <div class="action-text">航线查询</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="quick-action-item" @click="goToVoyageList">
              <el-icon class="action-icon"><View /></el-icon>
              <div class="action-text">航次查询</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="quick-action-item" @click="goToMyOrders">
              <el-icon class="action-icon"><Document /></el-icon>
              <div class="action-text">我的订单</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="quick-action-item" @click="goToOrderCreate">
              <el-icon class="action-icon"><Plus /></el-icon>
              <div class="action-text">创建订单</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Plus, Document, View, Ship, Search, Location, 
  Position, User, InfoFilled, Refresh
} from '@element-plus/icons-vue'
import { getCustomerOrders, getCustomerOrderStats } from '@/api/order'

const router = useRouter()

// 用户信息
const userInfo = reactive({
  username: '客户用户',
  email: '',
  role: 'CUSTOMER'
})

// 统计数据
const stats = reactive({
  totalOrders: 0,
  pendingOrders: 0,
  shippingOrders: 0,
  completedOrders: 0
})

// 最近订单
const recentOrders = ref([])

// 获取用户信息
const getUserInfo = () => {
  try {
    const token = localStorage.getItem('token')
    const userStr = localStorage.getItem('user')
    if (token && userStr) {
      const user = JSON.parse(userStr)
      userInfo.username = user.username || '客户用户'
      userInfo.email = user.email || ''
      userInfo.role = user.role || 'CUSTOMER'
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取统计数据
const getStats = async () => {
  try {
    const userStr = localStorage.getItem('user')
    if (!userStr) {
      console.error('未找到用户信息')
      return
    }
    
    const user = JSON.parse(userStr)
    const userId = user.userId || user.id
    if (!userId) {
      console.error('未找到用户ID')
      return
    }
    
    // 使用新的统计接口
    const statsRes = await getCustomerOrderStats(userId)
    if (statsRes.code === 200 && statsRes.data) {
      const data = statsRes.data
      stats.totalOrders = data.totalOrders || 0
      stats.pendingOrders = data.pendingOrders || 0
      stats.shippingOrders = data.shippingOrders || 0
      stats.completedOrders = data.completedOrders || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 设置默认值
    stats.totalOrders = 0
    stats.pendingOrders = 0
    stats.shippingOrders = 0
    stats.completedOrders = 0
  }
}

// 获取最近订单
const getRecentOrders = async () => {
  try {
    const userStr = localStorage.getItem('user')
    if (!userStr) {
      console.error('未找到用户信息')
      recentOrders.value = []
      return
    }
    
    const user = JSON.parse(userStr)
    const userId = user.userId || user.id
    if (!userId) {
      console.error('未找到用户ID')
      recentOrders.value = []
      return
    }
    
    // 获取用户的所有订单
    const response = await getCustomerOrders(userId)
    if (response.code === 200 && response.data) {
      const orders = response.data
      
      // 按创建时间排序，取最近的两条
      const sortedOrders = orders.sort((a, b) => 
        new Date(b.createdAt) - new Date(a.createdAt)
      ).slice(0, 2)
      
      // 格式化订单数据
      recentOrders.value = sortedOrders.map(order => ({
        id: order.id,
        orderNumber: order.orderNumber,
        route: `${order.originPortName || '未知'} → ${order.destinationPortName || '未知'}`,
        cargoType: order.cargoType,
        weight: order.cargoWeight,
        status: order.status,
        createdAt: order.createdAt ? order.createdAt.substring(0, 16) : ''
      }))
    } else {
      recentOrders.value = []
    }
  } catch (error) {
    console.error('获取最近订单失败:', error)
    recentOrders.value = []
  }
}

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
    'PENDING_ASSIGNMENT': '待分配航次',
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'IN_TRANSIT': '运输中',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return textMap[status] || '未知'
}

// 导航方法
const goToMyOrders = () => {
  router.push('/customer/orders')
}

const goToPortList = () => {
  router.push('/customer/ports')
}

const goToShipList = () => {
  router.push('/customer/ships')
}

const goToRouteList = () => {
  router.push('/customer/routes')
}

const goToVoyageList = () => {
  router.push('/customer/voyages')
}

const goToOrderCreate = () => {
  router.push('/customer/orders/create')
}

const goToCreateOrder = () => {
  router.push('/customer/orders/create')
}

const viewOrder = (orderId) => {
  router.push(`/customer/orders/${orderId}`)
}

// 初始化
onMounted(() => {
  getUserInfo()
  getStats()
  getRecentOrders()
})
</script>

<style scoped>
.customer-dashboard {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.welcome-section {
  margin-bottom: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card :deep(.el-card__body) {
  padding: 30px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.welcome-actions {
  display: flex;
  gap: 15px;
}

.welcome-actions .el-button {
  padding: 12px 24px;
  font-size: 16px;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
}

.stat-icon.orders {
  background-color: #409eff;
}

.stat-icon.pending {
  background-color: #e6a23c;
}

.stat-icon.shipping {
  background-color: #909399;
}

.stat-icon.completed {
  background-color: #67c23a;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.recent-section {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.quick-actions {
  margin-bottom: 20px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;
  background-color: white;
}

.quick-action-item:hover {
  background-color: #f0f9ff;
  border-color: #409eff;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 10px;
}

.action-text {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .welcome-actions {
    justify-content: center;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style> 