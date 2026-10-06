import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getActiveAnnouncements,
  getUnreadCount,
  markAsRead
} from '@/api/announcement'

/**
 * 系统公告状态：
 * - activeList：首页展示公告（弹窗/轮播/横幅数据源）
 * - unreadCount：未读数（红点）
 * - popupQueue：待弹窗公告队列
 * - readIds：本地已读 ID 缓存（防止弹窗重复展示）
 */
export const useAnnouncementStore = defineStore('announcement', () => {
  const activeList = ref([])
  const unreadCount = ref(0)
  const popupQueue = ref([])
  const readIds = ref(new Set(JSON.parse(localStorage.getItem('ann_read') || '[]')))

  function isLoggedIn() {
    return !!localStorage.getItem('token')
  }

  // 拉取首页展示公告
  async function fetchActive() {
    try {
      const res = await getActiveAnnouncements()
      if (res.code === 200) {
        activeList.value = res.data || []
        popupQueue.value = activeList.value.filter(
          (a) => a.displayMode === 2 && !readIds.value.has(a.id)
        )
      }
    } catch (e) {
      console.warn('获取公告失败:', e)
    }
  }

  // 拉取未读数
  async function fetchUnread() {
    if (!isLoggedIn()) return
    try {
      const res = await getUnreadCount()
      if (res.code === 200) {
        unreadCount.value = res.data || 0
      }
    } catch (e) {
      // 未登录/登录过期时静默
    }
  }

  // 标记已读（服务端 + 本地缓存）
  async function read(id) {
    try {
      await markAsRead(id)
    } catch (e) {
      // 本地兜底，弹窗不重复展示
    }
    readIds.value.add(id)
    localStorage.setItem('ann_read', JSON.stringify([...readIds.value]))
    if (unreadCount.value > 0) unreadCount.value--
    popupQueue.value = popupQueue.value.filter((a) => a.id !== id)
  }

  // SSE 实时推送的新公告入列
  function pushRealtime(ann) {
    activeList.value.unshift(ann)
    if (ann.displayMode === 2 && !readIds.value.has(ann.id)) {
      popupQueue.value.push(ann)
    }
    if (isLoggedIn()) {
      unreadCount.value++
    }
  }

  return {
    activeList,
    unreadCount,
    popupQueue,
    readIds,
    isLoggedIn,
    fetchActive,
    fetchUnread,
    read,
    pushRealtime
  }
})
