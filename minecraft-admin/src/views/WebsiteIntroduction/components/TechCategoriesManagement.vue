<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入分类名称搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增分类</button>
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
              <tr v-for="item in filteredCategories" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.categoryName }}</td>
                <td><span class="theme-tag" :class="item.themeClass">{{ item.themeClass }}</span></td>
                <td>{{ item.sortOrder }}</td>
                <td>{{ item.itemCount || 0 }}</td>
                <td>{{ formatDate(item.createdAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(item)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(item)">编辑</button>
                  <button class="btn sub-item-btn" @click="showItemsDialog(item)">子项</button>
                  <button class="btn delete-btn" @click="handleDelete(item.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="block">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>

      <FormDialog
        v-model:visible="showDialog"
        title="技术分类"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="false"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <!-- 分类详情 -->
      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>技术分类详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>分类名称:</label>
              <span>{{ selectedItem?.categoryName }}</span>
            </div>
            <div class="detail-item">
              <label>主题样式:</label>
              <span>{{ selectedItem?.themeClass }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatDate(selectedItem?.createdAt) }}</span>
            </div>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn cancel-btn" @click="closeDetailsDialog">关闭</button>
          </div>
        </div>
      </div>

      <!-- 子项管理弹窗 -->
      <div v-if="showItems" class="dialog-overlay" @click.self="closeItemsDialog">
        <div class="dialog items-dialog" @click.stop>
          <h2>技术项管理 - {{ currentItem?.categoryName }}</h2>
          <div class="items-action-bar">
            <button class="btn add-btn" @click="showAddItemDialog">新增技术项</button>
          </div>
          <div class="data-table-wrapper items-table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>技术名称</th>
                  <th>描述</th>
                  <th>排序</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in techItems" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td>{{ item.techName }}</td>
                  <td>{{ item.description }}</td>
                  <td>{{ item.sortOrder }}</td>
                  <td class="table-btn-display">
                    <button class="btn edit-btn" @click="showEditItemDialog(item)">编辑</button>
                    <button class="btn delete-btn" @click="handleDeleteItem(item.id)">删除</button>
                  </td>
                </tr>
                <tr v-if="techItems.length === 0">
                  <td colspan="5" style="text-align: center; color: #86868b; padding: 24px;">暂无技术项</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn cancel-btn" @click="closeItemsDialog">关闭</button>
          </div>
        </div>
      </div>

      <!-- 技术项新增/编辑弹窗 -->
      <div v-if="showItemForm" class="dialog-overlay" @click.self="closeItemForm">
        <div class="dialog" @click.stop>
          <h2>{{ isEditingItem ? '编辑技术项' : '新增技术项' }}</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>技术名称:</label>
              <input type="text" v-model="itemFormData.techName" placeholder="如：Spring Boot" class="search-input-management" style="flex: 1;" />
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <input type="text" v-model="itemFormData.description" placeholder="如：应用框架" class="search-input-management" style="flex: 1;" />
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <input type="number" v-model.number="itemFormData.sortOrder" placeholder="排序序号" class="search-input-management" style="flex: 1;" />
            </div>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn cancel-btn" @click="closeItemForm">取消</button>
            <button type="button" class="btn add-btn" @click="handleItemSubmit">保存</button>
          </div>
        </div>
      </div>

      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import websiteIntroductionApi from '@/api/websiteIntroduction';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: 'ID' },
  { key: 'categoryName', title: '分类名称' },
  { key: 'themeClass', title: '主题样式' },
  { key: 'sortOrder', title: '排序' },
  { key: 'itemCount', title: '技术项数' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const categories = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  categoryName: '',
  themeClass: 'backend',
  sortOrder: 0,
});

const formFields = [
  [
    { name: 'categoryName', label: '分类名称', type: 'text', required: true, placeholder: '如：后端' },
    {
      name: 'themeClass', label: '主题样式', type: 'select', required: true,
      options: [
        { value: 'backend', label: 'backend（后端）' },
        { value: 'frontend', label: 'frontend（前端）' },
        { value: 'database', label: 'database（数据）' },
      ]
    },
  ],
  [
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
];

// 子项管理状态
const showItems = ref(false);
const currentItem = ref(null);
const techItems = ref([]);
const showItemForm = ref(false);
const isEditingItem = ref(false);
const itemFormData = ref({ id: null, categoryId: null, techName: '', description: '', sortOrder: 0 });

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredCategories = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (categories.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.categoryName && item.categoryName.toLowerCase().includes(keyword)) ||
      (item.themeClass && item.themeClass.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchCategories();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchCategories();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchCategories();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = categories.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的分类', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个分类，请逐个删除`, 'info');
};

const fetchCategories = async () => {
  try {
    const response = await websiteIntroductionApi.getTechCategoryList();
    if (response.code === 200) {
      const data = response.data || [];
      categories.value = data.map(item => ({
        ...item,
        itemCount: item.items ? item.items.length : 0,
        checked: false
      }));
      total.value = data.length;
    } else {
      categories.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取技术分类失败:', error);
    categories.value = [];
    total.value = 0;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = { id: null, categoryName: '', themeClass: 'backend', sortOrder: 0 };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    categoryName: item.categoryName,
    themeClass: item.themeClass,
    sortOrder: item.sortOrder,
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
  setTimeout(() => { showToast.value = false; }, 3000);
};

const validateForm = (data) => {
  if (!data.categoryName) {
    return '请填写分类名称';
  }
  return null;
};

const handleSubmit = async (data) => {
  try {
    await websiteIntroductionApi.saveTechCategory(data);
    showToastMessage(isEditing.value ? '更新分类成功' : '新增分类成功');
    showDialog.value = false;
    await fetchCategories();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

// ============ 技术项管理 ============
const showItemsDialog = async (item) => {
  currentItem.value = item;
  showItems.value = true;
  await fetchTechItems(item.id);
};

const closeItemsDialog = () => {
  showItems.value = false;
  currentItem.value = null;
  techItems.value = [];
};

const fetchTechItems = async (categoryId) => {
  try {
    const response = await websiteIntroductionApi.getTechItems(categoryId);
    if (response.code === 200) {
      techItems.value = response.data || [];
    } else {
      techItems.value = [];
    }
  } catch (error) {
    console.error('获取技术项失败:', error);
    techItems.value = [];
  }
};

const showAddItemDialog = () => {
  isEditingItem.value = false;
  itemFormData.value = { id: null, categoryId: currentItem.value.id, techName: '', description: '', sortOrder: 0 };
  showItemForm.value = true;
};

const showEditItemDialog = (item) => {
  isEditingItem.value = true;
  itemFormData.value = { id: item.id, categoryId: currentItem.value.id, techName: item.techName, description: item.description, sortOrder: item.sortOrder };
  showItemForm.value = true;
};

const closeItemForm = () => {
  showItemForm.value = false;
};

const handleItemSubmit = async () => {
  if (!itemFormData.value.techName) {
    showToastMessage('请填写技术名称', 'warning');
    return;
  }
  try {
    await websiteIntroductionApi.saveTechItem(itemFormData.value);
    showToastMessage(isEditingItem.value ? '更新技术项成功' : '新增技术项成功');
    showItemForm.value = false;
    await fetchTechItems(currentItem.value.id);
    await fetchCategories();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleDeleteItem = async (id) => {
  try {
    await websiteIntroductionApi.deleteTechItem(id);
    showToastMessage('删除技术项成功');
    await fetchTechItems(currentItem.value.id);
    await fetchCategories();
  } catch (error) {
    showToastMessage('删除失败', 'error');
  }
};

// ============ 删除 ============
const isDeletePromptVisible = ref(false);
const deleteId = ref(null);

const handleDelete = (id) => {
  deleteId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteId.value = null;
};

const confirmDelete = async () => {
  if (deleteId.value) {
    try {
      await websiteIntroductionApi.deleteTechCategory(deleteId.value);
      showToastMessage('删除分类成功');
      await fetchCategories();
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除分类失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchCategories);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.theme-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(0, 113, 227, 0.08);
  color: #0071e3;
}

.theme-tag.backend { background: rgba(0, 113, 227, 0.08); color: #0071e3; }
.theme-tag.frontend { background: rgba(48, 209, 88, 0.12); color: #1d8045; }
.theme-tag.database { background: rgba(255, 159, 10, 0.12); color: #b25a00; }

.sub-item-btn {
  background: rgba(0, 113, 227, 0.08);
  color: #0071e3;
}

.items-dialog {
  width: 90%;
}

.items-action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.items-table-wrapper {
  max-height: 360px;
  overflow-y: auto;
}
</style>
