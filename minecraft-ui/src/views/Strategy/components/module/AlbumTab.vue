<template>
  <div class="album-tab">
    <div class="tab-toolbar">
      <span class="toolbar-title">
        {{ t('strategy.detailTabs.album') }}
        <em>{{ photos.length }}</em>
      </span>
      <button class="btn-primary" type="button" @click="triggerUpload">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M12 16V4M7 9l5-5 5 5" />
          <path d="M5 16v3a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-3" />
        </svg>
        {{ t('strategy.album.upload') }}
      </button>
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        multiple
        style="display: none"
        @change="handleUpload"
      />
    </div>

    <div v-if="photos.length === 0" class="tab-empty">
      <p>{{ t('strategy.album.empty') }}</p>
    </div>

    <div v-else class="album-grid">
      <div v-for="photo in photos" :key="photo.id" class="photo-item">
        <img :src="photo.url" :alt="photo.description || ''" loading="lazy" />
        <div class="photo-overlay">
          <span v-if="photo.description" class="photo-caption">{{ photo.description }}</span>
        </div>
        <button v-if="isAdmin" class="photo-delete" type="button" @click="$emit('delete', photo.id)">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M6 6l12 12M18 6L6 18" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

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

const { t } = useI18n()

const fileInput = ref(null)

function triggerUpload() {
  fileInput.value?.click()
}

function handleUpload(event) {
  const files = event.target.files
  if (!files.length) return
  emit('upload', files)
  event.target.value = ''
}
</script>

<style scoped>
.album-tab {
  animation: fade-in var(--transition-normal);
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.tab-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.toolbar-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.toolbar-title em {
  padding: 1px 8px;
  border-radius: var(--radius-full);
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  font-size: 11px;
  font-style: normal;
  font-weight: 600;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: var(--theme-primary-text);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.btn-primary svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-primary:active {
  transform: scale(0.97);
}

.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 10px;
}

.photo-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: var(--radius-sm);
  overflow: hidden;
  cursor: zoom-in;
  background: var(--color-bg-secondary);
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s var(--theme-ease);
}

.photo-item:hover img {
  transform: scale(1.06);
}

.photo-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: flex-end;
  padding: 10px;
  background: linear-gradient(180deg, transparent 55%, rgba(0, 0, 0, 0.55) 100%);
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

.photo-caption {
  font-size: 11px;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-delete {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background: rgba(12, 15, 20, 0.55);
  color: #fff;
  cursor: pointer;
  opacity: 0;
  backdrop-filter: blur(8px);
  transition: opacity var(--transition-fast), background var(--transition-fast);
}

.photo-item:hover .photo-delete {
  opacity: 1;
}

.photo-delete svg {
  width: 12px;
  height: 12px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
}

.photo-delete:hover {
  background: var(--color-danger);
}

.tab-empty {
  padding: 44px;
  text-align: center;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-size: 13px;
}

@media (max-width: 767px) {
  .album-grid {
    grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  }
}
</style>
