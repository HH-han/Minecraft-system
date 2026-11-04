package com.minecraft.aspect;

import com.minecraft.entity.User;
import com.minecraft.enums.OperationType;
import com.minecraft.log.OperationLogRecorder;
import com.minecraft.service.UserService;
import com.minecraft.utils.LogParamSerializer;
import com.minecraft.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 操作日志切面。
 *
 * <ul>
 *   <li>所有 Controller 写操作（POST/PUT/PATCH/DELETE）自动记录；</li>
 *   <li>标注 {@link OperationLog} 的方法（含 Service 层）按注解精确记录；</li>
 *   <li>框架统一补齐操作人、请求来源、耗时、成功/失败等公共信息；</li>
 *   <li>bizId、detail 支持 Spring EL，业务自行描述“改了什么”；</li>
 *   <li>默认不记录请求参数，防止敏感信息落库；</li>
 *   <li>日志通过独立事务异步落库，业务回滚不影响审计记录。</li>
 * </ul>
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    private static final int MAX_DETAIL_LENGTH = 2000;
    private static final int MAX_ERROR_LENGTH = 1000;

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final OperationLogRecorder operationLogRecorder;
    private final UserService userService;

    public LogAspect(OperationLogRecorder operationLogRecorder, UserService userService) {
        this.operationLogRecorder = operationLogRecorder;
        this.userService = userService;
    }

    @Pointcut("execution(* com.minecraft.controller..*(..))")
    public void controllerPointcut() {
    }

    @Pointcut("@annotation(com.minecraft.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    @Around("controllerPointcut() || operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.minecraft.annotation.OperationLog annotation =
                method.getAnnotation(com.minecraft.annotation.OperationLog.class);
        HttpServletRequest request = getRequest();

        // GET/OPTIONS/HEAD 等只读请求默认不记录，除非显式标注 @OperationLog
        boolean shouldLog = annotation != null || isWriteRequest(request);

        Object result = null;
        Throwable error = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            if (shouldLog) {
                try {
                    recordLog(joinPoint, method, annotation, request,
                            result, error, System.currentTimeMillis() - beginTime);
                } catch (Throwable ex) {
                    // 日志记录失败（含类加载错误等 Error）不能影响业务主流程
                    logger.error("记录操作日志失败: {}", method.getName(), ex);
                }
            }
        }
    }

    private void recordLog(ProceedingJoinPoint joinPoint, Method method,
                           com.minecraft.annotation.OperationLog annotation,
                           HttpServletRequest request, Object result, Throwable error,
                           long executionTime) {
        com.minecraft.entity.OperationLog operationLog = new com.minecraft.entity.OperationLog();

        fillOperator(operationLog);
        fillRequestInfo(operationLog, request);

        if (annotation != null) {
            fillFromAnnotation(operationLog, annotation, joinPoint, method, result);
        } else {
            operationLog.setModule(resolveModule(joinPoint));
            operationLog.setOperationDesc(method.getName());
            operationLog.setOperationType(resolveOperationType(request));
        }

        if (annotation != null && annotation.recordParams()) {
            operationLog.setRequestParams(LogParamSerializer.serialize(joinPoint.getArgs()));
        }

        operationLog.setExecutionTime(executionTime);
        if (error != null) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(truncate(errorText(error), MAX_ERROR_LENGTH));
        } else {
            operationLog.setStatus(1);
        }

        operationLogRecorder.record(operationLog);
    }

    /**
     * 填充操作人信息：JWT 中只有用户 ID，这里补充可读的操作人名称。
     */
    private void fillOperator(com.minecraft.entity.OperationLog operationLog) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            operationLog.setUserId(userId.intValue());
            operationLog.setUsername(resolveOperatorName(userId));
        } else {
            operationLog.setUsername(SecurityUtils.getCurrentUsername());
        }
    }

    private String resolveOperatorName(Long userId) {
        try {
            User user = userService.getById(userId);
            if (user != null) {
                if (!isBlank(user.getNickname())) {
                    return user.getNickname();
                }
                if (!isBlank(user.getUsername())) {
                    return user.getUsername();
                }
                if (!isBlank(user.getAccount())) {
                    return user.getAccount();
                }
            }
        } catch (Exception e) {
            logger.debug("解析操作人名称失败，userId={}", userId, e);
        }
        return SecurityUtils.getCurrentUsername();
    }

    private void fillRequestInfo(com.minecraft.entity.OperationLog operationLog, HttpServletRequest request) {
        if (request == null) {
            return;
        }
        operationLog.setIpAddress(getClientIp(request));
        operationLog.setUserAgent(request.getHeader("User-Agent"));
        operationLog.setRequestUri(request.getRequestURI());
        operationLog.setRequestMethod(request.getMethod());
        operationLog.setBrowser(parseBrowser(operationLog.getUserAgent()));
        operationLog.setOs(parseOs(operationLog.getUserAgent()));
    }

    private void fillFromAnnotation(com.minecraft.entity.OperationLog operationLog,
                                    com.minecraft.annotation.OperationLog annotation,
                                    ProceedingJoinPoint joinPoint, Method method, Object result) {
        OperationType type = annotation.type();
        operationLog.setOperationType(type != null && type != OperationType.OTHER
                ? type.name()
                : resolveOperationType(getRequest()));
        operationLog.setModule(isBlank(annotation.module()) ? resolveModule(joinPoint) : annotation.module());
        operationLog.setOperationDesc(isBlank(annotation.action()) ? method.getName() : annotation.action());

        EvaluationContext context = buildEvaluationContext(method, joinPoint.getArgs(), result);
        operationLog.setBizId(evaluate(annotation.bizId(), context));
        operationLog.setDetail(truncate(evaluate(annotation.detail(), context), MAX_DETAIL_LENGTH));
    }

    private EvaluationContext buildEvaluationContext(Method method, Object[] args, Object result) {
        EvaluationContext context = new MethodBasedEvaluationContext(null, method, args, PARAMETER_NAME_DISCOVERER);
        context.setVariable("result", result);
        return context;
    }

    /**
     * 解析 Spring EL 表达式；为空或解析失败时返回 null，不影响业务。
     */
    private String evaluate(String expression, EvaluationContext context) {
        if (isBlank(expression)) {
            return null;
        }
        try {
            Expression parsed = EXPRESSION_PARSER.parseExpression(expression);
            Object value = parsed.getValue(context);
            return value == null ? null : String.valueOf(value);
        } catch (Exception e) {
            logger.debug("操作日志表达式解析失败: {}", expression, e);
            return null;
        }
    }

    private boolean isWriteRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String method = request.getMethod();
        return !("GET".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method)
                || "HEAD".equalsIgnoreCase(method));
    }

    /**
     * 未指定 module 时根据 Controller 名称推断，如 ProductController -> PRODUCT。
     */
    private String resolveModule(ProceedingJoinPoint joinPoint) {
        String simpleName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String module = simpleName.replace("Controller", "");
        return module.isEmpty() ? "SYSTEM" : module.toUpperCase();
    }

    /**
     * 未指定类型时按 HTTP 方法归类操作类型。
     */
    private String resolveOperationType(HttpServletRequest request) {
        if (request == null) {
            return OperationType.OTHER.name();
        }
        return switch (request.getMethod().toUpperCase()) {
            case "POST" -> OperationType.ADD.name();
            case "PUT", "PATCH" -> OperationType.UPDATE.name();
            case "DELETE" -> OperationType.DELETE.name();
            default -> OperationType.OTHER.name();
        };
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String parseBrowser(String userAgent) {
        if (isBlank(userAgent)) {
            return "Unknown";
        }
        if (userAgent.contains("Edg/")) {
            return "Edge";
        } else if (userAgent.contains("Chrome/")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox/")) {
            return "Firefox";
        } else if (userAgent.contains("Safari/") && userAgent.contains("Version/")) {
            return "Safari";
        }
        return "Other";
    }

    private String parseOs(String userAgent) {
        if (isBlank(userAgent)) {
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

    private String errorText(Throwable error) {
        return Objects.requireNonNullElseGet(error.getMessage(), () -> error.getClass().getSimpleName());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
