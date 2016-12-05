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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.model.AndroidComponent;
import net.dongliu.apk.parser.model.AndroidManifest;
import net.dongliu.apk.parser.model.IntentFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //@BindView(R.id.text) TextView tvInfo;
    @BindView(R.id.action) EditText etAction;
    @BindView(R.id.category) EditText etCategory;
    @BindView(R.id.data) EditText etData;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private AppInfoAdapter adapter;
    private List<String> data = new ArrayList<>();

    @OnClick(R.id.btn_launch)
    public void launch() {
        try {
            String actions = etAction.getText().toString();
            String catagories = etCategory.getText().toString();
            String datas = etData.getText().toString();

            Intent intent = new Intent();

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

            PackageManager packageManager = getPackageManager();
            List activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;

            if (!isIntentSafe) {
                Toast.makeText(this, "未找到", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppInfoAdapter();
        recyclerView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                PackageManager pm = getPackageManager();
                StringBuilder sb = new StringBuilder();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                for (PackageInfo packageInfo : packageInfos) {
                    sb.setLength(0);
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
                        sb.append("======"+ appInfo.appName + "=======\n");

                        try {
                            //ApkParser parser = ApkParser.create(pm, "com.android.settings");
                            ApkParser parser = new ApkParser(apkfile);
                            String manifestXml = parser.getManifestXml();
                            try {
                                AndroidManifest androidManifest = new AndroidManifest(parser.getApkMeta(), manifestXml);
                                for (AndroidComponent component : androidManifest.getComponents()) {
                                    boolean exported = component.exported;
                                    if (!exported) {
                                       // continue;
                                    }
                                    List<IntentFilter> intentFilters = component.intentFilters;
                                    if (intentFilters==null || intentFilters.size()==0) {
                                        continue;
                                    }
                                    System.out.println("============= component ===================");
                                    String name = component.name;
                                    String process = component.process;
                                    int type = component.type;
                                    System.out.println("exported = " + exported);
                                    System.out.println("name = " + name);
                                    System.out.println("type = " + type);
                                    System.out.println("process = " + process);
                                    sb.append("name = " + name).append("\n");
                                    sb.append("type = " + type).append("\n");
                                    sb.append("process = " + process).append("\n");
                                    sb.append("exported = " + exported).append("\n");

                                    for (IntentFilter intentFilter : intentFilters) {
                                        sb.append("-----intentFilter------").append("\n");
                                        List<String> actions = intentFilter.actions;
                                        List<String> categories = intentFilter.categories;
                                        List<IntentFilter.IntentData> dataList = intentFilter.dataList;
                                        for (String action : actions) {
                                            System.out.println("action = " + action);
                                            sb.append("action = " + action).append("\n");
                                        }
                                        for (String category : categories) {
                                            System.out.println("category = " + category);
                                            sb.append("category = " + category).append("\n");
                                        }
                                        for (IntentFilter.IntentData intentData : dataList) {
                                            System.out.println("intentData = " + intentData.toString());
                                            sb.append("intentData = " + intentData.toString()).append("\n");
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            sb.append("\n");
                            data.add(sb.toString());
                        } catch (Exception e) {

                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

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

    private class AppInfoAdapter extends RecyclerView.Adapter<AppInfoHolder> {
        @Override
        public AppInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_info, parent, false);
            return new AppInfoHolder(view);
        }

        @Override
        public void onBindViewHolder(AppInfoHolder holder, int position) {
            holder.tvInfo.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class AppInfoHolder extends RecyclerView.ViewHolder {

        private final TextView tvInfo;

        public AppInfoHolder(View itemView) {
            super(itemView);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            tvInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     DetailActivity.start(MainActivity.this,data.get(getAdapterPosition()));
                }
            });
        }
    }
}
