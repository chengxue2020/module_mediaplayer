package com.kalu.mediaplayer.newPlayer.tiktok;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.kalu.mediaplayer.ConstantVideo;
import com.kalu.mediaplayer.R;

import com.yc.pagerlib.pager.VerticalViewPager;
import java.util.ArrayList;
import java.util.List;

import cn.ycbjie.ycstatusbarlib.bar.StateAppBar;
import lib.kalu.mediaplayer.config.PlayerType;
import lib.kalu.mediaplayer.config.VideoInfoBean;
import lib.kalu.mediaplayer.controller.standard.ControllerStandard;
import lib.kalu.mediaplayer.widget.player.VideoLayout;
import lib.kalu.mediaplayer.util.PlayerUtils;


/**
 * 模仿抖音短视频，使用VerticalViewPager实现，实现了预加载功能
 */

public class TikTok2Activity extends AppCompatActivity {

    /**
     * 当前播放位置
     */
    private int mCurPos;
    private List<VideoInfoBean> mVideoList = new ArrayList<>();
    private Tiktok2Adapter mTiktok2Adapter;
    private VerticalViewPager mViewPager;
    private VideoLayout mVideoPlayerLayout;
    private ControllerStandard mController;

    private static final String KEY_INDEX = "index";

    public static void start(Context context, int index) {
        Intent i = new Intent(context, TikTok2Activity.class);
        i.putExtra(KEY_INDEX, index);
        context.startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayerLayout != null) {
            mVideoPlayerLayout.release();
        }
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
    public void onBackPressed() {
        if (mVideoPlayerLayout == null || !mVideoPlayerLayout.onBackPressed()) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiktok2);
        StateAppBar.translucentStatusBar(this, true);
        initFindViewById();
        initListener();
        initView();
    }

    private void initFindViewById() {
        mViewPager = findViewById(R.id.vvp);

    }

    private void initListener() {

    }

    protected void initView() {
        initViewPager();
        initVideoView();
        addData(null);
        Intent extras = getIntent();
        int index = extras.getIntExtra(KEY_INDEX, 0);
        mViewPager.setCurrentItem(index);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                startPlay(index);
            }
        });
    }

    private void initVideoView() {
        mVideoPlayerLayout = new VideoLayout(this);
        mVideoPlayerLayout.setLooping(true);
        //以下只能二选一，看你的需求
        mVideoPlayerLayout.setRenderViewFactory(TikTokRenderViewFactory.create());
        mController = new ControllerStandard(this);
        mVideoPlayerLayout.setController(mController);
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.vvp);
        mViewPager.setOffscreenPageLimit(4);
        mTiktok2Adapter = new Tiktok2Adapter(mVideoList);
        mViewPager.setAdapter(mTiktok2Adapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            private int mCurItem;

            /**
             * VerticalViewPager是否反向滑动
             */
            private boolean mIsReverseScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == mCurItem) {
                    return;
                }
                mIsReverseScroll = position < mCurItem;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == mCurPos) return;
                startPlay(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == VerticalViewPager.SCROLL_STATE_DRAGGING) {
                    mCurItem = mViewPager.getCurrentItem();
                }
            }
        });

//        RecyclerView recyclerView = new RecyclerView(this);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            /**
//             * 是否反向滑动
//             */
//            private boolean mIsReverseScroll;
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy>0){
//                    //表示下滑
//                    mIsReverseScroll = false;
//                } else {
//                    //表示上滑
//                    mIsReverseScroll = true;
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == VerticalViewPager.SCROLL_STATE_IDLE) {
//                    mPreloadManager.resumePreload(mCurPos, mIsReverseScroll);
//                } else {
//                    mPreloadManager.pausePreload(mCurPos, mIsReverseScroll);
//                }
//            }
//        });
    }

    private void startPlay(int position) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = mViewPager.getChildAt(i);
            Tiktok2Adapter.ViewHolder viewHolder = (Tiktok2Adapter.ViewHolder) itemView.getTag();
            if (viewHolder.mPosition == position) {
                mVideoPlayerLayout.release();
                PlayerUtils.removeViewFormParent(mVideoPlayerLayout);

                VideoInfoBean tiktokBean = mVideoList.get(position);
//                String playUrl = mPreloadManager.getPlayUrl(tiktokBean.getVideoUrl());
//                VideoLogUtils.i("startPlay: " + "position: " + position + "  url: " + playUrl);
                mVideoPlayerLayout.setScreenScaleType(PlayerType.ScaleType.SCREEN_SCALE_16_9);
                mController.addComponent(viewHolder.mTikTokView, true);
                viewHolder.mPlayerContainer.addView(mVideoPlayerLayout, 0);
                mVideoPlayerLayout.start(tiktokBean.getVideoUrl());
                mCurPos = position;
                break;
            }
        }
    }

    public void addData(View view) {
        mVideoList.addAll(ConstantVideo.getVideoList());
        mTiktok2Adapter.notifyDataSetChanged();
    }

}
