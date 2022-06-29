package com.kalu.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.view.VideoLayout;
import lib.kalu.mediaplayer.listener.OnMediaStateListener;

public class QunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qun);

        // https://cdn.qupeiyin.cn/2021-02-28/1614502984140md126nwz.mp4

        findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(0);
            }
        });

        findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(2000);
            }
        });

        findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(4000);
            }
        });
    }

    private void start(int seek) {

        VideoLayout videoLayout = findViewById(R.id.video);
        videoLayout.setOnMediaStateListener(new OnMediaStateListener() {
            @Override
            public void onPlayStateChanged(int state) {
                if (state == PlayerType.StateType.STATE_END) {
                    videoLayout.repeat();
                }
            }
        });
        videoLayout.start(seek, "https://cdn.qupeiyin.cn/2021-02-28/1614502984140md126nwz.mp4");
    }
}