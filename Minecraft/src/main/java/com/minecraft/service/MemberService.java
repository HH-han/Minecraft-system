package com.minecraft.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.alibaba.fastjson2.JSON;
import com.minecraft.dto.request.MemberOpenDTO;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.*;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.*;
import com.minecraft.pay.dto.PayCreateRequest;
import com.minecraft.pay.dto.PayCreateResponse;
import com.minecraft.pay.enums.PayBizType;
import com.minecraft.pay.enums.PayChannel;
import com.minecraft.pay.enums.PayStatus;
import com.minecraft.pay.service.PayGatewayService;
import com.minecraft.service.UserService;
import com.minecraft.vo.MemberInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 会员核心业务：等级、成长值、签到、套餐、开通订单
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    public static final int SOURCE_CONSUME = 1;
    public static final int SOURCE_SIGN = 2;
    public static final int SOURCE_COMMENT = 3;
    public static final int SOURCE_SHARE = 4;
    public static final int SOURCE_OPEN = 5;
    public static final int SOURCE_ADMIN = 6;

    private final UserMemberMapper userMemberMapper;
    private final MemberLevelMapper levelMapper;
    private final MemberPackageMapper packageMapper;
    private final MemberOrderMapper orderMapper;
    private final MemberGrowthRecordMapper growthMapper;
    private final MemberSignRecordMapper signMapper;
    private final MemberBenefitRecordMapper benefitMapper;
    private final UserService userService;
    private final PayGatewayService payGatewayService;

    /**
     * 获取或初始化用户会员信息
     */
    @Transactional
    public UserMember getOrCreate(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        UserMember member = userMemberMapper.selectOne(
                new LambdaQueryWrapper<UserMember>().eq(UserMember::getUserId, userId));
        if (member != null) {
            expireIfNeeded(member);
            return member;
        }

        MemberLevel baseLevel = getBaseLevel();
        UserMember created = new UserMember();
        created.setUserId(userId);
        created.setLevelId(baseLevel.getId());
        created.setGrowth(0);
        created.setIsPaid(0);
        created.setSignContinuous(0);
        try {
            userMemberMapper.insert(created);
            return created;
        } catch (DuplicateKeyException e) {
            // 并发初始化时唯一索引兜底
            return userMemberMapper.selectOne(
                    new LambdaQueryWrapper<UserMember>().eq(UserMember::getUserId, userId));
        }
    }

    /**
     * 会员中心信息 + 权益
     */
    public MemberInfoVO getInfo(Long userId) {
        UserMember member = getOrCreate(userId);
        MemberLevel level = resolveLevel(member.getLevelId());
        MemberLevel nextLevel = levelMapper.selectOne(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getStatus, 1)
                .gt(MemberLevel::getLevel, level.getLevel())
                .orderByAsc(MemberLevel::getLevel)
                .last("LIMIT 1"));

        MemberInfoVO vo = new MemberInfoVO();
        vo.setUserId(userId);
        vo.setGrowth(member.getGrowth() == null ? 0 : member.getGrowth());
        vo.setLevel(level.getLevel());
        vo.setLevelName(level.getName());
        vo.setLevelIcon(level.getIcon());
        vo.setDiscount(level.getDiscount());
        vo.setPointsRate(level.getPointsRate());
        vo.setLevelMinGrowth(level.getGrowthMin());
        vo.setIsPaid(member.getIsPaid());
        vo.setExpireTime(member.getExpireTime());
        vo.setSignContinuous(member.getSignContinuous() == null ? 0 : member.getSignContinuous());
        vo.setTodaySigned(LocalDate.now().equals(member.getLastSignDate()) ? 1 : 0);
        vo.setNextLevelGrowth(nextLevel == null
                ? 0
                : Math.max(0, nextLevel.getGrowthMin() - vo.getGrowth()));
        vo.setBenefits(parseBenefits(level.getBenefits()));

        User user = userService.getById(userId);
        if (user != null) {
            vo.setNickname(user.getNickname() == null ? user.getUsername() : user.getNickname());
            vo.setAvatar(user.getAvatar());
        }
        return vo;
    }

    /**
     * 可用的套餐列表
     */
    public List<MemberPackage> listPackages() {
        return packageMapper.selectList(new LambdaQueryWrapper<MemberPackage>()
                .eq(MemberPackage::getStatus, 1)
                .orderByAsc(MemberPackage::getSort)
                .orderByAsc(MemberPackage::getId));
    }

    /**
     * 启用的等级列表
     */
    public List<MemberLevel> listLevels() {
        return levelMapper.selectList(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getStatus, 1)
                .orderByAsc(MemberLevel::getLevel));
    }

    /**
     * 开通/续费会员（生成待支付订单）
     */
    @Transactional
    public MemberOrder open(Long userId, MemberOpenDTO dto) {
        MemberPackage pkg = packageMapper.selectById(dto.getPackageId());
        if (pkg == null || pkg.getStatus() == null || pkg.getStatus() == 0) {
            throw new BusinessException("套餐不存在或已下架");
        }
        MemberOrder order = new MemberOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPackageId(pkg.getId());
        order.setPackageName(pkg.getName());
        order.setAmount(pkg.getPrice());
        order.setPayType(dto.getPayType());
        order.setStatus(0);
        orderMapper.insert(order);
        return order;
    }

    /**
     * 支付成功回调：标记订单已支付并发放权益，支持续费叠加
     */
    @Transactional
    public MemberInfoVO markPaid(String orderNo) {
        MemberOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException("会员订单不存在");
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            return getInfo(order.getUserId());
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException("当前订单状态不支持支付");
        }

        MemberPackage pkg = packageMapper.selectById(order.getPackageId());
        if (pkg == null) {
            throw new BusinessException("套餐不存在");
        }

        UserMember member = getOrCreate(order.getUserId());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now;
        if (member.getIsPaid() != null && member.getIsPaid() == 1
                && member.getExpireTime() != null && member.getExpireTime().isAfter(now)) {
            // 未过期续费：从原到期时间顺延
            start = member.getExpireTime();
        }
        LocalDateTime end = start.plusDays(pkg.getDurationDays());

        order.setStatus(1);
        order.setPayTime(now);
        order.setStartTime(start);
        order.setEndTime(end);
        orderMapper.updateById(order);

        member.setIsPaid(1);
        member.setExpireTime(end);
        userMemberMapper.updateById(member);

        if (pkg.getGiveGrowth() != null && pkg.getGiveGrowth() > 0) {
            addGrowth(order.getUserId(), pkg.getGiveGrowth(), SOURCE_OPEN, orderNo, "开通会员赠送");
        }
        if (pkg.getGivePoints() != null && pkg.getGivePoints() > 0) {
            userService.addPoints(order.getUserId(), pkg.getGivePoints(), "开通会员赠送");
        }
        return getInfo(order.getUserId());
    }

    /**
     * 用户支付自己的会员订单（统一走聚合支付网关）
     */
    @Transactional
    public MemberInfoVO payOrder(Long userId, String orderNo) {
        MemberOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("会员订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            if (order.getStatus() != null && order.getStatus() == 1) {
                return getInfo(userId);
            }
            throw new BusinessException("当前订单状态不支持支付");
        }

        PayCreateRequest request = new PayCreateRequest();
        request.setBizOrderNo(orderNo);
        request.setBizType(PayBizType.MEMBER.name());
        request.setUserId(userId);
        request.setTotalAmount(order.getAmount());
        request.setChannelCode(resolvePayChannel(order.getPayType()));
        request.setSubject("会员开通：" + order.getPackageName());
        PayCreateResponse response = payGatewayService.pay(request);
        if (!PayStatus.isSuccess(response.getStatus())) {
            throw new BusinessException("支付未完成");
        }
        return getInfo(userId);
    }

    /** 会员订单 payType：1 支付宝 / 2 微信 / 3 余额 */
    private String resolvePayChannel(Integer payType) {
        if (payType == null) {
            return PayChannel.WECHAT.name();
        }
        return switch (payType) {
            case 1 -> PayChannel.ALIPAY.name();
            case 3 -> PayChannel.MOCK.name();
            default -> PayChannel.WECHAT.name();
        };
    }

    /**
     * 取消未支付的会员订单
     */
    @Transactional
    public void cancelOrder(Long userId, String orderNo) {
        MemberOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("会员订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new BusinessException("当前订单状态不支持取消");
        }
        order.setStatus(2);
        orderMapper.updateById(order);
    }

    /**
     * 每日签到
     */
    @Transactional
    public MemberInfoVO sign(Long userId) {
        UserMember member = getOrCreate(userId);
        LocalDate today = LocalDate.now();
        if (today.equals(member.getLastSignDate())) {
            throw new BusinessException("今日已签到");
        }

        int continuous = 1;
        if (member.getLastSignDate() != null
                && member.getLastSignDate().plusDays(1).equals(today)) {
            continuous = (member.getSignContinuous() == null ? 0 : member.getSignContinuous()) + 1;
        }
        int giveGrowth = 10 + Math.min(continuous, 7) * 5;
        int givePoints = 5 + Math.min(continuous, 7) * 2;

        member.setLastSignDate(today);
        member.setSignContinuous(continuous);
        userMemberMapper.updateById(member);

        MemberSignRecord signRecord = new MemberSignRecord();
        signRecord.setUserId(userId);
        signRecord.setSignDate(today);
        signRecord.setContinuous(continuous);
        signRecord.setGiveGrowth(giveGrowth);
        signRecord.setGivePoints(givePoints);
        try {
            signMapper.insert(signRecord);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("今日已签到");
        }

        addGrowth(userId, giveGrowth, SOURCE_SIGN, null, "每日签到");
        userService.addPoints(userId, givePoints, "每日签到");
        return getInfo(userId);
    }

    /**
     * 增加/扣减成长值并自动升降级
     */
    @Transactional
    public void addGrowth(Long userId, int value, int source, String bizId, String remark) {
        UserMember member = getOrCreate(userId);
        int before = member.getGrowth() == null ? 0 : member.getGrowth();
        int after = Math.max(0, before + value);
        member.setGrowth(after);
        applyLevel(member, after);
        userMemberMapper.updateById(member);

        MemberGrowthRecord record = new MemberGrowthRecord();
        record.setUserId(userId);
        record.setChangeValue(value);
        record.setAfterValue(after);
        record.setSource(source);
        record.setBizId(bizId);
        record.setRemark(remark);
        growthMapper.insert(record);
    }

    /**
     * 计算会员折扣价（供订单服务调用）
     */
    public BigDecimal calcDiscountPrice(Long userId, BigDecimal original) {
        if (userId == null || original == null) {
            return original;
        }
        try {
            UserMember member = getOrCreate(userId);
            MemberLevel level = resolveLevel(member.getLevelId());
            BigDecimal discount = level.getDiscount() == null ? BigDecimal.ONE : level.getDiscount();
            return original.multiply(discount).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            // 会员数据未初始化时不阻断正常下单
            log.warn("计算会员折扣失败，按原价处理: {}", e.getMessage());
            return original;
        }
    }

    /**
     * 当前会员积分倍率
     */
    public BigDecimal getPointsRate(Long userId) {
        if (userId == null) {
            return BigDecimal.ONE;
        }
        try {
            UserMember member = getOrCreate(userId);
            MemberLevel level = resolveLevel(member.getLevelId());
            return level.getPointsRate() == null ? BigDecimal.ONE : level.getPointsRate();
        } catch (Exception e) {
            // 会员数据未初始化时不阻断正常支付
            log.warn("获取会员积分倍率失败，按 1 倍处理: {}", e.getMessage());
            return BigDecimal.ONE;
        }
    }

    /**
     * 我的成长值流水（分页）
     */
    public PageResponse<MemberGrowthRecord> pageGrowth(Long userId, PageRequest request) {
        Page<MemberGrowthRecord> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<MemberGrowthRecord> result = growthMapper.selectPage(page,
                new LambdaQueryWrapper<MemberGrowthRecord>()
                        .eq(MemberGrowthRecord::getUserId, userId)
                        .orderByDesc(MemberGrowthRecord::getCreateTime));
        return new PageResponse<>(result.getRecords(), result.getTotal(),
                request.getPageNum(), request.getPageSize());
    }

    /**
     * 我的会员订单
     */
    public List<MemberOrder> listOrders(Long userId) {
        return orderMapper.selectList(new LambdaQueryWrapper<MemberOrder>()
                .eq(MemberOrder::getUserId, userId)
                .orderByDesc(MemberOrder::getCreateTime)
                .last("LIMIT 50"));
    }

    /**
     * 过期付费会员标记（定时任务）
     */
    @Transactional
    public int expirePaidMembers() {
        List<UserMember> expired = userMemberMapper.selectList(new LambdaQueryWrapper<UserMember>()
                .eq(UserMember::getIsPaid, 1)
                .isNotNull(UserMember::getExpireTime)
                .lt(UserMember::getExpireTime, LocalDateTime.now()));
        int count = 0;
        for (UserMember member : expired) {
            member.setIsPaid(0);
            userMemberMapper.updateById(member);
            count++;
        }
        return count;
    }

    /**
     * 生日礼包发放（定时任务）
     */
    @Transactional
    public int grantBirthdayBenefits() {
        LocalDate today = LocalDate.now();
        List<UserMember> members = userMemberMapper.selectList(new LambdaQueryWrapper<UserMember>()
                .isNotNull(UserMember::getBirthday)
                .apply("MONTH(birthday) = {0} AND DAY(birthday) = {1}",
                        today.getMonthValue(), today.getDayOfMonth()));
        int count = 0;
        LocalDateTime yearStart = LocalDateTime.of(today.getYear(), 1, 1, 0, 0);
        for (UserMember member : members) {
            MemberLevel level = resolveLevel(member.getLevelId());
            if (level.getLevel() == null || level.getLevel() < 1) {
                continue;
            }
            Long granted = benefitMapper.selectCount(new LambdaQueryWrapper<MemberBenefitRecord>()
                    .eq(MemberBenefitRecord::getUserId, member.getUserId())
                    .eq(MemberBenefitRecord::getBenefitType, "BIRTHDAY")
                    .ge(MemberBenefitRecord::getCreateTime, yearStart));
            if (granted != null && granted > 0) {
                continue;
            }
            MemberBenefitRecord record = new MemberBenefitRecord();
            record.setUserId(member.getUserId());
            record.setBenefitType("BIRTHDAY");
            record.setLevelId(level.getId());
            record.setContent("{\"points\":200,\"growth\":100}");
            record.setStatus(1);
            benefitMapper.insert(record);

            userService.addPoints(member.getUserId(), 200, "会员生日礼包");
            addGrowth(member.getUserId(), 100, SOURCE_ADMIN, null, "会员生日礼包");
            count++;
        }
        return count;
    }

    /**
     * 管理员手动发放权益
     */
    @Transactional
    public MemberBenefitRecord grantBenefit(Long userId, String benefitType, String content) {
        getOrCreate(userId);
        MemberBenefitRecord record = new MemberBenefitRecord();
        record.setUserId(userId);
        record.setBenefitType(benefitType);
        record.setContent(content);
        record.setStatus(1);
        benefitMapper.insert(record);
        return record;
    }

    // ==================== 内部方法 ====================

    private MemberLevel getBaseLevel() {
        MemberLevel base = levelMapper.selectOne(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getLevel, 0)
                .last("LIMIT 1"));
        if (base == null) {
            throw new BusinessException("会员等级配置缺失，请先执行 member.sql 初始化数据");
        }
        return base;
    }

    private MemberLevel resolveLevel(Long levelId) {
        MemberLevel level = levelId == null ? null : levelMapper.selectById(levelId);
        return level == null ? getBaseLevel() : level;
    }

    private void expireIfNeeded(UserMember member) {
        if (member.getIsPaid() != null && member.getIsPaid() == 1
                && member.getExpireTime() != null
                && member.getExpireTime().isBefore(LocalDateTime.now())) {
            member.setIsPaid(0);
            userMemberMapper.updateById(member);
        }
    }

    private void applyLevel(UserMember member, int growth) {
        List<MemberLevel> levels = levelMapper.selectList(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getStatus, 1)
                .orderByDesc(MemberLevel::getGrowthMin));
        for (MemberLevel level : levels) {
            if (growth >= level.getGrowthMin()) {
                member.setLevelId(level.getId());
                return;
            }
        }
    }

    private List<String> parseBenefits(String benefits) {
        if (benefits == null || benefits.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseArray(benefits, String.class);
        } catch (Exception e) {
            log.warn("会员权益配置解析失败: {}", benefits);
            return Collections.emptyList();
        }
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "M" + time + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }
}
