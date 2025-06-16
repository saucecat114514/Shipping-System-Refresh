<!-- 航线管理页面 -->
<template>
  <div>
    <el-table :data="routes" style="width: 100%">
      <el-table-column prop="name" label="航线名称"></el-table-column>
      <el-table-column prop="originPort.nameCn" label="起始港"></el-table-column>
      <el-table-column prop="destinationPort.nameCn" label="目的港"></el-table-column>
      <el-table-column prop="distance" label="距离 (公里)"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="editRoute(scope.row)">编辑</el-button>
          <el-button @click="deleteRoute(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="编辑航线">
      <el-form :model="routeForm">
        <el-form-item label="航线名称">
          <el-input v-model="routeForm.name"></el-input>
        </el-form-item>
        <el-form-item label="经停点">
          <div v-for="(stop, index) in routeForm.intermediatePorts" :key="index">
            <el-input v-model="stop.name" placeholder="港口名称"></el-input>
            <el-button @click="removeStop(index)">删除</el-button>
          </div>
          <el-button @click="addStop">添加经停点</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateRoute">保存</el-button>
      </template>
    </el-dialog>

    <div id="map" style="height: 500px; width: 100%;"></div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      routes: [],
      dialogVisible: false,
      routeForm: {
        id: null,
        name: '',
        intermediatePorts: []
      }
    }
  },
  mounted() {
    this.fetchRoutes()
    this.initMap()
  },
  methods: {
    fetchRoutes() {
      get('/routes').then(response => {
        this.routes = response.data.list
      })
    },
    editRoute(route) {
      this.routeForm = { ...route }
      this.dialogVisible = true
    },
    updateRoute() {
      put(`/routes/${this.routeForm.id}`, this.routeForm).then(() => {
        this.dialogVisible = false
        this.fetchRoutes()
      })
    },
    deleteRoute(id) {
      this.$confirm('此操作将永久删除该航线, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        del(`/routes/${id}`).then(() => {
          this.fetchRoutes()
        })
      })
    },
    initMap() {
      const map = loadMap('map')
      // 绘制航线
      if (this.routes.length > 0) {
        const route = this.routes[0]
        const startPort = route.originPort
        const endPort = route.destinationPort
        const path = [
          [startPort.longitude, startPort.latitude],
          [endPort.longitude, endPort.latitude]
        ]
        new AMap.Polyline({
          path: path,
          strokeColor: '#3366FF',
          strokeWeight: 3
        }).setMap(map)
        map.setFitView()
      }
    },
    addStop() {
      this.routeForm.intermediatePorts.push({ name: '' })
    },
    removeStop(index) {
      this.routeForm.intermediatePorts.splice(index, 1)
    }
  }
}
</script>

