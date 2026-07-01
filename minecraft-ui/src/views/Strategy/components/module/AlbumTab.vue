<template>
  <div class="album-tab">
    <div class="tab-actions">
      <button class="btn-small" @click="triggerUpload">上传照片</button>
      <input
        type="file"
        ref="fileInput"
        accept="image/*"
        multiple
        style="display: none"
        @change="handleUpload"
      />
    </div>
    <div class="album-grid">
      <div v-for="photo in photos" :key="photo.id" class="photo-item">
        <img :src="photo.url" :alt="photo.description" />
        <button v-if="isAdmin" @click="$emit('delete', photo.id)" class="photo-delete">×</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  photos: {
    type: Array,
    default: () => []
  },
  isAdmin: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['upload', 'delete'])

const fileInput = ref(null)

function triggerUpload() {
  fileInput.value?.click()
}

function handleUpload(event) {
  const files = event.target.files
  if (!files.length) return
  emit('upload', files)
}
</script>

<style scoped>
.album-tab {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.tab-actions {
  display: flex;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-20);
}

.tab-actions .btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--spacing-12);
}

.photo-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: var(--radius-sm);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.photo-item:hover {
  transform: scale(1.02);
  box-shadow: var(--shadow-md);
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.photo-item:hover img {
  transform: scale(1.05);
}

.photo-delete {
  position: absolute;
  top: var(--spacing-8);
  right: var(--spacing-8);
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  color: #fff;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  opacity: 0;
  transition: all var(--transition-fast);
}

.photo-item:hover .photo-delete {
  opacity: 1;
}

.photo-delete:hover {
  background: var(--color-danger);
}

@media (max-width: 767px) {
  .album-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  }
}
</style>