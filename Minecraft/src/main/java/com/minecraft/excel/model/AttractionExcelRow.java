package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Attraction;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class AttractionExcelRow extends ExcelImportRow {

    @ExcelProperty("景点名称")
    private String name;

    @ExcelProperty("所属城市")
    private String city;

    @ExcelProperty("所属省份")
    private String province;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("门票价格")
    private BigDecimal price;

    @ExcelProperty("最佳季节")
    private String season;

    @ExcelProperty("标签")
    private String tags;

    @ExcelProperty("状态(0下架/1上架)")
    private Integer status;

    @ExcelProperty("简介")
    private String description;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("景点名称不能为空");
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("门票价格不能为负数");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Attraction entity = new Attraction();
        entity.setName(name);
        entity.setCity(city);
        entity.setProvince(province);
        entity.setAddress(address);
        entity.setPrice(price);
        entity.setSeason(season);
        entity.setTags(tags);
        entity.setStatus(status == null ? 1 : status);
        entity.setDescription(description);
        return entity;
    }

    public static AttractionExcelRow from(Attraction entity) {
        AttractionExcelRow row = new AttractionExcelRow();
        row.setName(entity.getName());
        row.setCity(entity.getCity());
        row.setProvince(entity.getProvince());
        row.setAddress(entity.getAddress());
        row.setPrice(entity.getPrice());
        row.setSeason(entity.getSeason());
        row.setTags(entity.getTags());
        row.setStatus(entity.getStatus());
        row.setDescription(entity.getDescription());
        return row;
    }
}
