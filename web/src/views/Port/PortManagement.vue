<template>
  <div class="port-management">
    <el-header class="page-header">
      <el-page-header @back="goBack" content="港口管理" />
    </el-header>
    <el-main class="page-main">
      <div class="search-bar">
        <el-input v-model="searchQuery" placeholder="请输入港口名称或代码" style="width: 300px;" />
        <el-button type="primary" @click="searchPorts">搜索</el-button>
        <el-button type="success" @click="openAddPortDialog">新增港口</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" class="custom-table">
        <el-table-column prop="nameCn" label="港口名称（中文）" />
        <el-table-column prop="nameEn" label="港口名称（英文）" />
        <el-table-column prop="code" label="港口代码" />
        <el-table-column prop="country" label="所属国家" />
        <el-table-column prop="longitude" label="经度" />
        <el-table-column prop="latitude" label="纬度" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="openEditPortDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePort(scope.row.id)">删除</el-button>
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
    <el-form ref="portFormRef" :model="portForm" :rules="portRules">
      <el-form-item label="港口名称（中文）" prop="nameCn">
        <el-input v-model="portForm.nameCn" />
      </el-form-item>
      <el-form-item label="港口名称（英文）" prop="nameEn">
        <el-input v-model="portForm.nameEn" />
      </el-form-item>
      <el-form-item label="港口代码" prop="code">
        <el-input v-model="portForm.code" />
      </el-form-item>
      <el-form-item label="所属国家" prop="country">
        <el-input v-model="portForm.country" />
      </el-form-item>
      <el-form-item label="经度" prop="longitude">
        <el-input v-model.number="portForm.longitude" type="number" />
      </el-form-item>
      <el-form-item label="纬度" prop="latitude">
        <el-input v-model.number="portForm.latitude" type="number" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePort">保存</el-button>
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
const portForm = reactive({
  id: null,
  nameCn: '',
  nameEn: '',
  code: '',
  country: '',
  longitude: null,
  latitude: null
});

const portRules = {
  nameCn: [{ required: true, message: '请输入港口名称（中文）', trigger: 'blur' }],
  nameEn: [{ required: true, message: '请输入港口名称（英文）', trigger: 'blur' }],
  code: [{ required: true, message: '请输入港口代码', trigger: 'blur' }],
  country: [{ required: true, message: '请输入所属国家', trigger: 'blur' }],
  longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
  latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }]
};

const portFormRef = ref();

const fetchPorts = async () => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('http://localhost:8080/api/ports', {
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
      ElMessage.error(data.msg || '获取港口列表失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络');
  }
};

const searchPorts = () => {
  currentPage.value = 1;
  fetchPorts();
};

const openAddPortDialog = () => {
  dialogTitle.value = '新增港口';
  isEditing.value = false;
  portForm.id = null;
  portForm.nameCn = '';
  portForm.nameEn = '';
  portForm.code = '';
  portForm.country = '';
  portForm.longitude = null;
  portForm.latitude = null;
  dialogVisible.value = true;
};

const openEditPortDialog = (row: any) => {
  dialogTitle.value = '编辑港口';
  isEditing.value = true;
  portForm.id = row.id;
  portForm.nameCn = row.nameCn;
  portForm.nameEn = row.nameEn;
  portForm.code = row.code;
  portForm.country = row.country;
  portForm.longitude = row.longitude;
  portForm.latitude = row.latitude;
  dialogVisible.value = true;
};

const deletePort = async (id: number) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.delete(`http://localhost:8080/api/ports/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    const data = response.data;
    if (data.code === 200) {
      ElMessage.success('删除港口成功');
      fetchPorts();
    } else {
      ElMessage.error(data.msg || '删除港口失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络');
  }
};

const savePort = () => {
  portFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const token = localStorage.getItem('token');
        if (isEditing.value) {
          const response = await axios.put(
            `http://localhost:8080/api/ports/${portForm.id}`,
            portForm,
            {
              headers: {
                Authorization: `Bearer ${token}`
              }
            }
          );
          const data = response.data;
          if (data.code === 200) {
            ElMessage.success('更新港口成功');
            dialogVisible.value = false;
            fetchPorts();
          } else {
            ElMessage.error(data.msg || '更新港口失败');
          }
        } else {
          const response = await axios.post(
            'http://localhost:8080/api/ports',
            portForm,
            {
              headers: {
                Authorization: `Bearer ${token}`
              }
            }
          );
          const data = response.data;
          if (data.code === 200) {
            ElMessage.success('新增港口成功');
            dialogVisible.value = false;
            fetchPorts();
          } else {
            ElMessage.error(data.msg || '新增港口失败');
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
  fetchPorts();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchPorts();
};

fetchPorts();
</script>

<style scoped>
.port-management {
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
