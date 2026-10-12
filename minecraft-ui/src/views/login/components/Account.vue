<template>
    <!-- From Uiverse.io by Yaya12085 --> 
<div class="card">
  <div class="header">
    <div class="image">
      <svg
        aria-hidden="true"
        stroke="currentColor"
        stroke-width="1.5"
        viewBox="0 0 24 24"
        fill="none"
      >
        <path
          d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          stroke-linejoin="round"
          stroke-linecap="round"
        ></path>
      </svg>
    </div>
    <div class="content">
      <span class="title">{{ title }}</span>
      <p class="message">
        {{ message }}
      </p>
      <div v-if="account" class="account-info">
        <span class="account-label">账号：</span>
        <span class="account-value">{{ account }}</span>
      </div>
      <p v-if="secondaryMessage" class="message">
        {{ secondaryMessage }}
      </p>
    </div>
    <div class="actions">
      <button class="confirm" type="button" @click="$emit('confirm')">确认</button>
    </div>
  </div>
</div>

</template>
<script setup>
defineProps({
  title: {
    type: String,
    default: '注册成功'
  },
  message: {
    type: String,
    default: '您的账号已生成，请妥善保管以下账号信息：'
  },
  account: {
    type: String,
    default: ''
  },
  secondaryMessage: {
    type: String,
    default: '请使用此账号进行登录'
  }
});

defineEmits(['confirm']);
</script>
<style scoped>
/* From Uiverse.io by Yaya12085 */ 
.card {
  overflow: hidden;
  position: relative;
  background-color: var(--theme-bg-surface);
  text-align: left;
  border-radius: 0.5rem;
  max-width: 400px;
  box-shadow: var(--theme-shadow-md);
}

.header {
  padding: 1.25rem 1rem 1rem 1rem;
  background-color: var(--theme-bg-surface);
}

.image {
  display: flex;
  margin-left: auto;
  margin-right: auto;
  background-color: var(--theme-primary-soft);
  flex-shrink: 0;
  justify-content: center;
  align-items: center;
  width: 3rem;
  height: 3rem;
  border-radius: 9999px;
}

.image svg {
  color: var(--theme-primary);
  width: 1.5rem;
  height: 1.5rem;
}

.content {
  margin-top: 0.75rem;
  text-align: center;
}

.title {
  color: var(--theme-text-1);
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.5rem;
}

.message {
  margin-top: 0.5rem;
  color: var(--theme-text-2);
  font-size: 0.875rem;
  line-height: 1.25rem;
}

.account-info {
  margin: 1rem 0;
  padding: 1rem;
  background-color: var(--theme-bg-surface-2);
  border-radius: 0.375rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.account-label {
  color: var(--theme-text-2);
  font-size: 0.875rem;
  font-weight: 500;
  margin-right: 0.5rem;
}

.account-value {
  color: var(--theme-highlight);
  font-size: 1rem;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

.actions {
  margin: 0.75rem 1rem;
  background-color: var(--theme-bg-surface-2);
}

.confirm {
  display: inline-flex;
  padding: 0.5rem 1rem;
  background-color: var(--theme-primary);
  color: var(--theme-primary-text);
  font-size: 1rem;
  line-height: 1.5rem;
  font-weight: 500;
  justify-content: center;
  width: 100%;
  border-radius: 0.375rem;
  border-width: 1px;
  border-color: transparent;
  box-shadow: var(--theme-shadow-sm);
  cursor: pointer;
  transition: all 0.3s ease;
}

.confirm:hover {
  background-color: var(--theme-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--theme-shadow-md);
}

</style>
