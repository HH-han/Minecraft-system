import axios, { AxiosError, type AxiosRequestConfig, type AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import { apiBaseUrl, tokenStorageKey, userInfoStorageKey } from '@/config';

/** 后端统一返回包装（com.minecraft.dto.response.ApiResponse） */
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
}

const request = axios.create({
  baseURL: apiBaseUrl,
  timeout: 15000
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem(tokenStorageKey);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  const method = config.method?.toUpperCase();
  if (method && ['POST', 'PUT', 'PATCH'].includes(method) && !config.headers['Content-Type']) {
    config.headers['Content-Type'] = 'application/json';
  }
  return config;
});

function isApiResponse(body: unknown): body is ApiResponse {
  return Boolean(body) && typeof body === 'object' && 'code' in (body as Record<string, unknown>);
}

function handleUnauthorized(): void {
  localStorage.removeItem(tokenStorageKey);
  localStorage.removeItem(userInfoStorageKey);
  window.dispatchEvent(new CustomEvent('app:unauthorized'));
}

request.interceptors.response.use(
  (response: AxiosResponse) => {
    const body = response.data;
    // 非包装响应（文件流等）直接透传
    if (!isApiResponse(body)) return body;
    if (body.code === 200) return body;
    // 业务错误：统一提示并拒绝
    const message = body.message || '请求失败';
    ElMessage.error(message);
    return Promise.reject(new Error(message));
  },
  (error: AxiosError<ApiResponse>) => {
    if (axios.isCancel(error)) return Promise.reject(error);

    const status = error.response?.status;
    const body = error.response?.data;
    const silent = (error.config as AxiosRequestConfig & { silent?: boolean })?.silent;

    if (status === 401) {
      if (!silent) ElMessage.error(body?.message || '登录已过期，请重新登录');
      handleUnauthorized();
      return Promise.reject(error);
    }

    if (!silent) {
      const message =
        body?.message ||
        (error.code === 'ECONNABORTED'
          ? '请求超时，请稍后重试'
          : status
            ? `请求失败（${status}）`
            : '网络异常，请检查后端服务');
      ElMessage.error(message);
    }
    return Promise.reject(error);
  }
);

export default request;
