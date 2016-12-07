package xyz.hanks.launchactivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import xyz.hanks.launchactivity.util.ToastUtils;

/**
 * Created by hanks on 2016/12/5.
 */

public class DetailActivity extends Activity {

    public static final String APK_PATH = "apk_path";
    public static final String PACKAGE_NAME = "package_name";

    @BindView(R.id.tv_detail) TextView tvDetail;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private String apkPath;
    private String packageName;
    private IntentInfoAdapter adapter;

    public static void start(Context context, String apkPath, String packageName) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra(APK_PATH, apkPath);
        starter.putExtra(PACKAGE_NAME, packageName);
        context.startActivity(starter);
    }
    private List<IntentInfo> data = new ArrayList<>();

    class IntentInfo{
        String packageName;
        String className;
        List<String> action;
        List<String> category;
        List<IntentFilter.IntentData> data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);


        apkPath = getIntent().getStringExtra(APK_PATH);
        packageName = getIntent().getStringExtra(PACKAGE_NAME);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IntentInfoAdapter();
        recyclerView.setAdapter(adapter);

        try {
            ApkParser parser = new ApkParser(new File(apkPath));
            AndroidManifest androidManifest = new AndroidManifest(parser.getApkMeta(), parser.getManifestXml());
            StringBuilder sb = new StringBuilder();
            for (AndroidComponent component : androidManifest.getComponents()) {
                boolean exported = component.exported;
                if (!exported) {
                     continue;
                }

                if (component.type != AndroidComponent.TYPE_ACTIVITY){
                    continue;
                }

                List<IntentFilter> intentFilters = component.intentFilters;
                if (intentFilters == null || intentFilters.size() == 0) {
                    continue;
                }
                System.out.println(String.format("============= component: %s ===================",component.name));
                String name = component.name;
                if (name.startsWith(".")){
                    name = packageName + name;
                }
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

                    IntentInfo intentInfo = new IntentInfo();
                    intentInfo.packageName = packageName;
                    intentInfo.className = name;
                    intentInfo.action = actions;
                    intentInfo.category = categories;
                    intentInfo.data = dataList;
                    data.add(intentInfo);

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
            tvDetail.setText(sb.toString());
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class IntentInfoAdapter extends RecyclerView.Adapter<IntentInfoHolder> {
        @Override
        public IntentInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intent_info,parent,false);
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

    private class IntentInfoHolder extends RecyclerView.ViewHolder{

        private final TextView tvInfo;

        public IntentInfoHolder(View itemView) {
            super(itemView);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            tvInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentInfo intentInfo = data.get(getAdapterPosition());
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(intentInfo.packageName,intentInfo.className));
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
                            port = ":"+intentData.port;
                        }

                        if (intentData.mimeType == null) {
                            Uri uri = Uri.parse(scheme+host+port+"/baidu.com");
                            intent.setDataAndType(uri,"text/plain");
                        }

                        if (intentData.mimeType!=null && intentData.mimeType.contains("image/")){
                            //将项目图片转换为uri
                            String imagePath = "/" + Environment.getExternalStorageDirectory() + "/oooo.png";
                            Uri uri = Uri.parse(scheme+host+port+imagePath);
                            intent.setDataAndType(uri,intentData.mimeType);
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                        }
                        if (intentData.mimeType!=null && intentData.mimeType.contains("text/")){
                            intent.putExtra(Intent.EXTRA_SUBJECT, "消息标题");
                            intent.putExtra(Intent.EXTRA_TEXT, "消息内容");
                            intent.setType(intentData.mimeType);
                        }
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        try {
                            v.getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        ToastUtils.show("找不到");
                    }
                }
            });
        }
    }
}
