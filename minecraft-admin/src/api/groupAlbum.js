import request from '@/utils/request'

/**
 * 群组相册接口 - 管理后台
 */

// 获取相册列表
export const getAlbumList = (groupId, params) => {
  return request.get(`/api/group/${groupId}/album/list`, { params })
}

// 删除照片
export const deletePhoto = (groupId, photoId) => {
  return request.delete(`/api/group/${groupId}/album/${photoId}`)
}