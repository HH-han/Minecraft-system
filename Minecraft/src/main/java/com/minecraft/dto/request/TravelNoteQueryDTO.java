package com.minecraft.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 旅游笔记查询请求
 * tab: recommend-推荐 latest-最新 follow-关注 city-同城
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TravelNoteQueryDTO extends PageRequest {

    private String tab = "latest";

    private Long cityId;

    private Long tagId;

    /** 只看加精 */
    private Boolean featured;
}
