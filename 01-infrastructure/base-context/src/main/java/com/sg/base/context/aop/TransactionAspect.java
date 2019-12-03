package com.sg.base.context.aop;

import com.sg.base.crud.Result;
import com.sg.base.dao.Transaction;
import com.sg.base.log.Logger;
import com.sg.base.util.Validator;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * TransactionAspect
 *
 * @author Dai Wenqing
 * @date 2016/2/26
 */
@Aspect
@Service
public class TransactionAspect {
    @Autowired(required = false)
    private Set<Transaction> transactionSet;
    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();
    private ThreadLocal<String> outerTransaction = new ThreadLocal<>();

    /*@After("anyMethod()")
    public void doAfter(JoinPoint jp) {

    }*/

    @Around("anyMethod()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        Object r = null;
        try {
            r = proceedingJoinPoint.proceed(args);
            if (r instanceof Result) {
                Result result = Result.class.cast(r);
                //执行事务，且返回执行成功，才提交事务
                confirm(result.getData(), proceedingJoinPoint.getTarget().getClass().getName());
            } else {
                confirm(r, proceedingJoinPoint.getTarget().getClass().getName());
            }

        } catch (Throwable e) {
            if (transactionSet != null) {
                transactionSet.forEach(Transaction::rollback);
            }
        }
        return r;
    }

    private void confirm(Object result, String simpleName) {
        //ProceedingJoinPoint proceedingJoinPoint = ; Result result = ;
        if (result instanceof Boolean) {
            if (Boolean.class.cast(result)) {
                commit(simpleName);
            }
        } else {
            commit(simpleName);
        }
    }

    private void commit(String simpleName) {
        if (!Validator.isEmpty(outerTransaction.get()))
            if (outerTransaction.get().equals(simpleName))
                if (transactionSet != null) {
                    transactionSet.forEach(Transaction::commit);
                    outerTransaction.remove();
                    threadLocal.remove();
                }
    }


    // @Pointcut("execution(* *..*Ctrl.*(..))")
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void anyMethod() {
    }

    @Before("anyMethod()")
    public void doBefore(JoinPoint jp) {
        if (!Validator.isEmpty(threadLocal.get()))
            return;
        if (transactionSet != null)
            transactionSet.forEach(Transaction::beginTransaction);
        threadLocal.set(true);
        outerTransaction.set(jp.getTarget().getClass().getName());
    }

    public void doThrowing(JoinPoint jp, Throwable ex) {
        if (transactionSet != null)
            transactionSet.forEach(Transaction::rollback);
        Logger.error(ex, "TransactionAspect->" + "method " + jp.getTarget().getClass().getName() + "."
                + jp.getSignature().getName() + " throw exception，并时行了事务回滚");

        outerTransaction.remove();
        threadLocal.remove();
    }
}
