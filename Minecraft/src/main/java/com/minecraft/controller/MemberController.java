package com.minecraft.controller;

import com.minecraft.dto.request.MemberOpenDTO;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.MemberGrowthRecord;
import com.minecraft.entity.MemberLevel;
import com.minecraft.entity.MemberOrder;
import com.minecraft.entity.MemberPackage;
import com.minecraft.exception.BusinessException;
import com.minecraft.service.MemberService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.MemberInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端会员中心接口
 */
@Tag(name = "会员中心")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "会员中心信息")
    @GetMapping("/info")
    public ApiResponse<MemberInfoVO> info() {
        return ApiResponse.success(memberService.getInfo(requireLogin()));
    }

    @Operation(summary = "会员等级列表")
    @GetMapping("/levels")
    public ApiResponse<List<MemberLevel>> levels() {
        return ApiResponse.success(memberService.listLevels());
    }

    @Operation(summary = "会员套餐列表")
    @GetMapping("/packages")
    public ApiResponse<List<MemberPackage>> packages() {
        return ApiResponse.success(memberService.listPackages());
    }

    @Operation(summary = "开通会员（生成订单）")
    @PostMapping("/open")
    public ApiResponse<MemberOrder> open(@Valid @RequestBody MemberOpenDTO dto) {
        return ApiResponse.success("订单已创建", memberService.open(requireLogin(), dto));
    }

    @Operation(summary = "支付会员订单（项目内模拟支付）")
    @PostMapping("/orders/{orderNo}/pay")
    public ApiResponse<MemberInfoVO> pay(@PathVariable String orderNo) {
        return ApiResponse.success("开通成功", memberService.payOrder(requireLogin(), orderNo));
    }

    @Operation(summary = "取消会员订单")
    @PostMapping("/orders/{orderNo}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String orderNo) {
        memberService.cancelOrder(requireLogin(), orderNo);
        return ApiResponse.success("已取消", null);
    }

    @Operation(summary = "每日签到")
    @PostMapping("/sign")
    public ApiResponse<MemberInfoVO> sign() {
        return ApiResponse.success("签到成功", memberService.sign(requireLogin()));
    }

    @Operation(summary = "成长值流水")
    @GetMapping("/growth/records")
    public ApiResponse<PageResponse<MemberGrowthRecord>> growthRecords(PageRequest request) {
        return ApiResponse.success(memberService.pageGrowth(requireLogin(), request));
    }

    @Operation(summary = "我的会员订单")
    @GetMapping("/orders")
    public ApiResponse<List<MemberOrder>> orders() {
        return ApiResponse.success(memberService.listOrders(requireLogin()));
    }

    @Operation(summary = "支付回调（由支付网关调用）")
    @PostMapping("/pay/callback")
    public ApiResponse<MemberInfoVO> payCallback(@RequestParam String orderNo) {
        return ApiResponse.success(memberService.markPaid(orderNo));
    }

    private Long requireLogin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
