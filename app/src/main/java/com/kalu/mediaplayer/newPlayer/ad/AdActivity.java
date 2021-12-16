package com.kalu.mediaplayer.newPlayer.ad;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kalu.mediaplayer.ConstantVideo;

import com.kalu.mediaplayer.R;

import lib.kalu.mediaplayer.listener.OnVideoStateListener;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.widget.player.VideoLayout;
import lib.kalu.mediaplayer.util.BaseToast;
import lib.kalu.mediaplayer.widget.CustomCenterController;

public class AdActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoLayout mVideoPlayerLayout;
    private Button mBtnScaleNormal;
    private Button mBtnScale169;
    private Button mBtnScale43;
    private Button mBtnCrop;
    private Button mBtnGif;
    private static final String URL_AD = "https://gslb.miaopai.com/stream/IR3oMYDhrON5huCmf7sHCfnU5YKEkgO2.mp4";
    CustomCenterController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_video);
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
        controller = new CustomCenterController(this);
        AdControlView adControlView = new AdControlView(this);
        adControlView.setListener(new AdControlView.AdControlListener() {
            @Override
            public void onAdClick() {
                BaseToast.showRoundRectToast(getApplicationContext(), "广告点击跳转");
            }

            @Override
            public void onSkipAd() {
                playVideo();
            }
        });
        controller.addControlComponent(adControlView);
        //设置视频背景图
        Glide.with(this).load(R.drawable.image_default).into(controller.getPrepare());
        //设置控制器
        mVideoPlayerLayout.setController(controller);
//        HttpProxyCacheServer cacheServer = ProxyVideoCacheManager.getProxy(this);
//        String proxyUrl = cacheServer.getProxyUrl(URL_AD);
//        HttpProxyCacheServer server = new HttpProxyCacheServer(this);
//        String proxyVideoUrl = server.getProxyUrl(URL_AD);


        mVideoPlayerLayout.start(URL_AD);
        //监听播放结束
        mVideoPlayerLayout.addOnStateChangeListener(new OnVideoStateListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == PlayerType.StateType.STATE_BUFFERING_PLAYING) {
                    playVideo();
                }
            }
        });
    }


    /**
     * 播放正片
     */
    private void playVideo() {
        mVideoPlayerLayout.release();
        controller.removeAllControlComponent();
        controller.addDefaultControlComponent("正片");
        //开始播放
        mVideoPlayerLayout.start(ConstantVideo.VideoPlayerList[0]);
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
