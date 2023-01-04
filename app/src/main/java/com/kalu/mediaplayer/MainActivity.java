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

    private final String getUrl() {
        EditText editText = findViewById(R.id.edit);
        String s = editText.getText().toString();
//                String s = "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400";
//                String s = "udp://@224.255.0.128:10000";
//        String s = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
//        String s = "http://10.43.111.4/PLTV/88888888/224/3221226306/index.m3u8?servicetype=1";
//        String s = "http://10.43.111.4/88888888/16/20221212/269516746/index.m3u8?servicetype=0";
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

//        // main_mpeg_L2
//        findViewById(R.id.main_mpeg_L2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://111.20.105.192:6610/000000001000/5000000008000023254/index.m3u8?channel-id=bestzb&Contentid=5000000008000023254&livemode=1&stbId=3");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // udp
//        findViewById(R.id.main_udp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "udp://@224.224.224.224:10000");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // 4k
//        findViewById(R.id.main_4k).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://111.20.105.192:6610/000000001000/5000000008000023254/index.m3u8?channel-id=bestzb&Contentid=5000000008000023254&livemode=1&stbId=3");
//                startActivity(intent);
//            }
//        });
//        // 8k
//        findViewById(R.id.main_8k).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://111.62.156.15/down.brtvcloud.com/brtv8kapptv/brtv8kapp8ktv.m3u8");
//                startActivity(intent);
//            }
//        });
//        // mkv
//        findViewById(R.id.main_mkv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
//                intent.putExtra(FullActivity.INTENT_URL, "https://sample-videos.com/video123/mkv/720/big_buck_bunny_720p_2mb.mkv");
//                startActivity(intent);
//            }
//        });
//        // 3gp
//        findViewById(R.id.main_3gp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
//                intent.putExtra(FullActivity.INTENT_URL, "https://sample-videos.com/video123/3gp/144/big_buck_bunny_144p_1mb.3gp");
//                startActivity(intent);
//            }
//        });
//        // flv
//        findViewById(R.id.main_flv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
//                intent.putExtra(FullActivity.INTENT_URL, "https://sample-videos.com/video123/flv/720/big_buck_bunny_720p_1mb.flv");
//                startActivity(intent);
//            }
//        });
//        // mp4
//        findViewById(R.id.main_mp4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
//                intent.putExtra(FullActivity.INTENT_URL, "https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_1mb.mp4");
//                startActivity(intent);
//            }
//        });
//        // main_full1
//        findViewById(R.id.main_full1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
//                intent.putExtra(FullActivity.INTENT_URL, "http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com");
//                intent.putExtra(FullActivity.INTENT_LIVE, true);
//                startActivity(intent);
//            }
//        });
//        // main_full2
//        findViewById(R.id.main_full2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), FullActivity.class);
//                intent.putExtra(FullActivity.INTENT_URL, "https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_1mb.mp4");
//                intent.putExtra(FullActivity.INTENT_LIVE, false);
//                startActivity(intent);
//            }
//        });
//        // main_toggle
//        findViewById(R.id.main_toggle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(intent);
//            }
//        });
//        // main_three00
//        findViewById(R.id.main_three00).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // main_three0
//        findViewById(R.id.main_three0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SEEK, 10 * 1000L);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // main_three1
//        findViewById(R.id.main_three1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SEEK, 10 * 1000L);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_MAX, 10 * 1000L);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // main_three2
//        findViewById(R.id.main_three2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SEEK, 10 * 1000L);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_MAX, 10 * 1000L);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // main_srt
//        findViewById(R.id.main_srt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://i51.lanzoug.com/030317bb/2022/03/03/5f39a7dc8e5d55f3288f14a1f4cc14a2.zip?st=8qotZ42dvTIDrvn5lYRp7A&e=1646301044&b=UmRbLlQ3AG4CegQ_aUHAENAJ5WnwAaAB2&fi=63751215&pid=61-185-224-115&up=2&mp=0&co=1");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "https://i21.lanzoug.com:446/030317bb/2022/03/03/b48a500fd06c83c2d648e11ca9d0af0a.zip?st=quSRNIR10JTOdG-IHss-1A&e=1646301085&b=AjQLflU2AipRdwcjVXECKFR7XGkHdg_c_c&fi=63751222&pid=61-185-224-115&up=2&mp=0&co=1");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://cdn.dfhon.com/599670896134659e143a892ebc0a6110.mp4");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "http://145.239.255.77/gtsubtitle/127%20Hours/English.srt");
//                startActivity(intent);
//            }
//        });
//
//        // main_mobile
//        findViewById(R.id.main_mobile).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(intent);
////                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://cdn.dfhon.com/599670896134659e143a892ebc0a6110.mp4");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, false);
////                startActivity(intent);
//            }
//        });
//
//        // live m3u8 http
//        findViewById(R.id.main_live_m3u8_http).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://hkss3.phoenixtv.com/fs/pcc.stream/playlist.m3u8");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://115.182.96.25/gitv_live/CCTV-1-HD/CCTV-1-HD.m3u8?p=GITV&area=AH_CMCC");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://114.118.13.20:8080/movie/33/playlist.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "http://145.239.255.77/gtsubtitle/127%20Hours/English.srt");
//                startActivity(intent);
//            }
//        });
//
//        // live m3u8 https
//        findViewById(R.id.main_live_m3u8_https).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://files.cochat.lenovo.com/download/dbb26a06-4604-3d2b-bb2c-6293989e63a7/55deb281e01b27194daf6da391fdfe83.mp4");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//
//        // rtmp
//        findViewById(R.id.main_rtmp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtmp://media3.scctv.net/live/scctv_800");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//
//        // rtsp
//        findViewById(R.id.main_rtsp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//
//        // rtp
//        findViewById(R.id.main_rtp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtp://239.111.205.131:5140");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//
//        // rmvb
//        findViewById(R.id.main_rmvb).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://i71.lanzoug.com/021900bb/2022/02/19/8669ddd6992f062b9bc6d583aad7eefd.zip?st=BlBavCatPoSINgcVnfwYzw&e=1645204518&b=Bi1aMwVrUS1XdwAzUXtXOwc7AC9ROAVmUm8IYlZ_aB38COQx8&fi=62701467&pid=111-19-95-209&up=2&mp=0&co=1");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
    }
}
