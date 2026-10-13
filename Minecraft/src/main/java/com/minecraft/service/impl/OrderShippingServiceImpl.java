package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.ShippingBatchRequest;
import com.minecraft.dto.request.ShippingCreateRequest;
import com.minecraft.dto.response.OrderShippingVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.ExchangeOrder;
import com.minecraft.entity.LogisticsCarrier;
import com.minecraft.entity.LogisticsSubscription;
import com.minecraft.entity.Order;
import com.minecraft.entity.OrderShipping;
import com.minecraft.enums.OrderStatus;
import com.minecraft.enums.ShippingStatus;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.ExchangeOrderMapper;
import com.minecraft.mapper.LogisticsCarrierMapper;
import com.minecraft.mapper.LogisticsSubscriptionMapper;
import com.minecraft.mapper.OrderMapper;
import com.minecraft.mapper.OrderShippingMapper;
import com.minecraft.service.LogisticsTrackService;
import com.minecraft.service.OrderShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 发货单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderShippingServiceImpl extends ServiceImpl<OrderShippingMapper, OrderShipping> implements OrderShippingService {

    private final OrderMapper orderMapper;
    private final LogisticsCarrierMapper carrierMapper;
    private final LogisticsSubscriptionMapper subscriptionMapper;
    private final ExchangeOrderMapper exchangeOrderMapper;
    private final LogisticsTrackService logisticsTrackService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderShipping createShipping(ShippingCreateRequest request, Long adminId) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.PAID.getCode().toString().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许发货");
        }

        OrderShipping shipping = new OrderShipping();
        shipping.setShippingNo(generateShippingNo());
        shipping.setOrderId(order.getId());
        shipping.setOrderNo(order.getOrderNo());
        shipping.setUserId(order.getUserId());
        // 商品快照
        shipping.setItemType(order.getItemType());
        shipping.setItemId(order.getItemId());
        shipping.setItemName(order.getItemName());
        shipping.setItemImage(order.getImage());
        shipping.setQuantity(request.getQuantity() != null ? request.getQuantity() : order.getQuantity());
        // 承运与收货信息
        shipping.setCarrierId(request.getCarrierId());
        shipping.setCarrierCode(request.getCarrierCode());
        // 运单号由系统自动生成（管理员如需录入真实运单号也可手动指定）
        String trackingNo = hasText(request.getTrackingNo())
                ? request.getTrackingNo().trim()
                : generateTrackingNo(request.getCarrierCode());
        shipping.setTrackingNo(trackingNo);
        // 收货信息优先取发货单录入值，缺省回落到订单在下单时留存的收件信息
        shipping.setReceiverName(hasText(request.getReceiverName()) ? request.getReceiverName() : order.getReceiverName());
        shipping.setReceiverPhone(hasText(request.getReceiverPhone()) ? request.getReceiverPhone() : order.getReceiverPhone());
        shipping.setReceiverAddress(hasText(request.getReceiverAddress()) ? request.getReceiverAddress() : order.getReceiverAddress());
        shipping.setPackageWeight(request.getPackageWeight());
        shipping.setFreight(request.getFreight());
        shipping.setExpectTime(request.getExpectTime());
        shipping.setRemark(request.getRemark());

        // 创建发货单即视为已发货
        shipping.setStatus(ShippingStatus.SHIPPED.getCode());
        shipping.setShipTime(LocalDateTime.now());
        save(shipping);

        // 同步订单物流字段
        order.setShippingStatus(2);
        order.setCarrierCode(request.getCarrierCode());
        order.setTrackingNo(trackingNo);
        order.setShipTime(shipping.getShipTime());
        order.setStatus(OrderStatus.SHIPPED.getCode().toString());
        orderMapper.updateById(order);

        // 积分兑换来源的订单，同步更新兑换单状态
        syncExchangeOrderStatus(order, "已发货");

        // TODO 调用承运商 subscribe 订阅轨迹回调；通过 WebSocket/SSE 推送"已发货"
        return shipping;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateShipping(ShippingBatchRequest request, Long adminId) {
        for (Long orderId : request.getOrderIds()) {
            ShippingCreateRequest single = new ShippingCreateRequest();
            single.setOrderId(orderId);
            single.setCarrierId(request.getCarrierId());
            single.setCarrierCode(request.getCarrierCode());
            single.setRemark(request.getRemark());
            // TODO 批量场景需从地址簿/订单补全收件人信息（orders 当前无地址字段）
            createShipping(single, adminId);
        }
    }

    @Override
    public List<OrderShippingVO> listByOrder(Long orderId) {
        LambdaQueryWrapper<OrderShipping> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderShipping::getOrderId, orderId).orderByDesc(OrderShipping::getCreateTime);
        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderShippingVO> listByUser(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<OrderShipping> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderShipping::getUserId, userId).orderByDesc(OrderShipping::getCreateTime);
        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public OrderShippingVO detail(Long shippingId) {
        OrderShipping shipping = getById(shippingId);
        if (shipping == null) {
            throw new BusinessException("发货单不存在");
        }
        OrderShippingVO vo = toVO(shipping);
        vo.setTracks(logisticsTrackService.listByShipping(shippingId));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long shippingId, Long userId) {
        OrderShipping shipping = getById(shippingId);
        if (shipping == null) {
            throw new BusinessException("发货单不存在");
        }
        if (userId != null && !shipping.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该包裹");
        }
        if (ShippingStatus.RECEIVED.getCode().equals(shipping.getStatus())) {
            return; // 幂等
        }
        LocalDateTime now = LocalDateTime.now();
        shipping.setStatus(ShippingStatus.RECEIVED.getCode());
        shipping.setReceiveTime(now);
        updateById(shipping);

        Order order = orderMapper.selectById(shipping.getOrderId());
        if (order != null) {
            order.setShippingStatus(3);
            order.setReceiveTime(now);
            order.setStatus(OrderStatus.COMPLETED.getCode().toString());
            orderMapper.updateById(order);
            // 积分兑换来源的订单，同步更新兑换单状态
            syncExchangeOrderStatus(order, "已完成");
        }
        // TODO 多包裹场景需全部签收后再置订单完成
    }

    @Override
    public void subscribe(Long shippingId, Long userId) {
        OrderShipping shipping = getById(shippingId);
        if (shipping == null) {
            throw new BusinessException("发货单不存在");
        }
        Long count = subscriptionMapper.selectCount(new LambdaQueryWrapper<LogisticsSubscription>()
                .eq(LogisticsSubscription::getShippingId, shippingId)
                .eq(LogisticsSubscription::getUserId, userId));
        if (count != null && count > 0) {
            return; // 幂等
        }
        LogisticsSubscription sub = new LogisticsSubscription();
        sub.setShippingId(shippingId);
        sub.setUserId(userId);
        sub.setCarrierCode(shipping.getCarrierCode());
        sub.setTrackingNo(shipping.getTrackingNo());
        sub.setSubscribeAt(LocalDateTime.now());
        sub.setStatus(1);
        subscriptionMapper.insert(sub);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoConfirmReceive(int days) {
        LocalDateTime deadline = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<OrderShipping> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrderShipping::getStatus,
                        ShippingStatus.SHIPPED.getCode(),
                        ShippingStatus.IN_TRANSIT.getCode(),
                        ShippingStatus.DELIVERING.getCode())
                .isNotNull(OrderShipping::getShipTime)
                .lt(OrderShipping::getShipTime, deadline);
        List<OrderShipping> list = list(wrapper);
        for (OrderShipping shipping : list) {
            confirmReceive(shipping.getId(), null);
        }
        return list.size();
    }

    @Override
    public PageResponse<?> listPendingOrders(PageRequest request) {
        Page<Order> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, OrderStatus.PAID.getCode().toString())
                .and(w -> w.isNull(Order::getShippingStatus).or().eq(Order::getShippingStatus, 0))
                .orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(),
                request.getPageNum(), request.getPageSize());
    }

    /** 积分兑换来源的订单：同步兑换单状态（发货/签收） */
    private void syncExchangeOrderStatus(Order order, String status) {
        if (order == null || !"points".equals(order.getItemType())) {
            return;
        }
        ExchangeOrder exchangeOrder = exchangeOrderMapper.selectOne(
                new LambdaQueryWrapper<ExchangeOrder>()
                        .eq(ExchangeOrder::getOrderId, order.getId())
                        .last("LIMIT 1"));
        if (exchangeOrder != null) {
            exchangeOrder.setStatus(status);
            exchangeOrder.setUpdateTime(LocalDateTime.now());
            exchangeOrderMapper.updateById(exchangeOrder);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private OrderShippingVO toVO(OrderShipping shipping) {
        OrderShippingVO vo = new OrderShippingVO();
        vo.setId(shipping.getId());
        vo.setShippingNo(shipping.getShippingNo());
        vo.setOrderId(shipping.getOrderId());
        vo.setOrderNo(shipping.getOrderNo());
        vo.setItemName(shipping.getItemName());
        vo.setItemImage(shipping.getItemImage());
        vo.setQuantity(shipping.getQuantity());
        vo.setCarrierCode(shipping.getCarrierCode());
        vo.setTrackingNo(shipping.getTrackingNo());
        vo.setReceiverName(shipping.getReceiverName());
        vo.setReceiverPhone(shipping.getReceiverPhone());
        vo.setReceiverAddress(shipping.getReceiverAddress());
        vo.setFreight(shipping.getFreight());
        vo.setStatus(shipping.getStatus());
        vo.setStatusDesc(ShippingStatus.descriptionOf(shipping.getStatus()));
        vo.setShipTime(shipping.getShipTime());
        vo.setReceiveTime(shipping.getReceiveTime());
        vo.setExpectTime(shipping.getExpectTime());
        if (shipping.getCarrierId() != null) {
            LogisticsCarrier carrier = carrierMapper.selectById(shipping.getCarrierId());
            if (carrier != null) {
                vo.setCarrierName(carrier.getName());
            }
        }
        return vo;
    }

    /** 系统自动生成运单号：承运商编码(缺省 SF) + yyyyMMdd + 8位随机数 */
    private String generateTrackingNo(String carrierCode) {
        String prefix = hasText(carrierCode) ? carrierCode.trim().toUpperCase() : "SF";
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long rand = ThreadLocalRandom.current().nextLong(10_000_000L, 100_000_000L);
        return prefix + dateStr + rand;
    }

    private String generateShippingNo() {
        // TODO 建议改为 "SH" + yyyyMMdd + 4位序列（参考 OrderServiceImpl.generateOrderNo）
        return "SH" + System.currentTimeMillis();
    }
}
