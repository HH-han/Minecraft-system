package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.WalletPasswordDTO;
import com.minecraft.dto.request.WalletRechargeDTO;
import com.minecraft.dto.request.WalletTransferDTO;
import com.minecraft.dto.request.WalletWithdrawDTO;
import com.minecraft.dto.response.WalletTransactionVO;
import com.minecraft.dto.response.WalletVO;
import com.minecraft.entity.User;
import com.minecraft.entity.WalletAccount;
import com.minecraft.entity.WalletTransaction;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.WalletAccountMapper;
import com.minecraft.mapper.WalletTransactionMapper;
import com.minecraft.service.UserService;
import com.minecraft.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * IM 钱包服务实现
 */
@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<WalletAccountMapper, WalletAccount>
        implements WalletService {

    private static final int STATUS_NORMAL = 1;

    private static final String TYPE_RECHARGE = "recharge";
    private static final String TYPE_WITHDRAW = "withdraw";
    private static final String TYPE_TRANSFER_OUT = "transfer_out";
    private static final String TYPE_TRANSFER_IN = "transfer_in";

    @Autowired
    private WalletTransactionMapper transactionMapper;

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletVO getWallet(Long userId) {
        return toVO(getOrCreateAccount(userId));
    }

    @Override
    public List<WalletTransactionVO> listTransactions(Long userId, String type, int page, int size) {
        Page<WalletTransaction> pageParam = new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 50));
        LambdaQueryWrapper<WalletTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletTransaction::getUserId, userId);
        if (type != null && !type.isBlank()) {
            wrapper.eq(WalletTransaction::getType, type);
        }
        wrapper.orderByDesc(WalletTransaction::getCreateTime);
        Page<WalletTransaction> result = transactionMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream().map(t -> {
            WalletTransactionVO vo = new WalletTransactionVO();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletVO recharge(WalletRechargeDTO dto) {
        Long userId = dto.getUserId();
        getOrCreateAccount(userId);
        int rows = baseMapper.recharge(userId, dto.getAmount());
        if (rows == 0) {
            throw new BusinessException("钱包状态异常，充值失败");
        }
        WalletAccount account = getByUserId(userId);
        insertTransaction(userId, TYPE_RECHARGE, dto.getAmount(), account.getBalance(), null, "钱包充值");
        log.info("钱包充值成功 userId={}, amount={}", userId, dto.getAmount());
        return toVO(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletVO withdraw(WalletWithdrawDTO dto) {
        Long userId = dto.getUserId();
        WalletAccount account = getOrCreateAccount(userId);
        verifyPayPassword(account, dto.getPayPassword());
        int rows = baseMapper.deductBalance(userId, dto.getAmount());
        if (rows == 0) {
            throw new BusinessException("余额不足");
        }
        WalletAccount refreshed = getByUserId(userId);
        insertTransaction(userId, TYPE_WITHDRAW, dto.getAmount(), refreshed.getBalance(), null, "余额提现");
        log.info("钱包提现成功 userId={}, amount={}", userId, dto.getAmount());
        return toVO(refreshed);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletVO transfer(WalletTransferDTO dto) {
        Long userId = dto.getUserId();
        Long toUserId = dto.getToUserId();
        if (Objects.equals(userId, toUserId)) {
            throw new BusinessException("不能给自己转账");
        }
        User target = userService.getUserInfo(toUserId);
        if (target == null) {
            throw new BusinessException("收款用户不存在");
        }
        WalletAccount account = getOrCreateAccount(userId);
        verifyPayPassword(account, dto.getPayPassword());

        // 扣减转出方（SQL 层保证不透支）
        int rows = baseMapper.deductBalance(userId, dto.getAmount());
        if (rows == 0) {
            throw new BusinessException("余额不足");
        }
        WalletAccount refreshed = getByUserId(userId);

        String targetName = target.getNickname() != null ? target.getNickname() : target.getUsername();
        String remark = dto.getRemark() != null && !dto.getRemark().isBlank()
                ? dto.getRemark().trim() : "转账给" + targetName;
        String bizNo = generateBizNo();
        insertTransaction(userId, TYPE_TRANSFER_OUT, dto.getAmount(), refreshed.getBalance(), toUserId, remark);

        // 增加收款方余额（收款方账户可能被冻结时退回失败由事务回滚）
        getOrCreateAccount(toUserId);
        int inRows = baseMapper.addBalance(toUserId, dto.getAmount());
        if (inRows == 0) {
            throw new BusinessException("收款方钱包状态异常，转账失败");
        }
        WalletAccount targetAccount = getByUserId(toUserId);
        insertTransaction(toUserId, TYPE_TRANSFER_IN, dto.getAmount(), targetAccount.getBalance(), userId, "来自好友的转账");
        log.info("钱包转账成功 from={}, to={}, amount={}", userId, toUserId, dto.getAmount());
        return toVO(refreshed);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPayPassword(WalletPasswordDTO dto) {
        WalletAccount account = getOrCreateAccount(dto.getUserId());
        if (account.getPayPassword() != null && !account.getPayPassword().isBlank()) {
            // 已设置过则校验旧密码
            if (dto.getOldPayPassword() == null
                    || !passwordEncoder.matches(dto.getOldPayPassword(), account.getPayPassword())) {
                throw new BusinessException("原支付密码错误");
            }
        }
        WalletAccount update = new WalletAccount();
        update.setId(account.getId());
        update.setPayPassword(passwordEncoder.encode(dto.getPayPassword()));
        updateById(update);
    }

    /**
     * 获取或创建钱包账户
     */
    private WalletAccount getOrCreateAccount(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        WalletAccount account = getByUserId(userId);
        if (account == null) {
            account = new WalletAccount();
            account.setUserId(userId);
            account.setBalance(BigDecimal.ZERO);
            account.setFrozenAmount(BigDecimal.ZERO);
            account.setTotalRecharge(BigDecimal.ZERO);
            account.setTotalExpense(BigDecimal.ZERO);
            account.setStatus(STATUS_NORMAL);
            save(account);
        }
        if (account.getStatus() != null && account.getStatus() != STATUS_NORMAL) {
            throw new BusinessException("钱包已被冻结，请联系客服");
        }
        return account;
    }

    private WalletAccount getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<WalletAccount>().eq(WalletAccount::getUserId, userId));
    }

    private void verifyPayPassword(WalletAccount account, String payPassword) {
        if (account.getPayPassword() == null || account.getPayPassword().isBlank()) {
            throw new BusinessException("请先设置支付密码");
        }
        if (!passwordEncoder.matches(payPassword, account.getPayPassword())) {
            throw new BusinessException("支付密码错误");
        }
    }

    private void insertTransaction(Long userId, String type, BigDecimal amount,
                                   BigDecimal balanceAfter, Long targetUserId, String remark) {
        WalletTransaction t = new WalletTransaction();
        t.setUserId(userId);
        t.setBizNo(generateBizNo());
        t.setType(type);
        t.setAmount(amount);
        t.setBalanceAfter(balanceAfter);
        t.setTargetUserId(targetUserId);
        t.setRemark(remark);
        t.setStatus(1);
        transactionMapper.insert(t);
    }

    private String generateBizNo() {
        return "W" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

    private WalletVO toVO(WalletAccount account) {
        WalletVO vo = new WalletVO();
        BeanUtils.copyProperties(account, vo);
        vo.setHasPayPassword(account.getPayPassword() != null && !account.getPayPassword().isBlank());
        return vo;
    }
}
