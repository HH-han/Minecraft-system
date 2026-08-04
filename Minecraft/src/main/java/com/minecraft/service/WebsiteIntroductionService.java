package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.*;
import com.minecraft.vo.FeatureVO;
import com.minecraft.vo.MilestoneVO;
import com.minecraft.vo.TechCategoryVO;

import java.util.List;
import java.util.Map;

/**
 * 网站介绍页聚合服务，覆盖 db_minecraft.sql 中的以下表：
 * features、feature_items、tech_categories、tech_items、milestones、platform_stats。
 * 参照 OfficialwebsiteService 的分组聚合风格。
 */
public interface WebsiteIntroductionService extends IService<Feature> {

    // ============ 核心功能 (features) ============
    List<FeatureVO> getFeatureList();

    Feature getFeatureDetail(Integer id);

    void saveFeature(Feature feature);

    void deleteFeature(Integer id);

    // ============ 功能子项 (feature_items) ============
    List<FeatureItem> getFeatureItemsByFeatureId(Integer featureId);

    void saveFeatureItem(FeatureItem featureItem);

    void deleteFeatureItem(Integer id);

    // ============ 技术分类 (tech_categories) ============
    List<TechCategoryVO> getTechCategoryList();

    void saveTechCategory(TechCategory techCategory);

    void deleteTechCategory(Integer id);

    // ============ 技术项 (tech_items) ============
    List<TechItem> getTechItemsByCategoryId(Integer categoryId);

    void saveTechItem(TechItem techItem);

    void deleteTechItem(Integer id);

    // ============ 发展历程 (milestones) ============
    List<Milestone> getMilestoneList();

    List<MilestoneVO> getMilestoneListFormatted();

    void saveMilestone(Milestone milestone);

    void deleteMilestone(Integer id);

    // ============ 平台数据统计 (platform_stats) ============
    List<PlatformStat> getPlatformStatList();

    void savePlatformStat(PlatformStat platformStat);

    void deletePlatformStat(Integer id);

    // ============ 聚合数据 ============
    /**
     * 一次性获取网站介绍页所需的全部数据（功能、技术栈、历程、统计）。
     */
    Map<String, Object> getIntroductionData();
}
