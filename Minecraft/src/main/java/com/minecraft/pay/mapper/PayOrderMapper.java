package com.minecraft.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.pay.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {
}
