package com.minecraft.excel.handler;

import com.minecraft.entity.News;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.NewsExcelRow;
import com.minecraft.service.NewsService;
import org.springframework.stereotype.Component;

@Component
public class NewsExcelHandler extends AbstractExcelBizHandler<NewsExcelRow, News> {

    public NewsExcelHandler(NewsService service) {
        super(service, NewsExcelRow.class, "资讯");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.NEWS;
    }

    @Override
    protected String uniqueColumn() {
        return "title";
    }

    @Override
    protected String uniqueValue(NewsExcelRow row) {
        return row.getTitle();
    }

    @Override
    protected String uniqueValue(News entity) {
        return entity.getTitle();
    }

    @Override
    protected Long idOf(News entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(News entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected NewsExcelRow toRow(News entity) {
        return NewsExcelRow.from(entity);
    }
}
