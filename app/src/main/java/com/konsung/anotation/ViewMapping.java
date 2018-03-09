package com.konsung.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Created by Administrator on 2015/11/27 0027.
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewMapping {
    int[] viewIds();
}
