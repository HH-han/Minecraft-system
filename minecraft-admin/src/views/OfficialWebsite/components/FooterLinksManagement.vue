<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入链接标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增链接</button>
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
              <tr v-for="item in filteredFooterLinks" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.category }}</td>
                <td>{{ item.title }}</td>
                <td>{{ item.url }}</td>
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
        title="页脚链接"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>页脚链接详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>分类:</label>
              <span>{{ selectedItem?.category }}</span>
            </div>
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>链接:</label>
              <span><a :href="selectedItem?.url" target="_blank">{{ selectedItem?.url }}</a></span>
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
import officialwebsiteApi from '@/api/officialwebsite';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: 'ID' },
  { key: 'category', title: '分类' },
  { key: 'title', title: '标题' },
  { key: 'url', title: '链接' },
  { key: 'sortOrder', title: '排序' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const footerLinks = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  category: '',
  title: '',
  url: '',
  sortOrder: 0,
});

const formFields = [
  [
    { name: 'category', label: '分类', type: 'text', required: true, placeholder: '如：关于、探索、支持' },
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入链接标题' },
  ],
  [
    { name: 'url', label: '链接地址', type: 'text', placeholder: '请输入链接地址' },
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredFooterLinks = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (footerLinks.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.title && item.title.toLowerCase().includes(keyword)) ||
      (item.category && item.category.toLowerCase().includes(keyword)) ||
      (item.url && item.url.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchFooterLinks();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchFooterLinks();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchFooterLinks();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = footerLinks.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的链接', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个链接，请逐个删除`, 'info');
};

const fetchFooterLinks = async () => {
  try {
    const response = await officialwebsiteApi.getFooterLinks();
    if (response.code === 200) {
      const data = response.data || [];
      footerLinks.value = data.map(item => ({
        ...item,
        checked: false
      }));
      total.value = data.length;
    } else {
      console.error('获取页脚链接数据失败:', response.msg || response.message || '未知错误');
      footerLinks.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取页脚链接数据失败:', error);
    footerLinks.value = [];
    total.value = 0;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    category: '',
    title: '',
    url: '',
    sortOrder: 0,
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
  if (!data.category || !data.title) {
    return '请填写分类和标题';
  }
  return null;
};

const handleSubmit = async (data) => {
  if (isEditing.value) {
    showToastMessage('更新页脚链接成功');
  } else {
    showToastMessage('新增页脚链接成功');
  }
  await fetchFooterLinks();
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
      footerLinks.value = footerLinks.value.filter(item => item.id !== deleteId.value);
      total.value = footerLinks.value.length;
      showToastMessage('删除页脚链接成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除页脚链接失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchFooterLinks);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
