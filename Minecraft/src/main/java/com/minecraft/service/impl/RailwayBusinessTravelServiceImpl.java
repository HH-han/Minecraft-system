package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.RailwayBusinessAccountRequest;
import com.minecraft.dto.request.RailwayBusinessTripRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayBusinessSummaryVO;
import com.minecraft.entity.RailwayBusinessAccount;
import com.minecraft.entity.RailwayBusinessTrip;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RailwayBusinessAccountMapper;
import com.minecraft.mapper.RailwayBusinessTripMapper;
import com.minecraft.service.RailwayBusinessTravelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RailwayBusinessTravelServiceImpl implements RailwayBusinessTravelService {

    private static final DateTimeFormatter NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final RailwayBusinessAccountMapper accountMapper;
    private final RailwayBusinessTripMapper tripMapper;

    @Override
    public RailwayBusinessAccount getAccount(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return accountMapper.selectOne(new LambdaQueryWrapper<RailwayBusinessAccount>()
                .eq(RailwayBusinessAccount::getUserId, userId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RailwayBusinessAccount saveAccount(Long userId, RailwayBusinessAccountRequest request) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        int budgetCents = request.getMonthlyBudget()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
        RailwayBusinessAccount account = getAccount(userId);
        if (account == null) {
            account = new RailwayBusinessAccount();
            account.setUserId(userId);
            account.setStatus(1);
        }
        account.setCompanyName(request.getCompanyName().trim());
        account.setTaxNo(StringUtils.hasText(request.getTaxNo()) ? request.getTaxNo().trim() : null);
        account.setContactName(request.getContactName().trim());
        account.setContactPhone(request.getContactPhone().trim());
        account.setMonthlyBudgetCents(budgetCents);
        if (account.getId() == null) {
            accountMapper.insert(account);
        } else {
            accountMapper.updateById(account);
        }
        return account;
    }

    @Override
    public RailwayBusinessSummaryVO summary(Long userId) {
        RailwayBusinessAccount account = getAccount(userId);
        YearMonth month = YearMonth.now();
        RailwayBusinessSummaryVO vo = new RailwayBusinessSummaryVO();
        vo.setAccount(account);
        vo.setMonth(month.toString());
        if (account == null) {
            vo.setUsedCents(0L);
            vo.setRemainingCents(0L);
            vo.setApprovedTrips(0L);
            vo.setUsagePercent(0);
            return vo;
        }
        long used = usedAmount(userId, month);
        int budget = account.getMonthlyBudgetCents() == null ? 0 : account.getMonthlyBudgetCents();
        vo.setUsedCents(used);
        vo.setRemainingCents(Math.max(0L, budget - used));
        vo.setApprovedTrips(countApproved(userId, month));
        vo.setUsagePercent(budget <= 0 ? 0 : (int) Math.min(100, used * 100 / budget));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RailwayBusinessTrip createTrip(Long userId, RailwayBusinessTripRequest request) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        RailwayBusinessAccount account = getAccount(userId);
        if (account == null) {
            throw new BusinessException("请先开通商旅企业账户");
        }
        if (request.getReturnDate() != null && request.getReturnDate().isBefore(request.getDepartDate())) {
            throw new BusinessException("返程日期不能早于出发日期");
        }
        if (request.getDepartDate().isBefore(LocalDate.now())) {
            throw new BusinessException("出发日期不能早于今天");
        }
        int amountCents = request.getEstimatedAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
        if (amountCents <= 0) {
            throw new BusinessException("预估票款必须大于 0");
        }

        YearMonth month = YearMonth.from(request.getDepartDate());
        long used = usedAmount(userId, month);
        int budget = account.getMonthlyBudgetCents() == null ? 0 : account.getMonthlyBudgetCents();

        RailwayBusinessTrip trip = new RailwayBusinessTrip();
        trip.setTripNo("B" + LocalDateTime.now().format(NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        trip.setUserId(userId);
        trip.setFromStation(request.getFromStation().trim());
        trip.setToStation(request.getToStation().trim());
        trip.setDepartDate(request.getDepartDate());
        trip.setReturnDate(request.getReturnDate());
        trip.setPurpose(request.getPurpose().trim());
        trip.setPassengerCount(request.getPassengerCount());
        trip.setEstimatedAmountCents(amountCents);

        if (budget <= 0) {
            trip.setStatus("REJECTED");
            trip.setAuditRemark("企业账户尚未设置月度差旅预算，请联系管理员配置");
        } else if (used + amountCents > budget) {
            trip.setStatus("REJECTED");
            trip.setAuditRemark("超出 " + month + " 月度差旅预算，剩余 "
                    + Math.max(0L, budget - used) / 100 + " 元");
        } else {
            trip.setStatus("APPROVED");
            trip.setAuditRemark("预算内自动审批通过");
        }
        tripMapper.insert(trip);
        return trip;
    }

    @Override
    public PageResponse<RailwayBusinessTrip> listTrips(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        int num = safePageNum(pageNum);
        int size = safePageSize(pageSize);
        Page<RailwayBusinessTrip> page = tripMapper.selectPage(new Page<>(num, size),
                new LambdaQueryWrapper<RailwayBusinessTrip>()
                        .eq(RailwayBusinessTrip::getUserId, userId)
                        .orderByDesc(RailwayBusinessTrip::getCreateTime));
        return new PageResponse<>(page.getRecords(), page.getTotal(), num, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTrip(Long userId, Long id) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        RailwayBusinessTrip trip = tripMapper.selectById(id);
        if (trip == null || !userId.equals(trip.getUserId())) {
            throw new BusinessException("出差申请不存在");
        }
        if (!"APPROVED".equals(trip.getStatus())) {
            throw new BusinessException("当前申请状态不可取消");
        }
        if (trip.getDepartDate() != null && trip.getDepartDate().isBefore(LocalDate.now())) {
            throw new BusinessException("已出发的申请不可取消");
        }
        trip.setStatus("CANCELLED");
        trip.setAuditRemark("用户主动取消");
        tripMapper.updateById(trip);
    }

    private long usedAmount(Long userId, YearMonth month) {
        List<Map<String, Object>> maps = tripMapper.selectMaps(new QueryWrapper<RailwayBusinessTrip>()
                .select("IFNULL(SUM(estimated_amount_cents), 0) AS amount")
                .eq("user_id", userId)
                .eq("status", "APPROVED")
                .ge("depart_date", month.atDay(1))
                .le("depart_date", month.atEndOfMonth()));
        if (maps == null || maps.isEmpty() || maps.get(0).get("amount") == null) {
            return 0L;
        }
        return Long.parseLong(String.valueOf(maps.get(0).get("amount")));
    }

    private long countApproved(Long userId, YearMonth month) {
        Long count = tripMapper.selectCount(new LambdaQueryWrapper<RailwayBusinessTrip>()
                .eq(RailwayBusinessTrip::getUserId, userId)
                .eq(RailwayBusinessTrip::getStatus, "APPROVED")
                .ge(RailwayBusinessTrip::getDepartDate, month.atDay(1))
                .le(RailwayBusinessTrip::getDepartDate, month.atEndOfMonth()));
        return count == null ? 0L : count;
    }

    private int safePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int safePageSize(Integer pageSize) {
        int size = pageSize == null ? 10 : pageSize;
        return Math.max(1, Math.min(size, 50));
    }
}
