package com.sg.base.context.validator;

import com.sg.base.bean.BeanFactory;
import com.sg.base.context.request.Request;
import com.sg.base.context.session.Session;
import com.sg.base.log.Logger;
import com.sg.base.message.Message;
import com.sg.base.resource.CustomizedPropertyConfigurer;
import com.sg.base.util.Validator;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ValidatorInterceptor
 *
 * @author Dai Wenqing
 * @date 2016/3/2
 */
public class ValidatorInterceptor extends HandlerInterceptorAdapter {
    /**
     * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
     * 如果返回true 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
     * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
     */
    // @Value("${commons.ctrl.dispatcher.max:512}")
    protected int max;
    // protected ThreadLocal<Long> time = new ThreadLocal<>();
    protected AtomicInteger counter = new AtomicInteger();
    protected List<String> sessionList = new ArrayList<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (max == 0)
            max = Integer.valueOf(CustomizedPropertyConfigurer.getContextProperty("snow.request.count.max").toString());
        return preValidate(request, response, handler);
    }

    private Boolean preValidate(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Session session = BeanFactory.getBean(Session.class);
        boolean re = true;
        if (handler instanceof HandlerMethod) {
            if (!sessionList.contains(session.getSessionId())) {
                sessionList.add(session.getSessionId());
            }

            if (counter.incrementAndGet() > max) {
                // counter.decrementAndGet();
                write(response, Message.Busy.getType());
                Logger.warn(null, "超过最大并发处理数[{}]，系统繁忙信息。", max);
                re = false;
            }

            if (Logger.isDebugEnable()) {
                Logger.info("当前并发量是[{}]，请求的会话ID是[{}]，未关闭的会话数据是[{}]，请求的URL是[{}]。", counter.get(), session.getSessionId(), sessionList.size(),
                        request.getRequestURI());
            }

            HandlerMethod hm = (HandlerMethod) handler;
            Validate validate = hm.getMethodAnnotation(Validate.class);
            if (!Validator.isEmpty(validate)) {
                for (Rule rule : validate.rules()) {
                    if (Validator.isEmpty(rule.name()))
                        break;
                    if (Validator.isEmpty(rule.message()))
                        break;
                    String failure = rule.message().getType();
                    Checker checker = BeanFactory.getBean(failure);
                    if (!Validator.isEmpty(checker)) {
                        Request r = BeanFactory.getBean(Request.class);
                        if (!checker.validate(r.get(rule.name()), rule.paramter())) {
                            String path = hm.getBeanType().getName();
                            String[] paths = path.split("\\.");
                            List<String> list = Arrays.asList(paths);
                            /*
                             * list.remove(0); list.remove(list.size() - 1);
                             */
                            if (list.size() > 3)
                                path = String.join(".", list.subList(2, list.size() - 1));
                            else if (list.size() > 2)
                                path = String.join(".", list.subList(2, list.size()));
                            else if (list.size() > 1)
                                path = String.join(".", list.subList(1, list.size()));
                            else
                                path = String.join(".", list);
                            String result = checker.failure(rule.message(), path + "." + rule.name()).toString();
                            write(response, result);
                            re = false;
                        }
                    } else {
                        String result = checker.failure(Message.CheckerNotFound, rule.name()).toString();
                        re = false;
                    }
                }
            }
        }
        return re;
    }

    private void write(HttpServletResponse response, String result) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.append(result);
        out.close();
    }

    /**
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作 可在modelAndView中加入数据，比如当前时间
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // counter.decrementAndGet();
    }

    /**
     * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
     * <p>
     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            counter.decrementAndGet();
        }

    }
}
