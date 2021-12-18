package com.kalu.mediaplayer.newPlayer.tiny;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kalu.mediaplayer.ConstantVideo;
import com.kalu.mediaplayer.newPlayer.list.OnItemChildClickListener;
import com.kalu.mediaplayer.newPlayer.list.VideoRecyclerViewAdapter;

import com.kalu.mediaplayer.R;

import java.util.List;

import lib.kalu.mediaplayer.listener.OnVideoStateListener;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.config.VideoInfoBean;
import lib.kalu.mediaplayer.widget.player.VideoLayout;
import lib.kalu.mediaplayer.util.PlayerUtils;
import lib.kalu.mediaplayer.widget.ControllerDefault;

/**
 * 小窗播放
 */
public class TinyScreenActivity extends AppCompatActivity implements OnItemChildClickListener {

    private ControllerDefault mController;
    private List<VideoInfoBean> mVideos;
    private LinearLayoutManager mLinearLayoutManager;
    private VideoLayout mVideoPlayerLayout;
    private int mCurPos = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_recycler_view);
        initView();
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


    protected void initView() {
        mVideoPlayerLayout = new VideoLayout(this);
        mVideoPlayerLayout.setOnStateChangeListener(new OnVideoStateListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == PlayerType.StateType.STATE_BUFFERING_PLAYING) {
                    if (mVideoPlayerLayout.isTinyScreen()) {
                        mVideoPlayerLayout.stopTinyScreen();
                        releaseVideoView();
                    }
                }
            }
        });
        mController = new ControllerDefault(this);
        initRecyclerView();
    }


    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mVideos = ConstantVideo.getVideoList();
        VideoRecyclerViewAdapter adapter = new VideoRecyclerViewAdapter(mVideos);
        adapter.setOnItemChildClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                VideoRecyclerViewAdapter.VideoHolder holder = (VideoRecyclerViewAdapter.VideoHolder) view.getTag();
                int position = holder.mPosition;
                if (position == mCurPos) {
                    startPlay(position, false);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                VideoRecyclerViewAdapter.VideoHolder holder = (VideoRecyclerViewAdapter.VideoHolder) view.getTag();
                int position = holder.mPosition;
                if (position == mCurPos && !mVideoPlayerLayout.isFullScreen()) {
                    mVideoPlayerLayout.startTinyScreen();
                    mVideoPlayerLayout.setController(null);
                    mController.setPlayState(PlayerType.StateType.STATE_IDLE);
                }
            }
        });
    }

    @Override
    public void onItemChildClick(int position) {
        startPlay(position, true);
    }

    /**
     * 开始播放
     *
     * @param position 列表位置
     */
    protected void startPlay(int position, boolean isRelease) {
        if (mVideoPlayerLayout.isTinyScreen())
            mVideoPlayerLayout.stopTinyScreen();
        if (mCurPos != -1 && isRelease) {
            releaseVideoView();
        }
        VideoInfoBean videoBean = mVideos.get(position);
        View itemView = mLinearLayoutManager.findViewByPosition(position);
        if (itemView == null) return;
        //注意：要先设置控制才能去设置控制器的状态。
        mVideoPlayerLayout.setController(mController);
        mController.setPlayState(mVideoPlayerLayout.getCurrentPlayState());

        VideoRecyclerViewAdapter.VideoHolder viewHolder = (VideoRecyclerViewAdapter.VideoHolder) itemView.getTag();
        //把列表中预置的PrepareView添加到控制器中，注意isPrivate此处只能为true。
        mController.add(viewHolder.mPrepareView, true);
        PlayerUtils.removeViewFormParent(mVideoPlayerLayout);
        viewHolder.mPlayerContainer.addView(mVideoPlayerLayout, 0);
        mVideoPlayerLayout.start(videoBean.getVideoUrl());
        mCurPos = position;
    }

    private void releaseVideoView() {
        mVideoPlayerLayout.release();
        if (mVideoPlayerLayout.isFullScreen()) {
            mVideoPlayerLayout.stopFullScreen();
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mCurPos = -1;
    }
}
