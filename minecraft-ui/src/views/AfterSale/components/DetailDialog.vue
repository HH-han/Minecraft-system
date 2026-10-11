<template>
  <el-dialog
    :model-value="modelValue"
    title="售后详情"
    width="660px"
    align-center
    class="as-detail-dialog"
    @update:model-value="handleVisible"
  >
    <div class="as-detail">
      <el-skeleton v-if="loading" :rows="6" animated />
      <template v-else-if="detail">
        <div class="as-detail__head">
          <div>
            <div class="as-detail__no">{{ detail.afterSaleNo }}</div>
            <div class="as-detail__type">{{ detail.typeDesc }}</div>
          </div>
          <span class="as-detail__status" :class="`as-detail__status--${statusKey(detail.status)}`">
            {{ detail.statusDesc }}
          </span>
        </div>

        <el-descriptions :column="2" size="small" class="as-detail__desc">
          <el-descriptions-item label="商品">
            {{ detail.itemName }} ×{{ detail.quantity }}
          </el-descriptions-item>
          <el-descriptions-item label="类型">{{ detail.typeDesc }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">
            ¥{{ detail.refundAmount ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="实际退款">
            ¥{{ detail.actualAmount ?? '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="问题描述" :span="2">
            {{ detail.reason || '-' }}
          </el-descriptions-item>
          <el-descriptions-item v-if="detail.refundNo" label="退款流水号" :span="2">
            {{ detail.refundNo }}
          </el-descriptions-item>
          <el-descriptions-item v-if="detail.auditRemark" label="审核备注" :span="2">
            {{ detail.auditRemark }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="evidence.length" class="as-detail__block">
          <div class="as-detail__block-title">凭证图片</div>
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
          <div class="as-detail__block-title">填写退货物流</div>
          <div class="as-return">
            <el-input v-model="returnForm.returnCarrier" placeholder="承运商" />
            <el-input v-model="returnForm.returnTracking" placeholder="退货单号" />
            <el-button type="primary" round :loading="returning" @click="submitReturnInfo">
              提交
            </el-button>
          </div>
        </div>

        <div class="as-detail__block">
          <div class="as-detail__block-title">协商记录</div>
          <el-timeline v-if="(detail.messages || []).length" class="as-detail__messages">
            <el-timeline-item
              v-for="(m, i) in detail.messages"
              :key="i"
              :timestamp="formatTime(m.createTime)"
            >
              <div class="as-msg__sender">{{ senderText(m.senderType) }}</div>
              <div class="as-msg__content">{{ m.content }}</div>
            </el-timeline-item>
          </el-timeline>
          <div v-else class="as-detail__empty">暂无记录</div>

          <div class="as-msg__input">
            <el-input
              v-model="messageContent"
              type="textarea"
              :rows="2"
              placeholder="输入留言…"
            />
            <el-button
              type="primary"
              round
              :loading="sending"
              class="as-msg__send"
              @click="sendMessage"
            >
              发送
            </el-button>
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
        撤销申请
      </el-button>
      <el-button round @click="handleVisible(false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelAfterSale,
  getAfterSaleDetail,
  sendAfterSaleMessage,
  submitReturn
} from '@/api/afterSale.js'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  id: { type: [Number, String], default: null }
})
const emit = defineEmits(['update:modelValue', 'refresh'])

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

const formatTime = (v) => (v ? String(v).replace('T', ' ') : '')

const senderText = (type) => {
  if (type === 2) return '商家'
  if (type === 3) return '系统'
  return '我'
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
    ElMessage.warning('请填写完整的退货物流信息')
    return
  }
  returning.value = true
  try {
    await submitReturn(props.id, { ...returnForm })
    ElMessage.success('退货物流已提交')
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
    await ElMessageBox.confirm('确定撤销该售后申请吗？', '撤销确认', { type: 'warning' })
  } catch {
    return
  }
  await cancelAfterSale(props.id)
  ElMessage.success('已撤销')
  emit('update:modelValue', false)
  emit('refresh')
}
</script>

<style scoped>
.as-detail {
  --tn-ink: #1d1d1f;
  --tn-ink-2: #6e6e73;
  --tn-ink-3: #9a9aa0;
  --tn-line: rgba(20, 30, 60, 0.08);
  color: var(--tn-ink);
}

/* head */
.as-detail__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 16px;
  background: linear-gradient(180deg, #f7f8fb, #eef1f6);
  border: 1px solid rgba(20, 30, 60, 0.05);
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
}
.as-detail__status--active {
  background: rgba(41, 151, 255, 0.12);
  color: #0473e2;
}
.as-detail__status--warn {
  background: rgba(255, 159, 10, 0.14);
  color: #b26a00;
}
.as-detail__status--done {
  background: rgba(52, 199, 89, 0.14);
  color: #1f9d4d;
}
.as-detail__status--idle {
  background: #f0f2f6;
  color: #6e6e73;
}

/* descriptions */
.as-detail__desc {
  margin-top: 16px;
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
  background: linear-gradient(180deg, #2997ff, #5e5ce6);
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
  box-shadow: 0 10px 22px -12px rgba(20, 40, 80, 0.4);
}

.as-return {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.as-return :deep(.el-input) {
  flex: 1;
  min-width: 140px;
}

/* messages */
.as-detail__messages {
  padding-left: 4px;
}

.as-detail__messages :deep(.el-timeline-item__timestamp) {
  color: var(--tn-ink-3);
  font-weight: 600;
}

.as-msg__sender {
  font-size: 12px;
  color: var(--tn-ink-3);
}

.as-msg__content {
  font-size: 13.5px;
  color: var(--tn-ink);
  margin-top: 2px;
}

.as-detail__empty {
  padding: 18px;
  text-align: center;
  font-size: 13px;
  color: var(--tn-ink-3);
  border-radius: 12px;
  background: #f7f8fb;
}

.as-msg__input {
  margin-top: 14px;
}

.as-msg__input :deep(.el-textarea__inner) {
  border-radius: 12px;
}

.as-msg__send {
  margin-top: 10px;
}
</style>

<style>
/* dialog 外壳（teleport 到 body，需全局作用域） */
.as-detail-dialog .el-dialog,
.as-detail-dialog.el-dialog {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 30px 80px -30px rgba(20, 40, 80, 0.5);
}

.as-detail-dialog .el-dialog__header,
.as-detail-dialog.el-dialog .el-dialog__header {
  padding: 22px 24px 6px;
  margin: 0;
}

.as-detail-dialog .el-dialog__title,
.as-detail-dialog.el-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: #1d1d1f;
}

.as-detail-dialog .el-dialog__body,
.as-detail-dialog.el-dialog .el-dialog__body {
  padding: 12px 24px 4px;
  max-height: 70vh;
  overflow-y: auto;
}

.as-detail-dialog .el-dialog__footer,
.as-detail-dialog.el-dialog .el-dialog__footer {
  padding: 12px 24px 22px;
}
</style>
