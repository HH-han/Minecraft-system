import { computed } from 'vue'
import { getInitial, LETTER_ORDER, nameCollator } from './useContactInitial'

// 通讯录：按首字母分组（支持「仅星标」过滤与关键字搜索）
// @param {Object} options
// @param {import('vue').Ref<Array>} options.friends 好友列表
// @param {import('vue').Ref<Array>} options.starredFriends 星标好友列表
// @param {import('vue').Ref<string>} options.searchKeyword 搜索关键字
// @param {import('vue').Ref<boolean>} options.showStarOnly 是否仅显示星标好友
export function useContactGroups({ friends, starredFriends, searchKeyword, showStarOnly }) {
  const contactSource = computed(() => {
    let list = showStarOnly.value ? starredFriends.value : friends.value
    const keyword = searchKeyword.value.trim()
    if (keyword) {
      list = list.filter((f) => (f.name || '').includes(keyword))
    }
    return list
  })

  const contactGroups = computed(() => {
    const map = new Map()
    for (const friend of contactSource.value) {
      const key = getInitial(friend.name)
      if (!map.has(key)) map.set(key, [])
      map.get(key).push(friend)
    }
    return LETTER_ORDER.filter((letter) => map.has(letter)).map((letter) => ({
      letter,
      friends: map
        .get(letter)
        .slice()
        .sort((a, b) => nameCollator.compare(a.name || '', b.name || ''))
    }))
  })

  const indexLetters = computed(() => contactGroups.value.map((g) => g.letter))

  return { contactSource, contactGroups, indexLetters }
}
