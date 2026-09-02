<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal">
      <div class="modal-header">
        <h2>{{ isEdit ? '编辑卡片' : '发布卡片' }}</h2>
        <button class="close-btn" @click="handleClose">×</button>
      </div>

      <div class="modal-body">
        <div class="form-group">
          <label>标题 <span class="required">*</span></label>
          <input
            v-model="form.title"
            type="text"
            maxlength="200"
            placeholder="给这趟旅程起个标题"
          />
        </div>

        <div class="form-group">
          <label>内容 <span class="required">*</span></label>
          <textarea
            v-model="form.content"
            rows="5"
            placeholder="记录旅途中的风景、美食与心情..."
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>地点</label>
            <input v-model="form.location" type="text" maxlength="255" placeholder="如：云南·大理" />
          </div>
          <div class="form-group">
            <label>旅行日期</label>
            <input v-model="form.date" type="date" />
          </div>
        </div>

        <div class="form-group">
          <label>标签</label>
          <input
            v-model="form.tags"
            type="text"
            maxlength="500"
            placeholder="多个标签用英文逗号分隔，如：美食,打卡,海景"
          />
        </div>

        <div class="form-group">
          <label>图片</label>
          <div class="image-uploader">
            <label class="upload-trigger">
              <input
                type="file"
                accept="image/*"
                multiple
                :disabled="uploading"
                @change="handleImagesUpload"
              />
              <span class="upload-icon">+</span>
              <span class="upload-text">{{ uploading ? '上传中...' : '添加图片' }}</span>
            </label>
            <div v-for="(img, index) in form.imageList" :key="`${img}-${index}`" class="upload-thumb">
              <img :src="normalizeImageUrl(img)" alt="preview" @error="hideBrokenImage" />
              <button class="thumb-remove" type="button" @click="removeImage(index)">×</button>
            </div>
          </div>
          <p class="form-hint">支持多张图片，建议使用横版照片，上传后可删除重选</p>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn-secondary" @click="handleClose">取消</button>
        <button class="btn-primary" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '保存中...' : isEdit ? '保存修改' : '发布' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadFile } from '@/api/upload'
import { normalizeImageUrl, parseImageList } from '../../utils.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  card: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'submit'])

const isEdit = computed(() => !!props.card?.id)
const submitting = reactive({ value: false })
const uploading = reactive({ value: false })

const form = reactive({
  title: '',
  content: '',
  location: '',
  tags: '',
  date: '',
  imageList: []
})

watch(
  () => props.visible,
  (val) => {
    if (val) {
      if (props.card?.id) {
        form.title = props.card.title || ''
        form.content = props.card.content || ''
        form.location = props.card.location || ''
        form.tags = props.card.tags || ''
        form.date = props.card.date || ''
        form.imageList = parseImageList(props.card)
      } else {
        resetForm()
      }
    }
  }
)

function resetForm() {
  form.title = ''
  form.content = ''
  form.location = ''
  form.tags = ''
  form.date = ''
  form.imageList = []
}

function handleClose() {
  if (submitting.value) return
  emit('close')
}

async function handleImagesUpload(event) {
  const files = Array.from(event.target.files || [])
  if (files.length === 0) return
  uploading.value = true
  try {
    for (const file of files) {
      if (!file.type.startsWith('image/')) {
        ElMessage.warning('请选择图片文件')
        continue
      }
      const res = await uploadFile(file)
      if (res?.code === 200 && res.data) {
        form.imageList.push(String(res.data))
      } else {
        ElMessage.error(res?.message || '图片上传失败')
      }
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败，请稍后重试')
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

function removeImage(index) {
  form.imageList.splice(index, 1)
}

function hideBrokenImage(event) {
  if (event.target) event.target.style.opacity = '0.3'
}

function handleSubmit() {
  if (!form.title.trim()) {
    ElMessage.warning('请输入卡片标题')
    return
  }
  if (!form.content.trim()) {
    ElMessage.warning('请输入卡片内容')
    return
  }
  submitting.value = true
  const payload = {
    title: form.title.trim(),
    content: form.content.trim(),
    location: form.location?.trim() || null,
    tags: form.tags?.trim() || null,
    date: form.date || null,
    images: form.imageList.length > 0 ? JSON.stringify(form.imageList) : null
  }
  if (isEdit.value) {
    payload.id = props.card.id
  }
  emit('submit', payload, () => {
    submitting.value = false
  })
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-24);
  animation: fadeIn var(--transition-fast);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: var(--color-bg);
  border-radius: var(--radius-xl);
  width: 100%;
  max-width: 640px;
  max-height: 88vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 40px 80px rgba(0, 0, 0, 0.2);
  animation: modalIn var(--transition-normal);
}

@keyframes modalIn {
  from { opacity: 0; transform: translateY(20px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-24) var(--spacing-32);
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  font-size: 22px;
  font-weight: 700;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: var(--color-bg-secondary);
  border-radius: 50%;
  font-size: 18px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.08);
  color: var(--color-text);
}

.modal-body {
  padding: var(--spacing-24) var(--spacing-32);
  overflow-y: auto;
}

.form-group {
  margin-bottom: var(--spacing-20);
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: var(--spacing-8);
}

.required {
  color: var(--color-danger);
}

.form-group input,
.form-group textarea {
  width: 100%;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: var(--spacing-12) var(--spacing-16);
  font-size: 15px;
  font-family: inherit;
  color: var(--color-text);
  background: var(--color-bg);
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
  outline: none;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.15);
}

.form-group textarea {
  resize: vertical;
  line-height: 1.6;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-16);
}

.form-hint {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-top: var(--spacing-8);
}

/* 图片上传 */
.image-uploader {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-12);
}

.upload-trigger {
  width: 96px;
  height: 96px;
  border: 1.5px dashed var(--color-border);
  border-radius: var(--radius-sm);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-4);
  cursor: pointer;
  color: var(--color-text-secondary);
  transition: all var(--transition-fast);
  background: var(--color-bg-secondary);
}

.upload-trigger:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.upload-trigger input {
  display: none;
}

.upload-icon {
  font-size: 24px;
  line-height: 1;
}

.upload-text {
  font-size: 12px;
}

.upload-thumb {
  position: relative;
  width: 96px;
  height: 96px;
}

.upload-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--radius-sm);
}

.thumb-remove {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: var(--color-danger);
  color: #fff;
  font-size: 12px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform var(--transition-fast);
}

.thumb-remove:hover {
  transform: scale(1.1);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-12);
  padding: var(--spacing-20) var(--spacing-32);
  border-top: 1px solid var(--color-border);
}

.modal-footer button {
  border: none;
  padding: var(--spacing-10) var(--spacing-28);
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.btn-primary:active:not(:disabled) {
  transform: scale(0.98);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.btn-secondary:hover {
  background: rgba(0, 0, 0, 0.08);
}

@media (max-width: 767px) {
  .modal-body,
  .modal-header,
  .modal-footer {
    padding-left: var(--spacing-20);
    padding-right: var(--spacing-20);
  }
  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>
