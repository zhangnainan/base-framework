package com.sg.base.context.aop;

import com.sg.base.bean.BeanFactory;
import com.sg.base.crud.Result;
import com.sg.base.log.Logger;
import com.sg.base.message.Message;
import com.sg.base.model.Model;
import com.sg.base.model.annotation.NotNull;
import com.sg.base.util.Validator;
import org.apache.commons.collections.map.HashedMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CloseSessionAspect
 *
 * @author Dai Wenqing
 * @date 2016/3/17
 */
@Aspect
@Service
public class CloseSessionAspect {
    @Autowired(required = false)
    private Set<com.sg.base.dao.Closable> cloneableSet;

    @After("anyMethod()")
    public void doAfter(JoinPoint jp) {
        // 能执行Jp切面，即保证了jp.getSignature().getDeclaringTypeName()不会出现异常
        if (!jp.getSignature().getDeclaringTypeName().endsWith("CrudService")) {
            if (cloneableSet != null) {
                cloneableSet.forEach(com.sg.base.dao.Closable::close);
            }
        }
    }

    @Before("anyMethod()")
    public void doBefore(JoinPoint jp) {

    }

    @Around("anyMethod()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        Result result = BeanFactory.getBean(Result.class);
        Method method = null;
        for (Method m : proceedingJoinPoint.getTarget().getClass().getDeclaredMethods()) {
            if (m.getName().equals(proceedingJoinPoint.getSignature().getName())) {
                if (m.getParameters().length == proceedingJoinPoint.getArgs().length) {
                    method = m;
                    break;
                }
            }
        }
        List<String> argPositionList = new ArrayList();
        if (!Validator.isEmpty(method)) {
            List<NotNull> notNulls = new ArrayList<>();
            Map<Integer, Class<?>> classMap = new HashedMap();
            List<Class<?>> classes = new ArrayList<>();
            List<Integer> positions = new ArrayList<>();

            /*
             * for (Parameter parameter : method.getParameters()) { int position
             * = Integer.valueOf(parameter.getName().substring(3));
             * classMap.put(position, (Class<?>)
             * parameter.getParameterizedType()); }
             */

            for (Annotation[] parameterAnnotation : method.getParameterAnnotations()) {
                for (Annotation annotation : parameterAnnotation) {
                    if (annotation instanceof NotNull) {
                        NotNull notNull = (NotNull) annotation;
                        notNulls.add(notNull);
                        // classes.add(notNull.value());
                        positions.add(notNull.value());
                    }
                }
            }

            if (!Validator.isEmpty(args)) {
                int ndx = 0;
                for (Object o : args) {
                    ndx++;
                    if (Validator.isEmpty(o)) {
                        if (positions.contains(ndx - 1))
                            argPositionList.add(ndx + "");
                    } else if (o instanceof Model) {
                        //// TODO: 2016/8/1  允许通过实体注解的方式 做字段过滤
                    }
                }
            }
        }

        if (argPositionList.size() > 0) {
            result.setResult(null, Message.ParameterPositionInHolderIsnull, String.join(",", argPositionList),
                    proceedingJoinPoint.getSignature().toShortString());
            return result;
        }
        try {
            return proceedingJoinPoint.proceed(args);

        } catch (Throwable e) {
            Logger.error(e, e.getMessage());
            result.setResult(null, Message.ServiceError);
        }
        return result;
    }

    @Pointcut("execution(* *..*ServiceImpl.*(..))")
    public void anyMethod() {
    }

    // public void Acc

    public void doThrowing(JoinPoint jp, Throwable ex) {
        Logger.error(ex, "CloseSessionAspect->" + "method " + jp.getTarget().getClass().getName() + "." + jp.getSignature().getName() + " throw exception");
    }
}
