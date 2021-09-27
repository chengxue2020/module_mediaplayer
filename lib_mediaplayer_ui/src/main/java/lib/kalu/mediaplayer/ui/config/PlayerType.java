package lib.kalu.mediaplayer.ui.config;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
public class PlayerType {

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     * MODE_NORMAL              普通模式
     * MODE_FULL_SCREEN         全屏模式
     * MODE_TINY_WINDOW         小屏模式
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface WindowType {
        //普通模式
        int NORMAL = 0x1001;
        //全屏模式
        int FULL = 0x1002;
        //小屏模式
        int TINY = 0x1003;

        @IntDef({NORMAL, FULL, TINY})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /***********************/

    /**
     * 播放状态，主要是指播放器的各种状态
     * -4               链接为空
     * -3               解析异常
     * -2               播放错误，网络异常
     * -1               播放错误
     * 0                播放未开始，即将进行
     * 1                播放准备中
     * 2                播放准备就绪
     * 3                正在播放
     * 4                暂停播放
     * 5                正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     * 6                暂停缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     * 7                播放完成
     * 8                开始播放中止
     * 9                即将开播
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface StateType {
        int STATE_URL_NULL = 0x2001;
        int STATE_PARSE_ERROR = 0x2002;
        int STATE_NETWORK_ERROR = 0x2003;
        int STATE_ERROR = 0x2004;
        int STATE_IDLE = 0x2005;
        int STATE_PREPARING = 0x2006;
        int STATE_PREPARED = 0x2007;
        int STATE_PLAYING = 0x2008;
        int STATE_PAUSED = 0x2009;
        int STATE_BUFFERING_PLAYING = 0x2010;
        int STATE_BUFFERING_PAUSED = 0x2011;
        int STATE_COMPLETED = 0x2012;
        int STATE_START_ABORT = 0x2013;
        int STATE_ONCE_LIVE = 0x2014;

        @IntDef({STATE_ERROR, STATE_IDLE, STATE_PREPARING,
                STATE_PREPARED, STATE_PLAYING, STATE_PAUSED,
                STATE_BUFFERING_PLAYING, STATE_BUFFERING_PAUSED,
                STATE_COMPLETED, STATE_START_ABORT, STATE_NETWORK_ERROR,
                STATE_PARSE_ERROR, STATE_URL_NULL, STATE_ONCE_LIVE})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /**************/

    /**
     * 播放视频缩放类型
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScaleType {
        //默认类型
        int SCREEN_SCALE_DEFAULT = 0x3001;
        //16：9比例类型，最为常见
        int SCREEN_SCALE_16_9 = 0x3002;
        //4：3比例类型，也比较常见
        int SCREEN_SCALE_4_3 = 0x3003;
        //充满整个控件视图
        int SCREEN_SCALE_MATCH_PARENT = 0x3004;
        //原始类型，指视频的原始类型
        int SCREEN_SCALE_ORIGINAL = 0x3005;
        //剧中裁剪类型
        int SCREEN_SCALE_CENTER_CROP = 0x3006;

        @IntDef({SCREEN_SCALE_DEFAULT, SCREEN_SCALE_16_9,
                SCREEN_SCALE_4_3, SCREEN_SCALE_MATCH_PARENT,
                SCREEN_SCALE_ORIGINAL, SCREEN_SCALE_CENTER_CROP})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /********************/

    /**
     * 播放状态
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusType {
        //播放完成
        int COMPLETED = 0x4001;
        //正在播放
        int PLAYING = 0x4002;
        //暂停状态
        int PAUSE = 0x4003;
        //用户点击back。当视频退出全屏或者退出小窗口后，再次点击返回键，让用户自己处理返回键事件的逻辑
        int BACK_CLICK = 0x4004;

        @IntDef({COMPLETED, PLAYING, PAUSE, BACK_CLICK})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /*****************/

    /**
     * 通过注解限定类型
     * IJK                 IjkPlayer，基于IjkPlayer封装播放器
     * NATIVE              MediaPlayer，基于原生自带的播放器控件
     * EXO                 基于谷歌视频播放器
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlatformType {
        int NATIVE = 0x5001;
        int EXO = 0x5002;
        int IJK = 0x5003;

        @IntDef({IJK, NATIVE, EXO})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /***********************/

    /**
     * 通过注解限定类型
     * 加载loading的类型
     * 1，是仿腾讯加载loading，其实是帧动画
     * 2，是转圈加载loading，是补间动画
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadingType {
        int LOADING_RING = 0x6001;
        int LOADING_QQ = 0x6002;

        @IntDef({LOADING_RING, LOADING_QQ})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /*********************/

    /**
     * 控制器上的视频顶部View点击事件
     * 在竖屏模式下，默认是不显示，需要显示设置controller.setTopVisibility(true);
     * 1.DOWNLOAD，下载
     * 2.AUDIO，切换音频
     * 3.SHARE，分享
     * 4.MENU，菜单
     * <p>
     * 在横屏模式下，默认是不显示，需要显示设置controller.setTvAndAudioVisibility(true,true);
     * 5.TV，点击投影到电视上
     * 6.HOR_AUDIO，音频
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface ControllerType {
        int DOWNLOAD = 0x7001;
        int AUDIO = 0x7002;
        int SHARE = 0x7003;
        int MENU = 0x7004;
        int TV = 0x7005;
        int HOR_AUDIO = 0x7006;

        @IntDef({DOWNLOAD, AUDIO,
                SHARE, MENU, TV,
                HOR_AUDIO})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }

    /******************/

    /**
     * 电量状态
     */
    @Keep
    @Retention(RetentionPolicy.SOURCE)
    public @interface BatterType {
        int BATTERY_CHARGING = 0x8001;
        int BATTERY_FULL = 0x8002;
        int BATTERY_10 = 0x8003;
        int BATTERY_20 = 0x8004;
        int BATTERY_50 = 0x8005;
        int BATTERY_80 = 0x8006;
        int BATTERY_100 = 0x8007;

        @IntDef({BATTERY_CHARGING, BATTERY_FULL, BATTERY_10, BATTERY_20, BATTERY_50, BATTERY_80, BATTERY_100})
        @Retention(RetentionPolicy.SOURCE)
        @Keep
        @interface Value {
        }
    }
}
