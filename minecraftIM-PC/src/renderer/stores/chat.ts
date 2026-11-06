import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import {
  acceptFriendRequest,
  createGroupWithMembers,
  deleteFriend,
  deleteGroup,
  getFriendInfoList,
  getGroupChatHistory,
  getGroupMembers,
  getGroupsByUserId,
  getPendingFriendRequestsWithInfo,
  getSingleChatHistory,
  inviteGroupMembers,
  rejectFriendRequest,
  removeGroupMember,
  sendGroupMessage,
  sendSingleMessage,
  uploadImFile,
  updateFriendRemark as updateFriendRemarkApi,
  updateGroup
} from '@/api/chat';
import { wsService, type WsMessage } from '@/api/ws';
import type {
  ChatGroup,
  ChatMessage,
  Conversation,
  FriendInfo,
  FriendRequestInfo,
  GroupMember
} from '@/types/im';
import type { CreateGroupPayload } from '@/api/chat';
import { useAuthStore } from './auth';

export const conversationKey = (type: 'single' | 'group', id: number): string =>
  `${type}-${id}`;

function timeValue(value?: string): number {
  if (!value) return 0;
  const time = new Date(value).getTime();
  return Number.isNaN(time) ? 0 : time;
}

function normalizeMessages(list: ChatMessage[] | undefined): ChatMessage[] {
  if (!list?.length) return [];
  const seen = new Set<string>();
  return [...list]
    .filter((item) => {
      const id = String(item.id ?? '');
      if (id && seen.has(id)) return false;
      if (id) seen.add(id);
      return true;
    })
    .sort((a, b) => timeValue(a.createTime) - timeValue(b.createTime));
}

export const useChatStore = defineStore('chat', () => {
  const authStore = useAuthStore();

  const friends = ref<FriendInfo[]>([]);
  const groups = ref<ChatGroup[]>([]);
  const friendRequests = ref<FriendRequestInfo[]>([]);
  const messagesMap = ref<Record<string, ChatMessage[]>>({});
  const unreadMap = ref<Record<string, number>>({});
  const starredIds = ref<number[]>([]);
  const groupMembersMap = ref<Record<number, GroupMember[]>>({});
  const activeKey = ref<string | null>(null);
  const loadingContacts = ref(false);
  const connected = ref(false);
  const wsBound = ref(false);

  const currentUserId = computed(() => authStore.userId ?? 0);

  const conversations = computed<Conversation[]>(() => {
    const list: Conversation[] = [];

    for (const friend of friends.value) {
      const key = conversationKey('single', friend.friendId);
      const messages = messagesMap.value[key] ?? [];
      const last = messages[messages.length - 1];
      list.push({
        key,
        type: 'single',
        targetId: friend.friendId,
        name: friend.remark || friend.username,
        avatar: friend.avatar,
        lastMessage: last?.content ?? friend.lastMessage,
        lastMessageType: last?.messageType,
        lastTime: last?.createTime ?? friend.lastMessageTime,
        unread: unreadMap.value[key] ?? friend.unreadCount ?? 0,
        online: friend.online
      });
    }

    for (const group of groups.value) {
      const key = conversationKey('group', group.id);
      const messages = messagesMap.value[key] ?? [];
      const last = messages[messages.length - 1];
      list.push({
        key,
        type: 'group',
        targetId: group.id,
        name: group.name,
        avatar: group.avatar,
        lastMessage: last?.content ?? group.description,
        lastMessageType: last?.messageType,
        lastTime: last?.createTime,
        unread: unreadMap.value[key] ?? 0,
        memberCount: group.memberCount
      });
    }

    return list.sort((a, b) => timeValue(b.lastTime) - timeValue(a.lastTime));
  });

  const activeConversation = computed<Conversation | null>(
    () => conversations.value.find((item) => item.key === activeKey.value) ?? null
  );

  const activeMessages = computed<ChatMessage[]>(() =>
    activeKey.value ? messagesMap.value[activeKey.value] ?? [] : []
  );

  const totalUnread = computed(() =>
    Object.values(unreadMap.value).reduce((sum, value) => sum + (value > 0 ? value : 0), 0)
  );

  const pendingRequestCount = computed(() => friendRequests.value.length);

  function findFriend(friendId: number): FriendInfo | undefined {
    return friends.value.find((item) => item.friendId === friendId);
  }

  function findGroup(groupId: number): ChatGroup | undefined {
    return groups.value.find((item) => item.id === groupId);
  }

  function isStarred(friendId: number): boolean {
    return starredIds.value.includes(friendId);
  }

  function persistStars(): void {
    if (!currentUserId.value) return;
    localStorage.setItem(`im:starred:${currentUserId.value}`, JSON.stringify(starredIds.value));
  }

  function readStars(): void {
    starredIds.value = [];
    if (!currentUserId.value) return;
    try {
      const raw = localStorage.getItem(`im:starred:${currentUserId.value}`);
      const parsed = raw ? (JSON.parse(raw) as unknown) : [];
      if (Array.isArray(parsed)) {
        starredIds.value = parsed.filter((id): id is number => typeof id === 'number');
      }
    } catch {
      starredIds.value = [];
    }
  }

  function toggleStar(friendId: number): boolean {
    const index = starredIds.value.indexOf(friendId);
    if (index >= 0) starredIds.value.splice(index, 1);
    else starredIds.value.push(friendId);
    persistStars();
    return starredIds.value.includes(friendId);
  }

  async function loadContacts(): Promise<void> {
    const userId = currentUserId.value;
    if (!userId) return;
    loadingContacts.value = true;
    readStars();

    const [friendResult, groupResult, requestResult] = await Promise.allSettled([
      getFriendInfoList(userId),
      getGroupsByUserId(userId),
      getPendingFriendRequestsWithInfo(userId)
    ]);

    if (friendResult.status === 'fulfilled') {
      friends.value = friendResult.value.data ?? [];
      for (const friend of friends.value) {
        const key = conversationKey('single', friend.friendId);
        if (unreadMap.value[key] === undefined) {
          unreadMap.value[key] = friend.unreadCount ?? 0;
        }
      }
    }
    if (groupResult.status === 'fulfilled') {
      groups.value = groupResult.value.data ?? [];
    }
    if (requestResult.status === 'fulfilled') {
      friendRequests.value = requestResult.value.data ?? [];
    }

    loadingContacts.value = false;
  }

  async function loadHistory(key: string, force = false): Promise<void> {
    if (!force && messagesMap.value[key]) return;
    const [type, rawId] = key.split('-');
    const targetId = Number(rawId);
    const userId = currentUserId.value;
    if (!targetId || !userId) return;

    try {
      const res =
        type === 'single'
          ? await getSingleChatHistory(userId, targetId, 80)
          : await getGroupChatHistory(targetId, 80);
      messagesMap.value[key] = normalizeMessages(res.data);
    } catch {
      messagesMap.value[key] = messagesMap.value[key] ?? [];
    }
  }

  function appendMessage(key: string, message: ChatMessage): void {
    const list = messagesMap.value[key] ? [...messagesMap.value[key]] : [];
    const id = String(message.id ?? '');
    if (id && list.some((item) => String(item.id ?? '') === id)) return;
    list.push(message);
    list.sort((a, b) => timeValue(a.createTime) - timeValue(b.createTime));
    messagesMap.value[key] = list;
  }

  function markConversationRead(key: string): void {
    unreadMap.value[key] = 0;
  }

  async function selectConversation(key: string | null): Promise<void> {
    activeKey.value = key;
    if (!key) return;
    markConversationRead(key);
    await loadHistory(key);
  }

  async function openWithFriend(friendId: number): Promise<void> {
    await selectConversation(conversationKey('single', friendId));
  }

  async function openWithGroup(groupId: number): Promise<void> {
    await selectConversation(conversationKey('group', groupId));
  }

  function ownMessage(content: string, messageType: string, extra: Partial<ChatMessage> = {}): ChatMessage {
    return {
      senderId: currentUserId.value,
      content,
      messageType,
      createTime: new Date().toISOString(),
      isRead: 0,
      senderName: authStore.user?.nickname || authStore.user?.username,
      senderAvatar: authStore.user?.avatar,
      ...extra
    };
  }

  async function sendMessage(content: string, messageType = 'text'): Promise<void> {
    const conversation = activeConversation.value;
    if (!conversation || !currentUserId.value) return;

    const text = content.trim();
    if (!text) return;

    if (conversation.type === 'single') {
      await sendSingleMessage({
        senderId: currentUserId.value,
        receiverId: conversation.targetId,
        content: text,
        messageType
      });
      appendMessage(
        conversation.key,
        ownMessage(text, messageType, { receiverId: conversation.targetId })
      );
      return;
    }

    await sendGroupMessage({
      groupId: conversation.targetId,
      senderId: currentUserId.value,
      content: text,
      messageType
    });
    appendMessage(conversation.key, ownMessage(text, messageType, { groupId: conversation.targetId }));
  }

  /** 上传并发送图片消息 */
  async function sendImage(file: File): Promise<void> {
    const res = await uploadImFile(file);
    const url = res.data;
    if (url) await sendMessage(url, 'image');
  }

  async function loadGroupMembers(groupId: number, force = false): Promise<GroupMember[]> {
    if (!force && groupMembersMap.value[groupId]) return groupMembersMap.value[groupId];
    try {
      const res = await getGroupMembers(groupId);
      groupMembersMap.value[groupId] = res.data ?? [];
    } catch {
      groupMembersMap.value[groupId] = groupMembersMap.value[groupId] ?? [];
    }
    return groupMembersMap.value[groupId];
  }

  async function resolveFriendRequest(requestId: number, accept: boolean): Promise<void> {
    if (accept) await acceptFriendRequest(requestId);
    else await rejectFriendRequest(requestId);
    friendRequests.value = friendRequests.value.filter(
      (item) => (item.id ?? item.requestId) !== requestId
    );
    if (accept) await loadContacts();
  }

  async function removeFriend(friendId: number): Promise<void> {
    if (!currentUserId.value) return;
    await deleteFriend(currentUserId.value, friendId);
    friends.value = friends.value.filter((item) => item.friendId !== friendId);
    const key = conversationKey('single', friendId);
    if (activeKey.value === key) activeKey.value = null;
    starredIds.value = starredIds.value.filter((id) => id !== friendId);
    persistStars();
  }

  async function updateFriendRemark(friendId: number, remark: string): Promise<void> {
    if (!currentUserId.value) return;
    await updateFriendRemarkApi(currentUserId.value, friendId, remark);
    const friend = findFriend(friendId);
    if (friend) friend.remark = remark;
  }

  async function createGroup(payload: CreateGroupPayload): Promise<ChatGroup | null> {
    const res = await createGroupWithMembers(payload);
    const group = res.data;
    if (group) {
      groups.value = [...groups.value, group];
      await openWithGroup(group.id);
    }
    return group ?? null;
  }

  async function inviteMembers(groupId: number, friendIds: number[]): Promise<void> {
    await inviteGroupMembers(groupId, friendIds);
    await loadGroupMembers(groupId, true);
    const group = findGroup(groupId);
    if (group) group.memberCount = (group.memberCount ?? 0) + friendIds.length;
  }

  async function removeMember(groupId: number, userId: number): Promise<void> {
    await removeGroupMember(groupId, userId);
    groupMembersMap.value[groupId] = (groupMembersMap.value[groupId] ?? []).filter(
      (item) => item.userId !== userId
    );
    const group = findGroup(groupId);
    if (group && group.memberCount) group.memberCount -= 1;
  }

  async function editGroup(payload: Partial<ChatGroup> & { id: number }): Promise<void> {
    await updateGroup(payload);
    const group = findGroup(payload.id);
    if (group) Object.assign(group, payload);
  }

  async function dissolveGroup(groupId: number): Promise<void> {
    await deleteGroup(groupId);
    groups.value = groups.value.filter((item) => item.id !== groupId);
    delete groupMembersMap.value[groupId];
    const key = conversationKey('group', groupId);
    if (activeKey.value === key) activeKey.value = null;
  }

  /* ---------------- WebSocket 实时消息 ---------------- */

  function handlePrivateMessage(event: WsMessage): void {
    const senderId = Number(event.senderId ?? 0);
    if (!senderId || senderId === currentUserId.value) return;
    const key = conversationKey('single', senderId);
    const message: ChatMessage = {
      id: typeof event.id === 'number' ? event.id : undefined,
      senderId,
      receiverId: currentUserId.value,
      content: String(event.content ?? ''),
      messageType: String(event.messageType ?? 'text'),
      createTime: String(event.createTime ?? new Date().toISOString()),
      isRead: 0
    };
    appendMessage(key, message);
    if (activeKey.value !== key) {
      unreadMap.value[key] = (unreadMap.value[key] ?? 0) + 1;
    }
  }

  function handleGroupMessage(event: WsMessage): void {
    const groupId = Number(event.groupId ?? 0);
    const senderId = Number(event.senderId ?? 0);
    if (!groupId || senderId === currentUserId.value) return;
    const key = conversationKey('group', groupId);
    appendMessage(key, {
      id: typeof event.id === 'number' ? event.id : undefined,
      groupId,
      senderId,
      senderName: String(event.senderName ?? ''),
      senderAvatar: event.senderAvatar ? String(event.senderAvatar) : undefined,
      content: String(event.content ?? ''),
      messageType: String(event.messageType ?? 'text'),
      createTime: String(event.createTime ?? new Date().toISOString())
    });
    if (activeKey.value !== key) {
      unreadMap.value[key] = (unreadMap.value[key] ?? 0) + 1;
    }
  }

  function onConnected(): void {
    connected.value = true;
  }

  function onDisconnected(): void {
    connected.value = false;
  }

  function bindWs(): void {
    if (wsBound.value) return;
    wsService.on('private-message', handlePrivateMessage);
    wsService.on('group-message', handleGroupMessage);
    wsService.on('connect-success', onConnected);
    wsService.on('disconnect', onDisconnected);
    wsService.on('connect', onConnected);
    wsBound.value = true;
    connected.value = wsService.connected;
    if (currentUserId.value) wsService.connect(currentUserId.value);
  }

  function unbindWs(): void {
    if (!wsBound.value) return;
    wsService.off('private-message', handlePrivateMessage);
    wsService.off('group-message', handleGroupMessage);
    wsService.off('connect-success', onConnected);
    wsService.off('disconnect', onDisconnected);
    wsService.off('connect', onConnected);
    wsBound.value = false;
  }

  function reset(): void {
    unbindWs();
    friends.value = [];
    groups.value = [];
    friendRequests.value = [];
    messagesMap.value = {};
    unreadMap.value = {};
    starredIds.value = [];
    groupMembersMap.value = {};
    activeKey.value = null;
    connected.value = false;
  }

  return {
    friends,
    groups,
    friendRequests,
    messagesMap,
    unreadMap,
    starredIds,
    groupMembersMap,
    activeKey,
    loadingContacts,
    connected,
    currentUserId,
    conversations,
    activeConversation,
    activeMessages,
    totalUnread,
    pendingRequestCount,
    findFriend,
    findGroup,
    isStarred,
    toggleStar,
    loadContacts,
    loadHistory,
    selectConversation,
    openWithFriend,
    openWithGroup,
    sendMessage,
    sendImage,
    loadGroupMembers,
    resolveFriendRequest,
    removeFriend,
    updateFriendRemark,
    createGroup,
    inviteMembers,
    removeMember,
    editGroup,
    dissolveGroup,
    bindWs,
    unbindWs,
    reset
  };
});
