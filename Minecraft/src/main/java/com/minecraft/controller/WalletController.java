package com.minecraft.controller;

import com.minecraft.dto.request.WalletPasswordDTO;
import com.minecraft.dto.request.WalletRechargeDTO;
import com.minecraft.dto.request.WalletTransferDTO;
import com.minecraft.dto.request.WalletWithdrawDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.WalletTransactionVO;
import com.minecraft.dto.response.WalletVO;
import com.minecraft.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * IM 钱包（服务页）
 */
@Tag(name = "IM钱包")
@RestController
@RequestMapping("/api/public/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Operation(summary = "钱包信息（不存在自动开户）")
    @GetMapping("/info")
    public ApiResponse<WalletVO> info(@RequestParam Long userId) {
        return ApiResponse.success("成功", walletService.getWallet(userId));
    }

    @Operation(summary = "钱包流水")
    @GetMapping("/transactions")
    public ApiResponse<List<WalletTransactionVO>> transactions(
            @RequestParam Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success("成功", walletService.listTransactions(userId, type, page, size));
    }

    @Operation(summary = "充值")
    @PostMapping("/recharge")
    public ApiResponse<WalletVO> recharge(@Valid @RequestBody WalletRechargeDTO dto) {
        return ApiResponse.success("充值成功", walletService.recharge(dto));
    }

    @Operation(summary = "提现")
    @PostMapping("/withdraw")
    public ApiResponse<WalletVO> withdraw(@Valid @RequestBody WalletWithdrawDTO dto) {
        return ApiResponse.success("提现成功", walletService.withdraw(dto));
    }

    @Operation(summary = "转账给好友")
    @PostMapping("/transfer")
    public ApiResponse<WalletVO> transfer(@Valid @RequestBody WalletTransferDTO dto) {
        return ApiResponse.success("转账成功", walletService.transfer(dto));
    }

    @Operation(summary = "设置/修改支付密码")
    @PostMapping("/pay-password")
    public ApiResponse<Void> setPayPassword(@Valid @RequestBody WalletPasswordDTO dto) {
        walletService.setPayPassword(dto);
        return ApiResponse.success("支付密码设置成功", null);
    }
}
