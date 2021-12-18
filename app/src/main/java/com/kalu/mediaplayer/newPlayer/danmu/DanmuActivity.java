package com.kalu.mediaplayer.newPlayer.danmu;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.kalu.mediaplayer.BaseActivity;
import com.kalu.mediaplayer.ConstantVideo;

import com.kalu.mediaplayer.R;

import cn.ycbjie.ycstatusbarlib.bar.StateAppBar;
import lib.kalu.mediaplayer.listener.OnVideoStateListener;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.widget.player.VideoLayout;
import lib.kalu.mediaplayer.widget.ControllerDefault;


/**
 * @author yc
 */
public class DanmuActivity extends BaseActivity implements View.OnClickListener {

    private VideoLayout mVideoPlayerLayout;
    private LinearLayout mLayout;
    private Button mBtnShow;
    private Button mBtnHide;
    private Button mBtnAddDan;
    private Button mBtnAddCustomDan;

    private MyDanmakuView mMyDanmakuView;

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
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoPlayerLayout == null || !mVideoPlayerLayout.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_danmu_player;
    }

    @Override
    public void initView() {
        StateAppBar.translucentStatusBar(this, true);
        initFindViewById();

        ControllerDefault controller = new ControllerDefault(this);
        mMyDanmakuView = new MyDanmakuView(this);
        controller.add(mMyDanmakuView);
        //设置控制器
        mVideoPlayerLayout.setController(controller);
        mVideoPlayerLayout.setScreenScaleType(PlayerType.ScaleType.SCREEN_SCALE_16_9);
        mVideoPlayerLayout.start(ConstantVideo.VideoPlayerList[0]);
        mVideoPlayerLayout.addOnStateChangeListener(new OnVideoStateListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == PlayerType.StateType.STATE_PREPARED) {
                    simulateDanmu();
                } else if (playState == PlayerType.StateType.STATE_BUFFERING_PLAYING) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
        });
    }

    private void initFindViewById() {
        mVideoPlayerLayout = findViewById(R.id.video_player);
        mLayout = findViewById(R.id.layout);
        mBtnShow = findViewById(R.id.btn_show);
        mBtnHide = findViewById(R.id.btn_hide);
        mBtnAddDan = findViewById(R.id.btn_add_dan);
        mBtnAddCustomDan = findViewById(R.id.btn_add_custom_dan);

    }

    @Override
    public void initListener() {
        mBtnShow.setOnClickListener(this);
        mBtnHide.setOnClickListener(this);
        mBtnAddDan.setOnClickListener(this);
        mBtnAddCustomDan.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show:
                mMyDanmakuView.show();
                break;
            case R.id.btn_hide:
                mMyDanmakuView.hide();
                break;
            case R.id.btn_add_dan:
                mMyDanmakuView.addDanmakuWithDrawable();
                break;
            case R.id.btn_add_custom_dan:
                mMyDanmakuView.addDanmaku("小杨逗比自定义弹幕~", true);
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler();

    /**
     * 模拟弹幕
     */
    private void simulateDanmu() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMyDanmakuView.addDanmaku("awsl", false);
                mHandler.postDelayed(this, 100);
            }
        });
    }


}
