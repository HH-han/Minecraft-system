<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElIcon } from 'element-plus';
import { ChatDotRound, InfoFilled, Plus, Search, User, UserFilled } from '@element-plus/icons-vue';
import { resolveMediaUrl } from '@/config';
import { useChatStore } from '@/stores/chat';
import { formatListTime } from '@/utils/format';
import type { Conversation } from '@/types/im';

const emit = defineEmits<{
  openAddFriend: [];
  openCreateGroup: [];
  openInfo: [key: string];
}>();

const { t, locale } = useI18n();
const chatStore = useChatStore();
const keyword = ref('');

const filtered = computed<Conversation[]>(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return chatStore.conversations;
  return chatStore.conversations.filter(
    (item) =>
      item.name.toLowerCase().includes(kw) ||
      (item.lastMessage ?? '').toLowerCase().includes(kw)
  );
});

function avatarOf(conversation: Conversation): string | undefined {
  return resolveMediaUrl(conversation.avatar);
}

function initialOf(conversation: Conversation): string {
  return (conversation.name || '?').trim().charAt(0).toUpperCase();
}

function previewOf(conversation: Conversation): string {
  if (conversation.lastMessageType === 'image') return t('im.chat.imagePreview');
  if (conversation.lastMessageType === 'file') return t('im.chat.filePreview');
  return conversation.lastMessage || t('im.conversation.noMessage');
}

function timeOf(conversation: Conversation): string {
  return formatListTime(conversation.lastTime, locale.value, t('im.time.yesterday'));
}

function select(conversation: Conversation): void {
  void chatStore.selectConversation(conversation.key);
}

function handleCommand(command: string): void {
  if (command === 'add-friend') emit('openAddFriend');
  if (command === 'create-group') emit('openCreateGroup');
}
</script>

<template>
  <aside class="im-column conversation-column">
    <header class="im-column-head">
      <h2 class="im-column-title">{{ t('im.conversation.title') }}</h2>
      <div class="im-column-actions">
        <el-dropdown trigger="click" placement="bottom-end" @command="handleCommand">
          <button class="im-icon-btn is-round" :title="t('im.common.more')">
            <el-icon :size="16"><Plus /></el-icon>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="add-friend">
                <el-icon><User /></el-icon>
                {{ t('im.conversation.addFriend') }}
              </el-dropdown-item>
              <el-dropdown-item command="create-group">
                <el-icon><ChatDotSquare /></el-icon>
                {{ t('im.conversation.newChat') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="im-search-row">
      <el-input
        v-model="keyword"
        :placeholder="t('im.conversation.search')"
        clearable
      >
        <template #prefix>
          <el-icon :size="14"><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <div class="im-scroll">
      <div v-if="filtered.length" class="im-list">
        <div
          v-for="conversation in filtered"
          :key="conversation.key"
          class="im-list-item"
          :class="{ 'is-active': chatStore.activeKey === conversation.key }"
          @click="select(conversation)"
        >
          <div class="im-avatar">
            <img
              v-if="avatarOf(conversation)"
              :src="avatarOf(conversation)"
              :alt="conversation.name"
            />
            <template v-else>{{ initialOf(conversation) }}</template>
            <span
              v-if="conversation.type === 'single'"
              class="im-status-dot"
              :class="{ offline: !conversation.online }"
            />
            <span v-else class="im-group-badge">{{ conversation.memberCount ?? 0 }}</span>
          </div>

          <div class="im-list-item__body">
            <div class="im-list-item__top">
              <span class="im-list-item__name">{{ conversation.name }}</span>
              <span class="im-list-item__time">{{ timeOf(conversation) }}</span>
            </div>
            <div class="im-list-item__bottom">
              <span class="im-list-item__preview">{{ previewOf(conversation) }}</span>
              <span v-if="conversation.unread" class="im-badge">
                {{ conversation.unread > 99 ? '99+' : conversation.unread }}
              </span>
              <button
                class="conversation-info"
                :title="t('im.conversation.info')"
                @click.stop="emit('openInfo', conversation.key)"
              >
                <el-icon :size="14"><InfoFilled /></el-icon>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="im-empty">
        <div class="im-empty__icon">
          <el-icon :size="30"><UserFilled /></el-icon>
        </div>
        <p class="im-empty__title">
          {{ keyword ? t('im.conversation.noResult') : t('im.conversation.empty') }}
        </p>
        <p class="im-empty__desc">
          {{ keyword ? t('im.conversation.noResultDesc') : t('im.conversation.emptyDesc') }}
        </p>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.conversation-column {
  width: 292px;
  flex-shrink: 0;
  border-right: 1px solid var(--theme-hairline);
}

.conversation-info {
  width: 22px;
  height: 22px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--theme-text-3);
  display: grid;
  place-items: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity var(--theme-duration-fast) var(--theme-ease),
    background var(--theme-duration-fast) var(--theme-ease);
}

.im-list-item:hover .conversation-info,
.im-list-item.is-active .conversation-info {
  opacity: 1;
}

.conversation-info:hover {
  background: var(--theme-bg-muted);
  color: var(--theme-text-1);
}
</style>
