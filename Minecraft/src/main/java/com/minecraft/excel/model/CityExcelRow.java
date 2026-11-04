package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Cities;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class CityExcelRow extends ExcelImportRow {

    @ExcelProperty("城市名称")
    private String name;

    @ExcelProperty("中文名称")
    private String chineseName;

    @ExcelProperty("所属国家ID")
    private Integer countryId;

    @ExcelProperty("是否首都")
    private Boolean isCapital;

    @ExcelProperty("面积")
    private BigDecimal area;

    @ExcelProperty("人口")
    private Integer population;

    @ExcelProperty("时区")
    private String timezone;

    @ExcelProperty("著名特色")
    private String famousFor;

    @ExcelProperty("最佳季节")
    private String bestSeason;

    @ExcelProperty("简介")
    private String description;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("城市名称不能为空");
        }
        if (chineseName == null || chineseName.isBlank()) {
            errors.add("中文名称不能为空");
        }
        if (population != null && population < 0) {
            errors.add("人口不能为负数");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Cities entity = new Cities();
        entity.setName(name);
        entity.setChineseName(chineseName);
        entity.setCountryId(countryId);
        entity.setIsCapital(isCapital == null ? Boolean.FALSE : isCapital);
        entity.setArea(area);
        entity.setPopulation(population);
        entity.setTimezone(timezone);
        entity.setFamousFor(famousFor);
        entity.setBestSeason(bestSeason);
        entity.setDescription(description);
        return entity;
    }

    public static CityExcelRow from(Cities entity) {
        CityExcelRow row = new CityExcelRow();
        row.setName(entity.getName());
        row.setChineseName(entity.getChineseName());
        row.setCountryId(entity.getCountryId());
        row.setIsCapital(entity.getIsCapital());
        row.setArea(entity.getArea());
        row.setPopulation(entity.getPopulation());
        row.setTimezone(entity.getTimezone());
        row.setFamousFor(entity.getFamousFor());
        row.setBestSeason(entity.getBestSeason());
        row.setDescription(entity.getDescription());
        return row;
    }
}
