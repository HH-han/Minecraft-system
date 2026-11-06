<script setup lang="ts">
import { nextTick, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { Promotion } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/auth';
import { useChatStore } from '@/stores/chat';
import type { ChatMessage } from '@/types/im';

const { t } = useI18n();
const authStore = useAuthStore();
const chatStore = useChatStore();

const draft = ref('');
const sending = ref(false);
const scrollRef = ref<HTMLElement | null>(null);

function isMine(msg: ChatMessage): boolean {
  return msg.senderId === authStore.userId;
}

function formatTime(msg: ChatMessage): string {
  const raw = msg.sendTime ?? msg.createTime;
  if (!raw) return '';
  const date = new Date(raw);
  if (Number.isNaN(date.getTime())) return '';
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
}

async function send(): Promise<void> {
  const text = draft.value.trim();
  if (!text || sending.value) return;
  sending.value = true;
  try {
    await chatStore.sendMessage(text);
    draft.value = '';
  } catch {
    ElMessage.error(t('im.chat.sendFailed'));
  } finally {
    sending.value = false;
  }
}

function onKeydown(event: KeyboardEvent): void {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    void send();
  }
}

watch(
  () => chatStore.activeMessages.length,
  async () => {
    await nextTick();
    const el = scrollRef.value;
    if (el) el.scrollTop = el.scrollHeight;
  },
  { immediate: true }
);

watch(
  () => chatStore.activeKey,
  async () => {
    await nextTick();
    const el = scrollRef.value;
    if (el) el.scrollTop = el.scrollHeight;
  }
);
</script>

<template>
  <div class="chat-area">
    <template v-if="chatStore.activeConversation">
      <header class="chat-header">
        <h3>{{ chatStore.activeConversation.name }}</h3>
        <span class="chat-type">
          {{ chatStore.activeConversation.type === 'group' ? t('im.chat.group') : t('im.chat.single') }}
        </span>
      </header>

      <div ref="scrollRef" class="message-scroll">
        <div
          v-for="(msg, index) in chatStore.activeMessages"
          :key="msg.id ?? index"
          class="message-row"
          :class="{ mine: isMine(msg) }"
        >
          <div class="bubble">
            <p class="bubble-content">{{ msg.content }}</p>
            <span class="bubble-time">{{ formatTime(msg) }}</span>
          </div>
        </div>
      </div>

      <footer class="chat-input">
        <el-input
          v-model="draft"
          type="textarea"
          :rows="3"
          resize="none"
          :placeholder="t('im.chat.inputPlaceholder')"
          @keydown="onKeydown"
        />
        <div class="input-actions">
          <el-button
            type="primary"
            :icon="Promotion"
            :loading="sending"
            :disabled="!draft.trim()"
            @click="send"
          >
            {{ t('im.chat.send') }}
          </el-button>
        </div>
      </footer>
    </template>

    <div v-else class="chat-empty">
      <div class="empty-mark">MC</div>
      <h3>{{ t('im.chat.emptyTitle') }}</h3>
      <p>{{ t('im.chat.emptyDesc') }}</p>
    </div>
  </div>
</template>

<style scoped>
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--theme-bg-app);
}

.chat-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
}

.chat-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
}

.chat-type {
  font-size: 12px;
  color: var(--theme-text-3);
}

.message-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.message-row {
  display: flex;
  justify-content: flex-start;
}

.message-row.mine {
  justify-content: flex-end;
}

.bubble {
  max-width: 62%;
  padding: 10px 14px;
  border-radius: 4px 14px 14px 14px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  box-shadow: var(--theme-shadow-sm);
}

.message-row.mine .bubble {
  border-radius: 14px 4px 14px 14px;
  background: var(--theme-primary);
  border-color: transparent;
  color: var(--theme-primary-text);
}

.bubble-content {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
}

.bubble-time {
  display: block;
  margin-top: 4px;
  font-size: 10px;
  opacity: 0.6;
  text-align: right;
}

.chat-input {
  padding: 12px 16px 14px;
  border-top: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: var(--theme-text-3);
}

.empty-mark {
  width: 72px;
  height: 72px;
  display: grid;
  place-items: center;
  font-size: 26px;
  font-weight: 800;
  color: var(--theme-primary-text);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-gradient);
  margin-bottom: 8px;
}

.chat-empty h3 {
  margin: 0;
  font-size: 18px;
  color: var(--theme-text-1);
}

.chat-empty p {
  margin: 0;
  font-size: 13px;
}
</style>
