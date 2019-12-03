package com.sg.base.context.aop;

import com.sg.base.log.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * StatisticsAspect
 *
 * @author Dai Wenqing
 * @date 2016/4/1
 */
@Aspect
@Service
public class StatisticsAspect {
    private ThreadLocal<Long> longThreadLocal = new ThreadLocal<>();

    @After("anyMethod()")
    public void doAfter(JoinPoint jp) {
        long end = System.currentTimeMillis();
        long span = end - longThreadLocal.get();
        /*
         * Calendar calendar = Calendar.getInstance();
         * calendar.setTimeInMillis(span);
         * 
         * int day = (int) (span / (24 * 60 * 60 * 1000)); int hour = (int)
         * (span % (24 * 3600) / 3600);
         */

        Logger.info("执行了目标:" + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName() + "，用时[" + span + "]毫秒");
    }

    @Pointcut("@annotation(com.zoe.snow.context.aop.annotation.Statistics)")
    public void anyMethod() {
    }

    @Before("anyMethod()")
    public void doBefore() {
        longThreadLocal.set(System.currentTimeMillis());
    }

    public void doThrowing(JoinPoint jp, Throwable ex) {
        Logger.error(ex, "StatisticsAspect->" + "method " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName() + " throw exception");
    }
}
