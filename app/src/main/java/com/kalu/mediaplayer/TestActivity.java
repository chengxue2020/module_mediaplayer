package com.kalu.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import lib.kalu.mediaplayer.config.builder.BundleBuilder;
import lib.kalu.mediaplayer.core.view.VideoLayout;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // https://cdn.qupeiyin.cn/2021-02-28/1614506793915md121nwz.mp4
        open();

        // 配音
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout videoView = findViewById(R.id.video);
                videoView.enableExternalMusic(true, false);
            }
        });

        // 原音
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout videoView = findViewById(R.id.video);
                videoView.enableExternalMusic(false, false);
            }
        });

        // 原音
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout videoView = findViewById(R.id.video);
//                videoView.pause();
                videoView.setSpeed(2.0f);
//                videoView.resume();
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout videoView = findViewById(R.id.video);
//                videoView.pause();
                videoView.setSpeed(1.0f);
//                videoView.resume();
            }
        });
    }

    private void open() {
        VideoLayout videoView = findViewById(R.id.video);
        String path = BaseApplication.getModelFilePath(getApplicationContext(), "test.mp3");
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        builder.setExternalMusicUrl(path);
        builder.setExternalMusicLoop(true);
        builder.setExternalMusicAuto(true);
        builder.setLoop(true);
//        videoView.start(builder.build(), "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-uni4934e7b/c4d93960-5643-11eb-a16f-5b3e54966275.m3u8");
        videoView.start(builder.build(), "https://cdn.qupeiyin.cn/2021-02-28/1614506793915md121nwz.mp4");
    }
}