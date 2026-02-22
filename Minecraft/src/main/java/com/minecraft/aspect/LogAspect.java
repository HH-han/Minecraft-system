package com.minecraft.aspect;

import com.alibaba.fastjson2.JSON;
import com.minecraft.annotation.Log;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class LogAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Pointcut("@annotation(com.minecraft.annotation.Log)")
    public void logPointcut() {
    }
    
    @Before("logPointcut()")
    public void before(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);
            
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("[操作日志]")
                     .append(" 时间: ").append(formatter.format(LocalDateTime.now()))
                     .append(" 方法: ").append(signature.getDeclaringTypeName()).append(".").append(signature.getName())
                     .append(" 描述: ").append(logAnnotation.value());
            
            if (logAnnotation.recordParams()) {
                logBuilder.append(" 请求参数: ").append(JSON.toJSONString(joinPoint.getArgs()));
            }
            
            logBuilder.append(" IP: ").append(request.getRemoteAddr())
                     .append(" URI: ").append(request.getRequestURI());
            
            logger.info(logBuilder.toString());
        } catch (Exception e) {
            logger.error("日志记录失败: {}", e.getMessage());
        }
    }
    
    @AfterReturning(pointcut = "logPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);
            
            if (logAnnotation.recordResult()) {
                logger.info("[操作日志] 方法: {}.{} 响应结果: {}",
                        signature.getDeclaringTypeName(),
                        signature.getName(),
                        JSON.toJSONString(result));
            }
        } catch (Exception e) {
            logger.error("日志记录失败: {}", e.getMessage());
        }
    }
    
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            logger.error("[操作日志] 方法: {}.{} 异常: {}",
                    signature.getDeclaringTypeName(),
                    signature.getName(),
                    e.getMessage(), e);
        } catch (Exception ex) {
            logger.error("日志记录失败: {}", ex.getMessage());
        }
    }
}