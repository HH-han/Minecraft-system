package com.minecraft.excel.handler;

import com.minecraft.entity.Hotel;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.HotelExcelRow;
import com.minecraft.service.HotelService;
import org.springframework.stereotype.Component;

@Component
public class HotelExcelHandler extends AbstractExcelBizHandler<HotelExcelRow, Hotel> {

    public HotelExcelHandler(HotelService service) {
        super(service, HotelExcelRow.class, "酒店");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.HOTEL;
    }

    @Override
    protected String uniqueValue(HotelExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Hotel entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Hotel entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(Hotel entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected HotelExcelRow toRow(Hotel entity) {
        return HotelExcelRow.from(entity);
    }
}
