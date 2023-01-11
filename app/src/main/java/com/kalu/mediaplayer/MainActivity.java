package com.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.TestActivity;

/**
 * description:
 * created by kalu on 2021/11/23
 */
public class MainActivity extends Activity {

    private final boolean isLive() {
        String url = getUrl();
        return "http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com".equals(url);
    }

    private final String getUrl() {
        EditText editText = findViewById(R.id.edit);
        String s = editText.getText().toString();
//        String s = "http://video.cdn.aizys.com/video_vp09_fail.mp4";
//                String s = "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400";
//                String s = "udp://@224.255.0.128:10000";
//        String s = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4";
//        String s = "https://media.w3.org/2010/05/sintel/trailer.mp4";
//        String s = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        return s;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, getUrl());
                intent.putExtra(TestActivity.INTENT_LIVE, isLive());
//                intent.putExtra(TestActivity.INTENT_SEEK, 100000L); // 10s
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
                intent.putExtra(FullActivity.INTENT_URL, getUrl());
//                intent.putExtra(FullActivity.INTENT_LIVE, true);
                startActivity(intent);
            }
        });
        findViewById(R.id.main_seek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeekActivity.class);
                intent.putExtra(SeekActivity.INTENT_URL, getUrl());
                startActivity(intent);
            }
        });
    }
}
