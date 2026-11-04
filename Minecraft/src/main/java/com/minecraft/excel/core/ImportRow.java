package com.minecraft.excel.core;

import java.util.List;

/**
 * Excel 导入行通用接口：行级校验 + 转实体。
 */
public interface ImportRow {

    /** 返回错误信息列表，空表示校验通过 */
    List<String> validate();

    /** 转换为业务实体 */
    Object toEntity();

    /** Excel 行号（从 1 开始，含表头），由框架回填 */
    void setRowIndex(int rowIndex);

    int getRowIndex();
}
