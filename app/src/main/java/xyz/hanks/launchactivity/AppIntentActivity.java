package xyz.hanks.launchactivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hanks on 2016/12/6.
 */

public class AppIntentActivity extends Activity {

    public final static String WEIXIN_CHATTING_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.chatting.profile";//微信聊天
    public final static String WEIXIN_SNS_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.plugin.sns.timeline";//微信朋友圈
    public final static String WEIXIN_VIDIO_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.chatting.voip.video";//微信视频


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intent);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_timeline)
    public void circle(){
        shareToCircle();
    }

    @OnClick(R.id.btn_chat)
    public void friend(){
        shareToFriend();
    }


    private void  shareToFriend(){
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "文字内容");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("/sdcard/a.jpg")));
        startActivity(intent);
    }

    private void  shareToCircle(){
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "文字内容");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("/sdcard/a.jpg")));
        startActivity(intent);
    }

}
