<template>
  <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal">
      <div class="modal-header">
        <h2>添加账单</h2>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label>账单名称</label>
          <input v-model="form.title" type="text" placeholder="如：晚餐" />
        </div>
        <div class="form-group">
          <label>金额</label>
          <input v-model.number="form.amount" type="number" placeholder="0.00" />
        </div>
        <div class="form-group">
          <label>说明</label>
          <textarea v-model="form.description" placeholder="账单说明"></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn-secondary" @click="$emit('close')">取消</button>
        <button class="btn-primary" @click="handleSubmit">添加</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'submit'])

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
    alert('请填写账单名称和金额')
    return
  }
  emit('submit', { ...form })
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-20);
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: var(--color-bg);
  border-radius: var(--radius-lg);
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: slideUp var(--transition-normal);
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-24);
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  font-size: 22px;
  font-weight: 600;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: var(--color-text-secondary);
  line-height: 1;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all var(--transition-fast);
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.modal-body {
  padding: var(--spacing-24);
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-12);
  padding: var(--spacing-16) var(--spacing-24);
  border-top: 1px solid var(--color-border);
}

.modal-footer button {
  border: none;
  padding: var(--spacing-12) var(--spacing-28);
  border-radius: var(--radius-full);
  font-size: 17px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-4);
}

.modal-footer .btn-primary {
  background: var(--color-primary);
  color: #fff;
}

.modal-footer .btn-primary:hover {
  background: var(--color-primary-hover);
}

.modal-footer .btn-primary:active {
  transform: scale(0.98);
}

.modal-footer .btn-secondary {
  background: transparent;
  color: var(--color-primary);
}

.modal-footer .btn-secondary:hover {
  background: rgba(41, 151, 255, 0.1);
}

.modal-footer .btn-secondary:active {
  transform: scale(0.98);
}

.form-group {
  margin-bottom: var(--spacing-20);
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: var(--spacing-8);
  color: var(--color-text);
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: var(--spacing-12) var(--spacing-16);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 15px;
  font-family: inherit;
  transition: all var(--transition-normal);
  box-sizing: border-box;
  background: var(--color-bg);
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.1);
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: var(--color-text-secondary);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

@media (max-width: 767px) {
  .modal {
    max-width: 100%;
    border-radius: var(--radius-lg);
    margin: var(--spacing-8);
  }
  .modal-body {
    padding: var(--spacing-16);
  }
  .modal-header {
    padding: var(--spacing-16);
  }
  .modal-footer {
    padding: var(--spacing-12) var(--spacing-16);
  }
}
</style>