package xyz.hanks.launchactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hanks on 2016/12/5.
 */

public class DetailActivity extends Activity {

    @BindView(R.id.tv_detail) TextView tvDetail;

    public static void start(Context context, String manifest) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra("manifest", manifest);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String manifest = getIntent().getStringExtra("manifest");
        if (!TextUtils.isEmpty(manifest)) {
            tvDetail.setText(manifest);
        }
    }
}
