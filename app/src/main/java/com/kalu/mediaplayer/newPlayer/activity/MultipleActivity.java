package com.kalu.mediaplayer.newPlayer.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kalu.mediaplayer.ConstantVideo;

import com.kalu.mediaplayer.R;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.mediaplayer.ui.player.VideoBuilder;
import lib.kalu.mediaplayer.ui.player.VideoLayout;
import lib.kalu.mediaplayer.ui.ui.view.DefaultController;


public class MultipleActivity extends AppCompatActivity {

    private static final String VOD_URL_1 = ConstantVideo.VideoPlayerList[3];
    private static final String VOD_URL_2 = ConstantVideo.VideoPlayerList[0];
    private VideoLayout player1;
    private VideoLayout player2;
    private List<VideoLayout> mVideoViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_play);

        initFindViewById();
        initVideoPlayer();
    }

    private void initFindViewById() {
        player1 = findViewById(R.id.video_player1);
        player2 = findViewById(R.id.video_player2);
    }

    private void initVideoPlayer() {
        //必须设置
        player1.setUrl(VOD_URL_1);
        VideoBuilder.Builder builder = VideoBuilder.newBuilder();
        builder.setEnableAudioFocus(false);
        VideoBuilder videoPlayerBuilder = new VideoBuilder(builder);
        player1.setVideoBuilder(videoPlayerBuilder);
        DefaultController controller1 = new DefaultController(this);
        player1.setController(controller1);
        mVideoViews.add(player1);

        //必须设置
        player2.setUrl(VOD_URL_2);
        VideoBuilder.Builder builder2 = VideoBuilder.newBuilder();
        builder.setEnableAudioFocus(false);
        VideoBuilder videoPlayerBuilder2 = new VideoBuilder(builder2);
        player2.setVideoBuilder(videoPlayerBuilder2);
        DefaultController controller2 = new DefaultController(this);
        player2.setController(controller2);
        mVideoViews.add(player2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (VideoLayout vv : mVideoViews) {
            vv.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (VideoLayout vv : mVideoViews) {
            vv.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (VideoLayout vv : mVideoViews) {
            vv.release();
        }
    }

    @Override
    public void onBackPressed() {
        for (VideoLayout vv : mVideoViews) {
            if (vv.onBackPressed())
                return;
        }
        super.onBackPressed();
    }
}
