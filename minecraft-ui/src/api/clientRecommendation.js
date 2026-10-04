import request from '@/utils/request'

/**
 * 客户端推荐接口
 * 服务端结合后台推荐规则（置顶/加权/隐藏）与综合推荐分返回结果
 */

// 获取客户端推荐列表
// category: attraction | hotel | food | product
// params: { limit, city }
export const getClientRecommendations = (category, params) => {
  return request.get(`/client/recommendations/${category}`, { params })
}
