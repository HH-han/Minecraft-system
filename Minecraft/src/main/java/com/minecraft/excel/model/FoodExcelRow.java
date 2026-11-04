package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Food;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class FoodExcelRow extends ExcelImportRow {

    @ExcelProperty("美食名称")
    private String name;

    @ExcelProperty("所属城市")
    private String city;

    @ExcelProperty("所属省份")
    private String province;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("参考价格")
    private BigDecimal price;

    @ExcelProperty("菜系")
    private String cuisineType;

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
            errors.add("美食名称不能为空");
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("参考价格不能为负数");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Food entity = new Food();
        entity.setName(name);
        entity.setCity(city);
        entity.setProvince(province);
        entity.setAddress(address);
        entity.setPrice(price);
        entity.setCuisineType(cuisineType);
        entity.setTags(tags);
        entity.setStatus(status == null ? 1 : status);
        entity.setDescription(description);
        return entity;
    }

    public static FoodExcelRow from(Food entity) {
        FoodExcelRow row = new FoodExcelRow();
        row.setName(entity.getName());
        row.setCity(entity.getCity());
        row.setProvince(entity.getProvince());
        row.setAddress(entity.getAddress());
        row.setPrice(entity.getPrice());
        row.setCuisineType(entity.getCuisineType());
        row.setTags(entity.getTags());
        row.setStatus(entity.getStatus());
        row.setDescription(entity.getDescription());
        return row;
    }
}
