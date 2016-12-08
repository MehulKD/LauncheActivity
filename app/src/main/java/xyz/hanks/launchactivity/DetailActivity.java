package xyz.hanks.launchactivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
                    intentInfo.action = actions;
                    intentInfo.category = categories;
                    intentInfo.data = dataList;
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
        intent.setComponent(new ComponentName(intentInfo.packageName, intentInfo.className));
        intent.setAction(intentInfo.action.get(0));
        for (String s : intentInfo.category) {
            intent.addCategory(s);
        }
        for (IntentFilter.IntentData intentData : intentInfo.data) {
            String scheme = "file://";
            String host = "";
            String port = "";
            if (intentData.scheme != null) {
                scheme = intentData.scheme + "://";
            }
            if (intentData.host != null) {
                host = intentData.host;
            }
            if (intentData.port != null) {
                port = ":" + intentData.port;
            }

            if (intentData.mimeType == null) {
                Uri uri = Uri.parse(scheme + host + port + "/baidu.com");
                intent.setDataAndType(uri, "text/plain");
            }

            if (intentData.mimeType != null && intentData.mimeType.contains("image/")) {
                //将项目图片转换为uri
                String imagePath = "/" + Environment.getExternalStorageDirectory() + "/oooo.png";
                Uri uri = Uri.parse(scheme + host + port + imagePath);
                intent.setDataAndType(uri, intentData.mimeType);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
            if (intentData.mimeType != null && intentData.mimeType.contains("text/")) {
                intent.putExtra(Intent.EXTRA_SUBJECT, "消息标题");
                intent.putExtra(Intent.EXTRA_TEXT, "消息内容");
                intent.setType(intentData.mimeType);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    class IntentInfo {
        String packageName;
        String className;
        List<String> action;
        List<String> category;
        List<IntentFilter.IntentData> data;
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
            StringBuilder sb = new StringBuilder();
            sb.append(intentInfo.packageName).append("\n").append(intentInfo.className).append("\n");
            sb.append("actions:\n");
            for (String s : intentInfo.action) {
                sb.append(s).append("\n");
            }
            sb.append("category:\n");
            for (String s : intentInfo.category) {
                sb.append(s).append("\n");
            }
            sb.append("data:\n");
            for (IntentFilter.IntentData s : intentInfo.data) {
                sb.append(s.toString()).append("\n");
            }
            holder.tvInfo.setText(sb.toString());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class IntentInfoHolder extends RecyclerView.ViewHolder {

        private final TextView tvInfo;
        private final Button btnShortcut;
        private final Button btnPreview;

        public IntentInfoHolder(View itemView) {
            super(itemView);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            btnPreview = (Button) itemView.findViewById(R.id.btn_preview);
            btnShortcut = (Button) itemView.findViewById(R.id.btn_shortcut);
            btnShortcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    IntentInfo intentInfo = data.get(getAdapterPosition());
                    final Intent intent = getIntent(intentInfo);

                    final String shortcutName = apkInfo.loadLabel(getPackageManager()).toString();
                    final Bitmap shortcutIcon = FileUtils.mergeBitmap(drawableToBitmap(apkInfo.loadIcon(getPackageManager())), drawableToBitmap(getResources().getDrawable(R.mipmap.ic_launcher)));

                    View dialogView = View.inflate(v.getContext(), R.layout.layout_dialog, null);
                    final ImageView ivIcon = (ImageView) dialogView.findViewById(R.id.iv_icon);
                    final EditText tvName = (EditText) dialogView.findViewById(R.id.tv_name);
                    ivIcon.setImageBitmap(shortcutIcon);
                    tvName.setText(shortcutName);
                    new AlertDialog.Builder(v.getContext())
                            .setTitle(R.string.title_add_shortcut)
                            .setView(dialogView)
                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = tvName.getText().toString();
                                    Bitmap icon = FileUtils.drawableToBitmap(ivIcon.getDrawable());
                                    ShortcutUtils.installShortcut(v.getContext(), name, icon, intent);
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();

                }
            });
            btnPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentInfo intentInfo = data.get(getAdapterPosition());
                    Intent intent = getIntent(intentInfo);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        try {
                            v.getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtils.show(getString(R.string.not_found_activity));
                    }
                }
            });
        }
    }
}
