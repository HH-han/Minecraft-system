<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  ChatDotRound,
  Collection,
  Compass,
  Setting,
  Star,
  User,
  Wallet
} from '@element-plus/icons-vue';
import { resolveMediaUrl } from '@/config';
import { useAuthStore } from '@/stores/auth';
import { useChatStore } from '@/stores/chat';
import ConversationList from './components/ConversationList.vue';
import ContactsColumn from './components/ContactsColumn.vue';
import ChatArea from './components/ChatArea.vue';
import ContactDetail from './components/ContactDetail.vue';
import FavoritesView from './components/FavoritesView.vue';
import MomentsView from './components/MomentsView.vue';
import MeView from './components/MeView.vue';
import ProfileView from './components/ProfileView.vue';
import ServicesView from './components/ServicesView.vue';
import SettingsDialog from './components/SettingsDialog.vue';
import AddFriendModal from './components/AddFriendModal.vue';
import CreateGroupModal from './components/CreateGroupModal.vue';

type TabKey = 'chat' | 'contacts' | 'favorites' | 'discover' | 'me';
type MeViewKey = 'overview' | 'profile' | 'services';

interface ContactSelection {
  type: 'friend' | 'group';
  id: number;
}

const { t } = useI18n();
const authStore = useAuthStore();
const chatStore = useChatStore();

const activeTab = ref<TabKey>('chat');
const meView = ref<MeViewKey>('overview');
const selectedContact = ref<ContactSelection | null>(null);
const infoOpen = ref(false);
const showAddFriend = ref(false);
const showCreateGroup = ref(false);
const showSettings = ref(false);

const navItems = computed(() => [
  { key: 'chat' as const, label: t('im.nav.chat'), icon: ChatDotRound },
  { key: 'contacts' as const, label: t('im.nav.contacts'), icon: User },
  { key: 'favorites' as const, label: t('im.nav.favorites'), icon: Collection },
  { key: 'discover' as const, label: t('im.nav.discover'), icon: Compass }
]);

const userAvatar = computed(() => resolveMediaUrl(authStore.user?.avatar));

const userInitial = computed(() =>
  (authStore.user?.nickname || authStore.user?.username || 'M').trim().charAt(0).toUpperCase()
);

const displayName = computed(
  () => authStore.user?.nickname || authStore.user?.username || t('im.profile.noAccount')
);

const points = computed(() => Number(authStore.user?.points ?? 0));

const activeConversation = computed(() => chatStore.activeConversation);

const chatInfoTarget = computed<ContactSelection | null>(() => {
  const conversation = activeConversation.value;
  if (!conversation) return null;
  return { type: conversation.type === 'group' ? 'group' : 'friend', id: conversation.targetId };
});

const contactEmptyDesc = computed(() => t('im.contacts.emptyDesc'));

function badgeFor(key: TabKey): number {
  if (key === 'chat') return chatStore.totalUnread;
  if (key === 'contacts') return chatStore.pendingRequestCount;
  return 0;
}

function switchTab(tab: TabKey): void {
  activeTab.value = tab;
  if (tab !== 'contacts') selectedContact.value = null;
  if (tab !== 'chat') infoOpen.value = false;
  if (tab === 'me') meView.value = 'overview';
}

function handleSelectContact(selection: ContactSelection): void {
  selectedContact.value = selection;
  infoOpen.value = false;
}

async function openChatFromContact(selection: ContactSelection): Promise<void> {
  const key = selection.type === 'friend' ? `single-${selection.id}` : `group-${selection.id}`;
  activeTab.value = 'chat';
  selectedContact.value = null;
  infoOpen.value = false;
  await chatStore.selectConversation(key);
}

async function handleContactChat(type: 'friend' | 'group', id: number): Promise<void> {
  await openChatFromContact({ type, id });
}

async function openConversationInfo(key: string): Promise<void> {
  await chatStore.selectConversation(key);
  infoOpen.value = true;
}

function openMeView(view: MeViewKey): void {
  activeTab.value = 'me';
  meView.value = view;
}

function openSettings(): void {
  showSettings.value = true;
}

/** 账号就绪 / 切换后重新绑定 WS 并拉取通讯录（登录其他账号也能正确加载） */
watch(
  () => chatStore.currentUserId,
  async (id, prev) => {
    if (!id) return;
    if (prev && prev !== id) chatStore.reset();
    chatStore.bindWs();
    await chatStore.loadContacts();
  },
  { immediate: true }
);

onBeforeUnmount(() => {
  chatStore.unbindWs();
});
</script>

<template>
  <div class="im-shell">
    <!-- 左侧导航栏 -->
    <nav class="rail">
      <button class="rail-avatar" :class="{ 'is-active': activeTab === 'me' }" @click="switchTab('me')">
        <img v-if="userAvatar" :src="userAvatar" :alt="displayName" />
        <span v-else>{{ userInitial }}</span>
        <span class="rail-avatar__dot" :class="{ offline: !chatStore.connected }" />
      </button>

      <div class="rail-items">
        <el-tooltip
          v-for="item in navItems"
          :key="item.key"
          :content="item.label"
          placement="right"
          :show-after="200"
        >
          <button
            class="rail-item"
            :class="{ 'is-active': activeTab === item.key }"
            @click="switchTab(item.key)"
          >
            <el-icon :size="21"><component :is="item.icon" /></el-icon>
            <span v-if="badgeFor(item.key)" class="rail-badge">
              {{ badgeFor(item.key) > 99 ? '99+' : badgeFor(item.key) }}
            </span>
          </button>
        </el-tooltip>
      </div>

      <el-tooltip :content="t('im.nav.settings')" placement="right" :show-after="200">
        <button class="rail-item rail-settings" @click="openSettings">
          <el-icon :size="20"><Setting /></el-icon>
        </button>
      </el-tooltip>
    </nav>

    <div class="im-main">
      <!-- 聊天 -->
      <template v-if="activeTab === 'chat'">
        <ConversationList
          @open-add-friend="showAddFriend = true"
          @open-create-group="showCreateGroup = true"
          @open-info="openConversationInfo"
        />
        <ChatArea :info-open="infoOpen" @toggle-info="infoOpen = !infoOpen" />
        <ContactDetail
          v-if="infoOpen && chatInfoTarget"
          :type="chatInfoTarget.type"
          :target-id="chatInfoTarget.id"
          @close="infoOpen = false"
          @chat="handleContactChat"
          @changed="chatStore.loadContacts()"
        />
      </template>

      <!-- 通讯录 -->
      <template v-else-if="activeTab === 'contacts'">
        <ContactsColumn
          :selected="selectedContact"
          @select-friend="(id: number) => handleSelectContact({ type: 'friend', id })"
          @select-group="(id: number) => handleSelectContact({ type: 'group', id })"
          @open-add-friend="showAddFriend = true"
          @open-create-group="showCreateGroup = true"
        />
        <main class="content-area">
          <ContactDetail
            v-if="selectedContact"
            :type="selectedContact.type"
            :target-id="selectedContact.id"
            @close="selectedContact = null"
            @chat="handleContactChat"
            @changed="chatStore.loadContacts()"
          />
          <div v-else class="im-empty">
            <div class="im-empty__icon">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <p class="im-empty__title">{{ t('im.contacts.emptyTitle') }}</p>
            <p class="im-empty__desc">{{ contactEmptyDesc }}</p>
          </div>
        </main>
      </template>

      <!-- 收藏 -->
      <FavoritesView v-else-if="activeTab === 'favorites'" />

      <!-- 朋友圈 -->
      <MomentsView v-else-if="activeTab === 'discover'" />

      <!-- 我 -->
      <template v-else>
        <MeView
          @profile="openMeView('profile')"
          @services="openMeView('services')"
          @favorites="switchTab('favorites')"
          @settings="openSettings"
        />

        <ProfileView v-if="meView === 'profile'" @back="meView = 'overview'" />
        <ServicesView v-else-if="meView === 'services'" @back="meView = 'overview'" />

        <main v-else class="content-area me-home">
          <div class="im-scroll me-home__scroll">
            <div class="me-hero">
              <div class="im-avatar xl round me-hero__avatar">
                <img v-if="userAvatar" :src="userAvatar" :alt="displayName" />
                <template v-else>{{ userInitial }}</template>
              </div>
              <div class="me-hero__body">
                <h2 class="me-hero__name">{{ displayName }}</h2>
                <p class="me-hero__sign">{{ authStore.user?.signature || t('im.me.defaultSignature') }}</p>
                <div class="me-hero__chips">
                  <span class="im-chip">{{ t('im.me.points') }} · {{ points }}</span>
                  <span class="im-chip">{{ authStore.user?.account || t('im.profile.noAccount') }}</span>
                </div>
              </div>
            </div>

            <div class="me-stats">
              <button class="me-stat" @click="switchTab('contacts')">
                <span class="me-stat__value">{{ chatStore.friends.length }}</span>
                <span class="me-stat__label">{{ t('im.overview.friends') }}</span>
              </button>
              <button class="me-stat" @click="switchTab('contacts')">
                <span class="me-stat__value">{{ chatStore.groups.length }}</span>
                <span class="me-stat__label">{{ t('im.overview.groups') }}</span>
              </button>
              <button class="me-stat" @click="openMeView('services')">
                <span class="me-stat__value">{{ points }}</span>
                <span class="me-stat__label">{{ t('im.overview.points') }}</span>
              </button>
            </div>

            <div class="me-quick">
              <button class="me-quick__item" @click="openMeView('services')">
                <span class="me-quick__icon wallet">
                  <el-icon :size="18"><Wallet /></el-icon>
                </span>
                <span>{{ t('im.me.services') }}</span>
              </button>
              <button class="me-quick__item" @click="switchTab('favorites')">
                <span class="me-quick__icon star">
                  <el-icon :size="18"><Star /></el-icon>
                </span>
                <span>{{ t('im.me.favorites') }}</span>
              </button>
              <button class="me-quick__item" @click="openSettings">
                <span class="me-quick__icon setting">
                  <el-icon :size="18"><Setting /></el-icon>
                </span>
                <span>{{ t('im.me.settings') }}</span>
              </button>
            </div>
          </div>
        </main>
      </template>
    </div>

    <AddFriendModal v-model="showAddFriend" @sent="chatStore.loadContacts()" />
    <CreateGroupModal v-model="showCreateGroup" @created="chatStore.loadContacts()" />
    <SettingsDialog v-model="showSettings" />
  </div>
</template>

<style scoped>
.im-shell {
  display: flex;
  width: 100%;
  height: 100%;
  background: var(--theme-bg-app);
  color: var(--theme-text-1);
  overflow: hidden;
}

/* ---- 导航栏 ---- */
.rail {
  width: 72px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 18px 0 16px;
  background: var(--theme-rail-bg);
  border-right: 1px solid var(--theme-hairline);
}

.rail-avatar {
  position: relative;
  width: 44px;
  height: 44px;
  padding: 0;
  border: none;
  border-radius: 14px;
  display: grid;
  place-items: center;
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  background: var(--theme-accent-gradient);
  cursor: pointer;
  overflow: hidden;
  box-shadow: var(--theme-shadow-sm);
  transition: transform var(--theme-duration-fast) var(--theme-ease);
}

.rail-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.rail-avatar:hover {
  transform: scale(1.05);
}

.rail-avatar.is-active {
  box-shadow: var(--theme-ring);
}

.rail-avatar__dot {
  position: absolute;
  right: 2px;
  bottom: 2px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--theme-online);
  border: 1.5px solid var(--theme-rail-bg);
}

.rail-avatar__dot.offline {
  background: var(--theme-text-3);
}

.rail-items {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 22px;
}

.rail-item {
  position: relative;
  width: 46px;
  height: 46px;
  border: none;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease), transform var(--theme-duration-fast);
}

.rail-item:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.rail-item.is-active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.rail-item:active {
  transform: scale(0.94);
}

.rail-badge {
  position: absolute;
  top: 5px;
  right: 5px;
  min-width: 15px;
  height: 15px;
  padding: 0 4px;
  border-radius: 99px;
  background: var(--theme-danger);
  color: #fff;
  font-size: 9px;
  font-weight: 600;
  line-height: 15px;
  text-align: center;
}

.rail-settings {
  color: var(--theme-text-3);
}

/* ---- 主区域 ---- */
.im-main {
  flex: 1;
  min-width: 0;
  display: flex;
  height: 100%;
  overflow: hidden;
}

.content-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--theme-bg-app);
}

/* ---- 我的主页 ---- */
.me-home {
  overflow: hidden;
}

.me-home__scroll {
  padding: 24px 28px 36px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.me-hero {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px 26px;
  border-radius: 18px;
  border: 1px solid var(--theme-hairline);
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--theme-primary) 12%, var(--theme-bg-surface)),
    var(--theme-bg-surface) 64%
  );
  box-shadow: var(--theme-shadow-sm);
}

.me-hero__avatar {
  box-shadow: 0 16px 34px -20px color-mix(in srgb, var(--theme-primary) 70%, transparent);
}

.me-hero__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.me-hero__name {
  margin: 0;
  font-size: 21px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.me-hero__sign {
  margin: 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.me-hero__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.me-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.me-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 10px;
  border-radius: 14px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
  cursor: pointer;
  transition: transform var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease);
}

.me-stat:hover {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, var(--theme-primary) 30%, transparent);
}

.me-stat__value {
  font-size: 22px;
  font-weight: 800;
  color: var(--theme-primary);
}

.me-stat__label {
  font-size: 11.5px;
  color: var(--theme-text-3);
}

.me-quick {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.me-quick__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-radius: 14px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  font-size: 12.5px;
  cursor: pointer;
  text-align: left;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.me-quick__item:hover {
  background: var(--theme-bg-hover);
}

.me-quick__icon {
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: #fff;
}

.me-quick__icon.wallet {
  background: var(--theme-wallet-gradient);
}

.me-quick__icon.star {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
}

.me-quick__icon.setting {
  background: linear-gradient(135deg, #94a3b8, #64748b);
}

@media (max-width: 900px) {
  .me-quick,
  .me-stats {
    grid-template-columns: 1fr;
  }
}
</style>
