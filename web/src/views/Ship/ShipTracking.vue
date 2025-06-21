<template>
  <div class="ship-tracking">
    <el-card class="map-card">
      <template #header>
        <div class="card-header">
          <span>船舶实时位置</span>
          <el-button-group>
            <el-button type="primary" plain size="small" @click="refreshMap">刷新</el-button>
            <el-button type="success" plain size="small" @click="showAllShips">全部船舶</el-button>
          </el-button-group>
        </div>
      </template>
      <div class="map-container">
        <!-- 高德地图容器 -->
        <div id="ship-map" class="amap-container"></div>
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
          <el-table :data="filteredShips" style="width: 100%" size="small" height="calc(100vh - 400px)">
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
            <el-descriptions-item label="当前位置">
              {{ selectedShip.longitude?.toFixed(4) }}, {{ selectedShip.latitude?.toFixed(4) }}
            </el-descriptions-item>
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
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { MAP_CONFIG } from '@/utils/constants'

// 响应式数据
const searchKeyword = ref('')
const selectedShip = ref(null)
let map = null
let markers = []
let infoWindow = null

// 船舶数据（扩展版本）
const ships = [
  {
    id: 1,
    name: '海运之星',
    status: '航行中',
    imo: 'IMO9876543',
    type: '集装箱船',
    longitude: 121.5,
    latitude: 31.3,
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
    longitude: 120.3826,
    latitude: 36.0673,
    destination: '釜山港',
    eta: '2024-01-21 08:00',
    speed: 0,
    course: 0,
    lastUpdate: '2024-01-15 10:30'
  },
  {
    id: 3,
    name: '蓝海号',
    status: '航行中',
    imo: 'IMO9876545',
    type: '油轮',
    longitude: 114.2,
    latitude: 22.6,
    destination: '香港港',
    eta: '2024-01-19 18:00',
    speed: 12.8,
    course: 90,
    lastUpdate: '2024-01-15 10:25'
  },
  {
    id: 4,
    name: '星辰号',
    status: '航行中',
    imo: 'IMO9876546',
    type: '集装箱船',
    longitude: 103.8,
    latitude: 1.3,
    destination: '马来西亚港',
    eta: '2024-01-22 09:15',
    speed: 18.2,
    course: 210,
    lastUpdate: '2024-01-15 10:20'
  },
  {
    id: 5,
    name: '海峰号',
    status: '维修中',
    imo: 'IMO9876547',
    type: '散货船',
    longitude: 121.4648,
    latitude: 31.2304,
    destination: '上海港',
    eta: '2024-01-25 12:00',
    speed: 0,
    course: 0,
    lastUpdate: '2024-01-15 09:45'
  }
]

// 过滤后的船舶列表
const filteredShips = computed(() => {
  if (!searchKeyword.value) {
    return ships
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return ships.filter(ship => 
    ship.name.toLowerCase().includes(keyword) ||
    ship.imo.toLowerCase().includes(keyword) ||
    ship.type.toLowerCase().includes(keyword) ||
    ship.destination.toLowerCase().includes(keyword)
  )
})

// 初始化高德地图
const initMap = async () => {
  try {
    // 创建地图实例
    map = new window.AMap.Map('ship-map', {
      zoom: 6,
      center: MAP_CONFIG.CENTER,
      mapStyle: 'amap://styles/normal'
    })

    // 创建信息窗口
    infoWindow = new window.AMap.InfoWindow({
      isCustom: false,
      autoMove: true
    })

    // 地图加载完成后添加船舶标记
    map.on('complete', () => {
      console.log('船舶追踪地图加载完成')
      addShipMarkers()
    })

    ElMessage.success('船舶追踪地图加载成功')
  } catch (error) {
    console.error('船舶追踪地图初始化失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接')
  }
}

// 获取船舶状态对应的图标
const getShipIcon = (status: string) => {
  const iconMap = {
    '航行中': 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
    '靠港中': 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
    '待机中': 'https://webapi.amap.com/theme/v1.3/markers/n/mark_g.png',
    '维修中': 'https://webapi.amap.com/theme/v1.3/markers/n/mark_y.png'
  }
  return iconMap[status] || iconMap['航行中']
}

// 添加船舶标记
const addShipMarkers = () => {
  // 清除现有标记
  clearMarkers()

  const shipsToShow = filteredShips.value

  shipsToShow.forEach(ship => {
    // 创建标记点
    const marker = new window.AMap.Marker({
      position: [ship.longitude, ship.latitude],
      title: ship.name,
      icon: new window.AMap.Icon({
        image: getShipIcon(ship.status),
        size: new window.AMap.Size(25, 34),
        imageSize: new window.AMap.Size(25, 34),
        imageOffset: new window.AMap.Pixel(0, 0)
      })
    })

    // 添加到地图
    marker.setMap(map)
    markers.push(marker)

    // 点击标记显示信息窗口
    marker.on('click', () => {
      const content = `
        <div style="padding: 12px; min-width: 220px;">
          <h4 style="margin: 0 0 10px 0; color: #333;">🚢 ${ship.name}</h4>
          <p style="margin: 5px 0; color: #666; line-height: 1.6;">
            <strong>IMO编号:</strong> ${ship.imo}<br>
            <strong>船舶类型:</strong> ${ship.type}<br>
            <strong>状态:</strong> <span style="color: ${getStatusColor(ship.status)}; font-weight: bold;">${ship.status}</span><br>
            <strong>坐标:</strong> ${ship.longitude.toFixed(4)}, ${ship.latitude.toFixed(4)}<br>
            <strong>目的港:</strong> ${ship.destination}
          </p>
          <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
            <strong>航行信息:</strong><br>
            航速: ${ship.speed} 节 | 航向: ${ship.course}°<br>
            <small style="color: #999;">最后更新: ${ship.lastUpdate}</small>
          </div>
        </div>
      `
      infoWindow.setContent(content)
      infoWindow.open(map, marker.getPosition())
      
      // 更新选中的船舶
      selectedShip.value = ship
    })
  })

  // 调整地图视野以包含所有标记
  if (markers.length > 0) {
    map.setFitView(markers, false, [50, 50, 50, 50])
  }
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colorMap = {
    '航行中': '#67c23a',
    '靠港中': '#409eff',
    '待机中': '#e6a23c',
    '维修中': '#f56c6c'
  }
  return colorMap[status] || '#909399'
}

// 清除所有标记
const clearMarkers = () => {
  markers.forEach(marker => {
    marker.setMap(null)
  })
  markers = []
}

// 定位到指定船舶
const focusShip = (ship: any) => {
  selectedShip.value = ship
  
  if (map) {
    // 设置地图中心和缩放级别
    map.setCenter([ship.longitude, ship.latitude])
    map.setZoom(12)
    
    // 找到对应的标记并触发点击事件
    const marker = markers.find(m => {
      const pos = m.getPosition()
      return Math.abs(pos.lng - ship.longitude) < 0.001 && 
             Math.abs(pos.lat - ship.latitude) < 0.001
    })
    
    if (marker) {
      const content = `
        <div style="padding: 12px; min-width: 220px;">
          <h4 style="margin: 0 0 10px 0; color: #333;">🚢 ${ship.name}</h4>
          <p style="margin: 5px 0; color: #666; line-height: 1.6;">
            <strong>IMO编号:</strong> ${ship.imo}<br>
            <strong>船舶类型:</strong> ${ship.type}<br>
            <strong>状态:</strong> <span style="color: ${getStatusColor(ship.status)}; font-weight: bold;">${ship.status}</span><br>
            <strong>坐标:</strong> ${ship.longitude.toFixed(4)}, ${ship.latitude.toFixed(4)}<br>
            <strong>目的港:</strong> ${ship.destination}
          </p>
          <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
            <strong>航行信息:</strong><br>
            航速: ${ship.speed} 节 | 航向: ${ship.course}°<br>
            <small style="color: #999;">最后更新: ${ship.lastUpdate}</small>
          </div>
        </div>
      `
      infoWindow.setContent(content)
      infoWindow.open(map, marker.getPosition())
    }
  }
}

// 刷新地图
const refreshMap = () => {
  if (map) {
    addShipMarkers()
    ElMessage.success('地图已刷新')
  }
}

// 显示所有船舶
const showAllShips = () => {
  searchKeyword.value = ''
  nextTick(() => {
    addShipMarkers()
  })
}

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '航行中': 'success',
    '靠港中': 'info',
    '待机中': 'warning',
    '维修中': 'danger'
  }
  return statusMap[status] || 'info'
}

// 动态加载高德地图API
const loadAmapScript = () => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve(window.AMap)
      return
    }

    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${MAP_CONFIG.AMAP.KEY}&plugin=${MAP_CONFIG.AMAP.PLUGINS.join(',')}`
    script.onload = () => resolve(window.AMap)
    script.onerror = reject
    document.head.appendChild(script)
  })
}

// 组件挂载
onMounted(async () => {
  try {
    await loadAmapScript()
    await nextTick()
    initMap()
  } catch (error) {
    console.error('加载船舶追踪地图API失败:', error)
    ElMessage.error('地图API加载失败')
  }
})
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
  position: relative;
}

.amap-container {
  width: 100%;
  height: 100%;
  border-radius: 4px;
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

/* 高德地图控件样式优化 */
:deep(.amap-logo) {
  opacity: 0.5;
}

:deep(.amap-copyright) {
  opacity: 0.5;
}
</style> 