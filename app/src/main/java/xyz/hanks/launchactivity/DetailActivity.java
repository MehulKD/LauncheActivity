package xyz.hanks.launchactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.model.AndroidComponent;
import net.dongliu.apk.parser.model.AndroidManifest;
import net.dongliu.apk.parser.model.IntentFilter;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hanks on 2016/12/5.
 */

public class DetailActivity extends Activity {

    public static final String APK_PATH = "apk_path";
    @BindView(R.id.tv_detail) TextView tvDetail;

    public static void start(Context context, String apkPath) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra(APK_PATH, apkPath);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String apkPath = getIntent().getStringExtra(APK_PATH);
        try {
            ApkParser parser = new ApkParser(new File(apkPath));
            AndroidManifest androidManifest = new AndroidManifest(parser.getApkMeta(), parser.getManifestXml());
            StringBuilder sb = new StringBuilder();
            for (AndroidComponent component : androidManifest.getComponents()) {
                boolean exported = component.exported;
                if (!exported) {
                    // continue;
                }
                List<IntentFilter> intentFilters = component.intentFilters;
                if (intentFilters == null || intentFilters.size() == 0) {
                    continue;
                }
                System.out.println(String.format("============= component: %s ===================",component.name));
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
            tvDetail.setText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
