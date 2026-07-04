import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([])
  const unreadCount = ref(0)
  const isLoading = ref(false)

  const sortedNotifications = computed(() => {
    return [...notifications.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  })

  const readNotifications = computed(() => notifications.value.filter(n => n.read))

  const unreadNotifications = computed(() => notifications.value.filter(n => !n.read))

  function addNotification(notification) {
    const newNotification = {
      id: notification.id || Date.now(),
      type: notification.type || 'info',
      title: notification.title || '',
      message: notification.message || '',
      read: notification.read || false,
      createdAt: notification.createdAt || new Date().toISOString(),
      link: notification.link || null,
      data: notification.data || null
    }
    notifications.value.unshift(newNotification)
    if (!newNotification.read) {
      unreadCount.value++
    }
  }

  function markAsRead(notificationId) {
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification && !notification.read) {
      notification.read = true
      unreadCount.value--
    }
  }

  function markAllAsRead() {
    notifications.value.forEach(n => {
      if (!n.read) {
        n.read = true
      }
    })
    unreadCount.value = 0
  }

  function removeNotification(notificationId) {
    const index = notifications.value.findIndex(n => n.id === notificationId)
    if (index !== -1) {
      const notification = notifications.value[index]
      if (!notification.read) {
        unreadCount.value--
      }
      notifications.value.splice(index, 1)
    }
  }

  function clearAll() {
    notifications.value = []
    unreadCount.value = 0
  }

  function setNotifications(newNotifications) {
    notifications.value = newNotifications
    unreadCount.value = newNotifications.filter(n => !n.read).length
  }

  function setUnreadCount(count) {
    unreadCount.value = count
  }

  return {
    notifications,
    unreadCount,
    isLoading,
    sortedNotifications,
    readNotifications,
    unreadNotifications,
    addNotification,
    markAsRead,
    markAllAsRead,
    removeNotification,
    clearAll,
    setNotifications,
    setUnreadCount
  }
}, {
  persist: true
})