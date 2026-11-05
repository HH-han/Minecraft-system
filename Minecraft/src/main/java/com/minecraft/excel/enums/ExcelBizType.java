package com.minecraft.excel.enums;

import lombok.Getter;

/**
 * Excel 业务类型。新增实体时在此登记，并实现对应的 ExcelBizHandler 即可。
 */
@Getter
public enum ExcelBizType {

    ATTRACTION("attraction", "景点"),
    HOTEL("hotel", "酒店"),
    FOOD("food", "美食"),
    PRODUCT("product", "商品"),
    TICKET("ticket", "票务"),
    CITY("city", "城市"),
    POINTS_PRODUCT("points_product", "积分商品"),
    ANNOUNCEMENT("announcement", "公告"),
    CAROUSEL("carousel", "轮播图"),
    RAILWAY_STATION("railway_station", "火车站点"),
    TAGS("tags", "标签"),
    SAFETY_TIPS("safety_tips", "安全提示"),
    COUNTRIES("countries", "国家"),
    CONTINENTS("continents", "大洲"),
    NEWS("news", "资讯"),
    USER("user", "用户");

    private final String code;
    private final String desc;

    ExcelBizType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 支持 code（attraction）、枚举名（ATTRACTION）两种写法，大小写不敏感。
     */
    public static ExcelBizType of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("业务类型不能为空");
        }
        String v = value.trim();
        for (ExcelBizType type : values()) {
            if (type.code.equalsIgnoreCase(v) || type.name().equalsIgnoreCase(v)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的业务类型：" + value);
    }
}
