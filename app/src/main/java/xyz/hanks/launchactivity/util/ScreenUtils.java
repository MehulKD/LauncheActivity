package xyz.hanks.launchactivity.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import xyz.hanks.launchactivity.LaunchApp;


/**
 * Created by hanks on 16/6/30.
 */
public class ScreenUtils {

    public static int dpToPx(float valueInDp) {
        DisplayMetrics metrics = LaunchApp.app.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static int getDeviceWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) LaunchApp.app.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

}
