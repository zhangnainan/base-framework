package com.sg.base.model.annotation;

import com.sg.base.model.enums.JoinType;

import java.lang.annotation.*;

/**
 * Fetch
 *
 * @author Dai Wenqing
 * @date 2016/5/30
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FetchWay {
    /**
     * 取数据关联方式
     *
     * @return
     */
    JoinType way() default JoinType.Inner;
}
