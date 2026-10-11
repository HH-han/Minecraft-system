package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.response.LogisticsTrackDTO;
import com.minecraft.entity.LogisticsTrack;

import java.util.List;

/**
 * 物流轨迹服务
 */
public interface LogisticsTrackService extends IService<LogisticsTrack> {

    /** 查询运单轨迹（本地缓存数据，命中不足可由适配器补拉） */
    List<LogisticsTrackDTO> queryTrack(String trackingNo, String phone);

    /** 查询某发货单的轨迹 */
    List<LogisticsTrackDTO> listByShipping(Long shippingId);

    /** 保存轨迹（去重，更新 is_latest 与发货单状态） */
    void saveTracks(Long shippingId, List<LogisticsTrackDTO> tracks);

    /** 定时任务：拉取未签收运单轨迹，返回处理条数 */
    int syncTracks();
}
