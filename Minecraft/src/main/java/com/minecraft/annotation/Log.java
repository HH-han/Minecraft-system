package com.minecraft.annotation;

import com.minecraft.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解：标注在 Controller 方法上用于补充操作描述、类型与模块信息。
 * 未标注的写操作（POST/PUT/DELETE）也会被 LogAspect 自动记录。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String value() default "";

    OperationType operationType() default OperationType.OTHER;

    String module() default "";

    boolean recordParams() default true;

    boolean recordResult() default false;
}
