<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总订单数</span>
              <el-tag size="small">本月</el-tag>
            </div>
          </template>
          <div class="card-content">
            <div class="number">128</div>
            <div class="trend">
              <span class="label">较上月</span>
              <span class="value up">+12.5%</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>在航船舶</span>
              <el-tag type="success" size="small">实时</el-tag>
            </div>
          </template>
          <div class="card-content">
            <div class="number">15</div>
            <div class="trend">
              <span class="label">航行中</span>
              <span class="value">75%</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>运输总量</span>
              <el-tag type="warning" size="small">本月</el-tag>
            </div>
          </template>
          <div class="card-content">
            <div class="number">25,630</div>
            <div class="trend">
              <span class="label">吨</span>
              <span class="value up">+8.2%</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="card-header">
              <span>营收金额</span>
              <el-tag type="info" size="small">本月</el-tag>
            </div>
          </template>
          <div class="card-content">
            <div class="number">￥1,256,800</div>
            <div class="trend">
              <span class="label">较上月</span>
              <span class="value up">+15.3%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>订单趋势</span>
              <el-radio-group v-model="chartTimeRange" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">全年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-placeholder">
            订单趋势图表
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热门航线</span>
            </div>
          </template>
          <div class="route-list">
            <div v-for="(route, index) in hotRoutes" :key="index" class="route-item">
              <span class="route-rank">{{ index + 1 }}</span>
              <span class="route-name">{{ route.name }}</span>
              <span class="route-value">{{ route.value }}单</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新动态 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新订单</span>
              <el-button text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="latestOrders" style="width: 100%" size="small">
            <el-table-column prop="orderNo" label="订单号" width="120" />
            <el-table-column prop="customer" label="客户" width="120" />
            <el-table-column prop="route" label="航线" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统通知</span>
              <el-button text>全部已读</el-button>
            </div>
          </template>
          <div class="notification-list">
            <div v-for="(notice, index) in notifications" :key="index" class="notice-item">
              <span class="notice-icon" :class="notice.type">{{ notice.type === 'warning' ? '⚠️' : 'ℹ️' }}</span>
              <span class="notice-content">{{ notice.content }}</span>
              <span class="notice-time">{{ notice.time }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const chartTimeRange = ref('month')

// 模拟数据
const hotRoutes = [
  { name: '上海 - 新加坡', value: 156 },
  { name: '青岛 - 釜山', value: 129 },
  { name: '宁波 - 洛杉矶', value: 98 },
  { name: '广州 - 马尼拉', value: 89 },
  { name: '天津 - 东京', value: 76 }
]

const latestOrders = [
  { orderNo: 'ORD20240101', customer: '张三物流', route: '上海-新加坡', status: '已确认' },
  { orderNo: 'ORD20240102', customer: '李四货运', route: '青岛-釜山', status: '运输中' },
  { orderNo: 'ORD20240103', customer: '王五船务', route: '宁波-洛杉矶', status: '待确认' },
  { orderNo: 'ORD20240104', customer: '赵六物流', route: '广州-马尼拉', status: '已完成' }
]

const notifications = [
  { type: 'warning', content: '船舶"海运明珠"即将到达新加坡港', time: '10分钟前' },
  { type: 'info', content: '新增订单等待确认', time: '30分钟前' },
  { type: 'warning', content: '青岛港区域天气预警', time: '1小时前' },
  { type: 'info', content: '系统将于今晚23:00进行例行维护', time: '2小时前' }
]

const getStatusType = (status) => {
  const statusMap = {
    '待确认': 'info',
    '已确认': 'primary',
    '运输中': 'warning',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  height: 180px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
  padding: 20px 0;
}

.number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.trend {
  font-size: 14px;
  color: #909399;
}

.trend .label {
  margin-right: 8px;
}

.trend .value.up {
  color: #67c23a;
}

.chart-row {
  margin-top: 20px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}

.route-list {
  padding: 0 20px;
}

.route-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.route-rank {
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background-color: #f5f7fa;
  border-radius: 12px;
  margin-right: 12px;
  font-size: 14px;
  color: #909399;
}

.route-name {
  flex: 1;
  color: #303133;
}

.route-value {
  color: #409eff;
  font-weight: bold;
}

.activity-row {
  margin-top: 20px;
}

.notification-list {
  padding: 0 20px;
}

.notice-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.notice-icon {
  font-size: 18px;
  margin-right: 12px;
  display: inline-block;
}

.notice-icon.warning {
  color: #e6a23c;
}

.notice-icon.info {
  color: #909399;
}

.notice-content {
  flex: 1;
  color: #303133;
}

.notice-time {
  color: #909399;
  font-size: 12px;
}
</style> 