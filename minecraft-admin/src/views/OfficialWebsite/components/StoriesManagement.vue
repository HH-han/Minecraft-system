<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入故事标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增故事</button>
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
              <tr v-for="item in filteredStories" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.title }}</td>
                <td>{{ item.authorName }}</td>
                <td>{{ item.authorRole }}</td>
                <td>{{ item.content ? item.content.substring(0, 20) : '未设置' }}</td>
                <td>{{ item.destination }}</td>
                <td>{{ item.rating }} 星</td>
                <td>{{ item.sortOrder }}</td>
                <td>
                  <label class="switch">
                    <input type="checkbox" :checked="item.isFeatured" @change="toggleEnabled(item)" />
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
        title="故事"
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
          <h2>故事详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>作者名称:</label>
              <span>{{ selectedItem?.authorName }}</span>
            </div>
            <div class="detail-item">
              <label>作者角色:</label>
              <span>{{ selectedItem?.authorRole }}</span>
            </div>
            <div class="detail-item">
              <label>目的地:</label>
              <span>{{ selectedItem?.destination }}</span>
            </div>
            <div class="detail-item">
              <label>评分:</label>
              <span>{{ selectedItem?.rating }} 星</span>
            </div>
            <div class="detail-item">
              <label>内容:</label>
              <span>{{ selectedItem?.content }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item">
              <label>是否推荐:</label>
              <span>{{ selectedItem?.isFeatured ? '是' : '否' }}</span>
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
  { key: 'title', title: '标题' },
  { key: 'authorName', title: '作者名称' },
  { key: 'authorRole', title: '作者角色' },
  { key: 'content', title: '内容' },
  { key: 'destination', title: '目的地' },
  { key: 'rating', title: '评分' },
  { key: 'sortOrder', title: '排序' },
  { key: 'isFeatured', title: '推荐' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const stories = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  title: '',
  authorName: '',
  authorRole: '',
  destination: '',
  rating: 5,
  content: '',
  sortOrder: 0,
  isFeatured: false,
});

const formFields = [
  [
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入故事标题' },
    { name: 'authorName', label: '作者名称', type: 'text', placeholder: '请输入作者名称' },
    { name: 'authorRole', label: '作者角色', type: 'text', placeholder: '如：自由撰稿人' },
  ],
  [
    { name: 'destination', label: '目的地', type: 'text', placeholder: '如：巴黎' },
    { name: 'rating', label: '评分', type: 'number', min: 1, max: 5, placeholder: '1-5星' },
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
  [
    { name: 'content', label: '内容', type: 'textarea', rows: 4, placeholder: '请输入故事内容' },
  ],
  [
    { name: 'isFeatured', label: '推荐', type: 'switch' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredStories = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (stories.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.title && item.title.toLowerCase().includes(keyword)) ||
      (item.authorName && item.authorName.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchStories();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchStories();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchStories();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = stories.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的故事', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个故事，请逐个删除`, 'info');
};

const fetchStories = async () => {
  try {
    const response = await officialwebsiteApi.getStories();
    if (response.code === 200) {
      const data = response.data || [];
      stories.value = data.map(item => ({
        ...item,
        checked: false
      }));
      total.value = data.length;
    } else {
      console.error('获取故事数据失败:', response.msg || response.message || '未知错误');
      stories.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取故事数据失败:', error);
    stories.value = [];
    total.value = 0;
  }
};

const toggleEnabled = async (item) => {
  try {
    item.isFeatured = !item.isFeatured;
    showToastMessage(item.isFeatured ? '已推荐' : '已取消推荐', 'success');
  } catch (error) {
    item.isFeatured = !item.isFeatured;
    showToastMessage('操作失败', 'error');
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    title: '',
    authorName: '',
    authorRole: '',
    destination: '',
    rating: 5,
    content: '',
    sortOrder: 0,
    isFeatured: false,
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
  if (!data.title) {
    return '请填写标题';
  }
  return null;
};

const handleSubmit = async (data) => {
  if (isEditing.value) {
    showToastMessage('更新故事成功');
  } else {
    showToastMessage('新增故事成功');
  }
  await fetchStories();
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
      stories.value = stories.value.filter(item => item.id !== deleteId.value);
      total.value = stories.value.length;
      showToastMessage('删除故事成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除故事失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchStories);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
