<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  ChatDotRound,
  Collection,
  Compass,
  Setting,
  User
} from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/auth';
import { useChatStore } from '@/stores/chat';
import ConversationList from './components/ConversationList.vue';
import ContactsColumn from './components/ContactsColumn.vue';
import FavoritesColumn from './components/FavoritesColumn.vue';
import MomentsView from './components/MomentsView.vue';
import MeView from './components/MeView.vue';
import ChatArea from './components/ChatArea.vue';
import ContactDetail from './components/ContactDetail.vue';
import AddFriendModal from './components/AddFriendModal.vue';
import CreateGroupModal from './components/CreateGroupModal.vue';
import SettingsView from '@/views/SettingsView.vue';

type TabKey = 'chat' | 'contacts' | 'favorites' | 'discover' | 'me' | 'settings';

interface ContactSelection {
  type: 'friend' | 'group';
  id: number;
}

const { t } = useI18n();
const authStore = useAuthStore();
const chatStore = useChatStore();

const activeTab = ref<TabKey>('chat');
const selectedContact = ref<ContactSelection | null>(null);
const showAddFriend = ref(false);
const showCreateGroup = ref(false);

const navItems = computed(() => [
  { key: 'chat' as const, label: t('im.nav.chat'), icon: ChatDotRound },
  { key: 'contacts' as const, label: t('im.nav.contacts'), icon: User },
  { key: 'favorites' as const, label: t('im.nav.favorites'), icon: Collection },
  { key: 'discover' as const, label: t('im.nav.discover'), icon: Compass }
]);

const columnTitle = computed(() => {
  switch (activeTab.value) {
    case 'chat':
      return t('im.conversation.title');
    case 'contacts':
      return t('im.contacts.title');
    case 'favorites':
      return t('im.favorites.title');
    default:
      return '';
  }
});

const showListColumn = computed(
  () => activeTab.value === 'chat' || activeTab.value === 'contacts' || activeTab.value === 'favorites'
);

function switchTab(tab: TabKey): void {
  activeTab.value = tab;
  if (tab !== 'contacts') selectedContact.value = null;
}

function handleSelectContact(selection: ContactSelection): void {
  selectedContact.value = selection;
}

async function openChatFromContact(selection: ContactSelection): Promise<void> {
  const key = selection.type === 'friend' ? `single-${selection.id}` : `group-${selection.id}`;
  activeTab.value = 'chat';
  selectedContact.value = null;
  await chatStore.selectConversation(key);
}

onMounted(async () => {
  chatStore.bindWs();
  await chatStore.loadContacts();
});

onBeforeUnmount(() => {
  chatStore.unbindWs();
});
</script>

<template>
  <div class="im-shell">
    <!-- 左侧导航栏 -->
    <nav class="nav-rail">
      <button
        type="button"
        class="nav-avatar"
        :class="{ active: activeTab === 'me' }"
        :title="t('im.nav.me')"
        @click="switchTab('me')"
      >
        <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" alt="avatar" />
        <span v-else>{{ (authStore.user?.nickname ?? authStore.user?.username ?? 'M')[0] }}</span>
      </button>

      <div class="nav-items">
        <el-tooltip
          v-for="item in navItems"
          :key="item.key"
          :content="item.label"
          placement="right"
          :show-after="200"
        >
          <button
            type="button"
            class="nav-item"
            :class="{ active: activeTab === item.key }"
            @click="switchTab(item.key)"
          >
            <el-icon :size="22"><component :is="item.icon" /></el-icon>
            <span
              v-if="item.key === 'chat' && chatStore.conversations.some((c) => c.unread > 0)"
              class="nav-dot"
            />
          </button>
        </el-tooltip>
      </div>

      <el-tooltip :content="t('im.nav.settings')" placement="right" :show-after="200">
        <button
          type="button"
          class="nav-item nav-settings"
          :class="{ active: activeTab === 'settings' }"
          @click="switchTab('settings')"
        >
          <el-icon :size="22"><Setting /></el-icon>
        </button>
      </el-tooltip>
    </nav>

    <!-- 中间列表列 -->
    <section v-if="showListColumn" class="list-column">
      <header class="column-header">
        <h2>{{ columnTitle }}</h2>
        <div v-if="activeTab === 'contacts'" class="column-actions">
          <el-tooltip :content="t('im.contacts.addFriend')" placement="bottom">
            <button type="button" class="action-btn" @click="showAddFriend = true">
              <span class="action-icon">+</span>
            </button>
          </el-tooltip>
          <el-tooltip :content="t('im.contacts.createGroup')" placement="bottom">
            <button type="button" class="action-btn" @click="showCreateGroup = true">
              <el-icon :size="15"><ChatDotRound /></el-icon>
            </button>
          </el-tooltip>
        </div>
      </header>

      <ConversationList v-if="activeTab === 'chat'" />
      <ContactsColumn
        v-else-if="activeTab === 'contacts'"
        :selected="selectedContact"
        @select="handleSelectContact"
        @open-requests="selectedContact = null"
      />
      <FavoritesColumn v-else-if="activeTab === 'favorites'" />
    </section>

    <!-- 右侧内容区 -->
    <main class="content-area">
      <template v-if="activeTab === 'chat'">
        <ChatArea />
      </template>
      <template v-else-if="activeTab === 'contacts'">
        <ContactDetail
          v-if="selectedContact"
          :type="selectedContact.type"
          :target-id="selectedContact.id"
          @send-message="openChatFromContact"
        />
        <div v-else class="content-empty">
          <p>{{ t('im.chat.emptyDesc') }}</p>
        </div>
      </template>
      <MomentsView v-else-if="activeTab === 'discover'" />
      <MeView v-else-if="activeTab === 'me'" @open-settings="switchTab('settings')" />
      <SettingsView v-else-if="activeTab === 'settings'" />
      <div v-else-if="activeTab === 'favorites'" class="content-empty">
        <p>{{ t('im.favorites.empty') }}</p>
      </div>
    </main>

    <AddFriendModal v-model="showAddFriend" @sent="chatStore.loadContacts()" />
    <CreateGroupModal v-model="showCreateGroup" @created="chatStore.loadContacts()" />
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
.nav-rail {
  width: 64px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 14px 0;
  background: var(--theme-bg-surface);
  border-right: 1px solid var(--theme-hairline);
}

.nav-avatar {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-primary-text);
  background: var(--theme-gradient);
  cursor: pointer;
  overflow: hidden;
  transition: transform var(--theme-duration-fast) var(--theme-ease);
}

.nav-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.nav-avatar:hover {
  transform: scale(1.06);
}

.nav-avatar.active {
  box-shadow: var(--theme-ring);
}

.nav-items {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 26px;
}

.nav-item {
  position: relative;
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  transition: all var(--theme-duration-fast) var(--theme-ease);
}

.nav-item:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.nav-item.active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.nav-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--theme-danger);
}

/* ---- 列表列 ---- */
.list-column {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-surface);
  border-right: 1px solid var(--theme-hairline);
  overflow: hidden;
}

.column-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 18px 12px;
}

.column-header h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
}

.column-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: 1px solid var(--theme-border);
  border-radius: 8px;
  display: grid;
  place-items: center;
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  cursor: pointer;
  transition: all var(--theme-duration-fast) var(--theme-ease);
}

.action-btn:hover {
  color: var(--theme-primary);
  border-color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.action-icon {
  font-size: 16px;
  line-height: 1;
}

/* ---- 内容区 ---- */
.content-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-empty {
  flex: 1;
  display: grid;
  place-items: center;
  color: var(--theme-text-3);
  font-size: 14px;
}
</style>
