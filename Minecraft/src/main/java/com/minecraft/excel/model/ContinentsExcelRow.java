package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Continents;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class ContinentsExcelRow extends ExcelImportRow {

    @ExcelProperty("大洲名称")
    private String name;

    @ExcelProperty("中文名称")
    private String chineseName;

    @ExcelProperty("面积")
    private BigDecimal area;

    @ExcelProperty("人口")
    private Integer population;

    @ExcelProperty("简介")
    private String description;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("大洲名称不能为空");
        }
        if (chineseName == null || chineseName.isBlank()) {
            errors.add("中文名称不能为空");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Continents entity = new Continents();
        entity.setName(name);
        entity.setChineseName(chineseName);
        entity.setArea(area);
        entity.setPopulation(population);
        entity.setDescription(description);
        return entity;
    }

    public static ContinentsExcelRow from(Continents entity) {
        ContinentsExcelRow row = new ContinentsExcelRow();
        row.setName(entity.getName());
        row.setChineseName(entity.getChineseName());
        row.setArea(entity.getArea());
        row.setPopulation(entity.getPopulation());
        row.setDescription(entity.getDescription());
        return row;
    }
}
