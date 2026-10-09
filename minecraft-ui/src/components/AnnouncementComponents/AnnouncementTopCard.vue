<template>
  <transition name="top-card" appear>
    <div v-if="store.topList.length" class="top-card-layer">
      <div
        v-for="item in store.topList"
        :key="item.id"
        class="top-card"
        @click="goDetail(item.id)"
      >
        <div class="card-head">
          <el-tag :type="typeTagType(item.type)" size="small">{{ typeText(item.type) }}</el-tag>
          <el-tag v-if="item.level > 1" :type="item.level === 3 ? 'danger' : 'warning'" size="small" effect="plain">
            {{ levelText(item.level) }}
          </el-tag>
          <span v-if="!item.isRead" class="unread-dot" title="未读"></span>
          <el-icon class="close-btn" title="本次会话不再显示" @click.stop="store.closeTop(item.id)">
            <Close />
          </el-icon>
        </div>
        <h4 class="card-title">
          <span class="top-badge">置顶</span>
          {{ item.title }}
        </h4>
        <p class="card-summary">{{ item.summary || stripHtml(item.content) }}</p>
      </div>
    </div>
  </transition>

  <AnnouncementDetailModal v-model:visible="detailVisible" :id="detailId" />
</template>

<script setup>
import { ref } from 'vue'
import { Close } from '@element-plus/icons-vue'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from './AnnouncementDetailModal.vue'

const store = useAnnouncementStore()
const detailVisible = ref(false)
const detailId = ref(null)

const TYPE_OPTIONS = [
  { value: 1, label: '系统公告' },
  { value: 2, label: '活动通知' },
  { value: 3, label: '维护通知' },
  { value: 4, label: '版本更新' }
]

function typeText(type) {
  return TYPE_OPTIONS.find((t) => t.value === type)?.label || '公告'
}
function typeTagType(type) {
  return { 1: 'primary', 2: 'warning', 3: 'danger', 4: 'success' }[type] || 'info'
}
function levelText(level) {
  return { 2: '重要', 3: '紧急' }[level] || '普通'
}
function stripHtml(html) {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').slice(0, 100)
}

function goDetail(id) {
  detailId.value = id
  detailVisible.value = true
}
</script>

<style scoped>
/* 悬浮层：固定在页面最顶层（高于 el-dialog 动态 z-index） */
.top-card-layer {
  position: fixed;
  top: 32px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 4000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: min(720px, calc(100vw - 32px));
  pointer-events: none;
}
.top-card {
  pointer-events: auto;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 14px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.18);
  padding: 14px 18px;
  cursor: pointer;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
.top-card:hover {
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.28);
  transform: translateY(-2px);
}
.card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f56c6c;
}
.close-btn {
  margin-left: auto;
  color: #999;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.2s ease;
}
.close-btn:hover {
  color: #666;
}
.card-title {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  display: flex;
  align-items: center;
  gap: 6px;
}
.top-badge {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 400;
  color: #e6a23c;
  border: 1px solid #e6a23c;
  border-radius: 4px;
  padding: 0 4px;
  line-height: 16px;
}
.card-summary {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 进出场动画 */
.top-card-enter-active,
.top-card-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.top-card-enter-from,
.top-card-leave-to {
  opacity: 0;
  transform: translate(-50%, -12px);
}

@media (max-width: 767px) {
  .top-card-layer {
    top: 8px;
  }
  .card-summary {
    white-space: normal;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
}
</style>
