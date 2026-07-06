<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入体验名称搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增体验</button>
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
              <tr v-for="item in filteredExperiences" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.title }}</td>
                <td>{{ item.description ? item.description.substring(0, 20) : '未设置' }}</td>
                <td>{{ item.category }}</td>
                <td>
                  <img :src="item.image?.replace(/[`\s]/g, '')" alt="图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(item)" />
                </td>
                <td>{{ item.price }}</td>
                <td>{{ item.sortOrder }}</td>
                <td>
                  <label class="switch">
                    <input type="checkbox" :checked="item.enabled" @change="toggleEnabled(item)" />
                    <span class="slider"></span>
                  </label>
                </td>
                <td>{{ formatDate(item.createTime) }}</td>
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
        title="体验"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传体验图片"
        recommendedSize="推荐尺寸：800×600px"
        imageField="image"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>体验详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedItem?.description }}</span>
            </div>
            <div class="detail-item">
              <label>分类:</label>
              <span>{{ selectedItem?.category }}</span>
            </div>
            <div class="detail-item">
              <label>价格:</label>
              <span>{{ selectedItem?.price }}</span>
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
              <span>{{ formatDate(selectedItem?.createTime) }}</span>
            </div>
            <div class="detail-item" v-if="selectedItem?.image">
              <label>图片:</label>
              <img :src="selectedItem.image.replace(/[`\s]/g, '')" alt="体验图片" style="max-width: 100%; max-height: 300px;" />
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
  { key: 'description', title: '描述' },
  { key: 'category', title: '分类' },
  { key: 'image', title: '图片' },
  { key: 'price', title: '价格' },
  { key: 'sortOrder', title: '排序' },
  { key: 'enabled', title: '状态' },
  { key: 'createTime', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const experiences = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  title: '',
  description: '',
  category: '',
  image: '',
  price: '',
  sortOrder: 0,
  enabled: true,
});

const formFields = [
  [
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入体验标题' },
    { name: 'category', label: '分类', type: 'text', placeholder: '请输入分类' },
    { name: 'price', label: '价格', type: 'number', min: 0, step: '0.01', placeholder: '请输入价格' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', rows: 3, placeholder: '请输入体验描述' },
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
  [
    { name: 'enabled', label: '启用', type: 'switch' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredExperiences = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (experiences.value || []).filter(
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
  fetchExperiences();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchExperiences();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchExperiences();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = experiences.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的体验', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个体验，请逐个删除`, 'info');
};

const fetchExperiences = async () => {
  try {
    const response = await officialwebsiteApi.getExperiences();
    if (response.code === 200) {
      const data = response.data || [];
      experiences.value = data.map(item => ({
        ...item,
        checked: false
      }));
      total.value = data.length;
    } else {
      console.error('获取体验数据失败:', response.msg || response.message || '未知错误');
      experiences.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取体验数据失败:', error);
    experiences.value = [];
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
    description: '',
    category: '',
    image: '',
    price: '',
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

const triggerFileInput = (item) => {
  if (item.image) {
    window.open(item.image, '_blank');
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
  if (isEditing.value) {
    showToastMessage('更新体验成功');
  } else {
    showToastMessage('新增体验成功');
  }
  await fetchExperiences();
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
      experiences.value = experiences.value.filter(item => item.id !== deleteId.value);
      total.value = experiences.value.length;
      showToastMessage('删除体验成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除体验失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchExperiences);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>