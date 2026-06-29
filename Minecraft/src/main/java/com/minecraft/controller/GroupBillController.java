package com.minecraft.controller;

import com.minecraft.dto.request.GroupBillDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupBill;
import com.minecraft.service.GroupBillService;
import com.minecraft.utils.SecurityUtils;
import com.minecraft.vo.GroupBillSettlementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AA账单")
@RestController
@RequestMapping("/api/group/{groupId}/bill")
public class GroupBillController {

    @Autowired
    private GroupBillService groupBillService;

    @Operation(summary = "添加账单")
    @PostMapping
    public ApiResponse<GroupBill> addBill(@PathVariable Long groupId, @RequestBody GroupBillDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        dto.setGroupId(groupId);
        GroupBill bill = groupBillService.addBill(dto, userId);
        return ApiResponse.success("添加成功", bill);
    }

    @Operation(summary = "修改账单")
    @PutMapping("/{billId}")
    public ApiResponse<Void> updateBill(@PathVariable Long billId, @RequestBody GroupBillDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupBillService.updateBill(billId, dto, userId);
        return ApiResponse.success("修改成功", null);
    }

    @Operation(summary = "删除账单")
    @DeleteMapping("/{billId}")
    public ApiResponse<Void> deleteBill(@PathVariable Long billId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupBillService.deleteBill(billId, userId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取账单列表")
    @GetMapping("/list")
    public ApiResponse<List<GroupBill>> getBillList(@PathVariable Long groupId) {
        List<GroupBill> bills = groupBillService.getBillList(groupId);
        return ApiResponse.success(bills);
    }

    @Operation(summary = "获取结算汇总")
    @GetMapping("/settlement")
    public ApiResponse<GroupBillSettlementVO> getSettlement(@PathVariable Long groupId) {
        GroupBillSettlementVO settlement = groupBillService.getSettlement(groupId);
        return ApiResponse.success(settlement);
    }

    @Operation(summary = "标记账单已结清")
    @PutMapping("/{billId}/settle")
    public ApiResponse<Void> settleBill(@PathVariable Long billId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupBillService.settleBill(billId, userId);
        return ApiResponse.success("已结清", null);
    }
}