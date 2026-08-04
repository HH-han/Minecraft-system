package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 发展历程 VO，提供按月格式化后的日期字段，对应 SQL 中 DATE_FORMAT(milestone_date, '%Y.%m') 查询。
 */
@Data
public class MilestoneVO {
    private Integer id;
    private LocalDate milestoneDate;
    /**
     * 格式化后的日期字符串，格式：yyyy.MM
     */
    private String dateFormatted;
    private String title;
    private String description;
    private Integer sortOrder;
}
