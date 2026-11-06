/** 后端 IM / 用户 / 收藏 / 朋友圈 领域模型 */

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
  experience?: string;
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

export interface UserInfo {
  id?: number;
  userId?: number;
  account?: string;
  username: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  gender?: string;
  age?: number;
  occupation?: string;
  hobbies?: string;
  signature?: string;
  experience?: string;
  bio?: string;
  points?: number;
  /** 0-在线 1-离线 */
  online?: number;
  /** 0-禁用 1-正常 */
  status?: number;
  permissions?: string;
  createTime?: string;
}

/** 好友（好友列表带最后一条消息与未读数） */
export interface FriendInfo {
  friendId: number;
  remark?: string;
  username: string;
  avatar?: string;
  online?: boolean;
  lastMessage?: string;
  lastMessageTime?: string;
  unreadCount?: number;
  phone?: string;
  email?: string;
}

export interface FriendRequestInfo {
  id?: number;
  requestId?: number;
  userId?: number;
  senderId?: number;
  friendId?: number;
  username?: string;
  nickname?: string;
  avatar?: string;
  phone?: string;
  message?: string;
  remark?: string;
  status?: number;
  createTime?: string;
}

export interface ChatGroup {
  id: number;
  name: string;
  description?: string;
  creatorId?: number;
  avatar?: string;
  memberCount?: number;
  maxMembers?: number;
  createTime?: string;
}

export interface GroupMember {
  id?: number;
  groupId: number;
  userId: number;
  nickname?: string;
  username?: string;
  avatar?: string;
  role?: string;
  status?: number;
  joinTime?: string;
}

export interface ChatMessage {
  id?: number;
  senderId: number;
  receiverId?: number;
  groupId?: number;
  senderName?: string;
  senderAvatar?: string;
  content: string;
  messageType?: string;
  isRead?: number;
  createTime?: string;
  [key: string]: unknown;
}

export interface Conversation {
  key: string;
  type: 'single' | 'group';
  targetId: number;
  name: string;
  avatar?: string;
  lastMessage?: string;
  lastMessageType?: string;
  lastTime?: string;
  unread: number;
  online?: boolean;
  memberCount?: number;
}

export type FavoriteType =
  | 'note'
  | 'link'
  | 'image'
  | 'file'
  | 'chat'
  | 'voice'
  | 'location'
  | 'miniapp'
  | 'other';

export interface FavoriteItem {
  id: number;
  userId?: number;
  type: FavoriteType | string;
  title?: string;
  content?: string;
  cover?: string;
  url?: string;
  fileName?: string;
  fileSize?: number;
  tags?: string[];
  source?: string;
  status?: number;
  createTime?: string;
  updateTime?: string;
}

export interface FavoriteStat {
  total: number;
  types: Record<string, number>;
}

export interface MomentUser {
  userId: number;
  username?: string;
  nickname?: string;
  avatar?: string;
}

export interface MomentComment {
  id: number;
  momentId: number;
  userId: number;
  username?: string;
  nickname?: string;
  avatar?: string;
  replyToUserId?: number;
  replyToNickname?: string;
  content: string;
  status?: number;
  createTime?: string;
}

export interface MomentItem {
  id: number;
  userId: number;
  username?: string;
  nickname?: string;
  avatar?: string;
  content?: string;
  images?: string[];
  location?: string;
  visibility?: number;
  likeCount?: number;
  commentCount?: number;
  status?: number;
  createTime?: string;
  liked?: boolean;
  likes?: MomentUser[];
  comments?: MomentComment[];
}

export interface LikeResult {
  liked?: boolean;
  likeCount?: number;
  [key: string]: unknown;
}

export interface PointsRecord {
  id?: number;
  userId?: number;
  points?: number;
  amount?: number;
  type?: string | number;
  description?: string;
  remark?: string;
  createTime?: string;
  [key: string]: unknown;
}
