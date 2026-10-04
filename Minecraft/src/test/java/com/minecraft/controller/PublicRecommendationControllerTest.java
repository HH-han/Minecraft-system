package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.service.RecommendationQueryService;
import com.minecraft.recommendation.vo.RecommendationItemVO;
import com.minecraft.recommendation.vo.RecommendationResultVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PublicRecommendationControllerTest {

    @Mock
    private RecommendationQueryService recommendationQueryService;
    @InjectMocks
    private PublicRecommendationController controller;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private RecommendationResultVO result(String source) {
        RecommendationResultVO vo = new RecommendationResultVO();
        vo.setCategory("attraction");
        vo.setSource(source);
        vo.setItems(List.of(new RecommendationItemVO()));
        return vo;
    }

    @Test
    void list_anonymous_passesNullUserId() {
        when(recommendationQueryService.list(eq(RecommendCategory.ATTRACTION), isNull(), eq(10),
                isNull(), isNull())).thenReturn(result("POPULAR"));

        ApiResponse<RecommendationResultVO> response = controller.list("attraction", 10, null, null);

        assertEquals(200, response.getCode());
        assertEquals("POPULAR", response.getData().getSource());
    }

    @Test
    void list_loggedIn_passesUserIdAndParams() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("42", null, List.of()));
        when(recommendationQueryService.list(eq(RecommendCategory.HOTEL), eq(42L), eq(5),
                eq("北京"), eq("winter"))).thenReturn(result("PERSONALIZED"));

        ApiResponse<RecommendationResultVO> response = controller.list("hotel", 5, "北京", "winter");

        assertEquals(200, response.getCode());
        assertEquals("PERSONALIZED", response.getData().getSource());
    }

    @Test
    void related_delegates() {
        when(recommendationQueryService.related(eq(RecommendCategory.RESTAURANT), eq(9L), eq(8)))
                .thenReturn(result("RELATED"));

        ApiResponse<RecommendationResultVO> response = controller.related("food", 9L, 8);

        assertEquals(200, response.getCode());
        assertEquals("RELATED", response.getData().getSource());
    }

    @Test
    void list_nonNumericPrincipal_treatedAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of()));
        when(recommendationQueryService.list(eq(RecommendCategory.ATTRACTION), isNull(), isNull(),
                isNull(), isNull())).thenReturn(result("POPULAR"));

        ApiResponse<RecommendationResultVO> response = controller.list("attraction", null, null, null);

        assertEquals(200, response.getCode());
        assertEquals("POPULAR", response.getData().getSource());
    }
}
