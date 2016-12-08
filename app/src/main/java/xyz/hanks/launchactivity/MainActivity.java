package xyz.hanks.launchactivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.hanks.launchactivity.model.ApkInfo;

public class MainActivity extends BaseActivity {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private AppInfoAdapter adapter;
    private List<ApkInfo> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setTitle(getString(R.string.app_name));

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AppInfoAdapter();
        recyclerView.setAdapter(adapter);
        getData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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
                    //if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
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
                    appInfo.setApplicationInfo(app);
                    data.add(appInfo);
                    //}
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected int getNavigationIcon() {
        return R.drawable.ic_menu_white_24dp;
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
                    ApkInfo apkInfo = data.get(getAdapterPosition());
                    //XmlSourceViewerActivity.start(MainActivity.this, apkInfo.getPath());
                    DetailActivity.start(MainActivity.this, apkInfo.getApplicationInfo());
                }
            });
        }
    }
}
