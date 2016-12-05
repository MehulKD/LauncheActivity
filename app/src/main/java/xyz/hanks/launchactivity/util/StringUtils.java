package xyz.hanks.launchactivity.util;

/**
 * Created by hanks on 2016/11/29.
 */

public class StringUtils {
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isEmptyTrim(String s) {
        return s == null || s.trim().length() == 0;
    }
}
