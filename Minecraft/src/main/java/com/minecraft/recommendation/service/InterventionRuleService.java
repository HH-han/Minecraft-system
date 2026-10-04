package com.minecraft.recommendation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraft.entity.recommendation.InterventionRule;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.InterventionRuleMapper;
import com.minecraft.recommendation.dto.InterventionRuleParam;
import com.minecraft.recommendation.enums.InterventionActionType;
import com.minecraft.recommendation.enums.InterventionRuleType;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.AdminInterventionRuleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 业务级人工干预规则服务：管理端 CRUD + 客户端规则引擎。
 * <p>
 * 生效判定：status=1、scope_category 命中分类（或 ALL）、当前时间落在
 * [effective_start, effective_end] 区间（端点可空）。
 * <p>
 * 条件（condition_json）支持的形态：
 * <ul>
 *   <li>排名：{"metric":"recommendation_score","operator":"TOP_N","value":5}（metric 支持
 *       recommendation_score/score/price/rating）；</li>
 *   <li>数值比较：{"field":"price","operator":"LTE|GTE|LT|GT|EQ","value":200}
 *       （field 支持 price/rating/score）；</li>
 *   <li>规则级时间窗：{"operator":"BETWEEN","start":"2026-10-01 00:00:00","end":"..."}
 *       （无 field/metric 时表示规则整体在此窗口内才生效）；</li>
 *   <li>空对象 {}：命中全部物品（用于 SORT 等全局动作）。</li>
 * </ul>
 * 动作（action_type + action_params）：
 * FILTER 仅保留命中 / SORT 全列表排序 {"orderBy":"score|price|rating","order":"DESC"}
 * / HIDE 剔除命中 / PIN 命中置顶 / BOOST 命中加权后重排 {"weight":0.8}。
 * 单条规则执行异常时跳过并告警，不影响其余规则与推荐接口可用性。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterventionRuleService {

    private static final String SCOPE_ALL = "ALL";
    private static final DateTimeFormatter FLEX_DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd['T'][' ']HH:mm[:ss]");

    private final InterventionRuleMapper ruleMapper;
    private final ObjectMapper objectMapper;

    // ---------------- 规则引擎输入/输出模型 ----------------

    /**
     * 参与规则计算的候选物品快照（score 已含物品级 BOOST 加成）。
     */
    public record Candidate(long itemId, double score, BigDecimal price, Integer rating) {
    }

    /**
     * 规则应用结果：最终排序的物品 ID + 被业务规则置顶的物品集合。
     */
    public record AppliedOrder(List<Long> orderedIds, Set<Long> pinnedIds) {
    }

    // ---------------- 管理端查询 ----------------

    /**
     * 管理端规则列表（scopeCategory/status/ruleType 均为可选过滤）。
     */
    public List<AdminInterventionRuleVO> listForAdmin(String scopeCategory, Integer status, String ruleType) {
        LambdaQueryWrapper<InterventionRule> wrapper = new LambdaQueryWrapper<InterventionRule>()
                .orderByAsc(InterventionRule::getPriority)
                .orderByDesc(InterventionRule::getUpdatedAt);
        if (scopeCategory != null && !scopeCategory.isBlank()) {
            wrapper.eq(InterventionRule::getScopeCategory, normalizeScope(scopeCategory));
        }
        if (status != null) {
            wrapper.eq(InterventionRule::getStatus, status);
        }
        if (ruleType != null && !ruleType.isBlank()) {
            wrapper.eq(InterventionRule::getRuleType,
                    InterventionRuleType.fromCode(ruleType).name());
        }
        List<InterventionRule> rules = ruleMapper.selectList(wrapper);
        List<AdminInterventionRuleVO> result = new ArrayList<>(rules.size());
        for (InterventionRule rule : rules) {
            result.add(toVO(rule));
        }
        return result;
    }

    // ---------------- 管理端写入 ----------------

    @Transactional(rollbackFor = Exception.class)
    public AdminInterventionRuleVO create(InterventionRuleParam param, Long operatorId) {
        validate(param, true);
        String ruleCode = param.getRuleCode().trim();
        Long duplicated = ruleMapper.selectCount(
                new LambdaQueryWrapper<InterventionRule>()
                        .eq(InterventionRule::getRuleCode, ruleCode));
        if (duplicated != null && duplicated > 0) {
            throw new BusinessException(400, "规则编码已存在：" + ruleCode);
        }
        InterventionRule rule = new InterventionRule();
        applyParam(rule, param);
        rule.setCreatedBy(operatorId);
        ruleMapper.insert(rule);
        log.info("创建业务干预规则 id={} code={} type={} action={} operator={}",
                rule.getId(), rule.getRuleCode(), rule.getRuleType(), rule.getActionType(), operatorId);
        return toVO(rule);
    }

    @Transactional(rollbackFor = Exception.class)
    public AdminInterventionRuleVO update(Long id, InterventionRuleParam param, Long operatorId) {
        if (id == null) {
            throw new BusinessException(400, "规则 ID 不能为空");
        }
        InterventionRule rule = ruleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(404, "干预规则不存在：" + id);
        }
        validate(param, false);
        if (param.getRuleCode() != null && !param.getRuleCode().isBlank()) {
            String code = param.getRuleCode().trim();
            if (!code.equalsIgnoreCase(rule.getRuleCode())) {
                Long count = ruleMapper.selectCount(
                        new LambdaQueryWrapper<InterventionRule>()
                                .eq(InterventionRule::getRuleCode, code)
                                .ne(InterventionRule::getId, id));
                if (count != null && count > 0) {
                    throw new BusinessException(400, "规则编码已存在：" + code);
                }
            }
        }
        applyParam(rule, param);
        ruleMapper.updateById(rule);
        log.info("更新业务干预规则 id={} code={} operator={}", id, rule.getRuleCode(), operatorId);
        return toVO(rule);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(400, "规则 ID 不能为空");
        }
        InterventionRule rule = ruleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(404, "干预规则不存在：" + id);
        }
        ruleMapper.deleteById(id);
        log.info("删除业务干预规则 id={} code={}", id, rule.getRuleCode());
    }

    // ---------------- 客户端规则引擎 ----------------

    /**
     * 查询某分类当前时刻生效的业务规则（启用 + 范围命中 + 时间窗口命中，按优先级升序）。
     */
    public List<InterventionRule> activeRules(String categoryCode, LocalDateTime now) {
        LocalDateTime moment = now == null ? LocalDateTime.now() : now;
        List<InterventionRule> rules = ruleMapper.selectList(
                new LambdaQueryWrapper<InterventionRule>()
                        .in(InterventionRule::getScopeCategory, categoryCode, SCOPE_ALL)
                        .eq(InterventionRule::getStatus, 1)
                        .and(w -> w.isNull(InterventionRule::getEffectiveStart)
                                .or().le(InterventionRule::getEffectiveStart, moment))
                        .and(w -> w.isNull(InterventionRule::getEffectiveEnd)
                                .or().ge(InterventionRule::getEffectiveEnd, moment))
                        .orderByAsc(InterventionRule::getPriority)
                        .orderByAsc(InterventionRule::getId));
        // condition 内嵌时间窗过滤
        List<InterventionRule> applicable = new ArrayList<>(rules.size());
        for (InterventionRule rule : rules) {
            if (ruleLevelWindowHit(rule, moment)) {
                applicable.add(rule);
            }
        }
        return applicable;
    }

    /**
     * 对候选列表依次应用生效规则，返回最终排序。
     */
    public AppliedOrder apply(String categoryCode, List<Candidate> candidates, LocalDateTime now) {
        List<Long> ordered = candidates.stream().map(Candidate::itemId).toList();
        if (candidates.isEmpty()) {
            return new AppliedOrder(ordered, Set.of());
        }
        List<InterventionRule> rules = activeRules(categoryCode, now);
        if (rules.isEmpty()) {
            return new AppliedOrder(ordered, Set.of());
        }
        List<Candidate> items = new ArrayList<>(candidates);
        Set<Long> pinned = new LinkedHashSet<>();
        for (InterventionRule rule : rules) {
            try {
                items = applyRule(rule, items, pinned);
            } catch (Exception e) {
                log.warn("业务干预规则执行失败，已跳过 rule={} err={}", rule.getRuleCode(), e.getMessage());
            }
        }
        return new AppliedOrder(items.stream().map(Candidate::itemId).toList(), pinned);
    }

    private List<Candidate> applyRule(InterventionRule rule, List<Candidate> items, Set<Long> pinned) {
        JsonNode condition = parseObject(rule.getConditionJson());
        JsonNode params = parseObjectOrNull(rule.getActionParams());
        InterventionActionType action = InterventionActionType.fromCode(rule.getActionType());
        List<Candidate> matched = matchItems(condition, items);
        switch (action) {
            case FILTER -> {
                return matched;
            }
            case HIDE -> {
                Set<Long> hideIds = idsOf(matched);
                List<Candidate> kept = new ArrayList<>(items.size());
                for (Candidate c : items) {
                    if (!hideIds.contains(c.itemId())) {
                        kept.add(c);
                    }
                }
                return kept;
            }
            case PIN -> {
                Set<Long> pinIds = idsOf(matched);
                List<Candidate> front = new ArrayList<>(matched.size());
                List<Candidate> rest = new ArrayList<>(items.size());
                for (Candidate c : items) {
                    if (pinIds.contains(c.itemId())) {
                        front.add(c);
                    } else {
                        rest.add(c);
                    }
                }
                front.addAll(rest);
                pinned.addAll(pinIds);
                return front;
            }
            case BOOST -> {
                double weight = params == null ? 0.0 : params.path("weight").asDouble(0.0);
                if (weight <= 0) {
                    return items;
                }
                Set<Long> boostIds = idsOf(matched);
                List<Candidate> adjusted = new ArrayList<>(items.size());
                for (Candidate c : items) {
                    double s = boostIds.contains(c.itemId()) ? boostScore(c.score(), weight) : c.score();
                    adjusted.add(new Candidate(c.itemId(), s, c.price(), c.rating()));
                }
                adjusted.sort(Comparator.comparingDouble(Candidate::score).reversed()
                        .thenComparingLong(Candidate::itemId));
                return adjusted;
            }
            case SORT -> {
                String orderBy = params == null ? "score" : params.path("orderBy").asText("score");
                boolean desc = params == null || !"ASC".equalsIgnoreCase(params.path("order").asText("DESC"));
                Comparator<Candidate> comparator = comparatorBy(orderBy);
                List<Candidate> sorted = new ArrayList<>(items);
                sorted.sort(desc ? comparator.reversed() : comparator);
                return sorted;
            }
            default -> {
                return items;
            }
        }
    }

    private List<Candidate> matchItems(JsonNode condition, List<Candidate> items) {
        if (condition == null || condition.isEmpty()) {
            return new ArrayList<>(items);
        }
        // 排名条件：metric + TOP_N
        if (condition.hasNonNull("metric")
                && "TOP_N".equalsIgnoreCase(condition.path("operator").asText())) {
            int n = Math.max(0, condition.path("value").asInt(0));
            if (n == 0) {
                return List.of();
            }
            String metric = condition.get("metric").asText();
            List<Candidate> sorted = new ArrayList<>(items);
            sorted.sort(comparatorBy(metric).reversed());
            return sorted.subList(0, Math.min(n, sorted.size())).stream().toList();
        }
        // 数值比较条件：field + operator + value
        if (condition.hasNonNull("field")) {
            String field = condition.get("field").asText();
            String operator = condition.path("operator").asText();
            double value = condition.path("value").asDouble();
            List<Candidate> matched = new ArrayList<>();
            for (Candidate c : items) {
                Double target = metricValue(c, field);
                if (target != null && compare(target, operator, value)) {
                    matched.add(c);
                }
            }
            return matched;
        }
        return new ArrayList<>(items);
    }

    private Double metricValue(Candidate c, String metric) {
        return switch (metric.toLowerCase()) {
            case "recommendation_score", "score" -> c.score();
            case "price" -> c.price() == null ? null : c.price().doubleValue();
            case "rating" -> c.rating() == null ? null : c.rating().doubleValue();
            default -> null;
        };
    }

    private Comparator<Candidate> comparatorBy(String metric) {
        return switch (metric.toLowerCase()) {
            case "price" -> Comparator.comparing(Candidate::price,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "rating" -> Comparator.comparing(Candidate::rating,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparingDouble(Candidate::score);
        };
    }

    private boolean compare(double target, String operator, double value) {
        return switch (operator.toUpperCase()) {
            case "LTE", "LE", "<=" -> target <= value;
            case "GTE", "GE", ">=" -> target >= value;
            case "LT", "<" -> target < value;
            case "GT", ">" -> target > value;
            case "EQ", "=" -> target == value;
            default -> false;
        };
    }

    /** BOOST 与物品级规则同公式：score + w*(1-score)，夹在 [0,1]。 */
    private double boostScore(double score, double weight) {
        double s = Math.max(0.0, Math.min(1.0, score));
        return Math.max(0.0, Math.min(1.0, s + weight * (1.0 - s)));
    }

    private Set<Long> idsOf(List<Candidate> items) {
        Set<Long> ids = new HashSet<>();
        for (Candidate c : items) {
            ids.add(c.itemId());
        }
        return ids;
    }

    /**
     * condition 中无 field/metric 的 BETWEEN 视为规则级时间窗（columns 之外补充窗口）。
     */
    private boolean ruleLevelWindowHit(InterventionRule rule, LocalDateTime now) {
        JsonNode condition;
        try {
            condition = parseObject(rule.getConditionJson());
        } catch (Exception e) {
            return true;
        }
        if (condition == null || condition.isEmpty()
                || condition.hasNonNull("field") || condition.hasNonNull("metric")) {
            return true;
        }
        if (!"BETWEEN".equalsIgnoreCase(condition.path("operator").asText())) {
            return true;
        }
        LocalDateTime start = parseFlexibleDateTime(condition.path("start").asText(null));
        LocalDateTime end = parseFlexibleDateTime(condition.path("end").asText(null));
        if (start != null && now.isBefore(start)) {
            return false;
        }
        return end == null || !now.isAfter(end);
    }

    private LocalDateTime parseFlexibleDateTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text.trim(), FLEX_DATETIME);
        } catch (Exception e) {
            log.warn("干预规则时间格式无法解析：{}", text);
            return null;
        }
    }

    // ---------------- 参数校验与组装 ----------------

    private void validate(InterventionRuleParam param, boolean creating) {
        if (param == null) {
            throw new BusinessException(400, "规则参数不能为空");
        }
        if (creating) {
            if (param.getRuleCode() == null || param.getRuleCode().isBlank()) {
                throw new BusinessException(400, "规则编码不能为空");
            }
            if (!param.getRuleCode().trim().matches("^[A-Za-z0-9_-]{1,64}$")) {
                throw new BusinessException(400, "规则编码仅允许字母/数字/下划线/中划线，长度 1-64");
            }
            if (param.getRuleName() == null || param.getRuleName().isBlank()) {
                throw new BusinessException(400, "规则名称不能为空");
            }
        }
        if (param.getRuleName() != null && param.getRuleName().length() > 100) {
            throw new BusinessException(400, "规则名称长度不能超过 100");
        }
        if (param.getDescription() != null && param.getDescription().length() > 500) {
            throw new BusinessException(400, "规则描述长度不能超过 500");
        }
        if (param.getRuleType() != null) {
            InterventionRuleType.fromCode(param.getRuleType());
        }
        if (param.getActionType() != null) {
            InterventionActionType.fromCode(param.getActionType());
        }
        if (param.getScopeCategory() != null && !param.getScopeCategory().isBlank()) {
            normalizeScope(param.getScopeCategory());
        }
        if (param.getConditionJson() != null && !param.getConditionJson().isBlank()) {
            requireJsonObject(param.getConditionJson(), "干预条件");
        }
        if (param.getActionParams() != null && !param.getActionParams().isBlank()) {
            requireJsonObject(param.getActionParams(), "动作参数");
        }
        if (param.getPriority() != null && param.getPriority() < 0) {
            throw new BusinessException(400, "执行优先级不能为负数");
        }
        if (param.getStatus() != null && param.getStatus() != 0 && param.getStatus() != 1) {
            throw new BusinessException(400, "状态仅支持 0（停用）或 1（启用）");
        }
        if (param.getEffectiveStart() != null && param.getEffectiveEnd() != null
                && param.getEffectiveEnd().isBefore(param.getEffectiveStart())) {
            throw new BusinessException(400, "生效结束时间不能早于开始时间");
        }
    }

    private void requireJsonObject(String json, String label) {
        try {
            JsonNode node = objectMapper.readTree(json);
            if (!node.isObject()) {
                throw new BusinessException(400, label + "必须是 JSON 对象");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(400, label + "不是合法的 JSON：" + e.getMessage());
        }
    }

    private String normalizeScope(String scope) {
        String s = scope.trim();
        if (SCOPE_ALL.equalsIgnoreCase(s)) {
            return SCOPE_ALL;
        }
        return RecommendCategory.fromCode(s).code();
    }

    private void applyParam(InterventionRule rule, InterventionRuleParam param) {
        if (param.getRuleCode() != null && !param.getRuleCode().isBlank()) {
            rule.setRuleCode(param.getRuleCode().trim());
        }
        if (param.getRuleName() != null && !param.getRuleName().isBlank()) {
            rule.setRuleName(param.getRuleName().trim());
        }
        if (param.getRuleType() != null) {
            rule.setRuleType(InterventionRuleType.fromCode(param.getRuleType()).name());
        }
        rule.setDescription(param.getDescription());
        if (param.getScopeCategory() != null && !param.getScopeCategory().isBlank()) {
            rule.setScopeCategory(normalizeScope(param.getScopeCategory()));
        }
        if (param.getConditionJson() != null && !param.getConditionJson().isBlank()) {
            rule.setConditionJson(param.getConditionJson().trim());
        }
        if (param.getActionType() != null) {
            rule.setActionType(InterventionActionType.fromCode(param.getActionType()).name());
        }
        rule.setActionParams(param.getActionParams() == null || param.getActionParams().isBlank()
                ? null : param.getActionParams().trim());
        rule.setPriority(param.getPriority() == null ? 0 : param.getPriority());
        if (param.getStatus() != null) {
            rule.setStatus(param.getStatus());
        } else if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        rule.setEffectiveStart(param.getEffectiveStart());
        rule.setEffectiveEnd(param.getEffectiveEnd());
        rule.setRemark(param.getRemark());
    }

    private AdminInterventionRuleVO toVO(InterventionRule rule) {
        AdminInterventionRuleVO vo = new AdminInterventionRuleVO();
        vo.setId(rule.getId());
        vo.setRuleCode(rule.getRuleCode());
        vo.setRuleName(rule.getRuleName());
        vo.setRuleType(rule.getRuleType());
        vo.setDescription(rule.getDescription());
        vo.setScopeCategory(rule.getScopeCategory());
        vo.setConditionJson(rule.getConditionJson());
        vo.setActionType(rule.getActionType());
        vo.setActionParams(rule.getActionParams());
        vo.setPriority(rule.getPriority());
        vo.setStatus(rule.getStatus());
        vo.setEffectiveStart(rule.getEffectiveStart());
        vo.setEffectiveEnd(rule.getEffectiveEnd());
        vo.setCreatedBy(rule.getCreatedBy());
        vo.setRemark(rule.getRemark());
        vo.setCreatedAt(rule.getCreatedAt());
        vo.setUpdatedAt(rule.getUpdatedAt());
        return vo;
    }

    private JsonNode parseObject(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.isObject() ? node : null;
        } catch (Exception e) {
            throw new BusinessException(400, "JSON 解析失败：" + e.getMessage());
        }
    }

    private JsonNode parseObjectOrNull(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.isObject() ? node : null;
        } catch (Exception e) {
            return null;
        }
    }
}
