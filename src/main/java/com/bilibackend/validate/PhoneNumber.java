package com.bilibackend.validate;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @Author 20126
 * @Description
 * @Date 2023/11/5 16:18
 * @Version 1.0
 */

@Documented
@Constraint(validatedBy = {PhoneNumberValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    // 注解的提示信息
    String message() default "手机号码格式不正确";

    Class<?>[] groups() default {};
    /**
     * 自定义正则表达式，优先级最高
     * @return String
     */
    String regexPattern() default "";

    /**
     * 严格模式，默认开启
     * @return boolean
     */
    boolean restrict() default true;


    boolean allowNull() default false;



    Class<? extends Payload>[] payload() default {};

    // 指定多个时使用
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PhoneNumber[] value();
    }
}
