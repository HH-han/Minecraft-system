package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Announcement;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class AnnouncementExcelRow extends ExcelImportRow {

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("摘要")
    private String summary;

    @ExcelProperty("内容")
    private String content;

    @ExcelProperty("类型")
    private Integer type;

    @ExcelProperty("级别")
    private Integer level;

    @ExcelProperty("分类ID")
    private Long categoryId;

    @ExcelProperty("封面图")
    private String coverImage;

    @ExcelProperty("展示方式")
    private Integer displayMode;

    @ExcelProperty("目标受众")
    private Integer targetAudience;

    @ExcelProperty("状态")
    private Integer status;

    @ExcelProperty("是否置顶")
    private Integer isTop;

    @ExcelProperty("排序权重")
    private Integer sortWeight;

    @ExcelProperty("发布时间")
    private LocalDateTime publishTime;

    @ExcelProperty("过期时间")
    private LocalDateTime expireTime;

    @ExcelProperty("浏览数")
    private Long viewCount;

    @ExcelProperty("阅读数")
    private Long readCount;

    @ExcelProperty("创建人ID")
    private Long creatorId;

    @ExcelProperty("创建人名称")
    private String creatorName;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (title == null || title.isBlank()) {
            errors.add("标题不能为空");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Announcement entity = new Announcement();
        entity.setTitle(title);
        entity.setSummary(summary);
        entity.setContent(content);
        entity.setType(type);
        entity.setLevel(level);
        entity.setCategoryId(categoryId);
        entity.setCoverImage(coverImage);
        entity.setDisplayMode(displayMode);
        entity.setTargetAudience(targetAudience);
        entity.setStatus(status);
        entity.setIsTop(isTop);
        entity.setSortWeight(sortWeight);
        entity.setPublishTime(publishTime);
        entity.setExpireTime(expireTime);
        entity.setViewCount(viewCount);
        entity.setReadCount(readCount);
        entity.setCreatorId(creatorId);
        entity.setCreatorName(creatorName);
        return entity;
    }

    public static AnnouncementExcelRow from(Announcement entity) {
        AnnouncementExcelRow row = new AnnouncementExcelRow();
        row.setTitle(entity.getTitle());
        row.setSummary(entity.getSummary());
        row.setContent(entity.getContent());
        row.setType(entity.getType());
        row.setLevel(entity.getLevel());
        row.setCategoryId(entity.getCategoryId());
        row.setCoverImage(entity.getCoverImage());
        row.setDisplayMode(entity.getDisplayMode());
        row.setTargetAudience(entity.getTargetAudience());
        row.setStatus(entity.getStatus());
        row.setIsTop(entity.getIsTop());
        row.setSortWeight(entity.getSortWeight());
        row.setPublishTime(entity.getPublishTime());
        row.setExpireTime(entity.getExpireTime());
        row.setViewCount(entity.getViewCount());
        row.setReadCount(entity.getReadCount());
        row.setCreatorId(entity.getCreatorId());
        row.setCreatorName(entity.getCreatorName());
        return row;
    }
}
