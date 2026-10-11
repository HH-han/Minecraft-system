package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.response.LogisticsTrackDTO;
import com.minecraft.entity.LogisticsTrack;
import com.minecraft.entity.OrderShipping;
import com.minecraft.enums.ShippingStatus;
import com.minecraft.mapper.LogisticsTrackMapper;
import com.minecraft.mapper.OrderShippingMapper;
import com.minecraft.service.LogisticsProviderService;
import com.minecraft.service.LogisticsTrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物流轨迹服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsTrackServiceImpl extends ServiceImpl<LogisticsTrackMapper, LogisticsTrack> implements LogisticsTrackService {

    private final OrderShippingMapper orderShippingMapper;
    private final LogisticsProviderService logisticsProviderService;

    @Override
    public List<LogisticsTrackDTO> queryTrack(String trackingNo, String phone) {
        LambdaQueryWrapper<LogisticsTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsTrack::getTrackingNo, trackingNo).orderByDesc(LogisticsTrack::getTrackTime);
        List<LogisticsTrackDTO> local = list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
        if (!local.isEmpty()) {
            return local;
        }
        // TODO 本地无轨迹时：按 carrierCode 调用适配器补拉并落库
        return local;
    }

    @Override
    public List<LogisticsTrackDTO> listByShipping(Long shippingId) {
        LambdaQueryWrapper<LogisticsTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsTrack::getShippingId, shippingId).orderByDesc(LogisticsTrack::getTrackTime);
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTracks(Long shippingId, List<LogisticsTrackDTO> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }
        OrderShipping shipping = orderShippingMapper.selectById(shippingId);
        if (shipping == null) {
            return;
        }
        // 历史轨迹置为非最新
        update(new LambdaUpdateWrapper<LogisticsTrack>()
                .eq(LogisticsTrack::getShippingId, shippingId)
                .set(LogisticsTrack::getIsLatest, 0));

        for (LogisticsTrackDTO dto : tracks) {
            // TODO 去重：按 (shipping_id, track_time, context) 唯一，已存在则跳过
            LogisticsTrack track = new LogisticsTrack();
            track.setShippingId(shippingId);
            track.setTrackingNo(shipping.getTrackingNo());
            track.setCarrierCode(shipping.getCarrierCode());
            track.setTrackTime(dto.getTrackTime());
            track.setStatus(dto.getStatus());
            track.setContext(dto.getContext());
            track.setLocation(dto.getLocation());
            track.setLongitude(dto.getLongitude());
            track.setLatitude(dto.getLatitude());
            track.setIsLatest(0);
            save(track);
        }
        // TODO 取最新一条置 is_latest=1；映射 order_shipping.status；通过 WebSocket/SSE 推送
    }

    @Override
    public int syncTracks() {
        LambdaQueryWrapper<OrderShipping> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrderShipping::getStatus,
                        ShippingStatus.SHIPPED.getCode(),
                        ShippingStatus.IN_TRANSIT.getCode(),
                        ShippingStatus.DELIVERING.getCode())
                .isNotNull(OrderShipping::getTrackingNo);
        List<OrderShipping> pending = orderShippingMapper.selectList(wrapper);
        int count = 0;
        for (OrderShipping shipping : pending) {
            try {
                List<LogisticsTrackDTO> tracks = logisticsProviderService.query(
                        shipping.getCarrierCode(), shipping.getTrackingNo(), shipping.getReceiverPhone());
                if (tracks != null && !tracks.isEmpty()) {
                    saveTracks(shipping.getId(), tracks);
                    count++;
                }
            } catch (Exception e) {
                log.error("[物流] 同步轨迹失败 shippingId={}", shipping.getId(), e);
            }
        }
        return count;
    }

    private LogisticsTrackDTO toDTO(LogisticsTrack track) {
        LogisticsTrackDTO dto = new LogisticsTrackDTO();
        dto.setTrackTime(track.getTrackTime());
        dto.setStatus(track.getStatus());
        dto.setContext(track.getContext());
        dto.setLocation(track.getLocation());
        dto.setLongitude(track.getLongitude());
        dto.setLatitude(track.getLatitude());
        return dto;
    }
}
