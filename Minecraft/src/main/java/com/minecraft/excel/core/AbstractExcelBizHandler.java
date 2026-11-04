package com.minecraft.excel.core;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.excel.enums.DuplicateStrategy;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 通用导入导出处理器：分页读取、批量入库、重复策略、模板输出。
 */
public abstract class AbstractExcelBizHandler<T extends ExcelImportRow, E> implements ExcelBizHandler<T> {

    private static final int EXPORT_PAGE_SIZE = 1000;

    private final IService<E> service;
    private final Class<T> rowClass;
    private final String sheetName;

    protected AbstractExcelBizHandler(IService<E> service, Class<T> rowClass, String sheetName) {
        this.service = service;
        this.rowClass = rowClass;
        this.sheetName = sheetName;
    }

    @Override
    public Class<T> rowClass() {
        return rowClass;
    }

    @Override
    public String sheetName() {
        return sheetName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int importBatch(List<T> rows, ExcelTaskContext ctx, Map<String, Object> params) {
        DuplicateStrategy strategy = DuplicateStrategy.of(params == null ? null : params.get("duplicateStrategy"));
        String uniqueColumn = uniqueColumn();
        Map<String, E> existing = new LinkedHashMap<>();
        if (uniqueColumn != null) {
            List<String> keys = rows.stream()
                    .map(this::uniqueValue)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            if (!keys.isEmpty()) {
                List<E> existList = service.list(new QueryWrapper<E>().in(uniqueColumn, keys));
                for (E entity : existList) {
                    existing.putIfAbsent(uniqueValue(entity), entity);
                }
            }
        }

        List<E> toSave = new ArrayList<>(rows.size());
        List<E> toUpdate = new ArrayList<>();
        Set<String> batchKeys = new HashSet<>();
        int failed = 0;
        for (T row : rows) {
            String key = uniqueColumn == null ? null : uniqueValue(row);
            if (key != null) {
                E exist = existing.get(key);
                boolean duplicatedInBatch = !batchKeys.add(key);
                if (exist != null || duplicatedInBatch) {
                    if (strategy == DuplicateStrategy.ERROR) {
                        ctx.recordFail(row.getRowIndex(), row, "数据已存在：" + key);
                        failed++;
                        continue;
                    }
                    if (strategy == DuplicateStrategy.OVERWRITE && exist != null && !duplicatedInBatch) {
                        E entity = (E) row.toEntity();
                        assignId(entity, idOf(exist));
                        toUpdate.add(entity);
                    }
                    continue;
                }
            }
            toSave.add((E) row.toEntity());
        }

        if (!toSave.isEmpty()) {
            service.saveBatch(toSave);
        }
        if (!toUpdate.isEmpty()) {
            service.updateBatchById(toUpdate);
        }
        return rows.size() - failed;
    }

    @Override
    public void writeTemplate(OutputStream out) {
        EasyExcel.write(out, rowClass).sheet(sheetName).doWrite(new ArrayList<T>());
    }

    @Override
    public void exportData(OutputStream out, Map<String, Object> query) {
        ExcelWriter writer = EasyExcel.write(out, rowClass).build();
        WriteSheet sheet = EasyExcel.writerSheet(sheetName).build();
        try {
            Page<E> page = new Page<>(1, EXPORT_PAGE_SIZE);
            while (true) {
                QueryWrapper<E> wrapper = new QueryWrapper<E>().orderByAsc("id");
                String uniqueColumn = uniqueColumn();
                Object keyword = query == null ? null : query.get("keyword");
                if (uniqueColumn != null && keyword != null && !keyword.toString().isBlank()) {
                    wrapper.like(uniqueColumn, keyword.toString().trim());
                }
                Page<E> result = service.page(page, wrapper);
                List<T> rows = new ArrayList<>(result.getRecords().size());
                for (E entity : result.getRecords()) {
                    rows.add(toRow(entity));
                }
                if (!rows.isEmpty()) {
                    writer.write(rows, sheet);
                }
                if (!result.hasNext()) {
                    break;
                }
                page.setCurrent(page.getCurrent() + 1);
            }
        } finally {
            writer.finish();
        }
    }

    /** 业务唯一列，返回 null 表示不做重复校验 */
    protected String uniqueColumn() {
        return "name";
    }

    protected String uniqueValue(T row) {
        return null;
    }

    protected String uniqueValue(E entity) {
        return null;
    }

    protected Long idOf(E entity) {
        return null;
    }

    protected void assignId(E entity, Long id) {
    }

    protected abstract T toRow(E entity);
}
