<template>
    <div v-if="isVisible" class="error-message-box">
        <div class="error-message-content">
            <div class="error-message-text">{{ message }}</div>
            <button class="error-message-close" @click="close">关闭</button>
        </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ErrorMessage',
    props: {
      message: {
        type: String,
        required: true,
      },
    },
    emits: ['close'],
    data() {
      return {
        isVisible: true,
      };
    },
    methods: {
      close() {
        this.isVisible = false;
        this.$emit('close');
      },
    },
  };
  </script>
  
<style scoped>
.error-message-box {
  width: 300px;
  padding: 20px;
  background-color: var(--theme-bg-elevated);
  border-radius: 8px;
  box-shadow: 0 4px 8px color-mix(in srgb, var(--theme-danger) 50%, transparent);
  border: 1px solid var(--theme-danger);
  position: fixed;
  top: 10%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  text-align: center;
}

.error-message-content {
  padding: 10px;
  text-align: center;
}

.error-message-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--theme-danger);
  margin-bottom: 10px;
}

.error-message-text {
  font-size: 16px;
  color: var(--theme-text-1);
  margin-bottom: 20px;
}

.error-message-close {
  background-color: var(--theme-danger);
  color: var(--theme-primary-text);
  border: none;
  border-radius: 5px;
  padding: 10px 50px;
  cursor: pointer;
  font-size: 16px;
}

.error-message-close:hover {
  background-color: color-mix(in srgb, var(--theme-danger) 80%, #000);
}
@media (max-width: 768px) {
  .error-message-box {
      position: fixed;
      left: 40%;
  }
}
</style>