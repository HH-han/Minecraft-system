import { requestClient } from '#/api/request';

// 上传文件
export const uploadFile = (file: any): Promise<any> => {
  const formData = new FormData();
  formData.append('file', file);
  return requestClient.post('/upload/file', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// 上传分片
export const uploadChunk = (data: any): Promise<any> => {
  const formData = new FormData();
  formData.append('file', data.file);
  formData.append('fileMd5', data.fileMd5);
  formData.append('chunkIndex', data.chunkIndex);
  formData.append('totalChunks', data.totalChunks);
  return requestClient.post('/upload/chunk', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// 合并分片
export const mergeChunks = (params: any): Promise<any> => {
  return requestClient.post('/upload/merge', null, { params });
};
