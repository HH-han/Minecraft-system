<template>
  <el-dialog
    :model-value="modelValue"
    width="660px"
    align-center
    class="as-detail-dialog"
    @update:model-value="handleVisible"
  >
    <template #header>
      <div class="as-detail-head">
        <span class="as-detail-head__badge"><el-icon :size="18"><Document /></el-icon></span>
        <div class="as-detail-head__text">
          <h3 class="as-detail-head__title">{{ $t('afterSale.detailDialog.title') }}</h3>
          <p class="as-detail-head__sub">{{ $t('afterSale.detailDialog.subtitle') }}</p>
        </div>
      </div>
    </template>

    <div class="as-detail">
      <el-skeleton v-if="loading" :rows="6" animated />
      <template v-else-if="detail">
        <div class="as-detail__head">
          <div>
            <div class="as-detail__no">{{ detail.afterSaleNo }}</div>
            <div class="as-detail__type">{{ typeText(detail) }}</div>
          </div>
          <span class="as-detail__status" :class="`as-detail__status--${statusKey(detail.status)}`">
            {{ statusText(detail) }}
          </span>
        </div>

        <el-descriptions :column="2" size="small" class="as-detail__desc">
          <el-descriptions-item :label="$t('afterSale.detailDialog.item')">
            {{ detail.itemName }} ×{{ detail.quantity }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('afterSale.detailDialog.type')">
            {{ typeText(detail) }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('afterSale.detailDialog.refundAmount')">
            ¥{{ detail.refundAmount ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('afterSale.detailDialog.actualAmount')">
            ¥{{ detail.actualAmount ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('afterSale.detailDialog.reason')" :span="2">
            {{ detail.reason || '-' }}
          </el-descriptions-item>
          <el-descriptions-item
            v-if="detail.refundNo"
            :label="$t('afterSale.detailDialog.refundNo')"
            :span="2"
          >
            {{ detail.refundNo }}
          </el-descriptions-item>
          <el-descriptions-item
            v-if="detail.auditRemark"
            :label="$t('afterSale.detailDialog.auditRemark')"
            :span="2"
          >
            {{ detail.auditRemark }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="evidence.length" class="as-detail__block">
          <div class="as-detail__block-title">{{ $t('afterSale.detailDialog.evidence') }}</div>
          <div class="as-detail__evidence">
            <el-image
              v-for="(url, i) in evidence"
              :key="i"
              :src="url"
              :preview-src-list="evidence"
              fit="cover"
              class="as-detail__evidence-img"
            />
          </div>
        </div>

        <!-- 待买家退货：填写退货物流 -->
        <div v-if="detail.status === 3" class="as-detail__block">
          <div class="as-detail__block-title">
            {{ $t('afterSale.detailDialog.returnLogistics') }}
          </div>
          <div class="as-return">
            <el-input
              v-model="returnForm.returnCarrier"
              :placeholder="$t('afterSale.detailDialog.returnCarrier')"
            />
            <el-input
              v-model="returnForm.returnTracking"
              :placeholder="$t('afterSale.detailDialog.returnTracking')"
            />
            <el-button type="primary" round :loading="returning" @click="submitReturnInfo">
              {{ $t('afterSale.detailDialog.submit') }}
            </el-button>
          </div>
        </div>

        <div class="as-detail__block">
          <div class="as-detail__block-title">{{ $t('afterSale.detailDialog.messages') }}</div>
          <el-timeline v-if="(detail.messages || []).length" class="as-detail__messages">
            <el-timeline-item
              v-for="(m, i) in detail.messages"
              :key="i"
              :timestamp="formatTime(m.createTime)"
            >
              <div class="as-msg">
                <div class="as-msg__sender">{{ senderText(m.senderType) }}</div>
                <div class="as-msg__content">{{ m.content }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          <div v-else class="as-detail__empty">{{ $t('afterSale.detailDialog.noMessages') }}</div>

          <div class="as-msg__input">
            <el-input
              v-model="messageContent"
              type="textarea"
              :rows="2"
              resize="none"
              :placeholder="$t('afterSale.detailDialog.messagePlaceholder')"
            />
            <div class="as-msg__actions">
              <el-button
                type="primary"
                round
                :loading="sending"
                class="as-msg__send"
                @click="sendMessage"
              >
                {{ $t('afterSale.detailDialog.send') }}
              </el-button>
            </div>
          </div>
        </div>
      </template>
    </div>

    <template #footer>
      <el-button
        v-if="detail && (detail.status === 0 || detail.status === 1)"
        type="danger"
        plain
        round
        @click="handleCancel"
      >
        {{ $t('afterSale.detailDialog.cancelApply') }}
      </el-button>
      <el-button round @click="handleVisible(false)">
        {{ $t('afterSale.detailDialog.close') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelAfterSale,
  getAfterSaleDetail,
  sendAfterSaleMessage,
  submitReturn
} from '@/api/afterSale.js'
import { Document } from '@element-plus/icons-vue'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  id: { type: [Number, String], default: null }
})
const emit = defineEmits(['update:modelValue', 'refresh'])

const { t, te } = useI18n()

const loading = ref(false)
const detail = ref(null)
const messageContent = ref('')
const sending = ref(false)
const returning = ref(false)
const returnForm = reactive({ returnCarrier: '', returnTracking: '' })

// 0-待审核 1-审核通过 2-已驳回 3-待买家退货 4-待商家收货 5-退款中 6-已完成 7-已关闭
const statusKey = (status) => {
  if (status === 6) return 'done'
  if (status === 2 || status === 7) return 'idle'
  if (status === 3 || status === 4 || status === 5) return 'warn'
  return 'active'
}

const statusText = (row) => {
  const key = `afterSale.status.${row.status}`
  return te(key) ? t(key) : row.statusDesc || '-'
}

const typeText = (row) => {
  const key = `afterSale.type.${row.type}`
  return te(key) ? t(key) : row.typeDesc || row.type
}

const formatTime = (v) => (v ? String(v).replace('T', ' ') : '')

const senderText = (type) => {
  if (type === 2) return t('afterSale.detailDialog.senderMerchant')
  if (type === 3) return t('afterSale.detailDialog.senderSystem')
  return t('afterSale.detailDialog.senderMe')
}

const evidence = computed(() => {
  const raw = detail.value?.evidenceUrls
  if (!raw) return []
  try {
    const arr = JSON.parse(raw)
    if (Array.isArray(arr)) return arr
  } catch {
    // 非 JSON，按逗号分隔
  }
  return String(raw)
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)
})

const handleVisible = (v) => emit('update:modelValue', v)

const fetchDetail = async () => {
  if (!props.id) return
  loading.value = true
  try {
    const res = await getAfterSaleDetail(props.id)
    detail.value = res?.data ?? null
  } catch {
    // 拦截器已提示
  } finally {
    loading.value = false
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchDetail)

watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      messageContent.value = ''
      fetchDetail()
    }
  }
)

const submitReturnInfo = async () => {
  if (!returnForm.returnCarrier || !returnForm.returnTracking) {
    ElMessage.warning(t('afterSale.detailDialog.returnIncomplete'))
    return
  }
  returning.value = true
  try {
    await submitReturn(props.id, { ...returnForm })
    ElMessage.success(t('afterSale.detailDialog.returnSuccess'))
    await fetchDetail()
    emit('refresh')
  } finally {
    returning.value = false
  }
}

const sendMessage = async () => {
  if (!messageContent.value.trim()) return
  sending.value = true
  try {
    await sendAfterSaleMessage(props.id, { content: messageContent.value.trim() })
    messageContent.value = ''
    await fetchDetail()
  } finally {
    sending.value = false
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm(
      t('afterSale.cancelConfirmText'),
      t('afterSale.cancelConfirmTitle'),
      { type: 'warning' }
    )
  } catch {
    return
  }
  await cancelAfterSale(props.id)
  ElMessage.success(t('afterSale.cancelSuccess'))
  emit('update:modelValue', false)
  emit('refresh')
}
</script>

<style scoped>
.as-detail {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-line: var(--theme-hairline);
  color: var(--tn-ink);
}

/* 自定义弹窗头部 */
.as-detail-head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.as-detail-head__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 13px;
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.as-detail-head__text {
  min-width: 0;
}

.as-detail-head__title {
  margin: 0;
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.as-detail-head__sub {
  margin: 3px 0 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

/* head */
.as-detail__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 16px 18px;
  border-radius: 16px;
  background:
    linear-gradient(135deg, var(--theme-primary-soft), transparent 68%),
    var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.as-detail__no {
  font-size: 15px;
  font-weight: 800;
  letter-spacing: -0.01em;
  font-variant-numeric: tabular-nums;
}

.as-detail__type {
  margin-top: 3px;
  font-size: 12px;
  color: var(--tn-ink-3);
}

.as-detail__status {
  padding: 5px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid transparent;
  white-space: nowrap;
}
.as-detail__status--active {
  background: var(--theme-primary-soft);
  border-color: color-mix(in srgb, var(--theme-primary) 22%, transparent);
  color: var(--theme-primary);
}
.as-detail__status--warn {
  background: var(--theme-warning-soft);
  border-color: color-mix(in srgb, var(--theme-warning) 24%, transparent);
  color: var(--theme-warning);
}
.as-detail__status--done {
  background: var(--theme-success-soft);
  border-color: color-mix(in srgb, var(--theme-success) 24%, transparent);
  color: var(--theme-success);
}
.as-detail__status--idle {
  background: var(--theme-bg-surface-2);
  border-color: var(--theme-hairline);
  color: var(--theme-text-2);
}

/* descriptions */
.as-detail__desc {
  margin-top: 18px;
  padding: 4px 14px;
  border-radius: 16px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.as-detail__desc :deep(.el-descriptions__cell) {
  padding-bottom: 12px;
}

.as-detail__desc :deep(.el-descriptions__label) {
  color: var(--tn-ink-3);
  font-weight: 600;
}

.as-detail__desc :deep(.el-descriptions__content) {
  color: var(--tn-ink);
}

/* blocks */
.as-detail__block {
  margin-top: 22px;
}

.as-detail__block-title {
  display: flex;
  align-items: center;
  gap: 9px;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 12px;
}

.as-detail__block-title::before {
  content: '';
  width: 4px;
  height: 15px;
  border-radius: 3px;
  background: var(--theme-gradient);
}

.as-detail__evidence {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.as-detail__evidence-img {
  width: 76px;
  height: 76px;
  border-radius: 12px;
  border: 1px solid var(--tn-line);
  cursor: zoom-in;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.as-detail__evidence-img:hover {
  transform: translateY(-2px);
  box-shadow: var(--theme-shadow-md);
}

.as-return {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 14px;
  border-radius: 14px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.as-return :deep(.el-input) {
  flex: 1;
  min-width: 140px;
}

.as-return :deep(.el-input__wrapper) {
  border-radius: 10px;
  background: var(--theme-bg-surface);
  box-shadow: inset 0 0 0 1px var(--theme-hairline);
}

.as-return :deep(.el-input__wrapper.is-focus) {
  box-shadow: inset 0 0 0 1px var(--theme-primary);
}

/* messages */
.as-detail__messages {
  padding-left: 4px;
}

.as-detail__messages :deep(.el-timeline-item__tail) {
  border-left-color: var(--theme-hairline);
}

.as-detail__messages :deep(.el-timeline-item__node) {
  background: var(--theme-primary);
}

.as-detail__messages :deep(.el-timeline-item__timestamp) {
  color: var(--tn-ink-3);
  font-weight: 600;
  font-size: 11.5px;
}

.as-msg {
  padding: 10px 14px;
  border-radius: 12px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.as-msg__sender {
  font-size: 11.5px;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: var(--theme-primary);
}

.as-msg__content {
  margin-top: 3px;
  font-size: 13.5px;
  line-height: 1.65;
  color: var(--tn-ink);
  word-break: break-word;
}

.as-detail__empty {
  padding: 18px;
  text-align: center;
  font-size: 13px;
  color: var(--tn-ink-3);
  border-radius: 12px;
  background: var(--theme-bg-surface-2);
}

.as-msg__input {
  margin-top: 16px;
  padding: 12px;
  border-radius: 14px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.as-msg__input :deep(.el-textarea__inner) {
  padding: 10px 12px;
  border-radius: 10px;
  background: var(--theme-bg-surface);
  box-shadow: inset 0 0 0 1px var(--theme-hairline);
  font-size: 13.5px;
  line-height: 1.65;
  transition: box-shadow 0.2s ease;
}

.as-msg__input :deep(.el-textarea__inner:focus) {
  background: var(--theme-bg-surface);
  box-shadow: inset 0 0 0 1px var(--theme-primary);
}

.as-msg__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.as-msg__send {
  min-width: 88px;
  border: none;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-weight: 600;
  box-shadow: 0 8px 18px -12px color-mix(in srgb, var(--theme-primary) 85%, transparent);
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.as-msg__send:hover {
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  opacity: 0.94;
  transform: translateY(-1px);
}
</style>

<style>
/* dialog 外壳（teleport 到 body，需全局作用域） */
.as-detail-dialog .el-dialog,
.as-detail-dialog.el-dialog {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--theme-shadow-lg);
}

.as-detail-dialog .el-dialog::before,
.as-detail-dialog.el-dialog::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--theme-gradient);
  opacity: 0.9;
  z-index: 1;
}

.as-detail-dialog .el-dialog__header,
.as-detail-dialog.el-dialog .el-dialog__header {
  padding: 22px 24px 16px;
  margin: 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.as-detail-dialog .el-dialog__title,
.as-detail-dialog.el-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.as-detail-dialog .el-dialog__headerbtn {
  top: 18px;
  right: 18px;
}

.as-detail-dialog .el-dialog__body,
.as-detail-dialog.el-dialog .el-dialog__body {
  padding: 20px 24px 4px;
  max-height: 70vh;
  overflow-y: auto;
}

.as-detail-dialog .el-dialog__footer,
.as-detail-dialog.el-dialog .el-dialog__footer {
  padding: 16px 24px 22px;
  border-top: 1px solid var(--theme-hairline);
}
</style>
