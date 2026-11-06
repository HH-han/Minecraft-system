package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.WalletPasswordDTO;
import com.minecraft.dto.request.WalletRechargeDTO;
import com.minecraft.dto.request.WalletTransferDTO;
import com.minecraft.dto.request.WalletWithdrawDTO;
import com.minecraft.dto.response.WalletTransactionVO;
import com.minecraft.dto.response.WalletVO;
import com.minecraft.entity.WalletAccount;

import java.util.List;

/**
 * IM 钱包服务
 */
public interface WalletService extends IService<WalletAccount> {

    /**
     * 获取钱包信息（不存在则自动开户）
     */
    WalletVO getWallet(Long userId);

    /**
     * 钱包流水列表
     */
    List<WalletTransactionVO> listTransactions(Long userId, String type, int page, int size);

    /**
     * 充值
     */
    WalletVO recharge(WalletRechargeDTO dto);

    /**
     * 提现
     */
    WalletVO withdraw(WalletWithdrawDTO dto);

    /**
     * 转账给好友
     */
    WalletVO transfer(WalletTransferDTO dto);

    /**
     * 设置/修改支付密码
     */
    void setPayPassword(WalletPasswordDTO dto);
}
