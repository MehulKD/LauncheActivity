package xyz.hanks.launchactivity;

import android.app.Application;

/**
 * Created by hanks on 2016/12/6.
 */

public class LaunchApp extends Application {
    public static LaunchApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
