package com.minecraft.excel.handler;

import com.minecraft.entity.PointsProducts;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.PointsProductExcelRow;
import com.minecraft.service.PointsProductsService;
import org.springframework.stereotype.Component;

@Component
public class PointsProductExcelHandler extends AbstractExcelBizHandler<PointsProductExcelRow, PointsProducts> {

    public PointsProductExcelHandler(PointsProductsService service) {
        super(service, PointsProductExcelRow.class, "积分商品");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.POINTS_PRODUCT;
    }

    @Override
    protected String uniqueValue(PointsProductExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(PointsProducts entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(PointsProducts entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(PointsProducts entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected PointsProductExcelRow toRow(PointsProducts entity) {
        return PointsProductExcelRow.from(entity);
    }
}
