<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入里程碑标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增里程碑</button>
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
              <tr v-for="item in filteredMilestones" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.milestoneDate }}</td>
                <td>{{ item.title }}</td>
                <td>{{ item.description ? item.description.substring(0, 20) + '...' : '未设置' }}</td>
                <td>{{ item.sortOrder }}</td>
                <td>{{ formatDate(item.createdAt) }}</td>
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
        title="里程碑"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="false"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <!-- 里程碑详情 -->
      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>里程碑详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>日期:</label>
              <span>{{ selectedItem?.milestoneDate }}</span>
            </div>
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedItem?.description }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatDate(selectedItem?.createdAt) }}</span>
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
  { key: 'milestoneDate', title: '日期' },
  { key: 'title', title: '标题' },
  { key: 'description', title: '描述' },
  { key: 'sortOrder', title: '排序' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const milestones = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  milestoneDate: '',
  title: '',
  description: '',
  sortOrder: 0,
});

// 使用 text + inputType:date，原生日期选择器返回 yyyy-MM-dd，与后端 LocalDate 直接兼容
const formFields = [
  [
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入里程碑标题' },
    { name: 'milestoneDate', label: '日期', type: 'text', inputType: 'date', required: true },
  ],
  [
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', rows: 4, placeholder: '请输入里程碑描述' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredMilestones = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (milestones.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.title && item.title.toLowerCase().includes(keyword)) ||
      (item.milestoneDate && item.milestoneDate.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchMilestones();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchMilestones();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchMilestones();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = milestones.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的里程碑', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个里程碑，请逐个删除`, 'info');
};

const fetchMilestones = async () => {
  try {
    const response = await websiteIntroductionApi.getMilestoneList();
    if (response.code === 200) {
      const data = response.data || [];
      milestones.value = data.map(item => ({ ...item, checked: false }));
      total.value = data.length;
    } else {
      milestones.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取里程碑失败:', error);
    milestones.value = [];
    total.value = 0;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = { id: null, milestoneDate: '', title: '', description: '', sortOrder: 0 };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    milestoneDate: item.milestoneDate,
    title: item.title,
    description: item.description,
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
  if (!data.title) {
    return '请填写里程碑标题';
  }
  if (!data.milestoneDate) {
    return '请选择日期';
  }
  return null;
};

const handleSubmit = async (data) => {
  try {
    await websiteIntroductionApi.saveMilestone(data);
    showToastMessage(isEditing.value ? '更新里程碑成功' : '新增里程碑成功');
    showDialog.value = false;
    await fetchMilestones();
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
      await websiteIntroductionApi.deleteMilestone(deleteId.value);
      showToastMessage('删除里程碑成功');
      await fetchMilestones();
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除里程碑失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchMilestones);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
