package com.minecraft.excel.handler;

import com.minecraft.entity.Food;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.FoodExcelRow;
import com.minecraft.service.FoodService;
import org.springframework.stereotype.Component;

@Component
public class FoodExcelHandler extends AbstractExcelBizHandler<FoodExcelRow, Food> {

    public FoodExcelHandler(FoodService service) {
        super(service, FoodExcelRow.class, "美食");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.FOOD;
    }

    @Override
    protected String uniqueValue(FoodExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Food entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Food entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(Food entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected FoodExcelRow toRow(Food entity) {
        return FoodExcelRow.from(entity);
    }
}
