<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <button class="btn edit-btn" @click="showEditDialog(heroData)" v-if="heroData">编辑Banner</button>
        <button class="btn add-btn" @click="showAddDialog" v-else>新增Banner</button>
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
              <tr v-if="heroData">
                <td>{{ heroData.id }}</td>
                <td>{{ heroData.badgeText }}</td>
                <td>{{ heroData.title }}</td>
                <td>{{ heroData.subtitle }}</td>
                <td>
                  <img :src="heroData.bgImage?.replace(/[`\s]/g, '')" alt="图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(heroData)" />
                </td>
                <td>{{ heroData.description ? heroData.description.substring(0, 20) : '未设置' }}</td>
                <td>{{ heroData.btnText || '-' }}</td>
                <td>{{ heroData.isActive === 1 ? '启用' : '禁用' }}</td>
                <td>{{ formatDate(heroData.createdAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(heroData)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(heroData)">编辑</button>
                </td>
              </tr>
              <tr v-else>
                <td colspan="10" style="text-align: center;">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <FormDialog
        v-model:visible="showDialog"
        title="Hero Banner"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传Banner图片"
        recommendedSize="推荐尺寸：1920×1080px"
        imageField="bgImage"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>Hero Banner详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>徽章文本:</label>
              <span>{{ selectedItem?.badgeText }}</span>
            </div>
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>副标题:</label>
              <span>{{ selectedItem?.subtitle }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedItem?.description }}</span>
            </div>
            <div class="detail-item">
              <label>按钮文字:</label>
              <span>{{ selectedItem?.btnText || '-' }}</span>
            </div>
            <div class="detail-item">
              <label>是否启用:</label>
              <span>{{ selectedItem?.isActive === 1 ? '是' : '否' }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatDate(selectedItem?.createdAt) }}</span>
            </div>
            <div class="detail-item" v-if="selectedItem?.bgImage">
              <label>背景图片:</label>
              <img :src="selectedItem.bgImage.replace(/[`\s]/g, '')" alt="Banner图片" style="max-width: 100%; max-height: 300px;" />
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
  { key: 'badgeText', title: '徽章文本' },
  { key: 'title', title: '标题' },
  { key: 'subtitle', title: '副标题' },
  { key: 'bgImage', title: '背景图片' },
  { key: 'description', title: '描述' },
  { key: 'btnText', title: '按钮文字' },
  { key: 'isActive', title: '状态' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const heroData = ref(null);
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  badgeText: '',
  title: '',
  subtitle: '',
  bgImage: '',
  bgVideo: null,
  description: '',
  btnText: '',
  isActive: 1,
});

const formFields = [
  [
    { name: 'badgeText', label: '徽章文本', type: 'text', placeholder: '例如：2026 · 叙事之旅' },
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入标题' },
    { name: 'subtitle', label: '副标题', type: 'text', placeholder: '请输入副标题' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', rows: 3, placeholder: '请输入描述' },
    { name: 'btnText', label: '按钮文字', type: 'text', placeholder: '请输入按钮文字' },
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

const fetchHero = async () => {
  try {
    const response = await officialwebsiteApi.getHero();
    if (response.code === 200) {
      heroData.value = response.data || null;
    } else {
      console.error('获取Hero数据失败:', response.msg || response.message || '未知错误');
      heroData.value = null;
    }
  } catch (error) {
    console.error('获取Hero数据失败:', error);
    heroData.value = null;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    badgeText: '',
    title: '',
    subtitle: '',
    bgImage: '',
    bgVideo: null,
    description: '',
    btnText: '',
    isActive: 1,
  };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    badgeText: item.badgeText,
    title: item.title,
    subtitle: item.subtitle,
    bgImage: item.bgImage,
    bgVideo: item.bgVideo,
    description: item.description,
    btnText: item.btnText,
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
  if (item.bgImage) {
    window.open(item.bgImage.replace(/[`\s]/g, ''), '_blank');
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
    await officialwebsiteApi.saveHero(submitData);
    if (isEditing.value) {
      showToastMessage('更新Banner成功');
    } else {
      showToastMessage('新增Banner成功');
    }
    showDialog.value = false;
    await fetchHero();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

onMounted(fetchHero);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
