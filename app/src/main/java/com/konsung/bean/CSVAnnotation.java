package com.konsung.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dlx on 2017/4/13 0013.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CSVAnnotation {
    /**
     * 姓名
     * @return 姓名
     */
    String name() default "";

    /**
     * 枚举类型
     * @return 枚举类型
     */
    InfoType type() default InfoType.NORMAL;
    /**
     * 优先级
     * @return 优先级值
     */
    int priority() default 0;
}
