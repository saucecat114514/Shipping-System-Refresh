<template>
  <div class="system-config">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统配置管理</span>
          <el-button type="primary" @click="handleSaveAll">
            保存所有配置
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="config-tabs">
        <!-- 基础配置 -->
        <el-tab-pane label="基础配置" name="basic">
          <el-form
            ref="basicFormRef"
            :model="basicConfig"
            :rules="basicRules"
            label-width="200px"
            class="config-form"
          >
            <el-form-item label="系统名称" prop="systemName">
              <el-input v-model="basicConfig.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            
            <el-form-item label="系统版本" prop="systemVersion">
              <el-input v-model="basicConfig.systemVersion" placeholder="请输入系统版本" />
            </el-form-item>
            
            <el-form-item label="系统描述" prop="systemDescription">
              <el-input 
                v-model="basicConfig.systemDescription" 
                type="textarea" 
                :rows="3"
                placeholder="请输入系统描述"
              />
            </el-form-item>
            
            <el-form-item label="默认分页大小" prop="defaultPageSize">
              <el-input-number
                v-model="basicConfig.defaultPageSize"
                :min="5"
                :max="100"
                :step="5"
                style="width: 200px"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 运价配置 -->
        <el-tab-pane label="运价配置" name="pricing">
          <el-form
            ref="pricingFormRef"
            :model="pricingConfig"
            :rules="pricingRules"
            label-width="200px"
            class="config-form"
          >
            <el-form-item label="基础运价费率(元/吨/公里)" prop="baseRate">
              <el-input-number
                v-model="pricingConfig.baseRate"
                :precision="4"
                :step="0.0001"
                :min="0"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="燃油附加费率(%)" prop="fuelSurchargeRate">
              <el-input-number
                v-model="pricingConfig.fuelSurchargeRate"
                :precision="2"
                :step="0.01"
                :min="0"
                :max="100"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="加急费率(%)" prop="urgentSurchargeRate">
              <el-input-number
                v-model="pricingConfig.urgentSurchargeRate"
                :precision="2"
                :step="0.01"
                :min="0"
                :max="100"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="危险品附加费率(%)" prop="dangerousGoodsSurchargeRate">
              <el-input-number
                v-model="pricingConfig.dangerousGoodsSurchargeRate"
                :precision="2"
                :step="0.01"
                :min="0"
                :max="100"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="最低运费(元)" prop="minimumFreight">
              <el-input-number
                v-model="pricingConfig.minimumFreight"
                :precision="2"
                :step="100"
                :min="0"
                style="width: 200px"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 地图配置 -->
        <el-tab-pane label="地图配置" name="map">
          <el-form
            ref="mapFormRef"
            :model="mapConfig"
            :rules="mapRules"
            label-width="200px"
            class="config-form"
          >
            <el-form-item label="地图API类型" prop="mapType">
              <el-select v-model="mapConfig.mapType" placeholder="请选择地图API类型" style="width: 200px">
                <el-option label="高德地图" value="amap" />
                <el-option label="百度地图" value="baidu" />
                <el-option label="腾讯地图" value="tencent" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="地图API密钥" prop="mapApiKey">
              <el-input 
                v-model="mapConfig.mapApiKey" 
                placeholder="请输入地图API密钥"
                show-password
                style="width: 400px"
              />
            </el-form-item>
            
            <el-form-item label="默认地图中心经度" prop="defaultLongitude">
              <el-input-number
                v-model="mapConfig.defaultLongitude"
                :precision="6"
                :step="0.000001"
                :min="-180"
                :max="180"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="默认地图中心纬度" prop="defaultLatitude">
              <el-input-number
                v-model="mapConfig.defaultLatitude"
                :precision="6"
                :step="0.000001"
                :min="-90"
                :max="90"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="默认地图缩放级别" prop="defaultZoom">
              <el-input-number
                v-model="mapConfig.defaultZoom"
                :min="1"
                :max="20"
                style="width: 200px"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- AIS配置 -->
        <el-tab-pane label="AIS配置" name="ais">
          <el-form
            ref="aisFormRef"
            :model="aisConfig"
            :rules="aisRules"
            label-width="200px"
            class="config-form"
          >
            <el-form-item label="AIS数据源URL" prop="aisDataSourceUrl">
              <el-input 
                v-model="aisConfig.aisDataSourceUrl" 
                placeholder="请输入AIS数据源URL"
                style="width: 400px"
              />
            </el-form-item>
            
            <el-form-item label="AIS更新间隔(秒)" prop="aisUpdateInterval">
              <el-input-number
                v-model="aisConfig.aisUpdateInterval"
                :min="5"
                :max="3600"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="历史轨迹保留天数" prop="trackRetentionDays">
              <el-input-number
                v-model="aisConfig.trackRetentionDays"
                :min="1"
                :max="365"
                style="width: 200px"
              />
            </el-form-item>
            
            <el-form-item label="启用AIS数据" prop="enableAis">
              <el-switch v-model="aisConfig.enableAis" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemConfig, updateSystemConfig } from '@/api/config'

const activeTab = ref('basic')

// 表单引用
const basicFormRef = ref()
const pricingFormRef = ref()
const mapFormRef = ref()
const aisFormRef = ref()

// 基础配置
const basicConfig = reactive({
  systemName: '航运管理系统',
  systemVersion: '1.0.0',
  systemDescription: '基于Spring Boot + Vue的航运管理系统',
  defaultPageSize: 10
})

// 运价配置
const pricingConfig = reactive({
  baseRate: 0.5,
  fuelSurchargeRate: 10.0,
  urgentSurchargeRate: 20.0,
  dangerousGoodsSurchargeRate: 30.0,
  minimumFreight: 100.0
})

// 地图配置
const mapConfig = reactive({
  mapType: 'amap',
  mapApiKey: '',
  defaultLongitude: 121.4648,
  defaultLatitude: 31.2304,
  defaultZoom: 10
})

// AIS配置
const aisConfig = reactive({
  aisDataSourceUrl: '',
  aisUpdateInterval: 60,
  trackRetentionDays: 30,
  enableAis: false
})

// 验证规则
const basicRules = {
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' }
  ],
  systemVersion: [
    { required: true, message: '请输入系统版本', trigger: 'blur' }
  ],
  defaultPageSize: [
    { required: true, message: '请设置默认分页大小', trigger: 'blur' }
  ]
}

const pricingRules = {
  baseRate: [
    { required: true, message: '请设置基础运价费率', trigger: 'blur' }
  ],
  fuelSurchargeRate: [
    { required: true, message: '请设置燃油附加费率', trigger: 'blur' }
  ],
  minimumFreight: [
    { required: true, message: '请设置最低运费', trigger: 'blur' }
  ]
}

const mapRules = {
  mapType: [
    { required: true, message: '请选择地图API类型', trigger: 'change' }
  ],
  mapApiKey: [
    { required: true, message: '请输入地图API密钥', trigger: 'blur' }
  ],
  defaultLongitude: [
    { required: true, message: '请设置默认经度', trigger: 'blur' }
  ],
  defaultLatitude: [
    { required: true, message: '请设置默认纬度', trigger: 'blur' }
  ]
}

const aisRules = {
  aisUpdateInterval: [
    { required: true, message: '请设置AIS更新间隔', trigger: 'blur' }
  ],
  trackRetentionDays: [
    { required: true, message: '请设置轨迹保留天数', trigger: 'blur' }
  ]
}

// 加载配置数据
const loadConfigs = async () => {
  try {
    const response = await getSystemConfig()
    if (response.data) {
      // 更新各个配置对象
      Object.assign(basicConfig, response.data.basic || {})
      Object.assign(pricingConfig, response.data.pricing || {})
      Object.assign(mapConfig, response.data.map || {})
      Object.assign(aisConfig, response.data.ais || {})
    }
  } catch (error) {
    console.error('加载配置失败:', error)
    ElMessage.warning('加载配置失败，使用默认配置')
  }
}

// 保存所有配置
const handleSaveAll = async () => {
  try {
    // 验证所有表单
    await Promise.all([
      basicFormRef.value?.validate(),
      pricingFormRef.value?.validate(),
      mapFormRef.value?.validate(),
      aisFormRef.value?.validate()
    ])

    const configData = {
      basic: { ...basicConfig },
      pricing: { ...pricingConfig },
      map: { ...mapConfig },
      ais: { ...aisConfig }
    }

    await updateSystemConfig(configData)
    ElMessage.success('保存配置成功')
  } catch (error) {
    console.error('保存配置失败:', error)
    if (error.message) {
      ElMessage.error('表单验证失败，请检查输入')
    } else {
      ElMessage.error('保存配置失败')
    }
  }
}

// 组件挂载时加载配置
onMounted(() => {
  loadConfigs()
})
</script>

<style scoped>
.system-config {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.config-tabs {
  margin-top: 20px;
}

.config-form {
  max-width: 800px;
  padding: 20px;
}

:deep(.el-tabs__content) {
  padding: 0;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}
</style> 