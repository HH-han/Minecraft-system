package com.minecraft.excel.handler;

import com.minecraft.entity.Product;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.ProductExcelRow;
import com.minecraft.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class ProductExcelHandler extends AbstractExcelBizHandler<ProductExcelRow, Product> {

    public ProductExcelHandler(ProductService service) {
        super(service, ProductExcelRow.class, "商品");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.PRODUCT;
    }

    @Override
    protected String uniqueValue(ProductExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Product entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Product entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(Product entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected ProductExcelRow toRow(Product entity) {
        return ProductExcelRow.from(entity);
    }
}
