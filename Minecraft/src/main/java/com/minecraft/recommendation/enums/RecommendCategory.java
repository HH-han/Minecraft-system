package com.minecraft.recommendation.enums;

import com.minecraft.exception.BusinessException;

import java.util.Arrays;

/**
 * 推荐分类枚举。
 * <p>
 * 统一维护「分类编码 - 行为表 item_type - 物品表 - 推荐结果表」映射，
 * 行为表（collection/like_record/comment/cart/orders）中的 item_type
 * 与 {@link #code()} 保持一致。
 */
public enum RecommendCategory {

    ATTRACTION("attraction", "attraction", "attractions_recommendations", "景点"),
    HOTEL("hotel", "hotel", "hotels_recommendations", "酒店"),
    RESTAURANT("food", "food", "restaurants_recommendations", "美食"),
    SOUVENIR("product", "product", "souvenirs_recommendations", "纪念品");

    private final String code;
    private final String itemTable;
    private final String recommendationTable;
    private final String displayName;

    RecommendCategory(String code, String itemTable, String recommendationTable, String displayName) {
        this.code = code;
        this.itemTable = itemTable;
        this.recommendationTable = recommendationTable;
        this.displayName = displayName;
    }

    /** 分类编码，同时也是行为表中的 item_type 值 */
    public String code() {
        return code;
    }

    /** 源物品表名 */
    public String itemTable() {
        return itemTable;
    }

    /** 推荐结果表名 */
    public String recommendationTable() {
        return recommendationTable;
    }

    public String displayName() {
        return displayName;
    }

    /**
     * 按编码解析分类，非法编码抛出 400 业务异常。
     */
    public static RecommendCategory fromCode(String code) {
        return Arrays.stream(values())
                .filter(c -> c.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(400, "不支持的推荐分类：" + code));
    }
}
