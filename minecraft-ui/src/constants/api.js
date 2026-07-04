export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/user/logout',
    EMAIL_LOGIN: '/api/auth/email-login',
    USER_INFO: '/user/info'
  },

  USER: {
    BASE: '/user',
    UPDATE: '/user/update',
    AVATAR: '/user/avatar',
    PASSWORD: '/user/password'
  },

  TRAVEL: {
    BASE: '/travel',
    RECOMMEND: '/travel/recommend',
    DETAIL: '/travel/detail',
    SEARCH: '/travel/search'
  },

  HOTEL: {
    BASE: '/hotel',
    LIST: '/hotel/list',
    DETAIL: '/hotel/detail',
    BOOKING: '/hotel/booking'
  },

  ATTRACTION: {
    BASE: '/attraction',
    LIST: '/attraction/list',
    DETAIL: '/attraction/detail',
    BOOKING: '/attraction/booking'
  },

  FOOD: {
    BASE: '/food',
    LIST: '/food/list',
    DETAIL: '/food/detail'
  },

  TICKET: {
    BASE: '/ticket',
    LIST: '/ticket/list',
    DETAIL: '/ticket/detail',
    BOOKING: '/ticket/booking'
  },

  CART: {
    BASE: '/cart',
    ADD: '/cart/add',
    REMOVE: '/cart/remove',
    UPDATE: '/cart/update',
    LIST: '/cart/list',
    CLEAR: '/cart/clear'
  },

  ORDER: {
    BASE: '/order',
    CREATE: '/order/create',
    LIST: '/order/list',
    DETAIL: '/order/detail',
    CANCEL: '/order/cancel',
    CONFIRM: '/order/confirm'
  },

  PAYMENT: {
    BASE: '/payment',
    CREATE: '/payment/create',
    NOTIFY: '/payment/notify',
    STATUS: '/payment/status'
  },

  POINTS: {
    BASE: '/points',
    LIST: '/points/list',
    BALANCE: '/points/balance',
    HISTORY: '/points/history'
  },

  GROUP: {
    BASE: '/group',
    CREATE: '/group/create',
    LIST: '/group/list',
    DETAIL: '/group/detail',
    JOIN: '/group/join',
    LEAVE: '/group/leave',
    MEMBERS: '/group/members',
    POSTS: '/group/posts',
    ALBUM: '/group/album',
    BILL: '/group/bill',
    POLL: '/group/poll',
    TRIP_PLAN: '/group/trip-plan',
    LOCATION: '/group/location',
    NOTIFICATION: '/group/notification'
  },

  CHAT: {
    BASE: '/chat',
    MESSAGES: '/chat/messages',
    SEND: '/chat/send',
    CONTACTS: '/chat/contacts',
    HISTORY: '/chat/history'
  },

  CALL: {
    BASE: '/call',
    INVITE: '/call/invite',
    ACCEPT: '/call/accept',
    REJECT: '/call/reject',
    HANGUP: '/call/hangup'
  },

  FRIEND: {
    BASE: '/friend',
    ADD: '/friend/add',
    LIST: '/friend/list',
    ACCEPT: '/friend/accept',
    REJECT: '/friend/reject',
    DELETE: '/friend/delete',
    RECOMMEND: '/friend/recommend'
  },

  COLLECTION: {
    BASE: '/collection',
    ADD: '/collection/add',
    REMOVE: '/collection/remove',
    LIST: '/collection/list'
  },

  LIKE: {
    BASE: '/like',
    ADD: '/like/add',
    REMOVE: '/like/remove',
    LIST: '/like/list'
  },

  COMMENT: {
    BASE: '/comment',
    ADD: '/comment/add',
    LIST: '/comment/list',
    DELETE: '/comment/delete'
  },

  COMMUNITY: {
    BASE: '/community',
    POSTS: '/community/posts',
    DETAIL: '/community/detail',
    CREATE: '/community/create'
  },

  RECOMMEND: {
    BASE: '/recommend',
    HOME: '/recommend/home',
    MONTHLY: '/recommend/monthly'
  },

  NEWS: {
    BASE: '/news',
    LIST: '/news/list',
    DETAIL: '/news/detail'
  },

  SAFETY_TIPS: {
    BASE: '/safety-tips',
    LIST: '/safety-tips/list',
    DETAIL: '/safety-tips/detail'
  },

  CAROUSEL: {
    BASE: '/carousel',
    LIST: '/carousel/list'
  },

  UPLOAD: {
    BASE: '/upload',
    IMAGE: '/upload/image',
    FILE: '/upload/file'
  },

  WORLD_CHARACTERISTICS: {
    BASE: '/world-characteristics',
    LIST: '/world-characteristics/list',
    DETAIL: '/world-characteristics/detail'
  },

  CITIES: {
    BASE: '/cities',
    LIST: '/cities/list'
  },

  COUNTRIES: {
    BASE: '/countries',
    LIST: '/countries/list'
  },

  CONTINENTS: {
    BASE: '/continents',
    LIST: '/continents/list'
  },

  TRAVEL_DIARY: {
    BASE: '/travel-diary',
    LIST: '/travel-diary/list',
    DETAIL: '/travel-diary/detail',
    CREATE: '/travel-diary/create',
    UPDATE: '/travel-diary/update',
    DELETE: '/travel-diary/delete'
  },

  AI_TRIP: {
    BASE: '/ai-trip',
    RECOMMEND: '/ai-trip/recommend',
    PLAN: '/ai-trip/plan'
  }
}

export const HTTP_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
  PATCH: 'PATCH'
}

export const API_STATUS_CODES = {
  SUCCESS: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_ERROR: 500
}

export const API_ERROR_MESSAGES = {
  [API_STATUS_CODES.BAD_REQUEST]: '请求参数错误',
  [API_STATUS_CODES.UNAUTHORIZED]: '登录已过期，请重新登录',
  [API_STATUS_CODES.FORBIDDEN]: '没有权限访问',
  [API_STATUS_CODES.NOT_FOUND]: '请求资源不存在',
  [API_STATUS_CODES.INTERNAL_ERROR]: '服务器内部错误',
  NETWORK_ERROR: '网络异常',
  TIMEOUT: '请求超时'
}