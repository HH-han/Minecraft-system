package com.minecraft.dto.response;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 收藏分类统计
 */
@Data
public class FavoriteStatVO {
    /** 收藏总数 */
    private Long total = 0L;

    /** 各类型数量，key 为类型名 */
    private Map<String, Long> types = new LinkedHashMap<>();
}
