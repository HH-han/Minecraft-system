package com.minecraft.annotation;

import com.minecraft.enums.OperationType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解，标注在需要审计的方法上（Controller 或 Service）。
 *
 * <p>框架负责记录“谁、什么时候、从哪来、耗时多久、成功还是失败”，
 * 业务只需通过 {@link #bizId()} 与 {@link #detail()} 描述“操作了哪个对象、改了什么”。</p>
 *
 * <p>bizId 与 detail 均支持 Spring EL 表达式，可直接引用方法参数（如 {@code #orderId}），
 * 方法执行成功后还可以引用返回值（如 {@code #result.data.id}）。</p>
 *
 * <p>示例：</p>
 * <pre>{@code
 * @OperationLog(
 *     module = "PRODUCT",
 *     action = "修改商品价格",
 *     bizId = "#productId",
 *     detail = "'商品价格从 ' + #oldPrice + ' 修改为 ' + #newPrice",
 *     type = OperationType.UPDATE
 * )
 * public void updatePrice(Long productId, BigDecimal oldPrice, BigDecimal newPrice) { ... }
 * }</pre>
 *
 * <p>操作日志默认不序列化请求参数，避免密码、身份证、手机号等敏感数据落库；
 * 确需记录结构化参数时，将 {@link #recordParams()} 设为 true，并在敏感字段上标注
 * {@link LogIgnore}。</p>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /** 业务模块，如 ORDER、PRODUCT、MEMBER；为空时根据 Controller 名称推断 */
    String module() default "";

    /** 操作动作，如“审核订单”“修改商品价格”；为空时使用方法名 */
    String action() default "";

    /** 业务对象 ID，支持 Spring EL 表达式，如 {@code #orderId}、{@code #result.data.id} */
    String bizId() default "";

    /** 操作详情/变更说明，支持 Spring EL 表达式，用于回答“到底改了什么” */
    String detail() default "";

    /** 操作类型，保留默认值 OTHER 时按 HTTP 方法推断 */
    OperationType type() default OperationType.OTHER;

    /** 是否记录请求参数（默认 false，避免敏感信息落库） */
    boolean recordParams() default false;
}
