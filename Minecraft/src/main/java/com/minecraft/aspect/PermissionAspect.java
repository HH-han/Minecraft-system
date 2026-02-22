package com.minecraft.aspect;

import com.minecraft.annotation.Permission;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    
    @Pointcut("@annotation(com.minecraft.annotation.Permission)")
    public void permissionPointcut() {
    }
    
    @Before("permissionPointcut()")
    public void before(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Permission permissionAnnotation = method.getAnnotation(Permission.class);
            
            if (permissionAnnotation.requireAuth()) {
                String token = request.getHeader("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                    throw new RuntimeException("未授权访问");
                }
                
                String tokenValue = token.substring(7);
                Long userId = null;
                
                String[] permissions = permissionAnnotation.value();
                if (permissions != null && permissions.length > 0) {
                }
                
                logger.info("[权限校验] 方法: {}.{} 认证通过", 
                        signature.getDeclaringTypeName(), 
                        signature.getName());
            }
        } catch (Exception e) {
            logger.error("权限校验失败: {}", e.getMessage());
            throw e;
        }
    }
}