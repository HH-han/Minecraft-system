package com.minecraft.aspect;

import com.alibaba.fastjson2.JSON;
import com.minecraft.annotation.Log;
import com.minecraft.entity.OperationLog;
import com.minecraft.service.OperationLogService;
import com.minecraft.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 操作日志切面：
 * 1. 所有 Controller 写操作（POST/PUT/DELETE/PATCH）自动记录到 operation_logs 表；
 * 2. 标注 {@link Log} 注解的方法（含查询）按注解描述精确记录；
 * 3. 记录执行时长、操作状态、错误信息、请求参数与响应结果，异步落库不影响主流程。
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final int MAX_RESULT_LENGTH = 500;

    private final OperationLogService operationLogService;

    public LogAspect(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Pointcut("execution(* com.minecraft.controller..*(..))")
    public void controllerPointcut() {
    }

    @Pointcut("@annotation(com.minecraft.annotation.Log)")
    public void logAnnotationPointcut() {
    }

    @Around("controllerPointcut() || logAnnotationPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        HttpServletRequest request = getRequest();

        // GET 请求且无 @Log 注解时不记录，避免查询日志量过大
        boolean shouldLog = logAnnotation != null
                || (request != null && !"GET".equalsIgnoreCase(request.getMethod())
                && !"OPTIONS".equalsIgnoreCase(request.getMethod()));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            if (shouldLog) {
                saveLog(joinPoint, method, logAnnotation, request,
                        System.currentTimeMillis() - beginTime, null, e);
            }
            throw e;
        }

        if (shouldLog) {
            saveLog(joinPoint, method, logAnnotation, request,
                    System.currentTimeMillis() - beginTime, result, null);
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Method method, Log logAnnotation,
                         HttpServletRequest request, long executionTime, Object result, Throwable e) {
        try {
            OperationLog operationLog = new OperationLog();

            // 操作用户信息
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId != null) {
                operationLog.setUserId(userId.intValue());
            }
            operationLog.setUsername(SecurityUtils.getCurrentUsername());

            // 请求信息
            if (request != null) {
                operationLog.setIpAddress(getClientIp(request));
                operationLog.setUserAgent(request.getHeader("User-Agent"));
                operationLog.setRequestUri(request.getRequestURI());
                operationLog.setRequestMethod(request.getMethod());
                operationLog.setBrowser(parseBrowser(request.getHeader("User-Agent")));
                operationLog.setOs(parseOs(request.getHeader("User-Agent")));
            }

            // @Log 注解信息优先，否则按 HTTP 方法自动归类
            if (logAnnotation != null) {
                operationLog.setOperationType(logAnnotation.operationType().name());
                operationLog.setOperationDesc(logAnnotation.value());
                operationLog.setModule(logAnnotation.module());
                if (logAnnotation.recordParams()) {
                    operationLog.setRequestParams(serializeParams(joinPoint));
                }
                if (logAnnotation.recordResult() && result != null) {
                    operationLog.setResponseResult(truncate(JSON.toJSONString(result)));
                }
            } else {
                operationLog.setOperationType(resolveOperationType(request));
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                operationLog.setOperationDesc(signature.getDeclaringType().getSimpleName()
                        + "." + method.getName());
            }

            // 未通过注解记录参数时统一记录请求参数
            if (operationLog.getRequestParams() == null) {
                operationLog.setRequestParams(serializeParams(joinPoint));
            }

            operationLog.setExecutionTime(executionTime);

            // 操作状态与错误信息
            if (e != null) {
                operationLog.setStatus(0);
                operationLog.setErrorMsg(e.getMessage());
            } else {
                operationLog.setStatus(1);
            }

            // 异步落库，不影响主流程
            operationLogService.saveOperationLogAsync(operationLog);
        } catch (Exception ex) {
            logger.error("保存操作日志失败", ex);
        }
    }

    /**
     * 序列化请求参数，过滤不可序列化的 Servlet/上传对象
     */
    private String serializeParams(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> !(arg instanceof HttpServletRequest)
                            && !(arg instanceof HttpServletResponse)
                            && !(arg instanceof MultipartFile)
                            && !(arg instanceof org.springframework.validation.BindingResult))
                    .toArray();
            String params = JSON.toJSONString(args);
            return truncate(params);
        } catch (Exception e) {
            return truncate(Arrays.toString(joinPoint.getArgs()));
        }
    }

    private String truncate(String text) {
        if (text == null) {
            return null;
        }
        return text.length() > MAX_RESULT_LENGTH ? text.substring(0, MAX_RESULT_LENGTH) + "..." : text;
    }

    /**
     * 无注解时按 HTTP 方法归类操作类型
     */
    private String resolveOperationType(HttpServletRequest request) {
        if (request == null) {
            return com.minecraft.enums.OperationType.OTHER.name();
        }
        return switch (request.getMethod().toUpperCase()) {
            case "POST" -> com.minecraft.enums.OperationType.ADD.name();
            case "PUT", "PATCH" -> com.minecraft.enums.OperationType.UPDATE.name();
            case "DELETE" -> com.minecraft.enums.OperationType.DELETE.name();
            default -> com.minecraft.enums.OperationType.OTHER.name();
        };
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 从 User-Agent 简单解析浏览器信息
     */
    private String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        if (userAgent.contains("Edg/")) {
            return "Edge";
        } else if (userAgent.contains("Chrome/") && !userAgent.contains("Edg/")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox/")) {
            return "Firefox";
        } else if (userAgent.contains("Safari/") && userAgent.contains("Version/")) {
            return "Safari";
        }
        return "Other";
    }

    /**
     * 从 User-Agent 简单解析操作系统信息
     */
    private String parseOs(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac OS")) {
            return "macOS";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        }
        return "Other";
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }
}
