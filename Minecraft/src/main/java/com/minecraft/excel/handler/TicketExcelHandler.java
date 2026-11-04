package com.minecraft.excel.handler;

import com.minecraft.entity.Ticket;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.TicketExcelRow;
import com.minecraft.service.TicketService;
import org.springframework.stereotype.Component;

@Component
public class TicketExcelHandler extends AbstractExcelBizHandler<TicketExcelRow, Ticket> {

    public TicketExcelHandler(TicketService service) {
        super(service, TicketExcelRow.class, "票务");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.TICKET;
    }

    @Override
    protected String uniqueColumn() {
        return null;
    }

    @Override
    protected TicketExcelRow toRow(Ticket entity) {
        return TicketExcelRow.from(entity);
    }
}
