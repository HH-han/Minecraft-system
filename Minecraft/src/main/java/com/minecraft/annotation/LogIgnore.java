package com.minecraft.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志敏感字段标记：当请求参数被序列化进操作日志时，自动忽略该字段。
 *
 * <p>示例：</p>
 * <pre>{@code
 * public class UpdateUserCommand {
 *     private String nickname;
 *     private String mobile;
 *     @LogIgnore
 *     private String idCard;
 * }
 * }</pre>
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogIgnore {
}
