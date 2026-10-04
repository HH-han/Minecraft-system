package com.minecraft.recommendation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.entity.recommendation.RecommendationRule;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationRuleMapper;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.dto.RecommendationRuleParam;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.enums.RecommendationRuleType;
import com.minecraft.recommendation.vo.AdminRecommendationRuleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 推荐人工干预规则服务：管理端 CRUD + 客户端生效规则解析。
 * <p>
 * 生效判定：status=1 且当前时间落在 [startTime, endTime] 区间（端点可空）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRuleService {

    private static final BigDecimal MAX_WEIGHT = BigDecimal.ONE;

    private final RecommendationRuleMapper ruleMapper;
    private final RecommendationItemCatalog catalog;

    // ---------------- 管理端查询 ----------------

    /**
     * 管理端分页前的全量列表（规则量级小，按分类返回；status 为空时返回启用+停用）。
     */
    public List<AdminRecommendationRuleVO> listForAdmin(String categoryCode, Integer status) {
        RecommendCategory category = RecommendCategory.fromCode(categoryCode);
        LambdaQueryWrapper<RecommendationRule> wrapper =
                new LambdaQueryWrapper<RecommendationRule>()
                        .eq(RecommendationRule::getCategory, category.code())
                        .orderByAsc(RecommendationRule::getRuleType)
                        .orderByAsc(RecommendationRule::getSortOrder)
                        .orderByDesc(RecommendationRule::getUpdatedAt);
        if (status != null) {
            wrapper.eq(RecommendationRule::getStatus, status);
        }
        List<RecommendationRule> rules = ruleMapper.selectList(wrapper);
        Map<Long, ItemDetail> details = catalog.load(category,
                rules.stream().map(RecommendationRule::getItemId).toList());
        List<AdminRecommendationRuleVO> result = new ArrayList<>(rules.size());
        for (RecommendationRule rule : rules) {
            result.add(toVO(rule, details.get(rule.getItemId())));
        }
        return result;
    }

    // ---------------- 管理端写入 ----------------

    @Transactional(rollbackFor = Exception.class)
    public AdminRecommendationRuleVO create(RecommendationRuleParam param, Long operatorId) {
        validate(param, true);
        RecommendCategory category = RecommendCategory.fromCode(param.getCategory());
        if (!catalog.exists(category, param.getItemId())) {
            throw new BusinessException(404, "干预物品不存在：" + category.code() + "/" + param.getItemId());
        }
        Long duplicated = ruleMapper.selectCount(
                new LambdaQueryWrapper<RecommendationRule>()
                        .eq(RecommendationRule::getCategory, category.code())
                        .eq(RecommendationRule::getItemId, param.getItemId()));
        if (duplicated != null && duplicated > 0) {
            throw new BusinessException(400, "该物品已存在干预规则，同一分类下每个物品仅允许一条规则");
        }

        RecommendationRule rule = new RecommendationRule();
        applyParam(rule, param);
        rule.setOperatorId(operatorId);
        ruleMapper.insert(rule);
        log.info("创建推荐规则 id={} category={} itemId={} type={} operator={}",
                rule.getId(), category.code(), rule.getItemId(), rule.getRuleType(), operatorId);
        return toVO(rule, catalog.load(category, List.of(rule.getItemId())).get(rule.getItemId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public AdminRecommendationRuleVO update(Long id, RecommendationRuleParam param, Long operatorId) {
        if (id == null) {
            throw new BusinessException(400, "规则 ID 不能为空");
        }
        RecommendationRule rule = ruleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(404, "推荐规则不存在：" + id);
        }
        validate(param, false);
        RecommendCategory category = RecommendCategory.fromCode(rule.getCategory());

        // 不允许改绑到其他已存在规则的物品
        if (param.getItemId() != null && !param.getItemId().equals(rule.getItemId())) {
            if (!catalog.exists(category, param.getItemId())) {
                throw new BusinessException(404, "干预物品不存在：" + category.code() + "/" + param.getItemId());
            }
            Long count = ruleMapper.selectCount(
                    new LambdaQueryWrapper<RecommendationRule>()
                            .eq(RecommendationRule::getCategory, category.code())
                            .eq(RecommendationRule::getItemId, param.getItemId())
                            .ne(RecommendationRule::getId, id));
            if (count != null && count > 0) {
                throw new BusinessException(400, "目标物品已存在干预规则");
            }
        }
        applyParam(rule, param);
        rule.setOperatorId(operatorId);
        ruleMapper.updateById(rule);
        log.info("更新推荐规则 id={} category={} itemId={} type={} operator={}",
                id, category.code(), rule.getItemId(), rule.getRuleType(), operatorId);
        return toVO(rule, catalog.load(category, List.of(rule.getItemId())).get(rule.getItemId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(400, "规则 ID 不能为空");
        }
        RecommendationRule rule = ruleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(404, "推荐规则不存在：" + id);
        }
        ruleMapper.deleteById(id);
        log.info("删除推荐规则 id={} category={} itemId={}", id, rule.getCategory(), rule.getItemId());
    }

    // ---------------- 客户端生效规则 ----------------

    /**
     * 查询某分类当前时刻生效的全部规则（启用 + 时间窗口命中）。
     */
    public List<RecommendationRule> activeRules(RecommendCategory category, LocalDateTime now) {
        LocalDateTime moment = now == null ? LocalDateTime.now() : now;
        return ruleMapper.selectList(
                new LambdaQueryWrapper<RecommendationRule>()
                        .eq(RecommendationRule::getCategory, category.code())
                        .eq(RecommendationRule::getStatus, 1)
                        .and(w -> w.isNull(RecommendationRule::getStartTime)
                                .or().le(RecommendationRule::getStartTime, moment))
                        .and(w -> w.isNull(RecommendationRule::getEndTime)
                                .or().ge(RecommendationRule::getEndTime, moment))
                        .orderByAsc(RecommendationRule::getSortOrder));
    }

    // ---------------- 内部方法 ----------------

    private void validate(RecommendationRuleParam param, boolean creating) {
        if (param == null) {
            throw new BusinessException(400, "规则参数不能为空");
        }
        if (creating && (param.getCategory() == null || param.getCategory().isBlank())) {
            throw new BusinessException(400, "规则分类不能为空");
        }
        if (creating && param.getItemId() == null) {
            throw new BusinessException(400, "干预物品 ID 不能为空");
        }
        RecommendationRuleType type = RecommendationRuleType.fromCode(param.getRuleType());
        if (param.getSortOrder() != null && param.getSortOrder() < 0) {
            throw new BusinessException(400, "置顶位序不能为负数");
        }
        if (param.getBoostWeight() != null) {
            BigDecimal w = param.getBoostWeight();
            if (w.compareTo(BigDecimal.ZERO) < 0 || w.compareTo(MAX_WEIGHT) > 0) {
                throw new BusinessException(400, "加权系数必须在 [0,1] 区间");
            }
        }
        if (type == RecommendationRuleType.BOOST
                && (param.getBoostWeight() == null || param.getBoostWeight().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BusinessException(400, "加权规则必须提供大于 0 的加权系数");
        }
        if (param.getStatus() != null && param.getStatus() != 0 && param.getStatus() != 1) {
            throw new BusinessException(400, "状态仅支持 0（停用）或 1（启用）");
        }
        if (param.getStartTime() != null && param.getEndTime() != null
                && param.getEndTime().isBefore(param.getStartTime())) {
            throw new BusinessException(400, "生效结束时间不能早于开始时间");
        }
    }

    private void applyParam(RecommendationRule rule, RecommendationRuleParam param) {
        if (param.getCategory() != null && !param.getCategory().isBlank()) {
            rule.setCategory(RecommendCategory.fromCode(param.getCategory()).code());
        }
        if (param.getItemId() != null) {
            rule.setItemId(param.getItemId());
        }
        rule.setRuleType(RecommendationRuleType.fromCode(param.getRuleType()).name());
        rule.setSortOrder(param.getSortOrder() == null ? 0 : param.getSortOrder());
        rule.setBoostWeight(param.getBoostWeight() == null ? BigDecimal.ZERO : param.getBoostWeight());
        rule.setStartTime(param.getStartTime());
        rule.setEndTime(param.getEndTime());
        if (param.getStatus() != null) {
            rule.setStatus(param.getStatus());
        } else if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        rule.setRemark(param.getRemark());
    }

    private AdminRecommendationRuleVO toVO(RecommendationRule rule, ItemDetail detail) {
        AdminRecommendationRuleVO vo = new AdminRecommendationRuleVO();
        vo.setId(rule.getId());
        vo.setCategory(rule.getCategory());
        vo.setItemId(rule.getItemId());
        if (detail != null) {
            vo.setItemName(detail.getName());
            vo.setCity(detail.getCity());
            vo.setSubType(detail.getSubType());
        }
        vo.setRuleType(rule.getRuleType());
        vo.setSortOrder(rule.getSortOrder());
        vo.setBoostWeight(rule.getBoostWeight());
        vo.setStartTime(rule.getStartTime());
        vo.setEndTime(rule.getEndTime());
        vo.setStatus(rule.getStatus());
        vo.setRemark(rule.getRemark());
        vo.setOperatorId(rule.getOperatorId());
        vo.setCreatedAt(rule.getCreatedAt());
        vo.setUpdatedAt(rule.getUpdatedAt());
        return vo;
    }
}
