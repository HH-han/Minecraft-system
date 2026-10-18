package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.RailwayTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RailwayTicketMapper extends BaseMapper<RailwayTicket> {

    /**
     * 校验同一乘车人是否已购买同一车次同一日期的有效车票（防一人多单）
     */
    @Select("SELECT COUNT(1) FROM railway_ticket t " +
            "JOIN railway_order o ON t.order_id = o.id " +
            "JOIN railway_service s ON o.service_id = s.id " +
            "WHERE t.id_card_no = #{idCardNo} AND s.train_id = #{trainId} AND s.service_date = #{serviceDate} " +
            "AND o.status IN ('PENDING', 'PAID')")
    int countActiveTicket(@Param("idCardNo") String idCardNo,
                          @Param("trainId") Long trainId,
                          @Param("serviceDate") java.time.LocalDate serviceDate);
}
