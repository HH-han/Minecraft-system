package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.SafetyTips;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class SafetyTipsExcelRow extends ExcelImportRow {

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("描述")
    private String description;

    @ExcelProperty("图片地址")
    private String imageUrl;

    @ExcelProperty("分类ID")
    private Long categoryId;

    @ExcelProperty("排序权重")
    private Integer sortWeight;

    @ExcelProperty("状态")
    private Integer status;

    @ExcelProperty("创建人ID")
    private Long creatorId;

    @ExcelProperty("更新人ID")
    private Long updaterId;

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
        SafetyTips entity = new SafetyTips();
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setImageUrl(imageUrl);
        entity.setCategoryId(categoryId);
        entity.setSortWeight(sortWeight);
        entity.setStatus(status);
        entity.setCreatorId(creatorId);
        entity.setUpdaterId(updaterId);
        return entity;
    }

    public static SafetyTipsExcelRow from(SafetyTips entity) {
        SafetyTipsExcelRow row = new SafetyTipsExcelRow();
        row.setTitle(entity.getTitle());
        row.setDescription(entity.getDescription());
        row.setImageUrl(entity.getImageUrl());
        row.setCategoryId(entity.getCategoryId());
        row.setSortWeight(entity.getSortWeight());
        row.setStatus(entity.getStatus());
        row.setCreatorId(entity.getCreatorId());
        row.setUpdaterId(entity.getUpdaterId());
        return row;
    }
}
