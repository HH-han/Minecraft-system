package com.minecraft.excel.core;

import com.minecraft.excel.enums.ExcelBizType;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 业务实体的 Excel 处理扩展点：新增实体时实现本接口即可接入导入导出。
 */
public interface ExcelBizHandler<T extends ImportRow> {

    ExcelBizType bizType();

    Class<T> rowClass();

    String sheetName();

    /**
     * 批量入库，返回成功处理条数（含按策略跳过的数据）。
     */
    int importBatch(List<T> rows, ExcelTaskContext ctx, Map<String, Object> params);

    /**
     * 输出导入模板（仅表头）。
     */
    void writeTemplate(OutputStream out);

    /**
     * 分页流式导出到输出流。
     */
    void exportData(OutputStream out, Map<String, Object> query);
}
