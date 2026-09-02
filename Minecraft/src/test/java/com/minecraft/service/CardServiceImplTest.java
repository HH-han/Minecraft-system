package com.minecraft.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.dto.request.CardRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Card;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.CardMapper;
import com.minecraft.mapper.CardTagMapper;
import com.minecraft.service.impl.CardServiceImpl;
import com.minecraft.vo.CardVO;
import org.apache.ibatis.builder.MapperBuilderAssistant;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CardServiceImpl 单元测试。
 * 重点验证：分页查询过滤、图片JSON解析、详情浏览量自增、
 * 创建/更新时的标签关联维护、权限校验与逻辑删除。
 */
@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardTagMapper cardTagMapper;

    @Mock
    private TagsService tagsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardServiceImpl service;

    @BeforeEach
    void setUp() {
        // ServiceImpl 的 baseMapper 为父类 protected 字段，显式注入保证被赋值
        ReflectionTestUtils.setField(service, "baseMapper", cardMapper);
        // 初始化 MyBatis-Plus 实体表信息缓存，供 LambdaWrapper 解析列名（纯 Mock 环境不会自动初始化）
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, Card.class);
    }

    // ==================== 分页查询 ====================

    @Test
    void getCardList_shouldReturnNormalCardsAndParseImages() {
        Card card1 = buildCard(1, "北京之旅", "[\"http://img/a.jpg\",\"http://img/b.jpg\"]", 1);
        Card card2 = buildCard(2, "上海之旅", null, 1);
        List<Card> records = Arrays.asList(card1, card2);

        when(cardMapper.selectPage(any(Page.class), any())).thenAnswer(invocation -> {
            Page<Card> page = invocation.getArgument(0);
            page.setRecords(records);
            page.setTotal(records.size());
            return page;
        });
        when(cardTagMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);
        PageResponse<CardVO> response = service.getCardList(pageRequest, "北京", null);

        assertEquals(2L, response.getTotal());
        assertEquals(2, response.getRecords().size());
        CardVO first = response.getRecords().get(0);
        assertEquals("北京之旅", first.getTitle());
        // JSON 数组图片应被解析为列表
        assertEquals(2, first.getImageList().size());
        assertEquals("http://img/a.jpg", first.getImageList().get(0));
        // 无图片时返回空列表而非 null
        assertEquals(0, response.getRecords().get(1).getImageList().size());
        // 无标签关联时标签名列表为空
        assertEquals(0, first.getTagNames().size());
    }

    @Test
    void getMyCards_shouldQueryByUserId() {
        Card card = buildCard(1, "我的卡片", null, 1);
        when(cardMapper.selectPage(any(Page.class), any())).thenAnswer(invocation -> {
            Page<Card> page = invocation.getArgument(0);
            page.setRecords(Collections.singletonList(card));
            page.setTotal(1L);
            return page;
        });
        when(cardTagMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageRequest pageRequest = new PageRequest();
        PageResponse<CardVO> response = service.getMyCards(1L, pageRequest);

        assertEquals(1L, response.getTotal());
        assertEquals("我的卡片", response.getRecords().get(0).getTitle());
    }

    // ==================== 详情 ====================

    @Test
    void getCardDetail_notFound_shouldThrow() {
        when(cardMapper.selectById(99)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.getCardDetail(99));
        assertEquals(404, exception.getCode());
    }

    @Test
    void getCardDetail_deletedCard_shouldThrow() {
        Card card = buildCard(1, "已删除", null, 0);
        when(cardMapper.selectById(1)).thenReturn(card);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.getCardDetail(1));
        assertEquals(404, exception.getCode());
    }

    @Test
    void getCardDetail_shouldIncrementViewCount() {
        Card card = buildCard(1, "详情卡片", "[\"http://img/a.jpg\"]", 1);
        card.setViewCount(10);
        when(cardMapper.selectById(1)).thenReturn(card);
        when(cardMapper.update(any(), any())).thenReturn(1);
        when(cardTagMapper.selectList(any())).thenReturn(Collections.emptyList());

        CardVO vo = service.getCardDetail(1);

        assertEquals(11, vo.getViewCount());
        // 应执行一次浏览量自增更新
        verify(cardMapper, times(1)).update(any(), any());
        assertEquals(1, vo.getImageList().size());
    }

    // ==================== 创建 ====================

    @Test
    void createCard_shouldSaveCardAndDedupTagRelations() {
        User user = new User();
        user.setId(1L);
        user.setUsername("tester");
        when(userService.getUserInfo(1L)).thenReturn(user);
        when(cardMapper.insert(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(100);
            return 1;
        });

        CardRequest request = new CardRequest();
        request.setTitle("新卡片");
        request.setContent("内容");
        request.setLocation("杭州");
        request.setDate(LocalDate.of(2026, 9, 1));
        // 重复标签ID应被去重
        request.setTagIds(Arrays.asList(1, 2, 1));

        Card created = service.createCard(request, 1L);

        assertEquals(100, created.getId());
        assertEquals("tester", created.getUsername());
        assertEquals(1, created.getStatus());
        assertEquals(0, created.getViewCount());
        assertEquals(0, created.getLikeCount());
        // 去重后只写入 2 条关联
        verify(cardTagMapper, times(2)).insert(any());
    }

    // ==================== 更新 ====================

    @Test
    void updateCard_notOwner_shouldThrow() {
        Card exist = buildCard(1, "他人卡片", null, 1);
        exist.setUserId(2);
        when(cardMapper.selectById(1)).thenReturn(exist);

        CardRequest request = new CardRequest();
        request.setId(1);
        request.setTitle("篡改标题");
        request.setContent("内容");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.updateCard(request, 1L));
        assertEquals(403, exception.getCode());
        verify(cardMapper, never()).updateById(any());
    }

    @Test
    void updateCard_shouldSyncTagRelations() {
        Card exist = buildCard(1, "原标题", null, 1);
        when(cardMapper.selectById(1)).thenReturn(exist);
        when(cardMapper.updateById(any(Card.class))).thenReturn(1);
        when(cardTagMapper.delete(any())).thenReturn(1);

        CardRequest request = new CardRequest();
        request.setId(1);
        request.setTitle("新标题");
        request.setContent("新内容");
        request.setTagIds(Collections.singletonList(5));

        service.updateCard(request, 1L);

        // 先删旧关联，再写入新关联
        verify(cardTagMapper, times(1)).delete(any());
        verify(cardTagMapper, times(1)).insert(any());
        verify(cardMapper, times(1)).updateById(any(Card.class));
    }

    @Test
    void updateCard_withoutId_shouldThrow() {
        CardRequest request = new CardRequest();
        request.setTitle("无ID");
        request.setContent("内容");

        assertThrows(BusinessException.class, () -> service.updateCard(request, 1L));
        verify(cardMapper, never()).selectById(any());
    }

    // ==================== 删除 ====================

    @Test
    void deleteCard_notOwner_shouldThrow() {
        Card exist = buildCard(1, "他人卡片", null, 1);
        exist.setUserId(2);
        when(cardMapper.selectById(1)).thenReturn(exist);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.deleteCard(1, 1L));
        assertEquals(403, exception.getCode());
        verify(cardMapper, never()).update(any(), any());
    }

    @Test
    void deleteCard_shouldLogicalDelete() {
        Card exist = buildCard(1, "待删除", null, 1);
        when(cardMapper.selectById(1)).thenReturn(exist);
        when(cardMapper.update(any(), any())).thenReturn(1);

        service.deleteCard(1, 1L);

        // 逻辑删除走 update（status 置 0），不执行物理删除
        verify(cardMapper, times(1)).update(any(), any());
    }

    // ==================== 辅助方法 ====================

    private Card buildCard(Integer id, String title, String images, Integer status) {
        Card card = new Card();
        card.setId(id);
        card.setUserId(1);
        card.setUsername("tester");
        card.setTitle(title);
        card.setContent("卡片内容");
        card.setImages(images);
        card.setLocation("北京");
        card.setTags("美食,打卡");
        card.setDate(LocalDate.of(2026, 9, 1));
        card.setViewCount(0);
        card.setLikeCount(0);
        card.setStatus(status);
        return card;
    }
}
