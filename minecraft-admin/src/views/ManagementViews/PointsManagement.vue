<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入商品名称或ID搜索" class="search-input-management" />

          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
        </div>

        <button class="btn add-btn" @click="showAddDialog">新增商品</button>
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
              <tr v-for="product in filteredProducts" :key="product.id">
                <td>
                  <input type="checkbox" :checked="product.checked" @change="handleCheck(product)" class="ui-checkbox" />
                </td>
                <td>{{ product.id }}</td>
                <td>{{ product.name }}</td>
                <td>{{ product.description }}</td>
                <td>{{ product.pointsPrice }}</td>
                <td>{{ product.stock }}</td>
                <td>{{ product.status === 1 ? '上架' : '下架' }}</td>
                <td>{{ formatDate(product.createTime) }}</td>
                <td>{{ formatDate(product.updateTime) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showEditDialog(product)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(product)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(product.id)">删除</button>
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
      <!-- 新增/编辑弹窗 -->
      <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
        <div class="dialog" @click.stop>
          <h2>{{ isEditing ? '编辑商品' : '新增商品' }}</h2>
          <form @submit.prevent="submitForm" class="form-container">
            <div class="form-row">
              <div class="form-group">
                <label>商品名称:</label>
                <input v-model="formData.name" required />
              </div>
              <div class="form-group">
                <label>商品描述:</label>
                <input v-model="formData.description" required />
              </div>
              <div class="form-group">
                <label>积分价格:</label>
                <input v-model="formData.pointsPrice" type="number" required />
              </div>
              <div class="form-group">
                <label>库存:</label>
                <input v-model="formData.stock" type="number" required />
              </div>
              <div class="form-group">
                <label>图片URL:</label>
                <input v-model="formData.imageUrl" />
              </div>
              <div class="form-group">
                <label>状态:</label>
                <select v-model="formData.status">
                  <option value="1">上架</option>
                  <option value="0">下架</option>
                </select>
              </div>
            </div>
            <!-- 创建修改时间 -->
            <div class="dialog-buttons">
              <button type="button" class="btn cancel-btn" @click="closeDialog">取消</button>
              <button type="submit" class="btn confirm-btn">{{ isEditing ? '保存' : '创建' }}</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 删除提示框组件 -->
      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <!-- 自定义提示框组件 -->
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>

</template>

<script setup>

import { ref, computed, onMounted } from 'vue';
import { getProducts } from '@/api/points';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '商品ID' },
  { key: 'name', title: '商品名称' },
  { key: 'description', title: '商品描述' },
  { key: 'pointsPrice', title: '积分价格' },
  { key: 'stock', title: '库存' },
  { key: 'status', title: '状态' },
  { key: 'createTime', title: '创建时间' },
  { key: 'updateTime', title: '更新时间' },
];
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const products = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: null,
  name: '',
  description: '',
  pointsPrice: 0,
  stock: 0,
  imageUrl: '',
  status: 1,
  createTime: '',
  updateTime: '',
});

// 格式化日期显示
const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

// 搜索功能
const filteredProducts = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return Array.isArray(products.value)
    ? products.value.filter(
      (product) =>
        String(product.id).includes(keyword) ||
        product.name.toLowerCase().includes(keyword)
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
  fetchProducts();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchProducts();
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchProducts();
};

// 重置处理
const handleReset = () => {
  searchKeyword.value = '';
  currentPage.value = 1;
  fetchProducts();
};

// 多选处理
const handleCheck = (product) => {
  product.checked = !product.checked;
};

// 获取商品数据
const fetchProducts = async () => {
  try {
    const response = await getProducts();
    products.value = Array.isArray(response.data) ? response.data : [];
    total.value = products.value.length;
  } catch (error) {
    console.error('获取商品数据失败:', error);
    products.value = [];
    total.value = 0;
  }
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    name: '',
    description: '',
    pointsPrice: 0,
    stock: 0,
    imageUrl: '',
    status: 1,
    createTime: '',
    updateTime: '',
  };
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (product) => {
  isEditing.value = true;
  formData.value = { ...product };
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

// 提交表单
const submitForm = async () => {
  try {
    // 这里需要根据实际的API接口实现
    // 暂时模拟成功
    showToastMessage(isEditing.value ? '更新商品成功' : '新增商品成功');
    await fetchProducts();
    closeDialog();
  } catch (error) {
    const message = isEditing.value ? '更新商品失败' : '新增商品失败';
    showToastMessage(message, 'error');
    console.error('操作失败:', error);
  }
};

// 删除商品
const isDeletePromptVisible = ref(false);
const deleteProductId = ref(null);

const handleDelete = (id) => {
  deleteProductId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteProductId.value = null;
};

// 删除
const confirmDelete = async () => {
  if (deleteProductId.value) {
    try {
      // 这里需要根据实际的API接口实现
      // 暂时模拟成功
      showToastMessage('删除商品成功');
      await fetchProducts();
      closeDeletePrompt();
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除商品失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 关闭对话框
const closeDialog = () => {
  showDialog.value = false;
};

onMounted(fetchProducts);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>