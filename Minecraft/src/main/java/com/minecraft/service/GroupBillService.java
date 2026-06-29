package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.GroupBillDTO;
import com.minecraft.entity.GroupBill;
import com.minecraft.vo.GroupBillSettlementVO;

import java.util.List;

public interface GroupBillService extends IService<GroupBill> {
    GroupBill addBill(GroupBillDTO dto, Long userId);
    void updateBill(Long billId, GroupBillDTO dto, Long userId);
    void deleteBill(Long billId, Long userId);
    List<GroupBill> getBillList(Long groupId);
    GroupBillSettlementVO getSettlement(Long groupId);
    void settleBill(Long billId, Long userId);
}