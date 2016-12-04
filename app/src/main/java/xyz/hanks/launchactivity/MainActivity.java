package xyz.hanks.launchactivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaredrummler.apkparser.ApkParser;
import com.jaredrummler.apkparser.model.AndroidComponent;
import com.jaredrummler.apkparser.model.AndroidManifest;
import com.jaredrummler.apkparser.model.IntentFilter;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {

            ApplicationInfo app = packageInfo.applicationInfo;
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                File apkfile = new File(app.sourceDir);
                PackageStats stats = new PackageStats(packageInfo.packageName);
                AppEntity appInfo = new AppEntity(app.sourceDir);
                appInfo.setPackageName(packageInfo.packageName);
                appInfo.setVersionCode(packageInfo.versionCode);
                appInfo.setVersionName(packageInfo.versionName);
                appInfo.setUid(app.uid);
                appInfo.setIcon(app.loadIcon(pm));
                appInfo.setAppName(app.loadLabel(pm).toString());
                appInfo.setCacheSize(stats.cacheSize);
                appInfo.setDataSize(stats.dataSize);
                System.out.println(appInfo);

                System.out.println("----------------------------");
                try {
                    ApkParser parser = ApkParser.create(pm, "com.android.settings");
                    AndroidManifest androidManifest = parser.getAndroidManifest();
                    for (AndroidComponent component : androidManifest.getComponents()) {
                        boolean exported = component.exported;
                        if (!exported){
                            continue;
                        }
                        System.out.println("============= component ===================");
                        List<IntentFilter> intentFilters = component.intentFilters;
                        String name = component.name;
                        String process = component.process;
                        int type = component.type;
                        System.out.println("name = " + name);
                        System.out.println("type = " + type);
                        System.out.println("process = " + process);
                        for (IntentFilter intentFilter : intentFilters) {
                            List<String> actions = intentFilter.actions;
                            List<String> categories = intentFilter.categories;
                            List<IntentFilter.IntentData> dataList = intentFilter.dataList;
                            for (String action : actions) {
                                System.out.println("action = " + action);
                            }
                            for (String category : categories) {
                                System.out.println("category = " + category);
                            }
                            for (IntentFilter.IntentData intentData : dataList) {
                                System.out.println("intentData = " + intentData.toString());
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    /**
     * APK文件
     */
    public class AppEntity {

        @Override
        public String toString() {
            return "AppEntity{" +
                    "path='" + path + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", versionCode=" + versionCode +
                    ", uid=" + uid +
                    ", cacheSize=" + cacheSize +
                    ", dataSize=" + dataSize +
                    ", icon=" + icon +
                    ", checked=" + checked +
                    ", visible=" + visible +
                    ", appName='" + appName + '\'' +
                    '}';
        }

        private final String path;
        /**
         * APK包名
         */
        private String packageName;
        /**
         * APK版本
         */
        private String versionName;
        /**
         * APK版本号
         */
        private int versionCode;
        /**
         * APK uid
         */
        private int uid;
        /**
         * 缓存数据大小
         */
        private long cacheSize;
        /**
         * 应用数据大小
         */
        private long dataSize;
        /**
         * 图标
         */
        private Drawable icon;
        private boolean checked;
        private boolean visible;
        private String appName;

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public AppEntity(String path) {
            this.path = path;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
        }

        public long getDataSize() {
            return dataSize;
        }

        public void setDataSize(long dataSize) {
            this.dataSize = dataSize;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}
