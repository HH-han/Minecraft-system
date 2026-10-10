package com.minecraft.exception;

import com.minecraft.common.exception.RateLimitException;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.service.SystemLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private SystemLogService systemLogService;

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型";
        String value = e.getValue() != null ? e.getValue().toString() : "null";
        String message = String.format("参数 '%s' 类型错误：期望 %s 类型，但接收到值 '%s'", paramName, requiredType, value);
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(BindException.class)
    public ApiResponse<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.error(400, e.getMessage());
    }

    @ExceptionHandler(RateLimitException.class)
    public ApiResponse<?> handleRateLimitException(RateLimitException e) {
        systemLogService.recordError("安全", "接口限流", "触发接口限流：" + e.getMessage(), e);
        return ApiResponse.error(429, e.getMessage());
    }

    /**
     * SSE 长连接（如 /announcement/sse）30 分钟超时。
     * 此时响应 Content-Type 已预设为 text/event-stream，无法再序列化 ApiResponse JSON，
     * 若落入通用 handleException 会触发 HttpMessageNotWritableException 二次异常并产生垃圾错误日志。
     * 这里只打一行 WARN 并以 503 结束响应，不写 body、不落库。
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletResponse response) {
        log.warn("SSE 长连接超时断开，等待前端自动重连");
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        log.error("系统异常", e);
        // 记录系统报错日志（异步落库 system_log 表，含错误堆栈）
        systemLogService.recordError("全局", "系统异常", "系统异常：" + e.getMessage(), e);
        return ApiResponse.error(500, "系统异常：" + e.getMessage());
    }
}
