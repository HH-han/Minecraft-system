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
      <button 
        v-if="activeTab === 'custom'" 
        class="upload-emoji-btn" 
        @click="uploadEmoji"
      >
        <Icon name="plus" :size="'14px'" />
      </button>
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
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.1);
  padding: 12px;
  margin-bottom: 12px;
  max-height: 400px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.emoji-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.emoji-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 6px 16px;
  border: none;
  background: transparent;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  border-radius: 16px;
  transition: all 0.2s;
}

.tab-btn.active {
  background: #409eff;
  color: #fff;
}

.tab-btn:hover:not(.active) {
  background: #f5f5f5;
}

.upload-emoji-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: #409eff;
  border-radius: 50%;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
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
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
  overflow-x: auto;
  margin-bottom: 8px;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.category-btn {
  padding: 4px 12px;
  border: none;
  background: transparent;
  color: #666;
  font-size: 12px;
  cursor: pointer;
  border-radius: 12px;
  white-space: nowrap;
  transition: all 0.2s;
}

.category-btn.active {
  background: #e8f4ff;
  color: #409eff;
}

.category-btn:hover:not(.active) {
  background: #f5f5f5;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
  overflow-y: auto;
  flex: 1;
}

.emoji-item {
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emoji-item:hover {
  border-color: #409eff;
  background: #f8f9fa;
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
  grid-column: 1 / -1;
  text-align: center;
  color: #999;
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
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 4px;
  z-index: 1000;
}

.context-menu-item {
  display: block;
  width: 80px;
  padding: 8px 12px;
  border: none;
  background: transparent;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  text-align: left;
  border-radius: 4px;
}

.context-menu-item:hover {
  background: #f5f5f5;
}
</style>