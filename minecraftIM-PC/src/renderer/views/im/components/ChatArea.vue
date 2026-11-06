<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  ChatDotRound,
  Document,
  Folder,
  Microphone,
  MoreFilled,
  Phone,
  Picture,
  Search,
  VideoCamera
} from '@element-plus/icons-vue';
import { uploadImFile } from '@/api/chat';
import { resolveMediaUrl } from '@/config';
import { useAuthStore } from '@/stores/auth';
import { useChatStore } from '@/stores/chat';
import type { ChatMessage } from '@/types/im';
import { formatDaySeparator, toDate } from '@/utils/format';

const props = defineProps<{
  infoOpen: boolean;
}>();

const emit = defineEmits<{
  toggleInfo: [];
}>();

const { t, locale } = useI18n();
const authStore = useAuthStore();
const chatStore = useChatStore();

const draft = ref('');
const sending = ref(false);
const uploading = ref(false);
const emojiVisible = ref(false);
const scrollRef = ref<HTMLElement | null>(null);
const textareaRef = ref<HTMLTextAreaElement | null>(null);
const imageInput = ref<HTMLInputElement | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);

const EMOJIS = [
  '😀', '😄', '😁', '😆', '😅', '😂', '🤣', '😊', '😇', '🙂',
  '😉', '😍', '🥰', '😘', '😗', '😙', '😋', '😛', '😜', '🤪',
  '😎', '🤩', '🥳', '😏', '😒', '😞', '😔', '😟', '😕', '🙁',
  '😣', '😖', '😫', '😩', '🥺', '😢', '😭', '😤', '😠', '😡',
  '🤗', '🤔', '🤨', '😐', '😑', '😶', '🙄', '😯', '😲', '😮',
  '🤐', '😴', '😪', '😵', '🤯', '🤠', '🥱', '😷', '🤒', '🤕',
  '👍', '👎', '👏', '🙌', '🤝', '🙏', '💪', '✨', '🌹', '🎉',
  '❤️', '💔', '🔥', '💯', '⭐', '☀️', '🌙', '☕', '🍺', '🎁'
];

const conversation = computed(() => chatStore.activeConversation);
const messages = computed(() => chatStore.activeMessages);
const isGroup = computed(() => conversation.value?.type === 'group');

const headerStatus = computed(() => {
  if (!conversation.value) return '';
  if (isGroup.value) {
    return t('im.chat.groupMembers', { count: conversation.value.memberCount ?? 0 });
  }
  return conversation.value.online ? t('im.common.online') : t('im.common.offline');
});

function isMine(message: ChatMessage): boolean {
  return message.senderId === chatStore.currentUserId;
}

function avatarOf(url?: string): string | undefined {
  return resolveMediaUrl(url);
}

function initialOf(name?: string): string {
  return (name || '?').trim().charAt(0).toUpperCase();
}

function nameOf(message: ChatMessage): string {
  if (isMine(message)) {
    return authStore.user?.nickname || authStore.user?.username || '';
  }
  if (isGroup.value) {
    return message.senderName || t('im.common.member');
  }
  const conv = conversation.value;
  if (conv?.type === 'single') {
    const friend = chatStore.findFriend(conv.targetId);
    return friend?.remark || friend?.username || conv.name;
  }
  return conv?.name ?? '';
}

function avatarOfMessage(message: ChatMessage): string | undefined {
  if (isMine(message)) return authStore.user?.avatar;
  if (isGroup.value) return message.senderAvatar;
  const conv = conversation.value;
  if (conv?.type === 'single') return chatStore.findFriend(conv.targetId)?.avatar;
  return undefined;
}

function todayLabel(message: ChatMessage, index: number): string | null {
  const date = toDate(message.createTime);
  if (!date) return null;
  const previous = index > 0 ? toDate(messages.value[index - 1]?.createTime) : null;
  if (
    previous &&
    previous.getFullYear() === date.getFullYear() &&
    previous.getMonth() === date.getMonth() &&
    previous.getDate() === date.getDate()
  ) {
    return null;
  }
  return formatDaySeparator(message.createTime, locale.value);
}

function scrollToBottom(): void {
  const el = scrollRef.value;
  if (!el) return;
  el.scrollTop = el.scrollHeight;
}

watch(
  () => messages.value.length,
  () => nextTick(scrollToBottom)
);

watch(
  () => chatStore.activeKey,
  () => nextTick(scrollToBottom)
);

async function submit(): Promise<void> {
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
    await nextTick();
    scrollToBottom();
    textareaRef.value?.focus();
  }
}

function onKeydown(event: KeyboardEvent): void {
  if (event.key !== 'Enter' || event.shiftKey || event.isComposing) return;
  event.preventDefault();
  void submit();
}

function pickEmoji(emoji: string): void {
  draft.value += emoji;
  emojiVisible.value = false;
  textareaRef.value?.focus();
}

async function handleImage(event: Event): Promise<void> {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file || uploading.value) return;
  uploading.value = true;
  try {
    await chatStore.sendImage(file);
  } catch {
    ElMessage.error(t('im.chat.imageFailed'));
  } finally {
    uploading.value = false;
    await nextTick();
    scrollToBottom();
  }
}

async function handleFile(event: Event): Promise<void> {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file || uploading.value) return;
  uploading.value = true;
  try {
    const res = await uploadImFile(file);
    if (res.data) await chatStore.sendMessage(res.data, 'file');
  } catch {
    ElMessage.error(t('im.chat.fileFailed'));
  } finally {
    uploading.value = false;
    await nextTick();
    scrollToBottom();
  }
}

function comingSoon(): void {
  ElMessage.info(t('im.common.comingSoon'));
}
</script>

<template>
  <section class="chat-area">
    <template v-if="conversation">
      <header class="chat-head">
        <div class="im-avatar sm round">
          <img
            v-if="avatarOf(conversation.avatar)"
            :src="avatarOf(conversation.avatar)"
            :alt="conversation.name"
          />
          <template v-else>{{ initialOf(conversation.name) }}</template>
          <span
            v-if="conversation.type === 'single'"
            class="im-status-dot"
            :class="{ offline: !conversation.online }"
          />
        </div>

        <div class="chat-head__meta">
          <div class="chat-head__title">
            <h3>{{ conversation.name }}</h3>
            <span v-if="conversation.type === 'group'" class="im-tag">
              {{ t('im.common.group') }}
            </span>
          </div>
          <p class="chat-head__status">{{ headerStatus }}</p>
        </div>

        <div class="chat-head__actions">
          <el-tooltip :content="t('im.common.comingSoon')" placement="bottom">
            <button class="im-icon-btn" @click="comingSoon">
              <el-icon :size="17"><Phone /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="t('im.common.comingSoon')" placement="bottom">
            <button class="im-icon-btn" @click="comingSoon">
              <el-icon :size="17"><VideoCamera /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="t('im.chat.search')" placement="bottom">
            <button class="im-icon-btn" @click="comingSoon">
              <el-icon :size="17"><Search /></el-icon>
            </button>
          </el-tooltip>
          <el-tooltip :content="t('im.chat.info')" placement="bottom">
            <button class="im-icon-btn" :class="{ 'is-active': props.infoOpen }" @click="emit('toggleInfo')">
              <el-icon :size="17"><MoreFilled /></el-icon>
            </button>
          </el-tooltip>
        </div>
      </header>

      <div ref="scrollRef" class="chat-scroll im-scroll">
        <div class="chat-body">
          <div v-for="(message, index) in messages" :key="message.id ?? index" class="msg-block">
            <div v-if="todayLabel(message, index)" class="chat-date">
              {{ todayLabel(message, index) }}
            </div>

            <div class="msg-row" :class="{ mine: isMine(message) }">
              <div class="im-avatar sm round msg-avatar">
                <img
                  v-if="avatarOf(avatarOfMessage(message))"
                  :src="avatarOf(avatarOfMessage(message))"
                  :alt="nameOf(message)"
                />
                <template v-else>{{ initialOf(nameOf(message)) }}</template>
              </div>

              <div class="msg-main">
                <p v-if="isGroup && !isMine(message)" class="msg-sender">
                  {{ nameOf(message) }}
                </p>
                <div class="bubble" :class="{ 'bubble--mine': isMine(message) }">
                  <img
                    v-if="message.messageType === 'image'"
                    class="bubble-image"
                    :src="resolveMediaUrl(message.content)"
                    alt="image"
                    @load="scrollToBottom"
                  />
                  <a
                    v-else-if="message.messageType === 'file'"
                    class="bubble-file"
                    :href="resolveMediaUrl(message.content)"
                    target="_blank"
                    rel="noreferrer"
                  >
                    <el-icon :size="16"><Document /></el-icon>
                    <span>{{ t('im.chat.fileMessage') }}</span>
                  </a>
                  <span v-else class="bubble-text">{{ message.content }}</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="!messages.length" class="chat-hint">
            <div class="im-empty__icon">
              <el-icon :size="28"><ChatDotRound /></el-icon>
            </div>
            <p class="im-empty__title">{{ t('im.chat.noMessages') }}</p>
            <p class="im-empty__desc">{{ t('im.chat.startHint') }}</p>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <textarea
          ref="textareaRef"
          v-model="draft"
          class="chat-textarea"
          :placeholder="t('im.chat.inputPlaceholder')"
          rows="3"
          @keydown="onKeydown"
        />

        <div class="composer-bar">
          <div class="composer-tools">
            <el-popover v-model:visible="emojiVisible" trigger="click" placement="top-start" :width="322">
              <template #reference>
                <button class="im-icon-btn" :title="t('im.chat.emoji')">
                  <el-icon :size="18"><ChatDotRound /></el-icon>
                </button>
              </template>
              <div class="emoji-grid">
                <button
                  v-for="emoji in EMOJIS"
                  :key="emoji"
                  class="emoji-item"
                  @click="pickEmoji(emoji)"
                >
                  {{ emoji }}
                </button>
              </div>
            </el-popover>

            <button
              class="im-icon-btn"
              :title="t('im.chat.image')"
              :disabled="uploading"
              @click="imageInput?.click()"
            >
              <el-icon :size="18"><Picture /></el-icon>
            </button>
            <button
              class="im-icon-btn"
              :title="t('im.chat.file')"
              :disabled="uploading"
              @click="fileInput?.click()"
            >
              <el-icon :size="18"><Folder /></el-icon>
            </button>
            <el-tooltip :content="t('im.common.comingSoon')" placement="top">
              <button class="im-icon-btn" @click="comingSoon">
                <el-icon :size="18"><Microphone /></el-icon>
              </button>
            </el-tooltip>

            <input ref="imageInput" class="hidden-input" type="file" accept="image/*" @change="handleImage" />
            <input ref="fileInput" class="hidden-input" type="file" @change="handleFile" />
          </div>

          <span class="composer-tip">{{ t('im.chat.enterSend') }}</span>

          <el-button
            class="im-action-primary send-btn"
            :loading="sending"
            :disabled="!draft.trim()"
            @click="submit"
          >
            {{ t('im.chat.send') }}
          </el-button>
        </div>
      </div>
    </template>

    <div v-else class="chat-empty">
      <div class="chat-empty__tile">MC</div>
      <p class="im-empty__title">{{ t('im.chat.emptyTitle') }}</p>
      <p class="im-empty__desc">{{ t('im.chat.emptyDesc') }}</p>
    </div>
  </section>
</template>

<style scoped>
.chat-area {
  flex: 1;
  min-width: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-chat);
  position: relative;
  background-image: radial-gradient(
    circle at 1px 1px,
    color-mix(in srgb, var(--theme-text-1) 5%, transparent) 1px,
    transparent 0
  );
  background-size: 22px 22px;
}

.chat-head {
  height: 64px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  background: color-mix(in srgb, var(--theme-bg-surface) 88%, transparent);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--theme-hairline);
  z-index: 2;
}

.chat-head__meta {
  flex: 1;
  min-width: 0;
}

.chat-head__title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.chat-head__title h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-head__status {
  margin: 3px 0 0;
  font-size: 11px;
  color: var(--theme-text-3);
}

.chat-head__actions {
  display: flex;
  align-items: center;
  gap: 2px;
}

.chat-scroll {
  padding: 18px 0 10px;
}

.chat-body {
  max-width: 940px;
  margin: 0 auto;
  padding: 0 26px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chat-date {
  display: block;
  width: 100%;
  margin: 14px 0 10px;
  padding: 0;
  background: none;
  border: none;
  text-align: center;
  font-size: 11px;
  color: var(--theme-text-3);
}

.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin: 8px 0;
  animation: im-fade-up 260ms var(--theme-ease) both;
}

.msg-row.mine {
  flex-direction: row-reverse;
}

.msg-avatar {
  margin-top: 2px;
}

.msg-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: min(62%, 520px);
  min-width: 0;
}

.msg-row.mine .msg-main {
  align-items: flex-end;
}

.msg-sender {
  margin: 0 0 2px 4px;
  font-size: 11px;
  color: var(--theme-text-3);
}

.bubble {
  padding: 9px 13px;
  border-radius: 4px 14px 14px 14px;
  background: var(--theme-bubble-other);
  color: var(--theme-bubble-other-text);
  font-size: 13.5px;
  line-height: 1.62;
  box-shadow: 0 6px 18px -16px rgba(15, 23, 42, 0.4);
  border: 1px solid color-mix(in srgb, var(--theme-hairline) 60%, transparent);
  word-break: break-word;
  white-space: pre-wrap;
  position: relative;
}

.bubble--mine {
  border-radius: 14px 4px 14px 14px;
  background: var(--theme-bubble-mine);
  color: var(--theme-bubble-mine-text);
  border-color: transparent;
  box-shadow: var(--theme-bubble-mine-shadow);
}

.bubble-image {
  display: block;
  max-width: 240px;
  max-height: 280px;
  border-radius: 10px;
  object-fit: cover;
}

.bubble-file {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: inherit;
  text-decoration: none;
  font-size: 13px;
}

.bubble-file:hover {
  text-decoration: underline;
}

.chat-hint {
  margin-top: 64px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: var(--theme-text-3);
  text-align: center;
}

.chat-input {
  flex-shrink: 0;
  margin: 0 18px 16px;
  border-radius: 14px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  box-shadow: var(--theme-shadow-sm);
  transition: border-color var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.chat-input:focus-within {
  border-color: color-mix(in srgb, var(--theme-primary) 50%, transparent);
  box-shadow: var(--theme-ring);
}

.chat-textarea {
  display: block;
  width: 100%;
  padding: 14px 16px 6px;
  border: none;
  outline: none;
  resize: none;
  background: transparent;
  color: var(--theme-text-1);
  font-family: inherit;
  font-size: 13.5px;
  line-height: 1.65;
  max-height: 150px;
}

.chat-textarea::placeholder {
  color: var(--theme-text-3);
}

.composer-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px 10px;
}

.composer-tools {
  display: flex;
  align-items: center;
  gap: 2px;
}

.composer-tip {
  flex: 1;
  text-align: right;
  font-size: 10px;
  color: var(--theme-text-3);
}

.send-btn {
  height: 32px;
  padding: 0 20px;
  font-size: 13px;
}

.hidden-input {
  display: none;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 2px;
  max-height: 260px;
  overflow-y: auto;
}

.emoji-item {
  border: none;
  background: transparent;
  border-radius: 6px;
  padding: 4px 0;
  font-size: 18px;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.emoji-item:hover {
  background: var(--theme-bg-hover);
}

.chat-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  text-align: center;
  padding: 40px;
}

.chat-empty__tile {
  width: 92px;
  height: 92px;
  border-radius: 24px;
  display: grid;
  place-items: center;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: 1px;
  color: #fff;
  background: var(--theme-accent-gradient);
  box-shadow: var(--theme-shadow-lg);
  animation: float-tile 4s ease-in-out infinite;
}

@keyframes float-tile {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}
</style>
