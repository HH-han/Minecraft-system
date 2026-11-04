package com.minecraft.excel.core;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelAnalysisStopException;
import com.alibaba.excel.event.AnalysisEventListener;
import com.minecraft.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 通用流式导入监听器：行级校验 -> 攒批入库 -> 进度上报 -> 失败明细。
 */
@Slf4j
public class GenericImportListener<T extends ImportRow> extends AnalysisEventListener<T> {

    private static final int BATCH_SIZE = 500;

    private final ExcelTaskContext ctx;
    private final BiFunction<List<T>, ExcelTaskContext, Integer> batchHandler;
    private final List<T> buffer = new ArrayList<>(BATCH_SIZE);

    public GenericImportListener(ExcelTaskContext ctx, BiFunction<List<T>, ExcelTaskContext, Integer> batchHandler) {
        this.ctx = ctx;
        this.batchHandler = batchHandler;
    }

    @Override
    public void invoke(T row, AnalysisContext context) {
        if (ctx.isCancelled()) {
            throw new ExcelAnalysisStopException();
        }
        int rowIndex = context.readRowHolder().getRowIndex() + 1;
        ctx.increaseTotal();
        row.setRowIndex(rowIndex);

        List<String> errors = row.validate();
        if (errors != null && !errors.isEmpty()) {
            ctx.recordFail(rowIndex, row, String.join(";", errors));
            return;
        }

        buffer.add(row);
        if (buffer.size() >= BATCH_SIZE) {
            flush();
        }
        if (ctx.shouldReport(ctx.getTotal())) {
            ctx.reportProgress();
        }
    }

    private void flush() {
        if (buffer.isEmpty()) {
            return;
        }
        try {
            Integer saved = batchHandler.apply(new ArrayList<>(buffer), ctx);
            ctx.increaseSuccess(saved == null ? buffer.size() : saved);
        } catch (Exception e) {
            log.error("批量入库失败 taskNo={}", ctx.getTaskNo(), e);
            String message = e instanceof BusinessException ? e.getMessage() : "入库失败：" + e.getMessage();
            for (T row : buffer) {
                ctx.recordFail(row.getRowIndex(), row, message);
            }
        } finally {
            buffer.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        flush();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        if (exception instanceof ExcelAnalysisStopException) {
            return;
        }
        int rowIndex = context.readRowHolder() == null ? 0 : context.readRowHolder().getRowIndex() + 1;
        log.warn("Excel行解析异常 taskNo={} row={}", ctx.getTaskNo(), rowIndex, exception);
        ctx.recordFail(rowIndex, null, "解析异常：" + exception.getMessage());
    }
}
