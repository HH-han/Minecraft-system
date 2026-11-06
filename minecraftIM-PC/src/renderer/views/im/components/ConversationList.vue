<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useChatStore } from '@/stores/chat';

const { t } = useI18n();
const chatStore = useChatStore();

const keyword = ref('');

const filtered = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return chatStore.conversations;
  return chatStore.conversations.filter((item) => item.name.toLowerCase().includes(kw));
});

function formatTime(value?: string): string {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  const now = new Date();
  const sameDay = date.toDateString() === now.toDateString();
  if (sameDay) {
    return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  }
  return `${date.getMonth() + 1}/${date.getDate()}`;
}
</script>

<template>
  <div class="conversation-list">
    <div class="search-box">
      <el-input
        v-model="keyword"
        :placeholder="t('im.conversation.search')"
        size="small"
        clearable
      />
    </div>

    <div class="list-scroll">
      <el-empty
        v-if="!filtered.length"
        :description="t('im.conversation.empty')"
        :image-size="90"
      />
      <button
        v-for="item in filtered"
        :key="item.key"
        type="button"
        class="conversation-item"
        :class="{ active: chatStore.activeKey === item.key }"
        @click="chatStore.selectConversation(item.key)"
      >
        <span class="avatar" :class="{ group: item.type === 'group' }">
          <img v-if="item.avatar" :src="item.avatar" :alt="item.name" />
          <template v-else>{{ item.name[0] }}</template>
        </span>
        <span class="meta">
          <span class="row">
            <span class="name">{{ item.name }}</span>
            <span class="time">{{ formatTime(item.lastTime) }}</span>
          </span>
          <span class="row">
            <span class="preview">{{ item.lastMessage || '…' }}</span>
            <span v-if="item.unread > 0" class="badge">{{ item.unread > 99 ? '99+' : item.unread }}</span>
          </span>
        </span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.conversation-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.search-box {
  padding: 0 14px 10px;
}

.list-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px 10px;
}

.conversation-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 10px;
  border: none;
  border-radius: var(--theme-radius-sm);
  background: transparent;
  cursor: pointer;
  text-align: left;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.conversation-item:hover {
  background: var(--theme-bg-hover);
}

.conversation-item.active {
  background: var(--theme-primary-soft);
}

.avatar {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 10px;
  display: grid;
  place-items: center;
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  background: var(--theme-gradient);
  overflow: hidden;
}

.avatar.group {
  background: linear-gradient(135deg, var(--theme-accent-3), var(--theme-primary));
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.name {
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.time {
  font-size: 11px;
  color: var(--theme-text-3);
  flex-shrink: 0;
}

.preview {
  font-size: 12px;
  color: var(--theme-text-3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.badge {
  flex-shrink: 0;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: var(--theme-danger);
  color: #fff;
  font-size: 11px;
  line-height: 18px;
  text-align: center;
}
</style>
