package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.PointsProducts;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class PointsProductExcelRow extends ExcelImportRow {

    @ExcelProperty("商品名称")
    private String name;

    @ExcelProperty("所需积分")
    private Integer pointsPrice;

    @ExcelProperty("库存")
    private Integer stock;

    @ExcelProperty("图片地址")
    private String imageUrl;

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
        if (pointsPrice == null) {
            errors.add("所需积分不能为空");
        } else if (pointsPrice < 0) {
            errors.add("所需积分不能为负数");
        }
        if (stock != null && stock < 0) {
            errors.add("库存不能为负数");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        PointsProducts entity = new PointsProducts();
        entity.setName(name);
        entity.setPointsPrice(pointsPrice);
        entity.setStock(stock == null ? 0 : stock);
        entity.setImageUrl(imageUrl);
        entity.setStatus(status == null ? 1 : status);
        entity.setDescription(description);
        return entity;
    }

    public static PointsProductExcelRow from(PointsProducts entity) {
        PointsProductExcelRow row = new PointsProductExcelRow();
        row.setName(entity.getName());
        row.setPointsPrice(entity.getPointsPrice());
        row.setStock(entity.getStock());
        row.setImageUrl(entity.getImageUrl());
        row.setStatus(entity.getStatus());
        row.setDescription(entity.getDescription());
        return row;
    }
}
