package com.minecraft.excel.handler;

import com.minecraft.entity.RailwayStation;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.RailwayStationExcelRow;
import com.minecraft.service.RailwayStationService;
import org.springframework.stereotype.Component;

@Component
public class RailwayStationExcelHandler extends AbstractExcelBizHandler<RailwayStationExcelRow, RailwayStation> {

    public RailwayStationExcelHandler(RailwayStationService service) {
        super(service, RailwayStationExcelRow.class, "火车站点");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.RAILWAY_STATION;
    }

    @Override
    protected String uniqueColumn() {
        return "station_name";
    }

    @Override
    protected String uniqueValue(RailwayStationExcelRow row) {
        return row.getStationName();
    }

    @Override
    protected String uniqueValue(RailwayStation entity) {
        return entity.getStationName();
    }

    @Override
    protected Long idOf(RailwayStation entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(RailwayStation entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected RailwayStationExcelRow toRow(RailwayStation entity) {
        return RailwayStationExcelRow.from(entity);
    }
}
