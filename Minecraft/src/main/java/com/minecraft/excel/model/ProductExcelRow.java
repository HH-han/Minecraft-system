package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Product;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class ProductExcelRow extends ExcelImportRow {

    @ExcelProperty("商品名称")
    private String name;

    @ExcelProperty("商品类型")
    private String type;

    @ExcelProperty("所属城市")
    private String city;

    @ExcelProperty("所属省份")
    private String province;

    @ExcelProperty("价格")
    private BigDecimal price;

    @ExcelProperty("库存")
    private Integer stock;

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
            errors.add("商品名称不能为空");
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("价格不能为负数");
        }
        if (stock != null && stock < 0) {
            errors.add("库存不能为负数");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Product entity = new Product();
        entity.setName(name);
        entity.setType(type);
        entity.setCity(city);
        entity.setProvince(province);
        entity.setPrice(price);
        entity.setStock(stock == null ? 0 : stock);
        entity.setTags(tags);
        entity.setStatus(status == null ? 1 : status);
        entity.setDescription(description);
        return entity;
    }

    public static ProductExcelRow from(Product entity) {
        ProductExcelRow row = new ProductExcelRow();
        row.setName(entity.getName());
        row.setType(entity.getType());
        row.setCity(entity.getCity());
        row.setProvince(entity.getProvince());
        row.setPrice(entity.getPrice());
        row.setStock(entity.getStock());
        row.setTags(entity.getTags());
        row.setStatus(entity.getStatus());
        row.setDescription(entity.getDescription());
        return row;
    }
}
