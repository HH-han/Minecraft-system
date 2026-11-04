package com.minecraft.excel.core;

import com.alibaba.excel.annotation.ExcelIgnore;

/**
 * 导入行基类，框架写入行号用于失败明细定位。
 */
public abstract class ExcelImportRow implements ImportRow {

    @ExcelIgnore
    private int rowIndex;

    @Override
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }
}
