package xyz.hanks.launchactivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.dongliu.apk.parser.ApkParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.hanks.launchactivity.model.ApkInfo;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private AppInfoAdapter adapter;
    private List<ApkInfo> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
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
            holder.ivIcon.setImageDrawable(data.get(position).getIcon());
            holder.tvName.setText(data.get(position).getAppName());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class AppInfoHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final ImageView ivIcon;

        public AppInfoHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XmlSourceViewerActivity.start(MainActivity.this, data.get(getAdapterPosition()).getPath());
                    DetailActivity.start(MainActivity.this, data.get(getAdapterPosition()).getPath());
                }
            });
        }
    }
}
