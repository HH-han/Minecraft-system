package com.minecraft.excel.handler;

import com.minecraft.entity.Attraction;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.AttractionExcelRow;
import com.minecraft.service.AttractionService;
import org.springframework.stereotype.Component;

@Component
public class AttractionExcelHandler extends AbstractExcelBizHandler<AttractionExcelRow, Attraction> {

    public AttractionExcelHandler(AttractionService service) {
        super(service, AttractionExcelRow.class, "景点");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.ATTRACTION;
    }

    @Override
    protected String uniqueValue(AttractionExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Attraction entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Attraction entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(Attraction entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected AttractionExcelRow toRow(Attraction entity) {
        return AttractionExcelRow.from(entity);
    }
}
