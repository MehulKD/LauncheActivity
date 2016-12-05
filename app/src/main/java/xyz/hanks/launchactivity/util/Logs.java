package xyz.hanks.launchactivity.util;

import android.util.Log;

import xyz.hanks.launchactivity.BuildConfig;


/**
 * Created by hanks on 2016/11/19.
 */

public class Logs {
    private static final String TAG = "FliLog";
    private static boolean logOpen = BuildConfig.DEBUG;

    public static void i(String s) {
        if (logOpen) {
            Log.i(TAG, s);
        }
    }

    public static void w(String s) {
        if (logOpen) {
            Log.w(TAG, s);
        }
    }

    public static void d(String s) {
        if (logOpen) {
            Log.d(TAG, s);
        }
    }

    public static void e(String s) {
        if (logOpen) {
            Log.e(TAG, s);
        }
    }

    public static void e(Throwable e) {
        if (logOpen) {
            Log.e(TAG, e.getMessage());
        }
    }
}
