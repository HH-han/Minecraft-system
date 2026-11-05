package com.minecraft.excel.handler;

import com.minecraft.entity.Countries;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.CountriesExcelRow;
import com.minecraft.service.CountriesService;
import org.springframework.stereotype.Component;

@Component
public class CountriesExcelHandler extends AbstractExcelBizHandler<CountriesExcelRow, Countries> {

    public CountriesExcelHandler(CountriesService service) {
        super(service, CountriesExcelRow.class, "国家");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.COUNTRIES;
    }

    @Override
    protected String uniqueValue(CountriesExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Countries entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Countries entity) {
        return entity.getId() == null ? null : entity.getId().longValue();
    }

    @Override
    protected void assignId(Countries entity, Long id) {
        entity.setId(id == null ? null : id.intValue());
    }

    @Override
    protected CountriesExcelRow toRow(Countries entity) {
        return CountriesExcelRow.from(entity);
    }
}
