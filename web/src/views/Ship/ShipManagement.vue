<template>
  <div class="ship-management">
    <el-header class="page-header">
      <el-page-header @back="goBack" content="船舶管理" />
    </el-header>
    <el-main class="page-main">
      <div class="search-bar">
        <el-input v-model="searchQuery" placeholder="请输入船舶名称或标识码" style="width: 300px;" />
        <el-button type="primary" @click="searchShips">搜索</el-button>
        <el-button type="success" @click="openAddShipDialog">新增船舶</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" class="custom-table">
        <el-table-column prop="name" label="船舶名称" />
        <el-table-column prop="typeCn" label="船舶类型（中文）" />
        <el-table-column prop="typeEn" label="船舶类型（英文）" />
        <el-table-column prop="flag" label="船籍" />
        <el-table-column prop="mmsi" label="MMSI" />
        <el-table-column prop="imoNumber" label="IMO编号" />
        <el-table-column prop="status" label="船舶状态">
          <template #default="scope">
            {{ getStatusText(scope.row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="openEditShipDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteShip(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      />
    </el-main>
  </div>

  <el-dialog v-model="dialogVisible" :title="dialogTitle">
    <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules">
      <el-form-item label="船舶名称" prop="name">
        <el-input v-model="shipForm.name" />
      </el-form-item>
      <el-form-item label="船舶类型（中文）" prop="typeCn">
        <el-input v-model="shipForm.typeCn" />
      </el-form-item>
      <el-form-item label="船舶类型（英文）" prop="typeEn">
        <el-input v-model="shipForm.typeEn" />
      </el-form-item>
      <el-form-item label="船籍" prop="flag">
        <el-input v-model="shipForm.flag" />
      </el-form-item>
      <el-form-item label="MMSI" prop="mmsi">
        <el-input v-model="shipForm.mmsi" />
      </el-form-item>
      <el-form-item label="IMO编号" prop="imoNumber">
        <el-input v-model="shipForm.imoNumber" />
      </el-form-item>
      <el-form-item label="总吨位（GRT）" prop="grossTonnage">
        <el-input-number v-model="shipForm.grossTonnage" :precision="2" />
      </el-form-item>
      <el-form-item label="载重吨位（DWT）" prop="deadweightTonnage">
        <el-input-number v-model="shipForm.deadweightTonnage" :precision="2" />
      </el-form-item>
      <el-form-item label="船舶状态" prop="status">
        <el-select v-model="shipForm.status">
          <el-option label="停泊" :value="0" />
          <el-option label="航行中" :value="1" />
          <el-option label="锚泊" :value="2" />
          <el-option label="维修" :value="3" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveShip">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const tableData = ref([]);
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const isEditing = ref(false);
const shipForm = reactive({
  id: null,
  name: '',
  typeCn: '',
  typeEn: '',
  flag: '',
  mmsi: '',
  imoNumber: '',
  grossTonnage: null,
  deadweightTonnage: null,
  status: 0
});

const shipRules = {
  name: [{ required: true, message: '请输入船舶名称', trigger: 'blur' }],
  typeCn: [{ required: true, message: '请输入船舶类型（中文）', trigger: 'blur' }],
  typeEn: [{ required: true, message: '请输入船舶类型（英文）', trigger: 'blur' }],
  flag: [{ required: true, message: '请输入船籍', trigger: 'blur' }],
  mmsi: [{ required: true, message: '请输入MMSI', trigger: 'blur' }],
  imoNumber: [{ required: true, message: '请输入IMO编号', trigger: 'blur' }],
  grossTonnage: [{ required: true, message: '请输入总吨位（GRT）', trigger: 'blur' }],
  deadweightTonnage: [{ required: true, message: '请输入载重吨位（DWT）', trigger: 'blur' }],
  status: [{ required: true, message: '请选择船舶状态', trigger: 'blur' }]
};

const shipFormRef = ref();

const fetchShips = async () => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('http://localhost:8080/api/ships', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        search: searchQuery.value
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    const data = response.data;
    if (data.code === 200) {
      tableData.value = data.data.records;
      totalItems.value = data.data.total;
    } else {
      ElMessage.error(data.msg || '获取船舶列表失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络');
  }
};

const searchShips = () => {
  currentPage.value = 1;
  fetchShips();
};

const openAddShipDialog = () => {
  dialogTitle.value = '新增船舶';
  isEditing.value = false;
  shipForm.id = null;
  shipForm.name = '';
  shipForm.typeCn = '';
  shipForm.typeEn = '';
  shipForm.flag = '';
  shipForm.mmsi = '';
  shipForm.imoNumber = '';
  shipForm.grossTonnage = null;
  shipForm.deadweightTonnage = null;
  shipForm.status = 0;
  dialogVisible.value = true;
};

const openEditShipDialog = (row: any) => {
  dialogTitle.value = '编辑船舶';
  isEditing.value = true;
  shipForm.id = row.id;
  shipForm.name = row.name;
  shipForm.typeCn = row.typeCn;
  shipForm.typeEn = row.typeEn;
  shipForm.flag = row.flag;
  shipForm.mmsi = row.mmsi;
  shipForm.imoNumber = row.imoNumber;
  shipForm.grossTonnage = row.grossTonnage;
  shipForm.deadweightTonnage = row.deadweightTonnage;
  shipForm.status = row.status;
  dialogVisible.value = true;
};

const deleteShip = async (id: number) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.delete(`http://localhost:8080/api/ships/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    const data = response.data;
    if (data.code === 200) {
      ElMessage.success('删除船舶成功');
      fetchShips();
    } else {
      ElMessage.error(data.msg || '删除船舶失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络');
  }
};

const getStatusText = (status: number) => {
  switch (status) {
    case 0: return '停泊';
    case 1: return '航行中';
    case 2: return '锚泊';
    case 3: return '维修';
    default: return '未知';
  }
};

const saveShip = () => {
  shipFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const token = localStorage.getItem('token');
        if (isEditing.value) {
          const response = await axios.put(
            `http://localhost:8080/api/ships/${shipForm.id}`,
            shipForm,
            {
              headers: {
                Authorization: `Bearer ${token}`
              }
            }
          );
          const data = response.data;
          if (data.code === 200) {
            ElMessage.success('更新船舶成功');
            dialogVisible.value = false;
            fetchShips();
          } else {
            ElMessage.error(data.msg || '更新船舶失败');
          }
        } else {
          const response = await axios.post(
            'http://localhost:8080/api/ships',
            shipForm,
            {
              headers: {
                Authorization: `Bearer ${token}`
              }
            }
          );
          const data = response.data;
          if (data.code === 200) {
            ElMessage.success('新增船舶成功');
            dialogVisible.value = false;
            fetchShips();
          } else {
            ElMessage.error(data.msg || '新增船舶失败');
          }
        }
      } catch (error) {
        ElMessage.error('请求失败，请检查网络');
      }
    }
  });
};

const goBack = () => {
  // 返回上一页逻辑
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchShips();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchShips();
};

fetchShips();
</script>

<style scoped>
.ship-management {
  padding: 20px;
  background-color: #f5f7fa;
}

.page-header {
  margin-bottom: 20px;
}

.page-main {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.search-bar .el-input {
  margin-right: 10px;
}

:deep(.el-table) {
  box-shadow: none;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #333333;
  font-weight: 500;
}

:deep(.el-table tr:hover) {
  background-color: #f0f7ff;
}

:deep(.el-pagination) {
  margin-top: 20px;
  text-align: center;
}

:deep(.el-dialog) {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 16px;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #eaeaea;
  padding-top: 16px;
}

:deep(.el-button) {
  background-color: #0071e3;
  color: #ffffff;
  border: none;
  border-radius: 6px;
}

:deep(.el-button:hover) {
  background-color: #0050b3;
}

:deep(.el-button.el-button--danger) {
  background-color: #ff4d4f;
}

:deep(.el-button.el-button--danger:hover) {
  background-color: #ff1a1e;
}
</style>
