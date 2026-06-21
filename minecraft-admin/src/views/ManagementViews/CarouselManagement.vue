<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入轮播标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增轮播</button>
      </div>
      <!-- 位置筛选按钮 -->
      <div class="position-filter">
        <button
          v-for="(label, value) in positionOptions"
          :key="value"
          class="position-btn"
          :class="{ active: selectedPosition === value }"
          @click="selectedPosition = value"
        >
          {{ label }}
        </button>
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
              <tr v-for="carousel in filteredCarousels" :key="carousel.id">
                <td>
                  <input type="checkbox" :checked="carousel.checked" @change="handleCheck(carousel)" class="ui-checkbox" />
                </td>
                <td>{{ carousel.id }}</td>
                <td>{{ carousel.title }}</td>
                <td>{{ carousel.subtitle }}</td>
                <td>
                  <img :src="carousel.imageUrl?.replace(/[`\s]/g, '')" alt="轮播图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(carousel)" />
                </td>
                <td>{{ getPositionName(carousel.position) }}</td>
                <td>{{ carousel.linkType }}</td>
                <td>{{ carousel.isActive ? '是' : '否' }}</td>
                <td>{{ formatDate(carousel.createdAt) }}</td>
                <td>{{ formatDate(carousel.updatedAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showEditDialog(carousel)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(carousel)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(carousel.id)">删除</button>
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
        title="轮播"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传轮播图片"
        recommendedSize="推荐尺寸：1920×1080px"
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
import { ref, computed, onMounted } from 'vue';
import carouselApi from '@/api/carousel';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '轮播ID' },
  { key: 'title', title: '轮播标题' },
  { key: 'subtitle', title: '副标题' },
  { key: 'imageUrl', title: '轮播图片' },
  { key: 'position', title: '位置' },
  { key: 'linkType', title: '链接类型' },
  { key: 'isActive', title: '是否激活' },
  { key: 'createdAt', title: '创建时间' },
  { key: 'updatedAt', title: '更新时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const carousels = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const selectedPosition = ref('all');

const formData = ref({
  id: null,
  title: '',
  subtitle: '',
  imageUrl: '',
  mobileImageUrl: '',
  thumbnailUrl: '',
  linkType: '',
  linkUrl: '',
  linkTarget: '',
  targetId: null,
  buttonText: '',
  buttonColor: '',
  textColor: '',
  textShadow: false,
  overlayColor: '',
  overlayOpacity: null,
  position: '',
  deviceType: '',
  userType: '',
  startTime: null,
  endTime: null,
  isAlwaysShow: false,
  clickCount: 0,
  impressionCount: 0,
  sortOrder: 0,
  isActive: true,
  isDeleted: false,
  remark: '',
  createdBy: '',
  createdAt: null,
  updatedAt: null,
});

// 表单字段配置
const formFields = [
  [
    { name: 'title', label: '轮播标题', type: 'text', required: true, placeholder: '请输入轮播标题' },
    { name: 'subtitle', label: '副标题', type: 'text', placeholder: '请输入副标题' },
    { name: 'position', label: '位置', type: 'select', required: true, placeholder: '请选择位置', options: [
      { value: 'home_top', label: '首页顶部' },
      { value: 'home_middle', label: '首页中部' },
      { value: 'destination', label: '目的地' },
      { value: 'attraction', label: '景点' },
      { value: 'hotel', label: '酒店' },
      { value: 'food', label: '美食' },
      { value: 'souvenir', label: '纪念品' },
      { value: 'strategy', label: '攻略群' },
      { value: 'community', label: '社区' },
    ]},
    { name: 'linkType', label: '链接类型', type: 'text', placeholder: '请输入链接类型' },
  ],
  [
    { name: 'linkUrl', label: '链接URL', type: 'text', placeholder: '请输入链接地址' },
    { name: 'linkTarget', label: '链接目标', type: 'text', placeholder: '请输入链接目标' },
    { name: 'targetId', label: '目标ID', type: 'number', placeholder: '请输入目标ID' },
    { name: 'buttonText', label: '按钮文本', type: 'text', placeholder: '请输入按钮文本' },
  ],
  [
    { name: 'buttonColor', label: '按钮颜色', type: 'text', placeholder: '如：#2997ff' },
    { name: 'textColor', label: '文本颜色', type: 'text', placeholder: '如：#ffffff' },
    { name: 'textShadow', label: '文本阴影', type: 'switch' },
    { name: 'overlayColor', label: '叠加颜色', type: 'text', placeholder: '如：#000000' },
  ],
  [
    { name: 'overlayOpacity', label: '叠加透明度', type: 'number', min: 0, max: 1, step: 0.1, placeholder: '0-1之间' },
    { name: 'deviceType', label: '设备类型', type: 'text', placeholder: '如：PC/Mobile/All' },
    { name: 'userType', label: '用户类型', type: 'text', placeholder: '如：VIP/普通用户' },
    { name: 'sortOrder', label: '排序顺序', type: 'number', min: 0, placeholder: '数字越小越靠前' },
  ],
  [
    { name: 'startTime', label: '开始时间', type: 'datetime' },
    { name: 'endTime', label: '结束时间', type: 'datetime' },
    { name: 'isAlwaysShow', label: '是否始终显示', type: 'switch' },
    { name: 'isActive', label: '是否激活', type: 'switch' },
  ],
  [
    { name: 'mobileImageUrl', label: '移动端图片', type: 'text', placeholder: '移动端图片URL' },
    { name: 'thumbnailUrl', label: '缩略图', type: 'text', placeholder: '缩略图URL' },
  ],
  [
    { name: 'remark', label: '备注', type: 'textarea', fullWidth: true, rows: 3, placeholder: '请输入备注信息' },
  ],
];

// 位置映射
const positionMap = {
  'home_top': '首页顶部',
  'home_middle': '首页中部',
  'destination': '目的地',
  'attraction': '景点',
  'hotel': '酒店',
  'food': '美食',
  'souvenir': '纪念品',
  'strategy': '攻略群',
  'community': '社区'
};

// 位置筛选选项
const positionOptions = {
  'all': '全部位置',
  'home_top': '首页顶部',
  'home_middle': '首页中部',
  'destination': '目的地',
  'attraction': '景点',
  'hotel': '酒店',
  'food': '美食',
  'souvenir': '纪念品',
  'strategy': '攻略群',
  'community': '社区'
};

// 获取中文位置名称
const getPositionName = (position) => {
  return positionMap[position] || position;
};

// 格式化日期显示
const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

// 搜索功能
const filteredCarousels = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (carousels.value || []).filter(
    (carousel) => {
      // 位置筛选
      if (selectedPosition.value !== 'all' && carousel.position !== selectedPosition.value) {
        return false;
      }
      // 关键词搜索
      return String(carousel.id).includes(keyword) ||
             (carousel.title && carousel.title.toLowerCase().includes(keyword));
    }
  );
});

// 分页功能
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页处理函数
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchCarousels();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchCarousels();
};

// 获取轮播数据
const fetchCarousels = async () => {
  try {
    const response = await carouselApi.getAllCarousels();
    carousels.value = response.data || [];
    total.value = carousels.value.length;
  } catch (error) {
    console.error('获取轮播数据失败:', error);
    carousels.value = [];
    total.value = 0;
  }
};

// 搜索按钮点击事件
const handleSearch = () => {
  currentPage.value = 1;
  fetchCarousels();
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    title: '',
    subtitle: '',
    imageUrl: '',
    mobileImageUrl: '',
    thumbnailUrl: '',
    linkType: '',
    linkUrl: '',
    linkTarget: '',
    targetId: null,
    buttonText: '',
    buttonColor: '',
    textColor: '',
    textShadow: false,
    overlayColor: '',
    overlayOpacity: null,
    position: '',
    deviceType: '',
    userType: '',
    startTime: null,
    endTime: null,
    isAlwaysShow: false,
    clickCount: 0,
    impressionCount: 0,
    sortOrder: 0,
    isActive: true,
    isDeleted: false,
    remark: '',
    createdBy: '',
  };
  showDialog.value = true;
};

// 处理日期时间格式，去除秒部分
const formatDateTimeForInput = (dateTimeString) => {
  if (!dateTimeString) return null;
  if (dateTimeString.includes(':')) {
    const parts = dateTimeString.split(':');
    if (parts.length >= 3) {
      return dateTimeString.substring(0, dateTimeString.lastIndexOf(':'));
    }
  }
  return dateTimeString;
};

// 显示编辑对话框
const showEditDialog = (carousel) => {
  isEditing.value = true;
  const formattedCarousel = { ...carousel };
  if (formattedCarousel.startTime) {
    formattedCarousel.startTime = formatDateTimeForInput(formattedCarousel.startTime);
  }
  if (formattedCarousel.endTime) {
    formattedCarousel.endTime = formatDateTimeForInput(formattedCarousel.endTime);
  }
  formData.value = formattedCarousel;
  showDialog.value = true;
};

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.title || !data.position) {
    return '请填写轮播标题和位置';
  }
  if (!isEdit && !data.imageUrl) {
    return '请上传轮播图片';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  const submitData = { ...data };
  // 处理日期时间格式
  if (submitData.startTime) {
    if (submitData.startTime.includes('T') && !submitData.startTime.includes(':')) {
      submitData.startTime = submitData.startTime + ':00';
    }
  }
  if (submitData.endTime) {
    if (submitData.endTime.includes('T') && !submitData.endTime.includes(':')) {
      submitData.endTime = submitData.endTime + ':00';
    }
  }

  if (isEdit) {
    await carouselApi.updateCarousel(submitData);
    showToastMessage('更新轮播成功');
  } else {
    await carouselApi.addCarousel(submitData);
    showToastMessage('新增轮播成功');
  }
  await fetchCarousels();
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

// 删除轮播
const isDeletePromptVisible = ref(false);
const deleteCarouselId = ref(null);

const handleDelete = (id) => {
  deleteCarouselId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteCarouselId.value = null;
};

const confirmDelete = async () => {
  if (deleteCarouselId.value) {
    try {
      await carouselApi.deleteCarousel(deleteCarouselId.value);
      await fetchCarousels();
      showToastMessage('删除轮播成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除轮播失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 触发文件输入框
const triggerFileInput = (carousel) => {
  // 显示大图预览
  if (carousel.imageUrl) {
    window.open(carousel.imageUrl, '_blank');
  }
};

// 处理复选框
const handleCheck = (carousel) => {
  carousel.checked = !carousel.checked;
};

// 处理批量重置
const handleReset = () => {
  const selected = carousels.value.filter(c => c.checked);
  if (selected.length === 0) {
    showToastMessage('请先选择要删除的轮播', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selected.length} 个轮播，请逐个删除`, 'info');
};

onMounted(fetchCarousels);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.position-filter {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.position-btn {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 15px;
  background-color: #fff;
  color: #606266;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.position-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.position-btn.active {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}
</style>
