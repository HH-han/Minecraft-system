import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useToast() {
  const visible = ref(false)
  const message = ref('')
  const type = ref('success')
  const duration = ref(3000)

  function showToast(msg, toastType = 'success', dur = 3000) {
    message.value = msg
    type.value = toastType
    duration.value = dur
    visible.value = true

    ElMessage({
      message: msg,
      type: toastType,
      duration: dur,
      onClose: () => {
        visible.value = false
      }
    })
  }

  function success(msg, dur = 3000) {
    showToast(msg, 'success', dur)
  }

  function error(msg, dur = 3000) {
    showToast(msg, 'error', dur)
  }

  function warning(msg, dur = 3000) {
    showToast(msg, 'warning', dur)
  }

  function info(msg, dur = 3000) {
    showToast(msg, 'info', dur)
  }

  async function confirm(msg, title = '提示') {
    try {
      await ElMessageBox.confirm(msg, title, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      return true
    } catch {
      return false
    }
  }

  async function alert(msg, title = '提示', type = 'info') {
    await ElMessageBox.alert(msg, title, {
      confirmButtonText: '确定',
      type
    })
  }

  async function prompt(msg, title = '提示', options = {}) {
    try {
      const { value } = await ElMessageBox.prompt(msg, title, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        ...options
      })
      return value
    } catch {
      return null
    }
  }

  return {
    visible,
    message,
    type,
    duration,
    showToast,
    success,
    error,
    warning,
    info,
    confirm,
    alert,
    prompt
  }
}