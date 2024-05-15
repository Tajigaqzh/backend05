package com.bilibackend.validate;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

/**
 * @Author 20126
 * @Description
 * @Date 2024/4/13 12:42
 * @Version 1.0
 */
public class AllowConstraintValidator implements ConstraintValidator<AllowValue, Object> {

    private AllowValue annotation;
    private String[] strValues;
    private int[] intValues;


    @Override
    public void initialize(AllowValue constraintAnnotation) {
        this.annotation = constraintAnnotation;
        strValues = constraintAnnotation.strValues();
        intValues = constraintAnnotation.intValues();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {

        /**
         * 校验数字，校验数字必须传递至少两个
         */
        if (intValues != null && intValues.length > 1) {
            for (int s : intValues) {
                if (s == (Integer) object) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 校验字符串
         */

        if (strValues != null && strValues.length != 0) {
            for (String s : strValues) {
                if (s.equals(object)) {
                    return true;
                }
            }
            return false;
        }
        /**
         * 校验枚举
         */
        // 如果待校验的值为null，是否校验通过
        if (object == null) {
            return annotation.allowNull();
        }
        // 返回枚举常量的数组
        Object[] objects = annotation.clazz().getEnumConstants();
        try {
            // 获取枚举类中获取校验字段的方法名
            Method method = annotation.clazz().getMethod(annotation.method());
            for (Object o : objects) {
                // 将方法执行的结果method.invoke(o)和待校验的值value进行比较，如果相同，说明校验成功
                if (object.equals(method.invoke(o))) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
