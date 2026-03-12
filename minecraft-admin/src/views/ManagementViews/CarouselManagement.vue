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

      <!-- 新增/编辑弹窗 -->
      <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
        <div class="dialog" @click.stop>
          <h2>{{ isEditing ? '编辑轮播' : '新增轮播' }}</h2>
          <form @submit.prevent="submitForm" class="form-container">
            <div class="form-group">
              <div class="image-upload-container">
                <div class="upload-header">
                  <h3>上传轮播图片</h3>
                  <p>支持 JPG, PNG 格式，最大 5MB</p>
                </div>

                <div class="upload-area" @click="triggerFileInput" @dragover.prevent="dragOver = true"
                  @dragleave="dragOver = false" @drop.prevent="handleDrop" :class="{ 'drag-active': dragOver }">
                  <input type="file" ref="fileInput" @change="handleFileUpload" accept="image/*" class="file-input" />

                  <div class="upload-content">
                    <div class="upload-icon">
                      <svg viewBox="0 0 24 24">
                        <path d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z" />
                      </svg>
                    </div>
                    <p class="upload-text">点击或拖拽文件到此处</p>
                    <p class="upload-hint">推荐尺寸：1920×1080px</p>
                  </div>
                </div>

                <!-- 图片预览区域 -->
                <div class="preview-container" v-if="previewImage">
                  <div class="preview-card">
                    <img :src="previewImage" alt="预览图片" class="preview-image" />
                    <div class="preview-actions">
                      <button class="action-btn-image edit-btn-image" @click="triggerFileInput">
                        <svg viewBox="0 0 24 24">
                          <path
                            d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z" />
                        </svg>
                      </button>
                      <button class="action-btn-image delete-btn-image" @click="removeImage">
                        <svg viewBox="0 0 24 24">
                          <path d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z" />
                        </svg>
                      </button>
                    </div>
                    <div class="preview-footer">
                      <div class="file-info">
                        <span class="file-name">{{ fileName }}</span>
                        <span class="file-size">{{ fileSize }}</span>
                      </div>
                      <div class="upload-progress" v-if="uploading">
                        <div class="progress-bar" :style="{ width: progress + '%' }"></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>轮播标题:</label>
                <input v-model="formData.title" required />
              </div>
              <div class="form-group">
                <label>副标题:</label>
                <input v-model="formData.subtitle" />
              </div>
              <div class="form-group">
                <label>位置:</label>
                <el-select v-model="formData.position" placeholder="请选择位置" clearable style="width: 100%" required>
                  <el-option value="home_top" label="首页顶部" />
                  <el-option value="home_middle" label="首页中部" />
                  <el-option value="destination" label="目的地" />
                  <el-option value="attraction" label="景点" />
                  <el-option value="hotel" label="酒店" />
                  <el-option value="food" label="美食" />
                  <el-option value="souvenir" label="纪念品" />
                  <el-option value="strategy" label="攻略群" />
                  <el-option value="community" label="社区" />
                </el-select>
              </div>
              <div class="form-group">
                <label>链接类型:</label>
                <input v-model="formData.linkType" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>链接URL:</label>
                <input v-model="formData.linkUrl" />
              </div>
              <div class="form-group">
                <label>链接目标:</label>
                <input v-model="formData.linkTarget" />
              </div>
              <div class="form-group">
                <label>目标ID:</label>
                <input v-model="formData.targetId" type="number" />
              </div>
              <div class="form-group">
                <label>按钮文本:</label>
                <input v-model="formData.buttonText" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>按钮颜色:</label>
                <input v-model="formData.buttonColor" />
              </div>
              <div class="form-group">
                <label>文本颜色:</label>
                <input v-model="formData.textColor" />
              </div>
              <div class="form-group">
                <label>文本阴影:</label>
                <el-switch v-model="formData.textShadow" />
              </div>
              <div class="form-group">
                <label>叠加颜色:</label>
                <input v-model="formData.overlayColor" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>叠加透明度:</label>
                <input v-model="formData.overlayOpacity" type="number" step="0.1" min="0" max="1" />
              </div>
              <div class="form-group">
                <label>设备类型:</label>
                <input v-model="formData.deviceType" />
              </div>
              <div class="form-group">
                <label>用户类型:</label>
                <input v-model="formData.userType" />
              </div>
              <div class="form-group">
                <label>排序顺序:</label>
                <input v-model="formData.sortOrder" type="number" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>开始时间:</label>
                <input v-model="formData.startTime" type="datetime-local" />
              </div>
              <div class="form-group">
                <label>结束时间:</label>
                <input v-model="formData.endTime" type="datetime-local" />
              </div>
              <div class="form-group">
                <label>是否始终显示:</label>
                <el-switch v-model="formData.isAlwaysShow" />
              </div>
              <div class="form-group">
                <label>是否激活:</label>
                <el-switch v-model="formData.isActive" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>备注:</label>
                <textarea v-model="formData.remark" rows="3"></textarea>
              </div>
              <div class="form-group">
                <label>移动端图片:</label>
                <input v-model="formData.mobileImageUrl" />
              </div>
              <div class="form-group">
                <label>缩略图:</label>
                <input v-model="formData.thumbnailUrl" />
              </div>
            </div>
            <!-- 表单提交按钮 -->
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
import carouselApi from '@/api/carousel';
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
// 分页相关变量
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
  currentPage.value = 1; // 搜索时重置到第一页
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
  // 检查是否包含秒，如果包含则去除
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
  // 处理日期时间格式
  if (formattedCarousel.startTime) {
    formattedCarousel.startTime = formatDateTimeForInput(formattedCarousel.startTime);
  }
  if (formattedCarousel.endTime) {
    formattedCarousel.endTime = formatDateTimeForInput(formattedCarousel.endTime);
  }
  formData.value = formattedCarousel;
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
const validateForm = () => {
  if (!formData.value.title || !formData.value.position) {
    showToastMessage('请填写轮播标题和位置', 'error');
    return false;
  }

  if (!isEditing.value && !formData.value.imageUrl) {
    showToastMessage('请上传轮播图片', 'error');
    return false;
  }

  return true;
};

// 处理表单提交时的日期时间格式
const prepareFormDataForSubmit = (formData) => {
  const submitData = { ...formData };
  // 确保日期时间格式正确
  if (submitData.startTime) {
    // 如果包含T但没有秒，添加秒部分
    if (submitData.startTime.includes('T') && !submitData.startTime.includes(':')) {
      submitData.startTime = submitData.startTime + ':00';
    }
  }
  if (submitData.endTime) {
    // 如果包含T但没有秒，添加秒部分
    if (submitData.endTime.includes('T') && !submitData.endTime.includes(':')) {
      submitData.endTime = submitData.endTime + ':00';
    }
  }
  return submitData;
};

const submitForm = async () => {
  if (!validateForm()) return;

  try {
    const submitData = prepareFormDataForSubmit(formData.value);
    if (isEditing.value) {
      await carouselApi.updateCarousel(submitData);
      showToastMessage('更新轮播成功');
    } else {
      await carouselApi.addCarousel(submitData);
      showToastMessage('新增轮播成功');
    }
    await fetchCarousels();
    closeDialog();
  } catch (error) {
    const message = isEditing.value ? '更新轮播失败' : '新增轮播失败';
    showToastMessage(message, 'error');
    console.error('操作失败:', error);
  }
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
      console.error('删除失败:', error.response?.data || error.message);
      console.error('删除失败:', error);
      showToastMessage('删除轮播失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 关闭对话框
const closeDialog = () => {
  showDialog.value = false;
};

// 图片上传相关状态
const dragOver = ref(false);
const previewImage = ref('');
const fileName = ref('');
const fileSize = ref('');
const uploading = ref(false);
const progress = ref(0);

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 处理文件上传
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png'];
  if (!validTypes.includes(file.type)) {
    showToastMessage('只支持JPG/PNG格式图片', 'error');
    return;
  }

  // 验证文件大小
  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
    showToastMessage('图片大小不能超过5MB', 'error');
    return;
  }

  // 显示文件信息
  fileName.value = file.name;
  fileSize.value = formatFileSize(file.size);

  // 读取并预览图片
  const reader = new FileReader();
  reader.onload = (e) => {
    previewImage.value = e.target.result;
    formData.value.imageUrl = e.target.result;
  };
  reader.readAsDataURL(file);

  // 模拟上传进度
  uploading.value = true;
  const interval = setInterval(() => {
    if (progress.value < 100) {
      progress.value += 10;
    } else {
      clearInterval(interval);
      uploading.value = false;
    }
  }, 100);

  return file;
};

// 处理拖放上传
const handleDrop = (event) => {
  dragOver.value = false;
  const file = event.dataTransfer.files[0];
  if (file) {
    const fakeEvent = { target: { files: [file] } };
    handleFileUpload(fakeEvent);
  }
};

// 移除图片
const removeImage = () => {
  previewImage.value = '';
  fileName.value = '';
  fileSize.value = '';
  formData.value.imageUrl = '';
};

// 触发文件输入框
const triggerFileInput = () => {
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = 'image/*';
  fileInput.onchange = (event) => {
    const file = handleFileUpload(event);
    if (file) {
      formData.value.file = file;
    }
  };
  fileInput.click();
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