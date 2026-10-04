package com.minecraft.recommendation.service;

import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.enums.JobTriggerType;
import com.minecraft.recommendation.event.RecommendationConfigChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationScheduleManagerTest {

    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private RecommendationConfigService configService;
    @Mock
    private RecommendationRecalcService recalcService;
    @Mock
    private RecommendationAnalyticsService analyticsService;
    @Mock
    @SuppressWarnings("rawtypes")
    private ScheduledFuture future;

    private RecommendationScheduleManager manager;

    @BeforeEach
    void setUp() {
        manager = new RecommendationScheduleManager(taskScheduler, configService,
                recalcService, analyticsService);
        when(taskScheduler.schedule(any(Runnable.class), any(CronTrigger.class))).thenReturn(future);
        when(future.isCancelled()).thenReturn(false);
        when(future.isDone()).thenReturn(false);
        when(configService.getBoolean(RecommendationDefaults.SCHEDULE_ENABLED)).thenReturn(true);
        when(configService.getGlobalString(RecommendationDefaults.SCHEDULE_CRON))
                .thenReturn("0 0 3 * * ?");
    }

    @Test
    void reschedule_enabled_registersCron() {
        manager.reschedule();

        assertTrue(manager.isScheduled());
        assertEquals("0 0 3 * * ?", manager.getActiveCron());
        assertNotNull(manager.nextRunTime());
        verify(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void reschedule_disabled_noSchedule() {
        when(configService.getBoolean(RecommendationDefaults.SCHEDULE_ENABLED)).thenReturn(false);

        manager.reschedule();

        assertFalse(manager.isScheduled());
        assertNull(manager.getActiveCron());
        assertNull(manager.nextRunTime());
        verify(taskScheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void reschedule_invalidCron_noSchedule() {
        when(configService.getGlobalString(RecommendationDefaults.SCHEDULE_CRON))
                .thenReturn("not a cron");

        manager.reschedule();

        assertFalse(manager.isScheduled());
        verify(taskScheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void reschedule_configReadFailure_safeNoSchedule() {
        when(configService.getBoolean(RecommendationDefaults.SCHEDULE_ENABLED))
                .thenThrow(new RuntimeException("db down"));

        assertDoesNotThrow(() -> manager.reschedule());
        assertFalse(manager.isScheduled());
    }

    @Test
    void reschedule_replacesPreviousFuture() {
        manager.reschedule();

        @SuppressWarnings("unchecked")
        ScheduledFuture<Void> old = (ScheduledFuture<Void>) future;
        // 再次注册（新 cron）
        manager.reschedule();

        verify(old).cancel(false);
        verify(taskScheduler, org.mockito.Mockito.times(2)).schedule(any(Runnable.class), any(CronTrigger.class));
        assertTrue(manager.isScheduled());
    }

    @Test
    void scheduledTask_runsRecalcAndPurge() {
        manager.reschedule();
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskScheduler).schedule(captor.capture(), any(CronTrigger.class));

        captor.getValue().run();

        verify(recalcService).recalculate(isNull(), org.mockito.ArgumentMatchers.eq(JobTriggerType.SYSTEM), isNull());
        verify(analyticsService).purgeExpired();
    }

    @Test
    void scheduledTask_recalcThrows_purgeStillRuns() {
        manager.reschedule();
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskScheduler).schedule(captor.capture(), any(CronTrigger.class));

        org.mockito.Mockito.doThrow(new RuntimeException("recalc boom"))
                .when(recalcService).recalculate(isNull(), any(), isNull());

        assertDoesNotThrow(() -> captor.getValue().run());
        verify(analyticsService).purgeExpired();
    }

    @Test
    void scheduledTask_purgeThrows_noPropagation() {
        manager.reschedule();
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskScheduler).schedule(captor.capture(), any(CronTrigger.class));
        org.mockito.Mockito.doThrow(new RuntimeException("purge boom"))
                .when(analyticsService).purgeExpired();

        assertDoesNotThrow(() -> captor.getValue().run());
    }

    @Test
    void configChanged_scheduleAffected_triggersReschedule() {
        manager.onConfigChanged(new RecommendationConfigChangedEvent(this, true, 1L));
        verify(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void configChanged_unrelatedKey_noReschedule() {
        manager.onConfigChanged(new RecommendationConfigChangedEvent(this, false, 1L));
        verify(taskScheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void nextRunTime_nullWhenFutureDone() {
        manager.reschedule();
        when(future.isDone()).thenReturn(true);
        assertNull(manager.nextRunTime());
        assertNull(manager.getActiveCron());
    }

    @Test
    void nextRunTimeValue_isFuture() {
        manager.reschedule();
        LocalDateTime next = manager.nextRunTime();
        assertNotNull(next);
        assertTrue(next.isAfter(LocalDateTime.now().minusMinutes(1)));
    }
}
