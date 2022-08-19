package lib.kalu.mediaplayer.config.player;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tv.danmaku.ijk.media.player.IMediaPlayer;

@Documented
@Retention(CLASS)
@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
@Keep
public @interface PlayerType {
    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface WindowType {
        //普通模式
        int NORMAL = 1001;
        //全屏模式
        int FULL = 1002;
        //小屏模式
        int TINY = 1003;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({NORMAL, FULL, TINY})
        @Keep
        @interface Value {
        }
    }


    /***********************/

    /**
     * 播放状态，主要是指播放器的各种状态
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface StateType {
        int STATE_INIT = 2001; // 播放未开始，即将进行
        int STATE_CLEAN = 2002; //
        int STATE_LOADING_START = 2003; // 开始转圈
        int STATE_LOADING_STOP = 2004; // 停止转圈(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
        int STATE_START = 2005; // 开始播放
        int STATE_END = 2006; // 播放完成
        int STATE_PAUSE = 2007; // 暂停播放
        int STATE_RESUME = 2008; // 暂停播放
        int STATE_REPEAT = 2009; // 重播一次
        int STATE_CLOSE = 2010; // 暂停播放
        int STATE_BUFFERING_START = 2011; // 开始缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
        int STATE_BUFFERING_STOP = 2012; // 停止缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
        int STATE_START_ABORT = 2013; // 开始播放中止
        int STATE_ONCE_LIVE = 2014; // 即将开播

        int STATE_ERROR = 2015; // 错误1
        int STATE_ERROR_NET = 2016; // 错误2

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({
//                STATE_TIMESTAMP_LOOP,
//                STATE_TIMESTAMP_CLEAN,
//                STATE_SUBTITLE_START,
                STATE_INIT,
                STATE_CLEAN,
                STATE_START,
                STATE_PAUSE,
                STATE_RESUME,
                STATE_REPEAT,
                STATE_CLOSE,
                STATE_BUFFERING_START,
                STATE_BUFFERING_STOP,
                STATE_LOADING_STOP,
                STATE_END,
                STATE_START_ABORT,
                STATE_LOADING_START,
                STATE_ONCE_LIVE,
                STATE_ERROR,
                STATE_ERROR_NET})
        @Keep
        @interface Value {
        }
    }

    /**************/

    /**
     * 播放视频缩放类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface ScaleType {
        int SCREEN_SCALE_DEFAULT = 3001; //默认类型
        int SCREEN_SCALE_16_9 = 3002; //16：9比例类型，最为常见
        int SCREEN_SCALE_4_3 = 3003;  //4：3比例类型，也比较常见
        int SCREEN_SCALE_MATCH_PARENT = 3004; //充满整个控件视图
        int SCREEN_SCALE_ORIGINAL = 3005; //原始类型，指视频的原始类型
        int SCREEN_SCALE_CENTER_CROP = 3006; //剧中裁剪类型

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({SCREEN_SCALE_DEFAULT, SCREEN_SCALE_16_9,
                SCREEN_SCALE_4_3, SCREEN_SCALE_MATCH_PARENT,
                SCREEN_SCALE_ORIGINAL, SCREEN_SCALE_CENTER_CROP})
        @Keep
        @interface Value {
        }
    }

    /********************/

    /**
     * 播放状态
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface StatusType {
        //播放完成
        int COMPLETED = 4001;
        //正在播放
        int PLAYING = 4002;
        //暂停状态
        int PAUSE = 4003;
        //用户点击back。当视频退出全屏或者退出小窗口后，再次点击返回键，让用户自己处理返回键事件的逻辑
        int BACK_CLICK = 4004;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({COMPLETED, PLAYING, PAUSE, BACK_CLICK})
        @Keep
        @interface Value {
        }
    }

    /*****************/

    /**
     * 通过注解限定类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface RenderType {
        int SURFACE_VIEW = 1; // SurfaceView
        int TEXTURE_VIEW = 0; // TextureView

        @IntDef({SURFACE_VIEW, TEXTURE_VIEW})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /*****************/

    /**
     * 通过注解限定类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface KernelType {
        int ANDROID = 5001; // MediaPlayer，基于原生自带的播放器控件
        int EXO = 5002; // exo
        int IJK = 5003; // ijk
        int VLC = 5004; // vlc

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({IJK, ANDROID, EXO, VLC})
        @Keep
        @interface Value {
        }
    }

    /***********************/

    /**
     * 控制器上的视频顶部View点击事件
     * 在竖屏模式下，默认是不显示，需要显示设置controller.setTopVisibility(true);
     * 在横屏模式下，默认是不显示，需要显示设置controller.setTvAndAudioVisibility(true,true);
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface ControllerType {
        int DOWNLOAD = 7001; // 下载
        int AUDIO = 7002; // 切换音频
        int SHARE = 7003; // 分享
        int MENU = 7004; // 菜单
        int TV = 7005; // TV，点击投影到电视上
        int HOR_AUDIO = 7006; // 音频

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({DOWNLOAD,
                AUDIO,
                SHARE,
                MENU,
                TV,
                HOR_AUDIO})
        @Keep
        @interface Value {
        }
    }

    /******************/

    /**
     * 电量状态
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface BatterType {
        int BATTERY_CHARGING = 8001;
        int BATTERY_FULL = 8002;
        int BATTERY_10 = 8003;
        int BATTERY_20 = 8004;
        int BATTERY_50 = 8005;
        int BATTERY_80 = 8006;
        int BATTERY_100 = 8007;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({BATTERY_CHARGING, BATTERY_FULL, BATTERY_10, BATTERY_20, BATTERY_50, BATTERY_80, BATTERY_100})
        @Keep
        @interface Value {
        }
    }

    /********************/

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @Keep
    @interface EventType {
        int EVENT_LOADING_START = 9901; // 开始转圈
        int EVENT_LOADING_STOP = 9902; // 停止转圈(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放
        // 播放结束
        //        // 开始渲染视频画面
//        int EVENT_VIDEO_SEEK_RENDERING_START = IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START;
//        // 开始渲染视频画面
//        int EVENT_AUDIO_SEEK_RENDERING_START = IMediaPlayer.MEDIA_INFO_AUDIO_SEEK_RENDERING_START;
//        // 开始渲染视频画面
//        int EVENT_AUDIO_RENDERING_START = IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START;
        // 开始渲染视频画面
        int EVENT_VIDEO_END = 9903;
        int EVENT_VIDEO_START = IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;
        int EVENT_VIDEO_START_SEEK = IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START;
        //        int EVENT_VIDEO_SEEK_COMPLETE_B = IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START;
        //        int EVENT_VIDEO_STOP = IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;
//        int EVENT_VIDEO_END = IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;
        // 缓冲开始
        int EVENT_OPEN_INPUT = IMediaPlayer.MEDIA_INFO_OPEN_INPUT;
        // 缓冲开始
        int EVENT_BUFFERING_START = IMediaPlayer.MEDIA_INFO_BUFFERING_START;
        // 缓冲结束
        int EVENT_BUFFERING_END = IMediaPlayer.MEDIA_INFO_BUFFERING_END;
        // 视频旋转信息
//        int EVENT_VIDEO_ROTATION_CHANGED = IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED;
//        int EVENT_AUDIO_DECODED_START = IMediaPlayer.MEDIA_INFO_AUDIO_DECODED_START;
//        int EVENT_VIDEO_DECODED_START = IMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START;

        int EVENT_ERROR_URL = -9001;
        //错误的链接
        int EVENT_ERROR_RETRY = -9002;
        //错误的链接
        int EVENT_ERROR_SOURCE = -9003;
        //解析异常
        int EVENT_ERROR_PARSE = -9004;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({
                EVENT_ERROR_URL,
                EVENT_ERROR_RETRY,
                EVENT_ERROR_SOURCE,
                EVENT_ERROR_PARSE,
                EVENT_OPEN_INPUT,
                EVENT_LOADING_START,
                EVENT_LOADING_STOP,
//                EVENT_VIDEO_SEEK_RENDERING_START,
//                EVENT_AUDIO_SEEK_RENDERING_START,
//                EVENT_AUDIO_RENDERING_START,
                EVENT_VIDEO_START,
                EVENT_VIDEO_START_SEEK,
                EVENT_VIDEO_END,
//                EVENT_VIDEO_STOP,
//                EVENT_VIDEO_END,
                EVENT_BUFFERING_START,
                EVENT_BUFFERING_END,
//                EVENT_VIDEO_ROTATION_CHANGED,
//                EVENT_AUDIO_DECODED_START,
//                EVENT_VIDEO_DECODED_START
        })
        @Keep
        @interface Value {
        }
    }
}