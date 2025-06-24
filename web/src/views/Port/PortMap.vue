<template>
  <div class="port-map-container">
    <!-- 顶部工具栏 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索港口名称、代码或国家"
          :prefix-icon="Search"
          clearable
          @input="filterPorts"
        />
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="fetchPortData" :loading="loading">
          刷新数据
        </el-button>
      </el-col>
      <el-col :span="6">
        <div class="port-count">
          共 {{ filteredPorts.length }} 个港口
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- 左侧港口列表 -->
      <el-col :span="8">
        <el-card class="port-list-card">
          <template #header>
            <div class="card-header">
              <span>港口列表</span>
              <el-tag type="info">{{ filteredPorts.length }}</el-tag>
            </div>
          </template>
          <el-table :data="filteredPorts" style="width: 100%" size="small" height="calc(100vh - 200px)" v-loading="loading">
            <el-table-column prop="nameCn" label="港口名称" />
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

      <!-- 右侧地图和详情 -->
      <el-col :span="16">
        <el-row :gutter="20">
          <!-- 地图 -->
          <el-col :span="24">
            <el-card class="map-card">
              <template #header>
                <div class="card-header">
                  <span>港口地图</span>
                  <el-tag type="success">高德地图</el-tag>
                </div>
              </template>
              <div id="port-map" style="height: 400px; width: 100%;"></div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 港口详情 -->
        <el-row :gutter="20" style="margin-top: 20px;" v-if="selectedPort">
          <el-col :span="24">
            <el-card class="port-detail-card">
              <template #header>
                <div class="card-header">
                  <span>港口详情 - {{ selectedPort.nameCn || selectedPort.nameEn }}</span>
                  <el-tag>正常运营</el-tag>
                </div>
              </template>
              <el-descriptions :column="3" border>
                <el-descriptions-item label="港口代码">{{ selectedPort.code }}</el-descriptions-item>
                <el-descriptions-item label="所在国家">{{ selectedPort.country }}</el-descriptions-item>
                <el-descriptions-item label="经度">{{ selectedPort.longitude }}</el-descriptions-item>
                <el-descriptions-item label="纬度">{{ selectedPort.latitude }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAllPorts } from '@/api/port'

// 响应式数据
const searchKeyword = ref('')
const selectedPort = ref(null)
const loading = ref(false)
const ports = ref([])
const filteredPorts = ref([])
let map = null
let markers = []

// 地图配置
const AMAP_KEY = 'f2163136e1dae2878a9962e856a5f125'
const MAP_CENTER = [121.4648, 31.2304]

// 过滤港口
const filterPorts = () => {
  if (!searchKeyword.value) {
    filteredPorts.value = ports.value
    return
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  filteredPorts.value = ports.value.filter(port => 
    port.nameEn?.toLowerCase().includes(keyword) ||
    port.nameCn?.toLowerCase().includes(keyword) ||
    port.country?.toLowerCase().includes(keyword) ||
    port.code?.toLowerCase().includes(keyword)
  )
  
  // 重新添加标记
  if (map) {
    addPortMarkers()
  }
}

// 获取港口数据
const fetchPortData = async () => {
  try {
    loading.value = true
    console.log('开始获取港口数据...')
    
    const result = await getAllPorts()
    console.log('API响应:', result)
    
    if (result.code === 200) {
      ports.value = result.data || []
      filteredPorts.value = ports.value
      console.log('获取港口数据成功:', ports.value.length, '个港口')
      
      // 地图加载完成后添加港口标记
      if (map) {
        addPortMarkers()
      }
      
      ElMessage.success(`成功加载 ${ports.value.length} 个港口`)
    } else {
      ElMessage.error('获取港口数据失败: ' + result.msg)
    }
  } catch (error) {
    console.error('获取港口数据失败:', error)
    ElMessage.error('获取港口数据失败: 用户未登录或token无效')
  } finally {
    loading.value = false
  }
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
    console.log('开始初始化地图...')
    await loadAmapScript()
    console.log('高德地图API加载完成')
    
    // 创建地图实例
    map = new window.AMap.Map('port-map', {
      zoom: 5,
      center: MAP_CENTER,
      mapStyle: 'amap://styles/normal'
    })

    // 地图加载完成后添加港口标记
    map.on('complete', () => {
      console.log('港口地图加载完成')
      addPortMarkers()
    })

    console.log('地图初始化成功')
    ElMessage.success('地图加载成功')
  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接')
  }
}

// 添加港口标记
const addPortMarkers = () => {
  if (!map) {
    console.log('地图未初始化，跳过添加标记')
    return
  }
  
  // 清除现有标记
  clearMarkers()

  const portsToShow = filteredPorts.value
  console.log('添加港口标记:', portsToShow.length, '个港口')

  portsToShow.forEach(port => {
    // 检查港口是否有有效的坐标
    if (!port.longitude || !port.latitude) {
      console.warn('港口坐标无效:', port)
      return
    }

    // 创建标记点
    const marker = new window.AMap.Marker({
      position: [port.longitude, port.latitude],
      title: port.nameCn || port.nameEn,
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
      selectedPort.value = port
      
      // 创建信息窗口
      const infoWindow = new window.AMap.InfoWindow({
        content: `
          <div style="padding: 10px; min-width: 200px;">
            <h4 style="margin: 0 0 10px 0; color: #333;">${port.nameCn || port.nameEn}</h4>
            <p style="margin: 5px 0; color: #666;">
              <strong>港口代码:</strong> ${port.code}<br>
              <strong>所在国家:</strong> ${port.country}<br>
              <strong>状态:</strong> <span style="color: #67c23a;">正常运营</span><br>
              <strong>坐标:</strong> ${port.longitude?.toFixed(4)}, ${port.latitude?.toFixed(4)}
            </p>
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

// 清除所有标记
const clearMarkers = () => {
  markers.forEach(marker => {
    marker.setMap(null)
  })
  markers = []
}

// 定位到指定港口
const focusPort = (port) => {
  selectedPort.value = port
  
  if (map && port.longitude && port.latitude) {
    // 设置地图中心和缩放级别
    map.setCenter([port.longitude, port.latitude])
    map.setZoom(12)
    
    // 显示信息窗口
    const infoWindow = new window.AMap.InfoWindow({
      content: `
        <div style="padding: 10px; min-width: 200px;">
          <h4 style="margin: 0 0 10px 0; color: #333;">${port.nameCn || port.nameEn}</h4>
          <p style="margin: 5px 0; color: #666;">
            <strong>港口代码:</strong> ${port.code}<br>
            <strong>所在国家:</strong> ${port.country}<br>
            <strong>状态:</strong> <span style="color: #67c23a;">正常运营</span><br>
            <strong>坐标:</strong> ${port.longitude?.toFixed(4)}, ${port.latitude?.toFixed(4)}
          </p>
        </div>
      `,
      isCustom: false,
      autoMove: true
    })
    
    infoWindow.open(map, [port.longitude, port.latitude])
  }
}

// 组件挂载后初始化
onMounted(async () => {
  console.log('PortMap组件挂载，开始初始化...')
  await fetchPortData()
  await initMap()
})
</script>

<style scoped>
.port-map-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.port-count {
  text-align: right;
  color: #666;
  font-size: 14px;
}

.port-list-card {
  height: calc(100vh - 140px);
}

.map-card {
  margin-bottom: 20px;
}

.port-detail-card {
  margin-bottom: 20px;
}

#port-map {
  border-radius: 4px;
}
</style> 