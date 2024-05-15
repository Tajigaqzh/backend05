package com.bilibackend.validate;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/11 12:25
 * @Version 1.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {AllowConstraintValidator.class})
public @interface AllowValue {


    // 检验失败的提示信息
    String message() default "校验失败";

    String[] strValues() default {};

    int[] intValues() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    /**
     * 要校验的枚举类
     * 比传字段，校验枚举的时候会调用这个类的method方法，如果发生异常会报错
     *
     * @return 枚举类
     */
    Class<?> clazz() default Object.class;


    /**
     * 是否允许null值，默认是允许
     */
    boolean allowNull() default true;

    /**
     * 校验枚举时要传递的方法名称
     * @return String 方法名
     */
    String method() default "getValue";


    /**
     * 要校验枚举类的哪个字段，这个字段通过getxxx()方法获取,返回方法的名称
     *
     * @return 方法的名称
     */
    int[] values() default {};


    @Documented
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        AllowValue[] value();
    }
}