<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入统计标签搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增统计</button>
      </div>

      <div class="data-table-container">
        <div class="data-table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th v-for="col in columns" :key="col.key">{{ col.title }}</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in filteredStats" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.statLabel }}</td>
                <td><span class="stat-value">{{ item.statValue }}</span></td>
                <td>
                  <img v-if="item.statIcon" :src="item.statIcon.replace(/[`\s]/g, '')" alt="图标"
                    style="width: 35px; height: 35px; object-fit: contain; cursor: pointer;" @click="triggerFileInput(item)" />
                  <span v-else style="color: #86868b;">未设置</span>
                </td>
                <td>{{ item.sortOrder }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(item)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(item)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(item.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="block">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>

      <FormDialog
        v-model:visible="showDialog"
        title="平台统计"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传统计图标"
        recommendedSize="推荐尺寸：48×48px（SVG/PNG）"
        imageField="statIcon"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <!-- 统计详情 -->
      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>平台统计详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>统计标签:</label>
              <span>{{ selectedItem?.statLabel }}</span>
            </div>
            <div class="detail-item">
              <label>统计数值:</label>
              <span>{{ selectedItem?.statValue }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item" v-if="selectedItem?.statIcon">
              <label>图标:</label>
              <img :src="selectedItem.statIcon.replace(/[`\s]/g, '')" alt="统计图标" style="max-width: 64px; max-height: 64px;" />
            </div>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn cancel-btn" @click="closeDetailsDialog">关闭</button>
          </div>
        </div>
      </div>

      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import websiteIntroductionApi from '@/api/websiteIntroduction';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: 'ID' },
  { key: 'statLabel', title: '统计标签' },
  { key: 'statValue', title: '统计数值' },
  { key: 'statIcon', title: '图标' },
  { key: 'sortOrder', title: '排序' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const stats = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  statLabel: '',
  statValue: '',
  statIcon: '',
  sortOrder: 0,
});

const formFields = [
  [
    { name: 'statLabel', label: '统计标签', type: 'text', required: true, placeholder: '如：注册用户' },
    { name: 'statValue', label: '统计数值', type: 'text', required: true, placeholder: '如：52万+' },
  ],
  [
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
];

const filteredStats = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (stats.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.statLabel && item.statLabel.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchStats();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchStats();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchStats();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = stats.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的统计', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个统计，请逐个删除`, 'info');
};

const fetchStats = async () => {
  try {
    const response = await websiteIntroductionApi.getPlatformStatList();
    if (response.code === 200) {
      const data = response.data || [];
      stats.value = data.map(item => ({ ...item, checked: false }));
      total.value = data.length;
    } else {
      stats.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取平台统计失败:', error);
    stats.value = [];
    total.value = 0;
  }
};

const triggerFileInput = (item) => {
  if (item.statIcon) {
    window.open(item.statIcon, '_blank');
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = { id: null, statLabel: '', statValue: '', statIcon: '', sortOrder: 0 };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    statLabel: item.statLabel,
    statValue: item.statValue,
    statIcon: item.statIcon,
    sortOrder: item.sortOrder,
  };
  showDialog.value = true;
};

const showDetailsDialog = (item) => {
  selectedItem.value = item;
  showDetails.value = true;
};

const closeDetailsDialog = () => {
  showDetails.value = false;
  selectedItem.value = null;
};

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => { showToast.value = false; }, 3000);
};

const validateForm = (data) => {
  if (!data.statLabel || !data.statValue) {
    return '请填写统计标签和数值';
  }
  return null;
};

const handleSubmit = async (data) => {
  try {
    await websiteIntroductionApi.savePlatformStat(data);
    showToastMessage(isEditing.value ? '更新统计成功' : '新增统计成功');
    showDialog.value = false;
    await fetchStats();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

const isDeletePromptVisible = ref(false);
const deleteId = ref(null);

const handleDelete = (id) => {
  deleteId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteId.value = null;
};

const confirmDelete = async () => {
  if (deleteId.value) {
    try {
      await websiteIntroductionApi.deletePlatformStat(deleteId.value);
      showToastMessage('删除统计成功');
      await fetchStats();
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除统计失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchStats);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.stat-value {
  font-weight: 600;
  color: #0071e3;
  font-size: 15px;
}
</style>
