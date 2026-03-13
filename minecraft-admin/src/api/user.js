import request from '@/utils/request'

/**
 * 用户相关接口
 */

// 获取用户信息
export const getUserInfo = () => {
  return request.get('/user/info')
}

// 更新用户信息
export const updateUserInfo = (user) => {
  return request.put('/user/update', user)
}

// 修改密码
export const updatePassword = (oldPassword, newPassword) => {
  return request.put('/user/password', null, {
    params: {
      oldPassword,
      newPassword
    }
  })
}

// 发送邮箱验证码
export const EmailCaptcha = (data) => {
  return request.post('/auth/email-captcha', data)
}

// 删除用户
export const deleteUser = (id) => {
  return request.delete(`/user/${id}`)
}

// 获取所有用户
export const getAllUsers = () => {
  return request.get('/user/list')
}

// 上传头像
export const uploadAvatar = (formData) => {
  return request.post('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 新增用户
export const createUser = (user) => {
  return request.post('/auth/register', user)
}
