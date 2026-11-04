package com.minecraft.excel.service;

import com.alibaba.fastjson2.JSON;
import com.minecraft.excel.core.ExcelBizHandler;
import com.minecraft.excel.core.ExcelBizHandlerRegistry;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.enums.ExcelTaskType;
import com.minecraft.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ExcelBizHandlerRegistry handlerRegistry;
    private final ExcelTaskService taskService;

    public void writeTemplate(HttpServletResponse response, ExcelBizType bizType) {
        ExcelBizHandler<?> handler = handlerRegistry.get(bizType);
        setExcelHeaders(response, bizType.getDesc() + "_导入模板");
        try {
            handler.writeTemplate(response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException("模板下载失败：" + e.getMessage());
        }
    }

    public void export(HttpServletResponse response, ExcelBizType bizType, Map<String, Object> query, Long operatorId) {
        ExcelBizHandler<?> handler = handlerRegistry.get(bizType);
        String fileName = bizType.getDesc() + "_导出_" + LocalDate.now().format(DATE_FORMAT) + ".xlsx";
        String paramsJson = query == null || query.isEmpty() ? null : JSON.toJSONString(query);
        String taskNo = taskService.createTask(bizType, ExcelTaskType.EXPORT, fileName, null, 0L, operatorId, paramsJson);
        taskService.markProcessing(taskNo);
        setExcelHeaders(response, fileName);
        try (OutputStream out = response.getOutputStream()) {
            handler.exportData(out, query);
            out.flush();
            taskService.markExportSuccess(taskNo);
        } catch (IOException e) {
            log.error("Excel导出失败 taskNo={}", taskNo, e);
            taskService.markFailed(taskNo, "写出文件失败：" + e.getMessage());
            throw new BusinessException("导出失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Excel导出失败 taskNo={}", taskNo, e);
            taskService.markFailed(taskNo, e.getMessage());
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }

    private void setExcelHeaders(HttpServletResponse response, String baseName) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        String encoded = URLEncoder.encode(baseName + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encoded);
    }
}
