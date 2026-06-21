<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入卡片ID或标题搜索" class="search-input-management" />

          </div>
          <button class="btn search-btn" @click="applySearchFilter">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>

        <button class="btn add-btn" @click="showAddDialog">新增安全知识提示</button>
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
              <tr v-for="tip in safetyTips" :key="tip.id">
                <td>
                  <input type="checkbox" :checked="tip.checked" @change="handleCheck(tip)" class="ui-checkbox" />
                </td>
                <td>{{ tip.id }}</td>
                <td>{{ tip.title }}</td>
                <td>
                  <img :src="tip.imageUrl" alt="安全图片" style="width: 35px; height: 35px;" />
                </td>
                <td>{{ tip.description }}</td>
                <td>{{ tip.categoryId }}</td>
                <td>{{ tip.sortWeight }}</td>
                <td>{{ tip.status === 1 ? '启用' : '禁用' }}</td>
                <td>{{ formatDate(tip.createTime) }}</td>
                <td>{{ formatDate(tip.updateTime) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showEditDialog(tip)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(tip)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(tip.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <!-- 分页器 -->
      <div class="block">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
      <FormDialog
        v-model:visible="showDialog"
        title="安全知识提示"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传图片"
        recommendedSize="推荐尺寸：1200×800px"
        imageField="imageUrl"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <!-- 删除提示框组件 -->
      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <!-- 自定义提示框组件 -->
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>

</template>

<script setup>
import { ref, onMounted } from 'vue';
import safetyTipsApi from '@/api/safetytips';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '安全ID' },
  { key: 'title', title: '安全标题' },
  { key: 'imageUrl', title: '安全图片' },
  { key: 'description', title: '安全描述' },
  { key: 'categoryId', title: '分类ID' },
  { key: 'sortWeight', title: '排序权重' },
  { key: 'status', title: '状态' },
  { key: 'createTime', title: '创建时间' },
  { key: 'updateTime', title: '更新时间' },
];

const safetyTips = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: null,
  title: '',
  description: '',
  imageUrl: '',
  categoryId: null,
  sortWeight: 0,
  status: 1,
  createTime: '',
  updateTime: '',
});

// 表单字段配置
const formFields = [
  [
    { name: 'title', label: '安全标题', type: 'text', required: true, placeholder: '请输入安全标题' },
    { name: 'description', label: '安全描述', type: 'text', required: true, placeholder: '请输入安全描述' },
    { name: 'categoryId', label: '分类ID', type: 'number', min: 1, placeholder: '请输入分类ID' },
  ],
  [
    { name: 'sortWeight', label: '排序权重', type: 'number', min: 0, max: 999, placeholder: '0-999' },
    { name: 'status', label: '状态', type: 'select', options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }] },
  ],
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
  return new Date(date).toLocaleString('zh-CN', options);
};

// 原始安全知识提示数据
const originalSafetyTips = ref([]);

const fetchSafetyTips = async () => {
  try {
    const response = await safetyTipsApi.getActiveSafetyTips();
    originalSafetyTips.value = response.data || [];
    // 应用搜索过滤
    applySearchFilter();
  } catch (error) {
    console.error('获取数据失败:', error);
    safetyTips.value = [];
    total.value = 0;
  }
};

// 应用搜索过滤
const applySearchFilter = () => {
  const keyword = searchKeyword.value.toLowerCase();
  if (!keyword) {
    safetyTips.value = [...originalSafetyTips.value];
  } else {
    safetyTips.value = originalSafetyTips.value.filter(tip => {
      return String(tip.id).includes(keyword) ||
             (tip.title && tip.title.toLowerCase().includes(keyword));
    });
  }
  total.value = safetyTips.value.length;
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    title: '',
    description: '',
    imageUrl: '',
    categoryId: null,
    sortWeight: 0,
    status: 1,
    createTime: '',
    updateTime: '',
  };
  showDialog.value = true;
};

const showEditDialog = (tip) => {
  isEditing.value = true;
  formData.value = { ...tip };
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
// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.title || !data.description) {
    return '请填写所有必填字段';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  if (isEdit) {
    await safetyTipsApi.updateSafetyTip(data);
    showToastMessage('更新成功');
  } else {
    await safetyTipsApi.addSafetyTip(data);
    showToastMessage('新增成功');
  }
  await fetchSafetyTips();
};

// 处理错误
const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

const handleDelete = (id) => {
  deleteTipId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteTipId.value = null;
};

const confirmDelete = async () => {
  try {
    await safetyTipsApi.deleteSafetyTip(deleteTipId.value);
    await fetchSafetyTips();
    showToastMessage('删除成功');
    toastType.value = 'success';
    showToast.value = true;
  } catch (error) {
    showToastMessage('删除失败');
    toastType.value = 'error';
    showToast.value = true;
    console.error('删除失败:', error);
  } finally {
    closeDeletePrompt();
  }
};

// 处理复选框点击
const handleCheck = (tip) => {
  tip.checked = !tip.checked;
};

// 处理批量删除
const handleBatchDelete = async () => {
  const selectedTips = safetyTips.value.filter(tip => tip.checked);
  if (selectedTips.length === 0) {
    showToastMessage('请选择要删除的安全知识提示', 'error');
    return;
  }
  
  try {
    for (const tip of selectedTips) {
      await safetyTipsApi.deleteSafetyTip(tip.id);
    }
    await fetchSafetyTips();
    showToastMessage('批量删除成功');
  } catch (error) {
    showToastMessage('批量删除失败', 'error');
    console.error('批量删除失败:', error);
  }
};

const closeDialog = () => {
  showDialog.value = false;
};

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  fetchSafetyTips();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchSafetyTips();
};

const isDeletePromptVisible = ref(false);
const deleteTipId = ref(null);

onMounted(fetchSafetyTips);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>