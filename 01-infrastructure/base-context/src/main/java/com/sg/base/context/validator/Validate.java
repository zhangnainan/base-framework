package com.sg.base.context.validator;

import java.lang.annotation.*;

/**
 * Validate
 *
 * @author Dai Wenqing
 * @date 2016/3/3
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    Rule[] rules() default {};
}
