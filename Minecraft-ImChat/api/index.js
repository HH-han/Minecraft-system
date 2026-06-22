/**
 * API 接口统一导出
 *
 * 参照 minecraft-App/api 目录的组织方式
 * 提供统一的接口导入入口
 */

import * as authApi from './auth.js'
import * as userApi from './user.js'
import * as chatApi from './chat.js'
import * as friendApi from './friend.js'
import * as groupApi from './group.js'

export { authApi, userApi, chatApi, friendApi, groupApi }

// 兼容旧版统一导出
export const api = {
  ...authApi.default,
  ...userApi.default,
  ...chatApi.default,
  ...friendApi.default,
  ...groupApi.default
}

export default api
