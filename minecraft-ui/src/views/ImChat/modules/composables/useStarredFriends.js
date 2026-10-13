import { computed, ref } from 'vue'

const STARRED_KEY = 'im-starred-friends'

// 星标朋友（本地持久化）
// @param {import('vue').Ref<Array>} friends 好友列表
export function useStarredFriends(friends) {
  const starredIds = ref([])
  try {
    starredIds.value = JSON.parse(localStorage.getItem(STARRED_KEY) || '[]')
  } catch (e) {
    starredIds.value = []
  }

  const isStarred = (id) => starredIds.value.includes(id)

  const toggleStar = (friend) => {
    const index = starredIds.value.indexOf(friend.id)
    if (index === -1) {
      starredIds.value.push(friend.id)
    } else {
      starredIds.value.splice(index, 1)
    }
    try {
      localStorage.setItem(STARRED_KEY, JSON.stringify(starredIds.value))
    } catch (e) {
      // 忽略存储异常（隐私模式等）
    }
  }

  const starredFriends = computed(() => friends.value.filter((f) => isStarred(f.id)))

  return { starredIds, isStarred, toggleStar, starredFriends }
}
