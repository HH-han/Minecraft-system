package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Tags;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class TagsExcelRow extends ExcelImportRow {

    @ExcelProperty("标签名称")
    private String name;

    @ExcelProperty("中文名称")
    private String chineseName;

    @ExcelProperty("分类")
    private String category;

    @ExcelProperty("图标")
    private String icon;

    @ExcelProperty("颜色")
    private String color;

    @ExcelProperty("排序")
    private Integer sortOrder;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("标签名称不能为空");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Tags entity = new Tags();
        entity.setName(name);
        entity.setChineseName(chineseName);
        entity.setCategory(category);
        entity.setIcon(icon);
        entity.setColor(color);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static TagsExcelRow from(Tags entity) {
        TagsExcelRow row = new TagsExcelRow();
        row.setName(entity.getName());
        row.setChineseName(entity.getChineseName());
        row.setCategory(entity.getCategory());
        row.setIcon(entity.getIcon());
        row.setColor(entity.getColor());
        row.setSortOrder(entity.getSortOrder());
        return row;
    }
}
