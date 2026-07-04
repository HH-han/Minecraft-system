import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export function useAuth() {
  const router = useRouter()
  const authStore = useAuthStore()
  const isLoading = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => authStore.isAuthenticated)
  const userInfo = computed(() => authStore.userInfo)
  const token = computed(() => authStore.token)

  async function login(credentials) {
    isLoading.value = true
    error.value = null
    try {
      const response = await authStore.login(credentials)
      if (response.code === 200) {
        await router.push('/')
      }
      return response
    } catch (err) {
      error.value = err.message || '登录失败'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function emailLogin(data) {
    isLoading.value = true
    error.value = null
    try {
      const response = await authStore.emailLogin(data)
      if (response.code === 200) {
        await router.push('/')
      }
      return response
    } catch (err) {
      error.value = err.message || '登录失败'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function logout() {
    try {
      await authStore.logout()
      await router.push('/login')
    } catch (err) {
      console.error('Logout error:', err)
    }
  }

  async function updateUserInfo(info) {
    try {
      await authStore.updateUserInfo(info)
      return true
    } catch (err) {
      error.value = err.message || '更新失败'
      throw err
    }
  }

  function requireAuth() {
    if (!isAuthenticated.value) {
      router.push('/login')
      return false
    }
    return true
  }

  function requireGuest() {
    if (isAuthenticated.value) {
      router.push('/')
      return false
    }
    return true
  }

  return {
    isAuthenticated,
    userInfo,
    token,
    isLoading,
    error,
    login,
    emailLogin,
    logout,
    updateUserInfo,
    requireAuth,
    requireGuest
  }
}