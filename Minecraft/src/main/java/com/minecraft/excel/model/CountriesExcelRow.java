package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Countries;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class CountriesExcelRow extends ExcelImportRow {

    @ExcelProperty("国家名称")
    private String name;

    @ExcelProperty("中文名称")
    private String chineseName;

    @ExcelProperty("图片")
    private String image;

    @ExcelProperty("所属大洲ID")
    private Integer continentId;

    @ExcelProperty("首都")
    private String capital;

    @ExcelProperty("面积")
    private BigDecimal area;

    @ExcelProperty("人口")
    private Integer population;

    @ExcelProperty("货币")
    private String currency;

    @ExcelProperty("语言")
    private String language;

    @ExcelProperty("时区")
    private String timezone;

    @ExcelProperty("国家代码")
    private String countryCode;

    @ExcelProperty("电话区号")
    private String phoneCode;

    @ExcelProperty("国旗Emoji")
    private String flagEmoji;

    @ExcelProperty("简介")
    private String description;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("国家名称不能为空");
        }
        if (chineseName == null || chineseName.isBlank()) {
            errors.add("中文名称不能为空");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Countries entity = new Countries();
        entity.setName(name);
        entity.setChineseName(chineseName);
        entity.setImage(image);
        entity.setContinentId(continentId);
        entity.setCapital(capital);
        entity.setArea(area);
        entity.setPopulation(population);
        entity.setCurrency(currency);
        entity.setLanguage(language);
        entity.setTimezone(timezone);
        entity.setCountryCode(countryCode);
        entity.setPhoneCode(phoneCode);
        entity.setFlagEmoji(flagEmoji);
        entity.setDescription(description);
        return entity;
    }

    public static CountriesExcelRow from(Countries entity) {
        CountriesExcelRow row = new CountriesExcelRow();
        row.setName(entity.getName());
        row.setChineseName(entity.getChineseName());
        row.setImage(entity.getImage());
        row.setContinentId(entity.getContinentId());
        row.setCapital(entity.getCapital());
        row.setArea(entity.getArea());
        row.setPopulation(entity.getPopulation());
        row.setCurrency(entity.getCurrency());
        row.setLanguage(entity.getLanguage());
        row.setTimezone(entity.getTimezone());
        row.setCountryCode(entity.getCountryCode());
        row.setPhoneCode(entity.getPhoneCode());
        row.setFlagEmoji(entity.getFlagEmoji());
        row.setDescription(entity.getDescription());
        return row;
    }
}
