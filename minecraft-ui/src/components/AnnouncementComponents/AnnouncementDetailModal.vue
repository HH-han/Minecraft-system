<template>
  <el-dialog
    :model-value="visible"
    width="640px"
    align-center
    destroy-on-close
    append-to-body
    class="ann-detail-dialog"
    @update:model-value="(v) => emit('update:visible', v)"
  >
    <template #header>
      <div class="ann-modal__head">
        <div class="ann-modal__tags">
          <el-tag :type="typeTagType(detail?.type)" size="small">
            {{ typeText(detail?.type) }}
          </el-tag>
          <el-tag
            v-if="detail?.level > 1"
            :type="detail.level === 3 ? 'danger' : 'warning'"
            size="small"
            effect="plain"
          >
            {{ levelText(detail.level) }}
          </el-tag>
          <span v-if="detail?.isTop" class="ann-modal__top">置顶</span>
        </div>
        <h3 class="ann-modal__title">{{ detail?.title || '公告详情' }}</h3>
        <div class="ann-modal__meta">
          <span>{{ detail?.creatorName || '系统' }}</span>
          <span>{{ formatTime(detail?.publishTime) }}</span>
          <span>{{ detail?.viewCount || 0 }} 次浏览</span>
        </div>
      </div>
    </template>

    <div v-if="loading" class="ann-modal__body">
      <el-skeleton animated :rows="6" />
    </div>

    <div v-else-if="detail" class="ann-modal__body" v-html="detail.content || detail.summary" />

    <el-empty v-else description="公告不存在或已下架" :image-size="90" />

    <template #footer>
      <el-button @click="emit('update:visible', false)">我知道了</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getAnnouncementDetail } from '@/api/announcement'
import { useAnnouncementStore } from '@/stores/announcementStore'

const props = defineProps({
  visible: { type: Boolean, default: false },
  id: { type: [Number, String], default: null }
})
const emit = defineEmits(['update:visible'])

const store = useAnnouncementStore()
const loading = ref(false)
const detail = ref(null)

watch(
  () => [props.visible, props.id],
  async ([visible, id]) => {
    if (!visible || id == null) return
    loading.value = true
    detail.value = null
    try {
      const res = await getAnnouncementDetail(id)
      if (res.code === 200) {
        detail.value = res.data
        // 打开即视为已读
        if (!store.readIds.has(Number(id))) {
          await store.read(id)
        }
      }
    } finally {
      loading.value = false
    }
  },
  { immediate: true }
)

function typeText(type) {
  return { 1: '系统公告', 2: '活动通知', 3: '维护通知', 4: '版本更新' }[Number(type)] || '公告'
}
function typeTagType(type) {
  return { 1: 'primary', 2: 'warning', 3: 'danger', 4: 'success' }[Number(type)] || 'info'
}
function levelText(level) {
  return { 2: '重要', 3: '紧急' }[Number(level)] || '普通'
}
function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  return isNaN(d.getTime()) ? '' : d.toLocaleString('zh-CN', { hour12: false })
}
</script>

<style scoped>
.ann-modal__head {
  padding-right: 24px;
}
.ann-modal__tags {
  display: flex;
  align-items: center;
  gap: 8px;
}
.ann-modal__top {
  padding: 1px 6px;
  border-radius: 4px;
  background: linear-gradient(135deg, #f97316, #ef4444);
  color: #fff;
  font-size: 12px;
  line-height: 18px;
}
.ann-modal__title {
  margin: 10px 0 8px;
  font-size: 19px;
  line-height: 1.4;
  color: #1d1d1f;
}
.ann-modal__meta {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: #999;
}
.ann-modal__body {
  max-height: 52vh;
  overflow-y: auto;
  line-height: 1.9;
  color: #333;
  font-size: 14px;
  word-break: break-word;
}
.ann-modal__body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
.ann-modal__body :deep(table) {
  border-collapse: collapse;
}
.ann-modal__body :deep(td),
.ann-modal__body :deep(th) {
  border: 1px solid #e5e5e5;
  padding: 4px 8px;
}
</style>

<style>
.ann-detail-dialog {
  border-radius: 14px !important;
}
.ann-detail-dialog .el-dialog__header {
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}
</style>
