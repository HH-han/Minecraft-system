package com.minecraft.task;

import com.minecraft.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 会员定时任务：付费会员过期、生日礼包发放
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberTask {

    private final MemberService memberService;

    /**
     * 每日 00:10 处理过期付费会员（成长值等级保留）
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void expirePaidMembers() {
        try {
            int count = memberService.expirePaidMembers();
            if (count > 0) {
                log.info("[会员] 处理过期付费会员 {} 个", count);
            }
        } catch (Exception e) {
            log.error("[会员] 处理过期付费会员失败", e);
        }
    }

    /**
     * 每日 00:20 发放会员生日礼包
     */
    @Scheduled(cron = "0 20 0 * * ?")
    public void grantBirthdayBenefits() {
        try {
            int count = memberService.grantBirthdayBenefits();
            if (count > 0) {
                log.info("[会员] 发放生日礼包 {} 份", count);
            }
        } catch (Exception e) {
            log.error("[会员] 发放生日礼包失败", e);
        }
    }
}
