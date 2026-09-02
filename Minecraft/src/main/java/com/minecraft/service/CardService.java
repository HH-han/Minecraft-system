package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.CardRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Card;
import com.minecraft.vo.CardVO;

/**
 * 旅行卡片业务接口
 */
public interface CardService extends IService<Card> {
    /**
     * 分页查询卡片列表（仅查询状态正常的卡片）
     *
     * @param request  分页参数
     * @param location 地点筛选（模糊匹配，可为空）
     * @param tag      标签筛选（模糊匹配，可为空）
     */
    PageResponse<CardVO> getCardList(PageRequest request, String location, String tag);

    /**
     * 分页查询当前用户的卡片列表
     */
    PageResponse<CardVO> getMyCards(Long userId, PageRequest request);

    /**
     * 获取卡片详情，同时浏览次数 +1
     */
    CardVO getCardDetail(Integer id);

    /**
     * 创建卡片，并维护卡片标签关联关系
     */
    Card createCard(CardRequest request, Long userId);

    /**
     * 更新卡片（仅卡片作者可操作），并全量同步标签关联关系
     */
    void updateCard(CardRequest request, Long userId);

    /**
     * 删除卡片（逻辑删除，status 置为 0；仅卡片作者可操作）
     */
    void deleteCard(Integer id, Long userId);
}
