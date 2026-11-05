import { requestClient } from '#/api/request';

/** Excel 业务类型 */
export interface ExcelBizTypeItem {
  code: string;
  desc: string;
}

/** 导入进度 */
export interface ExcelTaskProgress {
  taskNo: string;
  bizType: string;
  taskType?: number;
  fileName?: string;
  total: number;
  success: number;
  fail: number;
  status: number;
  percent: number;
  errorMsg?: null | string;
  errorFile?: boolean;
}

/** 导入导出任务 */
export interface ExcelTaskItem {
  id?: number;
  taskNo: string;
  bizType: string;
  taskType: number;
  fileName?: string;
  fileSize?: number;
  totalRows?: number;
  successRows?: number;
  failRows?: number;
  status: number;
  errorMsg?: null | string;
  errorFile?: null | string;
  startTime?: string;
  endTime?: string;
  createTime?: string;
}

/** 任务分页结果 */
export interface ExcelTaskPage {
  records: ExcelTaskItem[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

const BASE = '/excel';

/** 业务类型字典 */
export function getExcelBizTypes() {
  return requestClient.get<ExcelBizTypeItem[]>(`${BASE}/bizTypes`);
}

/** 上传 Excel 并提交异步导入任务 */
export function importExcel(
  bizType: string,
  file: File,
  params?: Record<string, any>,
) {
  const formData = new FormData();
  formData.append('file', file);
  if (params) {
    formData.append('params', JSON.stringify(params));
  }
  return requestClient.post<{ bizType: string; taskNo: string }>(
    `${BASE}/import/${bizType}`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      timeout: 300_000,
    },
  );
}

/** 轮询导入进度 */
export function getImportProgress(taskNo: string) {
  return requestClient.get<ExcelTaskProgress>(
    `${BASE}/task/${taskNo}/progress`,
  );
}

/** 任务分页列表 */
export function getExcelTaskList(params: {
  bizType?: string;
  pageNum: number;
  pageSize: number;
  status?: number;
}) {
  return requestClient.get<ExcelTaskPage>(`${BASE}/task/list`, { params });
}

/** 取消任务 */
export function cancelExcelTask(taskNo: string) {
  return requestClient.post(`${BASE}/task/${taskNo}/cancel`);
}

/** 下载导入模板（Blob） */
export function downloadExcelTemplate(bizType: string) {
  return requestClient.download<Blob>(`${BASE}/template/${bizType}`, {
    timeout: 60_000,
  });
}

/** 下载失败明细（Blob） */
export function downloadExcelErrorFile(taskNo: string) {
  return requestClient.download<Blob>(`${BASE}/task/${taskNo}/error-file`, {
    timeout: 60_000,
  });
}

/** 导出 Excel（Blob） */
export function exportExcel(
  bizType: string,
  query: Record<string, any> = {},
) {
  return requestClient.download<Blob>(`${BASE}/export/${bizType}`, {
    data: query,
    method: 'POST',
    timeout: 300_000,
  });
}

/**
 * 触发浏览器下载。
 * 后端异常时可能以 JSON 形式返回错误信息，这里做一次拦截，避免下载到错误文件。
 */
export async function saveBlob(blob: Blob, filename: string) {
  if (blob.type.includes('json')) {
    const text = await blob.text();
    let errorMessage = '';
    try {
      const data = JSON.parse(text);
      errorMessage = data?.message || data?.error || '';
    } catch {
      errorMessage = '';
    }
    throw new Error(errorMessage);
  }
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.append(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}
