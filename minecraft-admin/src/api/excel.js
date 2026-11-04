import request, { http } from '@/utils/request'

const BASE = '/excel'

/** 业务类型字典 */
export const getExcelBizTypes = () => http.get(`${BASE}/bizTypes`)

/** 上传 Excel 异步导入，返回 { taskNo } */
export const importExcel = (bizType, file, params) => {
  const formData = new FormData()
  formData.append('file', file)
  if (params) {
    formData.append('params', JSON.stringify(params))
  }
  return request.post(`${BASE}/import/${bizType}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 300000
  })
}

/** 轮询导入进度 */
export const getImportProgress = (taskNo) =>
  request.get(`${BASE}/task/${taskNo}/progress`, { silent: true })

/** 任务分页列表 */
export const getExcelTaskList = (params) => http.get(`${BASE}/task/list`, params)

/** 取消任务 */
export const cancelExcelTask = (taskNo) => request.post(`${BASE}/task/${taskNo}/cancel`)

/** 下载导入模板（返回 Blob） */
export const downloadExcelTemplate = (bizType) =>
  request.get(`${BASE}/template/${bizType}`, { responseType: 'blob', timeout: 300000 })

/** 下载失败明细（返回 Blob） */
export const downloadExcelErrorFile = (taskNo) =>
  request.get(`${BASE}/task/${taskNo}/error-file`, { responseType: 'blob', timeout: 300000 })

/** 导出 Excel（返回 Blob） */
export const exportExcel = (bizType, query = {}) =>
  request.post(`${BASE}/export/${bizType}`, query, { responseType: 'blob', timeout: 300000 })

/** 触发浏览器下载 Blob */
export const saveBlob = (blob, filename) => {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
