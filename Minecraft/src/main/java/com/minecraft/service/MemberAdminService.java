package com.minecraft.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.MemberGrowthAdjustDTO;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.*;
import com.minecraft.enums.RefundStatus;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.*;
import com.minecraft.pay.entity.PayOrder;
import com.minecraft.pay.entity.PayRefund;
import com.minecraft.pay.enums.PayStatus;
import com.minecraft.pay.mapper.PayOrderMapper;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.vo.MemberGrowthRecordVO;
import com.minecraft.vo.MemberOrderVO;
import com.minecraft.vo.MemberUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会员管理端业务
 */
@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberService memberService;
    private final MemberLevelMapper levelMapper;
    private final MemberPackageMapper packageMapper;
    private final MemberOrderMapper orderMapper;
    private final UserMemberMapper userMemberMapper;
    private final MemberGrowthRecordMapper growthMapper;
    private final MemberSignRecordMapper signMapper;
    private final UserService userService;
    private final PayGatewayService payGatewayService;
    private final PayOrderMapper payOrderMapper;

    // ==================== 等级配置 ====================

    public List<MemberLevel> listLevels() {
        return levelMapper.selectList(new LambdaQueryWrapper<MemberLevel>()
                .orderByAsc(MemberLevel::getLevel));
    }

    @Transactional
    public MemberLevel saveLevel(MemberLevel level) {
        if (level.getLevel() == null) {
            throw new BusinessException("等级序号不能为空");
        }
        if (level.getStatus() == null) {
            level.setStatus(1);
        }
        if (level.getSort() == null) {
            level.setSort(level.getLevel());
        }
        levelMapper.insert(level);
        return level;
    }

    @Transactional
    public MemberLevel updateLevel(Long id, MemberLevel level) {
        MemberLevel existing = levelMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("会员等级不存在");
        }
        level.setId(id);
        levelMapper.updateById(level);
        return levelMapper.selectById(id);
    }

    @Transactional
    public void deleteLevel(Long id) {
        MemberLevel level = levelMapper.selectById(id);
        if (level == null) {
            return;
        }
        if (level.getLevel() != null && level.getLevel() == 0) {
            throw new BusinessException("默认等级不允许删除");
        }
        levelMapper.deleteById(id);
    }

    // ==================== 套餐配置 ====================

    public List<MemberPackage> listPackages() {
        return packageMapper.selectList(new LambdaQueryWrapper<MemberPackage>()
                .orderByAsc(MemberPackage::getSort)
                .orderByAsc(MemberPackage::getId));
    }

    @Transactional
    public MemberPackage savePackage(MemberPackage pkg) {
        if (pkg.getStatus() == null) {
            pkg.setStatus(1);
        }
        if (pkg.getSort() == null) {
            pkg.setSort(0);
        }
        if (pkg.getGivePoints() == null) {
            pkg.setGivePoints(0);
        }
        if (pkg.getGiveGrowth() == null) {
            pkg.setGiveGrowth(0);
        }
        packageMapper.insert(pkg);
        return pkg;
    }

    @Transactional
    public MemberPackage updatePackage(Long id, MemberPackage pkg) {
        MemberPackage existing = packageMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("会员套餐不存在");
        }
        pkg.setId(id);
        packageMapper.updateById(pkg);
        return packageMapper.selectById(id);
    }

    @Transactional
    public void deletePackage(Long id) {
        packageMapper.deleteById(id);
    }

    // ==================== 会员订单 ====================

    public PageResponse<MemberOrderVO> pageOrders(PageRequest request, Integer status, String keyword) {
        LambdaQueryWrapper<MemberOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(MemberOrder::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(MemberOrder::getOrderNo, keyword)
                    .or().like(MemberOrder::getPackageName, keyword));
        }
        wrapper.orderByDesc(MemberOrder::getCreateTime);

        Page<MemberOrder> page = orderMapper.selectPage(
                new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        Map<Long, User> userMap = loadUserMap(page.getRecords().stream()
                .map(MemberOrder::getUserId).collect(Collectors.toSet()));

        List<MemberOrderVO> records = page.getRecords().stream().map(order -> {
            MemberOrderVO vo = new MemberOrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setUserId(order.getUserId());
            vo.setPackageId(order.getPackageId());
            vo.setPackageName(order.getPackageName());
            vo.setAmount(order.getAmount());
            vo.setPayType(order.getPayType());
            vo.setStatus(order.getStatus());
            vo.setPayTime(order.getPayTime());
            vo.setStartTime(order.getStartTime());
            vo.setEndTime(order.getEndTime());
            vo.setCreateTime(order.getCreateTime());
            User user = userMap.get(order.getUserId());
            if (user != null) {
                vo.setAccount(user.getAccount());
                vo.setNickname(user.getNickname() == null ? user.getUsername() : user.getNickname());
            }
            return vo;
        }).toList();

        return new PageResponse<>(records, page.getTotal(), request.getPageNum(), request.getPageSize());
    }

    /**
     * 会员订单退款：回退会员有效期
     */
    @Transactional
    public void refundOrder(Long id) {
        MemberOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("会员订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new BusinessException("仅已支付的订单可以退款");
        }

        // 统一走聚合支付网关退款（历史订单无支付单时跳过渠道退款）
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getBizOrderNo, order.getOrderNo())
                .in(PayOrder::getStatus, PayStatus.SUCCESS.name(), PayStatus.REFUNDING.name())
                .orderByDesc(PayOrder::getId)
                .last("LIMIT 1"));
        if (payOrder != null) {
            PayRefund refund = payGatewayService.refund(
                    payOrder.getPayNo(), order.getAmount(), "会员订单退款", null);
            if (!RefundStatus.SUCCESS.getCode().equals(refund.getStatus())) {
                throw new BusinessException("渠道退款失败：" + refund.getFailReason());
            }
        }

        order.setStatus(3);
        orderMapper.updateById(order);

        MemberPackage pkg = packageMapper.selectById(order.getPackageId());
        UserMember member = userMemberMapper.selectOne(
                new LambdaQueryWrapper<UserMember>().eq(UserMember::getUserId, order.getUserId()));
        LocalDateTime now = LocalDateTime.now();
        if (pkg != null && pkg.getDurationDays() != null && member != null
                && member.getIsPaid() != null && member.getIsPaid() == 1
                && member.getExpireTime() != null && member.getExpireTime().isAfter(now)) {
            LocalDateTime newExpire = member.getExpireTime().minusDays(pkg.getDurationDays());
            if (newExpire.isBefore(now)) {
                newExpire = now;
                member.setIsPaid(0);
            }
            member.setExpireTime(newExpire);
            userMemberMapper.updateById(member);
        }
    }

    // ==================== 会员用户 ====================

    public PageResponse<MemberUserVO> pageUsers(PageRequest request, String keyword) {
        LambdaQueryWrapper<UserMember> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            List<Long> userIds = findUserIds(keyword);
            if (userIds.isEmpty()) {
                return new PageResponse<>(Collections.emptyList(), 0L,
                        request.getPageNum(), request.getPageSize());
            }
            wrapper.in(UserMember::getUserId, userIds);
        }
        wrapper.orderByDesc(UserMember::getUpdateTime);

        Page<UserMember> page = userMemberMapper.selectPage(
                new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        Map<Long, User> userMap = loadUserMap(page.getRecords().stream()
                .map(UserMember::getUserId).collect(Collectors.toSet()));
        Map<Long, MemberLevel> levelMap = levelMapper.selectList(null).stream()
                .collect(Collectors.toMap(MemberLevel::getId, Function.identity(), (a, b) -> a));

        List<MemberUserVO> records = page.getRecords().stream().map(member -> {
            MemberUserVO vo = new MemberUserVO();
            vo.setUserId(member.getUserId());
            vo.setGrowth(member.getGrowth());
            vo.setIsPaid(member.getIsPaid());
            vo.setExpireTime(member.getExpireTime());
            vo.setLastSignDate(member.getLastSignDate());
            vo.setSignContinuous(member.getSignContinuous());
            vo.setCreateTime(member.getCreateTime());
            MemberLevel level = levelMap.get(member.getLevelId());
            if (level != null) {
                vo.setLevel(level.getLevel());
                vo.setLevelName(level.getName());
                vo.setDiscount(level.getDiscount());
                vo.setPointsRate(level.getPointsRate());
            }
            User user = userMap.get(member.getUserId());
            if (user != null) {
                vo.setAccount(user.getAccount());
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname() == null ? user.getUsername() : user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            return vo;
        }).toList();

        return new PageResponse<>(records, page.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Transactional
    public void adjustGrowth(Long userId, MemberGrowthAdjustDTO dto) {
        memberService.addGrowth(userId, dto.getChangeValue(),
                MemberService.SOURCE_ADMIN, null,
                dto.getRemark() == null ? "管理员调整" : dto.getRemark());
    }

    @Transactional
    public void setLevel(Long userId, Long levelId) {
        MemberLevel level = levelMapper.selectById(levelId);
        if (level == null) {
            throw new BusinessException("会员等级不存在");
        }
        UserMember member = memberService.getOrCreate(userId);
        member.setLevelId(levelId);
        userMemberMapper.updateById(member);
    }

    public MemberBenefitRecord grantBenefit(Long userId, String benefitType, String content) {
        return memberService.grantBenefit(userId, benefitType, content);
    }

    // ==================== 流水记录 ====================

    public PageResponse<MemberGrowthRecordVO> pageGrowthRecords(
            PageRequest request, Long userId, String keyword) {
        LambdaQueryWrapper<MemberGrowthRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(MemberGrowthRecord::getUserId, userId);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(MemberGrowthRecord::getRemark, keyword)
                    .or().like(MemberGrowthRecord::getBizId, keyword));
        }
        wrapper.orderByDesc(MemberGrowthRecord::getCreateTime);

        Page<MemberGrowthRecord> page = growthMapper.selectPage(
                new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        Map<Long, User> userMap = loadUserMap(page.getRecords().stream()
                .map(MemberGrowthRecord::getUserId).collect(Collectors.toSet()));

        List<MemberGrowthRecordVO> records = page.getRecords().stream().map(record -> {
            MemberGrowthRecordVO vo = new MemberGrowthRecordVO();
            vo.setId(record.getId());
            vo.setUserId(record.getUserId());
            vo.setChangeValue(record.getChangeValue());
            vo.setAfterValue(record.getAfterValue());
            vo.setSource(record.getSource());
            vo.setBizId(record.getBizId());
            vo.setRemark(record.getRemark());
            vo.setCreateTime(record.getCreateTime());
            User user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setAccount(user.getAccount());
                vo.setNickname(user.getNickname() == null ? user.getUsername() : user.getNickname());
            }
            return vo;
        }).toList();

        return new PageResponse<>(records, page.getTotal(), request.getPageNum(), request.getPageSize());
    }

    public PageResponse<MemberSignRecord> pageSignRecords(PageRequest request, Long userId) {
        LambdaQueryWrapper<MemberSignRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(MemberSignRecord::getUserId, userId);
        }
        wrapper.orderByDesc(MemberSignRecord::getSignDate);
        Page<MemberSignRecord> page = signMapper.selectPage(
                new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return new PageResponse<>(page.getRecords(), page.getTotal(),
                request.getPageNum(), request.getPageSize());
    }

    // ==================== 内部方法 ====================

    private List<Long> findUserIds(String keyword) {
        List<User> users = userService.list(new LambdaQueryWrapper<User>()
                .and(w -> w.like(User::getAccount, keyword)
                        .or().like(User::getUsername, keyword)
                        .or().like(User::getNickname, keyword)
                        .or().like(User::getPhone, keyword))
                .last("LIMIT 200"));
        return users.stream().map(User::getId).toList();
    }

    private Map<Long, User> loadUserMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
    }
}
