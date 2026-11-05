package com.minecraft.excel.handler;

import com.minecraft.entity.Tags;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.TagsExcelRow;
import com.minecraft.service.TagsService;
import org.springframework.stereotype.Component;

@Component
public class TagsExcelHandler extends AbstractExcelBizHandler<TagsExcelRow, Tags> {

    public TagsExcelHandler(TagsService service) {
        super(service, TagsExcelRow.class, "标签");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.TAGS;
    }

    @Override
    protected String uniqueValue(TagsExcelRow row) {
        return row.getName();
    }

    @Override
    protected String uniqueValue(Tags entity) {
        return entity.getName();
    }

    @Override
    protected Long idOf(Tags entity) {
        return entity.getId() == null ? null : entity.getId().longValue();
    }

    @Override
    protected void assignId(Tags entity, Long id) {
        entity.setId(id == null ? null : id.intValue());
    }

    @Override
    protected TagsExcelRow toRow(Tags entity) {
        return TagsExcelRow.from(entity);
    }
}
