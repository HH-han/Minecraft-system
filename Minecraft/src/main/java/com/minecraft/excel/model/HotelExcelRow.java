package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Hotel;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class HotelExcelRow extends ExcelImportRow {

    @ExcelProperty("酒店名称")
    private String name;

    @ExcelProperty("所属城市")
    private String city;

    @ExcelProperty("所属省份")
    private String province;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("参考价格")
    private BigDecimal price;

    @ExcelProperty("星级(1-5)")
    private Integer starLevel;

    @ExcelProperty("设施(逗号分隔)")
    private String facilities;

    @ExcelProperty("状态(0下架/1上架)")
    private Integer status;

    @ExcelProperty("简介")
    private String description;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add("酒店名称不能为空");
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("参考价格不能为负数");
        }
        if (starLevel != null && (starLevel < 1 || starLevel > 5)) {
            errors.add("星级必须在1-5之间");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Hotel entity = new Hotel();
        entity.setName(name);
        entity.setCity(city);
        entity.setProvince(province);
        entity.setAddress(address);
        entity.setPrice(price);
        entity.setStarLevel(starLevel);
        entity.setFacilities(facilities);
        entity.setStatus(status == null ? 1 : status);
        entity.setDescription(description);
        return entity;
    }

    public static HotelExcelRow from(Hotel entity) {
        HotelExcelRow row = new HotelExcelRow();
        row.setName(entity.getName());
        row.setCity(entity.getCity());
        row.setProvince(entity.getProvince());
        row.setAddress(entity.getAddress());
        row.setPrice(entity.getPrice());
        row.setStarLevel(entity.getStarLevel());
        row.setFacilities(entity.getFacilities());
        row.setStatus(entity.getStatus());
        row.setDescription(entity.getDescription());
        return row;
    }
}
