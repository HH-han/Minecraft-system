<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入功能标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增功能</button>
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
              <tr v-for="item in filteredFeatures" :key="item.id">
                <td>
                  <input type="checkbox" :checked="item.checked" @change="handleCheck(item)" class="ui-checkbox" />
                </td>
                <td>{{ item.id }}</td>
                <td>
                  <img v-if="item.icon" :src="item.icon.replace(/[`\s]/g, '')" alt="图标"
                    style="width: 35px; height: 35px; object-fit: contain; cursor: pointer;" @click="triggerFileInput(item)" />
                  <span v-else style="color: #86868b;">未设置</span>
                </td>
                <td>{{ item.title }}</td>
                <td>{{ item.description ? item.description.substring(0, 20) : '未设置' }}</td>
                <td>
                  <span class="color-block" :style="{ background: item.bgColor }"></span>
                </td>
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
        title="功能"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传功能图标"
        recommendedSize="推荐尺寸：64×64px（SVG/PNG）"
        imageField="icon"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <!-- 功能详情 -->
      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>功能详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedItem?.description }}</span>
            </div>
            <div class="detail-item">
              <label>背景色:</label>
              <span>{{ selectedItem?.bgColor }}</span>
            </div>
            <div class="detail-item">
              <label>排序:</label>
              <span>{{ selectedItem?.sortOrder }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatDate(selectedItem?.createdAt) }}</span>
            </div>
            <div class="detail-item" v-if="selectedItem?.icon">
              <label>图标:</label>
              <img :src="selectedItem.icon.replace(/[`\s]/g, '')" alt="功能图标" style="max-width: 80px; max-height: 80px;" />
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
          <h2>子项管理 - {{ currentItem?.title }}</h2>
          <div class="items-action-bar">
            <button class="btn add-btn" @click="showAddItemDialog">新增子项</button>
          </div>
          <div class="data-table-wrapper items-table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>子项名称</th>
                  <th>排序</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in featureItems" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td>{{ item.itemName }}</td>
                  <td>{{ item.sortOrder }}</td>
                  <td class="table-btn-display">
                    <button class="btn edit-btn" @click="showEditItemDialog(item)">编辑</button>
                    <button class="btn delete-btn" @click="handleDeleteItem(item.id)">删除</button>
                  </td>
                </tr>
                <tr v-if="featureItems.length === 0">
                  <td colspan="4" style="text-align: center; color: #86868b; padding: 24px;">暂无子项</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn cancel-btn" @click="closeItemsDialog">关闭</button>
          </div>
        </div>
      </div>

      <!-- 子项新增/编辑弹窗 -->
      <div v-if="showItemForm" class="dialog-overlay" @click.self="closeItemForm">
        <div class="dialog" @click.stop>
          <h2>{{ isEditingItem ? '编辑子项' : '新增子项' }}</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>子项名称:</label>
              <input type="text" v-model="itemFormData.itemName" placeholder="请输入子项名称" class="search-input-management" style="flex: 1;" />
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
  { key: 'icon', title: '图标' },
  { key: 'title', title: '标题' },
  { key: 'description', title: '描述' },
  { key: 'bgColor', title: '背景色' },
  { key: 'sortOrder', title: '排序' },
  { key: 'itemCount', title: '子项数' },
  { key: 'createdAt', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const features = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedItem = ref(null);

const formData = ref({
  id: null,
  icon: '',
  title: '',
  description: '',
  bgColor: '',
  sortOrder: 0,
});

const formFields = [
  [
    { name: 'title', label: '标题', type: 'text', required: true, placeholder: '请输入功能标题' },
    { name: 'sortOrder', label: '排序', type: 'number', min: 0, placeholder: '排序序号' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', rows: 2, placeholder: '请输入功能描述' },
  ],
  [
    { name: 'bgColor', label: '背景渐变色', type: 'text', placeholder: '如：linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)' },
  ],
];

// 子项管理状态
const showItems = ref(false);
const currentItem = ref(null);
const featureItems = ref([]);
const showItemForm = ref(false);
const isEditingItem = ref(false);
const itemFormData = ref({ id: null, featureId: null, itemName: '', sortOrder: 0 });

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredFeatures = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (features.value || []).filter(
    (item) =>
      String(item.id).includes(keyword) ||
      (item.title && item.title.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchFeatures();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchFeatures();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchFeatures();
};

const handleCheck = (item) => {
  item.checked = !item.checked;
};

const handleBatchDelete = () => {
  const selectedItems = features.value.filter(item => item.checked);
  if (selectedItems.length === 0) {
    showToastMessage('请选择要删除的功能', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedItems.length} 个功能，请逐个删除`, 'info');
};

const fetchFeatures = async () => {
  try {
    const response = await websiteIntroductionApi.getFeatureList();
    if (response.code === 200) {
      const data = response.data || [];
      features.value = data.map(item => ({
        ...item,
        itemCount: item.items ? item.items.length : 0,
        checked: false
      }));
      total.value = data.length;
    } else {
      console.error('获取功能数据失败:', response.msg || response.message || '未知错误');
      features.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取功能数据失败:', error);
    features.value = [];
    total.value = 0;
  }
};

const triggerFileInput = (item) => {
  if (item.icon) {
    window.open(item.icon, '_blank');
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = { id: null, icon: '', title: '', description: '', bgColor: '', sortOrder: 0 };
  showDialog.value = true;
};

const showEditDialog = (item) => {
  isEditing.value = true;
  formData.value = {
    id: item.id,
    icon: item.icon,
    title: item.title,
    description: item.description,
    bgColor: item.bgColor,
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
  if (!data.title) {
    return '请填写功能标题';
  }
  return null;
};

const handleSubmit = async (data) => {
  try {
    await websiteIntroductionApi.saveFeature(data);
    showToastMessage(isEditing.value ? '更新功能成功' : '新增功能成功');
    showDialog.value = false;
    await fetchFeatures();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

// ============ 子项管理 ============
const showItemsDialog = async (item) => {
  currentItem.value = item;
  showItems.value = true;
  await fetchFeatureItems(item.id);
};

const closeItemsDialog = () => {
  showItems.value = false;
  currentItem.value = null;
  featureItems.value = [];
};

const fetchFeatureItems = async (featureId) => {
  try {
    const response = await websiteIntroductionApi.getFeatureItems(featureId);
    if (response.code === 200) {
      featureItems.value = response.data || [];
    } else {
      featureItems.value = [];
    }
  } catch (error) {
    console.error('获取子项失败:', error);
    featureItems.value = [];
  }
};

const showAddItemDialog = () => {
  isEditingItem.value = false;
  itemFormData.value = { id: null, featureId: currentItem.value.id, itemName: '', sortOrder: 0 };
  showItemForm.value = true;
};

const showEditItemDialog = (item) => {
  isEditingItem.value = true;
  itemFormData.value = { id: item.id, featureId: currentItem.value.id, itemName: item.itemName, sortOrder: item.sortOrder };
  showItemForm.value = true;
};

const closeItemForm = () => {
  showItemForm.value = false;
};

const handleItemSubmit = async () => {
  if (!itemFormData.value.itemName) {
    showToastMessage('请填写子项名称', 'warning');
    return;
  }
  try {
    await websiteIntroductionApi.saveFeatureItem(itemFormData.value);
    showToastMessage(isEditingItem.value ? '更新子项成功' : '新增子项成功');
    showItemForm.value = false;
    await fetchFeatureItems(currentItem.value.id);
    // 同步刷新父表的子项计数
    await fetchFeatures();
  } catch (error) {
    showToastMessage('保存失败', 'error');
  }
};

const handleDeleteItem = async (id) => {
  try {
    await websiteIntroductionApi.deleteFeatureItem(id);
    showToastMessage('删除子项成功');
    await fetchFeatureItems(currentItem.value.id);
    await fetchFeatures();
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
      await websiteIntroductionApi.deleteFeature(deleteId.value);
      showToastMessage('删除功能成功');
      await fetchFeatures();
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除功能失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(fetchFeatures);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.color-block {
  display: inline-block;
  width: 40px;
  height: 22px;
  border-radius: 6px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  vertical-align: middle;
}

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
