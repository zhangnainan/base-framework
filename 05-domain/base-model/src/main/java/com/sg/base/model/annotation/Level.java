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
public @interface Level {
    /**
     * 层级
     *
     * @return 数据格式。
     */
    String value() default "";
}
