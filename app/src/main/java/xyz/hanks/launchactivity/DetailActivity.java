package xyz.hanks.launchactivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import xyz.hanks.launchactivity.util.FileUtils;
import xyz.hanks.launchactivity.util.ShortcutUtils;
import xyz.hanks.launchactivity.util.ToastUtils;

import static xyz.hanks.launchactivity.util.FileUtils.drawableToBitmap;

/**
 * 列出隐式意图
 * Created by hanks on 2016/12/5.
 */

public class DetailActivity extends BaseActivity {

    public static final String EXTRA_APPLICATION = "extra_application";

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private IntentInfoAdapter adapter;
    private List<IntentInfo> data = new ArrayList<>();
    private ApplicationInfo apkInfo;

    public static void start(Context context, ApplicationInfo applicationInfo) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra(EXTRA_APPLICATION, applicationInfo);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        apkInfo = getIntent().getParcelableExtra(EXTRA_APPLICATION);
        if (mToolbar != null) {
            getSupportActionBar().setTitle(apkInfo.loadLabel(getPackageManager()));
            mToolbar.setSubtitle(apkInfo.packageName);
            mToolbar.setSubtitleTextAppearance(this, R.style.ToolbarSmallSubtitleAppearance);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IntentInfoAdapter();
        recyclerView.setAdapter(adapter);

        try {
            ApkParser parser = new ApkParser(new File(apkInfo.sourceDir));
            AndroidManifest androidManifest = new AndroidManifest(parser.getApkMeta(), parser.getManifestXml());
            for (AndroidComponent component : androidManifest.getComponents()) {
                boolean exported = component.exported;
                if (!exported) {
                    continue;
                }

                if (component.type != AndroidComponent.TYPE_ACTIVITY) {
                    continue;
                }

                List<IntentFilter> intentFilters = component.intentFilters;
                if (intentFilters == null || intentFilters.size() == 0) {
                    continue;
                }
                String name = component.name;
                if (name.startsWith(".")) {
                    name = apkInfo.packageName + name;
                }
                for (IntentFilter intentFilter : intentFilters) {
                    List<String> actions = intentFilter.actions;
                    List<String> categories = intentFilter.categories;
                    List<IntentFilter.IntentData> dataList = intentFilter.dataList;
                    IntentInfo intentInfo = new IntentInfo();
                    intentInfo.packageName = apkInfo.packageName;
                    intentInfo.className = name;
                    intentInfo.actionList = actions;
                    intentInfo.categoryList = categories;
                    intentInfo.dataList = dataList;
                    data.add(intentInfo);
                }
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.app_source:
                XmlSourceViewerActivity.start(this, apkInfo);
                break;
        }
        return true;
    }

    @NonNull
    private Intent getIntent(IntentInfo intentInfo) {
        Intent intent = new Intent();

        if (!TextUtils.isEmpty(intentInfo.packageName) && !TextUtils.isEmpty(intentInfo.className)) {
            intent.setComponent(new ComponentName(intentInfo.packageName, intentInfo.className));
        }

        if (!TextUtils.isEmpty(intentInfo.action)) {
            intent.setAction(intentInfo.action);
        }

        if (intentInfo.categoryList != null) {
            for (String s : intentInfo.categoryList) {
                intent.addCategory(s);
            }
        }

        if (!TextUtils.isEmpty(intentInfo.data)) {
            if (!TextUtils.isEmpty(intentInfo.mimeType)) {
                intent.setDataAndType(Uri.parse(intentInfo.data), intentInfo.mimeType);
            } else {
                intent.setData(Uri.parse(intentInfo.data));
            }
        }

        if (intentInfo.extra != null) {
            intent.putExtras(intentInfo.extra);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        return intent;
    }

    class IntentInfo {
        String packageName;
        String className;
        String action;
        String data;
        String mimeType;
        List<String> categoryList;
        Bundle extra;

        // extra
        List<String> actionList;
        List<IntentFilter.IntentData> dataList;
    }

    private class IntentInfoAdapter extends RecyclerView.Adapter<IntentInfoHolder> {
        @Override
        public IntentInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intent_info, parent, false);
            return new IntentInfoHolder(view);
        }

        @Override
        public void onBindViewHolder(IntentInfoHolder holder, int position) {
            IntentInfo intentInfo = data.get(position);
            holder.tvActivityName.setText(intentInfo.className);
            holder.layoutAction.removeAllViews();
            holder.layoutCategory.removeAllViews();
            holder.layoutData.removeAllViews();

            for (String s : intentInfo.actionList) {
                holder.layoutAction.addCheckEditor(s);
            }
            for (String s : intentInfo.categoryList) {
                holder.layoutCategory.addCheckEditor(s);
            }
            for (IntentFilter.IntentData s : intentInfo.dataList) {
                String scheme = TextUtils.isEmpty(s.scheme) ? "file" : s.scheme;
                String host = TextUtils.isEmpty(s.host) ? "" : s.host;
                String port = TextUtils.isEmpty(s.port) ? "" : ":" + s.port;
                String path = TextUtils.isEmpty(s.path) ? "" : "/" + s.path;
                String type = TextUtils.isEmpty(s.mimeType) ? "" : s.mimeType;

                if (!TextUtils.isEmpty(type)) {
                    holder.layoutMimeType.addCheckEditor(type);
                }
                holder.layoutData.addCheckEditor("", String.format("%s://%s%s%s", scheme, host, port, path));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class IntentInfoHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_activity_name) TextView tvActivityName;
        @BindView(R.id.layout_action) CheckBoxLayout layoutAction;
        @BindView(R.id.layout_category) CheckBoxLayout layoutCategory;
        @BindView(R.id.layout_mimeType) CheckBoxLayout layoutMimeType;
        @BindView(R.id.layout_data) CheckBoxLayout layoutData;
        @BindView(R.id.layout_extra) CheckBoxLayout layoutExtra;
        @BindView(R.id.cb_component) CheckBox cbComponent;
        @BindView(R.id.btn_shortcut) ImageView btnShortcut;
        @BindView(R.id.btn_preview) ImageView btnPreview;
        @BindView(R.id.btn_share) ImageView btnShare;

        public IntentInfoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tv_extra)
        public void addExtra() {
            layoutExtra.addCheckEditor("");
        }

        @OnClick(R.id.btn_shortcut)
        public void addShortcut() {
            final Context context = itemView.getContext();
            final String shortcutName = apkInfo.loadLabel(getPackageManager()).toString();
            final Bitmap shortcutIcon = FileUtils.mergeBitmap(drawableToBitmap(apkInfo.loadIcon(getPackageManager())), drawableToBitmap(getResources().getDrawable(R.mipmap.ic_launcher)));

            View dialogView = View.inflate(context, R.layout.layout_dialog, null);
            final ImageView ivIcon = (ImageView) dialogView.findViewById(R.id.iv_icon);
            final EditText tvName = (EditText) dialogView.findViewById(R.id.tv_name);
            ivIcon.setImageBitmap(shortcutIcon);
            tvName.setText(shortcutName);
            new AlertDialog.Builder(context)
                    .setTitle(R.string.title_add_shortcut)
                    .setView(dialogView)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = tvName.getText().toString();
                            Bitmap icon = FileUtils.drawableToBitmap(ivIcon.getDrawable());
                            ShortcutUtils.installShortcut(context, name, icon, getIntent(getIntentInfoByChecked()));
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }

        private IntentInfo getIntentInfoByChecked(){
            IntentInfo intentInfo = new IntentInfo();
            if (cbComponent.isChecked()) {
                intentInfo.packageName = apkInfo.packageName;
                intentInfo.className = tvActivityName.getText().toString();
            }
            for (String s : layoutAction.getSelectedString()) {
                intentInfo.action = s;
            }
            intentInfo.categoryList = layoutCategory.getSelectedString();

            List<String> dataList = layoutData.getSelectedString();
            List<String> mimeTypeList = layoutMimeType.getSelectedString();
            if (dataList.size() > 0) {
                intentInfo.data = dataList.get(0);
            }
            if (mimeTypeList.size() > 0) {
                intentInfo.mimeType = mimeTypeList.get(0);
            }

            layoutExtra.getSelectedString();
            Bundle bundle = new Bundle();
            return  intentInfo;
        }

        @OnClick(R.id.btn_preview)
        public void previewIntent() {
            Intent intent = getIntent(getIntentInfoByChecked());
            if (intent.resolveActivity(getPackageManager()) != null) {
                try {
                    itemView.getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtils.show(getString(R.string.not_found_activity));
            }
        }
    }
}
