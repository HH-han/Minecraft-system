package com.minecraft.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.minecraft.annotation.LogIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 操作日志参数序列化工具。
 *
 * <p>仅在 {@code @OperationLog(recordParams = true)} 时使用；
 * 序列化前会过滤 Servlet 容器对象，并按 {@link LogIgnore} 剔除敏感字段。</p>
 */
public final class LogParamSerializer {

    private static final int MAX_LENGTH = 2000;
    private static final Map<Class<?>, Set<String>> IGNORED_FIELD_CACHE = new ConcurrentHashMap<>();

    private LogParamSerializer() {
    }

    /**
     * 序列化方法参数，异常时降级为字符串数组，绝不抛出异常影响业务。
     */
    public static String serialize(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        try {
            List<Object> payload = new ArrayList<>(args.length);
            for (Object arg : args) {
                if (isExcluded(arg)) {
                    continue;
                }
                payload.add(maskSensitiveFields(arg));
            }
            return truncate(JSON.toJSONString(payload));
        } catch (Exception e) {
            return truncate(String.valueOf(args));
        }
    }

    /**
     * 过滤 HttpServletRequest/Response、上传文件、校验结果等不可序列化对象。
     */
    private static boolean isExcluded(Object arg) {
        return arg instanceof HttpServletRequest
                || arg instanceof HttpServletResponse
                || arg instanceof MultipartFile
                || arg instanceof BindingResult;
    }

    /**
     * 将对象转换为 JSON 结构并移除标注 {@link LogIgnore} 的顶层字段。
     */
    private static Object maskSensitiveFields(Object arg) {
        if (arg == null || isSimpleValue(arg.getClass())) {
            return arg;
        }
        Set<String> ignoredFields = ignoredFields(arg.getClass());
        if (ignoredFields.isEmpty()) {
            return arg;
        }
        Object json = JSON.toJSON(arg);
        if (json instanceof JSONObject jsonObject) {
            ignoredFields.forEach(jsonObject::remove);
        }
        return json;
    }

    private static boolean isSimpleValue(Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class.equals(type)
                || Character.class.equals(type)
                || Date.class.isAssignableFrom(type)
                || Temporal.class.isAssignableFrom(type)
                || BigDecimal.class.equals(type)
                || BigInteger.class.equals(type);
    }

    private static Set<String> ignoredFields(Class<?> type) {
        return IGNORED_FIELD_CACHE.computeIfAbsent(type, LogParamSerializer::scanIgnoredFields);
    }

    private static Set<String> scanIgnoredFields(Class<?> type) {
        Set<String> fields = new HashSet<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(LogIgnore.class)) {
                    fields.add(field.getName());
                }
            }
            current = current.getSuperclass();
        }
        return fields.isEmpty() ? Collections.emptySet() : fields;
    }

    private static String truncate(String text) {
        if (text == null) {
            return null;
        }
        return text.length() > MAX_LENGTH ? text.substring(0, MAX_LENGTH) + "..." : text;
    }
}
