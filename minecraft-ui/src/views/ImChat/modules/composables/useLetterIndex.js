import { onUnmounted, ref } from 'vue'

// 通讯录 A-Z 字母索引定位
// @param {import('vue').ComputedRef<Array>} contactGroups 分组后的人员列表
export function useLetterIndex(contactGroups) {
  const contactListRef = ref(null)
  const activeLetter = ref('')
  const bubbleLetter = ref('')
  const groupRefs = {}
  let bubbleTimer = null
  let dragging = false

  const setGroupRef = (letter, el) => {
    if (el) {
      groupRefs[letter] = el
    } else {
      delete groupRefs[letter]
    }
  }

  const showBubble = (letter) => {
    bubbleLetter.value = letter
    if (bubbleTimer) clearTimeout(bubbleTimer)
    bubbleTimer = setTimeout(() => {
      bubbleLetter.value = ''
    }, 500)
  }

  const scrollToLetter = (letter) => {
    const container = contactListRef.value
    const el = groupRefs[letter]
    if (!container || !el) return
    container.scrollTop = el.offsetTop
    activeLetter.value = letter
  }

  const onListScroll = () => {
    const container = contactListRef.value
    if (!container) return
    const threshold = container.scrollTop + 4
    let current = ''
    for (const group of contactGroups.value) {
      const el = groupRefs[group.letter]
      if (el && el.offsetTop <= threshold) {
        current = group.letter
      } else {
        break
      }
    }
    if (current) activeLetter.value = current
  }

  const onIndexMove = (event) => {
    if (!dragging) return
    const el = document.elementFromPoint(event.clientX, event.clientY)
    const letter = el && el.dataset ? el.dataset.letter : ''
    if (letter) {
      showBubble(letter)
      scrollToLetter(letter)
    }
  }

  const onIndexUp = () => {
    dragging = false
    window.removeEventListener('pointermove', onIndexMove)
    window.removeEventListener('pointerup', onIndexUp)
  }

  const onIndexDown = (event, letter) => {
    event.preventDefault()
    dragging = true
    showBubble(letter)
    scrollToLetter(letter)
    window.addEventListener('pointermove', onIndexMove)
    window.addEventListener('pointerup', onIndexUp)
  }

  // 切换筛选条件后重置到顶部
  const resetScroll = () => {
    activeLetter.value = ''
    if (contactListRef.value) {
      contactListRef.value.scrollTop = 0
    }
  }

  onUnmounted(() => {
    onIndexUp()
    if (bubbleTimer) clearTimeout(bubbleTimer)
  })

  return {
    contactListRef,
    activeLetter,
    bubbleLetter,
    setGroupRef,
    onListScroll,
    onIndexDown,
    resetScroll
  }
}
