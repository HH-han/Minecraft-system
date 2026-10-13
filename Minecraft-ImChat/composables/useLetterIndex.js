/**
 * 右侧字母索引交互辅助（对齐 PC 端 modules/composables/useLetterIndex）
 *
 * 页面持有 scroll-into-view 状态，这里只提供与 DOM 无关的计算逻辑。
 */

export { INDEX_LETTERS } from './useContactGroups.js'

/**
 * 根据触摸点 Y 坐标解析命中的字母
 * @param {Number} clientY 触摸点相对视口的 Y
 * @param {{top:number,height:number}} rect 索引条的位置尺寸
 * @param {Array<string>} letters 当前索引字母
 * @returns {String|null}
 */
export function resolveLetterByY(clientY, rect, letters = []) {
  if (!rect || !rect.height || !letters.length) return null
  const ratio = (clientY - rect.top) / rect.height
  let index = Math.floor(ratio * letters.length)
  index = Math.max(0, Math.min(index, letters.length - 1))
  return letters[index]
}

/**
 * 测量元素位置（Promise 化，兼容 uni-app createSelectorQuery）
 * @param {Object} instance 页面/组件实例（this）
 * @param {String} selector 选择器，如 '.letter-index'
 */
export function measureRect(instance, selector) {
  return new Promise((resolve) => {
    const query = uni.createSelectorQuery().in(instance)
    query
      .select(selector)
      .boundingClientRect((rect) => resolve(rect || null))
      .exec()
  })
}
