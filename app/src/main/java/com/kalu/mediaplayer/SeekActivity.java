package com.kalu.mediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import lib.kalu.mediaplayer.core.controller.ControllerEmpty;
import lib.kalu.mediaplayer.core.controller.component.ComponentLoading;
import lib.kalu.mediaplayer.core.controller.component.ComponentSeek;
import lib.kalu.mediaplayer.core.player.VideoLayout;

public class SeekActivity extends Activity {

    public static String INTENT_URL = "intent_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);

        findViewById(R.id.seek_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLayout view = findViewById(R.id.seek_video);
                view.showComponentSeek();
            }
        });

        ControllerEmpty controller = new ControllerEmpty(this);
        controller.addComponent(new ComponentSeek(this));
        controller.addComponent(new ComponentLoading(this));

        VideoLayout videoView = findViewById(R.id.seek_video);
        videoView.setControllerLayout(controller);

        String url = getIntent().getStringExtra(INTENT_URL);
        videoView.start(url);
    }
}