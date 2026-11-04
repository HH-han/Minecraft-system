package com.minecraft.excel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.entity.ExcelTask;
import com.minecraft.entity.ExcelTaskDetail;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.enums.ExcelTaskType;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface ExcelTaskService {

    String createTask(ExcelBizType bizType, ExcelTaskType taskType, String fileName, String filePath,
                      Long fileSize, Long operatorId, String paramsJson);

    ExcelTask getByTaskNo(String taskNo);

    void markProcessing(String taskNo);

    void finishTask(String taskNo, int total, int success, int fail);

    void markFailed(String taskNo, String message);

    void markCancelled(String taskNo, int total, int success, int fail);

    void markExportSuccess(String taskNo);

    void saveDetails(List<ExcelTaskDetail> details);

    Map<String, Object> getProgress(String taskNo);

    Page<ExcelTask> pageTasks(int pageNum, int pageSize, String bizType, Integer status);

    void cancel(String taskNo);

    String writeErrorFile(String taskNo);

    Path resolveErrorFile(String taskNo);
}
