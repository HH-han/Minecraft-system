import request from '@/utils/request'

// 上传文件
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/file', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 上传分片
export const uploadChunk = (data) => {
  const formData = new FormData()
  formData.append('file', data.file)
  formData.append('fileMd5', data.fileMd5)
  formData.append('chunkIndex', data.chunkIndex)
  formData.append('totalChunks', data.totalChunks)
  return request.post('/upload/chunk', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 合并分片
export const mergeChunks = (params) => {
  return request.post('/upload/merge', null, { params })
}
