<template>
  <el-dialog
    :model-value="modelValue"
    title="申请售后"
    width="560px"
    align-center
    class="as-dialog"
    @update:model-value="handleVisible"
  >
    <div class="as-dialog-body">
      <p class="as-dialog-tip">请如实填写售后信息，商家将尽快为您处理</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="as-grid">
          <el-form-item label="订单ID" prop="orderId">
            <el-input
              v-model.number="form.orderId"
              :disabled="!!orderId"
              placeholder="请输入订单ID"
            />
          </el-form-item>

          <el-form-item label="售后类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
              <el-option label="仅退款" :value="1" />
              <el-option label="退货退款" :value="2" />
              <el-option label="换货" :value="3" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="退款金额">
          <el-input-number
            v-model="form.refundAmount"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="问题描述" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请描述您遇到的问题"
          />
        </el-form-item>

        <el-form-item label="上传凭证">
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
      <el-button round @click="handleVisible(false)">取消</el-button>
      <el-button type="primary" round :loading="submitting" @click="submit">
        提交申请
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { applyAfterSale } from '@/api/afterSale.js'
import { uploadFile } from '@/api/upload.js'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  orderId: { type: [Number, String], default: undefined }
})
const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const submitting = ref(false)
// 已上传凭证：fileUid -> url
const uploadedMap = reactive({})

const form = reactive({
  orderId: undefined,
  type: 1,
  refundAmount: undefined,
  reason: ''
})

const rules = {
  orderId: [{ required: true, message: '请输入订单ID', trigger: 'blur' }],
  type: [{ required: true, message: '请选择售后类型', trigger: 'change' }],
  reason: [{ required: true, message: '请填写问题描述', trigger: 'blur' }]
}

watch(
  () => props.modelValue,
  (visible) => {
    if (visible && props.orderId) {
      form.orderId = props.orderId
    }
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
  submitting.value = true
  try {
    await applyAfterSale({
      orderId: form.orderId,
      type: form.type,
      reason: form.reason,
      refundAmount: form.refundAmount,
      evidenceUrls: Object.values(uploadedMap)
    })
    ElMessage.success('申请已提交')
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
  --tn-ink: #1d1d1f;
  --tn-ink-2: #6e6e73;
  --tn-line: rgba(20, 30, 60, 0.08);

  color: var(--tn-ink);
}

.as-dialog-tip {
  margin: 0 0 18px;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 13px;
  color: #0473e2;
  background: rgba(41, 151, 255, 0.08);
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

.as-upload :deep(.el-upload--picture-card) {
  width: 88px;
  height: 88px;
  border-radius: 14px;
  border: 1.5px dashed rgba(41, 151, 255, 0.4);
  background: rgba(41, 151, 255, 0.04);
  transition: all 0.22s ease;
}

.as-upload :deep(.el-upload--picture-card:hover) {
  border-color: #2997ff;
  background: rgba(41, 151, 255, 0.08);
  transform: translateY(-2px);
}

.as-upload :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 88px;
  height: 88px;
  border-radius: 14px;
}

.as-upload__plus {
  font-size: 20px;
  color: #8e8e93;
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
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 30px 80px -30px rgba(20, 40, 80, 0.5);
}

.as-dialog .el-dialog__header,
.as-dialog.el-dialog .el-dialog__header {
  padding: 22px 24px 6px;
  margin: 0;
}

.as-dialog .el-dialog__title,
.as-dialog.el-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: #1d1d1f;
}

.as-dialog .el-dialog__body,
.as-dialog.el-dialog .el-dialog__body {
  padding: 12px 24px 4px;
}

.as-dialog .el-dialog__footer,
.as-dialog.el-dialog .el-dialog__footer {
  padding: 12px 24px 22px;
}
</style>
