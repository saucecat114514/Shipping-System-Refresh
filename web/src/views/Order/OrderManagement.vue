<!-- 订单管理页面 -->
<template>
  <div>
    <el-button type="primary" @click="addOrderDialogVisible = true">添加订单</el-button>

    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="orderNumber" label="订单编号"></el-table-column>
      <el-table-column prop="customer.realName" label="客户名称"></el-table-column>
      <el-table-column prop="voyage.voyageNumber" label="航次编号"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="updateOrderStatus(scope.row.id)">更新状态</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="addOrderDialogVisible" title="添加订单">
      <el-form :model="orderForm">
        <el-form-item label="选择客户">
          <el-select v-model="orderForm.customerId" placeholder="请选择客户">
            <el-option
              v-for="customer in customers"
              :key="customer.id"
              :label="customer.realName"
              :value="customer.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择航次">
          <el-select v-model="orderForm.voyageId" placeholder="请选择航次">
            <el-option
              v-for="voyage in voyages"
              :key="voyage.id"
              :label="voyage.voyageNumber"
              :value="voyage.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="货物名称">
          <el-input v-model="orderForm.cargoName"></el-input>
        </el-form-item>
        <el-form-item label="货物类型">
          <el-select v-model="orderForm.cargoType">
            <el-option label="普通货物" value="普通货物"></el-option>
            <el-option label="危险品" value="危险品"></el-option>
            <el-option label="冷藏货物" value="冷藏货物"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="货物重量">
          <el-input-number v-model="orderForm.cargoWeight" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="是否加急">
          <el-switch v-model="orderForm.isUrgent"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOrderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="calculatePriceAndAddOrder">计算运价并保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="priceDialogVisible" title="运价信息">
      <div>基础运费: {{ priceDetails.basePrice }}</div>
      <div>附加费用: {{ priceDetails.additionalFees }}</div>
      <div>总运费: {{ priceDetails.totalPrice }}</div>
      <template #footer>
        <el-button @click="priceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmOrder">确认订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>

export default {
  data() {
    return {
      orders: [],
      addOrderDialogVisible: false,
      orderForm: {
        customerId: null,
        voyageId: null,
        cargoName: '',
        cargoType: '普通货物',
        cargoWeight: 0,
        isUrgent: false
      },
      customers: [],
      voyages: [],
      priceDialogVisible: false,
      priceDetails: {
        basePrice: 0,
        additionalFees: 0,
        totalPrice: 0
      }
    }
  },
  mounted() {
    this.fetchOrders()
    this.fetchCustomers()
    this.fetchVoyages()
  },
  methods: {
    fetchOrders() {
      get('/orders').then(response => {
        this.orders = response.data.list
      })
    },
    fetchCustomers() {
      // 假设有一个获取客户列表的接口
      get('/customers').then(response => {
        this.customers = response.data.list
      })
    },
    fetchVoyages() {
      get('/voyages').then(response => {
        this.voyages = response.data.list
      })
    },
    calculatePriceAndAddOrder() {
      post('/orders/calculate-price', {
        voyageId: this.orderForm.voyageId,
        cargoType: this.orderForm.cargoType,
        cargoWeight: this.orderForm.cargoWeight,
        isUrgent: this.orderForm.isUrgent
      }).then(response => {
        this.priceDetails = response.data
        this.priceDialogVisible = true
      })
    },
    confirmOrder() {
      post('/orders', this.orderForm).then(() => {
        this.addOrderDialogVisible = false
        this.priceDialogVisible = false
        this.fetchOrders()
      })
    },
    updateOrderStatus(id) {
      this.$prompt('请输入新的订单状态', '更新状态', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: 'IN_TRANSIT' // 默认值，可以根据实际情况调整
      }).then(({ value }) => {
        put(`/orders/${id}/status`, { status: value }).then(() => {
          this.fetchOrders()
        })
      })
    }
  }
}
</script>
