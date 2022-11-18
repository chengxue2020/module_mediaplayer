package com.kalu.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import lib.kalu.mediaplayer.config.builder.BundleBuilder;
import lib.kalu.mediaplayer.core.view.VideoLayout;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_1mb.mp4
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
        String path = Application.getModelFilePath(getApplicationContext(), "test.mp3");
        BundleBuilder.Builder builder = new BundleBuilder.Builder();
        builder.setExternalMusicUrl(path);
        builder.setExternalMusicLoop(true);
        builder.setExternalMusicAuto(true);
        builder.setLoop(true);
        videoView.start(builder.build(), "https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_1mb.mp4");
    }
}