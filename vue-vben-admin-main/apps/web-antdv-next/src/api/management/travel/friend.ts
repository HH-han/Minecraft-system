// @ts-nocheck
import request from '#/utils/request';

/**
 * 好友相关接口
 */

// 添加好友
export const addFriend = (friendId: any): Promise<any> => {
  return request.post('/friend/add', null, {
    params: {
      friendId
    }
  })
}

// 接受好友请求
export const acceptFriend = (friendId: any): Promise<any> => {
  return request.post('/friend/accept', null, {
    params: {
      friendId
    }
  })
}

// 删除好友
export const deleteFriend = (friendId: any): Promise<any> => {
  return request.delete('/friend/delete', {
    params: {
      friendId
    }
  })
}

// 获取好友列表
export const getFriendList = (): Promise<any> => {
  return request.get('/friend/list')
}

// 获取好友请求列表
export const getFriendRequestList = (): Promise<any> => {
  return request.get('/friend/requests')
}
