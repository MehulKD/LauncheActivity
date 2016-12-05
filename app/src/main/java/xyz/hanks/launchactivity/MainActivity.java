package xyz.hanks.launchactivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import net.dongliu.apk.parser.ApkParser;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //@BindView(R.id.text) TextView textView;
    @BindView(R.id.action) EditText etAction;
    @BindView(R.id.category) EditText etCategory;
    @BindView(R.id.data) EditText etData;


    @OnClick(R.id.btn_launch)
    public void launch() {
        Intent intent = new Intent();
        String actions = etAction.getText().toString();
        String catagories = etCategory.getText().toString();
        String datas = etData.getText().toString();


        if (!TextUtils.isEmpty(actions)) {
            intent.setAction(actions);
        }
        if (!TextUtils.isEmpty(catagories)) {
            for (String line : catagories.split("#")) {
                intent.addCategory(line);
            }
        }
        if (!TextUtils.isEmpty(datas)) {
            intent.setData(Uri.parse(datas));
        }

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.text);
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


                StringBuilder sb = new StringBuilder();
                System.out.println("----------------------------");
                sb.append("----------------------------------------------\n");
                try {
                    //ApkParser parser = ApkParser.create(pm, "com.android.settings");
                    ApkParser parser = new ApkParser(apkfile);
                    String manifestXml = parser.getManifestXml();
                    sb.append(manifestXml).append("\n");
                    textView.append(sb.toString());

//                    for (AndroidComponent component : androidManifest.getComponents()) {
//                        boolean exported = component.exported;
//                        if (!exported) {
//                            continue;
//                        }
//                        sb.append("============= component ===================\n");
//                        System.out.println("============= component ===================");
//                        List<IntentFilter> intentFilters = component.intentFilters;
//                        String name = component.name;
//                        String process = component.process;
//                        int type = component.type;
//
//                        System.out.println("name = " + name);
//                        System.out.println("type = " + type);
//                        System.out.println("process = " + process);
//                        sb.append("name = " + name + "\n");
//                        sb.append("type = " + type + "\n");
//                        sb.append("process = " + process + "\n");
//                        for (IntentFilter intentFilter : intentFilters) {
//                            List<String> actions = intentFilter.actions;
//                            List<String> categories = intentFilter.categories;
//                            List<IntentFilter.IntentData> dataList = intentFilter.dataList;
//                            for (String action : actions) {
//                                System.out.println("action = " + action);
//                                sb.append("action = " + action + "\n");
//                            }
//                            for (String category : categories) {
//                                System.out.println("category = " + category);
//                                sb.append("category = " + category + "\n");
//                            }
//                            for (IntentFilter.IntentData intentData : dataList) {
//                                System.out.println("intentData = " + intentData.toString());
//                                sb.append("intentData = " + intentData.toString() + "\n");
//                            }
//                        }
//                        textView.append(sb.toString());
//                    }


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
        public AppEntity(String path) {
            this.path = path;
        }

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

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
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
