package com.minecraft.service;

import com.minecraft.entity.Feature;
import com.minecraft.entity.FeatureItem;
import com.minecraft.entity.Milestone;
import com.minecraft.entity.PlatformStat;
import com.minecraft.entity.TechCategory;
import com.minecraft.entity.TechItem;
import com.minecraft.mapper.FeatureItemMapper;
import com.minecraft.mapper.FeatureMapper;
import com.minecraft.mapper.MilestoneMapper;
import com.minecraft.mapper.PlatformStatMapper;
import com.minecraft.mapper.TechCategoryMapper;
import com.minecraft.mapper.TechItemMapper;
import com.minecraft.service.impl.WebsiteIntroductionServiceImpl;
import com.minecraft.utils.ImageUtils;
import com.minecraft.vo.FeatureVO;
import com.minecraft.vo.MilestoneVO;
import com.minecraft.vo.TechCategoryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WebsiteIntroductionServiceImpl 单元测试。
 * 重点验证：图片处理逻辑、JOIN 分组、日期格式化、聚合数据与 CRUD 委托。
 */
@ExtendWith(MockitoExtension.class)
class WebsiteIntroductionServiceImplTest {

    @Mock
    private FeatureMapper featureMapper;

    @Mock
    private FeatureItemMapper featureItemMapper;

    @Mock
    private TechCategoryMapper techCategoryMapper;

    @Mock
    private TechItemMapper techItemMapper;

    @Mock
    private MilestoneMapper milestoneMapper;

    @Mock
    private PlatformStatMapper platformStatMapper;

    @Mock
    private ImageUtils imageUtils;

    @InjectMocks
    private WebsiteIntroductionServiceImpl service;

    @BeforeEach
    void setUp() {
        // ServiceImpl 的 baseMapper 为父类 protected 字段，显式注入保证被赋值
        ReflectionTestUtils.setField(service, "baseMapper", featureMapper);
    }

    // ==================== 核心功能 (features) ====================

    @Test
    void getFeatureList_shouldGroupItemsByFeatureId() {
        Feature f1 = new Feature();
        f1.setId(1);
        f1.setTitle("资讯中心");
        f1.setSortOrder(1);
        Feature f2 = new Feature();
        f2.setId(2);
        f2.setTitle("预订服务");
        f2.setSortOrder(2);

        when(featureMapper.selectList(any())).thenReturn(Arrays.asList(f1, f2));

        FeatureItem i1 = new FeatureItem();
        i1.setId(1);
        i1.setFeatureId(1);
        i1.setItemName("热门景点推荐");
        i1.setSortOrder(1);
        FeatureItem i2 = new FeatureItem();
        i2.setId(2);
        i2.setFeatureId(1);
        i2.setItemName("季节性旅行指南");
        i2.setSortOrder(2);
        FeatureItem i3 = new FeatureItem();
        i3.setId(3);
        i3.setFeatureId(2);
        i3.setItemName("酒店比价预订");
        i3.setSortOrder(1);

        when(featureItemMapper.selectList(any())).thenReturn(Arrays.asList(i1, i2, i3));

        List<FeatureVO> result = service.getFeatureList();

        assertEquals(2, result.size());
        assertEquals("资讯中心", result.get(0).getTitle());
        assertEquals(2, result.get(0).getItems().size());
        assertEquals("热门景点推荐", result.get(0).getItems().get(0).getItemName());
        assertEquals(1, result.get(1).getItems().size());
        assertEquals("酒店比价预订", result.get(1).getItems().get(0).getItemName());
    }

    @Test
    void getFeatureList_shouldReturnEmptyWhenNoData() {
        when(featureMapper.selectList(any())).thenReturn(Collections.emptyList());
        List<FeatureVO> result = service.getFeatureList();
        assertTrue(result.isEmpty());
        verify(featureItemMapper, never()).selectList(any());
    }

    @Test
    void saveFeature_withBase64Icon_shouldProcessImageAndInsert() {
        Feature feature = new Feature();
        feature.setTitle("资讯中心");
        feature.setIcon("data:image/png;base64,iVBORw0KGgo=");

        when(imageUtils.processBase64ImageSafe("data:image/png;base64,iVBORw0KGgo="))
                .thenReturn("/image/abc.png");

        service.saveFeature(feature);

        assertEquals("/image/abc.png", feature.getIcon());
        verify(featureMapper).insert(feature);
        verify(featureMapper, never()).updateById(any());
    }

    @Test
    void saveFeature_withUrlIcon_shouldNotProcessImage() {
        Feature feature = new Feature();
        feature.setId(1);
        feature.setTitle("资讯中心");
        feature.setIcon("/icons/map.svg");

        service.saveFeature(feature);

        assertEquals("/icons/map.svg", feature.getIcon());
        verify(imageUtils, never()).processBase64ImageSafe(any());
        verify(featureMapper).updateById(feature);
        verify(featureMapper, never()).insert(any());
    }

    @Test
    void deleteFeature_shouldCascadeDeleteItems() {
        service.deleteFeature(1);
        verify(featureItemMapper).delete(any());
        verify(featureMapper).deleteById(1);
    }

    // ==================== 技术分类 (tech_categories) ====================

    @Test
    void getTechCategoryList_shouldGroupItemsByCategoryId() {
        TechCategory c1 = new TechCategory();
        c1.setId(1);
        c1.setCategoryName("后端");
        c1.setSortOrder(1);
        TechCategory c2 = new TechCategory();
        c2.setId(2);
        c2.setCategoryName("前端");
        c2.setSortOrder(2);

        when(techCategoryMapper.selectList(any())).thenReturn(Arrays.asList(c1, c2));

        TechItem t1 = new TechItem();
        t1.setId(1);
        t1.setCategoryId(1);
        t1.setTechName("Spring Boot");
        TechItem t2 = new TechItem();
        t2.setId(2);
        t2.setCategoryId(2);
        t2.setTechName("Vue 3");

        when(techItemMapper.selectList(any())).thenReturn(Arrays.asList(t1, t2));

        List<TechCategoryVO> result = service.getTechCategoryList();

        assertEquals(2, result.size());
        assertEquals("后端", result.get(0).getCategoryName());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals("Spring Boot", result.get(0).getItems().get(0).getTechName());
        assertEquals("Vue 3", result.get(1).getItems().get(0).getTechName());
    }

    @Test
    void deleteTechCategory_shouldCascadeDeleteItems() {
        service.deleteTechCategory(1);
        verify(techItemMapper).delete(any());
        verify(techCategoryMapper).deleteById(1);
    }

    // ==================== 发展历程 (milestones) ====================

    @Test
    void getMilestoneListFormatted_shouldFormatDateAsYearMonth() {
        Milestone m1 = new Milestone();
        m1.setId(1);
        m1.setMilestoneDate(LocalDate.of(2024, 1, 1));
        m1.setTitle("项目启动");
        m1.setDescription("正式立项");
        Milestone m2 = new Milestone();
        m2.setId(2);
        m2.setMilestoneDate(LocalDate.of(2026, 7, 1));
        m2.setTitle("持续进化中");
        m2.setDescription("打磨细节");

        when(milestoneMapper.selectList(any())).thenReturn(Arrays.asList(m1, m2));

        List<MilestoneVO> result = service.getMilestoneListFormatted();

        assertEquals(2, result.size());
        assertEquals("2024.01", result.get(0).getDateFormatted());
        assertEquals("项目启动", result.get(0).getTitle());
        assertEquals("2026.07", result.get(1).getDateFormatted());
    }

    // ==================== 平台数据统计 (platform_stats) ====================

    @Test
    void savePlatformStat_withBase64Icon_shouldProcessImage() {
        PlatformStat stat = new PlatformStat();
        stat.setStatLabel("注册用户");
        stat.setStatValue("52万+");
        stat.setStatIcon("data:image/png;base64,iVBORw0KGgo=");

        when(imageUtils.processBase64ImageSafe(any())).thenReturn("/image/stat.png");

        service.savePlatformStat(stat);

        assertEquals("/image/stat.png", stat.getStatIcon());
        verify(platformStatMapper).insert(stat);
    }

    @Test
    void savePlatformStat_withoutIcon_shouldNotProcessImage() {
        PlatformStat stat = new PlatformStat();
        stat.setId(1);
        stat.setStatLabel("注册用户");
        stat.setStatValue("52万+");
        stat.setStatIcon(null);

        service.savePlatformStat(stat);

        verify(imageUtils, never()).processBase64ImageSafe(any());
        verify(platformStatMapper).updateById(stat);
    }

    // ==================== 聚合数据 ====================

    @Test
    void getIntroductionData_shouldAggregateAllSections() {
        when(featureMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(techCategoryMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(milestoneMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(platformStatMapper.selectList(any())).thenReturn(Collections.emptyList());

        Map<String, Object> data = service.getIntroductionData();

        assertNotNull(data);
        assertTrue(data.containsKey("features"));
        assertTrue(data.containsKey("techStack"));
        assertTrue(data.containsKey("milestones"));
        assertTrue(data.containsKey("stats"));
    }

    // ==================== CRUD 委托 ====================

    @Test
    void getFeatureDetail_shouldDelegateToMapper() {
        Feature feature = new Feature();
        feature.setId(1);
        when(featureMapper.selectById(1)).thenReturn(feature);

        Feature result = service.getFeatureDetail(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void saveTechItem_shouldInsertWhenNoId() {
        TechItem item = new TechItem();
        item.setTechName("Redis");
        service.saveTechItem(item);
        verify(techItemMapper).insert(item);
        verify(techItemMapper, never()).updateById(any());
    }

    @Test
    void saveTechItem_shouldUpdateWhenHasId() {
        TechItem item = new TechItem();
        item.setId(5);
        item.setTechName("Redis");
        service.saveTechItem(item);
        verify(techItemMapper).updateById(item);
        verify(techItemMapper, never()).insert(any());
    }
}
