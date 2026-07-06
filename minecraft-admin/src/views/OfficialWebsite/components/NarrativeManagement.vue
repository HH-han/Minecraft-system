<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增叙事内容</button>
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
              <tr v-for="item in filteredNarratives" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.title }}</td>
                <td>{{ item.subtitle }}</td>
                <td>{{ item.content ? item.content.substring(0, 20) : '未设置' }}</td>
                <td>{{ item.quoteText ? item.quoteText.substring(0, 15) : '未设置' }}</td>
                <td>
                  <img :src="item.imageUrl?.replace(/[`\s]/g, '')" alt="图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(item)" />
                </td>
                <td>{{ item.sortOrder }}</td>
                <td>
                  <label class="switch">
                    <input type="checkbox" :checked="item.isActive === 1" @change="toggleEnabled(item)" />
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
        title="叙事内容"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传叙事图片"
        recommendedSize="推荐尺寸：800×600px"
        imageField="imageUrl"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>叙事内容详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>副标题:</label>
              <span>{{ selectedItem?.subtitle }}</span>
            </div>
            <div class="detail-item">
              <label>内容:</label>
              <span>{{ selectedItem?.content }}</span>
            </div>
            <div class="detail-item">
              <label>引用语:</label>
              <span>{{ selectedItem?.quoteText }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item">
              <label>是否启用:</label>
              <span>{{ selectedItem?.isActive === 1 ? '是' : '否' }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatDate(selectedItem?.createdAt) }}</span>
            </div>
            <div class="detail-item" v-if="selectedItem?.imageUrl">
              <label>图片:</label>
              <img :src="selectedItem.imageUrl.replace(/[`\s]/g, '')" alt="叙事图片" style="max-width: 100%; max-height: 300px;" />
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
  { key: 'subtitle', title: '副标题' },
  { key: 'content', title: '内容' },
  { key: 'quoteText', title: '引用语' },
  { key: 'imageUrl', title: '图片' },
  { key: 'sortOrder', title: '排序' },
  { key: 'isActive', title: '状态' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const narratives = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  title: '',
  subtitle: '',
  content: '',
  quoteText: '',
  imageUrl: '',
  sortOrder: 0,
  isActive: 1,
});

const formFields = [
  [
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入标题' },
    { name: 'subtitle', label: '副标题', type: 'text', placeholder: '请输入副标题' },
  ],
  [
    { name: 'content', label: '内容', type: 'textarea', rows: 4, placeholder: '请输入叙事内容' },
  ],
  [
    { name: 'quoteText', label: '引用语', type: 'textarea', rows: 2, placeholder: '请输入引用语' },
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
  [
    { name: 'isActive', label: '启用', type: 'switch' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredNarratives = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (narratives.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.title && item.title.toLowerCase().includes(keyword)) ||
      (item.subtitle && item.subtitle.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchNarratives();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchNarratives();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchNarratives();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = narratives.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的叙事内容', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个叙事内容，请逐个删除`, 'info');
};

const fetchNarratives = async () => {
  try {
    const response = await officialwebsiteApi.getNarrative();
    if (response.code === 200) {
      const data = response.data || [];
      narratives.value = data.map(item => ({
        ...item,
        checked: false
      }));
      total.value = data.length;
    } else {
      console.error('获取叙事内容数据失败:', response.msg || response.message || '未知错误');
      narratives.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取叙事内容数据失败:', error);
    narratives.value = [];
    total.value = 0;
  }
};

const toggleEnabled = async (item) => {
  const oldValue = item.isActive;
  try {
    item.isActive = item.isActive === 1 ? 0 : 1;
    await officialwebsiteApi.saveNarrative(item);
    showToastMessage(item.isActive === 1 ? '已启用' : '已禁用', 'success');
  } catch (error) {
    item.isActive = oldValue;
    showToastMessage('操作失败', 'error');
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    title: '',
    subtitle: '',
    content: '',
    quoteText: '',
    imageUrl: '',
    sortOrder: 0,
    isActive: 1,
  };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    title: item.title,
    subtitle: item.subtitle,
    content: item.content,
    quoteText: item.quoteText,
    imageUrl: item.imageUrl,
    sortOrder: item.sortOrder,
    isActive: item.isActive,
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

const triggerFileInput = (item) => {
  if (item.imageUrl) {
    window.open(item.imageUrl, '_blank');
  }
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
  try {
    const submitData = {
      ...data,
      isActive: data.isActive ? 1 : 0
    };
    await officialwebsiteApi.saveNarrative(submitData);
    if (isEditing.value) {
      showToastMessage('更新叙事内容成功');
    } else {
      showToastMessage('新增叙事内容成功');
    }
    showDialog.value = false;
    await fetchNarratives();
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
      await officialwebsiteApi.deleteNarrative(deleteId.value);
      narratives.value = narratives.value.filter(item => item.id !== deleteId.value);
      total.value = narratives.value.length;
      showToastMessage('删除叙事内容成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除叙事内容失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchNarratives);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
