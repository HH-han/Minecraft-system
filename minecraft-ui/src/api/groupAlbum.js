import request from '@/utils/request'

/**
 * 群组相册接口
 */

// 批量上传照片
export const uploadPhotos = (groupId, files) => {
  return request.post(`/group/${groupId}/album/upload`, files, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除照片
export const deletePhoto = (groupId, photoId) => {
  return request.delete(`/group/${groupId}/album/${photoId}`)
}

// 获取相册列表
export const getAlbumList = (groupId) => {
  return request.get(`/group/${groupId}/album/list`)
}