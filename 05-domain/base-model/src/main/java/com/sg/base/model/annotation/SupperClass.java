package com.sg.base.model.annotation;

import java.lang.annotation.*;

/**
 * SupperClass
 *
 * @author Dai Wenqing
 * @date 2015/10/13
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupperClass {
    String value() default "";
}
