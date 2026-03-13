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
  return request.post('/api/auth/email-captcha', data)
}

// 上传头像
export const uploadAvatar = (formData) => {
  return request.post('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 根据账号获取用户信息
export const getUserByAccount = (account) => {
  return request.get('/user/by-account', {
    params: {
      account
    }
  })
}
