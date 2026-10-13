// 联系人拼音首字母工具
// 拼音首字母：GB2312 一级汉字按拼音排序，通过 GBK 编码区间定位首字母

export const LETTER_ORDER = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('').concat('#')
export const nameCollator = new Intl.Collator('zh-Hans-CN', { sensitivity: 'base' })

const PINYIN_BOUNDARY = '啊芭擦搭蛾发噶哈击喀垃妈拿哦啪期然撒塌挖昔压匝'
const PINYIN_LETTERS = 'ABCDEFGHJKLMNOPQRSTWXYZ'
const initialCache = new Map()

// Unicode 汉字 → GBK 编码映射（GB2312 一级汉字区，按拼音排序），首次使用时构建
let gbkCodeMap = null
let boundaryCodes = null

const initPinyinTable = () => {
  if (gbkCodeMap) return true
  if (typeof TextDecoder === 'undefined') return false
  let decoder
  try {
    decoder = new TextDecoder('gbk')
  } catch (e) {
    return false
  }
  const map = new Map()
  const bytes = new Uint8Array(2)
  for (let hi = 0xb0; hi <= 0xd7; hi++) {
    for (let lo = 0xa1; lo <= 0xfe; lo++) {
      const code = (hi << 8) | lo
      if (code > 0xd7f9) break
      bytes[0] = hi
      bytes[1] = lo
      const ch = decoder.decode(bytes)
      if (ch && ch !== '\uFFFD' && !map.has(ch)) map.set(ch, code)
    }
  }
  if (map.size === 0) return false
  gbkCodeMap = map
  boundaryCodes = PINYIN_BOUNDARY.split('').map((ch) => map.get(ch))
  return true
}

// 取名称的拼音首字母：中文按拼音，英文取大写，其余归入 #
export const getInitial = (name) => {
  const ch = String(name || '').trim().charAt(0)
  if (!ch) return '#'
  if (/[a-zA-Z]/.test(ch)) return ch.toUpperCase()
  if (initialCache.has(ch)) return initialCache.get(ch)
  let letter = '#'
  if (initPinyinTable()) {
    const code = gbkCodeMap.get(ch)
    if (code !== undefined) {
      for (let i = 0; i < boundaryCodes.length; i++) {
        if (boundaryCodes[i] !== undefined && code >= boundaryCodes[i]) {
          letter = PINYIN_LETTERS[i]
        } else {
          break
        }
      }
    }
  }
  initialCache.set(ch, letter)
  return letter
}
