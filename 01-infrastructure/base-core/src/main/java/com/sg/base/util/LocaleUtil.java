package com.sg.base.util;

import java.util.Locale;

/**
 * LocaleUtil
 *
 * @author Dai Wenqing
 * @date 2016/1/6
 */
public final class LocaleUtil {
    private final static Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public final static String IETF_SEPARATOR = "-";

    public final static String SEPARATOR = "_";

    public final static String EMPTY_STRING = "";


    public static Locale toLocale(String language) {
        if (!Converter.isNullOrEmpty(language)) {
            return langToLocale(language, SEPARATOR);
        }
        return DEFAULT_LOCALE;
    }

    private static Locale langToLocale(String lang, String separator) {
        if (Converter.isNullOrEmpty(lang)) {
            return DEFAULT_LOCALE;
        }
        String language = EMPTY_STRING;
        String country = EMPTY_STRING;
        String variant = EMPTY_STRING;

        int i1 = lang.indexOf(IETF_SEPARATOR);
        if (i1 < 0) {
            language = lang;
        } else {
            language = lang.substring(0, i1);
            ++i1;
            int i2 = lang.indexOf(separator, i1);
            if (i2 < 0) {
                country = lang.substring(i1);
            } else {
                country = lang.substring(i1, i2);
                variant = lang.substring(i2 + 1);
            }
        }

        if (language.length() == 2) {
            language = language.toLowerCase();
        } else {
            language = EMPTY_STRING;
        }

        if (country.length() == 2) {
            country = country.toUpperCase();
        } else {
            country = EMPTY_STRING;
        }

        if ((variant.length() > 0) &&
                ((language.length() == 2) || (country.length() == 2))) {
            variant = variant.toUpperCase();
        } else {
            variant = EMPTY_STRING;
        }

        return new Locale(language, country, variant);
    }
}
