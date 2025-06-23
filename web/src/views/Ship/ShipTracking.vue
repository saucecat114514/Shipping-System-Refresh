<template>
  <div class="ship-tracking-container">
    <!-- 顶部工具栏 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索船舶名称、IMO或类型"
          :prefix-icon="Search"
          clearable
          @input="filterShips"
        />
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="fetchShipData" :loading="loading">
          刷新数据
        </el-button>
      </el-col>
      <el-col :span="6">
        <div class="ship-count">
          共 {{ filteredShips.length }} 艘船舶
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- 左侧船舶列表 -->
      <el-col :span="8">
        <el-card class="ship-list-card">
          <template #header>
            <div class="card-header">
              <span>船舶列表</span>
              <el-tag type="info">{{ filteredShips.length }}</el-tag>
            </div>
          </template>
          <el-table :data="filteredShips" style="width: 100%" size="small" height="calc(100vh - 400px)" v-loading="loading">
            <el-table-column prop="name" label="船名" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status || 0)">
                  {{ getStatusText(scope.row.status || 0) }}
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

      <!-- 右侧地图和详情 -->
      <el-col :span="16">
        <el-row :gutter="20">
          <!-- 地图 -->
          <el-col :span="24">
            <el-card class="map-card">
              <template #header>
                <div class="card-header">
                  <span>船舶跟踪地图</span>
                  <el-tag type="success">实时追踪</el-tag>
                </div>
              </template>
              <div id="ship-map" style="height: 400px; width: 100%;"></div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 船舶详情 -->
        <el-row :gutter="20" style="margin-top: 20px;" v-if="selectedShip">
          <el-col :span="24">
            <el-card class="ship-detail-card">
              <template #header>
                <div class="card-header">
                  <span>船舶详情 - {{ selectedShip.name }}</span>
                  <el-tag>{{ getStatusText(selectedShip.status || 0) }}</el-tag>
                </div>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="IMO编号">{{ selectedShip.imoNumber || '未知' }}</el-descriptions-item>
                <el-descriptions-item label="船舶类型">{{ selectedShip.typeEn }}</el-descriptions-item>
                <el-descriptions-item label="当前位置">
                  {{ selectedShip.currentLongitude?.toFixed(4) || selectedShip.longitude?.toFixed(4) || 'N/A' }}, 
                  {{ selectedShip.currentLatitude?.toFixed(4) || selectedShip.latitude?.toFixed(4) || 'N/A' }}
                </el-descriptions-item>
                <el-descriptions-item label="船旗国">{{ selectedShip.flag }}</el-descriptions-item>
                <el-descriptions-item label="载重吨位">{{ selectedShip.deadweightTonnage }}吨</el-descriptions-item>
                <el-descriptions-item label="总吨位">{{ selectedShip.grossTonnage }}吨</el-descriptions-item>
                <el-descriptions-item label="MMSI">{{ selectedShip.mmsi }}</el-descriptions-item>
                <el-descriptions-item label="最后更新时间">{{ formatDate(selectedShip.updatedAt) }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { MAP_CONFIG } from '@/utils/constants'
import { getAllShips } from '@/api/ship'

// 响应式数据
const searchKeyword = ref('')
const selectedShip = ref(null)
const loading = ref(false)
const ships = ref([])
const filteredShips = ref([])
let map = null
let markers = []
let infoWindow = null

// 地图配置
const AMAP_KEY = 'f2163136e1dae2878a9962e856a5f125'
const MAP_CENTER = [121.4648, 31.2304]

// 过滤船舶
const filterShips = () => {
  if (!searchKeyword.value) {
    filteredShips.value = ships.value
    return
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  filteredShips.value = ships.value.filter(ship => 
    ship.name?.toLowerCase().includes(keyword) ||
    ship.imoNumber?.toLowerCase().includes(keyword) ||
    ship.typeEn?.toLowerCase().includes(keyword) ||
    ship.flag?.toLowerCase().includes(keyword)
  )
  
  // 重新添加标记
  if (map) {
    addShipMarkers()
  }
}

// 获取船舶数据
const fetchShipData = async () => {
  try {
    loading.value = true
    console.log('开始获取船舶数据...')
    
    const response = await fetch('/api/ships/all')
    const result = await response.json()
    console.log('船舶API响应:', result)
    
    if (result.code === 200) {
      ships.value = result.data || []
      filteredShips.value = ships.value
      console.log('获取船舶数据成功:', ships.value.length, '艘船舶')
      
      // 为没有位置信息的船舶添加模拟位置
      ships.value.forEach(ship => {
        if (!ship.currentLongitude || !ship.currentLatitude) {
          // 在中国周边海域生成随机位置
          ship.longitude = 110 + Math.random() * 25  // 110-135度
          ship.latitude = 15 + Math.random() * 25    // 15-40度
        } else {
          ship.longitude = ship.currentLongitude
          ship.latitude = ship.currentLatitude
        }
        
        // 添加模拟的实时数据
        ship.destination = ship.destination || '未知港口'
        ship.eta = '2024-01-20 14:30'
        ship.speed = ship.currentSpeed || Math.random() * 20
        ship.course = ship.currentHeading || Math.random() * 360
        ship.lastUpdate = new Date().toLocaleString()
      })
      
      // 地图加载完成后添加船舶标记
      if (map) {
        addShipMarkers()
      }
      
      ElMessage.success(`成功加载 ${ships.value.length} 艘船舶`)
    } else {
      ElMessage.error('获取船舶数据失败: ' + result.msg)
    }
  } catch (error) {
    console.error('获取船舶数据失败:', error)
    ElMessage.error('获取船舶数据失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleString()
}

// 动态加载高德地图API
const loadAmapScript = () => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve()
      return
    }
    
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}`
    script.onload = resolve
    script.onerror = reject
    document.head.appendChild(script)
  })
}

// 初始化高德地图
const initMap = async () => {
  try {
    console.log('开始初始化船舶跟踪地图...')
    await loadAmapScript()
    console.log('高德地图API加载完成')
    
    // 创建地图实例
    map = new window.AMap.Map('ship-map', {
      zoom: 6,
      center: MAP_CENTER,
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

    console.log('船舶跟踪地图初始化成功')
    ElMessage.success('船舶追踪地图加载成功')
  } catch (error) {
    console.error('船舶追踪地图初始化失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接')
  }
}

// 获取船舶状态对应的图标
const getShipIcon = (status: number) => {
  const iconMap = {
    0: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',  // 停泊
    1: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_g.png',  // 航行中
    2: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_y.png',  // 锚泊
    3: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png'   // 维修
  }
  return iconMap[status] || iconMap[0]
}

// 添加船舶标记
const addShipMarkers = () => {
  if (!map) {
    console.log('地图未初始化，跳过添加船舶标记')
    return
  }
  
  // 清除现有标记
  clearMarkers()

  const shipsToShow = filteredShips.value
  console.log('添加船舶标记:', shipsToShow.length, '艘船舶')

  shipsToShow.forEach(ship => {
    // 检查船舶是否有有效的坐标
    if (!ship.longitude || !ship.latitude) {
      console.warn('船舶坐标无效:', ship)
      return
    }

    // 创建标记点
    const marker = new window.AMap.Marker({
      position: [ship.longitude, ship.latitude],
      title: ship.name,
      icon: new window.AMap.Icon({
        image: getShipIcon(ship.status || 0),
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
      selectedShip.value = ship
      
      // 创建信息窗口
      const infoWindow = new window.AMap.InfoWindow({
        content: `
          <div style="padding: 12px; min-width: 220px;">
            <h4 style="margin: 0 0 10px 0; color: #333;">🚢 ${ship.name}</h4>
            <p style="margin: 5px 0; color: #666; line-height: 1.6;">
              <strong>IMO编号:</strong> ${ship.imoNumber || '未知'}<br>
              <strong>船舶类型:</strong> ${ship.typeEn}<br>
              <strong>状态:</strong> <span style="color: ${getStatusColor(ship.status || 0)}; font-weight: bold;">${getStatusText(ship.status || 0)}</span><br>
              <strong>坐标:</strong> ${ship.longitude?.toFixed(4)}, ${ship.latitude?.toFixed(4)}<br>
              <strong>船旗国:</strong> ${ship.flag}
            </p>
            <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
              <strong>船舶信息:</strong><br>
              载重: ${ship.deadweightTonnage} 吨 | 总吨位: ${ship.grossTonnage} 吨<br>
              <small style="color: #999;">最后更新: ${ship.lastUpdate}</small>
            </div>
          </div>
        `,
        isCustom: false,
        autoMove: true
      })
      
      infoWindow.open(map, marker.getPosition())
    })
  })

  // 调整地图视野以包含所有标记
  if (markers.length > 0) {
    map.setFitView(markers, false, [50, 50, 50, 50])
  }
}

// 获取状态颜色
const getStatusColor = (status: number) => {
  const colorMap = {
    0: '#409eff',  // 停泊 - 蓝色
    1: '#67c23a',  // 航行中 - 绿色
    2: '#e6a23c',  // 锚泊 - 橙色
    3: '#f56c6c'   // 维修 - 红色
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
  
  if (map && ship.longitude && ship.latitude) {
    // 设置地图中心和缩放级别
    map.setCenter([ship.longitude, ship.latitude])
    map.setZoom(12)
    
    // 显示信息窗口
    const infoWindow = new window.AMap.InfoWindow({
      content: `
        <div style="padding: 12px; min-width: 220px;">
          <h4 style="margin: 0 0 10px 0; color: #333;">🚢 ${ship.name}</h4>
          <p style="margin: 5px 0; color: #666; line-height: 1.6;">
            <strong>IMO编号:</strong> ${ship.imoNumber || '未知'}<br>
            <strong>船舶类型:</strong> ${ship.typeEn}<br>
            <strong>状态:</strong> <span style="color: ${getStatusColor(ship.status || 0)}; font-weight: bold;">${getStatusText(ship.status || 0)}</span><br>
            <strong>坐标:</strong> ${ship.longitude?.toFixed(4)}, ${ship.latitude?.toFixed(4)}<br>
            <strong>船旗国:</strong> ${ship.flag}
          </p>
          <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
            <strong>船舶信息:</strong><br>
            载重: ${ship.deadweightTonnage} 吨 | 总吨位: ${ship.grossTonnage} 吨<br>
            <small style="color: #999;">最后更新: ${ship.lastUpdate}</small>
          </div>
        </div>
      `,
      isCustom: false,
      autoMove: true
    })
    
    infoWindow.open(map, [ship.longitude, ship.latitude])
  }
}

// 获取状态类型
const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: 'info',     // 停泊
    1: 'success',  // 航行中
    2: 'warning',  // 锚泊
    3: 'danger'    // 维修
  }
  return statusMap[status] || 'info'
}

// 状态转换函数
const getStatusText = (status: number) => {
  const statusMap = {
    0: '停泊',
    1: '航行中',
    2: '锚泊',
    3: '维修'
  }
  return statusMap[status] || '未知'
}

// 组件挂载后初始化
onMounted(async () => {
  console.log('ShipTracking组件挂载，开始初始化...')
  await fetchShipData()
  await initMap()
})
</script>

<style scoped>
.ship-tracking-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ship-count {
  text-align: right;
  color: #666;
  font-size: 14px;
}

.ship-list-card {
  height: calc(100vh - 140px);
}

.map-card {
  margin-bottom: 20px;
}

.ship-detail-card {
  margin-bottom: 20px;
}

#ship-map {
  border-radius: 4px;
}

/* 高德地图控件样式优化 */
:deep(.amap-logo) {
  opacity: 0.5;
}

:deep(.amap-copyright) {
  opacity: 0.5;
}
</style> 