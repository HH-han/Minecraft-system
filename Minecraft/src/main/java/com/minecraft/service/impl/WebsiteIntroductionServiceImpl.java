package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.*;
import com.minecraft.mapper.*;
import com.minecraft.service.WebsiteIntroductionService;
import com.minecraft.utils.ImageUtils;
import com.minecraft.vo.FeatureVO;
import com.minecraft.vo.MilestoneVO;
import com.minecraft.vo.TechCategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 网站介绍页聚合服务实现。
 * <p>
 * 图片处理逻辑参照 FoodServiceImpl：对 features.icon 与 platform_stats.stat_icon 字段，
 * 当传入 Base64（data:image 开头）时调用 ImageUtils 落盘并替换为可访问 URL。
 */
@Service
public class WebsiteIntroductionServiceImpl extends ServiceImpl<FeatureMapper, Feature> implements WebsiteIntroductionService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM");

    @Autowired
    private FeatureItemMapper featureItemMapper;

    @Autowired
    private TechCategoryMapper techCategoryMapper;

    @Autowired
    private TechItemMapper techItemMapper;

    @Autowired
    private MilestoneMapper milestoneMapper;

    @Autowired
    private PlatformStatMapper platformStatMapper;

    @Autowired
    private ImageUtils imageUtils;

    // ==================== 核心功能 (features) ====================

    @Override
    public List<FeatureVO> getFeatureList() {
        // 查询全部功能，按 sort_order 升序
        LambdaQueryWrapper<Feature> featureWrapper = new LambdaQueryWrapper<>();
        featureWrapper.orderByAsc(Feature::getSortOrder);
        List<Feature> features = baseMapper.selectList(featureWrapper);

        if (features.isEmpty()) {
            return Collections.emptyList();
        }

        // 一次性查询全部子项，按 feature_id 分组，避免 N+1 查询
        List<Integer> featureIds = features.stream().map(Feature::getId).collect(Collectors.toList());
        LambdaQueryWrapper<FeatureItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(FeatureItem::getFeatureId, featureIds)
                .orderByAsc(FeatureItem::getSortOrder);
        List<FeatureItem> allItems = featureItemMapper.selectList(itemWrapper);

        Map<Integer, List<FeatureItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(FeatureItem::getFeatureId));

        // 组装 VO
        return features.stream().map(feature -> {
            FeatureVO vo = new FeatureVO();
            BeanUtils.copyProperties(feature, vo);
            List<FeatureItem> items = itemMap.getOrDefault(feature.getId(), Collections.emptyList());
            vo.setItems(items.stream().map(this::toFeatureItemVO).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Feature getFeatureDetail(Integer id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public void saveFeature(Feature feature) {
        // 处理图标：Base64 落盘为可访问 URL
        if (feature.getIcon() != null && feature.getIcon().startsWith("data:image")) {
            feature.setIcon(imageUtils.processBase64ImageSafe(feature.getIcon()));
        }
        if (feature.getId() != null) {
            baseMapper.updateById(feature);
        } else {
            baseMapper.insert(feature);
        }
    }

    @Override
    @Transactional
    public void deleteFeature(Integer id) {
        // feature_items 设置了 ON DELETE CASCADE，但显式清理可保证非级联环境下数据一致
        LambdaQueryWrapper<FeatureItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(FeatureItem::getFeatureId, id);
        featureItemMapper.delete(itemWrapper);
        baseMapper.deleteById(id);
    }

    // ==================== 功能子项 (feature_items) ====================

    @Override
    public List<FeatureItem> getFeatureItemsByFeatureId(Integer featureId) {
        LambdaQueryWrapper<FeatureItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeatureItem::getFeatureId, featureId)
                .orderByAsc(FeatureItem::getSortOrder);
        return featureItemMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveFeatureItem(FeatureItem featureItem) {
        if (featureItem.getId() != null) {
            featureItemMapper.updateById(featureItem);
        } else {
            featureItemMapper.insert(featureItem);
        }
    }

    @Override
    @Transactional
    public void deleteFeatureItem(Integer id) {
        featureItemMapper.deleteById(id);
    }

    // ==================== 技术分类 (tech_categories) ====================

    @Override
    public List<TechCategoryVO> getTechCategoryList() {
        LambdaQueryWrapper<TechCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.orderByAsc(TechCategory::getSortOrder);
        List<TechCategory> categories = techCategoryMapper.selectList(categoryWrapper);

        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        // 一次性查询全部技术项，按 category_id 分组，避免 N+1 查询
        List<Integer> categoryIds = categories.stream().map(TechCategory::getId).collect(Collectors.toList());
        LambdaQueryWrapper<TechItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(TechItem::getCategoryId, categoryIds)
                .orderByAsc(TechItem::getSortOrder);
        List<TechItem> allItems = techItemMapper.selectList(itemWrapper);

        Map<Integer, List<TechItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(TechItem::getCategoryId));

        return categories.stream().map(category -> {
            TechCategoryVO vo = new TechCategoryVO();
            BeanUtils.copyProperties(category, vo);
            List<TechItem> items = itemMap.getOrDefault(category.getId(), Collections.emptyList());
            vo.setItems(items.stream().map(this::toTechItemVO).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveTechCategory(TechCategory techCategory) {
        if (techCategory.getId() != null) {
            techCategoryMapper.updateById(techCategory);
        } else {
            techCategoryMapper.insert(techCategory);
        }
    }

    @Override
    @Transactional
    public void deleteTechCategory(Integer id) {
        LambdaQueryWrapper<TechItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(TechItem::getCategoryId, id);
        techItemMapper.delete(itemWrapper);
        techCategoryMapper.deleteById(id);
    }

    // ==================== 技术项 (tech_items) ====================

    @Override
    public List<TechItem> getTechItemsByCategoryId(Integer categoryId) {
        LambdaQueryWrapper<TechItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TechItem::getCategoryId, categoryId)
                .orderByAsc(TechItem::getSortOrder);
        return techItemMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveTechItem(TechItem techItem) {
        if (techItem.getId() != null) {
            techItemMapper.updateById(techItem);
        } else {
            techItemMapper.insert(techItem);
        }
    }

    @Override
    @Transactional
    public void deleteTechItem(Integer id) {
        techItemMapper.deleteById(id);
    }

    // ==================== 发展历程 (milestones) ====================

    @Override
    public List<Milestone> getMilestoneList() {
        LambdaQueryWrapper<Milestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Milestone::getMilestoneDate);
        return milestoneMapper.selectList(wrapper);
    }

    @Override
    public List<MilestoneVO> getMilestoneListFormatted() {
        // 对应 SQL: DATE_FORMAT(milestone_date, '%Y.%m') AS date_formatted ... ORDER BY milestone_date
        return getMilestoneList().stream().map(milestone -> {
            MilestoneVO vo = new MilestoneVO();
            vo.setId(milestone.getId());
            vo.setMilestoneDate(milestone.getMilestoneDate());
            vo.setDateFormatted(milestone.getMilestoneDate().format(MONTH_FORMATTER));
            vo.setTitle(milestone.getTitle());
            vo.setDescription(milestone.getDescription());
            vo.setSortOrder(milestone.getSortOrder());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveMilestone(Milestone milestone) {
        if (milestone.getId() != null) {
            milestoneMapper.updateById(milestone);
        } else {
            milestoneMapper.insert(milestone);
        }
    }

    @Override
    @Transactional
    public void deleteMilestone(Integer id) {
        milestoneMapper.deleteById(id);
    }

    // ==================== 平台数据统计 (platform_stats) ====================

    @Override
    public List<PlatformStat> getPlatformStatList() {
        LambdaQueryWrapper<PlatformStat> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PlatformStat::getSortOrder);
        return platformStatMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void savePlatformStat(PlatformStat platformStat) {
        // 处理统计图标：Base64 落盘为可访问 URL
        if (platformStat.getStatIcon() != null && platformStat.getStatIcon().startsWith("data:image")) {
            platformStat.setStatIcon(imageUtils.processBase64ImageSafe(platformStat.getStatIcon()));
        }
        if (platformStat.getId() != null) {
            platformStatMapper.updateById(platformStat);
        } else {
            platformStatMapper.insert(platformStat);
        }
    }

    @Override
    @Transactional
    public void deletePlatformStat(Integer id) {
        platformStatMapper.deleteById(id);
    }

    // ==================== 聚合数据 ====================

    @Override
    public Map<String, Object> getIntroductionData() {
        Map<String, Object> data = new HashMap<>();
        data.put("features", getFeatureList());
        data.put("techStack", getTechCategoryList());
        data.put("milestones", getMilestoneListFormatted());
        data.put("stats", getPlatformStatList());
        return data;
    }

    // ==================== 私有转换方法 ====================

    private FeatureVO.FeatureItem toFeatureItemVO(FeatureItem item) {
        FeatureVO.FeatureItem vo = new FeatureVO.FeatureItem();
        vo.setId(item.getId());
        vo.setFeatureId(item.getFeatureId());
        vo.setItemName(item.getItemName());
        vo.setSortOrder(item.getSortOrder());
        return vo;
    }

    private TechCategoryVO.TechItem toTechItemVO(TechItem item) {
        TechCategoryVO.TechItem vo = new TechCategoryVO.TechItem();
        vo.setId(item.getId());
        vo.setCategoryId(item.getCategoryId());
        vo.setTechName(item.getTechName());
        vo.setDescription(item.getDescription());
        vo.setSortOrder(item.getSortOrder());
        return vo;
    }
}
