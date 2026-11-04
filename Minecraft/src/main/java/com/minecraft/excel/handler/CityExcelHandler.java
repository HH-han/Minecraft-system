package com.minecraft.excel.handler;

import com.minecraft.entity.Cities;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.CityExcelRow;
import com.minecraft.service.CitiesService;
import org.springframework.stereotype.Component;

@Component
public class CityExcelHandler extends AbstractExcelBizHandler<CityExcelRow, Cities> {

    public CityExcelHandler(CitiesService service) {
        super(service, CityExcelRow.class, "城市");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.CITY;
    }

    @Override
    protected String uniqueValue(CityExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Cities entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Cities entity) {
        return entity.getId() == null ? null : entity.getId().longValue();
    }

    @Override
    protected void assignId(Cities entity, Long id) {
        entity.setId(id == null ? null : id.intValue());
    }

    @Override
    protected CityExcelRow toRow(Cities entity) {
        return CityExcelRow.from(entity);
    }
}
