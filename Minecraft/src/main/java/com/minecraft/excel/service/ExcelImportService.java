package com.minecraft.excel.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisStopException;
import com.minecraft.excel.core.ExcelBizHandler;
import com.minecraft.excel.core.ExcelBizHandlerRegistry;
import com.minecraft.excel.core.ExcelImportRow;
import com.minecraft.excel.core.ExcelTaskContext;
import com.minecraft.excel.core.GenericImportListener;
import com.minecraft.excel.core.ImportRow;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final ExcelTaskService taskService;
    private final ExcelBizHandlerRegistry handlerRegistry;
    private final StringRedisTemplate redisTemplate;

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    /**
     * 请求结束前先把上传文件落盘，异步线程再读取，避免临时文件被容器清理。
     */
    public Path saveTempFile(MultipartFile file, String taskNo) {
        String originalName = file.getOriginalFilename();
        String ext = "xlsx";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        try {
            Path dir = Paths.get(uploadPath, "excel", "temp");
            Files.createDirectories(dir);
            Path target = dir.resolve(taskNo + "." + ext);
            file.transferTo(target.toFile());
            return target;
        } catch (IOException e) {
            throw new BusinessException("保存上传文件失败");
        }
    }

    @Async("excelExecutor")
    @SuppressWarnings("unchecked")
    public void doImportAsync(String taskNo, ExcelBizType bizType, Path filePath, Map<String, Object> params) {
        ExcelTaskContext ctx = new ExcelTaskContext(taskNo, taskService, redisTemplate);
        try {
            taskService.markProcessing(taskNo);
            ctx.reportProgress();

            ExcelBizHandler<?> handler = handlerRegistry.get(bizType);
            ExcelBizHandler<ImportRow> typedHandler = (ExcelBizHandler<ImportRow>) handler;
            Class<? extends ImportRow> rowClass = handler.rowClass();

            try (InputStream in = Files.newInputStream(filePath)) {
                EasyExcel.read(in, rowClass, new GenericImportListener<>(ctx,
                                (rows, context) -> typedHandler.importBatch(rows, context, params)))
                        .sheet(0)
                        .doRead();
            }

            if (ctx.isCancelled()) {
                ctx.cancelFinish();
            } else {
                ctx.finish();
            }
        } catch (ExcelAnalysisStopException e) {
            ctx.cancelFinish();
        } catch (Exception e) {
            log.error("Excel导入任务执行失败 taskNo={}", taskNo, e);
            ctx.flushDetails();
            taskService.markFailed(taskNo, e.getMessage());
        } finally {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("删除临时文件失败 {}", filePath, e);
            }
        }
    }
}
