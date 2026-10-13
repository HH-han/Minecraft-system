import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 全局数据刷新总线
 * <p>
 * 任何「数据可能已变化」的信号（本地写操作成功 / 服务端 SSE 推送）都会调用 notify()，
 * tick 自增，页面通过 useAutoRefresh 监听 tick 自动重新拉取数据，
 * 从而避免用户手动刷新才能看到最新数据。
 */
export const useRefreshStore = defineStore('refresh', () => {
  /** 变更计数：每次数据变更自增，作为自动刷新的触发源 */
  const tick = ref(0)
  /** 最近一次变更事件：{ resource, method, path, at } */
  const lastEvent = ref(null)

  /** 通知数据已变更 */
  function notify(event = null) {
    if (event) lastEvent.value = event
    tick.value++
  }

  return {
    tick,
    lastEvent,
    notify
  }
})
