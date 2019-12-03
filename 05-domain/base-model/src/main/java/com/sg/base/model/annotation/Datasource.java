package com.sg.base.model.annotation;

import java.lang.annotation.*;

/**
 * Datasource
 *
 * @author Dai Wenqing
 * @date 2016/4/5
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Datasource {
    String value() default "";
}
