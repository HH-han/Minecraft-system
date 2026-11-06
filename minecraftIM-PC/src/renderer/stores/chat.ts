import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import {
  acceptFriendRequest,
  getFriendInfoList,
  getGroupChatHistory,
  getGroupsByUserId,
  getPendingFriendRequestsWithInfo,
  getSingleChatHistory,
  rejectFriendRequest,
  sendGroupMessage,
  sendSingleMessage
} from '@/api/chat';
import { wsService, type WsMessage } from '@/api/ws';
import { useAuthStore } from '@/stores/auth';
import type { ChatGroup, ChatMessage, Conversation, FriendInfo, FriendRequestInfo } from '@/types/im';

function messageTime(msg: ChatMessage): string {
  return (msg.sendTime ?? msg.createTime ?? '') as string;
}

export const useChatStore = defineStore('chat', () => {
  const authStore = useAuthStore();

  const friends = ref<FriendInfo[]>([]);
  const groups = ref<ChatGroup[]>([]);
  const friendRequests = ref<FriendRequestInfo[]>([]);
  /** 会话 key -> 消息列表 */
  const messagesMap = ref<Record<string, ChatMessage[]>>({});
  /** 会话 key -> 未读数 */
  const unreadMap = ref<Record<string, number>>({});
  const activeKey = ref<string | null>(null);
  const loading = ref(false);

  const conversations = computed<Conversation[]>(() => {
    const list: Conversation[] = [];
    for (const friend of friends.value) {
      const targetId = Number(friend.friendId ?? friend.userId);
      if (!Number.isFinite(targetId)) continue;
      const key = `single-${targetId}`;
      const history = messagesMap.value[key] ?? [];
      const last = history[history.length - 1];
      list.push({
        key,
        type: 'single',
        targetId,
        name: String(friend.remark || friend.nickname || friend.username || `用户 ${targetId}`),
        avatar: friend.avatar,
        lastMessage: last?.content,
        lastTime: last ? messageTime(last) : undefined,
        unread: unreadMap.value[key] ?? 0
      });
    }
    for (const group of groups.value) {
      const key = `group-${group.id}`;
      const history = messagesMap.value[key] ?? [];
      const last = history[history.length - 1];
      list.push({
        key,
        type: 'group',
        targetId: group.id,
        name: String(group.name ?? group.groupName ?? `群聊 ${group.id}`),
        avatar: group.avatar,
        lastMessage: last?.content,
        lastTime: last ? messageTime(last) : undefined,
        unread: unreadMap.value[key] ?? 0
      });
    }
    return list.sort((a, b) => (b.lastTime ?? '').localeCompare(a.lastTime ?? ''));
  });

  const activeConversation = computed(
    () => conversations.value.find((item) => item.key === activeKey.value) ?? null
  );

  const activeMessages = computed<ChatMessage[]>(() => {
    if (!activeKey.value) return [];
    return messagesMap.value[activeKey.value] ?? [];
  });

  async function loadContacts(): Promise<void> {
    const userId = authStore.userId;
    if (!userId) return;
    loading.value = true;
    try {
      const [friendRes, groupRes, requestRes] = await Promise.allSettled([
        getFriendInfoList(userId),
        getGroupsByUserId(userId),
        getPendingFriendRequestsWithInfo(userId)
      ]);
      if (friendRes.status === 'fulfilled') friends.value = friendRes.value.data ?? [];
      if (groupRes.status === 'fulfilled') groups.value = groupRes.value.data ?? [];
      if (requestRes.status === 'fulfilled') friendRequests.value = requestRes.value.data ?? [];
    } finally {
      loading.value = false;
    }
  }

  async function loadHistory(conversation: Conversation): Promise<void> {
    const userId = authStore.userId;
    if (!userId) return;
    const res =
      conversation.type === 'single'
        ? await getSingleChatHistory(userId, conversation.targetId)
        : await getGroupChatHistory(conversation.targetId);
    const list = (res.data ?? []).slice().sort((a, b) => messageTime(a).localeCompare(messageTime(b)));
    messagesMap.value = { ...messagesMap.value, [conversation.key]: list };
  }

  async function selectConversation(key: string): Promise<void> {
    activeKey.value = key;
    unreadMap.value = { ...unreadMap.value, [key]: 0 };
    const conversation = conversations.value.find((item) => item.key === key);
    if (conversation) {
      await loadHistory(conversation).catch(() => undefined);
    }
  }

  function appendMessage(key: string, message: ChatMessage): void {
    const list = messagesMap.value[key] ?? [];
    messagesMap.value = { ...messagesMap.value, [key]: [...list, message] };
  }

  async function sendMessage(content: string): Promise<void> {
    const conversation = activeConversation.value;
    const userId = authStore.userId;
    if (!conversation || !userId || !content.trim()) return;
    const text = content.trim();
    if (conversation.type === 'single') {
      await sendSingleMessage({ senderId: userId, receiverId: conversation.targetId, content: text });
    } else {
      await sendGroupMessage({ groupId: conversation.targetId, senderId: userId, content: text });
    }
    appendMessage(conversation.key, {
      senderId: userId,
      receiverId: conversation.type === 'single' ? conversation.targetId : undefined,
      groupId: conversation.type === 'group' ? conversation.targetId : undefined,
      content: text,
      messageType: 'text',
      sendTime: new Date().toISOString()
    });
  }

  async function handleFriendRequest(requestId: number, accept: boolean): Promise<void> {
    if (accept) {
      await acceptFriendRequest(requestId);
    } else {
      await rejectFriendRequest(requestId);
    }
    await loadContacts();
  }

  function extractPayload(data: WsMessage): ChatMessage {
    return (data.data as ChatMessage) ?? (data as unknown as ChatMessage);
  }

  function handlePrivateMessage(data: WsMessage): void {
    const payload = extractPayload(data);
    const userId = authStore.userId;
    if (!userId) return;
    const peerId = Number(payload.senderId === userId ? payload.receiverId : payload.senderId);
    if (!Number.isFinite(peerId)) return;
    const key = `single-${peerId}`;
    appendMessage(key, payload);
    if (activeKey.value !== key && payload.senderId !== userId) {
      unreadMap.value = { ...unreadMap.value, [key]: (unreadMap.value[key] ?? 0) + 1 };
    }
  }

  function handleGroupMessage(data: WsMessage): void {
    const payload = extractPayload(data);
    const groupId = Number(payload.groupId);
    if (!Number.isFinite(groupId)) return;
    const key = `group-${groupId}`;
    appendMessage(key, payload);
    if (activeKey.value !== key && payload.senderId !== authStore.userId) {
      unreadMap.value = { ...unreadMap.value, [key]: (unreadMap.value[key] ?? 0) + 1 };
    }
  }

  function bindWs(): void {
    wsService.on('private-message', handlePrivateMessage);
    wsService.on('group-message', handleGroupMessage);
  }

  function unbindWs(): void {
    wsService.off('private-message', handlePrivateMessage);
    wsService.off('group-message', handleGroupMessage);
  }

  function reset(): void {
    friends.value = [];
    groups.value = [];
    friendRequests.value = [];
    messagesMap.value = {};
    unreadMap.value = {};
    activeKey.value = null;
  }

  return {
    friends,
    groups,
    friendRequests,
    conversations,
    activeConversation,
    activeMessages,
    activeKey,
    loading,
    loadContacts,
    selectConversation,
    sendMessage,
    handleFriendRequest,
    bindWs,
    unbindWs,
    reset
  };
});
