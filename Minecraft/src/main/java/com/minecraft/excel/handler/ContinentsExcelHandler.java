package com.minecraft.excel.handler;

import com.minecraft.entity.Continents;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.ContinentsExcelRow;
import com.minecraft.service.ContinentsService;
import org.springframework.stereotype.Component;

@Component
public class ContinentsExcelHandler extends AbstractExcelBizHandler<ContinentsExcelRow, Continents> {

    public ContinentsExcelHandler(ContinentsService service) {
        super(service, ContinentsExcelRow.class, "大洲");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.CONTINENTS;
    }

    @Override
    protected String uniqueValue(ContinentsExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Continents entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Continents entity) {
        return entity.getId() == null ? null : entity.getId().longValue();
    }

    @Override
    protected void assignId(Continents entity, Long id) {
        entity.setId(id == null ? null : id.intValue());
    }

    @Override
    protected ContinentsExcelRow toRow(Continents entity) {
        return ContinentsExcelRow.from(entity);
    }
}
