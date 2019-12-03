package com.sg.base.util;

import com.sg.base.log.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用转换类
 *
 * @author dwq
 */
public final class Converter {
    private static final String[] BIT_SIZE_FORMAT = {"0 B", "0.00 K", "0.00 M", "0.00 G", "0.00 T"};
    private static final String CHAR_SET = "utf-8";
    private static String localeAndCounty = "zh-cn";
    private static String localeDate = "yyyy-MM-dd";
    private static String localeDatetime = "yyyy-MM-dd HH:mm:ss";

    protected static Map<String, DecimalFormat> decimalFormatMap = new ConcurrentHashMap<>();
    protected static Map<String, SimpleDateFormat> dateFormatMap = new ConcurrentHashMap<>();
    protected static Map<Locale, String> localeDateMap = new ConcurrentHashMap<>();
    protected static Map<Locale, String> localDatetimeMap = new ConcurrentHashMap<>();

    /**
     * 将对象转化为字符串。如果为null或空集则返回空字符串；如果是数组则使用逗号分割后拼接；其它返回object.toString()结果。
     *
     * @param object 对象。
     * @return 字符串。
     */
    public static String toString(Object object) {
        if (Validator.isEmpty(object))
            return "";

        if (object.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            for (int length = Array.getLength(object), i = 0; i < length; i++)
                sb.append(',').append(Array.get(object, i));

            return sb.substring(1);
        }

        if (object instanceof Iterable) {
            StringBuilder sb = new StringBuilder();
            ((Iterable) object).forEach(obj -> sb.append(',').append(toString(obj)));

            return sb.substring(1);
        }

        if (object instanceof Map) {
            StringBuilder sb = new StringBuilder();
            ((Map) object).forEach((key, value) -> sb.append(',').append(toString(key)).append('=').append(toString(value)));

            return sb.substring(1);
        }

        if (object instanceof java.sql.Date)
            return toString((java.sql.Date) object, getDateFormat(LocaleUtil.toLocale(localeAndCounty)));

        if (object instanceof Timestamp)
            return toString((Timestamp) object, getDateTimeFormat(LocaleUtil.toLocale(localeAndCounty)));

        return object.toString();
    }

    /**
     * 根据当前编码获取下一个编码
     *
     * @param currentCode 当前编码
     * @param num         字母的个数
     * @return
     */
    public static String getNextCode(String currentCode, int num) {
        if (num > currentCode.length())
            return currentCode;
        String header = currentCode.substring(0, num);
        String body = currentCode.substring(num);
        int next = toInt(body);
        return header + (++next);
    }

    /**
     * 按指定格式将数值格式化为字符串。
     *
     * @param number 要进行格式化的数值。
     * @param format 目标格式。
     * @return 格式化后的数值字符串。
     */
    public static String toString(Number number, String format) {
        DecimalFormat df = decimalFormatMap.get(format);
        if (df == null) {
            df = new DecimalFormat(format);
            decimalFormatMap.put(format, df);
        }

        return df.format(number);
    }

    /**
     * 将整数数值对象转化为指定精度的浮点数字符串。
     *
     * @param number  数值对象。
     * @param decimal 精确度。
     * @param point   保留的小数点位数。
     * @return 浮点数字符串。
     */
    public static String toString(Object number, int decimal, int point) {
        StringBuilder sb = new StringBuilder().append("0.");
        for (int i = 0; i < point; i++)
            sb.append('0');

        return toString(toLong(number) * Math.pow(0.1D, decimal), sb.toString());
    }

    /**
     * 将字符串按指定分隔符转化为字符串数组。
     *
     * @param string    要进行转化的字符串。
     * @param separator 分隔符。
     * @return 字符串数组。如果字符串为空则返回空数组；如果分隔符为null则返回仅包含一个元素、且为原字符串的数组。
     */
    public static String[] toArray(String string, String separator) {
        if (Validator.isEmpty(string))
            return new String[0];

        if (separator == null || !string.contains(separator))
            return new String[]{string};

        if (!string.endsWith(separator))
            return string.split(separator);

        String[] strs = string.split(separator);
        String[] array = new String[strs.length + 1];
        for (int i = 0; i < strs.length; i++)
            array[i] = strs[i];
        array[strs.length] = "";

        return array;
    }

    /**
     * 将字符串按指定分隔符转化为二维数组。
     *
     * @param string    要转化的字符串。
     * @param separator 分隔符字符串数组，必须为两个元素的数组。
     * @return 转化后的二维数组；如果字符串为空或分隔符数组不为两个元素的数组则返回空二维数组。
     */
    public static String[][] toArray(String string, String[] separator) {
        if (Validator.isEmpty(string) || Validator.isEmpty(separator) || separator.length < 2 || Validator.isEmpty(separator[0])
                || Validator.isEmpty(separator[1]) || !string.contains(separator[1]))
            return new String[0][0];

        List<String> list = new ArrayList<>();
        for (String str : toArray(string, separator[0]))
            if (string.contains(separator[1]) && str.indexOf(separator[1]) == str.lastIndexOf(separator[1]))
                list.add(str);

        if (list.isEmpty())
            return new String[0][0];

        String[][] array = new String[list.size()][];
        for (int i = 0; i < array.length; i++)
            array[i] = toArray(list.get(i), separator[1]);

        return array;
    }

    /**
     * 将使用K、M、G、T等单位表示的字符串转化为整数值。
     *
     * @param size 使用K、M、G、T等单位表示的字符串。
     * @return 整数值；如果转化失败则返回-1。
     */
    public static String toBitSize(long size) {
        return toBitSize(size < 0 ? 0 : size, 0);
    }

    /**
     * 将使用K、M、G、T等单位表示的字符串转化为整数值。
     *
     * @param size 使用K、M、G、T等单位表示的字符串。
     * @return 整数值；如果转化失败则返回-1。
     */
    public static long toBitSize(String size) {
        if (Validator.isEmpty(size))
            return -1L;

        double value = toDouble(size.substring(0, size.length() - 1).trim(), -1);
        char unit = size.toLowerCase().charAt(size.length() - 1);
        if (unit == 't')
            return Math.round(value * 1024 * 1024 * 1024 * 1024);

        if (unit == 'g')
            return Math.round(value * 1024 * 1024 * 1024);

        if (unit == 'm')
            return Math.round(value * 1024 * 1024);

        if (unit == 'k')
            return Math.round(value * 1024);

        return Math.round(toDouble(size.trim(), -1));
    }

    /**
     * 将对象转化为int数值。
     *
     * @param object 要转化的对象。
     * @return 数值；如果转化失败则返回0。
     */
    public static int toInt(Object object) {
        if (Validator.isEmpty(object))
            return 0;

        try {
            return Integer.parseInt(object.toString());
        } catch (Exception e) {
            Logger.warn(e, "将对象[{}]转化为数值时发生异常！", object);

            return 0;
        }
    }

    /**
     * 将整数字符串转化为整数数组。
     *
     * @param string 整数字符串，数值间以逗号区分。
     * @return 整数数组。
     */
    public static int[] toInts(String string) {
        String[] array = toArray(string, ",");
        int[] ints = new int[array.length];
        for (int i = 0; i < ints.length; i++)
            ints[i] = toInt(array[i]);

        return ints;
    }

    /**
     * 将对象转化为long数值。
     *
     * @param object 要转化的对象。
     * @return 数值；如果转化失败则返回0。
     */
    public static long toLong(Object object) {
        if (Validator.isEmpty(object))
            return 0L;

        try {
            return Long.parseLong(object.toString());
        } catch (Exception e) {
            Logger.warn(e, "将对象[{}]转化为数值时发生异常！", object);

            return 0L;
        }
    }

    public static String toBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    // 解密
    public static String fromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 将对象转化为float数值。
     *
     * @param object 要转化的对象。
     * @return 数值；如果转化失败则返回0。
     */
    public static float toFloat(Object object) {
        if (Validator.isEmpty(object))
            return 0.0F;

        try {
            return Float.parseFloat(object.toString());
        } catch (Exception e) {
            Logger.warn(e, "将对象[{}]转化为数值时发生异常！", object);

            return 0.0F;
        }
    }

    /**
     * 将对象转化为double数值。
     *
     * @param object 要转化的对象。
     * @return 数值；如果转化失败则返回0。
     */
    public static double toDouble(Object object) {
        if (Validator.isEmpty(object))
            return 0.0D;

        try {
            return Double.parseDouble(object.toString());
        } catch (Exception e) {
            Logger.warn(e, "将对象[{}]转化为数值时发生异常！", object);

            return 0.0D;
        }
    }

    /**
     * 按指定格式将日期值格式化为字符串。
     *
     * @param date   要进行格式化的日期值。
     * @param format 目标格式。
     * @return 格式化后的日期值字符串。
     */
    public static String toString(Date date, String format) {
        return date == null ? "" : getSimpleDateFormat(format).format(date);
    }

    /**
     * 使用默认格式将日期对象转化为日期值。
     *
     * @param date 日期对象。
     * @return 日期值。如果格式不匹配则返回null。
     */
    public static Date toDate(Object date) {
        if (Validator.isEmpty(date))
            return null;

        if (date instanceof Date)
            return (Date) date;

        if (date instanceof String) {
            String dateFormat = getDateFormat(LocaleUtil.toLocale(localeAndCounty));
            String string = (String) date;

            return toDate(string, string.length() == dateFormat.length() ? dateFormat : getDateTimeFormat(LocaleUtil.toLocale(localeAndCounty)));
        }

        return null;
    }

    /**
     * 将日期字符串按指定格式转化为日期值。
     *
     * @param date   日期字符串。
     * @param format 字符串格式。
     * @return 日期值。如果格式不匹配则返回null。
     */
    public static Date toDate(String date, String format) {
        if (Validator.isEmpty(date) || Validator.isEmpty(format) || date.length() != format.length())
            return null;

        try {
            return getSimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            Logger.warn(e, "使用格式[{}]将字符串[{}]转化为日期值时发生异常！", format, date);

            return null;
        }
    }

    /**
     * 将字符串进行URL编码转换。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则默认使用UTF-8编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    public static String encodeUrl(String string, String charset) {
        if (string == null)
            return null;

        try {
            return URLEncoder.encode(string, Validator.isEmpty(charset) ? CHAR_SET : charset);
        } catch (UnsupportedEncodingException e) {
            Logger.warn(e, "将字符串[{}]进行URL编码[{}]转换时发生异常！", string, charset);

            return string;
        }
    }

    /**
     * 将字符串进行URL解码。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则默认使用UTF-8编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    public static String decodeUrl(String string, String charset) {
        if (string == null)
            return null;

        try {
            return URLDecoder.decode(string, Validator.isEmpty(charset) ? CHAR_SET : charset);
        } catch (UnsupportedEncodingException e) {
            Logger.warn(e, "将字符串[{}]进行URL解码[{}]转换时发生异常！", string, charset);

            return string;
        }
    }

    /**
     * 将字符串转化为首字母小写的字符串。
     *
     * @param string 要转化的字符串。
     * @return 转化后的字符串；如果转化失败则返回原值。
     */
    public static String toFirstLowerCase(String string) {
        return toFirstCase(string, 'A', 'Z', 'a' - 'A');
    }

    /**
     * 将字符串转化为首字母大写的字符串。
     *
     * @param string 要转化的字符串。
     * @return 转化后的字符串；如果转化失败则返回原值。
     */
    public static String toFirstUpperCase(String string) {
        return toFirstCase(string, 'a', 'z', 'A' - 'a');
    }

    /**
     * 将驼峰格式转化成下划线格式或横杠或其它格式
     *
     * @param source
     * @param pattern
     * @return
     */
    public static String camelToCustom(String source, String pattern) {
        if (source == null || "".equals(source.trim())) {
            return "";
        }
        int len = source.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = source.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append(pattern);
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将下划线格式或横杠或其它格式转驼峰
     *
     * @param source
     * @param pattren
     * @return
     */
    public static String customToCamel(String source, String pattren) {
        if (source == null || "".equals(source.trim())) {
            return "";
        }
        int len = source.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            String c = String.valueOf(source.charAt(i));
            if (c.equals(pattren)) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(source.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 设置并获取过期时间
     *
     * @param day 几天后过期
     * @return 返回时间
     */
    public static Date getExpiration(int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 将时间转换成中文
     *
     * @return 日期
     */
    public static String toDateCn(Date date) {
        if (date == null)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(new Date());
        if (calendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH))
            return "今天";
        else if (calendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH) + 1)
            return "明天";
        else if (calendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH) + 2)
            return "后天";
        else if (calendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH) - 1)
            return "昨天";

        return calendar.get(Calendar.DAY_OF_MONTH) + "号";
    }

    /**
     * 保护手机号码隐私
     *
     * @param phone 手机号码
     * @return 部分隐藏的手机号码
     */
    public static String toProtectPhone(String phone) {
        if (phone == null)
            return "";
        char[] phones = phone.toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 11; i++) {
            if (i > 2 && i < 8) {
                phones[i] = '*';
            }
            stringBuffer.append(phones[i]);
        }

        return stringBuffer.toString();
    }

    public static boolean isNullOrEmpty(String target) {
        return target == null || "".equals(target);
    }

    private static String toBitSize(double size, int pattern) {
        if (size >= 1024 && pattern < BIT_SIZE_FORMAT.length - 1)
            return toBitSize(size / 1024, pattern + 1);

        return toString(size, BIT_SIZE_FORMAT[pattern]);
    }

    protected static double toDouble(String string, double failure) {
        try {
            return Double.parseDouble(string);
        } catch (Exception e) {
            Logger.warn(e, "将字符串[{}]转化为浮点数时发生异常！", string);

            return failure;
        }
    }

    protected static String getDateFormat(Locale locale) {
        // LocaleUtil locale = context.getLocale();
        // 2016-01-06 戴文清做了修改
        if (locale == null)
            locale = Locale.CHINESE;
        String format = localeDateMap.get(locale);
        if (format == null) {
            // format = message.get("commons.format.date");
            localeDateMap.put(locale, localeDate);

            return localeDate;
        }

        return format;
    }

    protected static String getDateTimeFormat(Locale locale) {
        // Locale locale = context.getLocale();
        // 2016-01-06 戴文清做了修改
        if (locale == null)
            locale = Locale.CHINESE;
        String format = localDatetimeMap.get(locale);
        if (format == null) {
            // format = message.get("commons.format.date-time");
            localDatetimeMap.put(locale, localeDatetime);
            return localeDatetime;
        }

        return format;
    }

    protected static SimpleDateFormat getSimpleDateFormat(String format) {
        SimpleDateFormat sdf = dateFormatMap.get(format);
        if (sdf == null) {
            sdf = new SimpleDateFormat(format);
            dateFormatMap.put(format, sdf);
        }

        return sdf;
    }

    protected static String toFirstCase(String string, char start, char end, int shift) {
        if (Validator.isEmpty(string))
            return string;

        char ch = string.charAt(0);
        if (ch < start || ch > end)
            return string;

        char[] chars = string.toCharArray();
        chars[0] += shift;

        return new String(chars);
    }
}
