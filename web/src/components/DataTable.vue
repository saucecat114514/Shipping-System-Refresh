<template>
  <div class="data-table">
    <!-- 搜索区域 -->
    <div class="search-bar" v-if="searchConfig">
      <el-form :model="searchForm" inline>
        <el-form-item 
          v-for="item in searchConfig" 
          :key="item.prop" 
          :label="item.label"
        >
          <el-input
            v-if="item.type === 'input'"
            v-model="searchForm[item.prop]"
            :placeholder="item.placeholder"
            clearable
          />
          <el-select
            v-else-if="item.type === 'select'"
            v-model="searchForm[item.prop]"
            :placeholder="item.placeholder"
            clearable
          >
            <el-option
              v-for="option in item.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd" v-if="showAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <el-table 
      :data="tableData" 
      v-loading="loading"
      stripe
      style="width: 100%"
    >
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth"
        :fixed="column.fixed"
      >
        <template #default="scope" v-if="column.slot">
          <slot :name="column.slot" :row="scope.row" :index="scope.$index"></slot>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="200" fixed="right" v-if="showActions">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <div class="pagination" v-if="showPagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const props = defineProps({
  // 表格列配置
  columns: {
    type: Array,
    required: true
  },
  // 搜索配置
  searchConfig: {
    type: Array,
    default: () => []
  },
  // 数据加载函数
  loadData: {
    type: Function,
    required: true
  },
  // 删除函数
  deleteData: {
    type: Function,
    default: null
  },
  // 是否显示操作列
  showActions: {
    type: Boolean,
    default: true
  },
  // 是否显示新增按钮
  showAdd: {
    type: Boolean,
    default: true
  },
  // 是否显示分页
  showPagination: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['add', 'edit', 'delete', 'search'])

// 数据
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({})

// 初始化搜索表单
const initSearchForm = () => {
  props.searchConfig.forEach(item => {
    searchForm[item.prop] = ''
  })
}

// 加载数据
const loadTableData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const result = await props.loadData(params)
    tableData.value = result.list || result
    total.value = result.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadTableData()
  emit('search', searchForm)
}

// 重置
const handleReset = () => {
  initSearchForm()
  currentPage.value = 1
  loadTableData()
}

// 新增
const handleAdd = () => {
  emit('add')
}

// 编辑
const handleEdit = (row) => {
  emit('edit', row)
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '确认删除', {
      type: 'warning'
    })
    
    if (props.deleteData) {
      await props.deleteData(row.id)
      ElMessage.success('删除成功')
      loadTableData()
    }
    emit('delete', row)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadTableData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadTableData()
}

// 公开方法
const refresh = () => {
  loadTableData()
}

defineExpose({
  refresh,
  loadTableData
})

onMounted(() => {
  initSearchForm()
  loadTableData()
})
</script>

<style scoped>
.data-table {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 