package com.minecraft.excel.handler;

import com.minecraft.entity.Announcement;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.AnnouncementExcelRow;
import com.minecraft.service.AnnouncementService;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementExcelHandler extends AbstractExcelBizHandler<AnnouncementExcelRow, Announcement> {

    public AnnouncementExcelHandler(AnnouncementService service) {
        super(service, AnnouncementExcelRow.class, "公告");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.ANNOUNCEMENT;
    }

    @Override
    protected String uniqueColumn() {
        return "title";
    }

    @Override
    protected String uniqueValue(AnnouncementExcelRow row) {
        return row.getTitle();
    }

    @Override
    protected String uniqueValue(Announcement entity) {
        return entity.getTitle();
    }

    @Override
    protected Long idOf(Announcement entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(Announcement entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected AnnouncementExcelRow toRow(Announcement entity) {
        return AnnouncementExcelRow.from(entity);
    }
}
