package com.sg.base.model.annotation;

import java.lang.annotation.*;

/**
 * Unique
 *
 * @author Dai Wenqing
 * @date 2016/5/23
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
}
