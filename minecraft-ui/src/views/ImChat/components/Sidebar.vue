<template>
  <div class="im-sidebar">
    <div class="sidebar-left">
      <div class="user-avatar" @click="onUserProfile">
        <img 
          :src="currentUserAvatar || defaultAvatar" 
          :alt="currentUserName"
          class="avatar-img"
        />
      </div>
      <div class="nav-list">
        <button 
          :class="['nav-item', { active: isTabActive('chat') }]"
          @click="onNavChange('chat')"
          title="聊天"
        >
          <Icon name="message" :size="'24px'" />
          <span v-if="unreadTotal > 0" class="nav-badge">{{ unreadTotal > 99 ? '99+' : unreadTotal }}</span>
        </button>
        <button 
          :class="['nav-item', { active: isTabActive('contacts') }]"
          @click="onNavChange('contacts')"
          title="通讯录"
        >
          <Icon name="user" :size="'24px'" />
        </button>
        <button 
          :class="['nav-item', { active: isTabActive('discover') }]"
          @click="onNavChange('discover')"
          title="发现"
        >
          <Icon name="compass" :size="'24px'" />
        </button>
        <button 
          :class="['nav-item', { active: favoritesActive }]"
          @click="onFavorites"
          title="收藏"
        >
          <Icon name="star" :size="'24px'" />
        </button>
        <button 
          :class="['nav-item', { active: isTabActive('me') }]"
          @click="onNavChange('me')"
          title="我"
        >
          <Icon name="user-circle" :size="'24px'" />
        </button>
      </div>
    </div>

    <div class="sidebar-right">
      <div class="sidebar-header">
        <h2 class="sidebar-title">{{ currentTabTitle }}</h2>
      </div>

      <div class="search-row">
        <div class="search-box">
          <span class="search-icon">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </span>
          <input 
            type="text" 
            v-model="searchKeyword" 
            :placeholder="searchPlaceholder"
            class="search-input"
            @input="onSearch"
          />
        </div>
        <div class="plus-wrap">
          <button 
            :class="['plus-btn', { active: showPlusMenu }]" 
            @click="togglePlusMenu"
            title="更多"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            <span v-if="friendRequestCount > 0" class="plus-dot"></span>
          </button>
          <transition name="menu">
            <div v-if="showPlusMenu" class="plus-menu">
              <button class="menu-item" @click="onMenuCreateGroup">
                <span class="menu-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                  </svg>
                </span>
                <span class="menu-text">发起群聊</span>
              </button>
              <button class="menu-item" @click="onMenuAddFriend">
                <span class="menu-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="8.5" cy="7" r="4"></circle>
                    <line x1="20" y1="8" x2="20" y2="14"></line>
                    <line x1="23" y1="11" x2="17" y2="11"></line>
                  </svg>
                </span>
                <span class="menu-text">添加朋友</span>
              </button>
              <button class="menu-item" @click="onMenuFriendRequests">
                <span class="menu-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="22 12 16 12 14 15 10 15 8 12 2 12"></polyline>
                    <path d="M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"></path>
                  </svg>
                </span>
                <span class="menu-text">好友申请</span>
                <span v-if="friendRequestCount > 0" class="menu-badge">{{ friendRequestCount > 99 ? '99+' : friendRequestCount }}</span>
              </button>
            </div>
          </transition>
        </div>
      </div>
      <div v-if="showPlusMenu" class="menu-mask" @click="closePlusMenu"></div>
      
      <!-- ============ 通讯录：分组入口 ============ -->
      <div v-if="currentTab === 'contacts'" class="contacts-actions">
        <div class="contact-item" @click="onNewFriends">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="user-plus" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">新的朋友</h4>
          </div>
          <span v-if="friendRequestCount > 0" class="entry-badge">{{ friendRequestCount > 99 ? '99+' : friendRequestCount }}</span>
          <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
        </div>
        <div class="contact-item" @click="onGroups">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="users" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">群聊</h4>
          </div>
          <span v-if="groups.length" class="entry-count">{{ groups.length }}</span>
          <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
        </div>
        <div class="contact-item" @click="onTags">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="tag" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">标签</h4>
          </div>
          <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
        </div>
        <div class="contact-item" @click="onOfficials">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="building" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">公众号、服务号</h4>
          </div>
          <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
        </div>
      </div>
      
      <div class="list-wrap">
        <div ref="contactListRef" class="contact-list" @scroll="onListScroll">
          <template v-if="currentTab === 'chat'">
            <div 
              v-for="contact in filteredContacts" 
              :key="contact.id"
              :class="['contact-item', { active: selectedContact?.id === contact.id }]"
            >
              <div 
                class="contact-content"
                @click="onSelectContact(contact)"
              >
                <div class="avatar-wrapper">
                  <img 
                    :src="contact.avatar || defaultAvatar" 
                    :alt="contact.name"
                    class="contact-avatar"
                  />
                  <span v-if="contact.isGroup" class="group-badge">
                    <Icon name="users" :size="'10px'" />
                  </span>
                  <span v-if="contact.unreadCount > 0" class="unread-badge">{{ contact.unreadCount > 99 ? '99+' : contact.unreadCount }}</span>
                  <span v-if="!contact.isGroup" :class="['online-status', { online: contact.online }]"></span>
                </div>
                <div class="contact-main">
                  <div class="contact-row-top">
                    <h4 class="contact-name">
                      <span class="name-text">{{ contact.name }}</span>
                      <span v-if="contact.isGroup" class="contact-tag">群聊</span>
                    </h4>
                    <span v-if="contact.time" class="contact-time">{{ contact.time }}</span>
                  </div>
                  <p class="contact-last-message">
                    <span v-if="contact.isGroup && contact.memberCount" class="member-count">{{ contact.memberCount }}人</span>{{ contact.lastMessage }}
                  </p>
                </div>
              </div>
              <button 
                class="detail-btn"
                @click.stop="onOpenDetail(contact)"
                title="详情"
              >
                <Icon name="info" :size="'14px'" />
              </button>
            </div>
            
            <div v-if="filteredContacts.length === 0" class="empty-state">
              <Icon name="message" :size="'48px'" />
              <p>暂无联系人</p>
            </div>
          </template>
          
          <!-- ============ 通讯录：联系人分组列表 ============ -->
          <template v-if="currentTab === 'contacts'">
            <div class="section-header" @click="contactsCollapsed = !contactsCollapsed">
              <span :class="['section-chevron', { collapsed: contactsCollapsed }]">
                <Icon name="chevron-right" :size="'14px'" />
              </span>
              <span class="section-title">联系人</span>
              <span class="section-count">{{ friends.length }}</span>
            </div>

            <template v-if="!contactsCollapsed">
              <div 
                :class="['contact-item', 'star-entry', { active: showStarOnly }]" 
                @click="onStarEntryToggle"
              >
                <div class="avatar-wrapper">
                  <div class="icon-avatar star-avatar">
                    <Icon name="star" :size="'18px'" />
                  </div>
                </div>
                <div class="contact-info">
                  <h4 class="contact-name">星标朋友</h4>
                </div>
                <span v-if="starredFriends.length" class="entry-count">{{ starredFriends.length }}</span>
                <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
              </div>

              <div 
                v-for="group in contactGroups" 
                :key="group.letter" 
                :ref="(el) => setGroupRef(group.letter, el)"
                class="letter-group"
              >
                <div class="letter-header">{{ group.letter }}</div>
                <div 
                  v-for="friend in group.friends" 
                  :key="friend.id"
                  class="contact-item"
                  @click="onOpenDetail(friend)"
                >
                  <div class="avatar-wrapper">
                    <img 
                      :src="friend.avatar || defaultAvatar" 
                      :alt="friend.name"
                      class="contact-avatar"
                    />
                  </div>
                  <div class="contact-info">
                    <h4 class="contact-name">
                      <span class="name-text">{{ friend.name }}</span>
                    </h4>
                  </div>
                  <button 
                    :class="['star-btn', { active: isStarred(friend.id) }]"
                    :title="isStarred(friend.id) ? '取消星标朋友' : '设为星标朋友'"
                    @click.stop="toggleStar(friend)"
                  >
                    <Icon name="star" :size="'16px'" />
                  </button>
                </div>
              </div>

              <div v-if="contactGroups.length === 0" class="empty-state">
                <Icon :name="showStarOnly ? 'star' : 'user'" :size="'48px'" />
                <p>{{ showStarOnly ? '暂无星标朋友' : '暂无好友' }}</p>
              </div>
            </template>
          </template>
          
          <template v-if="currentTab === 'discover'">
            <div class="contact-item" @click="onMoments">
              <div class="avatar-wrapper">
                <div class="icon-avatar">
                  <Icon name="camera" :size="'20px'" />
                </div>
              </div>
              <div class="contact-info">
                <h4 class="contact-name">朋友圈</h4>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
            </div>
            <div class="contact-item" @click="onScan">
              <div class="avatar-wrapper">
                <div class="icon-avatar">
                  <Icon name="scan" :size="'20px'" />
                </div>
              </div>
              <div class="contact-info">
                <h4 class="contact-name">扫一扫</h4>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
            </div>
            <div class="contact-item" @click="onNearby">
              <div class="avatar-wrapper">
                <div class="icon-avatar">
                  <Icon name="map-pin" :size="'20px'" />
                </div>
              </div>
              <div class="contact-info">
                <h4 class="contact-name">附近的人</h4>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
            </div>
            <div class="contact-item" @click="onShake">
              <div class="avatar-wrapper">
                <div class="icon-avatar">
                  <Icon name="refresh-cw" :size="'20px'" />
                </div>
              </div>
              <div class="contact-info">
                <h4 class="contact-name">摇一摇</h4>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
            </div>
          </template>
          
          <template v-if="currentTab === 'me'">
            <div class="contact-item" @click="onProfile">
              <div class="avatar-wrapper">
                <img 
                  :src="currentUserAvatar || defaultAvatar" 
                  :alt="currentUserName"
                  class="contact-avatar"
                />
              </div>
              <div class="contact-info">
                <h4 class="contact-name">{{ currentUserName }}</h4>
                <p class="contact-last-message">个人信息</p>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
            </div>
            <div class="section-divider"></div>
            <div class="contact-item" @click="onServices">
              <div class="avatar-wrapper">
                <div class="icon-avatar">
                  <Icon name="credit-card" :size="'20px'" />
                </div>
              </div>
              <div class="contact-info">
                <h4 class="contact-name">服务</h4>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
            </div>
            <div class="contact-item" @click="onSettings">
              <div class="avatar-wrapper">
                <div class="icon-avatar">
                  <Icon name="settings" :size="'20px'" />
                </div>
              </div>
              <div class="contact-info">
                <h4 class="contact-name">设置</h4>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
            </div>
          </template>
        </div>

        <!-- A-Z 字母索引条 -->
        <div 
          v-if="currentTab === 'contacts' && !contactsCollapsed && indexLetters.length" 
          class="index-bar"
        >
          <span 
            v-for="letter in indexLetters" 
            :key="letter"
            :class="['index-letter', { active: activeLetter === letter }]"
            :data-letter="letter"
            @pointerdown="onIndexDown($event, letter)"
          >{{ letter }}</span>
        </div>

        <!-- 定位气泡 -->
        <transition name="bubble">
          <div v-if="bubbleLetter" class="index-bubble">{{ bubbleLetter }}</div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import Icon from './Icon.vue'

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

const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const currentTab = ref('chat')
const searchKeyword = ref('')
const showPlusMenu = ref(false)

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
  return props.friends.reduce((sum, f) => sum + (f.unreadCount || 0), 0) + 
         props.friendRequestCount
})

const unifiedContacts = computed(() => {
  const friendsWithType = props.friends.map(f => ({ ...f, isGroup: false }))
  const groupsWithType = props.groups.map(g => ({ ...g, isGroup: true }))
  return [...friendsWithType, ...groupsWithType].sort((a, b) => {
    const nameA = (a.name || '').toLowerCase()
    const nameB = (b.name || '').toLowerCase()
    return nameA.localeCompare(nameB, 'zh-CN')
  })
})

const filteredContacts = computed(() => {
  if (!searchKeyword.value) return unifiedContacts.value
  return unifiedContacts.value.filter(c => c.name.includes(searchKeyword.value))
})

/* ============ 通讯录：字母排序与分组 ============ */
// 拼音首字母：GB2312 一级汉字按拼音排序，通过 GBK 编码区间定位首字母
const PINYIN_BOUNDARY = '啊芭擦搭蛾发噶哈击喀垃妈拿哦啪期然撒塌挖昔压匝'
const PINYIN_LETTERS = 'ABCDEFGHJKLMNOPQRSTWXYZ'
const LETTER_ORDER = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('').concat('#')
const nameCollator = new Intl.Collator('zh-Hans-CN', { sensitivity: 'base' })
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
  for (let hi = 0xB0; hi <= 0xD7; hi++) {
    for (let lo = 0xA1; lo <= 0xFE; lo++) {
      const code = (hi << 8) | lo
      if (code > 0xD7F9) break
      bytes[0] = hi
      bytes[1] = lo
      const ch = decoder.decode(bytes)
      if (ch && ch !== '\uFFFD' && !map.has(ch)) map.set(ch, code)
    }
  }
  if (map.size === 0) return false
  gbkCodeMap = map
  boundaryCodes = PINYIN_BOUNDARY.split('').map(ch => map.get(ch))
  return true
}

// 取名称的拼音首字母：中文按拼音，英文取大写，其余归入 #
const getInitial = (name) => {
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

// 星标朋友（本地持久化）
const STARRED_KEY = 'im-starred-friends'
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
const starredFriends = computed(() => props.friends.filter(f => isStarred(f.id)))

const contactsCollapsed = ref(false)
const showStarOnly = ref(false)

const contactSource = computed(() => {
  let list = showStarOnly.value ? starredFriends.value : props.friends
  const keyword = searchKeyword.value.trim()
  if (keyword) {
    list = list.filter(f => (f.name || '').includes(keyword))
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
  return LETTER_ORDER
    .filter(letter => map.has(letter))
    .map(letter => ({
      letter,
      friends: map.get(letter).slice().sort((a, b) => nameCollator.compare(a.name || '', b.name || ''))
    }))
})

const indexLetters = computed(() => contactGroups.value.map(g => g.letter))

/* ============ 通讯录：字母索引定位 ============ */
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

const onStarEntryToggle = () => {
  showStarOnly.value = !showStarOnly.value
  activeLetter.value = ''
  if (contactListRef.value) {
    contactListRef.value.scrollTop = 0
  }
}

// 收藏面板由父组件独立控制，其打开时左侧导航不再高亮任何 tab，避免出现双高亮
const isTabActive = (tab) => !props.favoritesActive && currentTab.value === tab

const onNavChange = (tab) => {
  currentTab.value = tab
  if (tab !== 'contacts') {
    showStarOnly.value = false
  }
  emit('nav-change', tab)
}

const togglePlusMenu = () => {
  showPlusMenu.value = !showPlusMenu.value
}

const closePlusMenu = () => {
  showPlusMenu.value = false
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

const onMenuCreateGroup = () => {
  closePlusMenu()
  onCreateGroup()
}

const onMenuAddFriend = () => {
  closePlusMenu()
  onAddFriend()
}

const onMenuFriendRequests = () => {
  closePlusMenu()
  onOpenFriendRequests()
}

const onSearch = () => {
  emit('search', searchKeyword.value)
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

const onProfile = () => {
  emit('user-profile')
}

onUnmounted(() => {
  onIndexUp()
  if (bubbleTimer) clearTimeout(bubbleTimer)
})
</script>

<style scoped>
.im-sidebar {
  width: 320px;
  height: 100%;
  background: var(--theme-bg-surface);
  display: flex;
  overflow: hidden;
  transition: width var(--theme-duration) var(--theme-ease);
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* ============ 左侧导航竖栏 ============ */
.sidebar-left {
  width: 64px;
  height: 100%;
  box-sizing: border-box; /* 无全局 border-box 重置，避免纵向 padding 撑高竖栏 */
  background: var(--theme-bg-surface-2);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0 12px;
  flex-shrink: 0;
  border-right: 1px solid var(--theme-border);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  margin-bottom: 10px;
  overflow: hidden;
  flex-shrink: 0;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.user-avatar:hover {
  transform: scale(1.02);
  box-shadow: var(--theme-shadow-sm);
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.nav-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding-top: 14px;
}

.nav-item {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 10px;
  color: var(--theme-text-2);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: background 0.18s ease, color 0.18s ease;
}

.nav-item:hover {
  background: var(--theme-bg-hover);
}

.nav-item.active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

.nav-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--theme-danger);
  color: var(--theme-primary-text);
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
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

.sidebar-header {
  padding: 14px 16px 4px;
}

.sidebar-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin: 0;
  letter-spacing: 0.2px;
}

/* ---- 搜索行（微信风：圆角搜索框 + 圆形「+」） ---- */
.search-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 10px;
  display: flex;
  align-items: center;
  color: var(--theme-text-3);
  pointer-events: none;
}

.search-box .search-input {
  width: 100%;
  height: 32px;
  padding: 0 12px 0 32px;
  background: var(--theme-bg-surface-2);
  border: 1px solid transparent;
  border-radius: 8px;
  font-size: 13px;
  color: var(--theme-text-1);
  outline: none;
  box-sizing: border-box;
  transition: background 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.search-box .search-input::placeholder {
  color: var(--theme-text-3);
}

.search-box .search-input:focus {
  background: var(--theme-bg-surface);
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.plus-wrap {
  position: relative;
  z-index: 60;
  flex-shrink: 0;
}

.plus-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  transition: background 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
}

.plus-dot {
  position: absolute;
  top: -1px;
  right: -1px;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--theme-danger);
  border: 1.5px solid var(--theme-bg-surface);
  box-sizing: border-box;
}

.plus-btn:hover {
  background: var(--theme-bg-hover);
}

.plus-btn.active {
  background: var(--theme-bg-active);
  border-color: var(--theme-border-strong);
}

.menu-mask {
  position: fixed;
  inset: 0;
  z-index: 40;
  background: transparent;
}

.plus-menu {
  position: absolute;
  top: 40px;
  right: 0;
  min-width: 156px;
  padding: 6px;
  background: var(--theme-bg-elevated);
  border-radius: 10px;
  box-shadow: var(--theme-shadow-md);
  z-index: 61;
}

.menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border: none;
  background: transparent;
  border-radius: 7px;
  font-size: 13.5px;
  color: var(--theme-text-1);
  cursor: pointer;
  text-align: left;
  transition: background 0.15s ease;
}

.menu-item:hover {
  background: var(--theme-bg-hover);
}

.menu-icon {
  display: flex;
  align-items: center;
  color: var(--theme-text-2);
  flex-shrink: 0;
}

.menu-text {
  flex: 1;
  white-space: nowrap;
}

.menu-badge {
  margin-left: auto;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--theme-danger);
  color: var(--theme-primary-text);
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.menu-enter-active,
.menu-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.menu-enter-from,
.menu-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}

/* ============ 列表 ============ */
.contacts-actions {
  background: var(--theme-bg-surface);
  padding: 2px 0 6px;
}

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

.contact-item {
  display: flex;
  align-items: center;
  padding: 9px 12px;
  cursor: pointer;
  background: var(--theme-bg-surface);
  position: relative;
  transition: background 0.15s ease;
}

.contact-item:hover {
  background: var(--theme-bg-hover);
}

.contact-item.active {
  background: var(--theme-bg-active);
}

.contact-item.active:hover {
  background: var(--theme-bg-active);
}

.contact-content {
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 0;
}

.avatar-wrapper {
  position: relative;
  margin-right: 10px;
  flex-shrink: 0;
}

.contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.icon-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
  color: var(--theme-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.contact-main {
  flex: 1;
  min-width: 0;
}

.contact-row-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-info {
  flex: 1;
  min-width: 0;
}

.contact-name {
  flex: 1;
  min-width: 0;
  font-size: 14.5px;
  font-weight: 400;
  color: var(--theme-text-1);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.name-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.contact-time {
  flex-shrink: 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
}

.contact-tag {
  flex-shrink: 0;
  font-size: 10px;
  color: var(--theme-text-3);
  background: var(--theme-bg-surface-2);
  padding: 1px 5px;
  border-radius: 4px;
  font-weight: 400;
  line-height: 1.4;
}

.member-count {
  font-size: 12px;
  color: var(--theme-text-3);
  margin-right: 6px;
}

.contact-last-message {
  font-size: 12.5px;
  color: var(--theme-text-3);
  margin: 3px 0 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-btn {
  width: 26px;
  height: 26px;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-left: 2px;
  transition: background 0.15s ease, color 0.15s ease;
}

.detail-btn:hover {
  background: var(--theme-bg-active);
  color: var(--theme-text-2);
}

.arrow-icon {
  color: var(--theme-text-3);
  flex-shrink: 0;
}

/* ---- 通讯录分组入口计数 ---- */
.entry-count {
  flex-shrink: 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
  margin-left: 8px;
}

.entry-badge {
  flex-shrink: 0;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  margin-left: 8px;
  background: var(--theme-danger);
  color: var(--theme-primary-text);
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

/* ---- 联系人分组标题（可折叠） ---- */
.section-header {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 12px 8px;
  cursor: pointer;
  user-select: none;
  background: var(--theme-bg-surface);
  transition: background 0.15s ease;
}

.section-header:hover {
  background: var(--theme-bg-hover);
}

.section-chevron {
  display: flex;
  align-items: center;
  color: var(--theme-text-3);
  transform: rotate(90deg);
  transition: transform 0.18s ease;
}

.section-chevron.collapsed {
  transform: rotate(0deg);
}

.section-title {
  flex: 1;
  font-size: 12.5px;
  color: var(--theme-text-3);
  letter-spacing: 0.3px;
}

.section-count {
  flex-shrink: 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

/* ---- 星标朋友入口 ---- */
.star-entry .star-avatar {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.star-entry.active {
  background: var(--theme-primary-soft);
}

.star-entry.active:hover {
  background: var(--theme-primary-soft);
}

/* ---- 字母分组 ---- */
.letter-group {
  position: relative;
}

.letter-header {
  position: sticky;
  top: 0;
  z-index: 2;
  padding: 3px 12px;
  font-size: 12px;
  font-weight: 500;
  color: var(--theme-text-3);
  background: var(--theme-bg-surface-2);
  border-top: 1px solid var(--theme-hairline);
  border-bottom: 1px solid var(--theme-hairline);
}

/* ---- 星标按钮 ---- */
.star-btn {
  width: 26px;
  height: 26px;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-left: 2px;
  opacity: 0;
  transition: opacity 0.15s ease, background 0.15s ease, color 0.15s ease;
}

.contact-item:hover .star-btn {
  opacity: 1;
}

.star-btn:hover {
  background: var(--theme-bg-active);
  color: var(--theme-text-2);
}

.star-btn.active {
  opacity: 1;
  color: var(--theme-warning);
}

.star-btn.active :deep(svg) {
  fill: currentColor;
}

/* ---- A-Z 字母索引条 ---- */
.index-bar {
  position: absolute;
  top: 50%;
  right: 2px;
  transform: translateY(-50%);
  z-index: 6;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
  padding: 5px 3px;
  border-radius: 10px;
  background: var(--theme-bg-glass);
  backdrop-filter: blur(4px);
  touch-action: none;
  user-select: none;
}

.index-letter {
  width: 16px;
  height: 14px;
  line-height: 14px;
  text-align: center;
  font-size: 10px;
  font-weight: 500;
  color: var(--theme-text-2);
  cursor: pointer;
  border-radius: 50%;
  transition: background 0.12s ease, color 0.12s ease;
}

.index-letter.active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

/* ---- 定位气泡 ---- */
.index-bubble {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 20;
  width: 64px;
  height: 64px;
  border-radius: 14px;
  background: var(--theme-overlay);
  color: #ffffff;
  font-size: 30px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.bubble-enter-active,
.bubble-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.bubble-enter-from,
.bubble-leave-to {
  opacity: 0;
  transform: translate(-50%, -50%) scale(0.85);
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--theme-danger);
  color: var(--theme-primary-text);
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.group-badge {
  position: absolute;
  bottom: -3px;
  right: -3px;
  width: 16px;
  height: 16px;
  background: var(--theme-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1.5px solid var(--theme-bg-surface);
  color: var(--theme-primary-text);
  box-sizing: border-box;
}

.online-status {
  position: absolute;
  bottom: -1px;
  right: -1px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
  border: 2px solid var(--theme-bg-surface);
  box-sizing: border-box;
}

.online-status.online {
  background: var(--theme-success);
}

.section-divider {
  height: 8px;
  background: var(--theme-bg-surface-2);
  border-top: 1px solid var(--theme-hairline);
  border-bottom: 1px solid var(--theme-hairline);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--theme-text-3);
}

.empty-state .svg-icon {
  margin-bottom: 12px;
}

.empty-state p {
  margin: 0;
  font-size: 13px;
}
</style>
