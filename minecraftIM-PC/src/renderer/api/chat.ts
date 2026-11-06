import request, { type ApiResponse } from './request';
import type {
  ChatGroup,
  ChatMessage,
  FriendInfo,
  FriendRequestInfo,
  GroupMember
} from '../types/im';

export interface SingleMessagePayload {
  senderId: number;
  receiverId: number;
  content: string;
  messageType?: string;
}

export interface GroupMessagePayload {
  groupId: number;
  senderId: number;
  content: string;
  messageType?: string;
}

export interface CreateGroupPayload {
  name: string;
  description?: string;
  avatar?: string;
  creatorId: number;
  memberIds: number[];
}

export interface FriendRequestPayload {
  senderId: number;
  receiverId?: number;
  receiverPhone?: string;
  message?: string;
}

/* ---------------- 消息 ---------------- */

export function sendSingleMessage(payload: SingleMessagePayload) {
  return request.post<unknown, ApiResponse<ChatMessage>>('/public/im/single/send', payload);
}

export function getSingleChatHistory(userId1: number, userId2: number, limit = 50) {
  return request.get<unknown, ApiResponse<ChatMessage[]>>('/public/im/single/history', {
    params: { userId1, userId2, limit }
  });
}

export function sendGroupMessage(payload: GroupMessagePayload) {
  return request.post<unknown, ApiResponse<void>>('/public/im/group/send', payload);
}

export function getGroupChatHistory(groupId: number, limit = 50) {
  return request.get<unknown, ApiResponse<ChatMessage[]>>('/public/im/group/history', {
    params: { groupId, limit }
  });
}

export function markMessageAsRead(userId: number, messageId: number, groupId?: number) {
  return request.post<unknown, ApiResponse<void>>('/public/im/message/read', null, {
    params: { userId, messageId, groupId }
  });
}

export function getUnreadMessageCount(userId: number, groupId?: number) {
  return request.get<unknown, ApiResponse<number>>('/public/im/message/unread/count', {
    params: { userId, groupId }
  });
}

/** 上传聊天图片/文件 */
export function uploadImFile(file: File) {
  const form = new FormData();
  form.append('file', file);
  return request.post<unknown, ApiResponse<string>>('/public/im/upload', form);
}

/* ---------------- 好友 ---------------- */

export function getFriendInfoList(userId: number) {
  return request.get<unknown, ApiResponse<FriendInfo[]>>('/public/im/friend/info/list', {
    params: { userId }
  });
}

export function sendFriendRequest(payload: FriendRequestPayload) {
  return request.post<unknown, ApiResponse<void>>('/public/im/friend/request', payload);
}

export function getPendingFriendRequestsWithInfo(userId: number) {
  return request.get<unknown, ApiResponse<FriendRequestInfo[]>>(
    '/public/im/friend/request/info/pending',
    { params: { userId } }
  );
}

export function acceptFriendRequest(requestId: number) {
  return request.put<unknown, ApiResponse<void>>('/public/im/friend/request/accept', null, {
    params: { requestId }
  });
}

export function rejectFriendRequest(requestId: number) {
  return request.put<unknown, ApiResponse<void>>('/public/im/friend/request/reject', null, {
    params: { requestId }
  });
}

export function deleteFriend(userId: number, friendId: number) {
  return request.delete<unknown, ApiResponse<void>>('/public/im/friend/delete', {
    params: { userId, friendId }
  });
}

export function updateFriendRemark(userId: number, friendId: number, remark: string) {
  return request.put<unknown, ApiResponse<void>>('/public/im/friend/remark', null, {
    params: { userId, friendId, remark }
  });
}

export function checkFriend(userId: number, friendId: number) {
  return request.get<unknown, ApiResponse<boolean>>('/public/im/friend/check', {
    params: { userId, friendId }
  });
}

/* ---------------- 群组 ---------------- */

export function getGroupsByUserId(userId: number) {
  return request.get<unknown, ApiResponse<ChatGroup[]>>(`/public/chat-group/user/${userId}`);
}

export function getGroupById(groupId: number) {
  return request.get<unknown, ApiResponse<ChatGroup>>(`/public/chat-group/${groupId}`);
}

export function createGroupWithMembers(payload: CreateGroupPayload) {
  return request.post<unknown, ApiResponse<ChatGroup>>(
    '/public/chat-group/with-members',
    payload
  );
}

export function getGroupMembers(groupId: number) {
  return request.get<unknown, ApiResponse<GroupMember[]>>(
    `/public/chat-group/${groupId}/members`
  );
}

export function updateGroup(group: Partial<ChatGroup> & { id: number }) {
  return request.put<unknown, ApiResponse<void>>('/public/chat-group', group);
}

export function inviteGroupMembers(groupId: number, friendIds: number[]) {
  return request.post<unknown, ApiResponse<void>>(
    `/public/chat-group/${groupId}/invite`,
    friendIds
  );
}

export function removeGroupMember(groupId: number, userId: number) {
  return request.delete<unknown, ApiResponse<void>>(
    `/public/chat-group/${groupId}/members/${userId}`
  );
}

export function deleteGroup(groupId: number) {
  return request.delete<unknown, ApiResponse<void>>(`/public/chat-group/${groupId}`);
}
