package com.sg.base.log;

/**
 * 日志组件，
 *
 * @author Dai Wenqing
 * @date 2016/1/11
 */
public class Logger {

    static LogAdapter logAdapter = new LogAdapterImpl();

    /**
     * 判断是否允许输出DEBUF级别的日志信息。
     *
     * @return 如果允许则返回true；否则返回false。
     */
    public static boolean isDebugEnable() {
        return logAdapter.isDebugEnable();
    }

    /**
     * 输出DEBUG级别的日志信息。
     *
     * @param message 日志信息。
     * @param args    参数集，依次替换掉字符串中的{}。
     */
    public static void debug(String message, Object... args) {
        debug(LogType.System, message, args);
    }

    public static void debug(LogType logType, String message, Object... args) {
        message = getMessage(logType, message);
        logAdapter.debug(message, args);
    }

    private static String getMessage(LogType logType, String message) {
        message = logType.getType() + "###" + message;
        return message;
    }

    /**
     * 判断是否允许输出INFO级别的日志信息。
     *
     * @return 如果允许则返回true；否则返回false。
     */
    public static boolean isInfoEnable() {
        return logAdapter.isInfoEnable();
    }

    /**
     * 输出INFO级别的日志信息。
     *
     * @param message 日志信息。
     * @param args    参数集，依次替换掉字符串中的{}。
     */
    public static void info(String message, Object... args) {
        info(LogType.System, message, args);
    }

    public static void info(LogType logType, String message, Object... args) {
        message = getMessage(logType, message);
        logAdapter.info(message, args);
    }


    /**
     * 输出WARN级别的日志信息。
     *
     * @param throwable 异常信息，如果非异常信息可置为null。
     * @param message   日志信息。
     * @param args      参数集，依次替换掉字符串中的{}。
     */
    public static void warn(Throwable throwable, String message, Object... args) {
        warn(LogType.System, throwable, message, args);
    }

    public static void warn(LogType logType, Throwable throwable, String message, Object... args) {
        message = getMessage(logType, message);
        logAdapter.warn(throwable, message, args);
    }

    /**
     * 输出ERROR级别的日志信息。
     *
     * @param throwable 异常信息，如果非异常信息可置为null。
     * @param message   日志信息。
     * @param args      参数集，依次替换掉字符串中的{}。
     */
    public static void error(Throwable throwable, String message, Object... args) {
        error(LogType.System, throwable, message, args);
    }

    public static void error(LogType logType, Throwable throwable, String message, Object... args) {
        message = getMessage(logType, message);
        logAdapter.error(throwable, message, args);
    }
}
