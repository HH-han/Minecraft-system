<template>
  <el-dialog
    :model-value="modelValue"
    width="560px"
    align-center
    class="as-form-dialog"
    @update:model-value="handleVisible"
  >
    <template #header>
      <div class="as-dialog-head">
        <span class="as-dialog-head__badge"><el-icon :size="18"><Service /></el-icon></span>
        <div class="as-dialog-head__text">
          <h3 class="as-dialog-head__title">{{ $t('afterSale.applyPanel.formTitle') }}</h3>
          <p class="as-dialog-head__sub">{{ $t('afterSale.applyDialog.formSubtitle') }}</p>
        </div>
      </div>
    </template>

    <div v-if="order" class="as-dialog-body">
      <div class="as-order-summary">
        <el-image
          v-if="order.image"
          :src="order.image"
          fit="cover"
          class="as-order-summary__img"
        />
        <span v-else class="as-order-summary__img as-order-summary__img--empty">
          <el-icon :size="20"><Goods /></el-icon>
        </span>
        <div class="as-order-summary__main">
          <span class="as-order-summary__label">
            {{ $t('afterSale.applyPanel.currentOrder') }}
          </span>
          <span class="as-order-summary__name">{{ order.itemName || '-' }}</span>
          <span class="as-order-summary__meta">
            {{ order.orderNo }} · ×{{ order.quantity ?? 1 }} · ¥{{ order.amount ?? '-' }}
          </span>
        </div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="as-form">
        <div class="as-grid">
          <el-form-item :label="$t('afterSale.applyDialog.type')" prop="type" class="as-field">
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

          <el-form-item :label="$t('afterSale.applyDialog.refundAmount')" class="as-field">
            <el-input-number
              v-model="form.refundAmount"
              :min="0"
              :precision="2"
              style="width: 100%"
            />
            <p v-if="order.amount != null" class="as-field-hint">
              {{ $t('afterSale.applyDialog.refundAmountHint', { amount: order.amount }) }}
            </p>
          </el-form-item>
        </div>

        <el-form-item :label="$t('afterSale.applyDialog.reason')" prop="reason" class="as-field">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            resize="none"
            :placeholder="$t('afterSale.applyDialog.reasonPlaceholder')"
          />
        </el-form-item>

        <el-form-item :label="$t('afterSale.applyDialog.evidence')" class="as-field">
          <div class="as-upload-wrap">
            <el-upload
              class="as-upload"
              :http-request="doUpload"
              list-type="picture-card"
              accept="image/*"
              :on-remove="handleRemove"
              :limit="5"
            >
              <span class="as-upload__icon">
                <el-icon :size="18"><Plus /></el-icon>
              </span>
            </el-upload>
            <p class="as-field-hint">{{ $t('afterSale.applyDialog.evidenceHint') }}</p>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button round class="as-dialog-cancel" @click="handleVisible(false)">
        {{ $t('afterSale.applyDialog.cancel') }}
      </el-button>
      <el-button type="primary" round class="as-dialog-submit" :loading="submitting" @click="submit">
        {{ $t('afterSale.applyPanel.submit') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { Goods, Plus, Service } from '@element-plus/icons-vue'
import { applyAfterSale } from '@/api/afterSale.js'
import { uploadFile } from '@/api/upload.js'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  order: { type: Object, default: null }
})
const emit = defineEmits(['update:modelValue', 'success'])

const { t } = useI18n()

const formRef = ref(null)
const submitting = ref(false)
// 已上传凭证：fileUid -> url
const uploadedMap = reactive({})

const form = reactive({
  type: 1,
  refundAmount: undefined,
  reason: ''
})

const rules = computed(() => ({
  type: [{ required: true, message: t('afterSale.applyDialog.typeRequired'), trigger: 'change' }],
  reason: [
    { required: true, message: t('afterSale.applyDialog.reasonRequired'), trigger: 'blur' }
  ]
}))

const resetForm = () => {
  form.type = 1
  form.refundAmount = props.order?.amount != null ? Number(props.order.amount) : undefined
  form.reason = ''
  Object.keys(uploadedMap).forEach((k) => delete uploadedMap[k])
  formRef.value?.clearValidate()
}

watch(
  () => props.modelValue,
  (visible) => {
    if (visible) resetForm()
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
  if (!props.order) return
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    await applyAfterSale({
      orderId: props.order.id,
      type: form.type,
      reason: form.reason,
      refundAmount: form.refundAmount,
      evidenceUrls: Object.values(uploadedMap)
    })
    ElMessage.success(t('afterSale.applyDialog.submitSuccess'))
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
  --tn-ink-3: var(--theme-text-3);
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

/* 订单摘要 */
.as-order-summary {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  margin-bottom: 20px;
  border-radius: 16px;
  background:
    linear-gradient(135deg, var(--theme-primary-soft), transparent 62%),
    var(--theme-bg-surface-2);
  border: 1px solid color-mix(in srgb, var(--theme-primary) 16%, transparent);
}

.as-order-summary__img {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  border: 1px solid var(--tn-line);
  background: var(--theme-bg-surface);
}

.as-order-summary__img--empty {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--theme-primary);
}

.as-order-summary__main {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.as-order-summary__label {
  align-self: flex-start;
  padding: 1px 9px;
  border-radius: 999px;
  background: var(--theme-bg-surface);
  color: var(--theme-primary);
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.as-order-summary__name {
  font-size: 14px;
  font-weight: 600;
  color: var(--tn-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.as-order-summary__meta {
  font-size: 12px;
  color: var(--tn-ink-3);
  font-variant-numeric: tabular-nums;
}

/* 表单 */
.as-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 18px;
  align-items: start;
}

.as-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.as-form :deep(.el-form-item__label) {
  padding-bottom: 7px;
  font-size: 12.5px;
  font-weight: 600;
  line-height: 1.4;
  color: var(--tn-ink-2);
}

.as-form :deep(.el-form-item__error) {
  padding-top: 4px;
  font-size: 11.5px;
}

/* 控件统一为柔和填充 + 内描边 */
.as-dialog-body :deep(.el-input__wrapper),
.as-dialog-body :deep(.el-select__wrapper),
.as-dialog-body :deep(.el-textarea__inner) {
  border-radius: 12px;
  background: var(--theme-bg-surface-2);
  box-shadow: inset 0 0 0 1px var(--tn-line);
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.as-dialog-body :deep(.el-input__wrapper),
.as-dialog-body :deep(.el-select__wrapper) {
  min-height: 42px;
  padding: 1px 14px;
}

.as-dialog-body :deep(.el-input__inner) {
  font-size: 14px;
  color: var(--tn-ink);
}

.as-dialog-body :deep(.el-input__wrapper:hover),
.as-dialog-body :deep(.el-select__wrapper:hover),
.as-dialog-body :deep(.el-textarea__inner:hover) {
  box-shadow: inset 0 0 0 1px color-mix(in srgb, var(--theme-primary) 42%, transparent);
}

.as-dialog-body :deep(.el-input__wrapper.is-focus),
.as-dialog-body :deep(.el-select__wrapper.is-focused),
.as-dialog-body :deep(.el-textarea__inner:focus) {
  background: var(--theme-bg-surface);
  box-shadow: inset 0 0 0 1px var(--theme-primary), var(--theme-ring);
}

.as-dialog-body :deep(.el-textarea__inner) {
  padding: 11px 14px;
  font-size: 14px;
  line-height: 1.75;
  color: var(--tn-ink);
}

/* 金额步进器 */
.as-dialog-body :deep(.el-input-number) {
  width: 100%;
  height: 42px;
}

.as-dialog-body :deep(.el-input-number .el-input) {
  height: 100%;
}

.as-dialog-body :deep(.el-input-number .el-input__wrapper) {
  padding: 0 44px;
}

.as-dialog-body :deep(.el-input-number .el-input__inner) {
  text-align: center;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.02em;
}

.as-dialog-body :deep(.el-input-number__decrease),
.as-dialog-body :deep(.el-input-number__increase) {
  top: 4px;
  width: 36px;
  height: 34px;
  border: none;
  border-radius: 9px;
  background: transparent;
  color: var(--tn-ink-2);
  transition: background 0.18s ease, color 0.18s ease;
}

.as-dialog-body :deep(.el-input-number__decrease) {
  left: 4px;
}

.as-dialog-body :deep(.el-input-number__increase) {
  right: 4px;
}

.as-dialog-body :deep(.el-input-number__decrease:hover),
.as-dialog-body :deep(.el-input-number__increase:hover) {
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.as-dialog-body :deep(.el-input-number__decrease.is-disabled),
.as-dialog-body :deep(.el-input-number__increase.is-disabled) {
  color: var(--tn-ink-3);
  background: transparent;
  opacity: 0.4;
}

/* 上传 */
.as-upload-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.as-upload :deep(.el-upload--picture-card),
.as-upload :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 92px;
  height: 92px;
  border-radius: 16px;
}

.as-upload :deep(.el-upload--picture-card) {
  border: 1.5px dashed color-mix(in srgb, var(--theme-primary) 38%, transparent);
  background: var(--theme-primary-soft);
  transition: transform 0.22s ease, border-color 0.22s ease, background 0.22s ease;
}

.as-upload :deep(.el-upload--picture-card:hover) {
  border-color: var(--theme-primary);
  background: color-mix(in srgb, var(--theme-primary) 16%, transparent);
  transform: translateY(-2px);
}

.as-upload__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--theme-bg-surface);
  color: var(--theme-primary);
  box-shadow: var(--theme-shadow-sm);
}

.as-field-hint {
  margin: 6px 0 0;
  font-size: 11.5px;
  line-height: 1.5;
  color: var(--tn-ink-3);
}

/* 底部按钮 */
.as-dialog-cancel {
  color: var(--tn-ink-2);
}

.as-dialog-submit {
  border: none;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-weight: 600;
  letter-spacing: 0.02em;
  box-shadow: 0 10px 22px -12px color-mix(in srgb, var(--theme-primary) 80%, transparent);
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.as-dialog-submit:hover {
  color: var(--theme-primary-text);
  background: var(--theme-gradient);
  opacity: 0.94;
  transform: translateY(-1px);
  box-shadow: 0 14px 26px -12px color-mix(in srgb, var(--theme-primary) 85%, transparent);
}

@media (max-width: 560px) {
  .as-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<style>
/* dialog 外壳（teleport 到 body，需全局作用域） */
.as-form-dialog .el-dialog,
.as-form-dialog.el-dialog {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--theme-shadow-lg);
}

.as-form-dialog .el-dialog::before,
.as-form-dialog.el-dialog::before {
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

.as-form-dialog .el-dialog__header,
.as-form-dialog.el-dialog .el-dialog__header {
  padding: 22px 24px 16px;
  margin: 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.as-form-dialog .el-dialog__title,
.as-form-dialog.el-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.as-form-dialog .el-dialog__headerbtn {
  top: 18px;
  right: 18px;
}

.as-form-dialog .el-dialog__body,
.as-form-dialog.el-dialog .el-dialog__body {
  padding: 20px 24px 4px;
}

.as-form-dialog .el-dialog__footer,
.as-form-dialog.el-dialog .el-dialog__footer {
  padding: 16px 24px 22px;
  border-top: 1px solid var(--theme-hairline);
}
</style>
