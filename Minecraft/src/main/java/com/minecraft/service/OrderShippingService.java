package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.ShippingBatchRequest;
import com.minecraft.dto.request.ShippingCreateRequest;
import com.minecraft.dto.response.OrderShippingVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OrderShipping;

import java.util.List;
import java.util.Set;

/**
 * 发货单服务
 */
public interface OrderShippingService extends IService<OrderShipping> {

    /** 使用物流系统的商品类型：product-纪念品，points-积分兑换实物（酒店/景点/美食不发货） */
    Set<String> SHIPPABLE_ITEM_TYPES = Set.of("product", "points");

    /** 创建发货单（录入收货信息与运单） */
    OrderShipping createShipping(ShippingCreateRequest request, Long adminId);

    /** 批量发货（同一承运商多订单） */
    void batchCreateShipping(ShippingBatchRequest request, Long adminId);

    /** 待发货订单分页列表（仅纪念品等实物商品：已支付且未发货） */
    PageResponse<?> listPendingOrders(PageRequest request);

    /** 查询订单下所有包裹 */
    List<OrderShippingVO> listByOrder(Long orderId);

    /** 查询当前用户全部包裹 */
    List<OrderShippingVO> listByUser(Long userId);

    /** 发货单详情（含轨迹） */
    OrderShippingVO detail(Long shippingId);

    /** 确认收货（当前登录用户） */
    void confirmReceive(Long shippingId, Long userId);

    /** 订阅物流推送 */
    void subscribe(Long shippingId, Long userId);

    /** 定时任务：自动确认收货（发货后 N 天），返回处理条数 */
    int autoConfirmReceive(int days);
}
