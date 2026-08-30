import { requestClient } from '#/api/request';

/**
 * 用户信息字段（与后端返回的用户数据 JSON 对齐）
 * 注意：password 等敏感字段不在此声明，也不在页面展示
 */
export interface UserDetail {
  /** 用户ID */
  id: number | string;
  /** 账号 */
  account: string;
  /** 用户名 */
  username: string;
  /** 昵称 */
  nickname: string;
  /** 性别：1-男 2-女 0-未知 */
  gender: number;
  /** 年龄 */
  age: number;
  /** 邮箱 */
  email: string;
  /** 手机号 */
  phone: string;
  /** 头像地址 */
  avatar: string;
  /** 个人简介 */
  bio: string;
  /** 兴趣爱好 */
  hobbies: string[];
  /** 职业 */
  occupation: string;
  /** 经验/工龄（年） */
  experience: number;
  /** 个性签名 */
  signature: string;
  /** 积分 */
  points: number;
  /** 状态：1-正常 0-停用 */
  status: number;
  /** 是否在线 */
  online: boolean;
  /** 权限码列表 */
  permissions: string[];
  /** 创建时间（时间戳或字符串） */
  createTime: number | string;
  /** 更新时间（时间戳或字符串） */
  updateTime: number | string;
}

/**
 * 用户相关接口
 */

// 获取当前登录用户详情
export const getUserDetail = (): Promise<UserDetail> => {
  return requestClient.get<UserDetail>('/user/detail');
};

// 获取用户信息
export const getUserInfo = (): Promise<any> => {
  return requestClient.get('/user/info');
};

// 更新用户信息
export const updateUserInfo = (user: any): Promise<any> => {
  return requestClient.put('/user/update', user);
};

// 修改密码
export const updatePassword = (
  oldPassword: string,
  newPassword: string,
): Promise<any> => {
  return requestClient.put('/user/password', null, {
    params: {
      oldPassword,
      newPassword,
    },
  });
};

// 发送邮箱验证码
export const EmailCaptcha = (data: any): Promise<any> => {
  return requestClient.post('/auth/email-captcha', data);
};

// 删除用户
export const deleteUser = (id: number | string): Promise<any> => {
  return requestClient.delete(`/user/${id}`);
};

// 获取所有用户
export const getAllUsers = (): Promise<any> => {
  return requestClient.get('/user/list');
};

// 获取用户统计总数
export const getUserCount = (): Promise<any> => {
  return requestClient.get('/user/count');
};

// 上传头像
export const uploadAvatar = (formData: FormData): Promise<any> => {
  return requestClient.post('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// 新增用户
export const createUser = (user: any): Promise<any> => {
  return requestClient.post('/auth/register', user);
};

// 管理员登录
export const adminLogin = (data: any): Promise<any> => {
  return requestClient.post('/user/admin/login', data);
};

// 普通用户登录
export const login = (data: any): Promise<any> => {
  return requestClient.post('/auth/login', data);
};
