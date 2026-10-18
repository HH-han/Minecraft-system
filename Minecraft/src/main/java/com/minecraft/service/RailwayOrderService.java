package com.minecraft.service;

import com.minecraft.dto.request.RailwayOrderCreateRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayAdminStatsVO;
import com.minecraft.dto.response.RailwayOrderVO;

public interface RailwayOrderService {

    RailwayOrderVO createOrder(Long userId, RailwayOrderCreateRequest request);

    PageResponse<RailwayOrderVO> listOrders(Long userId, Integer pageNum, Integer pageSize, String status);

    PageResponse<RailwayOrderVO> listAllOrders(Integer pageNum, Integer pageSize, String status, String keyword);

    RailwayOrderVO getOrder(Long userId, Long orderId);

    void payOrder(Long userId, Long orderId);

    void cancelOrder(Long userId, Long orderId);

    /**
     * 取消超时未支付订单并归还区间库存
     *
     * @return 处理的订单数量
     */
    int cancelExpiredOrders();

    RailwayAdminStatsVO stats();
}
