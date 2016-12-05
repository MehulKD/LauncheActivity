package xyz.hanks.launchactivity.util;

import android.content.Context;
import android.widget.Toast;

import xyz.hanks.launchactivity.LaunchApp;

/**
 * Toast 的工具类
 * Created by hanks on 16/4/5.
 */
public class ToastUtils {

    public static Context getContext() {
        return LaunchApp.app;
    }

    public static void show(int resId) {
        show(getContext().getResources().getText(resId));
    }

    public static void show(CharSequence text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showL(int resId) {
        showL(getContext().getResources().getText(resId));
    }

    public static void showL(CharSequence text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void showError(CharSequence text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
