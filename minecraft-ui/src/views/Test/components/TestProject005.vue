<template>
  <div class="arc-tabs-container">
    <!-- 弧形选项卡头部 -->
    <div class="tabs-header">
      <div class="tabs-wrapper">
        <div 
          v-for="(tab, index) in tabs" 
          :key="tab.value"
          class="tab-item"
          :class="{
            active: activeTab === tab.value,
            'arc-left': index === 0,
            'arc-right': index === tabs.length - 1
          }"
          @click="handleTabClick(tab.value)"
        >
          <div class="tab-inner">
            <div class="tab-icon" v-if="tab.icon">
              <el-icon><component :is="tab.icon" /></el-icon>
            </div>
            <span class="tab-label">{{ tab.label }}</span>
            <div class="tab-badge" v-if="tab.badge">{{ tab.badge }}</div>
          </div>
          <!-- 弧形装饰 -->
          <div class="arc-decoration top"></div>
          <div class="arc-decoration bottom"></div>
        </div>
      </div>
      <!-- 活动指示器 -->
      <div class="active-indicator" :style="indicatorStyle"></div>
    </div>

    <!-- 选项卡内容区域 -->
    <div class="tabs-content">
      <transition 
        name="fade-slide" 
        mode="out-in"
      >
        <div :key="activeTab" class="content-pane">
          <slot :name="activeTab" :tab="getCurrentTab">
            <div class="empty-content">暂无内容</div>
          </slot>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  // 选项卡数据
  tabs: {
    type: Array,
    required: true,
    default: () => []
    // 格式: [{ label: '选项卡1', value: 'tab1', icon: 'Star', badge: 0 }]
  },
  // 默认激活的选项卡
  modelValue: {
    type: [String, Number],
    default: ''
  },
  // 是否自动滚动到激活项
  autoScroll: {
    type: Boolean,
    default: true
  },
  // 弧形样式配置
  arcConfig: {
    type: Object,
    default: () => ({
      radius: '20px',      // 弧形半径
      depth: '12px',       // 弧形深度
      color: '#667eea'     // 弧形颜色
    })
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 当前激活的选项卡
const activeTab = ref(props.modelValue || (props.tabs[0]?.value || ''))

// 获取当前选项卡对象
const getCurrentTab = computed(() => {
  return props.tabs.find(tab => tab.value === activeTab.value)
})

// 计算指示器位置
const indicatorStyle = computed(() => {
  const activeIndex = props.tabs.findIndex(tab => tab.value === activeTab.value)
  if (activeIndex === -1) return {}
  
  // 获取对应选项卡DOM元素
  const tabElements = document.querySelectorAll('.tab-item')
  if (!tabElements.length) return {}
  
  const activeElement = tabElements[activeIndex]
  if (!activeElement) return {}
  
  const rect = activeElement.getBoundingClientRect()
  const containerRect = document.querySelector('.tabs-wrapper')?.getBoundingClientRect()
  
  if (!containerRect) return {}
  
  return {
    left: `${rect.left - containerRect.left}px`,
    width: `${rect.width}px`,
    opacity: 1
  }
})

// 处理选项卡点击
const handleTabClick = (tabValue) => {
  if (activeTab.value === tabValue) return
  
  activeTab.value = tabValue
  emit('update:modelValue', tabValue)
  emit('change', tabValue)
  
  // 自动滚动到可视区域
  if (props.autoScroll) {
    scrollToActiveTab()
  }
}

// 滚动到激活的选项卡
const scrollToActiveTab = () => {
  const activeIndex = props.tabs.findIndex(tab => tab.value === activeTab.value)
  if (activeIndex === -1) return
  
  const tabElements = document.querySelectorAll('.tab-item')
  const activeElement = tabElements[activeIndex]
  const wrapper = document.querySelector('.tabs-wrapper')
  
  if (activeElement && wrapper) {
    const offsetLeft = activeElement.offsetLeft
    const offsetWidth = activeElement.offsetWidth
    const wrapperWidth = wrapper.clientWidth
    
    // 计算需要滚动的距离，使激活项居中
    const scrollLeft = offsetLeft - (wrapperWidth / 2) + (offsetWidth / 2)
    wrapper.scrollTo({
      left: scrollLeft,
      behavior: 'smooth'
    })
  }
}

// 监听tabs变化，重新计算指示器
watch(() => props.tabs, () => {
  setTimeout(() => {
    indicatorStyle.value
  }, 100)
}, { deep: true })

// 监听modelValue外部变化
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal !== activeTab.value) {
    activeTab.value = newVal
    setTimeout(() => {
      scrollToActiveTab()
    }, 100)
  }
})

// 监听窗口大小变化，重新计算指示器
const handleResize = () => {
  indicatorStyle.value
}

onMounted(() => {
  setTimeout(() => {
    scrollToActiveTab()
  }, 100)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 暴露方法供父组件调用
defineExpose({
  setActiveTab: (tabValue) => handleTabClick(tabValue),
  getActiveTab: () => activeTab.value
})
</script>

<style scoped>
.arc-tabs-container {
  width: 100%;
  background: transparent;
}

/* 选项卡头部容器 */
.tabs-header {
  position: relative;
  margin-bottom: 24px;
  padding: 8px 0;
}

/* 选项卡滚动容器 */
.tabs-wrapper {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  scroll-behavior: smooth;
  scrollbar-width: thin;
  position: relative;
  z-index: 2;
  padding: 4px 0 12px;
}

.tabs-wrapper::-webkit-scrollbar {
  height: 3px;
}

.tabs-wrapper::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}

.tabs-wrapper::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 10px;
}

.tabs-wrapper::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.6);
}

/* 单个选项卡 */
.tab-item {
  position: relative;
  flex-shrink: 0;
  padding: 12px 28px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border-radius: 40px 40px 30px 30px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  overflow: hidden;
}

/* 弧形左边框样式 */
.tab-item.arc-left {
  border-radius: 40px 30px 30px 40px;
}

/* 弧形右边框样式 */
.tab-item.arc-right {
  border-radius: 30px 40px 40px 30px;
}

/* 激活状态 */
.tab-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.1));
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px -8px rgba(102, 126, 234, 0.3);
}

/* 弧形装饰 - 顶部 */
.arc-decoration.top {
  position: absolute;
  top: -8px;
  left: 0;
  right: 0;
  height: 12px;
  background: radial-gradient(ellipse at center, rgba(102, 126, 234, 0.4) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

/* 弧形装饰 - 底部 */
.arc-decoration.bottom {
  position: absolute;
  bottom: -8px;
  left: 0;
  right: 0;
  height: 12px;
  background: radial-gradient(ellipse at center, rgba(102, 126, 234, 0.4) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
  transform: rotate(180deg);
}

.tab-item.active .arc-decoration.top,
.tab-item.active .arc-decoration.bottom {
  opacity: 0.6;
}

/* 选项卡内部内容 */
.tab-inner {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  z-index: 2;
}

.tab-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #667eea;
  transition: transform 0.2s;
}

.tab-item.active .tab-icon {
  transform: scale(1.05);
}

.tab-label {
  font-size: 15px;
  font-weight: 500;
  color: #334155;
  white-space: nowrap;
  transition: color 0.2s;
}

.tab-item.active .tab-label {
  color: #667eea;
  font-weight: 600;
}

.tab-badge {
  background: linear-gradient(135deg, #f56565, #ed8936);
  color: white;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 20px;
  margin-left: 4px;
  min-width: 20px;
  text-align: center;
}

/* 活动指示器 */
.active-indicator {
  position: absolute;
  bottom: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
  box-shadow: 0 0 8px rgba(102, 126, 234, 0.5);
  z-index: 3;
}

.active-indicator[style*="opacity: 1"] {
  opacity: 1;
}

/* 内容区域 */
.tabs-content {
  min-height: 200px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(12px);
  border-radius: 28px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s;
}

.content-pane {
  width: 100%;
}

/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.empty-content {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
}

/* 悬浮效果 */
.tab-item:hover:not(.active) {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-1px);
  border-color: rgba(102, 126, 234, 0.2);
}

.tab-item:hover:not(.active) .tab-label {
  color: #667eea;
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  .tab-item {
    background: rgba(30, 41, 59, 0.6);
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .tab-label {
    color: #cbd5e1;
  }
  
  .tab-item.active {
    background: rgba(102, 126, 234, 0.2);
  }
  
  .tab-item.active .tab-label {
    color: #a5b4fc;
  }
  
  .tabs-content {
    background: rgba(30, 41, 59, 0.4);
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .empty-content {
    color: #64748b;
  }
}

/* 响应式 */
@media (max-width: 640px) {
  .tab-item {
    padding: 8px 20px;
  }
  
  .tab-label {
    font-size: 13px;
  }
  
  .tab-icon {
    font-size: 16px;
  }
  
  .tabs-content {
    padding: 16px;
  }
}
</style>