package com.kalu.mediaplayer.newPlayer.list;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kalu.mediaplayer.ConstantVideo;

import com.kalu.mediaplayer.R;

import java.util.List;

import lib.kalu.mediaplayer.listener.OnVideoStateListener;
import lib.kalu.mediaplayer.ui.config.PlayerType;
import lib.kalu.mediaplayer.ui.config.VideoInfoBean;
import lib.kalu.mediaplayer.ui.player.VideoLayout;
import lib.kalu.mediaplayer.ui.widget.DefaultController;

/**
 * 连续播放列表视频
 * 意思是说播放完了第一个，接着播放第二个，第三个……
 */
public class ContinuousVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoLayout mVideoPlayerLayout;
    private Button mBtnScaleNormal;
    private Button mBtnScale169;
    private Button mBtnScale43;
    private Button mBtnCrop;
    private Button mBtnGif;
    private List<VideoInfoBean> data = ConstantVideo.getVideoList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_video);
        initFindViewById();
        initVideoPlayer();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoPlayerLayout != null) {
            mVideoPlayerLayout.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoPlayerLayout != null) {
            mVideoPlayerLayout.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayerLayout != null) {
            mVideoPlayerLayout.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoPlayerLayout == null || !mVideoPlayerLayout.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void initFindViewById() {
        mVideoPlayerLayout = findViewById(R.id.video_player);
        mBtnScaleNormal = findViewById(R.id.btn_scale_normal);
        mBtnScale169 = findViewById(R.id.btn_scale_169);
        mBtnScale43 = findViewById(R.id.btn_scale_43);
        mBtnCrop = findViewById(R.id.btn_crop);
        mBtnGif = findViewById(R.id.btn_gif);
    }

    private void initVideoPlayer() {
        DefaultController controller = new DefaultController(this);
        //设置视频背景图
        Glide.with(this).load(R.drawable.image_default).into(controller.getThumb());
        //设置控制器
        mVideoPlayerLayout.setController(controller);
        mVideoPlayerLayout.setUrl(ConstantVideo.VideoPlayerList[0]);
        mVideoPlayerLayout.start();

        //监听播放结束
        mVideoPlayerLayout.addOnStateChangeListener(new OnVideoStateListener() {
            private int mCurrentVideoPosition;

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == PlayerType.StateType.STATE_BUFFERING_PLAYING) {
                    if (data != null) {
                        mCurrentVideoPosition++;
                        if (mCurrentVideoPosition >= data.size()) return;
                        mVideoPlayerLayout.release();
                        //重新设置数据
                        VideoInfoBean videoBean = data.get(mCurrentVideoPosition);
                        mVideoPlayerLayout.setUrl(videoBean.getVideoUrl());
                        mVideoPlayerLayout.setController(controller);
                        //开始播放
                        mVideoPlayerLayout.start();
                    }
                }
            }
        });
    }

    private void initListener() {
        mBtnScaleNormal.setOnClickListener(this);
        mBtnScale169.setOnClickListener(this);
        mBtnScale43.setOnClickListener(this);
        mBtnCrop.setOnClickListener(this);
        mBtnGif.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == mBtnScale169) {
            mVideoPlayerLayout.setScreenScaleType(PlayerType.ScaleType.SCREEN_SCALE_16_9);
        } else if (v == mBtnScaleNormal) {
            mVideoPlayerLayout.setScreenScaleType(PlayerType.ScaleType.SCREEN_SCALE_DEFAULT);
        } else if (v == mBtnScale43) {
            mVideoPlayerLayout.setScreenScaleType(PlayerType.ScaleType.SCREEN_SCALE_4_3);
        } else if (v == mBtnCrop) {

        } else if (v == mBtnGif) {

        }
    }
}
