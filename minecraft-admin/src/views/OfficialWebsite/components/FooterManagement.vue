<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <button class="btn edit-btn" @click="showEditDialog(footerData)" v-if="footerData">编辑页脚</button>
        <button class="btn add-btn" @click="showAddDialog" v-else>新增页脚</button>
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
              <tr v-if="footerData">
                <td>{{ footerData.id }}</td>
                <td>{{ footerData.brandName }}</td>
                <td>{{ footerData.brandDescription ? footerData.brandDescription.substring(0, 20) : '未设置' }}</td>
                <td>{{ footerData.copyrightText }}</td>
                <td>{{ formatSocialLinks(footerData.socialLinks) }}</td>
                <td>{{ formatDate(footerData.updatedAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(footerData)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(footerData)">编辑</button>
                </td>
              </tr>
              <tr v-else>
                <td colspan="7" style="text-align: center;">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <FormDialog
        v-model:visible="showDialog"
        title="页脚信息"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>页脚信息详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>品牌名称:</label>
              <span>{{ selectedItem?.brandName }}</span>
            </div>
            <div class="detail-item">
              <label>品牌描述:</label>
              <span>{{ selectedItem?.brandDescription }}</span>
            </div>
            <div class="detail-item">
              <label>版权文本:</label>
              <span>{{ selectedItem?.copyrightText }}</span>
            </div>
            <div class="detail-item">
              <label>社交链接:</label>
              <span>{{ selectedItem?.socialLinks }}</span>
            </div>
            <div class="detail-item">
              <label>更新时间:</label>
              <span>{{ formatDate(selectedItem?.updatedAt) }}</span>
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
  { key: 'brandName', title: '品牌名称' },
  { key: 'brandDescription', title: '品牌描述' },
  { key: 'copyrightText', title: '版权文本' },
  { key: 'socialLinks', title: '社交链接' },
  { key: 'updatedAt', title: '更新时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const footerData = ref(null);
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  brandName: '',
  brandDescription: '',
  copyrightText: '',
  socialLinks: '',
});

const formFields = [
  [
    { name: 'brandName', label: '品牌名称', type: 'text', required: true, placeholder: '请输入品牌名称' },
    { name: 'copyrightText', label: '版权文本', type: 'text', placeholder: '例如：© 2026 叙事之旅 版权所有' },
  ],
  [
    { name: 'brandDescription', label: '品牌描述', type: 'textarea', rows: 3, placeholder: '请输入品牌描述' },
    { name: 'socialLinks', label: '社交链接', type: 'textarea', rows: 2, placeholder: '{"weibo": "@travelstory", "wechat": "travel_story"}' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const formatSocialLinks = (links) => {
  if (!links) return '-';
  try {
    const parsed = JSON.parse(links);
    return Object.keys(parsed).join(', ');
  } catch {
    return links.substring(0, 20) + '...';
  }
};

const fetchFooter = async () => {
  try {
    const response = await officialwebsiteApi.getFooter();
    if (response.code === 200) {
      footerData.value = response.data || null;
    } else {
      console.error('获取页脚数据失败:', response.msg || response.message || '未知错误');
      footerData.value = null;
    }
  } catch (error) {
    console.error('获取页脚数据失败:', error);
    footerData.value = null;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    brandName: '',
    brandDescription: '',
    copyrightText: '',
    socialLinks: '',
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
  if (!data.brandName) {
    return '请填写品牌名称';
  }
  return null;
};

const handleSubmit = async (data) => {
  try {
    await officialwebsiteApi.saveFooter(data);
    if (isEditing.value) {
      showToastMessage('更新页脚信息成功');
    } else {
      showToastMessage('新增页脚信息成功');
    }
    showDialog.value = false;
    await fetchFooter();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

onMounted(fetchFooter);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
