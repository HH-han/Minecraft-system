import request from '@/utils/request'

// 会员中心信息
export const getMemberInfo = () => request.get('/member/info')

// 会员等级列表
export const getMemberLevels = () => request.get('/member/levels')

// 会员套餐列表
export const getMemberPackages = () => request.get('/member/packages')

// 开通会员（生成待支付订单）
export const openMember = (data) => request.post('/member/open', data)

// 支付会员订单（内部统一走聚合支付网关）
export const payMemberOrder = (orderNo) =>
  request.post(`/member/orders/${orderNo}/pay`)

// 取消会员订单
export const cancelMemberOrder = (orderNo) =>
  request.post(`/member/orders/${orderNo}/cancel`)

// 每日签到
export const signIn = () => request.post('/member/sign')

// 成长值流水
export const getGrowthRecords = (params) =>
  request.get('/member/growth/records', { params })

// 我的会员订单
export const getMyMemberOrders = () => request.get('/member/orders')
