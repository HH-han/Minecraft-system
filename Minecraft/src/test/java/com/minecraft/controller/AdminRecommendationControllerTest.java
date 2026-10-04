package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.recommendation.dto.ConfigUpdateParam;
import com.minecraft.recommendation.dto.RecalcResult;
import com.minecraft.recommendation.dto.RecommendationFeatureParam;
import com.minecraft.recommendation.service.AdminAuthorizationService;
import com.minecraft.recommendation.service.AdminRecommendationService;
import com.minecraft.recommendation.vo.AdminRecommendationItemVO;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO;
import com.minecraft.recommendation.vo.RecommendationScheduleStatusVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminRecommendationControllerTest {

    @Mock
    private AdminAuthorizationService authorizationService;
    @Mock
    private AdminRecommendationService adminService;
    @InjectMocks
    private AdminRecommendationController controller;

    @Test
    void allEndpoints_requireAdmin() {
        when(authorizationService.requireAdmin()).thenReturn(9L);

        controller.listConfig();
        controller.updateConfig(List.of(new ConfigUpdateParam()));
        controller.resetConfig(null);
        controller.listItems("hotel", 1, 20);
        controller.featureItem("hotel", 1L, new RecommendationFeatureParam());
        controller.recalc(null);
        controller.listJobs(null, 20);
        controller.analytics(null, 7);
        controller.scheduleStatus();

        verify(authorizationService, org.mockito.Mockito.times(9)).requireAdmin();
    }

    @Test
    void updateConfig_delegatesOperator() {
        when(authorizationService.requireAdmin()).thenReturn(9L);
        List<ConfigUpdateParam> updates = List.of(new ConfigUpdateParam());

        controller.updateConfig(updates);

        verify(adminService).updateConfig(updates, 9L);
    }

    @Test
    void featureItem_returnsUpdated() {
        when(authorizationService.requireAdmin()).thenReturn(9L);
        AdminRecommendationItemVO vo = new AdminRecommendationItemVO();
        vo.setItemId(1L);
        when(adminService.featureItem(eq("hotel"), eq(1L), any())).thenReturn(vo);

        ApiResponse<AdminRecommendationItemVO> response =
                controller.featureItem("hotel", 1L, new RecommendationFeatureParam());

        assertEquals(200, response.getCode());
        assertEquals(1L, response.getData().getItemId());
    }

    @Test
    void recalc_delegatesAndWraps() {
        when(authorizationService.requireAdmin()).thenReturn(9L);
        RecalcResult result = new RecalcResult();
        result.setCategory("ALL");
        result.setStatus("SUCCESS");
        when(adminService.triggerRecalc(isNull(), eq(9L))).thenReturn(result);

        ApiResponse<RecalcResult> response = controller.recalc(null);

        assertEquals(200, response.getCode());
        assertEquals("SUCCESS", response.getData().getStatus());
    }

    @Test
    void listJobs_returnsLogs() {
        when(authorizationService.requireAdmin()).thenReturn(9L);
        when(adminService.listJobs(isNull(), anyInt())).thenReturn(List.of(new RecommendationJobLog()));

        assertEquals(1, controller.listJobs(null, 20).getData().size());
    }

    @Test
    void analytics_returnsDashboard() {
        when(authorizationService.requireAdmin()).thenReturn(9L);
        when(adminService.analytics(isNull(), eq(7))).thenReturn(new RecommendationAnalyticsVO());

        assertEquals(200, controller.analytics(null, 7).getCode());
    }

    @Test
    void scheduleStatus_returnsState() {
        when(authorizationService.requireAdmin()).thenReturn(9L);
        when(adminService.scheduleStatus()).thenReturn(new RecommendationScheduleStatusVO());

        assertEquals(200, controller.scheduleStatus().getCode());
    }

    @Test
    void unauthorized_requestRejectedWith401() {
        doThrow(new BusinessException(401, "请先登录")).when(authorizationService).requireAdmin();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> controller.listItems("hotel", 1, 20));
        assertEquals(401, ex.getCode());
        verify(adminService, org.mockito.Mockito.never()).listItems(any(), any(), any());
    }
}
