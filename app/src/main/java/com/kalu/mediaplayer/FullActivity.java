package com.kalu.mediaplayer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import lib.kalu.mediaplayer.core.controller.ControllerEmpty;
import lib.kalu.mediaplayer.core.controller.component.ComponentEnd;
import lib.kalu.mediaplayer.core.controller.component.ComponentError;
import lib.kalu.mediaplayer.core.controller.component.ComponentLoading;
import lib.kalu.mediaplayer.core.controller.component.ComponentSeek;
import lib.kalu.mediaplayer.core.view.VideoLayout;

public class FullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout view = findViewById(R.id.full_video);
                view.startFull();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout view = findViewById(R.id.full_video);
                view.startFloat();
            }
        });

        ComponentSeek loading = new ComponentSeek(this);
        ControllerEmpty controller = new ControllerEmpty(this);
        controller.addComponent(loading);

        VideoLayout videoView = findViewById(R.id.full_video);
        videoView.setControllerLayout(controller);
        videoView.start(0, 0, true, "https://cdn.qupeiyin.cn/2021-02-28/1614506793915md121nwz.mp4");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        VideoLayout videoView = findViewById(R.id.full_video);
        boolean dispatchKeyEvent = videoView.dispatchKeyEvent(event);
        if (dispatchKeyEvent)
            return true;
        return super.dispatchKeyEvent(event);
    }
}