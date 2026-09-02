package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraft.dto.request.CardRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Card;
import com.minecraft.entity.CardTag;
import com.minecraft.entity.Tags;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.CardMapper;
import com.minecraft.mapper.CardTagMapper;
import com.minecraft.service.CardService;
import com.minecraft.service.TagsService;
import com.minecraft.service.UserService;
import com.minecraft.vo.CardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 旅行卡片业务实现
 */
@Slf4j
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    @Autowired
    private CardTagMapper cardTagMapper;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private UserService userService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 卡片状态：正常
     */
    private static final int STATUS_NORMAL = 1;

    /**
     * 卡片状态：删除
     */
    private static final int STATUS_DELETED = 0;

    @Override
    public PageResponse<CardVO> getCardList(PageRequest request, String location, String tag) {
        Page<Card> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Card> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Card::getStatus, STATUS_NORMAL);
        // 关键字模糊匹配标题或内容
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().trim();
            wrapper.and(w -> w.like(Card::getTitle, keyword)
                    .or().like(Card::getContent, keyword));
        }
        // 地点筛选
        if (location != null && !location.trim().isEmpty()) {
            wrapper.like(Card::getLocation, location.trim());
        }
        // 标签筛选（匹配 tags 字段）
        if (tag != null && !tag.trim().isEmpty()) {
            wrapper.like(Card::getTags, tag.trim());
        }
        wrapper.orderByDesc(Card::getCreatedAt);

        Page<Card> result = page(page, wrapper);

        List<CardVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        fillTagNames(voList);

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResponse<CardVO> getMyCards(Long userId, PageRequest request) {
        Page<Card> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Card> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Card::getUserId, userId)
                .ne(Card::getStatus, STATUS_DELETED)
                .orderByDesc(Card::getCreatedAt);

        Page<Card> result = page(page, wrapper);

        List<CardVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        fillTagNames(voList);

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public CardVO getCardDetail(Integer id) {
        Card card = getById(id);
        if (card == null || (card.getStatus() != null && card.getStatus() == STATUS_DELETED)) {
            throw new BusinessException(404, "卡片不存在或已删除");
        }

        // 浏览次数 +1
        LambdaUpdateWrapper<Card> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Card::getId, id)
                .setSql("view_count = view_count + 1");
        this.update(updateWrapper);
        card.setViewCount(card.getViewCount() == null ? 1 : card.getViewCount() + 1);

        CardVO vo = convertToVO(card);
        fillTagNames(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Card createCard(CardRequest request, Long userId) {
        Card card = new Card();
        BeanUtils.copyProperties(request, card);
        card.setUserId(userId == null ? null : userId.intValue());
        card.setUsername(resolveUsername(userId));
        card.setViewCount(0);
        card.setLikeCount(0);
        card.setStatus(STATUS_NORMAL);
        save(card);

        // 维护卡片标签关联关系
        saveTagRelations(card.getId(), request.getTagIds());

        log.info("创建旅行卡片成功：cardId={}, userId={}, title={}", card.getId(), userId, card.getTitle());
        return card;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCard(CardRequest request, Long userId) {
        if (request.getId() == null) {
            throw new BusinessException("卡片ID不能为空");
        }
        Card existCard = getById(request.getId());
        if (existCard == null || (existCard.getStatus() != null && existCard.getStatus() == STATUS_DELETED)) {
            throw new BusinessException(404, "卡片不存在或已删除");
        }
        checkOwnership(existCard, userId);

        Card card = new Card();
        BeanUtils.copyProperties(request, card);
        // 保留不可由前端覆盖的字段
        card.setUserId(existCard.getUserId());
        card.setUsername(existCard.getUsername());
        card.setViewCount(existCard.getViewCount());
        card.setLikeCount(existCard.getLikeCount());
        card.setStatus(existCard.getStatus());
        updateById(card);

        // 全量同步卡片标签关联关系：先删除旧关联，再写入新关联
        cardTagMapper.delete(new LambdaQueryWrapper<CardTag>().eq(CardTag::getCardId, existCard.getId()));
        saveTagRelations(existCard.getId(), request.getTagIds());

        log.info("更新旅行卡片成功：cardId={}, userId={}", request.getId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCard(Integer id, Long userId) {
        Card existCard = getById(id);
        if (existCard == null || (existCard.getStatus() != null && existCard.getStatus() == STATUS_DELETED)) {
            throw new BusinessException(404, "卡片不存在或已删除");
        }
        checkOwnership(existCard, userId);

        // 逻辑删除：状态置为 0
        LambdaUpdateWrapper<Card> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Card::getId, id)
                .set(Card::getStatus, STATUS_DELETED);
        this.update(updateWrapper);

        log.info("删除旅行卡片成功（逻辑删除）：cardId={}, userId={}", id, userId);
    }

    /**
     * 校验当前登录用户是否为卡片作者
     */
    private void checkOwnership(Card card, Long userId) {
        if (userId == null || card.getUserId() == null
                || !userId.equals(card.getUserId().longValue())) {
            throw new BusinessException(403, "无权操作此卡片");
        }
    }

    /**
     * 根据用户ID解析用户名：优先昵称，其次用户名，最后账号
     */
    private String resolveUsername(Long userId) {
        if (userId == null) {
            return "未知用户";
        }
        try {
            User user = userService.getUserInfo(userId);
            if (user != null) {
                if (user.getNickname() != null && !user.getNickname().isEmpty()) {
                    return user.getNickname();
                }
                if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                    return user.getUsername();
                }
                if (user.getAccount() != null && !user.getAccount().isEmpty()) {
                    return user.getAccount();
                }
            }
        } catch (Exception e) {
            log.warn("查询用户信息失败，使用默认用户名：userId={}", userId, e);
        }
        return "用户" + userId;
    }

    /**
     * 批量保存卡片标签关联关系（自动去重、忽略空值）
     */
    private void saveTagRelations(Integer cardId, List<Integer> tagIds) {
        if (cardId == null || tagIds == null || tagIds.isEmpty()) {
            return;
        }
        Set<Integer> distinctTagIds = tagIds.stream()
                .filter(tagId -> tagId != null)
                .collect(Collectors.toSet());
        for (Integer tagId : distinctTagIds) {
            CardTag cardTag = new CardTag();
            cardTag.setCardId(cardId);
            cardTag.setTagId(tagId);
            cardTagMapper.insert(cardTag);
        }
    }

    /**
     * 实体转 VO，并解析图片 JSON 数组
     */
    private CardVO convertToVO(Card card) {
        CardVO vo = new CardVO();
        BeanUtils.copyProperties(card, vo);
        vo.setImageList(parseImages(card.getImages()));
        return vo;
    }

    /**
     * 批量填充卡片关联的标签名称列表
     */
    private void fillTagNames(List<CardVO> voList) {
        if (voList == null || voList.isEmpty()) {
            return;
        }
        List<Integer> cardIds = voList.stream()
                .map(CardVO::getId)
                .collect(Collectors.toList());
        List<CardTag> relations = cardTagMapper.selectList(
                new LambdaQueryWrapper<CardTag>().in(CardTag::getCardId, cardIds));
        if (relations.isEmpty()) {
            voList.forEach(vo -> vo.setTagNames(Collections.emptyList()));
            return;
        }

        Set<Integer> tagIds = relations.stream()
                .map(CardTag::getTagId)
                .collect(Collectors.toSet());
        Map<Integer, String> tagNameMap = tagsService.listByIds(tagIds).stream()
                .collect(Collectors.toMap(Tags::getId, this::resolveTagName, (a, b) -> a));

        Map<Integer, List<String>> cardTagNames = relations.stream()
                .filter(relation -> tagNameMap.containsKey(relation.getTagId()))
                .collect(Collectors.groupingBy(CardTag::getCardId,
                        Collectors.mapping(relation -> tagNameMap.get(relation.getTagId()), Collectors.toList())));

        voList.forEach(vo -> vo.setTagNames(cardTagNames.getOrDefault(vo.getId(), Collections.emptyList())));
    }

    /**
     * 标签显示名称：优先中文名，其次英文名
     */
    private String resolveTagName(Tags tag) {
        if (tag.getChineseName() != null && !tag.getChineseName().isEmpty()) {
            return tag.getChineseName();
        }
        return tag.getName();
    }

    /**
     * 解析图片字段：
     * 1. 支持 JSON 数组格式 ["http://...", "http://..."]
     * 2. 兼容逗号分隔格式 http://...,http://...
     */
    private List<String> parseImages(String images) {
        List<String> imageList = new ArrayList<>();
        if (images == null || images.trim().isEmpty()) {
            return imageList;
        }
        String imagesStr = images.trim();
        if (imagesStr.startsWith("[")) {
            try {
                List<String> parsedList = objectMapper.readValue(imagesStr, new TypeReference<List<String>>() {});
                for (String img : parsedList) {
                    if (img != null && !img.trim().isEmpty()) {
                        imageList.add(img.trim());
                    }
                }
                return imageList;
            } catch (Exception e) {
                log.warn("图片JSON解析失败，回退逗号分隔解析：{}", imagesStr);
            }
        }
        // 逗号分隔格式兼容
        String[] parts = imagesStr.split(",");
        for (String part : parts) {
            String trimmed = part.trim().replace("`", "").replace("\"", "");
            if (!trimmed.isEmpty()) {
                imageList.add(trimmed);
            }
        }
        return imageList;
    }
}
