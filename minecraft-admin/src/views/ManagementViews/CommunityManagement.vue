<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入帖子标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增帖子</button>
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
              <tr v-for="post in paginatedPosts" :key="post.id">
                <td>
                  <input type="checkbox" v-model="post.checked" class="ui-checkbox" />
                </td>
                <td>{{ post.id }}</td>
                <td>{{ post.userId }}</td>
                <td>{{ post.username || '未知用户' }}</td>
                <td>
                  <div class="image-preview-container">
                    <div class="image-preview-wrapper">
                      <img 
                        v-for="(img, index) in getAllImages(post.images).slice(0, 3)" 
                        :key="index"
                        :src="img" 
                        :alt="`图片${index + 1}`" 
                        class="preview-image"
                        :class="{ 'main-image': index === 0 }"
                        @error="handleImageError($event)"
                      />
                      <span v-if="getAllImages(post.images).length > 3" class="more-indicator">
                        +{{ getAllImages(post.images).length - 3 }}
                      </span>
                    </div>
                    <span v-if="getAllImages(post.images).length === 0" style="color: #999;">无图片</span>
                  </div>
                </td>
                <td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ post.title }}</td>
                <td style="max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ post.content }}</td>
                <td>{{ post.likeCount }}</td>
                <td>{{ post.collectCount }}</td>
                <td>{{ post.commentCount }}</td>
                <td>{{ formatDate(post.createTime) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailDialog(post)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(post)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(post.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 分页器 -->
      <div class="block">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>

      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="帖子"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="false"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      >
        <template #custom-fields>
          <div class="form-group">
            <div class="image-upload-container">
              <div class="upload-header">
                <h3>上传图片</h3>
                <p>支持 JPG, PNG 格式，最大 5MB</p>
              </div>
              <div class="upload-area" @click="triggerFileInput" @dragover.prevent="dragOver = true"
                @dragleave="dragOver = false" @drop.prevent="handleDrop" :class="{ 'drag-active': dragOver }">
                <input type="file" ref="fileInput" @change="handleFileUpload" accept="image/*" class="file-input" multiple />
                <div class="upload-content">
                  <div class="upload-icon">
                    <svg viewBox="0 0 24 24">
                      <path d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z" />
                    </svg>
                  </div>
                  <p class="upload-text">点击或拖拽文件到此处</p>
                  <p class="upload-hint">推荐尺寸：1200×800px，可上传多张</p>
                </div>
              </div>
              <div class="preview-container" v-if="previewImages.length > 0">
                <div class="preview-card" v-for="(img, index) in previewImages" :key="index">
                  <img :src="img" alt="预览图片" class="preview-image" />
                  <div class="preview-actions">
                    <button class="action-btn-image delete-btn-image" @click="removeImage(index)">
                      <svg viewBox="0 0 24 24">
                        <path d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </FormDialog>

      <!-- 详情弹窗 -->
      <div v-if="showDetail" class="dialog-overlay" @click.self="closeDetailDialog">
        <div class="dialog detail-dialog" @click.stop>
          <h2>帖子详情</h2>
          <div class="detail-content">
            <div class="detail-item">
              <span class="detail-label">帖子ID:</span>
              <span class="detail-value">{{ formData.id }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">用户ID:</span>
              <span class="detail-value">{{ formData.userId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">用户名:</span>
              <span class="detail-value">{{ formData.username || '未知用户' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">标题:</span>
              <span class="detail-value">{{ formData.title }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">内容:</span>
              <div class="detail-value content-text">{{ formData.content }}</div>
            </div>
            <div class="detail-item" v-if="previewImages.length > 0">
              <span class="detail-label">图片:</span>
              <div class="detail-images">
                <img v-for="(img, index) in previewImages" :key="index" :src="img" class="detail-image" alt="帖子图片" />
              </div>
            </div>
            <div class="detail-item" v-if="formData.videos">
              <span class="detail-label">视频:</span>
              <span class="detail-value">{{ formData.videos }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">点赞数:</span>
              <span class="detail-value">{{ formData.likeCount }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">收藏数:</span>
              <span class="detail-value">{{ formData.collectCount }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">评论数:</span>
              <span class="detail-value">{{ formData.commentCount }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间:</span>
              <span class="detail-value">{{ formatDate(formData.createTime) }}</span>
            </div>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn confirm-btn" @click="closeDetailDialog">关闭</button>
          </div>
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
import { getPostList, createPost, updatePost, deletePost } from '@/api/community';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '帖子ID' },
  { key: 'userId', title: '用户ID' },
  { key: 'username', title: '用户名' },
  { key: 'images', title: '图片' },
  { key: 'title', title: '标题' },
  { key: 'content', title: '内容' },
  { key: 'likeCount', title: '点赞数' },
  { key: 'collectCount', title: '收藏数' },
  { key: 'commentCount', title: '评论数' },
  { key: 'createTime', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const posts = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetail = ref(false);
const isEditing = ref(false);

const formData = ref({
  id: null,
  userId: null,
  username: '',
  avatar: '',
  title: '',
  content: '',
  images: '',
  videos: '',
  likeCount: 0,
  collectCount: 0,
  commentCount: 0,
  createTime: '',
});

// 表单字段配置
const formFields = [
  [
    { name: 'title', label: '帖子标题', type: 'text', required: true, placeholder: '请输入帖子标题' },
    { name: 'videos', label: '视频链接', type: 'text', placeholder: '请输入视频链接（可选）' },
  ],
  [
    { name: 'content', label: '帖子内容', type: 'textarea', required: true, rows: 4, placeholder: '请输入帖子内容' },
  ],
];

// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页处理函数
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchPosts();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchPosts();
};

const formatDate = (date) => {
  if (!date) return '未记录';
  return new Date(date).toLocaleString('zh-CN');
};

const getFirstImage = (imagesStr) => {
  if (!imagesStr) return '';
  try {
    const images = JSON.parse(imagesStr);
    if (Array.isArray(images) && images.length > 0) {
      return images[0].replace(/`/g, '').trim();
    }
  } catch (e) {
    // 如果不是JSON数组，尝试按逗号分割
    const parts = imagesStr.split(',');
    if (parts.length > 0) {
      return parts[0].trim();
    }
  }
  return '';
};

const getAllImages = (imagesStr) => {
  if (!imagesStr) return [];
  try {
    const images = JSON.parse(imagesStr);
    if (Array.isArray(images)) {
      return images.map(img => img.replace(/`/g, '').trim()).filter(img => img);
    }
  } catch (e) {
    // 如果不是JSON数组，尝试按逗号分割
    return imagesStr.split(',').map(img => img.trim()).filter(img => img);
  }
  return [];
};

const handleImageError = (event) => {
  event.target.style.display = 'none';
};

// 获取帖子数据
const fetchPosts = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
    };
    const response = await getPostList(params);
    if (response.code === 200) {
      posts.value = (response.data?.records || []).map(post => ({
        ...post,
        checked: false,
      }));
      total.value = response.data?.total || 0;
    } else {
      console.error('获取帖子数据失败:', response.message || '未知错误');
      posts.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('请求失败:', error);
    posts.value = [];
    total.value = 0;
  }
};

// 搜索按钮点击事件
const handleSearch = () => {
  currentPage.value = 1;
  fetchPosts();
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    userId: 1,
    username: '',
    avatar: '',
    title: '',
    content: '',
    images: '',
    videos: '',
    likeCount: 0,
    collectCount: 0,
    commentCount: 0,
    createTime: '',
  };
  previewImages.value = [];
  dragOver.value = false;
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (post) => {
  isEditing.value = true;
  formData.value = {
    id: post.id,
    userId: post.userId,
    username: post.username || '',
    avatar: post.avatar || '',
    title: post.title,
    content: post.content,
    images: post.images || '',
    videos: post.videos || '',
    likeCount: post.likeCount || 0,
    collectCount: post.collectCount || 0,
    commentCount: post.commentCount || 0,
    createTime: post.createTime || '',
  };
  // 解析已有图片
  previewImages.value = [];
  if (post.images) {
    try {
      const images = JSON.parse(post.images);
      if (Array.isArray(images)) {
        previewImages.value = images.map(img => img.replace(/`/g, '').trim());
      }
    } catch (e) {
      previewImages.value = [];
    }
  }
  showDialog.value = true;
};

// 显示详情对话框
const showDetailDialog = (post) => {
  formData.value = {
    id: post.id,
    userId: post.userId,
    username: post.username || '',
    avatar: post.avatar || '',
    title: post.title,
    content: post.content,
    images: post.images || '',
    videos: post.videos || '',
    likeCount: post.likeCount || 0,
    collectCount: post.collectCount || 0,
    commentCount: post.commentCount || 0,
    createTime: post.createTime || '',
  };
  previewImages.value = [];
  if (post.images) {
    try {
      const images = JSON.parse(post.images);
      if (Array.isArray(images)) {
        previewImages.value = images.map(img => img.replace(/`/g, '').trim());
      }
    } catch (e) {
      previewImages.value = [];
    }
  }
  showDetail.value = true;
};

const closeDetailDialog = () => {
  showDetail.value = false;
};

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.title) {
    return '请输入帖子标题';
  }
  if (!data.content) {
    return '请输入帖子内容';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  const submitData = {
    ...data,
    images: previewImages.value.length > 0 ? JSON.stringify(previewImages.value) : '',
  };
  if (isEdit) {
    await updatePost(submitData);
    showToastMessage('更新帖子成功');
  } else {
    await createPost(submitData);
    showToastMessage('新增帖子成功');
  }
  await fetchPosts();
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

// 删除帖子
const isDeletePromptVisible = ref(false);
const deletePostId = ref(null);

const handleDelete = (id) => {
  deletePostId.value = id;
  isDeletePromptVisible.value = true;
};

const handleBatchDelete = () => {
  const checkedIds = posts.value.filter(p => p.checked).map(p => p.id);
  if (checkedIds.length === 0) {
    showToastMessage('请先选择要删除的帖子', 'error');
    return;
  }
  deletePostId.value = checkedIds;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deletePostId.value = null;
};

const confirmDelete = async () => {
  if (deletePostId.value) {
    try {
      if (Array.isArray(deletePostId.value)) {
        for (const id of deletePostId.value) {
          await deletePost(id);
        }
        showToastMessage(`批量删除成功，共删除 ${deletePostId.value.length} 条帖子`);
      } else {
        await deletePost(deletePostId.value);
        showToastMessage('删除帖子成功');
      }
      await fetchPosts();
    } catch (error) {
      console.error('删除失败:', error.response?.data || error.message);
      showToastMessage('删除帖子失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 处理文件上传
const dragOver = ref(false);
const previewImages = ref([]);

const handleFileUpload = (event) => {
  dragOver.value = false;
  const files = event.target.files || event.dataTransfer?.files;
  if (!files || files.length === 0) return;
  Array.from(files).forEach((file) => {
    if (!file.type.match('image.*')) {
      showToastMessage('请选择图片文件', 'error');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      showToastMessage('文件大小不能超过5MB', 'error');
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      previewImages.value.push(e.target.result);
    };
    reader.readAsDataURL(file);
  });
};

const triggerFileInput = () => {
  document.querySelector('.file-input')?.click();
};

const handleDrop = (e) => {
  e.preventDefault();
  handleFileUpload(e);
};

const removeImage = (index) => {
  previewImages.value.splice(index, 1);
};

// 前端分页逻辑
const paginatedPosts = computed(() => {
  return posts.value || [];
});

onMounted(fetchPosts);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.detail-dialog {
  max-width: 600px;
}

.detail-content {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-label {
  font-weight: 600;
  color: #333;
  min-width: 80px;
}

.detail-value {
  flex: 1;
  color: #666;
}

.content-text {
  line-height: 1.6;
}

.detail-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.detail-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
}

.preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 20px;
}

.preview-card {
  position: relative;
  width: 150px;
  height: 150px;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.preview-actions {
  position: absolute;
  top: 5px;
  right: 5px;
}

.action-btn-image {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 77, 79, 0.9);
}

.action-btn-image svg {
  width: 14px;
  height: 14px;
  fill: white;
}

/* 表格图片预览样式 */
.image-preview-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.image-preview-wrapper {
  position: relative;
  display: flex;
  flex-direction: row;
  gap: 4px;
}

.image-preview-wrapper .preview-image {
  width: 32px;
  height: 32px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  transition: all 0.2s ease;
}

.image-preview-wrapper .preview-image:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.image-preview-wrapper .preview-image.main-image {
  width: 35px;
  height: 35px;
}

.more-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 0 4px 0 4px;
}

@media (max-width: 768px) {
  .image-preview-wrapper .preview-image {
    width: 28px;
    height: 28px;
  }
  
  .image-preview-wrapper .preview-image.main-image {
    width: 30px;
    height: 30px;
  }
}
</style>