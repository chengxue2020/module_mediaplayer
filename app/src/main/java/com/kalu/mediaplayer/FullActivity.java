package com.kalu.mediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import lib.kalu.mediaplayer.core.controller.ControllerEmpty;
import lib.kalu.mediaplayer.core.controller.component.ComponentLoading;
import lib.kalu.mediaplayer.core.controller.component.ComponentSeek;
import lib.kalu.mediaplayer.core.video.player.VideoLayout;

public class FullActivity extends Activity {

    public static String INTENT_LIVE = "intent_live";
    public static String INTENT_URL = "intent_url";

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
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VideoLayout view = findViewById(R.id.full_video);
                        long duration = view.getDuration();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "=>" + duration, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

        ControllerEmpty controller = new ControllerEmpty(this);
        controller.addComponent(new ComponentSeek(this));
        controller.addComponent(new ComponentLoading(this));

        VideoLayout videoView = findViewById(R.id.full_video);
        videoView.setControllerLayout(controller);

        String url = getIntent().getStringExtra(INTENT_URL);
        boolean isLive = getIntent().getBooleanExtra(INTENT_LIVE, false);
        if (isLive) {
            videoView.startLive(url);
        } else {
            videoView.startLoop(url);
        }
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