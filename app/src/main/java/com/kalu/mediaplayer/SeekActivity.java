package com.kalu.mediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.component.ComponentLoading;
import lib.kalu.mediaplayer.core.component.ComponentSeek;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

public class SeekActivity extends Activity {

    public static String INTENT_URL = "intent_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);

        findViewById(R.id.seek_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout view = findViewById(R.id.seek_video);
                view.showComponent(ComponentSeek.class);
            }
        });

        PlayerLayout playerViewGroup = findViewById(R.id.seek_video);
        playerViewGroup.addComponent(new ComponentSeek(this));
        playerViewGroup.addComponent(new ComponentLoading(this));
        String url = getIntent().getStringExtra(INTENT_URL);
        playerViewGroup.start(new StartBuilder.Builder().build(), url);
    }
}