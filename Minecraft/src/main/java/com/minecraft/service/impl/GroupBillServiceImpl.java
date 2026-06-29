package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.GroupBillDTO;
import com.minecraft.entity.GroupBill;
import com.minecraft.entity.GroupBillSplit;
import com.minecraft.entity.GroupMember;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupBillMapper;
import com.minecraft.mapper.GroupBillSplitMapper;
import com.minecraft.mapper.GroupMemberMapper;
import com.minecraft.service.GroupBillService;
import com.minecraft.service.GroupMemberService;
import com.minecraft.vo.GroupBillSettlementVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupBillServiceImpl extends ServiceImpl<GroupBillMapper, GroupBill> implements GroupBillService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupBillSplitMapper groupBillSplitMapper;

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Override
    @Transactional
    public GroupBill addBill(GroupBillDTO dto, Long userId) {
        if (!groupMemberService.isMember(dto.getGroupId(), userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        GroupBill bill = new GroupBill();
        BeanUtils.copyProperties(dto, bill);
        bill.setPayerId(userId);
        bill.setIsSettled(false);
        save(bill);

        splitBill(bill.getId(), dto.getGroupId(), dto.getAmount());

        return bill;
    }

    @Override
    @Transactional
    public void updateBill(Long billId, GroupBillDTO dto, Long userId) {
        GroupBill bill = getById(billId);
        if (bill == null) {
            throw new BusinessException(404, "账单不存在");
        }
        if (!bill.getPayerId().equals(userId) && !groupMemberService.isAdmin(bill.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限修改");
        }

        BeanUtils.copyProperties(dto, bill);
        updateById(bill);
    }

    @Override
    @Transactional
    public void deleteBill(Long billId, Long userId) {
        GroupBill bill = getById(billId);
        if (bill == null) {
            throw new BusinessException(404, "账单不存在");
        }
        if (!bill.getPayerId().equals(userId) && !groupMemberService.isAdmin(bill.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限删除");
        }

        removeById(billId);

        LambdaQueryWrapper<GroupBillSplit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBillSplit::getBillId, billId);
        groupBillSplitMapper.delete(wrapper);
    }

    @Override
    public List<GroupBill> getBillList(Long groupId) {
        return baseMapper.selectByGroupId(groupId);
    }

    @Override
    public GroupBillSettlementVO getSettlement(Long groupId) {
        List<GroupBill> bills = baseMapper.selectUnsettledByGroupId(groupId);
        List<GroupMember> members = groupMemberMapper.selectByGroupId(groupId);

        Map<Long, BigDecimal> userDebts = new HashMap<>();
        Map<Long, BigDecimal> userCredits = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        members.forEach(member -> {
            userDebts.put(member.getUserId(), BigDecimal.ZERO);
            userCredits.put(member.getUserId(), BigDecimal.ZERO);
        });

        for (GroupBill bill : bills) {
            totalAmount = totalAmount.add(bill.getAmount());
            userCredits.merge(bill.getPayerId(), bill.getAmount(), BigDecimal::add);
        }

        int memberCount = members.size();
        if (memberCount > 0) {
            for (GroupBill bill : bills) {
                BigDecimal share = bill.getAmount().divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP);
                members.forEach(member -> {
                    if (!member.getUserId().equals(bill.getPayerId())) {
                        userDebts.merge(member.getUserId(), share, BigDecimal::add);
                    }
                });
            }
        }

        GroupBillSettlementVO settlement = new GroupBillSettlementVO();
        settlement.setUserDebts(userDebts);
        settlement.setUserCredits(userCredits);
        settlement.setTotalAmount(totalAmount);
        settlement.setMemberCount(memberCount);

        return settlement;
    }

    @Override
    @Transactional
    public void settleBill(Long billId, Long userId) {
        GroupBill bill = getById(billId);
        if (bill == null) {
            throw new BusinessException(404, "账单不存在");
        }
        if (!groupMemberService.isAdmin(bill.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限结算");
        }

        bill.setIsSettled(true);
        updateById(bill);

        LambdaQueryWrapper<GroupBillSplit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBillSplit::getBillId, billId);
        List<GroupBillSplit> splits = groupBillSplitMapper.selectList(wrapper);
        splits.forEach(split -> {
            split.setIsPaid(true);
            groupBillSplitMapper.updateById(split);
        });
    }

    private void splitBill(Long billId, Long groupId, BigDecimal amount) {
        List<GroupMember> members = groupMemberMapper.selectByGroupId(groupId);
        if (members.isEmpty()) {
            return;
        }

        int memberCount = members.size();
        BigDecimal share = amount.divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP);
        BigDecimal remainder = amount.subtract(share.multiply(BigDecimal.valueOf(memberCount)));

        for (int i = 0; i < members.size(); i++) {
            GroupBillSplit split = new GroupBillSplit();
            split.setBillId(billId);
            split.setUserId(members.get(i).getUserId());
            if (i == 0 && remainder.compareTo(BigDecimal.ZERO) > 0) {
                split.setShareAmount(share.add(remainder));
            } else {
                split.setShareAmount(share);
            }
            split.setIsPaid(false);
            groupBillSplitMapper.insert(split);
        }
    }
}