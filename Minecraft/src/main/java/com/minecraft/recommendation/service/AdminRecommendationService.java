package com.minecraft.recommendation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationJobLogMapper;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.dto.ConfigUpdateParam;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.dto.RecalcResult;
import com.minecraft.recommendation.dto.RecommendationFeatureParam;
import com.minecraft.recommendation.enums.JobTriggerType;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.AdminRecommendationItemVO;
import com.minecraft.recommendation.vo.RecommendationAnalyticsVO;
import com.minecraft.recommendation.vo.RecommendationScheduleStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 推荐管理后台业务编排：参数管理、人工干预、重算触发、任务日志、分析看板、调度状态。
 */
@Service
@RequiredArgsConstructor
public class AdminRecommendationService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_JOB_LIMIT = 200;

    private final RecommendationConfigService configService;
    private final RecommendationRecalcService recalcService;
    private final RecommendationAnalyticsService analyticsService;
    private final RecommendationScheduleManager scheduleManager;
    private final RecommendationMapperRegistry registry;
    private final RecommendationItemCatalog catalog;
    private final RecommendationJobLogMapper jobLogMapper;

    // ---------------- 参数管理 ----------------

    public List<com.minecraft.entity.recommendation.RecommendationConfig> listConfig() {
        return configService.listAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(List<ConfigUpdateParam> updates, Long operatorId) {
        configService.updateBatch(updates, operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetConfig(String categoryCode, Long operatorId) {
        RecommendCategory category = categoryCode == null || categoryCode.isBlank()
                ? null : RecommendCategory.fromCode(categoryCode.trim());
        configService.resetToDefaults(category, operatorId);
    }

    // ---------------- 物品干预 ----------------

    /**
     * 分页查看某分类的推荐行。page 从 1 开始。
     */
    public List<AdminRecommendationItemVO> listItems(String categoryCode, Integer page, Integer size) {
        RecommendCategory category = RecommendCategory.fromCode(categoryCode);
        int pageSize = normalizeSize(size);
        int pageNo = page == null || page < 1 ? 1 : page;
        int offset = (pageNo - 1) * pageSize;

        List<BaseRecommendationItem> rows = registry.selectScoredPage(category, offset, pageSize);
        Map<Long, ItemDetail> details = catalog.load(category,
                rows.stream().map(BaseRecommendationItem::getItemId).toList());

        List<AdminRecommendationItemVO> result = new ArrayList<>(rows.size());
        for (BaseRecommendationItem row : rows) {
            result.add(toAdminItemVO(row, details.get(row.getItemId())));
        }
        return result;
    }

    private AdminRecommendationItemVO toAdminItemVO(BaseRecommendationItem row, ItemDetail detail) {
        AdminRecommendationItemVO vo = new AdminRecommendationItemVO();
        vo.setItemId(row.getItemId());
        if (detail != null) {
            vo.setName(detail.getName());
            vo.setCity(detail.getCity());
        }
        vo.setRecommendationScore(row.getRecommendationScore());
        vo.setUserPreferenceMatching(row.getUserPreferenceMatching());
        vo.setPopularityIndex(row.getPopularityIndex());
        vo.setCollaborativeScore(row.getCollaborativeScore());
        vo.setContentScore(row.getContentScore());
        vo.setSeasonalScore(row.getSeasonalScore());
        vo.setQualityScore(row.getQualityScore());
        vo.setIsFeatured(row.getIsFeatured());
        vo.setFeatureWeight(row.getFeatureWeight());
        vo.setStatus(row.getStatus());
        vo.setUpdateTimestamp(row.getUpdateTimestamp());
        return vo;
    }

    /**
     * 人工干预：置顶/加权/上线下线。物品尚无推荐分时返回 404。
     */
    public AdminRecommendationItemVO featureItem(String categoryCode, Long itemId,
                                                  RecommendationFeatureParam param) {
        if (itemId == null) {
            throw new BusinessException(400, "物品 ID 不能为空");
        }
        RecommendCategory category = RecommendCategory.fromCode(categoryCode);
        validateFeatureParam(param);

        BigDecimal weight = param.getFeatureWeight();
        BaseRecommendationItem updated = registry.updateManualFields(
                category, itemId, param.getFeatured(), weight, param.getStatus());
        if (updated == null) {
            throw new BusinessException(404, "推荐物品不存在，请先执行重算：" + categoryCode + "/" + itemId);
        }
        return toAdminItemVO(updated,
                catalog.load(category, List.of(itemId)).get(itemId));
    }

    private void validateFeatureParam(RecommendationFeatureParam param) {
        if (param == null) {
            throw new BusinessException(400, "干预参数不能为空");
        }
        if (param.getStatus() != null && param.getStatus() != 0 && param.getStatus() != 1) {
            throw new BusinessException(400, "状态仅支持 0（下线）或 1（在架）");
        }
        if (param.getFeatureWeight() != null) {
            double w = param.getFeatureWeight().doubleValue();
            if (w < 0.0 || w > 1.0) {
                throw new BusinessException(400, "加权系数必须在 [0,1] 区间");
            }
        }
        if (param.getFeatured() == null && param.getFeatureWeight() == null && param.getStatus() == null) {
            throw new BusinessException(400, "至少提供一项干预字段（featured/featureWeight/status）");
        }
    }

    // ---------------- 重算与任务 ----------------

    public RecalcResult triggerRecalc(String categoryCode, Long operatorId) {
        RecommendCategory category = categoryCode == null || categoryCode.isBlank()
                ? null : RecommendCategory.fromCode(categoryCode.trim());
        return recalcService.recalculate(category, JobTriggerType.MANUAL, operatorId);
    }

    /**
     * 最近任务日志：category 为空时看全部（含 ALL 汇总），默认 20 条。
     */
    public List<RecommendationJobLog> listJobs(String categoryCode, Integer limit) {
        int safeLimit = limit == null || limit <= 0 ? 20 : Math.min(limit, MAX_JOB_LIMIT);
        QueryWrapper<RecommendationJobLog> wrapper = new QueryWrapper<RecommendationJobLog>()
                .orderByDesc("start_time")
                .last("LIMIT " + safeLimit);
        if (categoryCode != null && !categoryCode.isBlank()) {
            wrapper.eq("category", categoryCode.trim());
        }
        return jobLogMapper.selectList(wrapper);
    }

    // ---------------- 分析 / 调度 ----------------

    public RecommendationAnalyticsVO analytics(String categoryCode, Integer windowDays) {
        return analyticsService.dashboard(categoryCode, windowDays);
    }

    public RecommendationScheduleStatusVO scheduleStatus() {
        RecommendationScheduleStatusVO vo = new RecommendationScheduleStatusVO();
        vo.setScheduled(scheduleManager.isScheduled());
        vo.setCron(scheduleManager.getActiveCron());
        vo.setNextRunTime(scheduleManager.nextRunTime());
        return vo;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
