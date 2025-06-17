<template>
  <div class="port-map">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="port-list">
          <template #header>
            <div class="card-header">
              <span>港口列表</span>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索港口"
                prefix-icon="Search"
                size="small"
              />
            </div>
          </template>
          <el-table :data="ports" style="width: 100%" size="small" height="calc(100vh - 200px)">
            <el-table-column prop="name" label="港口名称" />
            <el-table-column prop="country" label="所在国家" />
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button link type="primary" @click="focusPort(scope.row)">
                  定位
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card class="map-card">
          <template #header>
            <div class="card-header">
              <span>港口分布</span>
              <el-radio-group v-model="mapView" size="small">
                <el-radio-button label="all">全部港口</el-radio-button>
                <el-radio-button label="major">主要港口</el-radio-button>
                <el-radio-button label="route">航线港口</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="map-container">
            <!-- 地图将在此处显示 -->
            <div class="map-placeholder">
              地图加载中...
            </div>
          </div>
        </el-card>

        <el-card v-if="selectedPort" class="port-detail">
          <template #header>
            <div class="card-header">
              <span>港口详情 - {{ selectedPort.name }}</span>
              <el-tag>{{ selectedPort.status }}</el-tag>
            </div>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="港口代码">{{ selectedPort.code }}</el-descriptions-item>
            <el-descriptions-item label="所在国家">{{ selectedPort.country }}</el-descriptions-item>
            <el-descriptions-item label="时区">{{ selectedPort.timezone }}</el-descriptions-item>
            <el-descriptions-item label="经度">{{ selectedPort.longitude }}</el-descriptions-item>
            <el-descriptions-item label="纬度">{{ selectedPort.latitude }}</el-descriptions-item>
            <el-descriptions-item label="联系方式">{{ selectedPort.contact }}</el-descriptions-item>
          </el-descriptions>

          <el-divider>港口状态</el-divider>
          
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="在港船舶" :value="selectedPort.stats.ships" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="今日到港" :value="selectedPort.stats.arrivals" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="今日离港" :value="selectedPort.stats.departures" />
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'

const searchKeyword = ref('')
const mapView = ref('all')
const selectedPort = ref(null)

// 模拟数据
const ports = [
  {
    id: 1,
    name: '上海港',
    code: 'SHA',
    country: '中国',
    status: '正常运营',
    timezone: 'UTC+8',
    longitude: '121.4737',
    latitude: '31.2304',
    contact: '+86-21-12345678',
    stats: {
      ships: 45,
      arrivals: 12,
      departures: 8
    }
  },
  {
    id: 2,
    name: '新加坡港',
    code: 'SIN',
    country: '新加坡',
    status: '正常运营',
    timezone: 'UTC+8',
    longitude: '103.8198',
    latitude: '1.2755',
    contact: '+65-12345678',
    stats: {
      ships: 62,
      arrivals: 15,
      departures: 13
    }
  }
]

const focusPort = (port: any) => {
  selectedPort.value = port
  // TODO: 在地图上定位到该港口
}
</script>

<style scoped>
.port-map {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.map-card {
  margin-bottom: 20px;
}

.map-container {
  height: 500px;
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

:deep(.el-card__body) {
  padding: 15px;
}

.port-detail {
  margin-top: 20px;
}

:deep(.el-descriptions) {
  margin-bottom: 20px;
}

.el-divider {
  margin: 24px 0;
}

:deep(.el-input) {
  width: 180px;
}

:deep(.el-statistic) {
  text-align: center;
}

:deep(.el-statistic__title) {
  color: #606266;
}
</style> 