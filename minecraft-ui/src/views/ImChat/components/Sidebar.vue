<template>
  <div class="im-sidebar">
    <SidebarNav
      :avatar="currentUserAvatar"
      :user-name="currentUserName"
      :current-tab="currentTab"
      :favorites-active="favoritesActive"
      :unread-total="unreadTotal"
      @user-profile="onUserProfile"
      @nav-change="onNavChange"
      @favorites="onFavorites"
    />

    <div class="sidebar-right">
      <SidebarHeader
        v-model:keyword="searchKeyword"
        :title="currentTabTitle"
        :placeholder="searchPlaceholder"
        :friend-request-count="friendRequestCount"
        @search="onSearch"
        @create-group="onCreateGroup"
        @add-friend="onAddFriend"
        @open-friend-requests="onOpenFriendRequests"
      />

      <!-- ============ 通讯录：分组入口 ============ -->
      <ContactsActions
        v-if="currentTab === 'contacts'"
        :friend-request-count="friendRequestCount"
        :groups-count="groups.length"
        @new-friends="onNewFriends"
        @view-groups="onGroups"
        @tags="onTags"
        @officials="onOfficials"
      />

      <SidebarList
        :current-tab="currentTab"
        :friends="friends"
        :groups="groups"
        :selected-id="selectedContact ? selectedContact.id : ''"
        :search-keyword="searchKeyword"
        :current-user-avatar="currentUserAvatar"
        :current-user-name="currentUserName"
        @select-contact="onSelectContact"
        @open-detail="onOpenDetail"
        @user-profile="onUserProfile"
        @services="onServices"
        @settings="onSettings"
        @moments="onMoments"
        @scan="onScan"
        @nearby="onNearby"
        @shake="onShake"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import {
  ContactsActions,
  SidebarHeader,
  SidebarList,
  SidebarNav
} from '../modules'
import '../modules/styles/sidebar-shared.css'

const props = defineProps({
  friends: {
    type: Array,
    default: () => []
  },
  groups: {
    type: Array,
    default: () => []
  },
  activeTab: {
    type: String,
    default: 'friends'
  },
  selectedContact: {
    type: Object,
    default: null
  },
  friendRequestCount: {
    type: Number,
    default: 0
  },
  currentUserAvatar: {
    type: String,
    default: ''
  },
  currentUserName: {
    type: String,
    default: '用户'
  },
  favoritesActive: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'add-friend',
  'create-group',
  'search',
  'select-contact',
  'open-friend-requests',
  'nav-change',
  'user-profile',
  'new-friends',
  'view-groups',
  'tags',
  'officials',
  'moments',
  'scan',
  'nearby',
  'shake',
  'services',
  'favorites',
  'settings',
  'open-detail'
])

const currentTab = ref('chat')
const searchKeyword = ref('')

const currentTabTitle = computed(() => {
  const titles = {
    chat: '博览通讯',
    contacts: '通讯录',
    discover: '发现',
    favorites: '收藏',
    me: '我'
  }
  return titles[currentTab.value] || '博览通讯'
})

const searchPlaceholder = computed(() => {
  const placeholders = {
    chat: '搜索好友或群组...',
    contacts: '搜索好友...',
    discover: '搜索...',
    favorites: '收藏...',
    me: '搜索...'
  }
  return placeholders[currentTab.value] || '搜索...'
})

const unreadTotal = computed(() => {
  return (
    props.friends.reduce((sum, f) => sum + (f.unreadCount || 0), 0) + props.friendRequestCount
  )
})

const onNavChange = (tab) => {
  currentTab.value = tab
  emit('nav-change', tab)
}

const onSearch = (keyword) => {
  emit('search', keyword)
}

const onAddFriend = () => {
  emit('add-friend')
}

const onCreateGroup = () => {
  emit('create-group')
}

const onOpenFriendRequests = () => {
  emit('open-friend-requests')
}

const onSelectContact = (contact) => {
  emit('select-contact', contact)
}

const onOpenDetail = (contact) => {
  emit('open-detail', contact)
}

const onUserProfile = () => {
  emit('user-profile')
}

const onNewFriends = () => {
  emit('new-friends')
}

const onGroups = () => {
  emit('view-groups')
}

const onTags = () => {
  emit('tags')
}

const onOfficials = () => {
  emit('officials')
}

const onMoments = () => {
  emit('moments')
}

const onScan = () => {
  emit('scan')
}

const onNearby = () => {
  emit('nearby')
}

const onShake = () => {
  emit('shake')
}

const onServices = () => {
  emit('services')
}

const onFavorites = () => {
  emit('favorites')
}

const onSettings = () => {
  emit('settings')
}
</script>

<style scoped>
.im-sidebar {
  width: 320px;
  height: 100%;
  background: var(--theme-bg-surface);
  display: flex;
  overflow: hidden;
  transition: width var(--theme-duration) var(--theme-ease);
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
    sans-serif;
}

/* ============ 右侧列表面板 ============ */
.sidebar-right {
  flex: 1;
  height: 100%;
  min-width: 0;
  background: var(--theme-bg-surface);
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--theme-border);
  overflow: hidden;
}
</style>
