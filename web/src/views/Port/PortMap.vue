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
          <el-table :data="filteredPorts" style="width: 100%" size="small" height="calc(100vh - 200px)">
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
              <el-radio-group v-model="mapView" size="small" @change="onMapViewChange">
                <el-radio-button label="all">全部港口</el-radio-button>
                <el-radio-button label="major">主要港口</el-radio-button>
                <el-radio-button label="route">航线港口</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="map-container">
            <!-- 高德地图容器 -->
            <div id="port-map" class="amap-container"></div>
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
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { MAP_CONFIG } from '@/utils/constants'

// 响应式数据
const searchKeyword = ref('')
const mapView = ref('all')
const selectedPort = ref(null)
let map = null
let markers = []
let infoWindow = null

// 港口数据（扩展版本）
const ports = [
  {
    id: 1,
    name: '上海港',
    code: 'SHA',
    country: '中国',
    status: '正常运营',
    timezone: 'UTC+8',
    longitude: 121.4737,
    latitude: 31.2304,
    contact: '+86-21-12345678',
    type: 'major',
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
    longitude: 103.8198,
    latitude: 1.2755,
    contact: '+65-12345678',
    type: 'major',
    stats: {
      ships: 62,
      arrivals: 15,
      departures: 13
    }
  },
  {
    id: 3,
    name: '青岛港',
    code: 'QIN',
    country: '中国',
    status: '正常运营',
    timezone: 'UTC+8',
    longitude: 120.3826,
    latitude: 36.0673,
    contact: '+86-532-12345678',
    type: 'major',
    stats: {
      ships: 28,
      arrivals: 8,
      departures: 6
    }
  },
  {
    id: 4,
    name: '宁波港',
    code: 'NGB',
    country: '中国',
    status: '正常运营',
    timezone: 'UTC+8',
    longitude: 121.5562,
    latitude: 29.8770,
    contact: '+86-574-12345678',
    type: 'route',
    stats: {
      ships: 23,
      arrivals: 7,
      departures: 5
    }
  },
  {
    id: 5,
    name: '深圳港',
    code: 'SHE',
    country: '中国',
    status: '正常运营',
    timezone: 'UTC+8',
    longitude: 114.0579,
    latitude: 22.5431,
    contact: '+86-755-12345678',
    type: 'major',
    stats: {
      ships: 31,
      arrivals: 9,
      departures: 7
    }
  },
  {
    id: 6,
    name: '釜山港',
    code: 'PUS',
    country: '韩国',
    status: '正常运营',
    timezone: 'UTC+9',
    longitude: 129.0756,
    latitude: 35.1796,
    contact: '+82-51-12345678',
    type: 'route',
    stats: {
      ships: 19,
      arrivals: 5,
      departures: 4
    }
  }
]

// 过滤后的港口列表
const filteredPorts = computed(() => {
  let filtered = ports
  
  // 根据视图类型过滤
  if (mapView.value !== 'all') {
    filtered = filtered.filter(port => port.type === mapView.value)
  }
  
  // 根据搜索关键词过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(port => 
      port.name.toLowerCase().includes(keyword) ||
      port.country.toLowerCase().includes(keyword) ||
      port.code.toLowerCase().includes(keyword)
    )
  }
  
  return filtered
})

// 初始化高德地图
const initMap = async () => {
  try {
    // 创建地图实例
    map = new window.AMap.Map('port-map', {
      zoom: 5,
      center: MAP_CONFIG.CENTER,
      mapStyle: 'amap://styles/normal'
    })

    // 创建信息窗口
    infoWindow = new window.AMap.InfoWindow({
      isCustom: false,
      autoMove: true
    })

    // 地图加载完成后添加港口标记
    map.on('complete', () => {
      console.log('港口地图加载完成')
      addPortMarkers()
    })

    ElMessage.success('地图加载成功')
  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接')
  }
}

// 添加港口标记
const addPortMarkers = () => {
  // 清除现有标记
  clearMarkers()

  const portsToShow = filteredPorts.value

  portsToShow.forEach(port => {
    // 创建标记点
    const marker = new window.AMap.Marker({
      position: [port.longitude, port.latitude],
      title: port.name,
      icon: new window.AMap.Icon({
        image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
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
        <div style="padding: 10px; min-width: 200px;">
          <h4 style="margin: 0 0 10px 0; color: #333;">${port.name}</h4>
          <p style="margin: 5px 0; color: #666;">
            <strong>港口代码:</strong> ${port.code}<br>
            <strong>所在国家:</strong> ${port.country}<br>
            <strong>状态:</strong> <span style="color: #67c23a;">${port.status}</span><br>
            <strong>坐标:</strong> ${port.longitude.toFixed(4)}, ${port.latitude.toFixed(4)}
          </p>
          <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
            <strong>港口状态:</strong><br>
            在港船舶: ${port.stats.ships} | 今日到港: ${port.stats.arrivals} | 今日离港: ${port.stats.departures}
          </div>
        </div>
      `
      infoWindow.setContent(content)
      infoWindow.open(map, marker.getPosition())
      
      // 更新选中的港口
      selectedPort.value = port
    })
  })

  // 调整地图视野以包含所有标记
  if (markers.length > 0) {
    map.setFitView(markers, false, [50, 50, 50, 50])
  }
}

// 清除所有标记
const clearMarkers = () => {
  markers.forEach(marker => {
    marker.setMap(null)
  })
  markers = []
}

// 定位到指定港口
const focusPort = (port: any) => {
  selectedPort.value = port
  
  if (map) {
    // 设置地图中心和缩放级别
    map.setCenter([port.longitude, port.latitude])
    map.setZoom(12)
    
    // 找到对应的标记并触发点击事件
    const marker = markers.find(m => {
      const pos = m.getPosition()
      return Math.abs(pos.lng - port.longitude) < 0.001 && 
             Math.abs(pos.lat - port.latitude) < 0.001
    })
    
    if (marker) {
      const content = `
        <div style="padding: 10px; min-width: 200px;">
          <h4 style="margin: 0 0 10px 0; color: #333;">${port.name}</h4>
          <p style="margin: 5px 0; color: #666;">
            <strong>港口代码:</strong> ${port.code}<br>
            <strong>所在国家:</strong> ${port.country}<br>
            <strong>状态:</strong> <span style="color: #67c23a;">${port.status}</span><br>
            <strong>坐标:</strong> ${port.longitude.toFixed(4)}, ${port.latitude.toFixed(4)}
          </p>
          <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
            <strong>港口状态:</strong><br>
            在港船舶: ${port.stats.ships} | 今日到港: ${port.stats.arrivals} | 今日离港: ${port.stats.departures}
          </div>
        </div>
      `
      infoWindow.setContent(content)
      infoWindow.open(map, marker.getPosition())
    }
  }
}

// 地图视图改变处理
const onMapViewChange = () => {
  nextTick(() => {
    addPortMarkers()
  })
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
    console.error('加载地图API失败:', error)
    ElMessage.error('地图API加载失败')
  }
})
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
  position: relative;
}

.amap-container {
  width: 100%;
  height: 100%;
  border-radius: 4px;
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

/* 高德地图控件样式优化 */
:deep(.amap-logo) {
  opacity: 0.5;
}

:deep(.amap-copyright) {
  opacity: 0.5;
}
</style> 