<template>
  <div ref="rootRef" class="sort-select" :class="{ 'is-open': isOpen }" @focusout="handleFocusOut">
    <span v-if="label" class="sort-select__label">{{ label }}</span>

    <div class="sort-select__control">
      <button
        ref="triggerRef"
        type="button"
        class="sort-select__trigger"
        aria-haspopup="listbox"
        :aria-expanded="isOpen"
        @click="toggle"
        @keydown.esc.stop.prevent="close"
        @keydown.down.prevent="isOpen = true"
        @keydown.up.prevent="isOpen = true"
      >
        <span class="sort-select__value">{{ currentLabel }}</span>
        <svg class="sort-select__arrow" viewBox="0 0 16 16" aria-hidden="true">
          <path
            d="M4 6.2 8 10.2l4-4"
            fill="none"
            stroke="currentColor"
            stroke-width="1.6"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>

      <Transition name="ss-pop">
        <div v-if="isOpen" class="sort-select__panel" role="listbox">
          <button
            v-for="option in options"
            :key="option.value"
            type="button"
            class="sort-select__option"
            :class="{ 'is-active': option.value === modelValue }"
            role="option"
            :aria-selected="option.value === modelValue"
            @click="select(option.value)"
          >
            <span class="sort-select__option-label">{{ option.label }}</span>
            <svg v-if="option.value === modelValue" class="sort-select__check" viewBox="0 0 16 16" aria-hidden="true">
              <path
                d="m3.2 8.4 3.1 3.1 6.5-7"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </button>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  options: { type: Array, default: () => [] },
  label: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'change'])

const rootRef = ref(null)
const triggerRef = ref(null)
const isOpen = ref(false)

const currentLabel = computed(
  () => props.options.find((option) => option.value === props.modelValue)?.label ?? ''
)

function toggle() {
  isOpen.value = !isOpen.value
}

function close() {
  isOpen.value = false
}

function select(value) {
  emit('update:modelValue', value)
  emit('change', value)
  close()
  triggerRef.value?.focus()
}

function handleClickOutside(event) {
  if (isOpen.value && rootRef.value && !rootRef.value.contains(event.target)) {
    close()
  }
}

function handleFocusOut(event) {
  if (!rootRef.value?.contains(event.relatedTarget)) {
    close()
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
.sort-select {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  color: var(--theme-text-1);
}

.sort-select__label {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.02em;
  color: var(--theme-text-3);
  white-space: nowrap;
}

.sort-select__control {
  position: relative;
}

.sort-select__trigger {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-width: 152px;
  height: 38px;
  padding: 0 12px 0 16px;
  border: 1px solid var(--theme-border);
  border-radius: 999px;
  background: var(--theme-bg-glass-strong);
  backdrop-filter: saturate(180%) blur(14px);
  -webkit-backdrop-filter: saturate(180%) blur(14px);
  box-shadow: var(--theme-shadow-sm);
  color: var(--theme-text-1);
  font-family: inherit;
  font-size: 13.5px;
  font-weight: 500;
  cursor: pointer;
  transition:
    transform 0.24s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.24s ease,
    border-color 0.24s ease,
    background 0.24s ease;
}

.sort-select__trigger:hover {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, var(--theme-primary) 38%, var(--theme-border));
  box-shadow: var(--theme-shadow-md);
}

.sort-select__trigger:focus-visible {
  outline: none;
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.sort-select.is-open .sort-select__trigger {
  border-color: color-mix(in srgb, var(--theme-primary) 55%, transparent);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-ring);
}

.sort-select__value {
  flex: 1;
  text-align: left;
  white-space: nowrap;
}

.sort-select__arrow {
  flex-shrink: 0;
  width: 15px;
  height: 15px;
  color: var(--theme-text-3);
  transition:
    transform 0.24s ease,
    color 0.24s ease;
}

.sort-select.is-open .sort-select__arrow {
  transform: rotate(180deg);
  color: var(--theme-primary);
}

.sort-select__panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  z-index: 60;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 100%;
  padding: 6px;
  border: 1px solid var(--theme-border);
  border-radius: 16px;
  background: var(--theme-bg-glass-strong);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  box-shadow: var(--theme-shadow-lg);
}

.sort-select__option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 9px 12px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: var(--theme-text-2);
  font-family: inherit;
  font-size: 13.5px;
  text-align: left;
  white-space: nowrap;
  cursor: pointer;
  transition:
    background 0.18s ease,
    color 0.18s ease;
}

.sort-select__option:hover {
  background: var(--theme-primary-soft);
  color: var(--theme-text-1);
}

.sort-select__option.is-active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-weight: 600;
}

.sort-select__option:focus-visible {
  outline: none;
  background: var(--theme-primary-soft);
  color: var(--theme-text-1);
}

.sort-select__check {
  flex-shrink: 0;
  width: 14px;
  height: 14px;
  color: var(--theme-primary);
}

.ss-pop-enter-active,
.ss-pop-leave-active {
  transition:
    opacity 0.18s ease,
    transform 0.22s cubic-bezier(0.22, 1, 0.36, 1);
}

.ss-pop-enter-from,
.ss-pop-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}

@media (max-width: 480px) {
  .sort-select {
    gap: 6px;
  }

  .sort-select__label {
    font-size: 12px;
  }

  .sort-select__trigger {
    min-width: 132px;
    height: 36px;
    padding: 0 10px 0 14px;
  }
}
</style>
