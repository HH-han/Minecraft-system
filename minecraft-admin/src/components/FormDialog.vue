<template>
  <Teleport to="body">
    <div v-if="visible" class="form-dialog-overlay" @click.self="handleClose">
      <div class="form-dialog" @click.stop>
        <!-- 头部 -->
        <div class="dialog-header">
          <h2 class="dialog-title">{{ isEdit ? '编辑' : '新增' }}{{ title }}</h2>
          <button class="close-btn" @click="handleClose">
            <svg viewBox="0 0 24 24">
              <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
            </svg>
          </button>
        </div>

        <!-- 表单内容 -->
        <div class="dialog-body">
          <form @submit.prevent="handleSubmit" class="dialog-form">
            <!-- 图片上传区域 -->
            <div v-if="showImageUpload" class="form-section">
              <div class="image-upload-container">
                <div class="upload-header">
                  <h3>{{ imageUploadLabel }}</h3>
                  <p>支持 JPG, PNG 格式，最大 5MB</p>
                </div>

                <div
                  v-if="!previewImage"
                  class="upload-area"
                  :class="{ 'drag-active': dragOver }"
                  @click="triggerFileInput"
                  @dragover.prevent="dragOver = true"
                  @dragleave="dragOver = false"
                  @drop.prevent="handleDrop"
                >
                  <input
                    type="file"
                    ref="fileInputRef"
                    @change="handleFileUpload"
                    accept="image/*"
                    class="file-input"
                  />

                  <div class="upload-content">
                    <div class="upload-icon">
                      <svg viewBox="0 0 24 24">
                        <path d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z" />
                      </svg>
                    </div>
                    <p class="upload-text">点击或拖拽文件到此处</p>
                    <p class="upload-hint">{{ recommendedSize }}</p>
                  </div>
                </div>

                <!-- 图片预览区域 -->
                <div class="preview-container" v-if="previewImage">
                  <div class="preview-card">
                    <div class="preview-image-wrapper">
                      <img :src="previewImage" alt="预览图片" class="preview-image" />
                    </div>
                    <div class="preview-actions">
                      <button type="button" class="action-btn-image edit-btn-image" @click="triggerFileInput">
                        <svg viewBox="0 0 24 24">
                          <path d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z" />
                        </svg>
                      </button>
                      <button type="button" class="action-btn-image delete-btn-image" @click="removeImage">
                        <svg viewBox="0 0 24 24">
                          <path d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z" />
                        </svg>
                      </button>
                    </div>
                    <div class="preview-footer" v-if="fileName || uploading">
                      <div class="file-info" v-if="fileName">
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

            <!-- 自定义插槽 - 用于特殊组件如 FacilitySelector -->
            <slot name="custom-fields"></slot>

            <!-- 动态表单字段 -->
            <div class="form-fields">
              <div
                v-for="(field, index) in fields"
                :key="index"
                class="form-row"
              >
                <div
                  v-for="(fieldItem, fieldIndex) in field"
                  :key="fieldIndex"
                  :class="['form-group', { 'full-width': fieldItem.fullWidth }]"
                >
                  <!-- 标签 -->
                  <label v-if="fieldItem.type !== 'switch'" :class="{ 'required': fieldItem.required }">
                    {{ fieldItem.label }}
                  </label>

                  <!-- 输入框 -->
                  <input
                    v-if="fieldItem.type === 'text' || fieldItem.type === undefined"
                    v-model="formData[fieldItem.name]"
                    :type="fieldItem.inputType || 'text'"
                    :placeholder="fieldItem.placeholder || ''"
                    :required="fieldItem.required"
                    :min="fieldItem.min"
                    :max="fieldItem.max"
                    :step="fieldItem.step"
                    class="form-input"
                  />

                  <!-- 数字输入框 -->
                  <input
                    v-else-if="fieldItem.type === 'number'"
                    v-model.number="formData[fieldItem.name]"
                    type="number"
                    :placeholder="fieldItem.placeholder || ''"
                    :required="fieldItem.required"
                    :min="fieldItem.min"
                    :max="fieldItem.max"
                    :step="fieldItem.step"
                    class="form-input"
                  />

                  <!-- 密码输入框 -->
                  <input
                    v-else-if="fieldItem.type === 'password'"
                    v-model="formData[fieldItem.name]"
                    type="password"
                    :placeholder="fieldItem.placeholder || ''"
                    :required="fieldItem.required"
                    class="form-input"
                  />

                  <!-- 邮箱输入框 -->
                  <input
                    v-else-if="fieldItem.type === 'email'"
                    v-model="formData[fieldItem.name]"
                    type="email"
                    :placeholder="fieldItem.placeholder || ''"
                    :required="fieldItem.required"
                    class="form-input"
                  />

                  <!-- 文本域 -->
                  <textarea
                    v-else-if="fieldItem.type === 'textarea'"
                    v-model="formData[fieldItem.name]"
                    :placeholder="fieldItem.placeholder || ''"
                    :required="fieldItem.required"
                    :rows="fieldItem.rows || 3"
                    class="form-textarea"
                  ></textarea>

                  <!-- 下拉选择 -->
                  <select
                    v-else-if="fieldItem.type === 'select'"
                    v-model="formData[fieldItem.name]"
                    :required="fieldItem.required"
                    class="form-select"
                  >
                    <option value="">{{ fieldItem.placeholder || '请选择' }}</option>
                    <option
                      v-for="option in fieldItem.options"
                      :key="option.value"
                      :value="option.value"
                    >
                      {{ option.label }}
                    </option>
                  </select>

                  <!-- 开关 -->
                  <div v-else-if="fieldItem.type === 'switch'" class="switch-wrapper">
                    <label class="switch-label">{{ fieldItem.label }}</label>
                    <label class="switch">
                      <input
                        type="checkbox"
                        v-model="formData[fieldItem.name]"
                      />
                      <span class="slider"></span>
                    </label>
                  </div>

                  <!-- 日期时间选择 -->
                  <input
                    v-else-if="fieldItem.type === 'datetime'"
                    v-model="formData[fieldItem.name]"
                    type="datetime-local"
                    :required="fieldItem.required"
                    class="form-input"
                  />

                  <!-- 时间选择 -->
                  <input
                    v-else-if="fieldItem.type === 'time'"
                    v-model="formData[fieldItem.name]"
                    type="time"
                    :required="fieldItem.required"
                    class="form-input"
                  />
                </div>
              </div>
            </div>

            <!-- 错误提示 -->
            <div v-if="errorMessage" class="error-tip">
              <svg viewBox="0 0 24 24">
                <path d="M12,2C17.53,2 22,6.47 22,12C22,17.53 17.53,22 12,22C6.47,22 2,17.53 2,12C2,6.47 6.47,2 12,2M15.59,7L12,10.59L8.41,7L7,8.41L10.59,12L7,15.59L8.41,17L12,13.41L15.59,17L17,15.59L13.41,12L17,8.41L15.59,7Z" />
              </svg>
              <span>{{ errorMessage }}</span>
            </div>

            <!-- 底部按钮 -->
            <div class="dialog-footer">
              <button type="button" class="btn btn-cancel" @click="handleClose" :disabled="loading">
                取消
              </button>
              <button type="submit" class="btn btn-confirm" :disabled="loading">
                <span v-if="loading" class="loading-spinner"></span>
                <span v-else>{{ isEdit ? '保存' : '创建' }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, watch, computed } from 'vue';

// Props 定义
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  fields: {
    type: Array,
    default: () => [] // 二维数组，用于分组
  },
  initialData: {
    type: Object,
    default: () => ({})
  },
  showImageUpload: {
    type: Boolean,
    default: true
  },
  imageUploadLabel: {
    type: String,
    default: '上传图片'
  },
  recommendedSize: {
    type: String,
    default: '推荐尺寸：1200×800px'
  },
  imageField: {
    type: String,
    default: 'imageUrl'
  },
  validateFn: {
    type: Function,
    default: null
  },
  submitFn: {
    type: Function,
    required: true
  }
});

// Emits 定义
const emit = defineEmits(['update:visible', 'close', 'submit', 'error']);

// 响应式数据
const dragOver = ref(false);
const previewImage = ref('');
const fileName = ref('');
const fileSize = ref('');
const uploading = ref(false);
const progress = ref(0);
const loading = ref(false);
const errorMessage = ref('');
const fileInputRef = ref(null);

// 表单数据
const formData = ref({});

// 监听 visible 变化，初始化数据
watch(() => props.visible, (newVal) => {
  if (newVal) {
    initFormData();
    errorMessage.value = '';
  }
});

// 监听 initialData 变化
watch(() => props.initialData, () => {
  if (props.visible) {
    initFormData();
  }
}, { deep: true });

// 初始化表单数据
const initFormData = () => {
  const data = {};

  // 提取所有字段名
  const fieldNames = [];
  props.fields.forEach(row => {
    row.forEach(field => {
      if (field.name) {
        fieldNames.push(field.name);
      }
    });
  });

  // 从 initialData 中复制数据
  fieldNames.forEach(name => {
    data[name] = props.initialData[name] ?? '';
  });

  // 保留 id 字段（编辑模式下用于标识当前记录，避免后端因 id 缺失导致关联数据插入失败）
  if (props.initialData.id !== undefined && props.initialData.id !== null) {
    data.id = props.initialData.id;
  }

  // 设置图片预览
  if (props.showImageUpload && props.initialData[props.imageField]) {
    previewImage.value = props.initialData[props.imageField];
    fileName.value = '已上传图片';
  } else {
    previewImage.value = '';
    fileName.value = '';
  }

  formData.value = data;
};

// 触发文件输入
const triggerFileInput = () => {
  fileInputRef.value?.click();
};

// 处理文件上传
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/jpg'];
  if (!validTypes.includes(file.type)) {
    showError('只支持 JPG/PNG 格式图片');
    return;
  }

  // 验证文件大小
  const maxSize = 10 * 1024 * 1024;
  if (file.size > maxSize) {
    showError('图片大小不能超过 10MB');
    return;
  }

  // 显示文件信息
  fileName.value = file.name;
  fileSize.value = formatFileSize(file.size);

  // 读取并预览图片
  const reader = new FileReader();
  reader.onload = (e) => {
    previewImage.value = e.target.result;
    formData.value[props.imageField] = e.target.result;
  };
  reader.readAsDataURL(file);

  // 模拟上传进度
  uploading.value = true;
  progress.value = 0;
  const interval = setInterval(() => {
    if (progress.value < 100) {
      progress.value += Math.random() * 20;
      if (progress.value > 100) progress.value = 100;
    } else {
      clearInterval(interval);
      uploading.value = false;
    }
  }, 100);
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
  formData.value[props.imageField] = '';
  if (fileInputRef.value) {
    fileInputRef.value.value = '';
  }
};

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 显示错误
const showError = (message) => {
  errorMessage.value = message;
  emit('error', message);
  setTimeout(() => {
    errorMessage.value = '';
  }, 3000);
};

// 关闭弹窗
const handleClose = () => {
  if (loading.value) return;
  emit('update:visible', false);
  emit('close');
};

// 提交表单
const handleSubmit = async () => {
  errorMessage.value = '';

  // 自定义验证
  if (props.validateFn) {
    const validationError = props.validateFn(formData.value, props.isEdit);
    if (validationError) {
      showError(validationError);
      return;
    }
  }

  // 检查图片上传（如果需要）
  if (props.showImageUpload && !props.isEdit && !formData.value[props.imageField]) {
    showError('请上传图片');
    return;
  }

  loading.value = true;

  try {
    await props.submitFn(formData.value, props.isEdit);
    emit('submit', formData.value, props.isEdit);
    handleClose();
  } catch (error) {
    showError(error.message || '操作失败，请重试');
    emit('error', error);
  } finally {
    loading.value = false;
  }
};

// 暴露方法给父组件
defineExpose({
  showError,
  setLoading: (val) => { loading.value = val; }
});
</script>

<style scoped>
/* 弹窗-overlay */
.form-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 弹窗主体 */
.form-dialog {
  background: #ffffff;
  border-radius: 24px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 头部 */
.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
  border-bottom: 1px solid #f5f5f7;
}

.dialog-title {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f7;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  padding: 0;
}

.close-btn:hover {
  background: #e5e5e7;
  transform: scale(1.05);
}

.close-btn svg {
  width: 16px;
  height: 16px;
  fill: #6e6e73;
}

/* 主体内容 */
.dialog-body {
  padding: 32px;
  overflow-y: auto;
  max-height: calc(90vh - 160px);
}

/* 表单 */
.dialog-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 图片上传 */
.form-section {
  margin-bottom: 8px;
}

.image-upload-container {
  background: #f5f5f7;
  border-radius: 20px;
  padding: 24px;
}

.upload-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 4px 0;
}

.upload-header p {
  font-size: 12px;
  color: #6e6e73;
  margin: 0;
}

.upload-area {
  margin-top: 16px;
  border: 2px dashed #d2d2d6;
  border-radius: 16px;
  padding: 40px 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #ffffff;
}

.upload-area:hover,
.upload-area.drag-active {
  border-color: #2997ff;
  background: rgba(41, 151, 255, 0.05);
}

.file-input {
  display: none;
}

.upload-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
  background: #f5f5f7;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-icon svg {
  width: 24px;
  height: 24px;
  fill: #6e6e73;
}

.upload-text {
  font-size: 14px;
  color: #1d1d1f;
  margin: 0 0 4px 0;
}

.upload-hint {
  font-size: 12px;
  color: #6e6e73;
  margin: 0;
}

/* 预览区域 */
.preview-container {
  margin-top: 16px;
}

.preview-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.preview-image-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f8f8;
  border-radius: 12px;
  padding: 20px;
  min-height: 120px;
}

.preview-image {
  max-width: 100%;
  max-height: 280px;
  object-fit: contain;
  border-radius: 8px;
}

.preview-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 12px;
}

.action-btn-image {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.edit-btn-image {
  background: #e3f2fd;
}

.edit-btn-image:hover {
  background: #bbdefb;
}

.edit-btn-image svg {
  width: 18px;
  height: 18px;
  fill: #1976d2;
}

.delete-btn-image {
  background: #ffebee;
}

.delete-btn-image:hover {
  background: #ffcdd2;
}

.delete-btn-image svg {
  width: 18px;
  height: 18px;
  fill: #c62828;
}

.preview-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f7;
}

.file-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6e6e73;
}

.file-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 60%;
}

.upload-progress {
  margin-top: 8px;
  height: 4px;
  background: #f5f5f7;
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #2997ff, #0077ed);
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* 表单字段 */
.form-fields {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-group {
  flex: 1;
  min-width: calc(33.333% - 14px);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group.full-width {
  flex-basis: 100%;
  min-width: 100%;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  line-height: 1.4;
}

.form-group label.required::after {
  content: '*';
  color: #ff3b30;
  margin-left: 4px;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: 12px 16px;
  font-size: 14px;
  color: #1d1d1f;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  outline: none;
  transition: all 0.2s ease;
  box-sizing: border-box;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  border-color: #2997ff;
  box-shadow: 0 0 0 3px rgba(41, 151, 255, 0.15);
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: #a1a1a6;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%236e6e73' d='M6,8L1,3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 16px center;
  padding-right: 40px;
}

/* 开关 */
.switch-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
}

.switch-label {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  margin-bottom: 0 !important;
}

.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 28px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #e5e5ea;
  transition: 0.3s;
  border-radius: 28px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 24px;
  width: 24px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

input:checked + .slider {
  background-color: #34c759;
}

input:checked + .slider:before {
  transform: translateX(20px);
}

/* 错误提示 */
.error-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fff1f0;
  border-radius: 12px;
  color: #ff3b30;
  font-size: 14px;
}

.error-tip svg {
  width: 18px;
  height: 18px;
  fill: #ff3b30;
  flex-shrink: 0;
}

/* 底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f5f5f7;
}

.btn {
  padding: 12px 28px;
  font-size: 14px;
  font-weight: 500;
  border: none;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 100px;
}

.btn-cancel {
  background: #f5f5f7;
  color: #1d1d1f;
}

.btn-cancel:hover:not(:disabled) {
  background: #e5e5e7;
}

.btn-confirm {
  background: #2997ff;
  color: #ffffff;
}

.btn-confirm:hover:not(:disabled) {
  background: #0077ed;
  transform: scale(1.02);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #ffffff;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .form-dialog {
    width: 92%;
    max-width: 700px;
  }

  .form-group {
    min-width: calc(50% - 10px);
  }
}

@media (max-width: 768px) {
  .form-dialog {
    width: 95%;
    max-height: 95vh;
    border-radius: 20px;
  }

  .dialog-header {
    padding: 20px 24px;
  }

  .dialog-title {
    font-size: 20px;
  }

  .dialog-body {
    padding: 20px;
  }

  .form-group {
    min-width: 100%;
  }

  .form-row {
    flex-direction: column;
    gap: 12px;
  }

  .image-upload-container {
    padding: 16px;
  }

  .upload-area {
    padding: 24px 16px;
  }

  .preview-card {
    padding: 16px;
  }

  .preview-image-wrapper {
    padding: 12px;
    min-height: 100px;
  }

  .preview-image {
    max-height: 200px;
  }

  .dialog-footer {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .form-dialog {
    width: 98%;
    border-radius: 16px;
  }

  .dialog-header {
    padding: 16px 20px;
  }

  .dialog-title {
    font-size: 18px;
  }

  .dialog-body {
    padding: 16px;
  }

  .upload-area {
    padding: 20px 12px;
  }

  .upload-icon {
    width: 40px;
    height: 40px;
  }

  .upload-icon svg {
    width: 20px;
    height: 20px;
  }
}
</style>
