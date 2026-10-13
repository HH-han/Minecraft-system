/**
 * 基础配置
 *
 * 统一管理 API 接口地址
 * 替代 minecraft-App 项目中的 utils/config.js
 */

import env from '../config/index.js'

// API 基础 URL
export const BASE_URL = env.API_BASE_URL.replace(/\/api$/, '')

/**
 * 接口端点配置
 */
export const API_CONFIG = {
  // 用户管理接口
  USER: {
    BASE: '/api/user',
    ENDPOINTS: {
      INFO: '/info',
      LOGIN: '/login',
      ADMIN_LOGIN: '/adminlogin',
      AVATAR: '/avatar/{username}',
      UPDATE: '/update',
      UPDATE_ROLE: '/updateRole',
      UPDATE_STATUS: '/updateStatus',
      SEARCH: '/search',
      EXPORT: '/export',
      IMPORT: '/import',
      REGISTER: '/register',
      BY_ACCOUNT: '/by-account'
    }
  },

  // 认证管理接口
  AUTH: {
    BASE: '/api/auth',
    ENDPOINTS: {
      LOGIN: '/login',
      REGISTER: '/register',
      LOGOUT: '/user/logout',
      SEND_CODE: '/send-code',
      EMAIL_LOGIN: '/Emaillogin',
      RESET_PASSWORD: '/reset-password',
      RESET_CODE: '/reset-password/code'
    }
  },

  // 聊天管理接口
  CHAT: {
    BASE: '/api',
    ENDPOINTS: {
      SEND_SINGLE: '/public/im/single/send',
      HISTORY_SINGLE: '/public/im/single/history',
      SEND_GROUP: '/public/im/group/send',
      HISTORY_GROUP: '/public/im/group/history',
      MARK_READ: '/chat/read',
      UNREAD_COUNT: '/public/im/message/unread/count',
      UPLOAD: '/upload'
    }
  },

  // 好友管理接口
  FRIEND: {
    BASE: '/api',
    ENDPOINTS: {
      LIST: '/public/im/friend/list',
      INFO_LIST: '/public/im/friend/info/list',
      REQUEST: '/public/im/friend/request',
      REQUEST_PENDING: '/public/im/friend/request/pending',
      REQUEST_ACCEPT: '/public/im/friend/request/accept',
      REQUEST_REJECT: '/public/im/friend/request/reject',
      DELETE: '/public/im/friend/delete',
      REMARK: '/public/im/friend/remark'
    }
  },

  // 群组管理接口
  CHAT_GROUP: {
    BASE: '/api/public/chat-group',
    ENDPOINTS: {
      LIST: '',
      DETAIL: '/{id}',
      CREATE: '',
      CREATE_WITH_MEMBERS: '/with-members',
      UPDATE: '/update',
      DELETE: '/{id}',
      MEMBERS: '/{id}/members',
      INVITE: '/{id}/invite',
      REMOVE_MEMBER: '/{id}/members/{userId}',
      USER_GROUPS: '/user/{userId}'
    }
  },

  // 表情包接口
  EMOJI: {
    BASE: '/api',
    ENDPOINTS: {
      LIST: '/emoji/list',
      SYSTEM: '/emojis',
      GROUPED: '/emojis/grouped'
    }
  },

  // 朋友圈接口
  MOMENT: {
    BASE: '/api/public/moment',
    ENDPOINTS: {
      PUBLISH: '/publish',
      FEED: '/feed',
      USER: '/user',
      DELETE: '/{id}',
      LIKE: '/{id}/like',
      LIKES: '/{id}/likes',
      COMMENT: '/{id}/comment',
      COMMENTS: '/{id}/comments',
      DELETE_COMMENT: '/comment/{id}',
      COVER: '/cover'
    }
  },

  // 收藏接口
  FAVORITE: {
    BASE: '/api/public/favorite',
    ENDPOINTS: {
      ADD: '/add',
      UPDATE: '/{id}',
      LIST: '/list',
      RECENT: '/recent',
      STATS: '/stats',
      TAGS: '/tags',
      DETAIL: '/{id}',
      TOUCH: '/{id}/touch',
      DELETE: '/{id}',
      CLEAR: '/clear'
    }
  }
}

/**
 * 应用配置
 */
export const APP_CONFIG = {
  TITLE: env.APP_TITLE,
  VERSION: env.APP_VERSION,
  DEBUG: env.DEBUG,
  LOG_LEVEL: env.LOG_LEVEL,
  ENABLE_MOCK: env.ENABLE_MOCK,
  ENABLE_ANALYTICS: env.ENABLE_ANALYTICS
}

/**
 * 文件上传配置
 */
export const UPLOAD_CONFIG = {
  MAX_SIZE: env.UPLOAD_MAX_SIZE * 1024 * 1024, // 转换为字节
  ALLOWED_TYPES: env.UPLOAD_ALLOWED_TYPES.split(','),
  UPLOAD_URL: env.API_BASE_URL + API_CONFIG.CHAT.ENDPOINTS.UPLOAD
}

/**
 * WebSocket 配置
 */
export const WS_CONFIG = {
  URL: env.WS_URL,
  RECONNECT_INTERVAL: 3000,
  MAX_RECONNECT_ATTEMPTS: 5,
  HEARTBEAT_INTERVAL: 30000
}
