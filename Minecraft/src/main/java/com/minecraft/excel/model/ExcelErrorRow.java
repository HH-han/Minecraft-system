package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(25)
public class ExcelErrorRow {

    @ExcelProperty("行号")
    private Integer rowIndex;

    @ExcelProperty("错误字段")
    private String errorField;

    @ExcelProperty("错误信息")
    private String errorMsg;

    @ExcelProperty("原始数据")
    private String rowData;
}
