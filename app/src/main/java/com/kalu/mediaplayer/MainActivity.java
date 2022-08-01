package com.kalu.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * description:
 * created by kalu on 2021/11/23
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //检测当前是用的哪个播放器
//        Object factory = PlayerUtils.getCurrentPlayerFactory();
//        if (factory instanceof ExoPlayerFactory) {
//            mTvTitle.setText("视频内核：" + " (ExoPlayer)");
//            setTitle(getResources().getString(R.string.app_name) + " (ExoPlayer)");
//        } else if (factory instanceof IjkPlayerFactory) {
//            mTvTitle.setText("视频内核：" + " (IjkPlayer)");
//        } else if (factory instanceof MediaPlayerFactory) {
//            mTvTitle.setText("视频内核：" + " (MediaPlayer)");
//        } else {
//            mTvTitle.setText("视频内核：" + " (unknown)");
//        }

        // main_toggle
        findViewById(R.id.main_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
        // main_three00
        findViewById(R.id.main_three00).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, false);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });
        // main_three0
        findViewById(R.id.main_three0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SEEK, 10 * 1000L);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, false);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });
        // main_three1
        findViewById(R.id.main_three1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SEEK, 10 * 1000L);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_MAX, 10 * 1000L);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, false);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });
        // main_three2
        findViewById(R.id.main_three2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SEEK, 10 * 1000L);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_MAX, 10 * 1000L);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LOOP, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, false);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });
        // main_mp4
        findViewById(R.id.main_mp4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://cdn.dfhon.com/599670896134659e143a892ebc0a6110.mp4");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SEEK, 100 * 1000L);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, false);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });
        // udp
        findViewById(R.id.main_udp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "udp://@172.10.10.106:1234");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://114.118.13.20:8197/data_source/test/001/06/06.m3u8");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // main_srt
        findViewById(R.id.main_m3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "android.resource://" + getPackageName() + "/" + R.raw.test2);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "android.resource://" + getPackageName() + "/" + R.raw.test1);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "http://145.239.255.77/gtsubtitle/127%20Hours/English.srt");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                startActivity(intent);
            }
        });

        // main_srt
        findViewById(R.id.main_srt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://i51.lanzoug.com/030317bb/2022/03/03/5f39a7dc8e5d55f3288f14a1f4cc14a2.zip?st=8qotZ42dvTIDrvn5lYRp7A&e=1646301044&b=UmRbLlQ3AG4CegQ_aUHAENAJ5WnwAaAB2&fi=63751215&pid=61-185-224-115&up=2&mp=0&co=1");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "https://i21.lanzoug.com:446/030317bb/2022/03/03/b48a500fd06c83c2d648e11ca9d0af0a.zip?st=quSRNIR10JTOdG-IHss-1A&e=1646301085&b=AjQLflU2AipRdwcjVXECKFR7XGkHdg_c_c&fi=63751222&pid=61-185-224-115&up=2&mp=0&co=1");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://cdn.dfhon.com/599670896134659e143a892ebc0a6110.mp4");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "http://145.239.255.77/gtsubtitle/127%20Hours/English.srt");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                startActivity(intent);
            }
        });

        // main_mobile
        findViewById(R.id.main_mobile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://cdn.dfhon.com/599670896134659e143a892ebc0a6110.mp4");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, false);
//                startActivity(intent);
            }
        });

        // live m3u8 http
        findViewById(R.id.main_live_m3u8_http).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://hkss3.phoenixtv.com/fs/pcc.stream/playlist.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://115.182.96.25/gitv_live/CCTV-1-HD/CCTV-1-HD.m3u8?p=GITV&area=AH_CMCC");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://114.118.13.20:8080/movie/33/playlist.m3u8");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_SRT, "http://145.239.255.77/gtsubtitle/127%20Hours/English.srt");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                startActivity(intent);
            }
        });

        // live m3u8 https
        findViewById(R.id.main_live_m3u8_https).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://files.cochat.lenovo.com/download/dbb26a06-4604-3d2b-bb2c-6293989e63a7/55deb281e01b27194daf6da391fdfe83.mp4");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rtmp
        findViewById(R.id.main_rtmp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtmp://media3.scctv.net/live/scctv_800");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rtsp
        findViewById(R.id.main_rtsp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rtp
        findViewById(R.id.main_rtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtp://239.111.205.131:5140");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rmvb
        findViewById(R.id.main_rmvb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://i71.lanzoug.com/021900bb/2022/02/19/8669ddd6992f062b9bc6d583aad7eefd.zip?st=BlBavCatPoSINgcVnfwYzw&e=1645204518&b=Bi1aMwVrUS1XdwAzUXtXOwc7AC9ROAVmUm8IYlZ_aB38COQx8&fi=62701467&pid=111-19-95-209&up=2&mp=0&co=1");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // 无声音, audio/mpeg-L2, video/avc
        findViewById(R.id.main_sound_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://10.43.111.4/88888888/16/20211118/269273284/index.m3u8?servicetype=0");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // 有声音 audio/mp4a-latm, video/avc
        findViewById(R.id.main_sound_has).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://10.43.111.4/88888888/16/20210528/269218487/index.m3u8?servicetype=0");
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

//        // rtp_live
//        findViewById(R.id.main_rtp_live).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtp://239.111.205.131:5140");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // rtmp
//        findViewById(R.id.main_rtmp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtmp://58.200.131.2:1935/livetv/cctv1");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // rtsp
//        findViewById(R.id.main_rtsp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // m3u8
//        findViewById(R.id.main_m3u8).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://10.43.111.4/88888888/16/20201212/269077387/index.m3u8?servicetype=0");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://114.118.13.20:8080/movie/xingjitansuoyizhiban/playlist.m3u8");
////                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://114.118.13.20:8080/movie/yaolinglingguoyuban/playlist.m3u8");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // m3u8-live
//        findViewById(R.id.main_m3u8_live).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), lib.kalu.mediaplayer.TestActivity.class);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_URL, "http://115.182.96.25/gitv_live/CCTV-1-HD/CCTV-1-HD.m3u8?p=GITV&area=AH_CMCC");
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_LIVE, true);
//                intent.putExtra(lib.kalu.mediaplayer.TestActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // exo
//        findViewById(R.id.main_exo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setChangeVideoType(PlayerType.PlatformType.EXO);
//            }
//        });
//
//        // android
//        findViewById(R.id.main_android).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setChangeVideoType(PlayerType.PlatformType.NATIVE);
//            }
//        });
    }
}
