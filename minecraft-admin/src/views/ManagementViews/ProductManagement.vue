<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入产品名称或描述搜索" class="search-input-management" />

          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
        </div>

        <button class="btn add-btn" @click="showAddDialog">新增产品</button>
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
              <tr v-for="product in paginatedProducts" :key="product.id">
                <td>
                  <input type="checkbox" :checked="product.checked" @change="handleCheck(product)" class="ui-checkbox" />
                </td>
                <td>{{ product.id }}</td>
                <td>{{ product.name }}</td>
                <td>{{ product.type }}</td>
                <td>{{ product.commodity }}</td>
                <td>{{ product.city }}</td>
                <td>{{ product.province }}</td>
                <td>
                  <img :src="product.coverImage" alt="产品图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(product)" />
                </td>
                <td>{{ product.description }}</td>
                <td>￥{{ product.price }}</td>
                <td>{{ product.stock }}</td>
                <td>{{ product.rating }}</td>
                <td>{{ product.status }}</td>
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
          :total="total" />
      </div>

      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="产品"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传图片"
        recommendedSize="推荐尺寸：1200×800px"
        imageField="coverImage"
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
import { getProductList, addProduct, updateProduct, deleteProduct } from '@/api/product';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '产品ID' },
  { key: 'name', title: '产品名称' },
  { key: 'type', title: '产品类型' },
  { key: 'commodity', title: '商品' },
  { key: 'city', title: '城市' },
  { key: 'province', title: '省份' },
  { key: 'coverImage', title: '产品图片' },
  { key: 'description', title: '产品描述' },
  { key: 'price', title: '产品价格' },
  { key: 'stock', title: '库存' },
  { key: 'rating', title: '评分' },
  { key: 'status', title: '产品状态' },
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
  type: '',
  commodity: '',
  city: '',
  province: '',
  description: '',
  coverImage: '',
  price: '',
  stock: 0,
  rating: 0,
  status: 1,
  tags: '',
  createTime: '',
  updateTime: '',
  collectCount: 0,
  commentCount: 0,
  likeCount: 0,
  images: [],
});

// 表单字段配置
const formFields = [
  [
    { name: 'name', label: '产品名称', type: 'text', required: true, placeholder: '请输入产品名称' },
    { name: 'type', label: '产品类型', type: 'text', required: true, placeholder: '请输入产品类型' },
    { name: 'commodity', label: '商品', type: 'text', required: true, placeholder: '请输入商品' },
    { name: 'city', label: '城市', type: 'text', required: true, placeholder: '请输入城市' },
  ],
  [
    { name: 'province', label: '省份', type: 'text', required: true, placeholder: '请输入省份' },
    { name: 'description', label: '产品描述', type: 'textarea', required: true, rows: 3, placeholder: '请输入产品描述' },
  ],
  [
    { name: 'price', label: '产品价格', type: 'text', required: true, placeholder: '请输入产品价格' },
    { name: 'stock', label: '库存', type: 'number', required: true, placeholder: '请输入库存' },
    { name: 'rating', label: '评分', type: 'number', required: true, placeholder: '请输入评分' },
    { name: 'status', label: '状态', type: 'number', placeholder: '请输入状态' },
  ],
  [
    { name: 'tags', label: '标签', type: 'text', placeholder: '请输入标签' },
  ],
];

// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页处理函数
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1; // 切换每页显示数量时重置到第一页
  fetchProducts();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchProducts();
};
//方法定义
const formatDate = (date) => {
  if (!date) return '未记录';
  return new Date(date).toLocaleString('zh-CN');
};
// 获取产品数据
const fetchProducts = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
    };
    const response = await getProductList(params);
    // 检查响应格式
    if (response.code === "0" || response.code === 200) {
      products.value = response.data?.records || response.data?.list || [];
      total.value = response.data?.total || 0;
    } else {
      console.error('获取产品数据失败:', response.msg || response.message || '未知错误');
      products.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('请求失败:', error);
    products.value = [];
    total.value = 0;
  }
};

// 搜索按钮点击事件
const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
  fetchProducts();
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    name: '',
    type: '',
    commodity: '',
    city: '',
    province: '',
    description: '',
    coverImage: '',
    price: '',
    stock: 0,
    rating: 0,
    status: 1,
    tags: '',
    createTime: '',
    updateTime: '',
    collectCount: 0,
    commentCount: 0,
    likeCount: 0,
    images: [],
  };
  // 重置图片上传相关状态
  previewImage.value = null;
  fileName.value = '';
  fileSize.value = '';
  progress.value = 0;
  uploading.value = false;
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (product) => {
  isEditing.value = true;
  formData.value = {
    id: product.id,
    name: product.name,
    type: product.type || '',
    commodity: product.commodity || '',
    city: product.city || '',
    province: product.province || '',
    description: product.description || '',
    coverImage: product.coverImage || '',
    price: product.price || 0,
    stock: product.stock || 0,
    rating: product.rating || 0,
    status: product.status || 1,
    tags: product.tags || '',
    createTime: product.createTime || '',
    updateTime: product.updateTime || '',
    collectCount: product.collectCount || 0,
    commentCount: product.commentCount || 0,
    likeCount: product.likeCount || 0,
    images: product.images || [],
  };
  showDialog.value = true;
};
// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.name) {
    return '请输入产品名称';
  }
  if (!data.type) {
    return '请输入产品类型';
  }
  if (!data.city) {
    return '请输入城市';
  }
  if (!data.province) {
    return '请输入省份';
  }
  if (!data.description) {
    return '请输入产品描述';
  }
  if (!data.price) {
    return '请输入产品价格';
  }
  if (!data.stock && data.stock !== 0) {
    return '请输入库存';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  if (isEdit) {
    await updateProduct(data);
    showToastMessage('更新产品成功');
  } else {
    await addProduct(data);
    showToastMessage('新增产品成功');
  }
  await fetchProducts();
};

// 处理错误
const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
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
// 删除产品
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

const confirmDelete = async () => {
  if (deleteProductId.value) {
    try {
      await deleteProduct(deleteProductId.value);
      await fetchProducts();
      showToastMessage('删除产品成功');

    } catch (error) {
      console.error('删除失败:', error.response?.data || error.message);
      showToastMessage('删除产品失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 处理复选框选择
const handleCheck = (product) => {
  product.checked = !product.checked;
};

// 批量删除
const handleReset = async () => {
  const selectedProducts = products.value.filter(product => product.checked);
  if (selectedProducts.length === 0) {
    showToastMessage('请选择要删除的产品', 'error');
    return;
  }
  
  try {
    // 这里可以实现批量删除的逻辑
    for (const product of selectedProducts) {
      await deleteProduct(product.id);
    }
    await fetchProducts();
    showToastMessage('批量删除成功');
  } catch (error) {
    console.error('批量删除失败:', error);
    showToastMessage('批量删除失败', 'error');
  }
};

// 前端分页逻辑（如果后端不支持分页）
const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return (products.value || []).slice(start, end);
});

onMounted(fetchProducts);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>