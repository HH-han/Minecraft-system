package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.News;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class NewsExcelRow extends ExcelImportRow {

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("内容")
    private String content;

    @ExcelProperty("封面图")
    private String coverImage;

    @ExcelProperty("来源")
    private String source;

    @ExcelProperty("浏览数")
    private Integer viewCount;

    @ExcelProperty("点赞数")
    private Integer likeCount;

    @ExcelProperty("收藏数")
    private Integer collectCount;

    @ExcelProperty("评论数")
    private Integer commentCount;

    @ExcelProperty("状态")
    private Integer status;

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
        News entity = new News();
        entity.setTitle(title);
        entity.setContent(content);
        entity.setCoverImage(coverImage);
        entity.setSource(source);
        entity.setViewCount(viewCount);
        entity.setLikeCount(likeCount);
        entity.setCollectCount(collectCount);
        entity.setCommentCount(commentCount);
        entity.setStatus(status);
        return entity;
    }

    public static NewsExcelRow from(News entity) {
        NewsExcelRow row = new NewsExcelRow();
        row.setTitle(entity.getTitle());
        row.setContent(entity.getContent());
        row.setCoverImage(entity.getCoverImage());
        row.setSource(entity.getSource());
        row.setViewCount(entity.getViewCount());
        row.setLikeCount(entity.getLikeCount());
        row.setCollectCount(entity.getCollectCount());
        row.setCommentCount(entity.getCommentCount());
        row.setStatus(entity.getStatus());
        return row;
    }
}
