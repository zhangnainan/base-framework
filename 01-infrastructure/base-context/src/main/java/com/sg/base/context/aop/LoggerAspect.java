package com.sg.base.context.aop;

import com.sg.base.log.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;

/**
 * LoggerAspect
 *
 * @author Dai Wenqing
 * @date 2016/2/26
 */
@Aspect
public class LoggerAspect {
    public void doBefore(JoinPoint jp) {

    }

    public void doThrowing(JoinPoint jp, Throwable ex) {
        Logger.error(ex, "LoggerAspect->" + "method " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName()
                + " throw exception");
    }
}
