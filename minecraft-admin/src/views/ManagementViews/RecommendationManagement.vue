<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入推荐标题或内容搜索" class="search-input-management" />

          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
        </div>

        <button class="btn add-btn" @click="showAddDialog">推荐景点</button>
      </div>
      <!-- 数据表格 -->
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
              <tr v-for="item in filteredItems" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.name }}</td>
                <td>
                  <img :src="item.image" alt="图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(item)">
                </td>
                <td>{{ item.description ? item.description.slice(0, 20) : '无' }}...</td>
                <td>{{ item.details ? item.details.slice(0, 20) : '无' }}...</td>
                <td>
                  <label class="switch">
                    <input type="checkbox" :checked="item.state === '0'" @change="toggleState(item)">
                    <span class="slider" :class="{ 'green': item.state === '0', 'red': item.state !== '0' }"></span>
                    <span class="knob"></span>
                  </label>
                </td>
                <td class="table-btn-display">
                  <button class="btn edit-btn" @click="showEditDialog(item)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(item.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="推荐"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传图片"
        recommendedSize="推荐尺寸：1200×800px"
        imageField="image"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <!-- 删除提示框组件 -->
      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <!-- 自定义提示框组件 -->
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" @close="closeToast" />
    </div>
  </div>

</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getRecommendList, getAllRecommendList, addRecommend, updateRecommend, deleteRecommend } from '@/api/recommend';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';


const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: 'ID' },
  { key: 'name', title: '名称' },
  { key: 'image', title: '图片' },
  { key: 'description', title: '描述' },
  { key: 'details', title: '详情' },
  { key: 'state', title: '状态' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const items = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: null,
  name: '',
  image: '',
  description: '',
  details: '',
  state: '0'
});

// 表单字段配置
const formFields = [
  [
    { name: 'name', label: '名称', type: 'text', required: true, placeholder: '请输入名称' },
    { name: 'description', label: '描述', type: 'text', required: true, placeholder: '请输入描述' },
  ],
  [
    { name: 'details', label: '详情', type: 'textarea', required: true, rows: 4, placeholder: '请输入详情' },
    { name: 'state', label: '状态', type: 'select', required: true, options: [
      { value: '0', label: '显示' },
      { value: '1', label: '不显示' },
    ]},
  ],
];

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.name || !data.description || !data.details) {
    return '请填写所有必填字段';
  }
  if (!isEdit && !data.image) {
    return '请上传图片';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  if (isEdit) {
    await updateRecommend(data);
  } else {
    await addRecommend(data);
  }
};



// 搜索功能
const filteredItems = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return items.value.filter(
    (item) =>
      String(item.id).includes(keyword) ||
      item.name.toLowerCase().includes(keyword) ||
      item.description.toLowerCase().includes(keyword) ||
      item.details.toLowerCase().includes(keyword)
  );
});

// 获取数据
const fetchItems = async () => {
  try {
    const response = await getAllRecommendList();
    if (response.code === 200 && response.data) {
      items.value = response.data;
    }
  } catch (error) {
    console.error('获取数据失败:', error);
  }
};

// 修改状态
const toggleState = async (item) => {
  try {
    const newState = item.state === '0' ? '1' : '0';
    item.state = newState;
    await updateRecommend(item);
    showToastMessage('修改状态成功');
  } catch (error) {
    console.error('修改状态失败:', error);
    showToastMessage('修改状态失败', 'error');
  }
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    name: '',
    image: '',
    description: '',
    details: '',
    state: '0'
  };
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = { ...item };
  showDialog.value = true;
};
// 显示提示消息的方法
const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// 处理错误
const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

// 删除商品
const isDeletePromptVisible = ref(false);
const deleteItemId = ref(null);

const handleDelete = (id) => {
  deleteItemId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteItemId.value = null;
};

// 关闭toast
const closeToast = () => {
  showToast.value = false;
};
//删除
const confirmDelete = async () => {
  if (deleteItemId.value) {
    try {
      await deleteRecommend(deleteItemId.value);
      await fetchItems();
      showToastMessage('删除推荐成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除推荐失败', 'error');
      console.error('删除失败:', error.response?.data || error.message);
    } finally {
      closeDeletePrompt();
    }
  }
};

// 处理复选框选择
const handleCheck = (item) => {
  item.checked = !item.checked;
};

onMounted(fetchItems);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>