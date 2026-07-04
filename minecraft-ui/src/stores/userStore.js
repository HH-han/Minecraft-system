import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const profile = ref(null)
  const preferences = ref({
    language: 'zh-CN',
    timezone: 'Asia/Shanghai',
    notifications: {
      email: true,
      push: true,
      sms: false
    },
    privacy: {
      onlineStatus: true,
      activityStatus: true,
      profileVisibility: 'public'
    }
  })
  const isLoading = ref(false)

  const isLoggedIn = computed(() => !!profile.value)

  const displayName = computed(() => {
    if (!profile.value) return ''
    return profile.value.nickname || profile.value.username || profile.value.email || ''
  })

  const avatarUrl = computed(() => {
    if (!profile.value) return ''
    return profile.value.avatar || '/assets/defaultimage/moren.webp'
  })

  function setProfile(userProfile) {
    profile.value = userProfile
  }

  function updateProfile(updates) {
    if (profile.value) {
      profile.value = { ...profile.value, ...updates }
    }
  }

  function updatePreferences(newPreferences) {
    preferences.value = { ...preferences.value, ...newPreferences }
  }

  function updateNotificationPreferences(notificationPreferences) {
    preferences.value.notifications = { 
      ...preferences.value.notifications, 
      ...notificationPreferences 
    }
  }

  function updatePrivacyPreferences(privacyPreferences) {
    preferences.value.privacy = { 
      ...preferences.value.privacy, 
      ...privacyPreferences 
    }
  }

  function logout() {
    profile.value = null
    preferences.value = {
      language: 'zh-CN',
      timezone: 'Asia/Shanghai',
      notifications: {
        email: true,
        push: true,
        sms: false
      },
      privacy: {
        onlineStatus: true,
        activityStatus: true,
        profileVisibility: 'public'
      }
    }
  }

  function getPreference(key, defaultValue) {
    const keys = key.split('.')
    let value = preferences.value
    for (const k of keys) {
      if (value && typeof value === 'object') {
        value = value[k]
      } else {
        return defaultValue
      }
    }
    return value !== undefined ? value : defaultValue
  }

  function setPreference(key, value) {
    const keys = key.split('.')
    const lastKey = keys.pop()
    let obj = preferences.value
    for (const k of keys) {
      if (!obj[k]) {
        obj[k] = {}
      }
      obj = obj[k]
    }
    obj[lastKey] = value
  }

  return {
    profile,
    preferences,
    isLoading,
    isLoggedIn,
    displayName,
    avatarUrl,
    setProfile,
    updateProfile,
    updatePreferences,
    updateNotificationPreferences,
    updatePrivacyPreferences,
    logout,
    getPreference,
    setPreference
  }
}, {
  persist: true
})