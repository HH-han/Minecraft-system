import { watch, onMounted, onActivated, onUnmounted } from 'vue'
import { useRefreshStore } from '@/stores/refreshStore'

/**
 * 页面数据自动刷新
 * <p>
 * 在原有「进页面拉一次数据」的基础上，额外在这些时机自动重新拉取：
 * 1. 数据变更信号（本地写操作成功 / 服务端 SSE 推送，见 refreshStore）
 * 2. 浏览器窗口重新获得焦点
 * 3. 标签页从后台切回前台（在后台期间的变更会合并为一次刷新）
 * 4. keep-alive 页面被重新激活
 * 5. 可选定时轮询（interval > 0）
 *
 * 用法（保留原有 onMounted(fetchList)，仅新增一行）：
 *   const fetchList = async () => { ... }
 *   useAutoRefresh(fetchList)
 * <p>
 * 默认不会在挂载时立即执行（immediate: false），避免与页面自身 onMounted 重复请求。
 *
 * @param {Function} fetcher 数据拉取函数
 * @param {Object} [options]
 * @param {boolean} [options.immediate=false] 是否在挂载时立即执行一次
 * @param {number}  [options.interval=0] 定时轮询间隔（毫秒），0 表示不轮询
 * @param {boolean} [options.refreshOnFocus=true] 是否在窗口聚焦/切回前台时刷新
 * @param {number}  [options.debounce=500] 刷新防抖间隔（毫秒），合并密集的变更信号
 */
export function useAutoRefresh(fetcher, options = {}) {
  const {
    immediate = false,
    interval = 0,
    refreshOnFocus = true,
    debounce = 500
  } = options

  const refreshStore = useRefreshStore()

  let timer = null
  let intervalTimer = null
  let disposed = false
  // 标签页在后台期间收到的变更信号，切回前台时合并刷新一次
  let dirtyWhileHidden = false

  const run = () => {
    if (disposed || typeof fetcher !== 'function') return
    Promise.resolve()
      .then(fetcher)
      .catch((err) => {
        if (import.meta.env.DEV) {
          console.warn('[useAutoRefresh] 自动刷新失败:', err)
        }
      })
  }

  const schedule = () => {
    if (disposed) return
    // 页面不可见时先记账，等切回前台再刷新，避免无谓请求
    if (typeof document !== 'undefined' && document.hidden) {
      dirtyWhileHidden = true
      return
    }
    if (timer) clearTimeout(timer)
    timer = setTimeout(run, debounce)
  }

  const handleVisibility = () => {
    if (typeof document === 'undefined' || document.hidden) return
    if (dirtyWhileHidden) {
      dirtyWhileHidden = false
      schedule()
    }
  }

  const handleFocus = () => schedule()

  // 数据变更（本地操作 / 服务端推送）→ 自动重新拉取
  watch(() => refreshStore.tick, schedule)

  if (refreshOnFocus && typeof window !== 'undefined') {
    document.addEventListener('visibilitychange', handleVisibility)
    window.addEventListener('focus', handleFocus)
  }

  if (interval > 0) {
    intervalTimer = setInterval(() => {
      if (typeof document === 'undefined' || !document.hidden) run()
    }, interval)
  }

  if (immediate) {
    onMounted(run)
  }
  // keep-alive 页面重新激活时刷新
  onActivated(schedule)

  onUnmounted(() => {
    disposed = true
    if (timer) clearTimeout(timer)
    if (intervalTimer) clearInterval(intervalTimer)
    if (refreshOnFocus && typeof window !== 'undefined') {
      document.removeEventListener('visibilitychange', handleVisibility)
      window.removeEventListener('focus', handleFocus)
    }
  })

  return { refresh: run, scheduleRefresh: schedule }
}
