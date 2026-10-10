/**
 * 图层注册表：懒构建 / 显隐切换 / 统一动画 tick / 销毁
 *
 * 注意：flights、cables（globe.gl arcsData）与 gdp（heatmapsData）为 globe.gl 数据层，
 * 由 GlobeCanvas 直接管理，不经过本注册表。
 */
import { defs as natureDefs } from './nature.js'
import { defs as humanDefs } from './human.js'
import { defs as dynamicDefs } from './dynamic.js'
import { defs as ambientDefs } from './ambient.js'

const ALL_DEFS = { ...natureDefs, ...humanDefs, ...dynamicDefs, ...ambientDefs }

/** 由注册表管理的场景图层 key（globe.gl 数据层除外） */
export const REGISTRY_KEYS = Object.keys(ALL_DEFS)

export function createLayerRegistry({ globe, radius, container }) {
  const built = new Map() // key -> layer instance
  const pending = new Set()

  async function ensure(key) {
    if (built.has(key)) return built.get(key)
    if (pending.has(key)) return null
    const def = ALL_DEFS[key]
    if (!def) return null
    pending.add(key)
    try {
      const inst = await def.build({ globe, radius, container })
      built.set(key, inst)
      // 构建期间若已被关闭则保持隐藏
      inst.setVisible?.(visibleKeys.has(key) ?? false)
      if (inst.group) inst.group.visible = visibleKeys.has(key)
      if (inst.el) inst.el.style.display = visibleKeys.has(key) ? '' : 'none'
      return inst
    } catch (e) {
      console.warn(`图层 ${key} 构建失败:`, e)
      return null
    } finally {
      pending.delete(key)
    }
  }

  const visibleKeys = new Set()

  function setVisible(key, v) {
    if (v) visibleKeys.add(key)
    else visibleKeys.delete(key)
    const inst = built.get(key)
    if (!inst) {
      if (v) ensure(key)
      return
    }
    if (inst.group) inst.group.visible = v
    if (inst.el) inst.el.style.display = v ? '' : 'none'
    if (v) inst.onShow?.()
    else inst.onHide?.()
  }

  function tick(dt, t) {
    for (const [key, inst] of built) {
      if (inst.update && visibleKeys.has(key)) inst.update(dt, t)
    }
  }

  function dispose() {
    for (const inst of built.values()) {
      try { inst.dispose?.() } catch (e) { /* 忽略 */ }
    }
    built.clear()
  }

  return { setVisible, tick, dispose, REGISTRY_KEYS }
}
