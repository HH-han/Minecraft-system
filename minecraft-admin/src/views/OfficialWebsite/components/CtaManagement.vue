<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <button class="btn edit-btn" @click="showEditDialog(ctaData)" v-if="ctaData">编辑行动号召</button>
        <button class="btn add-btn" @click="showAddDialog" v-else>新增行动号召</button>
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
              <tr v-if="ctaData">
                <td>{{ ctaData.id }}</td>
                <td>{{ ctaData.title }}</td>
                <td>{{ ctaData.description }}</td>
                <td>{{ ctaData.btnText }}</td>
                <td>{{ ctaData.btnUrl }}</td>
                <td>
                  <div class="color-preview" :style="{ backgroundColor: ctaData.bgColor }"></div>
                </td>
                <td>{{ ctaData.isActive === 1 ? '启用' : '禁用' }}</td>
                <td>{{ formatDate(ctaData.createdAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(ctaData)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(ctaData)">编辑</button>
                </td>
              </tr>
              <tr v-else>
                <td colspan="9" style="text-align: center;">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <FormDialog
        v-model:visible="showDialog"
        title="行动号召"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>行动号召详情</h2>
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
              <label>按钮文本:</label>
              <span>{{ selectedItem?.btnText }}</span>
            </div>
            <div class="detail-item">
              <label>按钮链接:</label>
              <span>{{ selectedItem?.btnUrl }}</span>
            </div>
            <div class="detail-item">
              <label>背景颜色:</label>
              <div class="color-preview" :style="{ backgroundColor: selectedItem?.bgColor }"></div>
              <span>{{ selectedItem?.bgColor }}</span>
            </div>
            <div class="detail-item">
              <label>是否启用:</label>
              <span>{{ selectedItem?.isActive === 1 ? '是' : '否' }}</span>
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

      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import officialwebsiteApi from '@/api/officialwebsite';
import FormDialog from '@/components/FormDialog.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'id', title: 'ID' },
  { key: 'title', title: '标题' },
  { key: 'description', title: '描述' },
  { key: 'btnText', title: '按钮文本' },
  { key: 'btnUrl', title: '按钮链接' },
  { key: 'bgColor', title: '背景颜色' },
  { key: 'isActive', title: '状态' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const ctaData = ref(null);
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  title: '',
  description: '',
  btnText: '',
  btnUrl: '',
  bgColor: '',
  isActive: 1,
});

const formFields = [
  [
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入标题' },
    { name: 'btnText', label: '按钮文本', type: 'text', placeholder: '请输入按钮文本' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', rows: 2, placeholder: '请输入描述' },
    { name: 'btnUrl', label: '按钮链接', type: 'text', placeholder: '请输入按钮链接' },
  ],
  [
    { name: 'bgColor', label: '背景颜色', type: 'text', placeholder: '例如：#2c3e50' },
    { name: 'isActive', label: '启用', type: 'switch' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const fetchCta = async () => {
  try {
    const response = await officialwebsiteApi.getCta();
    if (response.code === 200) {
      ctaData.value = response.data || null;
    } else {
      console.error('获取行动号召数据失败:', response.msg || response.message || '未知错误');
      ctaData.value = null;
    }
  } catch (error) {
    console.error('获取行动号召数据失败:', error);
    ctaData.value = null;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    title: '',
    description: '',
    btnText: '',
    btnUrl: '',
    bgColor: '',
    isActive: 1,
  };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    title: item.title,
    description: item.description,
    btnText: item.btnText,
    btnUrl: item.btnUrl,
    bgColor: item.bgColor,
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
    await officialwebsiteApi.saveCta(submitData);
    if (isEditing.value) {
      showToastMessage('更新行动号召成功');
    } else {
      showToastMessage('新增行动号召成功');
    }
    showDialog.value = false;
    await fetchCta();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

onMounted(fetchCta);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.color-preview {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  display: inline-block;
  border: 1px solid #ddd;
}
</style>
