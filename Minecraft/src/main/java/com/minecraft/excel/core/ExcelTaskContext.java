package com.minecraft.excel.core;

import com.alibaba.fastjson2.JSON;
import com.minecraft.entity.ExcelTaskDetail;
import com.minecraft.excel.service.ExcelTaskService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 单次导入任务上下文：累计进度、收集失败明细、响应取消指令。
 */
@Slf4j
@Getter
public class ExcelTaskContext {

    private static final String PROGRESS_KEY = "excel:task:";
    private static final String CANCEL_KEY = "excel:cancel:";
    private static final int DETAIL_FLUSH_SIZE = 200;
    private static final int PROGRESS_STEP = 100;

    private final String taskNo;
    private final ExcelTaskService taskService;
    private final StringRedisTemplate redisTemplate;

    private final AtomicInteger total = new AtomicInteger(0);
    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger fail = new AtomicInteger(0);

    private final List<ExcelTaskDetail> details = new ArrayList<>(DETAIL_FLUSH_SIZE);
    private volatile boolean cancelled = false;

    public ExcelTaskContext(String taskNo, ExcelTaskService taskService, StringRedisTemplate redisTemplate) {
        this.taskNo = taskNo;
        this.taskService = taskService;
        this.redisTemplate = redisTemplate;
    }

    public int increaseTotal() {
        return total.incrementAndGet();
    }

    public int getTotal() {
        return total.get();
    }

    public int getSuccess() {
        return success.get();
    }

    public int getFail() {
        return fail.get();
    }

    public void increaseSuccess(int count) {
        success.addAndGet(count);
    }

    public void recordFail(int rowIndex, Object row, String message) {
        fail.incrementAndGet();
        ExcelTaskDetail detail = new ExcelTaskDetail();
        detail.setTaskNo(taskNo);
        detail.setRowIndex(rowIndex);
        detail.setErrorMsg(message == null ? "未知错误" : truncate(message, 512));
        if (row != null) {
            try {
                detail.setRowData(truncate(JSON.toJSONString(row), 2000));
            } catch (Exception e) {
                detail.setRowData(String.valueOf(row));
            }
        }
        synchronized (details) {
            details.add(detail);
            if (details.size() >= DETAIL_FLUSH_SIZE) {
                flushDetails();
            }
        }
    }

    public synchronized void flushDetails() {
        if (details.isEmpty()) {
            return;
        }
        try {
            taskService.saveDetails(new ArrayList<>(details));
        } catch (Exception e) {
            log.error("写入失败明细异常 taskNo={}", taskNo, e);
        } finally {
            details.clear();
        }
    }

    public void reportProgress() {
        Map<String, String> map = new HashMap<>();
        int done = success.get() + fail.get();
        int percent = total.get() == 0 ? 0 : Math.min(100, done * 100 / total.get());
        map.put("total", String.valueOf(total.get()));
        map.put("success", String.valueOf(success.get()));
        map.put("fail", String.valueOf(fail.get()));
        map.put("status", String.valueOf(1));
        map.put("percent", String.valueOf(percent));
        try {
            redisTemplate.opsForHash().putAll(PROGRESS_KEY + taskNo, map);
            redisTemplate.expire(PROGRESS_KEY + taskNo, Duration.ofHours(24));
        } catch (Exception e) {
            log.warn("写入任务进度缓存失败 taskNo={}", taskNo, e);
        }
    }

    public boolean isCancelled() {
        if (cancelled) {
            return true;
        }
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(CANCEL_KEY + taskNo))) {
                cancelled = true;
            }
        } catch (Exception e) {
            log.warn("读取取消标记失败 taskNo={}", taskNo, e);
        }
        return cancelled;
    }

    public boolean shouldReport(int totalRows) {
        return totalRows % PROGRESS_STEP == 0;
    }

    public void finish() {
        flushDetails();
        taskService.finishTask(taskNo, total.get(), success.get(), fail.get());
        if (fail.get() > 0) {
            try {
                taskService.writeErrorFile(taskNo);
            } catch (Exception e) {
                log.error("生成失败明细文件异常 taskNo={}", taskNo, e);
            }
        }
    }

    public void cancelFinish() {
        flushDetails();
        taskService.markCancelled(taskNo, total.get(), success.get(), fail.get());
    }

    private String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max);
    }
}
