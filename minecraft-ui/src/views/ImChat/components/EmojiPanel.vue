<template>
  <div class="emoji-panel">
    <div class="emoji-panel-header">
      <div class="emoji-tabs">
        <button 
          :class="['tab-btn', { active: activeTab === 'system' }]" 
          @click="switchTab('system')"
        >
          系统表情
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'custom' }]" 
          @click="switchTab('custom')"
        >
          我的表情包
        </button>
      </div>
    </div>
    
    <div v-if="activeTab === 'system'" class="emoji-content">
      <div class="category-tabs">
        <button 
          v-for="category in categories" 
          :key="category"
          :class="['category-btn', { active: activeCategory === category }]"
          @click="activeCategory = category"
        >
          {{ category }}
        </button>
      </div>
      <div class="emoji-grid">
        <div 
          v-for="emoji in filteredSystemEmojis" 
          :key="emoji.id" 
          class="emoji-item"
          @click="selectSystemEmoji(emoji)"
        >
          <span class="emoji-unicode">{{ emoji.unicode ? unicodeToEmoji(emoji.unicode) : emoji.emojiCode }}</span>
        </div>
      </div>
    </div>
    
    <div v-else class="emoji-content">
      <div class="emoji-grid">
        <div 
          class="emoji-item upload-item"
          @click="uploadEmoji"
        >
          <div class="upload-icon">
            <Icon name="upload" :size="'24px'" />
          </div>
        </div>
        <div 
          v-for="emoji in customEmojis" 
          :key="emoji.id" 
          class="emoji-item"
          @click="selectCustomEmoji(emoji)"
          @contextmenu.prevent="showContextMenu(emoji, $event)"
        >
          <img :src="getEmojiUrl(emoji.emojiUrl)" :alt="emoji.emojiName" class="emoji-image" />
        </div>
        <div v-if="customEmojis.length === 0" class="empty-emoji">
          <p>暂无表情包，点击上方按钮上传</p>
        </div>
      </div>
    </div>
    
    <input 
      ref="emojiUploadInputRef"
      type="file" 
      accept="image/*" 
      class="hidden-input"
      @change="handleEmojiUpload"
    />
    
    <div v-if="contextMenu.visible" class="context-menu" :style="contextMenuStyle">
      <button class="context-menu-item" @click="deleteEmoji">删除</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import Icon from './Icon.vue'
import { 
  getEmojiList, 
  uploadEmoji as apiUploadEmoji, 
  deleteEmoji as apiDeleteEmoji,
  getGroupedSystemEmojis 
} from '@/api/chat'

const emit = defineEmits(['select-emoji'])

const activeTab = ref('system')
const activeCategory = ref('')
const categories = ref([])
const systemEmojis = ref({})
const customEmojis = ref([])
const contextMenu = ref({
  visible: false,
  emojiId: null,
  x: 0,
  y: 0
})

const emojiUploadInputRef = ref(null)

const contextMenuStyle = ref({
  left: '0px',
  top: '0px'
})

const filteredSystemEmojis = computed(() => {
  if (!activeCategory.value || !systemEmojis.value[activeCategory.value]) {
    return []
  }
  return systemEmojis.value[activeCategory.value]
})

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'custom') {
    loadCustomEmojis()
  }
}

const loadSystemEmojis = async () => {
  try {
    const response = await getGroupedSystemEmojis()
    if (response.code === 200) {
      systemEmojis.value = response.data
      categories.value = Object.keys(response.data)
      if (categories.value.length > 0) {
        activeCategory.value = categories.value[0]
      }
    }
  } catch (error) {
    console.error('加载系统表情失败:', error)
  }
}

const loadCustomEmojis = async () => {
  try {
    const response = await getEmojiList()
    if (response.code === 200) {
      customEmojis.value = response.data
    }
  } catch (error) {
    console.error('加载自定义表情包失败:', error)
  }
}

const unicodeToEmoji = (unicode) => {
  if (!unicode) return ''
  const code = unicode.replace('U+', '0x')
  try {
    return String.fromCodePoint(parseInt(code, 16))
  } catch (e) {
    return unicode
  }
}

const selectSystemEmoji = (emoji) => {
  emit('select-emoji', {
    type: 'system',
    emojiCode: emoji.emojiCode,
    emojiName: emoji.emojiName,
    unicode: emoji.unicode,
    content: unicodeToEmoji(emoji.unicode)
  })
}

const selectCustomEmoji = (emoji) => {
  emit('select-emoji', {
    type: 'custom',
    id: emoji.id,
    emojiUrl: emoji.emojiUrl,
    emojiName: emoji.emojiName,
    content: emoji.emojiUrl
  })
  contextMenu.value.visible = false
}

const showContextMenu = (emoji, event) => {
  contextMenu.value = {
    visible: true,
    emojiId: emoji.id,
    x: event.clientX,
    y: event.clientY
  }
  contextMenuStyle.value = {
    left: `${event.clientX}px`,
    top: `${event.clientY}px`
  }
}

const deleteEmoji = async () => {
  try {
    const response = await apiDeleteEmoji(contextMenu.value.emojiId)
    if (response.code === 200) {
      customEmojis.value = customEmojis.value.filter(e => e.id !== contextMenu.value.emojiId)
    }
  } catch (error) {
    console.error('删除表情包失败:', error)
  }
  contextMenu.value.visible = false
}

const uploadEmoji = () => {
  emojiUploadInputRef.value?.click()
}

const handleEmojiUpload = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  
  try {
    const response = await apiUploadEmoji(file, file.name)
    if (response.code === 200) {
      await loadCustomEmojis()
    }
  } catch (error) {
    console.error('上传表情包失败:', error)
  }
  
  event.target.value = ''
}

const getEmojiUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) {
    return url
  }
  return import.meta.env.VITE_API_BASE_URL + url
}

const handleClickOutside = (e) => {
  if (contextMenu.value.visible && !e.target.closest('.context-menu')) {
    contextMenu.value.visible = false
  }
}

onMounted(() => {
  loadSystemEmojis()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

defineExpose({
  reload: loadCustomEmojis
})
</script>

<style scoped>
.emoji-panel {
  position: absolute;
  bottom: 100%;
  left: 20px;
  right: 20px;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  border-radius: 24px;
  box-shadow: 0 -8px 32px rgba(0, 0, 0, 0.08), 0 -4px 12px rgba(0, 0, 0, 0.04);
  padding: 16px;
  margin-bottom: 12px;
  max-height: 400px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.emoji-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f7;
}

.emoji-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 6px 16px;
  border: none;
  background: transparent;
  color: #6e6e73;
  font-size: 14px;
  cursor: pointer;
  border-radius: 16px;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
  font-weight: 500;
}

.tab-btn.active {
  background: #2997ff;
  color: #ffffff;
}

.tab-btn:hover:not(.active) {
  background: #f5f5f7;
  color: #1d1d1f;
}

.emoji-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.category-tabs {
  display: flex;
  gap: 6px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f7;
  overflow-x: auto;
  margin-bottom: 10px;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.category-btn {
  padding: 4px 12px;
  border: none;
  background: transparent;
  color: #6e6e73;
  font-size: 12px;
  cursor: pointer;
  border-radius: 12px;
  white-space: nowrap;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
  font-weight: 500;
}

.category-btn.active {
  background: rgba(41, 151, 255, 0.1);
  color: #2997ff;
}

.category-btn:hover:not(.active) {
  background: #f5f5f7;
  color: #1d1d1f;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(16, 1fr);
  gap: 8px;
  overflow-y: auto;
  flex: 1;
}

.emoji-item {
  height: 60px;
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s ease;
}

.emoji-item:hover {
  border-color: #2997ff;
  background: #f5f5f7;
}

.emoji-item.upload-item {
  border: 2px dashed #d2d2d6;
  background: #f5f5f7;
}

.emoji-item.upload-item:hover {
  border-color: #2997ff;
  background: rgba(41, 151, 255, 0.08);
}

.upload-icon {
  color: #6e6e73;
  transition: 0.2s ease;
}

.emoji-item.upload-item:hover .upload-icon {
  color: #2997ff;
}

.emoji-unicode {
  font-size: 24px;
}

.emoji-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.empty-emoji {
  grid-column: 2 / -1;
  text-align: center;
  color: #6e6e73;
  padding: 20px;
}

.empty-emoji p {
  margin: 0;
  font-size: 14px;
}

.hidden-input {
  display: none;
}

.context-menu {
  position: fixed;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08), 0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 6px;
  z-index: 1000;
}

.context-menu-item {
  display: block;
  width: 80px;
  padding: 8px 12px;
  border: none;
  background: transparent;
  color: #1d1d1f;
  font-size: 14px;
  cursor: pointer;
  text-align: left;
  border-radius: 8px;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.context-menu-item:hover {
  background: #f5f5f7;
  color: #ff453a;
}
</style>