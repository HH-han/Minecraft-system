package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.RailwaySegmentInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RailwaySegmentInventoryMapper extends BaseMapper<RailwaySegmentInventory> {

    /**
     * 扣减相邻区间库存；通过 left_seats >= count 条件保证并发下不超卖
     */
    @Update("UPDATE railway_segment_inventory SET left_seats = left_seats - #{count} " +
            "WHERE service_id = #{serviceId} AND from_stop_seq = #{fromSeq} AND to_stop_seq = #{toSeq} " +
            "AND seat_type = #{seatType} AND left_seats >= #{count}")
    int decrement(@Param("serviceId") Long serviceId,
                  @Param("fromSeq") Integer fromSeq,
                  @Param("toSeq") Integer toSeq,
                  @Param("seatType") String seatType,
                  @Param("count") Integer count);

    /**
     * 归还相邻区间库存（取消/退款）
     */
    @Update("UPDATE railway_segment_inventory SET left_seats = LEAST(total_seats, left_seats + #{count}) " +
            "WHERE service_id = #{serviceId} AND from_stop_seq = #{fromSeq} AND to_stop_seq = #{toSeq} " +
            "AND seat_type = #{seatType}")
    int increment(@Param("serviceId") Long serviceId,
                  @Param("fromSeq") Integer fromSeq,
                  @Param("toSeq") Integer toSeq,
                  @Param("seatType") String seatType,
                  @Param("count") Integer count);
}
