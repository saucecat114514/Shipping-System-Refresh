<!-- 航次管理页面 -->
<template>
  <div>
    <el-button type="primary" @click="addVoyageDialogVisible = true">添加航次</el-button>

    <el-table :data="voyages" style="width: 100%">
      <el-table-column prop="voyageNumber" label="航次编号"></el-table-column>
      <el-table-column prop="ship.name" label="船舶名称"></el-table-column>
      <el-table-column prop="route.name" label="航线名称"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="approveVoyage(scope.row.id)">审批</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="addVoyageDialogVisible" title="添加航次">
      <el-form :model="voyageForm">
        <el-form-item label="选择船舶">
          <el-select v-model="voyageForm.shipId" placeholder="请选择船舶">
            <el-option
              v-for="ship in ships"
              :key="ship.id"
              :label="ship.name"
              :value="ship.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择航线">
          <el-select v-model="voyageForm.routeId" placeholder="请选择航线">
            <el-option
              v-for="route in routes"
              :key="route.id"
              :label="route.name"
              :value="route.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出发日期">
          <el-date-picker v-model="voyageForm.departureDate" type="datetime" placeholder="选择日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="预计到达日期">
          <el-date-picker v-model="voyageForm.arrivalDate" type="datetime" placeholder="选择日期"></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVoyageDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addVoyage">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>

export default {
  data() {
    return {
      voyages: [],
      addVoyageDialogVisible: false,
      voyageForm: {
        shipId: null,
        routeId: null,
        departureDate: '',
        arrivalDate: ''
      },
      ships: [],
      routes: []
    }
  },
  mounted() {
    this.fetchVoyages()
    this.fetchShips()
    this.fetchRoutes()
  },
  methods: {
    fetchVoyages() {
      get('/voyages').then(response => {
        this.voyages = response.data.list
      })
    },
    fetchShips() {
      get('/ships/all').then(response => {
        this.ships = response.data
      })
    },
    fetchRoutes() {
      get('/routes').then(response => {
        this.routes = response.data.list
      })
    },
    addVoyage() {
      post('/voyages', this.voyageForm).then(() => {
        this.addVoyageDialogVisible = false
        this.fetchVoyages()
      })
    },
    approveVoyage(id) {
      this.$confirm('确认审批该航次?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        put(`/voyages/${id}/status`, { status: 'APPROVED' }).then(() => {
          this.fetchVoyages()
        })
      })
    }
  }
}
</script>
