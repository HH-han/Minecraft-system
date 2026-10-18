<template>
  <el-dialog
    :model-value="modelValue"
    width="560px"
    align-center
    class="as-dialog"
    @update:model-value="handleVisible"
  >
    <template #header>
      <div class="as-dialog-head">
        <span class="as-dialog-head__badge"><el-icon :size="18"><Service /></el-icon></span>
        <div class="as-dialog-head__text">
          <h3 class="as-dialog-head__title">{{ $t('afterSale.applyDialog.title') }}</h3>
          <p class="as-dialog-head__sub">{{ $t('afterSale.applyDialog.subtitle') }}</p>
        </div>
      </div>
    </template>

    <div class="as-dialog-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item :label="$t('afterSale.applyDialog.selectOrder')" prop="orderId">
          <el-select
            v-model="form.orderId"
            :disabled="!!orderId"
            :loading="ordersLoading"
            filterable
            :placeholder="$t('afterSale.applyDialog.selectOrderPlaceholder')"
            style="width: 100%"
          >
            <el-option
              v-for="o in orders"
              :key="o.id"
              :value="o.id"
              :label="orderLabel(o)"
              :disabled="!canApply(o)"
            >
              <div class="as-order-opt">
                <span class="as-order-opt__name">
                  {{ o.itemName || $t('afterSale.table.item') }}
                </span>
                <span class="as-order-opt__meta">
                  {{ o.orderNo }} · ¥{{ o.amount }} · {{ orderStatusText(o) }}
                  <template v-if="!canApply(o)"> · {{ disabledReason(o) }}</template>
                </span>
              </div>
            </el-option>
            <template #empty>
              <p class="as-order-opt__empty">{{ $t('afterSale.applyDialog.noEligibleOrders') }}</p>
            </template>
          </el-select>
        </el-form-item>

        <div class="as-grid">
          <el-form-item :label="$t('afterSale.applyDialog.type')" prop="type">
            <el-select
              v-model="form.type"
              :placeholder="$t('afterSale.applyDialog.typePlaceholder')"
              style="width: 100%"
            >
              <el-option :label="$t('afterSale.type.1')" :value="1" />
              <el-option :label="$t('afterSale.type.2')" :value="2" />
              <el-option :label="$t('afterSale.type.3')" :value="3" />
            </el-select>
          </el-form-item>

          <el-form-item :label="$t('afterSale.applyDialog.refundAmount')">
            <el-input-number
              v-model="form.refundAmount"
              :min="0"
              :precision="2"
              style="width: 100%"
            />
          </el-form-item>
        </div>

        <el-form-item :label="$t('afterSale.applyDialog.reason')" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            :placeholder="$t('afterSale.applyDialog.reasonPlaceholder')"
          />
        </el-form-item>

        <el-form-item :label="$t('afterSale.applyDialog.evidence')">
          <el-upload
            class="as-upload"
            :http-request="doUpload"
            list-type="picture-card"
            accept="image/*"
            :on-remove="handleRemove"
            :limit="5"
          >
            <span class="as-upload__plus">+</span>
          </el-upload>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button round @click="handleVisible(false)">
        {{ $t('afterSale.applyDialog.cancel') }}
      </el-button>
      <el-button type="primary" round :loading="submitting" @click="submit">
        {{ $t('afterSale.applyDialog.submit') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { Service } from '@element-plus/icons-vue'
import { applyAfterSale } from '@/api/afterSale.js'
import { getOrderDetail, getOrderList } from '@/api/order.js'
import { uploadFile } from '@/api/upload.js'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  orderId: { type: [Number, String], default: undefined }
})
const emit = defineEmits(['update:modelValue', 'success'])

const { t, te } = useI18n()

const formRef = ref(null)
const submitting = ref(false)
const orders = ref([])
const ordersLoading = ref(false)
// 已上传凭证：fileUid -> url
const uploadedMap = reactive({})

const form = reactive({
  orderId: undefined,
  type: 1,
  refundAmount: undefined,
  reason: ''
})

// 仅已支付 / 已发货 / 已完成的订单可申请售后
const ELIGIBLE_ORDER_STATUS = ['1', '2', '3']

const isAfterSaleBusy = (order) =>
  Number(order.afterSaleStatus) === 1 || Number(order.afterSaleStatus) === 2

const canApply = (order) =>
  ELIGIBLE_ORDER_STATUS.includes(String(order.status)) && !isAfterSaleBusy(order)

const disabledReason = (order) => {
  const afterSaleStatus = Number(order.afterSaleStatus)
  if (afterSaleStatus === 1) return t('afterSale.applyDialog.orderInAfterSale')
  if (afterSaleStatus === 2) return t('afterSale.applyDialog.orderAfterSaleDone')
  return t('afterSale.applyDialog.orderStatusBlocked')
}

const orderStatusText = (order) => {
  const key = `afterSale.orderStatus.${order.status}`
  return te(key) ? t(key) : order.statusDesc || String(order.status ?? '-')
}

const orderLabel = (order) => {
  const name = order.itemName || t('afterSale.table.item')
  const price = order.amount != null ? `¥${order.amount}` : ''
  return [name, order.orderNo, price].filter(Boolean).join(' · ')
}

const rules = computed(() => ({
  orderId: [
    { required: true, message: t('afterSale.applyDialog.selectOrderRequired'), trigger: 'change' }
  ],
  type: [{ required: true, message: t('afterSale.applyDialog.typeRequired'), trigger: 'change' }],
  reason: [
    { required: true, message: t('afterSale.applyDialog.reasonRequired'), trigger: 'blur' }
  ]
}))

// 选中订单后带出订单金额作为退款金额
const syncRefundAmount = () => {
  const order = orders.value.find((o) => String(o.id) === String(form.orderId))
  if (order?.amount != null) form.refundAmount = Number(order.amount)
}

// 拉取当前用户的全部订单（分页合并）
const fetchOrders = async () => {
  ordersLoading.value = true
  try {
    const list = []
    let pageNum = 1
    let total = 0
    do {
      const res = await getOrderList({ pageNum, pageSize: 100 })
      const page = res?.data ?? {}
      list.push(...(page.records ?? []))
      total = page.total ?? 0
      pageNum += 1
    } while (list.length < total && pageNum <= 10)

    // 从订单页跳转进入时，订单可能不在已拉取的分页里，单独补取
    if (props.orderId && !list.some((o) => String(o.id) === String(props.orderId))) {
      const res = await getOrderDetail(props.orderId)
      if (res?.data) list.unshift(res.data)
    }
    orders.value = list
    syncRefundAmount()
  } catch {
    orders.value = []
  } finally {
    ordersLoading.value = false
  }
}

watch(
  () => form.orderId,
  () => syncRefundAmount()
)

watch(
  () => props.modelValue,
  (visible) => {
    if (!visible) return
    form.orderId = props.orderId
      ? Number(props.orderId) || props.orderId
      : undefined
    fetchOrders()
  }
)

const handleVisible = (v) => emit('update:modelValue', v)

const doUpload = async (options) => {
  try {
    const res = await uploadFile(options.file)
    const url = res?.data || ''
    if (!url) {
      options.onError?.(new Error('上传失败'))
      return
    }
    uploadedMap[options.file.uid] = url
    options.onSuccess?.(res)
  } catch (e) {
    options.onError?.(e)
  }
}

const handleRemove = (file) => {
  delete uploadedMap[file.uid]
}

const submit = async () => {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  const selected = orders.value.find((o) => String(o.id) === String(form.orderId))
  if (!selected || !canApply(selected)) {
    ElMessage.warning(
      selected ? disabledReason(selected) : t('afterSale.applyDialog.selectOrderRequired')
    )
    return
  }
  submitting.value = true
  try {
    await applyAfterSale({
      orderId: selected.id,
      type: form.type,
      reason: form.reason,
      refundAmount: form.refundAmount,
      evidenceUrls: Object.values(uploadedMap)
    })
    ElMessage.success(t('afterSale.applyDialog.submitSuccess'))
    // 重置
    Object.keys(uploadedMap).forEach((k) => delete uploadedMap[k])
    form.type = 1
    form.refundAmount = undefined
    form.reason = ''
    if (!props.orderId) form.orderId = undefined
    emit('update:modelValue', false)
    emit('success')
  } catch {
    // 拦截器已提示
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.as-dialog-body {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-line: var(--theme-hairline);

  color: var(--tn-ink);
}

/* 自定义弹窗头部 */
.as-dialog-head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.as-dialog-head__badge {
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

.as-dialog-head__text {
  min-width: 0;
}

.as-dialog-head__title {
  margin: 0;
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.as-dialog-head__sub {
  margin: 3px 0 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.as-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}

.as-dialog-body :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--tn-ink);
  padding-bottom: 6px;
}

.as-dialog-body :deep(.el-input__wrapper),
.as-dialog-body :deep(.el-textarea__inner),
.as-dialog-body :deep(.el-select__wrapper) {
  border-radius: 12px;
}

/* 订单下拉项 */
.as-order-opt {
  display: flex;
  flex-direction: column;
  gap: 2px;
  line-height: 1.35;
  padding: 5px 0;
}

.as-order-opt__name {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.as-order-opt__meta {
  font-size: 12px;
  color: var(--theme-text-3);
  font-variant-numeric: tabular-nums;
}

.as-order-opt__empty {
  margin: 0;
  padding: 12px 0;
  text-align: center;
  font-size: 13px;
  color: var(--theme-text-3);
}

.as-upload :deep(.el-upload--picture-card) {
  width: 88px;
  height: 88px;
  border-radius: 14px;
  border: 1.5px dashed color-mix(in srgb, var(--theme-primary) 40%, transparent);
  background: var(--theme-primary-soft);
  transition: all 0.22s ease;
}

.as-upload :deep(.el-upload--picture-card:hover) {
  border-color: var(--theme-primary);
  background: var(--theme-primary-soft);
  transform: translateY(-2px);
}

.as-upload :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 88px;
  height: 88px;
  border-radius: 14px;
}

.as-upload__plus {
  font-size: 20px;
  color: var(--theme-text-3);
  line-height: 1;
}

@media (max-width: 560px) {
  .as-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<style>
/* dialog 外壳（teleport 到 body，需全局作用域） */
.as-dialog .el-dialog,
.as-dialog.el-dialog {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--theme-shadow-lg);
}

.as-dialog .el-dialog::before,
.as-dialog.el-dialog::before {
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

.as-dialog .el-dialog__header,
.as-dialog.el-dialog .el-dialog__header {
  padding: 22px 24px 16px;
  margin: 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.as-dialog .el-dialog__title,
.as-dialog.el-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.as-dialog .el-dialog__headerbtn {
  top: 18px;
  right: 18px;
}

.as-dialog .el-dialog__body,
.as-dialog.el-dialog .el-dialog__body {
  padding: 20px 24px 4px;
}

.as-dialog .el-dialog__footer,
.as-dialog.el-dialog .el-dialog__footer {
  padding: 16px 24px 22px;
  border-top: 1px solid var(--theme-hairline);
}
</style>
