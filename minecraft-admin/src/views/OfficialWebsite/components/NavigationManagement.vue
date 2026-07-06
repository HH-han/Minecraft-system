<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入导航名称搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增导航</button>
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
              <tr v-for="item in filteredNavigations" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.title }}</td>
                <td>{{ item.url }}</td>
                <td>{{ item.icon }}</td>
                <td>{{ item.sortOrder }}</td>
                <td>
                  <label class="switch">
                    <input type="checkbox" :checked="item.enabled" @change="toggleEnabled(item)" />
                    <span class="slider"></span>
                  </label>
                </td>
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
        title="导航"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="false"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>导航详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>导航名称:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>链接地址:</label>
              <span>{{ selectedItem?.url }}</span>
            </div>
            <div class="detail-item">
              <label>图标:</label>
              <span>{{ selectedItem?.icon }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item">
              <label>是否启用:</label>
              <span>{{ selectedItem?.enabled ? '是' : '否' }}</span>
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
import officialwebsiteApi from '@/api/officialwebsite';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: 'ID' },
  { key: 'title', title: '导航名称' },
  { key: 'url', title: '链接地址' },
  { key: 'icon', title: '图标' },
  { key: 'sortOrder', title: '排序' },
  { key: 'enabled', title: '状态' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const navigations = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  title: '',
  url: '',
  icon: '',
  sortOrder: 0,
  enabled: true,
});

const formFields = [
  [
    { title: 'title', label: '导航名称', type: 'text', required: true, placeholder: '请输入导航名称' },
    { title: 'url', label: '链接地址', type: 'text', required: true, placeholder: '请输入链接地址' },
    { title: 'icon', label: '图标', type: 'text', placeholder: '请输入图标名称' },
    { title: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
  [
    { title: 'enabled', label: '启用', type: 'switch' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredNavigations = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (navigations.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.title && item.title.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchNavigations();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchNavigations();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchNavigations();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = navigations.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的导航', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个导航，请逐个删除`, 'info');
};

const fetchNavigations = async () => {
  try {
    const response = await officialwebsiteApi.getNavigation();
    if (response.code === 200) {
      const data = response.data || [];
      navigations.value = data.map(item => ({
        ...item,
        checked: false
      }));
      total.value = data.length;
    } else {
      console.error('获取导航数据失败:', response.msg || response.message || '未知错误');
      navigations.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取导航数据失败:', error);
    navigations.value = [];
    total.value = 0;
  }
};

const toggleEnabled = async (item) => {
  try {
    item.enabled = !item.enabled;
    showToastMessage(item.enabled ? '已启用' : '已禁用', 'success');
  } catch (error) {
    item.enabled = !item.enabled;
    showToastMessage('操作失败', 'error');
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    title: '',
    url: '',
    icon: '',
    sortOrder: 0,
    enabled: true,
  };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    ...item,
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
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

const validateForm = (data) => {
  if (!data.title || !data.url) {
    return '请填写所有必填字段';
  }
  return null;
};

const handleSubmit = async (data) => {
  if (isEditing.value) {
    showToastMessage('更新导航成功');
  } else {
    showToastMessage('新增导航成功');
  }
  await fetchNavigations();
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
      navigations.value = navigations.value.filter(item => item.id !== deleteId.value);
      total.value = navigations.value.length;
      showToastMessage('删除导航成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除导航失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchNavigations);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>