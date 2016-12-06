package xyz.hanks.launchactivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.hanks.launchactivity.model.ApkInfo;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.action) EditText etAction;
    @BindView(R.id.category) EditText etCategory;
    @BindView(R.id.data) EditText etData;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private AppInfoAdapter adapter;
    private List<ApkInfo> data = new ArrayList<>();

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
                        ApkInfo appInfo = new ApkInfo(app.sourceDir);
                        appInfo.setPackageName(packageInfo.packageName);
                        appInfo.setVersionCode(packageInfo.versionCode);
                        appInfo.setVersionName(packageInfo.versionName);
                        appInfo.setUid(app.uid);
                        appInfo.setIcon(app.loadIcon(pm));
                        appInfo.setAppName(app.loadLabel(pm).toString());
                        appInfo.setCacheSize(stats.cacheSize);
                        appInfo.setDataSize(stats.dataSize);
                        try {
                            ApkParser parser = new ApkParser(apkfile);
                            String manifestXml = parser.getManifestXml();
                            appInfo.setManifest(manifestXml);
                            data.add(appInfo);


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


    private class AppInfoAdapter extends RecyclerView.Adapter<AppInfoHolder> {
        @Override
        public AppInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_info, parent, false);
            return new AppInfoHolder(view);
        }

        @Override
        public void onBindViewHolder(AppInfoHolder holder, int position) {
            holder.tvInfo.setText(data.get(position).getAppName());
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
                    XmlSourceViewerActivity.start(MainActivity.this, data.get(getAdapterPosition()).getPath());
                    DetailActivity.start(MainActivity.this, data.get(getAdapterPosition()).getPath());
                }
            });
        }
    }
}
