package xyz.hanks.launchactivity.util;

import android.content.Context;
import android.content.SharedPreferences;

import xyz.hanks.launchactivity.LaunchApp;


/**
 * SharedPreferences 相关操作
 * Created by hanks on 16/4/5.
 */
public class SpUtils {

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "note";


    public static SharedPreferences getSp() {
        return LaunchApp.app.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void save(String key, Object value) {
        SharedPreferences.Editor editor = getSp().edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
    }

    public static <T> T get(String key, T defaultValue) {
        SharedPreferences sp = getSp();
        Object value = null;
        if (defaultValue instanceof Boolean) {
            value = sp.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof String) {
            value = sp.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Float) {
            value = sp.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            value = sp.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Integer) {
            value = sp.getInt(key, (Integer) defaultValue);
        }
        return (T) value;
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(String key) {
        SharedPreferences.Editor editor = getSp().edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 是否已经存在该 key
     */
    public static boolean contains(String key) {
        return getSp().contains(key);
    }


    /**
     * 清除所有数据
     */
    public static void clear() {
        SharedPreferences.Editor editor = getSp().edit();
        editor.clear();
        editor.apply();
    }

}
