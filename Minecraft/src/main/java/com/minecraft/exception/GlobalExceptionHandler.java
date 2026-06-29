package com.minecraft.exception;

import com.minecraft.common.exception.RateLimitException;
import com.minecraft.dto.response.ApiResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
        return ApiResponse.error(429, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.error(500, "系统异常：" + e.getMessage());
    }
}
