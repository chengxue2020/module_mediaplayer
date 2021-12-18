package com.kalu.mediaplayer.newPlayer.ad;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kalu.mediaplayer.R;

import lib.kalu.mediaplayer.core.controller.ControllerWrapper;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.core.controller.impl.ImplComponent;


public class AdControlView extends FrameLayout implements ImplComponent, View.OnClickListener {

    private Context mContext;
    protected TextView mAdTime, mAdDetail;
    protected ImageView mBack, mVolume, mFullScreen, mPlayButton;
    protected AdControlListener mListener;

    private ControllerWrapper mControllerWrapper;

    public AdControlView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AdControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ad_control_view, this, true);
        mAdTime = findViewById(R.id.ad_time);
        mAdDetail = findViewById(R.id.ad_detail);
        mAdDetail.setText("了解详情>");
        mBack = findViewById(R.id.back);
        mBack.setVisibility(GONE);
        mVolume = findViewById(R.id.iv_volume);
        mFullScreen = findViewById(R.id.fullscreen);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mAdTime.setOnClickListener(this);
        mAdDetail.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mVolume.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onAdClick();
            }
        });
    }

    @Override
    public void attach(@NonNull ControllerWrapper controllerWrapper) {
        mControllerWrapper = controllerWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_PLAYING:
                mControllerWrapper.startProgress();
                mPlayButton.setSelected(true);
                break;
            case PlayerType.StateType.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
        }
    }

    @Override
    public void onWindowStateChanged(int playerState) {
        switch (playerState) {
            case PlayerType.WindowType.NORMAL:
                mBack.setVisibility(GONE);
                mFullScreen.setSelected(false);
                break;
            case PlayerType.WindowType.FULL:
                mBack.setVisibility(VISIBLE);
                mFullScreen.setSelected(true);
                break;
        }

        //暂未实现全面屏适配逻辑，需要你自己补全
    }

    @Override
    public void setProgress(int duration, int position) {
        if (mAdTime != null)
            mAdTime.setText(String.format("%s | 跳过", (duration - position) / 1000));
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back | id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_volume) {
            doMute();
        } else if (id == R.id.ad_detail) {
            if (mListener != null) mListener.onAdClick();
        } else if (id == R.id.ad_time) {
            if (mListener != null) mListener.onSkipAd();
        } else if (id == R.id.iv_play) {
            mControllerWrapper.toggle();
        }
    }

    private void doMute() {
        mControllerWrapper.setMute(!mControllerWrapper.isMute());
        mVolume.setImageResource(mControllerWrapper.isMute() ? R.mipmap.ic_launcher : R.mipmap.ic_launcher);
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControllerWrapper.toggleFullScreen(activity);
    }

    public void setListener(AdControlListener listener) {
        this.mListener = listener;
    }

    public interface AdControlListener {

        void onAdClick();

        void onSkipAd();
    }
}
