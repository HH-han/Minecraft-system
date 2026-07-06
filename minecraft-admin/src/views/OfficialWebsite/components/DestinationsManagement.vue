<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入目的地名称搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增目的地</button>
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
              <tr v-for="item in filteredDestinations" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.name }}</td>
                <td>{{ item.category }}</td>
                <td>{{ item.duration }}</td>
                <td>{{ item.description ? item.description.substring(0, 20) : '未设置' }}</td>
                <td>
                  <img :src="item.imageUrl?.replace(/[`\s]/g, '')" alt="图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(item)" />
                </td>
                <td>{{ item.isFeatured === 1 ? '是' : '否' }}</td>
                <td>{{ item.price }}</td>
                <td>{{ item.priceCurrency }}</td>
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
        title="目的地"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传目的地图片"
        recommendedSize="推荐尺寸：800×600px"
        imageField="imageUrl"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>目的地详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>名称:</label>
              <span>{{ selectedItem?.name }}</span>
            </div>
            <div class="detail-item">
              <label>分类:</label>
              <span>{{ selectedItem?.category }}</span>
            </div>
            <div class="detail-item">
              <label>时长:</label>
              <span>{{ selectedItem?.duration }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedItem?.description }}</span>
            </div>
            <div class="detail-item">
              <label>价格:</label>
              <span>{{ selectedItem?.priceCurrency }} {{ selectedItem?.price }}</span>
            </div>
            <div class="detail-item">
              <label>是否推荐:</label>
              <span>{{ selectedItem?.isFeatured === 1 ? '是' : '否' }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatDate(selectedItem?.createdAt) }}</span>
            </div>
            <div class="detail-item" v-if="selectedItem?.imageUrl">
              <label>图片:</label>
              <img :src="selectedItem.imageUrl.replace(/[`\s]/g, '')" alt="目的地图片" style="max-width: 100%; max-height: 300px;" />
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
  { key: 'name', title: '名称' },
  { key: 'category', title: '分类' },
  { key: 'duration', title: '时长' },
  { key: 'description', title: '描述' },
  { key: 'imageUrl', title: '图片' },
  { key: 'isFeatured', title: '推荐' },
  { key: 'price', title: '价格' },
  { key: 'priceCurrency', title: '货币' },
  { key: 'sortOrder', title: '排序' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const destinations = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  name: '',
  category: '',
  duration: '',
  description: '',
  imageUrl: '',
  price: 0,
  priceCurrency: 'CNY',
  isFeatured: 0,
  sortOrder: 0,
});

const formFields = [
  [
    { name: 'name', label: '名称', type: 'text', required: true, placeholder: '请输入目的地名称' },
    { name: 'category', label: '分类', type: 'text', required: true, placeholder: '如：欧洲 · 浪漫' },
    { name: 'duration', label: '时长', type: 'text', required: true, placeholder: '如：6 天 5 晚' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', rows: 3, placeholder: '请输入目的地描述' },
    { name: 'price', label: '价格', type: 'number', min: 0, placeholder: '价格' },
    { name: 'priceCurrency', label: '货币', type: 'text', placeholder: '如：CNY' },
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

const filteredDestinations = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (destinations.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.name && item.name.toLowerCase().includes(keyword)) ||
      (item.category && item.category.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchDestinations();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchDestinations();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchDestinations();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = destinations.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的目的地', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个目的地，请逐个删除`, 'info');
};

const fetchDestinations = async () => {
  try {
    const response = await officialwebsiteApi.getDestinations();
    if (response.code === 200) {
      const data = response.data || [];
      destinations.value = data.map(item => ({
        ...item,
        checked: false
      }));
      total.value = data.length;
    } else {
      console.error('获取目的地数据失败:', response.msg || response.message || '未知错误');
      destinations.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取目的地数据失败:', error);
    destinations.value = [];
    total.value = 0;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    name: '',
    category: '',
    duration: '',
    description: '',
    imageUrl: '',
    price: 0,
    priceCurrency: 'CNY',
    isFeatured: 0,
    sortOrder: 0,
  };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    name: item.name,
    category: item.category,
    duration: item.duration,
    description: item.description,
    imageUrl: item.imageUrl,
    price: item.price,
    priceCurrency: item.priceCurrency,
    isFeatured: item.isFeatured,
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
  if (!data.name || !data.category || !data.duration) {
    return '请填写所有必填字段';
  }
  return null;
};

const handleSubmit = async (data) => {
  try {
    const submitData = {
      ...data,
      isFeatured: data.isFeatured ? 1 : 0
    };
    await officialwebsiteApi.saveDestination(submitData);
    if (isEditing.value) {
      showToastMessage('更新目的地成功');
    } else {
      showToastMessage('新增目的地成功');
    }
    showDialog.value = false;
    await fetchDestinations();
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
      await officialwebsiteApi.deleteDestination(deleteId.value);
      destinations.value = destinations.value.filter(item => item.id !== deleteId.value);
      total.value = destinations.value.length;
      showToastMessage('删除目的地成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除目的地失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchDestinations);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
