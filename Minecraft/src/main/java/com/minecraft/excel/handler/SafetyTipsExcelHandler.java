package com.minecraft.excel.handler;

import com.minecraft.entity.SafetyTips;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.SafetyTipsExcelRow;
import com.minecraft.service.SafetyTipsService;
import org.springframework.stereotype.Component;

@Component
public class SafetyTipsExcelHandler extends AbstractExcelBizHandler<SafetyTipsExcelRow, SafetyTips> {

    public SafetyTipsExcelHandler(SafetyTipsService service) {
        super(service, SafetyTipsExcelRow.class, "安全提示");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.SAFETY_TIPS;
    }

    @Override
    protected String uniqueColumn() {
        return "title";
    }

    @Override
    protected String uniqueValue(SafetyTipsExcelRow row) {
        return row.getTitle();
    }

    @Override
    protected String uniqueValue(SafetyTips entity) {
        return entity.getTitle();
    }

    @Override
    protected Long idOf(SafetyTips entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(SafetyTips entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected SafetyTipsExcelRow toRow(SafetyTips entity) {
        return SafetyTipsExcelRow.from(entity);
    }
}
