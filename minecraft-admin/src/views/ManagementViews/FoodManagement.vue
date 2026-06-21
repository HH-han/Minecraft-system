<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入美食名称或描述搜索" class="search-input-management" />

          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
        </div>

        <button class="btn add-btn" @click="showAddDialog">新增美食</button>
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
              <tr v-for="food in paginatedFoods" :key="food.id">
                <td>
                  <input type="checkbox" :checked="food.checked" @change="handleCheck(food)" class="ui-checkbox" />
                </td>
                <td>{{ food.id }}</td>
                <td>{{ food.phone }}</td>
                <td>{{ food.name }}</td>
                <td>
                  <img :src="food.coverImage" alt="美食图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(food)" />
                </td>
                <td>{{ food.description }}</td>
                <td>{{ food.commodity }}</td>
                <td>{{ food.category }}</td>
                <td>{{ food.status }}</td>
                <td>{{ food.location }}</td>
                <td>￥{{ food.price }}</td>
                <td>{{ food.sales }}</td>
                <td>{{ food.rating }}</td>
                <td>{{ formatDate(food.created_at) }}</td>
                <td>{{ formatDate(food.updated_at) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showEditDialog(food)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(food)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(food.id)">删除</button>
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
        title="美食"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传美食图片"
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
import { getFoodList, addFood, updateFood, deleteFood } from '@/api/food';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '美食ID' },
  { key: 'phone', title: '美食电话' },
  { key: 'name', title: '美食名称' },
  { key: 'coverImage', title: '美食图片' },
  { key: 'description', title: '美食描述' },
  { key: 'commodity', title: '商品' },
  { key: 'category', title: '美食分类' },
  { key: 'status', title: '美食状态' },
  { key: 'location', title: '美食位置' },
  { key: 'price', title: '美食价格' },
  { key: 'sales', title: '销量' },
  { key: 'rating', title: '评分' },
  { key: 'created_at', title: '创建时间' },
  { key: 'updated_at', title: '更新时间' },
];

// 表单字段配置
const formFields = [
  [
    { name: 'name', label: '美食名称', type: 'text', required: true, placeholder: '请输入美食名称' },
    { name: 'description', label: '美食描述', type: 'text', required: true, placeholder: '请输入美食描述' },
    { name: 'commodity', label: '商品', type: 'text', placeholder: '请输入商品名称' },
  ],
  [
    { name: 'price', label: '美食价格', type: 'number', required: true, placeholder: '请输入美食价格', min: 0 },
    { name: 'sales', label: '销量', type: 'number', placeholder: '请输入销量', min: 0 },
    { name: 'rating', label: '评分', type: 'number', placeholder: '请输入评分', min: 0, max: 5, step: 0.1 },
  ],
  [
    { name: 'city', label: '城市', type: 'text', placeholder: '请输入城市' },
    { name: 'province', label: '省份', type: 'text', placeholder: '请输入省份' },
    { name: 'address', label: '地址', type: 'text', placeholder: '请输入详细地址' },
  ],
  [
    { name: 'cuisineType', label: '菜系类型', type: 'text', placeholder: '请输入菜系类型' },
    { name: 'status', label: '状态', type: 'number', placeholder: '请输入状态' },
    { name: 'tags', label: '标签', type: 'text', placeholder: '请输入标签，多个用逗号分隔' },
  ],
];
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const foods = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: null,
  name: '',
  description: '',
  coverImage: '',
  commodity: '',
  price: '',
  sales: 0,
  rating: 0,
  city: '',
  province: '',
  address: '',
  cuisineType: '',
  status: 1,
  tags: '',
  createTime: '',
  updateTime: '',
  collectCount: 0,
  commentCount: 0,
  likeCount: 0,
  images: [],
});

// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页处理函数
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1; // 切换每页显示数量时重置到第一页
  fetchFoods();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchFoods();
};
//方法定义
const formatDate = (date) => {
  if (!date) return '未记录';
  return new Date(date).toLocaleString('zh-CN');
};
// 获取美食数据
const fetchFoods = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
    };
    const response = await getFoodList(params);
    // 检查响应格式
    if (response.code === "0" || response.code === 200) {
      foods.value = response.data?.records || response.data?.list || [];
      total.value = response.data?.total || 0;
    } else {
      console.error('获取美食数据失败:', response.msg || response.message || '未知错误');
      foods.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('请求失败:', error);
    foods.value = [];
    total.value = 0;
  }
};

// 搜索按钮点击事件
const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
  fetchFoods();
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    name: '',
    description: '',
    commodity: '',
    coverImage: '',
    price: '',
    sales: 0,
    rating: 0,
    city: '',
    province: '',
    address: '',
    cuisineType: '',
    status: 1,
    tags: '',
    createTime: '',
    updateTime: '',
    collectCount: 0,
    commentCount: 0,
    likeCount: 0,
    images: [],
  };
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (food) => {
  isEditing.value = true;
  formData.value = {
    id: food.id,
    name: food.name,
    description: food.description,
    commodity: food.commodity || '',
    coverImage: food.image || food.coverImage,
    price: food.price,
    sales: food.sales || 0,
    rating: food.rating || 0,
    city: food.city || '',
    province: food.province || '',
    address: food.address || '',
    cuisineType: food.cuisineType || food.category || '',
    status: food.status || 1,
    tags: food.tags || '',
    createTime: food.createTime || food.created_at,
    updateTime: food.updateTime || food.updated_at,
    collectCount: food.collectCount || 0,
    commentCount: food.commentCount || 0,
    likeCount: food.likeCount || 0,
    images: food.images || [],
  };
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
  if (!data.name) {
    return '请输入美食名称';
  }
  if (!data.description) {
    return '请输入美食描述';
  }
  if (!data.price && data.price !== 0) {
    return '请输入美食价格';
  }
  return null;
};

// 处理错误
const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  try {
    if (isEdit) {
      await updateFood(data);
      showToastMessage('更新美食成功');
    } else {
      await addFood(data);
      showToastMessage('新增美食成功');
    }
    await fetchFoods();
  } catch (error) {
    console.error('操作失败:', error);
    const message = isEdit ? '更新美食失败' : '新增美食失败';
    showToastMessage(message, 'error');
    throw error;
  }
};

// 删除美食
const isDeletePromptVisible = ref(false);
const deleteFoodId = ref(null);

const handleDelete = (id) => {
  deleteFoodId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteFoodId.value = null;
};

const confirmDelete = async () => {
  if (deleteFoodId.value) {
    try {
      await deleteFood(deleteFoodId.value);
      await fetchFoods();
      showToastMessage('删除美食成功');

    } catch (error) {
      console.error('删除失败:', error.response?.data || error.message);
      showToastMessage('删除美食失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 前端分页逻辑（如果后端不支持分页）
const paginatedFoods = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return (foods.value || []).slice(start, end);
});

onMounted(fetchFoods);

// 处理复选框
const handleCheck = (food) => {
  food.checked = !food.checked;
};

// 触发文件输入框 - 点击表格图片时打开大图预览
const triggerFileInput = (food) => {
  if (food.coverImage) {
    window.open(food.coverImage, '_blank');
  }
};

</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>