package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.WalletAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface WalletAccountMapper extends BaseMapper<WalletAccount> {

    /**
     * 扣减余额（余额充足才生效），防透支
     * @return 受影响行数，0 表示余额不足或账户不存在
     */
    @Update("UPDATE wallet_account SET balance = balance - #{amount}, " +
            "total_expense = total_expense + #{amount}, version = version + 1 " +
            "WHERE user_id = #{userId} AND status = 1 AND balance >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 增加余额
     * @return 受影响行数
     */
    @Update("UPDATE wallet_account SET balance = balance + #{amount}, version = version + 1 " +
            "WHERE user_id = #{userId} AND status = 1")
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 充值（增加余额并累计充值额）
     */
    @Update("UPDATE wallet_account SET balance = balance + #{amount}, " +
            "total_recharge = total_recharge + #{amount}, version = version + 1 " +
            "WHERE user_id = #{userId} AND status = 1")
    int recharge(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
