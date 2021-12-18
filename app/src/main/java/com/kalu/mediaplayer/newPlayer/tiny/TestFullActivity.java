package com.kalu.mediaplayer.newPlayer.tiny;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.kalu.mediaplayer.BaseActivity;
import com.kalu.mediaplayer.ConstantVideo;

import com.kalu.mediaplayer.R;
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.controller.standard.ControllerStandard;
import lib.kalu.mediaplayer.widget.player.VideoLayout;


/**
 * @author yc
 */
public class TestFullActivity extends BaseActivity implements View.OnClickListener {

    private VideoLayout mVideoPlayerLayout;
    private Button mBtnTiny1;
    private Button mBtnTiny2;

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

    @Override
    public int getContentView() {
        return R.layout.activity_full_video1;
    }

    @Override
    public void initView() {
        StateAppBar.translucentStatusBar(this, true);
        adaptCutoutAboveAndroidP();
        mVideoPlayerLayout = findViewById(R.id.video_player);
        mBtnTiny1 = (Button) findViewById(R.id.btn_tiny_1);
        mBtnTiny2 = (Button) findViewById(R.id.btn_tiny_2);

        ControllerStandard controller = new ControllerStandard(this);
        //设置控制器
        mVideoPlayerLayout.setController(controller);
        mVideoPlayerLayout.setScreenScaleType(PlayerType.ScaleType.SCREEN_SCALE_16_9);
        mVideoPlayerLayout.start(ConstantVideo.VideoPlayerList[0]);
    }

    @Override
    public void initListener() {
        mBtnTiny1.setOnClickListener(this);
        mBtnTiny2.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tiny_1:
                mVideoPlayerLayout.startFullScreen();
                break;
            case R.id.btn_tiny_2:
                mVideoPlayerLayout.startTinyScreen();
                break;
            default:
                break;
        }
    }

    private void adaptCutoutAboveAndroidP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }


}
