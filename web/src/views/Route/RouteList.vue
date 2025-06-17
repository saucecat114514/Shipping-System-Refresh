<template>
  <div class="route-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">航线管理</span>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索航线"
              prefix-icon="Search"
              style="width: 200px; margin-left: 16px"
              size="small"
            />
          </div>
          <el-button type="primary" @click="handleAddRoute">
            <el-icon><Plus /></el-icon>新增航线
          </el-button>
        </div>
      </template>

      <el-table :data="routes" style="width: 100%" border>
        <el-table-column prop="code" label="航线编号" width="120" />
        <el-table-column prop="name" label="航线名称" width="200" />
        <el-table-column label="起始港口">
          <template #default="scope">
            {{ scope.row.startPort }} → {{ scope.row.endPort }}
          </template>
        </el-table-column>
        <el-table-column prop="distance" label="航程(海里)" width="120" align="right" />
        <el-table-column prop="duration" label="预计航行时间" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑航线对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增航线' : '编辑航线'"
      width="600px"
    >
      <el-form ref="formRef" :model="routeForm" :rules="rules" label-width="100px">
        <el-form-item label="航线编号" prop="code">
          <el-input v-model="routeForm.code" placeholder="请输入航线编号" />
        </el-form-item>
        <el-form-item label="航线名称" prop="name">
          <el-input v-model="routeForm.name" placeholder="请输入航线名称" />
        </el-form-item>
        <el-form-item label="起始港口" prop="startPort">
          <el-select v-model="routeForm.startPort" placeholder="请选择起始港口" style="width: 100%">
            <el-option v-for="port in ports" :key="port.code" :label="port.name" :value="port.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="目的港口" prop="endPort">
          <el-select v-model="routeForm.endPort" placeholder="请选择目的港口" style="width: 100%">
            <el-option v-for="port in ports" :key="port.code" :label="port.name" :value="port.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="航程" prop="distance">
          <el-input-number v-model="routeForm.distance" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计时间" prop="duration">
          <el-input v-model="routeForm.duration" placeholder="请输入预计航行时间" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="routeForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" value="正常" />
            <el-option label="维护中" value="维护中" />
            <el-option label="已停用" value="已停用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 搜索关键词
const searchKeyword = ref('')

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()

// 表单数据
const routeForm = reactive({
  code: '',
  name: '',
  startPort: '',
  endPort: '',
  distance: 0,
  duration: '',
  status: '正常'
})

// 表单验证规则
const rules: FormRules = {
  code: [{ required: true, message: '请输入航线编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入航线名称', trigger: 'blur' }],
  startPort: [{ required: true, message: '请选择起始港口', trigger: 'change' }],
  endPort: [{ required: true, message: '请选择目的港口', trigger: 'change' }],
  distance: [{ required: true, message: '请输入航程', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入预计航行时间', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 模拟数据
const routes = [
  {
    code: 'R001',
    name: '上海-新加坡快线',
    startPort: '上海港',
    endPort: '新加坡港',
    distance: 2580,
    duration: '5天',
    status: '正常'
  },
  {
    code: 'R002',
    name: '青岛-釜山航线',
    startPort: '青岛港',
    endPort: '釜山港',
    distance: 390,
    duration: '1天',
    status: '正常'
  }
]

const ports = [
  { code: 'SHA', name: '上海港' },
  { code: 'SIN', name: '新加坡港' },
  { code: 'QIN', name: '青岛港' },
  { code: 'PUS', name: '釜山港' }
]

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    '正常': 'success',
    '维护中': 'warning',
    '已停用': 'danger'
  }
  return statusMap[status] || 'info'
}

// 处理分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // TODO: 重新加载数据
}

// 处理新增航线
const handleAddRoute = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 处理编辑航线
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  // 填充表单数据
  Object.assign(routeForm, row)
}

// 处理查看航线
const handleView = (row: any) => {
  // TODO: 实现查看详情功能
  console.log('查看航线:', row)
}

// 处理删除航线
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除航线"${row.name}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用删除API
    ElMessage.success('删除成功')
  }).catch(() => {
    // 取消删除
  })
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用保存API
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
    }
  })
}
</script>

<style scoped>
.route-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-dialog__body) {
  padding-top: 10px;
}
</style> 