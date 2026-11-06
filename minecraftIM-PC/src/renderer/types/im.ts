/**
 * IM 领域模型。字段与后端 entity / DTO 对齐，后端可能返回额外字段，
 * 因此关键实体保留索引签名，避免严格类型阻断渲染。
 */

/** 登录 / 注册成功的返回（LoginResponse DTO） */
export interface LoginResult {
  token: string;
  userId: number;
  username: string;
  nickname?: string;
  email?: string;
  avatar?: string;
  account?: string;
  phone?: string;
  signature?: string;
  experience?: number;
}

/** 当前用户信息（/user/info） */
export interface UserInfo {
  id?: number;
  userId?: number;
  username?: string;
  nickname?: string;
  account?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  signature?: string;
  [key: string]: unknown;
}

export interface LoginParams {
  account: string;
  password: string;
}

export interface RegisterParams {
  username: string;
  password: string;
  email: string;
  phone: string;
  avatar?: string;
}

/** 好友信息（/public/im/friend/info/list 的列表项） */
export interface FriendInfo {
  friendId?: number;
  userId?: number;
  username?: string;
  nickname?: string;
  avatar?: string;
  remark?: string;
  signature?: string;
  status?: number | string;
  [key: string]: unknown;
}

/** 好友申请（/public/im/friend/request/info/pending 的列表项） */
export interface FriendRequestInfo {
  id?: number;
  requestId?: number;
  senderId?: number;
  receiverId?: number;
  message?: string;
  status?: number | string;
  senderNickname?: string;
  senderAvatar?: string;
  createTime?: string;
  [key: string]: unknown;
}

/** 群聊（ChatGroup 实体） */
export interface ChatGroup {
  id: number;
  name?: string;
  groupName?: string;
  avatar?: string;
  creatorId?: number;
  memberCount?: number;
  notice?: string;
  [key: string]: unknown;
}

export type ChatMessageType = 'text' | 'image' | 'emoji' | 'file' | string;

/** 聊天消息（ChatMessage / GroupChatMessage 实体，字段做并集处理） */
export interface ChatMessage {
  id?: number;
  senderId?: number;
  receiverId?: number;
  groupId?: number;
  content?: string;
  messageType?: ChatMessageType;
  type?: ChatMessageType;
  sendTime?: string;
  createTime?: string;
  isRead?: number | boolean;
  senderNickname?: string;
  senderAvatar?: string;
  [key: string]: unknown;
}

/** 渲染层聚合的会话模型（由好友 + 群组合成） */
export interface Conversation {
  /** 'single-<friendId>' 或 'group-<groupId>' */
  key: string;
  type: 'single' | 'group';
  targetId: number;
  name: string;
  avatar?: string;
  lastMessage?: string;
  lastTime?: string;
  unread: number;
}

export interface FavoriteItem {
  id?: number;
  title?: string;
  content?: string;
  type?: string;
  tags?: string;
  createTime?: string;
  updateTime?: string;
  [key: string]: unknown;
}

export interface MomentItem {
  id?: number;
  userId?: number;
  nickname?: string;
  avatar?: string;
  content?: string;
  images?: string | string[];
  likeCount?: number;
  commentCount?: number;
  createTime?: string;
  liked?: boolean;
  [key: string]: unknown;
}
