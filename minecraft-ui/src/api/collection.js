import request from '@/utils/request'

/**
 * 收藏相关接口
 */

// 收藏
export const collect = (collectionRequest) => {
  return request.post('/collection/add', collectionRequest)
}

// 取消收藏
export const uncollect = (collectionRequest) => {
  return request.post('/collection/cancel', collectionRequest)
}

// 检查是否已收藏
export const isCollected = (itemType, itemId) => {
  return request.get('/collection/check', {
    params: {
      itemType,
      itemId
    }
  })
}
