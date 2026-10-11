package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.FavoriteSaveDTO;
import com.minecraft.dto.response.FavoriteStatVO;
import com.minecraft.dto.response.FavoriteVO;
import com.minecraft.entity.Favorite;

import java.util.List;

/**
 * 收藏服务
 */
public interface FavoriteService extends IService<Favorite> {

    /** 新增/编辑收藏 */
    FavoriteVO save(Long userId, FavoriteSaveDTO dto);

    /** 分页查询收藏（可按类型、标签、关键字筛选） */
    List<FavoriteVO> list(Long userId, String type, String tag, String keyword, Integer page, Integer size);

    /** 最近使用的收藏 */
    List<FavoriteVO> recent(Long userId, Integer limit);

    /** 收藏详情 */
    FavoriteVO detail(Long userId, Long id);

    /** 删除收藏（软删除，仅本人可删） */
    void delete(Long userId, Long id);

    /** 清空某类型收藏（type 为空表示全部） */
    void clear(Long userId, String type);

    /** 更新最近使用时间 */
    void touch(Long userId, Long id);

    /** 分类统计 */
    FavoriteStatVO stats(Long userId);

    /** 当前用户的所有标签 */
    List<String> tags(Long userId);
}
