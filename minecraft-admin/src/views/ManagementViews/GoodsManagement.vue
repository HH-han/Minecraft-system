<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入订单ID或商品编号搜索" class="search-input-management" />

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
              <tr v-for="order in filteredOrders" :key="order.id">
                <td>
                  <input type="checkbox" :checked="order.checked" @change="handleCheck(order)" class="ui-checkbox" />
                </td>
                <td>{{ order.id }}</td>
                <td>
                  <img :src="order.image" alt="商品图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(order)">
                </td>
                <td>￥{{ order.price }}</td>
                <td>{{ order.totalAmount }}</td>
                <td>{{ order.status }}</td>
                <td>{{ formatDate(order.orderDate) }}</td>
                <td>{{ formatDate(order.paymentDate) }}</td>
                <td>{{ order.paymentMethod }}</td>
                <td>{{ order.sku }}</td>
                <td>{{ order.description ? order.description.substring(0, 10) : '' }}...</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showEditDialog(card)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(order)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(order.id)">删除</button>
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
          :total="total" />
      </div>

      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="订单"
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

// 数据定义
const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '订单ID' },
  { key: 'image', title: '商品图片' },
  { key: 'price', title: '商品价格' },
  { key: 'totalAmount', title: '订单总金额' },
  { key: 'status', title: '订单状态' },
  { key: 'orderDate', title: '下单时间' },
  { key: 'paymentDate', title: '支付时间' },
  { key: 'paymentMethod', title: '支付方式' },
  { key: 'sku', title: '商品编号' },
  { key: 'description', title: '描述' },
];

const orders = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: null,
  image: '',
  price: '',
  totalAmount: '',
  status: '',
  orderDate: '',
  paymentDate: '',
  paymentMethod: '',
  sku: '',
});

// 表单字段配置
const formFields = [
  [
    { name: 'price', label: '商品价格', type: 'text', required: true, placeholder: '请输入商品价格' },
    { name: 'totalAmount', label: '订单总金额', type: 'text', required: true, placeholder: '请输入订单总金额' },
    { name: 'status', label: '订单状态', type: 'select', required: true, placeholder: '请选择订单状态', options: [
      { value: 'PENDING', label: 'PENDING' },
      { value: 'PAID', label: 'PAID' },
      { value: 'SHIPPED', label: 'SHIPPED' },
      { value: 'COMPLETED', label: 'COMPLETED' },
    ]},
    { name: 'paymentMethod', label: '支付方式', type: 'select', required: true, placeholder: '请选择支付方式', options: [
      { value: 'BANK', label: '银行转账' },
      { value: 'PAYPAL', label: 'PayPal' },
      { value: 'WECHAT', label: '微信支付' },
    ]},
  ],
  [
    { name: 'sku', label: '商品编号', type: 'text', required: true, placeholder: '请输入商品编号' },
  ],
];

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 提示消息
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');

// 删除确认
const isDeletePromptVisible = ref(false);
const deleteOrderId = ref(null);

//方法定义
const formatDate = (date) => {
  if (!date) return '未记录';
  return new Date(date).toLocaleString('zh-CN');
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
};

const formatDateTime = (dateString) => {
  if (!dateString) return '未记录';
  return new Date(dateString).toLocaleString('zh-CN');
};

const fetchOrders = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
    };
    const response = await request.get('/api/public/order', { params });
    orders.value = response.data.list;
    total.value = response.data.total;
  } catch (error) {
    showToastMessage('获取订单失败', 'error');
  }
};

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.price) {
    return '请输入商品价格';
  }
  if (!data.totalAmount) {
    return '请输入订单总金额';
  }
  if (!data.status) {
    return '请选择订单状态';
  }
  if (!data.paymentMethod) {
    return '请选择支付方式';
  }
  if (!data.sku) {
    return '请输入商品编号';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  const payload = {
    ...data,
    orderDate: isEdit ? data.orderDate : new Date().toISOString(),
  };
  if (isEdit) {
    await request.put(`/api/public/order/${data.id}`, payload);
    showToastMessage('更新订单成功');
  } else {
    await request.post('/api/public/order', payload);
    showToastMessage('新增订单成功');
  }
  await fetchOrders();
};

// 处理错误
const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    image: '',
    price: '',
    totalAmount: '',
    status: '',
    orderDate: '',
    paymentDate: '',
    paymentMethod: '',
    sku: '',
  };
  showDialog.value = true;
};

const showEditDialog = (order) => {
  isEditing.value = true;
  formData.value = { ...order };
  showDialog.value = true;
};

const handleDelete = (id) => {
  deleteOrderId.value = id;
  isDeletePromptVisible.value = true;
};

const confirmDelete = async () => {
  try {
    await request.delete(`/api/public/order/${deleteOrderId.value}`);
    showToastMessage('删除成功');
    fetchOrders();
  } catch (error) {
    showToastMessage('删除失败', 'error');
  } finally {
    closeDeletePrompt();
  }
};

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => (showToast.value = false), 3000);
};

// 初始化
onMounted(fetchOrders);

const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchOrders();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchOrders();
};

const handleSearch = fetchOrders;

const filteredOrders = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return keyword
    ? orders.value.filter(
      (order) =>
        String(order.id).includes(keyword) || order.sku.toLowerCase().includes(keyword)
    )
    : orders.value;
});
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>