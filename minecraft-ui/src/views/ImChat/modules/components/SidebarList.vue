<template>
  <div class="list-wrap">
    <div ref="contactListRef" class="contact-list" @scroll="onListScroll">
      <ConversationList
        v-if="currentTab === 'chat'"
        :contacts="filteredContacts"
        :selected-id="selectedId"
        @select-contact="emit('select-contact', $event)"
        @open-detail="emit('open-detail', $event)"
      />

      <ContactsList
        v-else-if="currentTab === 'contacts'"
        :contact-groups="contactGroups"
        :starred-ids="starredIds"
        :starred-count="starredFriends.length"
        :friend-count="friends.length"
        :collapsed="contactsCollapsed"
        :show-star-only="showStarOnly"
        :set-group-ref="setGroupRef"
        @toggle-collapse="contactsCollapsed = !contactsCollapsed"
        @toggle-star-entry="onStarEntryToggle"
        @toggle-star="toggleStar"
        @open-detail="emit('open-detail', $event)"
      />

      <DiscoverList
        v-else-if="currentTab === 'discover'"
        @moments="emit('moments')"
        @scan="emit('scan')"
        @nearby="emit('nearby')"
        @shake="emit('shake')"
      />

      <ProfileList
        v-else-if="currentTab === 'me'"
        :avatar="currentUserAvatar"
        :user-name="currentUserName"
        @user-profile="emit('user-profile')"
        @services="emit('services')"
        @settings="emit('settings')"
      />
    </div>

    <!-- A-Z 字母索引条 + 定位气泡 -->
    <LetterIndexBar
      :visible="currentTab === 'contacts' && !contactsCollapsed"
      :letters="indexLetters"
      :active-letter="activeLetter"
      :bubble-letter="bubbleLetter"
      @index-down="onIndexDown"
    />
  </div>
</template>

<script setup>
import { computed, ref, toRef, watch } from 'vue'
import ConversationList from './ConversationList.vue'
import ContactsList from './ContactsList.vue'
import DiscoverList from './DiscoverList.vue'
import ProfileList from './ProfileList.vue'
import LetterIndexBar from './LetterIndexBar.vue'
import { useContactGroups } from '../composables/useContactGroups'
import { useLetterIndex } from '../composables/useLetterIndex'
import { useStarredFriends } from '../composables/useStarredFriends'

const props = defineProps({
  currentTab: {
    type: String,
    default: 'chat'
  },
  friends: {
    type: Array,
    default: () => []
  },
  groups: {
    type: Array,
    default: () => []
  },
  selectedId: {
    type: [String, Number],
    default: ''
  },
  searchKeyword: {
    type: String,
    default: ''
  },
  currentUserAvatar: {
    type: String,
    default: ''
  },
  currentUserName: {
    type: String,
    default: '用户'
  }
})

const emit = defineEmits([
  'select-contact',
  'open-detail',
  'user-profile',
  'services',
  'settings',
  'moments',
  'scan',
  'nearby',
  'shake'
])

const friendsRef = toRef(props, 'friends')
const searchKeywordRef = toRef(props, 'searchKeyword')

const showStarOnly = ref(false)
const contactsCollapsed = ref(false)

const { starredIds, toggleStar, starredFriends } = useStarredFriends(friendsRef)
const { contactGroups, indexLetters } = useContactGroups({
  friends: friendsRef,
  starredFriends,
  searchKeyword: searchKeywordRef,
  showStarOnly
})
const {
  contactListRef,
  activeLetter,
  bubbleLetter,
  setGroupRef,
  onListScroll,
  onIndexDown,
  resetScroll
} = useLetterIndex(contactGroups)

// 会话列表：好友 + 群聊合并后按名称排序
const unifiedContacts = computed(() => {
  const friendsWithType = props.friends.map((f) => ({ ...f, isGroup: false }))
  const groupsWithType = props.groups.map((g) => ({ ...g, isGroup: true }))
  return [...friendsWithType, ...groupsWithType].sort((a, b) => {
    const nameA = (a.name || '').toLowerCase()
    const nameB = (b.name || '').toLowerCase()
    return nameA.localeCompare(nameB, 'zh-CN')
  })
})

const filteredContacts = computed(() => {
  if (!searchKeywordRef.value) return unifiedContacts.value
  return unifiedContacts.value.filter((c) => c.name.includes(searchKeywordRef.value))
})

// 离开通讯录 tab 时重置「仅星标」筛选
watch(
  () => props.currentTab,
  (tab) => {
    if (tab !== 'contacts') {
      showStarOnly.value = false
    }
  }
)

const onStarEntryToggle = () => {
  showStarOnly.value = !showStarOnly.value
  resetScroll()
}
</script>

<style scoped>
.list-wrap {
  position: relative;
  flex: 1;
  min-height: 0;
  display: flex;
}

.contact-list {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  background: var(--theme-bg-surface);
  position: relative;
}

.contact-list::-webkit-scrollbar {
  width: 6px;
}

.contact-list::-webkit-scrollbar-track {
  background: transparent;
}

.contact-list::-webkit-scrollbar-thumb {
  background: var(--theme-scrollbar-thumb);
  border-radius: 3px;
}

.contact-list::-webkit-scrollbar-thumb:hover {
  background: var(--theme-scrollbar-thumb-hover);
}
</style>
