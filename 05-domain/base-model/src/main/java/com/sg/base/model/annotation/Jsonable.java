/**
 *
 */
package com.sg.base.model.annotation;

import java.lang.annotation.*;

/**
 * 定义可转化为JSON的属性。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Jsonable {
    /**
     * 数据格式。
     *
     * @return 数据格式。
     */
    String format() default "";
}
