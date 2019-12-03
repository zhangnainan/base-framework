/**
 *
 */
package com.sg.base.util;

import java.util.Random;
import java.util.UUID;

/**
 * @author dwq
 */
public final class Generator {
    protected static Random random = new Random();

    /**
     * 生成一个随机字符串。
     *
     * @param length 字符串长度。
     * @return 随机字符串。
     */
    public static String random(int length) {
        StringBuilder sb = new StringBuilder();
        for (; sb.length() < length; ) {
            int n = Math.abs(random.nextInt()) % 'z';
            if ((n >= '0' && n <= '9') || (n >= 'A' && n <= 'Z') || (n >= 'a' && n <= 'z'))
                sb.append((char) n);
        }

        return sb.toString();
    }

    /**
     * 生成一个随机整数。
     *
     * @param min 最小值。
     * @param max 最大值。
     * @return 随机整数。
     */
    public static int random(int min, int max) {
        if (min >= max)
            return (min + max) >> 1;

        return Math.abs(random.nextInt()) % (max - min + 1) + min;
    }

    /**
     * 生成一个UUID随机数。
     *
     * @return UUID随机数。
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
