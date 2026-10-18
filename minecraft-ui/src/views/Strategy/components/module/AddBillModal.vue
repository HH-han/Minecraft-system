<template>
  <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal" role="dialog" aria-modal="true">
      <div class="modal-header">
        <h2>{{ t('strategy.addBill.title') }}</h2>
        <button class="close-btn" type="button" @click="$emit('close')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M6 6l12 12M18 6L6 18" />
          </svg>
        </button>
      </div>

      <div class="modal-body">
        <div class="form-group">
          <label>{{ t('strategy.addBill.name') }}</label>
          <input v-model="form.title" type="text" :placeholder="t('strategy.addBill.namePlaceholder')" />
        </div>

        <div class="form-group">
          <label>{{ t('strategy.addBill.amount') }}</label>
          <div class="input-prefix">
            <span>¥</span>
            <input v-model.number="form.amount" type="number" min="0" step="0.01" placeholder="0.00" />
          </div>
        </div>

        <div class="form-group">
          <label>{{ t('strategy.addBill.description') }}</label>
          <textarea v-model="form.description" :placeholder="t('strategy.addBill.descriptionPlaceholder')"></textarea>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn-secondary" type="button" @click="$emit('close')">
          {{ t('strategy.addBill.cancel') }}
        </button>
        <button class="btn-primary" type="button" @click="handleSubmit">
          {{ t('strategy.addBill.submit') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'submit'])

const { t } = useI18n()

const form = reactive({
  title: '',
  amount: 0,
  description: ''
})

watch(() => props.visible, (val) => {
  if (!val) {
    Object.assign(form, {
      title: '',
      amount: 0,
      description: ''
    })
  }
})

function handleSubmit() {
  if (!form.title || !form.amount) {
    ElMessage.warning(t('strategy.addBill.required'))
    return
  }
  emit('submit', { ...form })
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1100;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: var(--theme-overlay);
  backdrop-filter: blur(16px);
  animation: fade-in var(--transition-normal);
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  overflow: hidden;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  animation: modal-in var(--transition-slow);
}

@keyframes modal-in {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 26px 18px;
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--color-text);
}

.close-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: background var(--transition-fast), color var(--transition-fast);
}

.close-btn svg {
  width: 16px;
  height: 16px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
}

.close-btn:hover {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.modal-body {
  flex: 1;
  padding: 22px 26px 8px;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 26px 22px;
  border-top: 1px solid var(--color-border);
}

.modal-footer button {
  padding: 10px 24px;
  border: 1px solid transparent;
  border-radius: var(--radius-full);
  font-family: inherit;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), border-color var(--transition-fast),
    transform var(--transition-fast);
}

.btn-primary {
  background: var(--color-primary);
  color: var(--theme-primary-text);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-secondary {
  background: var(--theme-bg-surface);
  border-color: var(--color-border);
  color: var(--color-text);
}

.btn-secondary:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.modal-footer button:active {
  transform: scale(0.97);
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 11px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-bg-secondary);
  font-family: inherit;
  font-size: 14px;
  color: var(--color-text);
  box-sizing: border-box;
  transition: border-color var(--transition-normal), box-shadow var(--transition-normal),
    background var(--transition-normal);
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-ring);
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: var(--color-text-secondary);
}

.form-group textarea {
  resize: vertical;
  min-height: 96px;
  line-height: 1.6;
}

.input-prefix {
  display: flex;
  align-items: center;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-bg-secondary);
  transition: border-color var(--transition-normal), box-shadow var(--transition-normal),
    background var(--transition-normal);
  overflow: hidden;
}

.input-prefix:focus-within {
  border-color: var(--color-primary);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-ring);
}

.input-prefix span {
  padding: 0 0 0 14px;
  font-size: 15px;
  font-weight: 700;
  color: var(--color-primary);
}

.input-prefix input {
  border: none;
  background: transparent;
  box-shadow: none;
}

.input-prefix input:focus {
  border: none;
  background: transparent;
  box-shadow: none;
}

@media (max-width: 767px) {
  .modal-overlay {
    padding: 12px;
  }

  .modal {
    max-width: 100%;
    max-height: 92vh;
    border-radius: var(--radius-md);
  }

  .modal-header {
    padding: 18px 18px 14px;
  }

  .modal-body {
    padding: 18px 18px 4px;
  }

  .modal-footer {
    padding: 14px 18px 18px;
  }

  .modal-footer button {
    flex: 1;
  }
}
</style>
