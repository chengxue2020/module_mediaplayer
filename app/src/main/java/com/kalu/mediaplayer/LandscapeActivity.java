package com.kalu.mediaplayer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import lib.kalu.mediaplayer.videokernel.factory.PlayerFactory;
import lib.kalu.mediaplayer.videokernel.utils.PlayerConstant;
import lib.kalu.mediaplayer.videokernel.utils.PlayerFactoryUtils;
import lib.kalu.mediaplayer.videoui.config.ConstantKeys;
import lib.kalu.mediaplayer.videoui.config.VideoPlayerConfig;
import lib.kalu.mediaplayer.videoui.player.OnVideoStateListener;
import lib.kalu.mediaplayer.videoui.player.VideoBuilder;
import lib.kalu.mediaplayer.videoui.player.VideoLayout;
import lib.kalu.mediaplayer.videoui.player.VideoViewManager;
import lib.kalu.mediaplayer.videoui.ui.view.BasisVideoController;
import lib.kalu.mediaplayer.videoui.ui.view.CustomErrorView;

/**
 * @description: 横屏播放
 * 视频测试地址： https://yunqivedio.alicdn.com/2017yq/v2/0x0/96d79d3f5400514a6883869399708e11/96d79d3f5400514a6883869399708e11.m3u8
 * @date: 2021-05-19 15:24
 */
public class LandscapeActivity extends AppCompatActivity {

    public static final String INTENT_URL = "intent_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscape);

        String url = getIntent().getStringExtra(INTENT_URL);
        if (null == url || url.length() == 0 || !url.startsWith("http")) {
            onBackPressed();
            return;
        }

        // 基础视频播放器
        BasisVideoController basisVideoController = new BasisVideoController(this);
        basisVideoController.setEnableOrientation(false);
        // 设置视频背景图
        ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
        basisVideoController.getThumb().setImageDrawable(colorDrawable);
        // 控制器
        VideoLayout videoLayout = findViewById(R.id.video_player);
        videoLayout.setController(basisVideoController);
        // 设置视频播放链接地址
        videoLayout.setUrl(url);
        videoLayout.showNetWarning();
        // 全屏
        videoLayout.startFullScreen();
        // 开始播放
        videoLayout.start();

        // 监听
        videoLayout.setOnStateChangeListener(new OnVideoStateListener() {
            /**
             * 播放模式
             * 普通模式，小窗口模式，正常模式三种其中一种
             * MODE_NORMAL              普通模式
             * MODE_FULL_SCREEN         全屏模式
             * MODE_TINY_WINDOW         小屏模式
             * @param playerState                       播放模式
             */
            @Override
            public void onPlayerStateChanged(int playerState) {
                switch (playerState) {
                    case ConstantKeys.PlayMode.MODE_NORMAL:
                        onBackPressed();
                        //普通模式
                        break;
                    case ConstantKeys.PlayMode.MODE_FULL_SCREEN:
                        //全屏模式
                        break;
                    case ConstantKeys.PlayMode.MODE_TINY_WINDOW:
                        //小屏模式
                        break;
                }
            }

            /**
             * 播放状态
             * -1               播放错误
             * 0                播放未开始
             * 1                播放准备中
             * 2                播放准备就绪
             * 3                正在播放
             * 4                暂停播放
             * 5                正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
             * 6                暂停缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
             * 7                播放完成
             * 8                开始播放中止
             * @param playState                         播放状态，主要是指播放器的各种状态
             */
            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case ConstantKeys.CurrentState.STATE_IDLE:
                        //播放未开始，初始化
                        break;
                    case ConstantKeys.CurrentState.STATE_START_ABORT:
                        //开始播放中止
                        break;
                    case ConstantKeys.CurrentState.STATE_PREPARING:
                        //播放准备中
                        break;
                    case ConstantKeys.CurrentState.STATE_PREPARED:
                        //播放准备就绪
                        break;
                    case ConstantKeys.CurrentState.STATE_ERROR:
                        //播放错误
                        break;
                    case ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING:
                        //正在缓冲
                        break;
                    case ConstantKeys.CurrentState.STATE_PLAYING:
                        //正在播放
                        break;
                    case ConstantKeys.CurrentState.STATE_PAUSED:
                        //暂停播放
                        break;
                    case ConstantKeys.CurrentState.STATE_BUFFERING_PAUSED:
                        //暂停缓冲
                        break;
                    case ConstantKeys.CurrentState.STATE_COMPLETED:
                        //播放完成
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        VideoLayout videoLayout = findViewById(R.id.video_player);
        if (videoLayout == null || !videoLayout.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoLayout videoLayout = findViewById(R.id.video_player);
        videoLayout.resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        VideoLayout videoLayout = findViewById(R.id.video_player);
        videoLayout.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoLayout videoLayout = findViewById(R.id.video_player);
        videoLayout.release();
    }

    private void test() {
        //VideoPlayer相关
        VideoBuilder.Builder builder = VideoBuilder.newBuilder();
        VideoBuilder videoPlayerBuilder = new VideoBuilder(builder);
        //设置视频播放器的背景色
        builder.setPlayerBackgroundColor(Color.BLACK);
        //设置小屏的宽高
        int[] mTinyScreenSize = {0, 0};
        builder.setTinyScreenSize(mTinyScreenSize);
        //是否开启AudioFocus监听， 默认开启
        builder.setEnableAudioFocus(false);

        VideoLayout mVideoPlayer = findViewById(R.id.video_player);
        mVideoPlayer.setVideoBuilder(videoPlayerBuilder);
        //截图
        Bitmap bitmap = mVideoPlayer.doScreenShot();
        //移除所有播放状态监听
        mVideoPlayer.clearOnStateChangeListeners();
        //获取当前缓冲百分比
        int bufferedPercentage = mVideoPlayer.getBufferedPercentage();
        //获取当前播放器的状态
        int currentPlayerState = mVideoPlayer.getCurrentPlayerState();
        //获取当前的播放状态
        int currentPlayState = mVideoPlayer.getCurrentPlayState();
        //获取当前播放的位置
        long currentPosition = mVideoPlayer.getCurrentPosition();
        //获取视频总时长
        long duration = mVideoPlayer.getDuration();
        //获取倍速速度
        float speed = mVideoPlayer.getSpeed();
        //获取缓冲速度
        long tcpSpeed = mVideoPlayer.getTcpSpeed();
        //获取视频宽高
        int[] videoSize = mVideoPlayer.getVideoSize();
        //是否处于静音状态
        boolean mute = mVideoPlayer.isMute();
        //判断是否处于全屏状态
        boolean fullScreen = mVideoPlayer.isFullScreen();
        //是否是小窗口模式
        boolean tinyScreen = mVideoPlayer.isTinyScreen();

        //是否处于播放状态
        boolean playing = mVideoPlayer.isPlaying();
        //暂停播放
        mVideoPlayer.pause();
        //视频缓冲完毕，准备开始播放时回调
        mVideoPlayer.onPrepared();
        //重新播放
        mVideoPlayer.replay(true);
        //继续播放
        mVideoPlayer.resume();
        //调整播放进度
        mVideoPlayer.seekTo(100);
        //循环播放， 默认不循环播放
        mVideoPlayer.setLooping(true);
        //设置播放速度
        mVideoPlayer.setSpeed(1.1f);
        //设置音量 0.0f-1.0f 之间
        mVideoPlayer.setVolume(1, 1);
        //开始播放
        mVideoPlayer.start();


        //进入全屏
        mVideoPlayer.startFullScreen();
        //退出全屏
        mVideoPlayer.stopFullScreen();
        //开启小屏
        mVideoPlayer.startTinyScreen();
        //退出小屏
        mVideoPlayer.stopTinyScreen();

        mVideoPlayer.setOnStateChangeListener(new OnVideoStateListener() {
            /**
             * 播放模式
             * 普通模式，小窗口模式，正常模式三种其中一种
             * MODE_NORMAL              普通模式
             * MODE_FULL_SCREEN         全屏模式
             * MODE_TINY_WINDOW         小屏模式
             * @param playerState                       播放模式
             */
            @Override
            public void onPlayerStateChanged(int playerState) {
                switch (playerState) {
                    case ConstantKeys.PlayMode.MODE_NORMAL:
                        //普通模式
                        break;
                    case ConstantKeys.PlayMode.MODE_FULL_SCREEN:
                        //全屏模式
                        break;
                    case ConstantKeys.PlayMode.MODE_TINY_WINDOW:
                        //小屏模式
                        break;
                }
            }

            /**
             * 播放状态
             * -1               播放错误
             * 0                播放未开始
             * 1                播放准备中
             * 2                播放准备就绪
             * 3                正在播放
             * 4                暂停播放
             * 5                正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
             * 6                暂停缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
             * 7                播放完成
             * 8                开始播放中止
             * @param playState                         播放状态，主要是指播放器的各种状态
             */
            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case ConstantKeys.CurrentState.STATE_IDLE:
                        //播放未开始，初始化
                        break;
                    case ConstantKeys.CurrentState.STATE_START_ABORT:
                        //开始播放中止
                        break;
                    case ConstantKeys.CurrentState.STATE_PREPARING:
                        //播放准备中
                        break;
                    case ConstantKeys.CurrentState.STATE_PREPARED:
                        //播放准备就绪
                        break;
                    case ConstantKeys.CurrentState.STATE_ERROR:
                        //播放错误
                        break;
                    case ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING:
                        //正在缓冲
                        break;
                    case ConstantKeys.CurrentState.STATE_PLAYING:
                        //正在播放
                        break;
                    case ConstantKeys.CurrentState.STATE_PAUSED:
                        //暂停播放
                        break;
                    case ConstantKeys.CurrentState.STATE_BUFFERING_PAUSED:
                        //暂停缓冲
                        break;
                    case ConstantKeys.CurrentState.STATE_COMPLETED:
                        //播放完成
                        break;
                }
            }
        });

        //设置视频背景图
        BasisVideoController controller = (BasisVideoController) mVideoPlayer.getVideoController();
        ImageView thumb = controller.getThumb();
        Glide.with(this).load(R.drawable.image_default).into(controller.getThumb());
        //设置视频标题
        controller.setTitle("视频标题");
        //添加自定义视图。每添加一个视图，都是方式层级树的最上层
        CustomErrorView customErrorView = new CustomErrorView(this);
        controller.addControlComponent(customErrorView);
        //移除控制组件
        controller.removeControlComponent(customErrorView);
        //移除所有的组件
        controller.removeAllControlComponent();
        //隐藏播放视图
        controller.hide();
        //显示播放视图
        controller.show();
        //是否开启根据屏幕方向进入/退出全屏
        controller.setEnableOrientation(true);
        //显示移动网络播放提示
        controller.showNetWarning();
        //刘海的高度
        int cutoutHeight = controller.getCutoutHeight();
        //是否有刘海屏
        boolean b = controller.hasCutout();
        //设置是否适配刘海屏
        controller.setAdaptCutout(true);
        //停止刷新进度
        controller.stopProgress();
        //开始刷新进度，注意：需在STATE_PLAYING时调用才会开始刷新进度
        controller.startProgress();
        //判断是否锁屏
        boolean locked = controller.isLocked();
        //设置是否锁屏
        controller.setLocked(true);
        //取消计时
        controller.stopFadeOut();
        //开始计时
        controller.startFadeOut();
        //设置播放视图自动隐藏超时
        controller.setDismissTimeout(8);
        //销毁
        controller.destroy();


        //播放器配置，注意：此为全局配置，按需开启
        PlayerFactory player = PlayerFactoryUtils.getPlayer(PlayerConstant.PlayerType.TYPE_IJK);
        VideoViewManager.setConfig(VideoPlayerConfig.newBuilder()
                //设置上下文
                .setContext(this)
                //设置视频全局埋点事件
                .setBuriedPointEvent(new BuriedPointEventImpl())
                //调试的时候请打开日志，方便排错
                .setLogEnabled(true)
                //设置ijk
                .setPlayerFactory(player)
                //在移动环境下调用start()后是否继续播放，默认不继续播放
                .setPlayOnMobileNetwork(false)
                //是否开启AudioFocus监听， 默认开启
                .setEnableAudioFocus(true)
                //是否适配刘海屏，默认适配
                .setAdaptCutout(true)
                //监听设备方向来切换全屏/半屏， 默认不开启
                .setEnableOrientation(false)
                //设置自定义渲染view，自定义RenderView
                //.setRenderViewFactory(null)
                //创建
                .build());
    }
}
