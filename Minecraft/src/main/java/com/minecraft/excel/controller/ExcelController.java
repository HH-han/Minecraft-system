package com.minecraft.excel.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraft.annotation.OperationLog;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.ExcelTask;
import com.minecraft.excel.core.ExcelFileValidator;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.enums.ExcelTaskType;
import com.minecraft.excel.service.ExcelExportService;
import com.minecraft.excel.service.ExcelImportService;
import com.minecraft.excel.service.ExcelTaskService;
import com.minecraft.exception.BusinessException;
import com.minecraft.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelImportService importService;
    private final ExcelExportService exportService;
    private final ExcelTaskService taskService;
    private final ObjectMapper objectMapper;

    /**
     * 业务类型字典，供前端下拉使用。
     */
    @GetMapping("/bizTypes")
    public ApiResponse<List<Map<String, Object>>> bizTypes() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ExcelBizType type : ExcelBizType.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", type.getCode());
            item.put("desc", type.getDesc());
            list.add(item);
        }
        return ApiResponse.success(list);
    }

    /**
     * 下载导入模板。
     */
    @GetMapping("/template/{bizType}")
    public void template(@PathVariable String bizType, HttpServletResponse response) {
        exportService.writeTemplate(response, ExcelBizType.of(bizType));
    }

    /**
     * 上传 Excel 并异步导入，返回任务号供前端轮询进度。
     */
    @PostMapping("/import/{bizType}")
    @OperationLog(module = "EXCEL", action = "Excel导入")
    public ApiResponse<Map<String, Object>> importExcel(@PathVariable String bizType,
                                                        @RequestParam("file") MultipartFile file,
                                                        @RequestParam(value = "params", required = false) String params) {
        ExcelBizType type = ExcelBizType.of(bizType);
        ExcelFileValidator.validate(file);
        Map<String, Object> paramMap = parseParams(params);
        Long operatorId = SecurityUtils.getCurrentUserId();
        String taskNo = taskService.createTask(type, ExcelTaskType.IMPORT, file.getOriginalFilename(), null,
                file.getSize(), operatorId, params);
        Path saved = importService.saveTempFile(file, taskNo);
        importService.doImportAsync(taskNo, type, saved, paramMap);

        Map<String, Object> data = new HashMap<>();
        data.put("taskNo", taskNo);
        data.put("bizType", type.getCode());
        return ApiResponse.success("导入任务已提交", data);
    }

    /**
     * 轮询导入进度。
     */
    @GetMapping("/task/{taskNo}/progress")
    public ApiResponse<Map<String, Object>> progress(@PathVariable String taskNo) {
        return ApiResponse.success(taskService.getProgress(taskNo));
    }

    /**
     * 下载失败明细。
     */
    @GetMapping("/task/{taskNo}/error-file")
    public ResponseEntity<FileSystemResource> errorFile(@PathVariable String taskNo) {
        Path file = taskService.resolveErrorFile(taskNo);
        if (file == null) {
            throw new BusinessException("该任务没有可下载的失败明细");
        }
        String encoded = URLEncoder.encode(file.getFileName().toString(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }

    /**
     * 任务列表分页。
     */
    @GetMapping("/task/list")
    public ApiResponse<PageResponse<ExcelTask>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(required = false) String bizType,
                                                     @RequestParam(required = false) Integer status) {
        var page = taskService.pageTasks(pageNum, pageSize, bizType, status);
        return ApiResponse.success(new PageResponse<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /**
     * 取消进行中的导入任务。
     */
    @PostMapping("/task/{taskNo}/cancel")
    @OperationLog(module = "EXCEL", action = "取消Excel任务")
    public ApiResponse<Void> cancel(@PathVariable String taskNo) {
        taskService.cancel(taskNo);
        return ApiResponse.success(null);
    }

    /**
     * 删除已结束的任务（含明细与失败文件）。
     */
    @DeleteMapping("/task/{taskNo}")
    @OperationLog(module = "EXCEL", action = "删除Excel任务")
    public ApiResponse<Void> deleteTask(@PathVariable String taskNo) {
        taskService.deleteTask(taskNo);
        return ApiResponse.success(null);
    }

    /**
     * 同步导出并直接下载（内部记录任务流水）。
     */
    @PostMapping("/export/{bizType}")
    @OperationLog(module = "EXCEL", action = "Excel导出")
    public void export(@PathVariable String bizType,
                       @RequestBody(required = false) Map<String, Object> query,
                       HttpServletResponse response) {
        exportService.export(response, ExcelBizType.of(bizType), query, SecurityUtils.getCurrentUserId());
    }

    private Map<String, Object> parseParams(String params) {
        if (params == null || params.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(params, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new BusinessException("导入参数格式不正确");
        }
    }
}
