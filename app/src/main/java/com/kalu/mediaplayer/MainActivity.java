package com.kalu.mediaplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lib.kalu.mediaplayer.ExoplayerActivity;
import lib.kalu.mediaplayer.core.kernel.video.factory.PlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.exo.ExoMediaPlayer;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.exo.ExoPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.ijk.IjkMediaPlayer;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.ijk.IjkPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.media.AndroidMediaPlayer;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.media.MediaPlayerFactory;
import lib.kalu.mediaplayer.config.PlayerConfig;
import lib.kalu.mediaplayer.config.PlayerConfigManager;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.util.PlayerUtils;

import java.lang.reflect.Field;

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

        // live m3u8 http
        findViewById(R.id.main_live_m3u8_http).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://115.182.96.25/gitv_live/CCTV-1-HD/CCTV-1-HD.m3u8?p=GITV&area=AH_CMCC");
//                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://114.118.13.20:8080/movie/33/playlist.m3u8");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // live m3u8 https
        findViewById(R.id.main_live_m3u8_https).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rtmp
        findViewById(R.id.main_rtmp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "rtmp://media3.scctv.net/live/scctv_800");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rtsp
        findViewById(R.id.main_rtsp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rtp
        findViewById(R.id.main_rtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "rtp://239.111.205.131:5140");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // mp4
        findViewById(R.id.main_mp4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "https://i71.lanzoug.com/021900bb/2022/02/19/822a6b6ffa33b8c777b53dba9fb65e17.zip?st=f2itOCtuVUSqV3e7QtL0wQ&e=1645204709&b=Um0MfFVhU3hTfAQ7UXE_c&fi=62701757&pid=111-19-95-209&up=2&mp=0&co=1");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // rmvb
        findViewById(R.id.main_rmvb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "https://i71.lanzoug.com/021900bb/2022/02/19/8669ddd6992f062b9bc6d583aad7eefd.zip?st=BlBavCatPoSINgcVnfwYzw&e=1645204518&b=Bi1aMwVrUS1XdwAzUXtXOwc7AC9ROAVmUm8IYlZ_aB38COQx8&fi=62701467&pid=111-19-95-209&up=2&mp=0&co=1");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // 无声音, audio/mpeg-L2, video/avc
        findViewById(R.id.main_sound_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://10.43.111.4/88888888/16/20211118/269273284/index.m3u8?servicetype=0");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

        // 有声音 audio/mp4a-latm, video/avc
        findViewById(R.id.main_sound_has).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://10.43.111.4/88888888/16/20210528/269218487/index.m3u8?servicetype=0");
                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
                startActivity(intent);
            }
        });

//        // rtp_live
//        findViewById(R.id.main_rtp_live).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
//                intent.putExtra(ExoplayerActivity.INTENT_URL, "rtp://239.111.205.131:5140");
//                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
//                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // rtmp
//        findViewById(R.id.main_rtmp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
//                intent.putExtra(ExoplayerActivity.INTENT_URL, "rtmp://58.200.131.2:1935/livetv/cctv1");
//                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // rtsp
//        findViewById(R.id.main_rtsp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
//                intent.putExtra(ExoplayerActivity.INTENT_URL, "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov");
//                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // m3u8
//        findViewById(R.id.main_m3u8).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
////                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8");
//                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://10.43.111.4/88888888/16/20201212/269077387/index.m3u8?servicetype=0");
////                intent.putExtra(ExoplayerActivity.INTENT_URL, "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
////                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://114.118.13.20:8080/movie/xingjitansuoyizhiban/playlist.m3u8");
////                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://114.118.13.20:8080/movie/yaolinglingguoyuban/playlist.m3u8");
//                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // m3u8-live
//        findViewById(R.id.main_m3u8_live).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
//                intent.putExtra(ExoplayerActivity.INTENT_URL, "http://115.182.96.25/gitv_live/CCTV-1-HD/CCTV-1-HD.m3u8?p=GITV&area=AH_CMCC");
//                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
//                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
//                startActivity(intent);
//            }
//        });
//        // udp
//        findViewById(R.id.main_udp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ExoplayerActivity.class);
//                intent.putExtra(ExoplayerActivity.INTENT_URL, "udp://@224.255.0.224:10000");
//                intent.putExtra(ExoplayerActivity.INTENT_LIVE, true);
//                intent.putExtra(ExoplayerActivity.INTENT_PREPARE_IMAGE_RESOURCE, R.drawable.ic_test_prepare);
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
