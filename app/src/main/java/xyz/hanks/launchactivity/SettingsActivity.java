package xyz.hanks.launchactivity;


import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.tv_version) TextView tvVersion;
    @BindString(R.string.version) String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tvVersion.setText(versionName + BuildConfig.VERSION_NAME);
    }


    @OnClick(R.id.tv_update)
    public void update() {

    }

    @OnClick(R.id.tv_support)
    public void support() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }
}
