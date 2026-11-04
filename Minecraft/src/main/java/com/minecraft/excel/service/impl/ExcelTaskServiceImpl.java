package com.minecraft.excel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.entity.ExcelTask;
import com.minecraft.entity.ExcelTaskDetail;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.enums.ExcelTaskStatus;
import com.minecraft.excel.enums.ExcelTaskType;
import com.minecraft.excel.model.ExcelErrorRow;
import com.minecraft.excel.service.ExcelTaskService;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.ExcelTaskDetailMapper;
import com.minecraft.mapper.ExcelTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelTaskServiceImpl implements ExcelTaskService {

    private static final String PROGRESS_KEY = "excel:task:";
    private static final String CANCEL_KEY = "excel:cancel:";

    private final ExcelTaskMapper taskMapper;
    private final ExcelTaskDetailMapper detailMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Override
    public String createTask(ExcelBizType bizType, ExcelTaskType taskType, String fileName, String filePath,
                             Long fileSize, Long operatorId, String paramsJson) {
        String taskNo = UUID.randomUUID().toString().replace("-", "");
        ExcelTask task = new ExcelTask();
        task.setTaskNo(taskNo);
        task.setBizType(bizType.getCode());
        task.setTaskType(taskType.getCode());
        task.setFileName(fileName);
        task.setFilePath(filePath);
        task.setFileSize(fileSize == null ? 0L : fileSize);
        task.setTotalRows(0);
        task.setSuccessRows(0);
        task.setFailRows(0);
        task.setStatus(ExcelTaskStatus.PENDING.getCode());
        task.setParams(paramsJson);
        task.setOperatorId(operatorId);
        taskMapper.insert(task);
        return taskNo;
    }

    @Override
    public ExcelTask getByTaskNo(String taskNo) {
        ExcelTask task = taskMapper.selectOne(new LambdaQueryWrapper<ExcelTask>()
                .eq(ExcelTask::getTaskNo, taskNo));
        if (task == null) {
            throw new BusinessException("任务不存在：" + taskNo);
        }
        return task;
    }

    @Override
    public void markProcessing(String taskNo) {
        update(taskNo, wrapper -> wrapper
                .set(ExcelTask::getStatus, ExcelTaskStatus.PROCESSING.getCode())
                .set(ExcelTask::getStartTime, LocalDateTime.now()));
    }

    @Override
    public void finishTask(String taskNo, int total, int success, int fail) {
        int status;
        if (fail == 0) {
            status = ExcelTaskStatus.SUCCESS.getCode();
        } else if (success > 0) {
            status = ExcelTaskStatus.PARTIAL_FAIL.getCode();
        } else {
            status = ExcelTaskStatus.FAILED.getCode();
        }
        int finalStatus = status;
        update(taskNo, wrapper -> wrapper
                .set(ExcelTask::getTotalRows, total)
                .set(ExcelTask::getSuccessRows, success)
                .set(ExcelTask::getFailRows, fail)
                .set(ExcelTask::getStatus, finalStatus)
                .set(ExcelTask::getEndTime, LocalDateTime.now()));
        pushProgress(taskNo, total, success, fail, finalStatus);
        redisTemplate.delete(CANCEL_KEY + taskNo);
    }

    @Override
    public void markFailed(String taskNo, String message) {
        String msg = message == null ? "导入失败" : message;
        if (msg.length() > 2000) {
            msg = msg.substring(0, 2000);
        }
        String finalMsg = msg;
        update(taskNo, wrapper -> wrapper
                .set(ExcelTask::getStatus, ExcelTaskStatus.FAILED.getCode())
                .set(ExcelTask::getErrorMsg, finalMsg)
                .set(ExcelTask::getEndTime, LocalDateTime.now()));
        pushProgress(taskNo, 0, 0, 0, ExcelTaskStatus.FAILED.getCode());
        redisTemplate.delete(CANCEL_KEY + taskNo);
    }

    @Override
    public void markCancelled(String taskNo, int total, int success, int fail) {
        update(taskNo, wrapper -> wrapper
                .set(ExcelTask::getTotalRows, total)
                .set(ExcelTask::getSuccessRows, success)
                .set(ExcelTask::getFailRows, fail)
                .set(ExcelTask::getStatus, ExcelTaskStatus.CANCELLED.getCode())
                .set(ExcelTask::getEndTime, LocalDateTime.now()));
        pushProgress(taskNo, total, success, fail, ExcelTaskStatus.CANCELLED.getCode());
        redisTemplate.delete(CANCEL_KEY + taskNo);
    }

    @Override
    public void markExportSuccess(String taskNo) {
        update(taskNo, wrapper -> wrapper
                .set(ExcelTask::getStatus, ExcelTaskStatus.SUCCESS.getCode())
                .set(ExcelTask::getStartTime, LocalDateTime.now())
                .set(ExcelTask::getEndTime, LocalDateTime.now()));
    }

    @Override
    public void saveDetails(List<ExcelTaskDetail> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        for (ExcelTaskDetail detail : details) {
            detailMapper.insert(detail);
        }
    }

    @Override
    public Map<String, Object> getProgress(String taskNo) {
        Map<Object, Object> hash = redisTemplate.opsForHash().entries(PROGRESS_KEY + taskNo);
        Map<String, Object> progress = new HashMap<>();
        if (!hash.isEmpty()) {
            progress.put("total", parseInt(hash.get("total")));
            progress.put("success", parseInt(hash.get("success")));
            progress.put("fail", parseInt(hash.get("fail")));
            progress.put("status", parseInt(hash.get("status")));
            progress.put("percent", parseInt(hash.get("percent")));
        }
        ExcelTask task = getByTaskNo(taskNo);
        progress.putIfAbsent("total", task.getTotalRows() == null ? 0 : task.getTotalRows());
        progress.putIfAbsent("success", task.getSuccessRows() == null ? 0 : task.getSuccessRows());
        progress.putIfAbsent("fail", task.getFailRows() == null ? 0 : task.getFailRows());
        progress.putIfAbsent("status", task.getStatus() == null ? 0 : task.getStatus());
        int total = (int) progress.get("total");
        int done = (int) progress.get("success") + (int) progress.get("fail");
        progress.putIfAbsent("percent", total == 0 ? 0 : Math.min(100, done * 100 / total));
        progress.put("taskNo", task.getTaskNo());
        progress.put("bizType", task.getBizType());
        progress.put("taskType", task.getTaskType());
        progress.put("fileName", task.getFileName());
        progress.put("errorMsg", task.getErrorMsg());
        progress.put("errorFile", task.getErrorFile() != null);
        return progress;
    }

    @Override
    public Page<ExcelTask> pageTasks(int pageNum, int pageSize, String bizType, Integer status) {
        LambdaQueryWrapper<ExcelTask> wrapper = new LambdaQueryWrapper<ExcelTask>()
                .eq(!isBlank(bizType), ExcelTask::getBizType, bizType)
                .eq(status != null, ExcelTask::getStatus, status)
                .orderByDesc(ExcelTask::getCreateTime);
        return taskMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void cancel(String taskNo) {
        ExcelTask task = getByTaskNo(taskNo);
        if (task.getStatus() != null && task.getStatus() >= ExcelTaskStatus.SUCCESS.getCode()) {
            throw new BusinessException("任务已结束，无法取消");
        }
        if (task.getStatus() != null && task.getStatus() == ExcelTaskStatus.PENDING.getCode()) {
            markCancelled(taskNo, 0, 0, 0);
            return;
        }
        redisTemplate.opsForValue().set(CANCEL_KEY + taskNo, "1", Duration.ofHours(1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String writeErrorFile(String taskNo) {
        List<ExcelTaskDetail> details = detailMapper.selectList(new LambdaQueryWrapper<ExcelTaskDetail>()
                .eq(ExcelTaskDetail::getTaskNo, taskNo)
                .orderByAsc(ExcelTaskDetail::getRowIndex));
        if (details.isEmpty()) {
            return null;
        }
        try {
            Path dir = Paths.get(uploadPath, "excel", "errors");
            Files.createDirectories(dir);
            Path file = dir.resolve("errors_" + taskNo + ".xlsx");
            List<ExcelErrorRow> rows = new ArrayList<>(details.size());
            for (ExcelTaskDetail detail : details) {
                ExcelErrorRow row = new ExcelErrorRow();
                row.setRowIndex(detail.getRowIndex());
                row.setErrorField(detail.getErrorField());
                row.setErrorMsg(detail.getErrorMsg());
                row.setRowData(detail.getRowData());
                rows.add(row);
            }
            EasyExcel.write(file.toFile(), ExcelErrorRow.class).sheet("错误明细").doWrite(rows);
            String absolutePath = file.toAbsolutePath().toString();
            update(taskNo, wrapper -> wrapper.set(ExcelTask::getErrorFile, absolutePath));
            return absolutePath;
        } catch (IOException e) {
            log.error("生成失败明细文件失败 taskNo={}", taskNo, e);
            return null;
        }
    }

    @Override
    public Path resolveErrorFile(String taskNo) {
        ExcelTask task = getByTaskNo(taskNo);
        if (isBlank(task.getErrorFile())) {
            return null;
        }
        Path path = Paths.get(task.getErrorFile());
        return Files.exists(path) ? path : null;
    }

    private void update(String taskNo, java.util.function.Consumer<LambdaUpdateWrapper<ExcelTask>> consumer) {
        LambdaUpdateWrapper<ExcelTask> wrapper = new LambdaUpdateWrapper<ExcelTask>()
                .eq(ExcelTask::getTaskNo, taskNo);
        consumer.accept(wrapper);
        taskMapper.update(null, wrapper);
    }

    private void pushProgress(String taskNo, int total, int success, int fail, int status) {
        int done = success + fail;
        int percent = total == 0 ? 0 : Math.min(100, done * 100 / total);
        Map<String, String> map = new HashMap<>();
        map.put("total", String.valueOf(total));
        map.put("success", String.valueOf(success));
        map.put("fail", String.valueOf(fail));
        map.put("status", String.valueOf(status));
        map.put("percent", String.valueOf(percent));
        redisTemplate.opsForHash().putAll(PROGRESS_KEY + taskNo, map);
        redisTemplate.expire(PROGRESS_KEY + taskNo, Duration.ofHours(24));
    }

    private int parseInt(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
