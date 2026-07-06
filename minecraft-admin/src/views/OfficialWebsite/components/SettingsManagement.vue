<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <button class="btn edit-btn" @click="showEditDialog(settingsData)" v-if="settingsData">编辑设置</button>
      </div>

      <div class="data-table-container">
        <div class="data-table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th v-for="col in columns" :key="col.key">{{ col.title }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(setting, index) in settingsList" :key="index">
                <td>{{ setting.label }}</td>
                <td>{{ setting.value }}</td>
                <td>{{ setting.description }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <FormDialog
        v-model:visible="showDialog"
        title="网站设置"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import officialwebsiteApi from '@/api/officialwebsite';
import FormDialog from '@/components/FormDialog.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'label', title: '设置项' },
  { key: 'value', title: '值' },
  { key: 'description', title: '说明' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const settingsData = ref(null);
const showDialog = ref(false);
const isEditing = ref(false);

const settingsList = computed(() => {
  if (!settingsData.value) return [];
  return [
    { label: '网站名称', value: settingsData.value.site_name || '-', description: '网站的品牌名称' },
    { label: '网站标语', value: settingsData.value.site_slogan || '-', description: '网站的宣传标语' },
    { label: '语言', value: settingsData.value.language || '-', description: '网站默认语言' },
    { label: '网站描述', value: settingsData.value.site_description || '-', description: '网站的SEO描述' },
    { label: '默认货币', value: settingsData.value.default_currency || '-', description: '网站默认货币单位' },
  ];
});

const formData = ref({
  site_name: '',
  site_slogan: '',
  language: '',
  site_description: '',
  default_currency: '',
});

const formFields = [
  [
    { name: 'site_name', label: '网站名称', type: 'text', required: true, placeholder: '请输入网站名称' },
    { name: 'site_slogan', label: '网站标语', type: 'text', placeholder: '请输入网站标语' },
  ],
  [
    { name: 'language', label: '语言', type: 'text', placeholder: '例如：zh-CN' },
    { name: 'default_currency', label: '默认货币', type: 'text', placeholder: '例如：CNY' },
  ],
  [
    { name: 'site_description', label: '网站描述', type: 'textarea', rows: 2, placeholder: '请输入网站描述' },
  ],
];

const fetchSettings = async () => {
  try {
    const response = await officialwebsiteApi.getSettings();
    if (response.code === 200) {
      settingsData.value = response.data || null;
      if (settingsData.value) {
        formData.value = {
          site_name: settingsData.value.site_name || '',
          site_slogan: settingsData.value.site_slogan || '',
          language: settingsData.value.language || '',
          site_description: settingsData.value.site_description || '',
          default_currency: settingsData.value.default_currency || '',
        };
      }
    } else {
      console.error('获取设置数据失败:', response.msg || response.message || '未知错误');
      settingsData.value = null;
    }
  } catch (error) {
    console.error('获取设置数据失败:', error);
    settingsData.value = null;
  }
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    site_name: item.site_name || '',
    site_slogan: item.site_slogan || '',
    language: item.language || '',
    site_description: item.site_description || '',
    default_currency: item.default_currency || '',
  };
  showDialog.value = true;
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
  if (!data.site_name) {
    return '请填写网站名称';
  }
  return null;
};

const handleSubmit = async (data) => {
  showToastMessage('更新设置成功');
  await fetchSettings();
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

onMounted(fetchSettings);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
