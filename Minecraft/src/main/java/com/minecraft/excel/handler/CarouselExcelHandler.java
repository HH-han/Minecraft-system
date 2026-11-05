package com.minecraft.excel.handler;

import com.minecraft.entity.Carousels;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.CarouselExcelRow;
import com.minecraft.service.CarouselsService;
import org.springframework.stereotype.Component;

@Component
public class CarouselExcelHandler extends AbstractExcelBizHandler<CarouselExcelRow, Carousels> {

    public CarouselExcelHandler(CarouselsService service) {
        super(service, CarouselExcelRow.class, "轮播图");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.CAROUSEL;
    }

    @Override
    protected String uniqueColumn() {
        return "title";
    }

    @Override
    protected String uniqueValue(CarouselExcelRow row) {
        return row.getTitle();
    }

    @Override
    protected String uniqueValue(Carousels entity) {
        return entity.getTitle();
    }

    @Override
    protected Long idOf(Carousels entity) {
        return entity.getId() == null ? null : entity.getId().longValue();
    }

    @Override
    protected void assignId(Carousels entity, Long id) {
        entity.setId(id == null ? null : id.intValue());
    }

    @Override
    protected CarouselExcelRow toRow(Carousels entity) {
        return CarouselExcelRow.from(entity);
    }
}
