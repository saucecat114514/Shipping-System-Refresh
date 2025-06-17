<template>
  <div class="ship-tracking">
    <el-card class="map-card">
      <template #header>
        <div class="card-header">
          <span>船舶实时位置</span>
          <el-button-group>
            <el-button type="primary" plain size="small">刷新</el-button>
            <el-button type="success" plain size="small">全部船舶</el-button>
          </el-button-group>
        </div>
      </template>
      <div class="map-container">
        <!-- 地图将在此处显示 -->
        <div class="map-placeholder">
          地图加载中...
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="info-row">
      <el-col :span="8">
        <el-card class="ship-list">
          <template #header>
            <div class="card-header">
              <span>船舶列表</span>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索船舶"
                prefix-icon="Search"
                size="small"
                style="width: 200px"
              />
            </div>
          </template>
          <el-table :data="ships" style="width: 100%" size="small" height="calc(100vh - 400px)">
            <el-table-column prop="name" label="船名" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button link type="primary" @click="focusShip(scope.row)">
                  定位
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="ship-detail" v-if="selectedShip">
          <template #header>
            <div class="card-header">
              <span>船舶详情 - {{ selectedShip.name }}</span>
              <el-tag>{{ selectedShip.status }}</el-tag>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="IMO编号">{{ selectedShip.imo }}</el-descriptions-item>
            <el-descriptions-item label="船舶类型">{{ selectedShip.type }}</el-descriptions-item>
            <el-descriptions-item label="当前位置">{{ selectedShip.position }}</el-descriptions-item>
            <el-descriptions-item label="目的港">{{ selectedShip.destination }}</el-descriptions-item>
            <el-descriptions-item label="预计到达时间">{{ selectedShip.eta }}</el-descriptions-item>
            <el-descriptions-item label="航速">{{ selectedShip.speed }} 节</el-descriptions-item>
            <el-descriptions-item label="航向">{{ selectedShip.course }}°</el-descriptions-item>
            <el-descriptions-item label="最后更新时间">{{ selectedShip.lastUpdate }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'

const searchKeyword = ref('')
const selectedShip = ref(null)

// 模拟数据
const ships = [
  {
    id: 1,
    name: '海运之星',
    status: '航行中',
    imo: 'IMO9876543',
    type: '集装箱船',
    position: '北纬 31°12\'，东经 121°30\'',
    destination: '新加坡港',
    eta: '2024-01-20 14:30',
    speed: 15.5,
    course: 175,
    lastUpdate: '2024-01-15 10:30'
  },
  {
    id: 2,
    name: '远洋号',
    status: '靠港中',
    imo: 'IMO9876544',
    type: '散货船',
    position: '青岛港',
    destination: '釜山港',
    eta: '2024-01-21 08:00',
    speed: 0,
    course: 0,
    lastUpdate: '2024-01-15 10:30'
  }
]

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '航行中': 'success',
    '靠港中': 'info',
    '待机中': 'warning',
    '维修中': 'danger'
  }
  return statusMap[status] || 'info'
}

const focusShip = (ship: any) => {
  selectedShip.value = ship
  // TODO: 在地图上定位到该船舶
}
</script>

<style scoped>
.ship-tracking {
  padding: 20px;
}

.map-card {
  margin-bottom: 20px;
}

.map-container {
  height: 400px;
  background-color: #f5f7fa;
}

.map-placeholder {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-row {
  margin-top: 20px;
}

.ship-list {
  height: calc(100vh - 580px);
}

:deep(.el-card__body) {
  padding: 10px;
}

:deep(.el-descriptions) {
  padding: 15px;
}
</style> 