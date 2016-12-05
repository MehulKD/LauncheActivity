package xyz.hanks.launchactivity.util;

/**
 * Created by hanks on 2016/7/11.
 */
public class PrettyDateUtils {
    // 刚刚
    // 50分钟前
    // 20小时前
    // 6天前
    // 4周前
    // 10个月前
    // 2年前
    private static String JUST_NOW = "刚刚";
    private static String MIN = "分钟前";
    private static String HOUR = "小时前";
    private static String DAY = "天前";
    private static String WEEK = "周前";
    private static String MONTH = "个月前";
    private static String YEAR = "年前";

    private static String MIN_AFTER = "分钟后";
    private static String HOUR_AFTER = "小时后";
    private static String DAY_AFTER = "天后";
    private static String WEEK_AFTER = "周后";
    private static String MONTH_AFTER = "个月后";
    private static String YEAR_AFTER = "年后";

    private static long UNIT_MIN = 60 * 1000;
    private static long UNIT_HOUR = 60 * UNIT_MIN;
    private static long UNIT_DAY = 24 * UNIT_HOUR;
    private static long UNIT_WEEK = 7 * UNIT_DAY;
    private static long UNIT_MONTH = 30 * UNIT_DAY;
    private static long UNIT_YEAR = 365 * UNIT_DAY;

    public static String format(long time) {
        long now = System.currentTimeMillis();
        if (now >= time) { // 以前的时间
            long duration = now - time;
            if (duration < UNIT_MIN) {
                return JUST_NOW;
            } else if (duration < UNIT_HOUR) {
                return duration / UNIT_MIN + MIN;
            } else if (duration < UNIT_DAY) {
                return duration / UNIT_HOUR + HOUR;
            } else if (duration < UNIT_WEEK) {
                return duration / UNIT_DAY + DAY;
            } else if (duration < UNIT_MONTH) {
                return duration / UNIT_WEEK + WEEK;
            } else if (duration < UNIT_YEAR) {
                return duration / UNIT_MONTH + MONTH;
            } else {
                return duration / UNIT_YEAR + YEAR;
            }
        } else { // 以后的时间
            long duration = time - now;
            if (duration < UNIT_MIN) {
                return JUST_NOW;
            } else if (duration < UNIT_HOUR) {
                return duration / UNIT_MIN + MIN_AFTER;
            } else if (duration < UNIT_DAY) {
                return duration / UNIT_HOUR + HOUR_AFTER;
            } else if (duration < UNIT_WEEK) {
                return duration / UNIT_DAY + DAY_AFTER;
            } else if (duration < UNIT_MONTH) {
                return duration / UNIT_WEEK + WEEK_AFTER;
            } else if (duration < UNIT_YEAR) {
                return duration / UNIT_MONTH + MONTH_AFTER;
            } else {
                return duration / UNIT_YEAR + YEAR_AFTER;
            }
        }
    }
}
