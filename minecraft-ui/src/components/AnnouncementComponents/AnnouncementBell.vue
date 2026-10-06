<template>
  <el-popover
    v-model:visible="panelVisible"
    placement="bottom-end"
    :width="320"
    trigger="click"
    popper-class="ann-bell-popover"
  >
    <template #reference>
      <el-badge :value="store.unreadCount" :hidden="!store.unreadCount" :max="99">
        <button class="bell-btn" title="系统公告">
          <svg viewBox="0 0 1024 1024" width="20" height="20" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M512 64a42.666667 42.666667 0 0 1 42.666667 42.666667v25.173333A298.666667 298.666667 0 0 1 810.666667 426.666667v149.333333l64 106.666667a42.666667 42.666667 0 0 1-36.565334 64.64H185.898667a42.666667 42.666667 0 0 1-36.565334-64.64l64-106.666667v-149.333333a298.666667 298.666667 0 0 1 256-294.826667V106.666667A42.666667 42.666667 0 0 1 512 64z m96 682.666667h-192a96 96 0 0 0 192 0z"
              fill="currentColor"
            />
          </svg>
        </button>
      </el-badge>
    </template>

    <div class="ann-panel">
      <div class="ann-panel__head">
        <span class="ann-panel__title">系统公告</span>
        <span class="ann-panel__more" @click="goList">查看全部</span>
      </div>

      <div v-if="displayList.length" class="ann-panel__list">
        <div
          v-for="item in displayList"
          :key="item.id"
          class="ann-item"
          :class="{ 'is-top': item.isTop === 1 }"
          @click="goDetail(item)"
        >
          <span
            v-if="!store.readIds.has(item.id)"
            class="ann-item__dot"
            title="未读"
          ></span>
          <div class="ann-item__main">
            <div class="ann-item__title">{{ item.title }}</div>
            <div class="ann-item__meta">
              <span class="ann-item__tag" :class="`ann-item__tag--t${item.type}`">
                {{ typeText(item.type) }}
              </span>
              <span v-if="item.level === 3" class="ann-item__urgent">紧急</span>
              <span class="ann-item__time">{{ formatTime(item.publishTime) }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="ann-panel__empty">暂无公告</div>
    </div>
  </el-popover>

  <AnnouncementDetailModal v-model:visible="detailVisible" :id="detailId" />
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from './AnnouncementDetailModal.vue'

const store = useAnnouncementStore()
const router = useRouter()

const panelVisible = ref(false)
const detailVisible = ref(false)
const detailId = ref(null)

const TYPE_TEXTS = { 1: '系统', 2: '活动', 3: '维护', 4: '版本' }
const displayList = computed(() => store.activeList.slice(0, 8))

function typeText(type) {
  return TYPE_TEXTS[Number(type)] || '公告'
}

function formatTime(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}

// 点击条目：关闭面板并以模态框查看详情
function goDetail(item) {
  panelVisible.value = false
  detailId.value = item.id
  detailVisible.value = true
}

function goList() {
  panelVisible.value = false
  router.push('/announcement')
}

onMounted(() => {
  store.fetchUnread()
  store.fetchActive()
})
</script>

<style scoped>
.bell-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.55),
    rgba(178, 178, 178, 0.45)
  );
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow:
    inset 0 1px 2px rgba(255, 255, 255, 0.65),
    0 2px 8px rgba(31, 38, 135, 0.08);
  color: #5b6472;
  cursor: pointer;
  transition:
    color 0.25s ease,
    background 0.25s ease,
    box-shadow 0.25s ease,
    transform 0.25s ease;
}
.bell-btn svg {
  transform-origin: 50% 12%;
  transition: color 0.25s ease;
}
.bell-btn:hover {
  color: #1d1d1f;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.95),
    rgba(233, 237, 244, 0.85)
  );
  box-shadow:
    inset 0 1px 2px rgba(255, 255, 255, 0.8),
    0 6px 18px rgba(31, 38, 135, 0.18);
  transform: translateY(-1px);
}
/* hover 摇铃动画 */
.bell-btn:hover svg {
  animation: bell-ring 0.9s ease-in-out;
}
.bell-btn:active {
  transform: translateY(0) scale(0.94);
}
.bell-btn:focus-visible {
  outline: 2px solid rgba(76, 110, 245, 0.55);
  outline-offset: 2px;
}

@keyframes bell-ring {
  0% {
    transform: rotate(0);
  }
  15% {
    transform: rotate(14deg);
  }
  35% {
    transform: rotate(-11deg);
  }
  55% {
    transform: rotate(7deg);
  }
  75% {
    transform: rotate(-4deg);
  }
  100% {
    transform: rotate(0);
  }
}

.ann-panel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2px 4px 10px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}
.ann-panel__title {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}
.ann-panel__more {
  font-size: 12px;
  color: #4c6ef5;
  cursor: pointer;
}
.ann-panel__more:hover {
  text-decoration: underline;
}

.ann-panel__list {
  max-height: 320px;
  margin-top: 4px;
  overflow-y: auto;
}

.ann-item {
  position: relative;
  padding: 10px 8px 10px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s ease;
}
.ann-item + .ann-item {
  margin-top: 2px;
}
.ann-item:hover {
  background: rgba(76, 110, 245, 0.06);
}
.ann-item.is-top .ann-item__title {
  font-weight: 600;
}

.ann-item__dot {
  position: absolute;
  top: 16px;
  left: 4px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f5554a;
}

.ann-item__title {
  font-size: 13px;
  color: #333;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.ann-item__meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.ann-item__tag {
  font-size: 11px;
  padding: 0 6px;
  line-height: 18px;
  border-radius: 4px;
  background: #f0f2f5;
  color: #666;
}
.ann-item__tag--t1 {
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
}
.ann-item__tag--t2 {
  background: rgba(249, 115, 22, 0.1);
  color: #ea580c;
}
.ann-item__tag--t3 {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}
.ann-item__tag--t4 {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.ann-item__urgent {
  font-size: 11px;
  color: #dc2626;
  font-weight: 600;
}

.ann-item__time {
  font-size: 11px;
  color: #999;
  margin-left: auto;
}

.ann-panel__empty {
  padding: 32px 0;
  text-align: center;
  font-size: 13px;
  color: #999;
}
</style>

<style>
/* popover 内容非 scoped，需全局样式（popper 挂在 body 下） */
.ann-bell-popover {
  border-radius: 14px !important;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.12) !important;
}
</style>
