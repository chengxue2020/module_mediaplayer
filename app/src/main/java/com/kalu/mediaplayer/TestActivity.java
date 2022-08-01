package com.kalu.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

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
                String path = BaseApplication.getModelFilePath(getApplicationContext(), "test.mp3");
                VideoLayout videoView = findViewById(R.id.video);
                videoView.toggleMusic(getApplicationContext(), path);
            }
        });

        // 原音
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout videoView = findViewById(R.id.video);
                videoView.toggleMusic(getApplicationContext(), null);
            }
        });
    }

    private void open() {
        VideoLayout videoView = findViewById(R.id.video);
        videoView.start(0, 0, 0, "https://cdn.qupeiyin.cn/2021-02-28/1614506793915md121nwz.mp4");
    }
}