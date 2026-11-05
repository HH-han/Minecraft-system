package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.RailwayStation;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class RailwayStationExcelRow extends ExcelImportRow {

    @ExcelProperty("站点编码")
    private String stationCode;

    @ExcelProperty("站点名称")
    private String stationName;

    @ExcelProperty("所在城市")
    private String cityName;

    @ExcelProperty("拼音")
    private String pinyin;

    @ExcelProperty("首字母")
    private String initial;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (stationName == null || stationName.isBlank()) {
            errors.add("站点名称不能为空");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        RailwayStation entity = new RailwayStation();
        entity.setStationCode(stationCode);
        entity.setStationName(stationName);
        entity.setCityName(cityName);
        entity.setPinyin(pinyin);
        entity.setInitial(initial);
        return entity;
    }

    public static RailwayStationExcelRow from(RailwayStation entity) {
        RailwayStationExcelRow row = new RailwayStationExcelRow();
        row.setStationCode(entity.getStationCode());
        row.setStationName(entity.getStationName());
        row.setCityName(entity.getCityName());
        row.setPinyin(entity.getPinyin());
        row.setInitial(entity.getInitial());
        return row;
    }
}
