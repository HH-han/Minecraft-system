<template>
  <el-dialog
    v-model="visible"
    :title="current?.title"
    width="520px"
    class="glass-dialog"
    :close-on-click-modal="false"
    @closed="handleClose"
  >
    <div class="popup-meta" v-if="current">
      <el-tag :type="levelTagType" size="small">{{ levelText }}</el-tag>
      <span class="popup-time">{{ formatTime(current.publishTime) }}</span>
    </div>
    <div class="popup-body" v-html="current?.content || current?.summary" />
    <template #footer>
      <el-button @click="handleClose">我知道了</el-button>
      <el-button type="primary" @click="goDetail">查看详情</el-button>
    </template>
  </el-dialog>

  <AnnouncementDetailModal
    :visible="detailVisible"
    :id="detailId"
    @update:visible="onDetailClose"
  />
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from './AnnouncementDetailModal.vue'

const store = useAnnouncementStore()
const current = ref(null)
const visible = ref(false)
const detailVisible = ref(false)
const detailId = ref(null)
// 详情模态框打开期间挂起弹窗队列，避免弹窗叠加在详情上
const suppressQueue = ref(false)

const LEVEL_MAP = { 1: '普通', 2: '重要', 3: '紧急' }
const levelText = computed(() => LEVEL_MAP[current.value?.level] || '普通')
const levelTagType = computed(() =>
  current.value?.level === 3 ? 'danger' : current.value?.level === 2 ? 'warning' : 'info'
)

watch(
  () => store.popupQueue,
  (queue) => {
    if (suppressQueue.value) return
    if (!visible.value && queue.length) {
      current.value = queue[0]
      visible.value = true
    }
  },
  { immediate: true, deep: true }
)

async function handleClose() {
  if (current.value) {
    const id = current.value.id
    visible.value = false
    current.value = null
    await store.read(id)
  } else {
    visible.value = false
  }
}

function goDetail() {
  const id = current.value?.id
  if (!id) return
  suppressQueue.value = true
  detailId.value = id
  detailVisible.value = true
  handleClose()
}

// 详情模态框关闭后：恢复弹窗队列，继续弹下一条
function onDetailClose(v) {
  detailVisible.value = v
  if (v) return
  suppressQueue.value = false
  if (!visible.value && store.popupQueue.length) {
    current.value = store.popupQueue[0]
    visible.value = true
  }
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  return isNaN(d.getTime()) ? '' : d.toLocaleString('zh-CN', { hour12: false })
}
</script>

<style scoped>
.popup-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}
.popup-time {
  font-size: 12px;
  color: #999;
}
.popup-body {
  line-height: 1.8;
  color: #333;
  max-height: 50vh;
  overflow-y: auto;
  word-break: break-word;
}
.popup-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
</style>

<style>
/* 弹窗玻璃拟态（非 scoped 以作用于 el-dialog） */
.glass-dialog {
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
}
@media (max-width: 767px) {
  .glass-dialog {
    width: 90% !important;
  }
}
</style>
