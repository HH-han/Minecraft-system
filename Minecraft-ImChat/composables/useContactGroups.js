/**
 * 通讯录按首字母分组（对齐 PC 端 modules/composables/useContactGroups）
 */

import { getFirstLetter } from '../utils/pinyin.js'

// 右侧字母索引用到的完整字母表
export const INDEX_LETTERS = [
  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
  'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#'
]

/**
 * 将联系人列表按首字母分组
 * @param {Array} list 联系人（每项需含 name）
 * @param {String} keyword 搜索关键字
 * @returns {{ groups: Array<{letter:string,list:Array}>, letters: Array<string>, total: number }}
 */
export function groupContacts(list = [], keyword = '') {
  const kw = String(keyword || '').trim().toLowerCase()
  const filtered = kw
    ? list.filter((c) => String(c.name || '').toLowerCase().includes(kw))
    : list

  const map = {}
  filtered.forEach((c) => {
    const letter = getFirstLetter(c.name)
    if (!map[letter]) map[letter] = []
    map[letter].push(c)
  })

  const letters = Object.keys(map).sort((a, b) => {
    if (a === '#') return 1
    if (b === '#') return -1
    return a.localeCompare(b)
  })

  const groups = letters.map((letter) => ({
    letter,
    list: map[letter].sort((a, b) => String(a.name).localeCompare(String(b.name), 'zh-CN'))
  }))

  return { groups, letters, total: filtered.length }
}

/**
 * 仅保留实际存在的字母（用于精简右侧索引条）
 */
export function presentLetters(letters = []) {
  return INDEX_LETTERS.filter((letter) => letters.includes(letter))
}
