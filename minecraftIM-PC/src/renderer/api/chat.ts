import request, { type ApiResponse } from './request';
import type {
  ChatGroup,
  ChatMessage,
  ChatMessageType,
  FriendInfo,
  FriendRequestInfo
} from '@/types/im';

/* ---------------- 单聊 ---------------- */

export function sendSingleMessage(message: {
  senderId: number;
  receiverId: number;
  content: string;
  messageType?: ChatMessageType;
}) {
  return request.post<unknown, ApiResponse<ChatMessage>>('/public/im/single/send', message);
}

export function getSingleChatHistory(userId1: number, userId2: number, limit = 50) {
  return request.get<unknown, ApiResponse<ChatMessage[]>>('/public/im/single/history', {
    params: { userId1, userId2, limit }
  });
}

/* ---------------- 群聊 ---------------- */

export function sendGroupMessage(message: {
  groupId: number;
  senderId: number;
  content: string;
  messageType?: ChatMessageType;
}) {
  return request.post<unknown, ApiResponse<ChatMessage>>('/public/im/group/send', message);
}

export function getGroupChatHistory(groupId: number, limit = 50) {
  return request.get<unknown, ApiResponse<ChatMessage[]>>('/public/im/group/history', {
    params: { groupId, limit }
  });
}

export function getGroupsByUserId(userId: number) {
  return request.get<unknown, ApiResponse<ChatGroup[]>>(`/public/chat-group/user/${userId}`);
}

export function createGroupWithMembers(groupData: {
  name: string;
  creatorId: number;
  memberIds?: number[];
  avatar?: string;
}) {
  return request.post<unknown, ApiResponse<ChatGroup>>('/public/chat-group/with-members', groupData);
}

export function getGroupMembers(groupId: number) {
  return request.get<unknown, ApiResponse<FriendInfo[]>>(`/public/chat-group/${groupId}/members`);
}

/* ---------------- 好友 ---------------- */

export function getFriendInfoList(userId: number) {
  return request.get<unknown, ApiResponse<FriendInfo[]>>('/public/im/friend/info/list', {
    params: { userId }
  });
}

export function sendFriendRequest(data: {
  senderId: number;
  receiverId?: number;
  receiverPhone?: string;
  message?: string;
}) {
  return request.post<unknown, ApiResponse<unknown>>('/public/im/friend/request', data);
}

export function getPendingFriendRequestsWithInfo(userId: number) {
  return request.get<unknown, ApiResponse<FriendRequestInfo[]>>(
    '/public/im/friend/request/info/pending',
    { params: { userId } }
  );
}

export function acceptFriendRequest(requestId: number) {
  return request.put<unknown, ApiResponse<unknown>>('/public/im/friend/request/accept', null, {
    params: { requestId }
  });
}

export function rejectFriendRequest(requestId: number) {
  return request.put<unknown, ApiResponse<unknown>>('/public/im/friend/request/reject', null, {
    params: { requestId }
  });
}

export function deleteFriend(userId: number, friendId: number) {
  return request.delete<unknown, ApiResponse<unknown>>('/public/im/friend/delete', {
    params: { userId, friendId }
  });
}

export function updateFriendRemark(userId: number, friendId: number, remark: string) {
  return request.put<unknown, ApiResponse<unknown>>('/public/im/friend/remark', null, {
    params: { userId, friendId, remark }
  });
}

/* ---------------- 已读 / 未读 ---------------- */

export function markMessageAsRead(messageId: number, userId: number, groupId?: number) {
  return request.post<unknown, ApiResponse<unknown>>('/public/im/message/read', null, {
    params: { messageId, userId, groupId }
  });
}

export function getUnreadMessageCount(userId: number, groupId?: number) {
  return request.get<unknown, ApiResponse<number>>('/public/im/message/unread/count', {
    params: { userId, groupId }
  });
}
