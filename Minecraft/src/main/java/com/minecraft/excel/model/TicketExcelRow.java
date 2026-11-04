package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Ticket;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class TicketExcelRow extends ExcelImportRow {

    @ExcelProperty("票务类型")
    private String type;

    @ExcelProperty("出发城市")
    private String departureCity;

    @ExcelProperty("到达城市")
    private String arrivalCity;

    @ExcelProperty("出发时间")
    private String departureTime;

    @ExcelProperty("到达时间")
    private String arrivalTime;

    @ExcelProperty("承运方")
    private String carrier;

    @ExcelProperty("价格")
    private BigDecimal price;

    @ExcelProperty("库存")
    private Integer stock;

    @ExcelProperty("舱位/座位等级")
    private String seatClass;

    @ExcelProperty("状态(0下架/1上架)")
    private Integer status;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (type == null || type.isBlank()) {
            errors.add("票务类型不能为空");
        }
        if (departureCity == null || departureCity.isBlank()) {
            errors.add("出发城市不能为空");
        }
        if (arrivalCity == null || arrivalCity.isBlank()) {
            errors.add("到达城市不能为空");
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("价格不能为负数");
        }
        if (stock != null && stock < 0) {
            errors.add("库存不能为负数");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Ticket entity = new Ticket();
        entity.setType(type);
        entity.setDepartureCity(departureCity);
        entity.setArrivalCity(arrivalCity);
        entity.setDepartureTime(departureTime);
        entity.setArrivalTime(arrivalTime);
        entity.setCarrier(carrier);
        entity.setPrice(price);
        entity.setStock(stock == null ? 0 : stock);
        entity.setSeatClass(seatClass);
        entity.setStatus(status == null ? 1 : status);
        return entity;
    }

    public static TicketExcelRow from(Ticket entity) {
        TicketExcelRow row = new TicketExcelRow();
        row.setType(entity.getType());
        row.setDepartureCity(entity.getDepartureCity());
        row.setArrivalCity(entity.getArrivalCity());
        row.setDepartureTime(entity.getDepartureTime());
        row.setArrivalTime(entity.getArrivalTime());
        row.setCarrier(entity.getCarrier());
        row.setPrice(entity.getPrice());
        row.setStock(entity.getStock());
        row.setSeatClass(entity.getSeatClass());
        row.setStatus(entity.getStatus());
        return row;
    }
}
