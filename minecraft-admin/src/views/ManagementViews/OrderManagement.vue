<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入卡片ID或标题搜索" class="search-input-management" />

          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
        </div>

        <button class="btn add-btn" @click="showAddDialog">新增订单</button>
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
              <tr v-for="card in filteredCards" :key="card.id">
                <td>
                  <input type="checkbox" :checked="card.checked" @change="handleCheck(card)" class="ui-checkbox" />
                </td>
                <td>{{ card.id }}</td>
                <td>{{ card.username }}</td>
                <td>{{ card.itemName }}</td>
                <td>{{ card.orderId }}</td>
                <td>{{ card.amount }}</td>
                <td>{{ card.status }}</td>
                <td>{{ card.paymentMethod && card.paymentMethod.length ? card.paymentMethod : '未支付' }}</td>
                <td>{{ formatDate(card.createdAt) }}</td>
                <td>{{ formatDate(card.updatedAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showEditDialog(card)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(card)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(card.id)">删除</button>
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
      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="订单"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="false"
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

import { ref, computed, onMounted } from 'vue';
import request from '@/utils/request';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '订单ID' },
  { key: 'username', title: '用户名' },
  { key: 'itemName', title: '订单标题' },
  { key: 'orderId', title: '订单编号' },
  { key: 'amount', title: '价格' },
  { key: 'status', title: '订单状态' },
  { key: 'paymentMethod', title: '支付方式' },
  { key: 'createdAt', title: '创建时间' },
  { key: 'updatedAt', title: '更新时间' },
];
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const cards = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: null,
  username: '',
  itemName: '',
  orderId: '',
  amount: 0,
});

// 表单字段配置
const formFields = [
  [
    { name: 'username', label: '用户名', type: 'text', required: true, placeholder: '请输入用户名' },
    { name: 'itemName', label: '订单标题', type: 'text', required: true, placeholder: '请输入订单标题' },
  ],
  [
    { name: 'orderId', label: '订单编号', type: 'text', required: true, placeholder: '请输入订单编号' },
    { name: 'amount', label: '价格', type: 'number', required: true, min: 0, placeholder: '请输入价格' },
  ],
];

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.username || !data.itemName || !data.orderId) {
    return '请填写所有必填字段';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  try {
    if (isEdit) {
      await request.put(`/api/public/payment/${data.id}`, data);
    } else {
      await request.post('/api/public/payment', data);
    }
  } catch (error) {
    throw new Error(isEdit ? '更新订单失败' : '新增订单失败');
  }
};

// 格式化日期显示
const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

// 搜索功能
const filteredCards = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return Array.isArray(cards.value)
    ? cards.value.filter(
      (card) =>
        String(card.id).includes(keyword) ||
        card.title.toLowerCase().includes(keyword)
    )
    : [];
});

// 分页功能
// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页处理函数
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchScenic();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchScenic();
};
// 获取订单数据
const fetchScenic = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    };
    const response = await request.get('/api/public/payment/all', { params });
    cards.value = Array.isArray(response.data?.list) ? response.data.list : [];
    total.value = response.data?.total || 0;
  } catch (error) {
    console.error('获取订单数据失败:', error);
  }
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    username: '',
    itemName: '',
    orderId: '',
    amount: 0,
  };
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (card) => {
  isEditing.value = true;
  formData.value = { ...card };
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

// 删除卡片
const isDeletePromptVisible = ref(false);
const deleteCardId = ref(null);

const handleDelete = (id) => {
  deleteCardId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteCardId.value = null;
};
//删除
const confirmDelete = async () => {
  if (deleteCardId.value) {
    try {
      await request.delete(`/api/public/payment/${deleteCardId.value}`);
      await fetchScenic();
      closeDeletePrompt();
      showToastMessage('删除订单成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除订单失败', 'error');

    } finally {
      closeDeletePrompt();
    }
  }
};
onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>