<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 加载状态 -->
      <div v-if="loading">加载中...</div>
      <div v-else>
        <!-- 操作栏 -->
        <div class="action-bar">
          <div class="search-bar">
            <div class="search-box-management">
              <input type="text" v-model="searchKeyword" placeholder="输入名称或类别搜索" class="search-input-management" />
            </div>
            <button class="btn search-btn" @click="handleSearch">搜索</button>
            <button class="btn delete-btn" @click="handleReset">批量删除</button>
          </div>
          <button class="btn add-btn" @click="showAddDialog">新增项目</button>
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
              <tbody v-if="filteredItems.length > 0">
                <!-- 确保 filteredItems 有数据时才渲染 -->
                <tr v-for="item in filteredItems" :key="item.id">
                  <td>
                    <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                  </td>
                  <td>{{ item.id }}</td>
                  <td>{{ item.name }}</td>
                  <td>{{ item.chineseName }}</td>
                  <td>
                    <img :src="item.image" alt="图片" style="width: 35px; height: 35px;" />
                  </td>
                  <td>{{ item.continentId }}</td>
                  <td>{{ item.capital }}</td>
                  <td>{{ item.area }}</td>
                  <td>{{ item.population }}</td>
                  <td>{{ item.currency }}</td>
                  <td>{{ item.language }}</td>
                  <td>{{ item.timezone }}</td>
                  <td>{{ item.countryCode }}</td>
                  <td>{{ item.phoneCode }}</td>
                  <td>{{ item.flagEmoji }}</td>
                  <td>{{ item.createdAt }}</td>
                  <td class="table-btn-display">
                    <button class="btn details-btn" @click="showEditDialog(item)">详情</button>
                    <button class="btn edit-btn" @click="showEditDialog(item)">编辑</button>
                    <button class="btn delete-btn" @click="handleDelete(item.id)">删除</button>
                  </td>
                </tr>
                <!-- 当 filteredItems 为空时显示提示信息 -->
                <tr v-if="filteredItems.length === 0">
                  <td colspan="17">未找到相关数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <!-- 分页器 -->
        <div class="block">
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="total" />
        </div>

      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="目的地"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传图片"
        recommendedSize="推荐尺寸：1200×800px"
        imageField="image"
        :submitFn="handleSubmit"
        @error="handleDialogError"
      />

      <!-- 删除提示框组件 -->
        <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />

        <!-- 自定义提示框组件 -->
        <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
      </div>
    </div>
  </div>

</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import countriesApi from '@/api/countries';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

// 表格列定义
const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: 'ID' },
  { key: 'name', title: '国家名称' },
  { key: 'chineseName', title: '中文名称' },
  { key: 'image', title: '图片' },
  { key: 'continentId', title: '大洲ID' },
  { key: 'capital', title: '首都' },
  { key: 'area', title: '面积' },
  { key: 'population', title: '人口' },
  { key: 'currency', title: '货币' },
  { key: 'language', title: '官方语言' },
  { key: 'timezone', title: '时区' },
  { key: 'countryCode', title: '国家代码' },
  { key: 'phoneCode', title: '电话区号' },
  { key: 'flagEmoji', title: '国旗' },
  { key: 'createdAt', title: '创建时间' }
];

// 表单字段配置
const formFields = [
  [
    { name: 'name', label: '名称', type: 'text', required: true, placeholder: '请输入国家名称' },
    { name: 'chineseName', label: '中文名称', type: 'text', required: true, placeholder: '请输入中文名称' },
    { name: 'continentId', label: '大洲ID', type: 'number', required: true, placeholder: '请输入大洲ID' },
    { name: 'capital', label: '首都', type: 'text', placeholder: '请输入首都' },
  ],
  [
    { name: 'area', label: '面积', type: 'number', step: 0.01, placeholder: '请输入面积' },
    { name: 'population', label: '人口', type: 'number', placeholder: '请输入人口' },
    { name: 'currency', label: '货币', type: 'text', placeholder: '请输入货币' },
    { name: 'language', label: '官方语言', type: 'text', placeholder: '请输入官方语言' },
  ],
  [
    { name: 'timezone', label: '时区', type: 'text', placeholder: '请输入时区' },
    { name: 'countryCode', label: '国家代码', type: 'text', placeholder: '请输入国家代码' },
    { name: 'phoneCode', label: '电话区号', type: 'text', placeholder: '请输入电话区号' },
    { name: 'flagEmoji', label: '国旗emoji', type: 'text', placeholder: '请输入国旗emoji' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', fullWidth: true, rows: 3, placeholder: '最多 255 字' },
  ],
];

// 数据相关
const items = ref([]);
const loading = ref(true);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: null,
  name: '',
  chineseName: '',
  image: '',
  continentId: 0,
  capital: '',
  area: 0,
  population: 0,
  currency: '',
  language: '',
  timezone: '',
  countryCode: '',
  phoneCode: '',
  flagEmoji: '',
  description: '',
});
// 提示框相关
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 删除提示框相关
const isDeletePromptVisible = ref(false);
const deleteItemId = ref(null);

// 格式化城市显示
const formatCities = (cities) => {
  return (cities || []).join(', ');
};

// 搜索功能
const filteredItems = computed(() => {
  console.log('items.value:', items.value);
  if (!items.value) return [];
  const keyword = searchKeyword.value.toLowerCase();
  return (items.value || []).filter(
    (item) =>
      (item.name && item.name.toLowerCase().includes(keyword)) ||
      (item.chineseName && item.chineseName.toLowerCase().includes(keyword))
  );
});

// 获取数据
const fetchItems = async () => {
  loading.value = true;
  try {
    const response = await countriesApi.getCountriesList(currentPage.value, pageSize.value);
    console.log('后端返回的数据:', response);

    // 处理完整的响应格式，数据在response.data.data.records中
    let countriesData = [];
    let totalCount = 0;

    if (response.data && response.data.data) {
      // 完整的axios响应格式
      countriesData = response.data.data.records || [];
      totalCount = response.data.data.total || 0;
    } else if (response.data) {
      // 简化的格式
      countriesData = response.data.records || response.data.list || [];
      totalCount = response.data.total || 0;
    } else {
      // 其他格式
      countriesData = response.records || response.list || [];
      totalCount = response.total || 0;
    }

    // 处理数据，确保每个国家都有 checked 属性
    items.value = countriesData.map(item => ({
      ...item,
      checked: false // 添加复选框状态
    }));

    // 更新分页参数
    total.value = totalCount;
    console.log('处理后的数据:', countriesData);
    console.log('更新后的items:', items.value);
    console.log('更新后的total:', total.value);
  } catch (error) {
    console.error('获取数据失败:', error);
    items.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    name: '',
    chineseName: '',
    image: '',
    continentId: 0,
    capital: '',
    area: 0,
    population: 0,
    currency: '',
    language: '',
    timezone: '',
    countryCode: '',
    phoneCode: '',
    flagEmoji: '',
    description: '',
  };
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    name: item.name || '',
    chineseName: item.chineseName || '',
    image: item.image || '',
    continentId: item.continentId || 0,
    capital: item.capital || '',
    area: item.area || 0,
    population: item.population || 0,
    currency: item.currency || '',
    language: item.language || '',
    timezone: item.timezone || '',
    countryCode: item.countryCode || '',
    phoneCode: item.phoneCode || '',
    flagEmoji: item.flagEmoji || '',
    description: item.description || '',
  };
  showDialog.value = true;
};

// 提交表单 (供 FormDialog 调用)
const handleSubmit = async (data, isEdit) => {
  try {
    if (isEdit) {
      await countriesApi.updateCountries(data);
      showToastMessage('更新国家成功');
    } else {
      await countriesApi.addCountries(data);
      showToastMessage('新增国家成功');
    }
    await fetchItems();
  } catch (error) {
    const message = isEdit ? '更新国家失败' : '新增国家失败';
    throw new Error(message);
  }
};

// 处理弹窗错误
const handleDialogError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

// 删除项目
const handleDelete = (id) => {
  deleteItemId.value = id;
  isDeletePromptVisible.value = true;
};

const confirmDelete = async () => {
  if (deleteItemId.value) {
    try {
      await countriesApi.deleteCountries(deleteItemId.value);
      await fetchItems();
      showToastMessage('删除国家成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除国家失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 关闭删除提示框
const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteItemId.value = null;
};

// 显示提示消息
const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// 分页处理
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  fetchItems(); // 移除 currentPage.value = 1 保持当前页码
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchItems();
};

// 处理复选框点击
const handleCheck = (item) => {
  item.checked = !item.checked;
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
  fetchItems();
};

// 处理批量删除
const handleReset = () => {
  const selectedItems = items.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的国家', 'error');
    return;
  }

  // 这里可以实现批量删除逻辑
  showToastMessage(`已选择 ${selectedItems.length} 个国家进行批量删除`, 'success');
};

// 初始化加载数据
onMounted(fetchItems);

// 确保数据正确渲染
const forceUpdate = () => {
  // 重新赋值，触发响应式更新
  items.value = [...items.value];
};


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>