import request from '@/utils/request'

export const getCaptcha = (traceId) => {
  return request.get('/captcha/get', {
    params: { traceId }
  })
}

export const verifyCaptcha = (data) => {
  return request.post('/captcha/verify', data)
}