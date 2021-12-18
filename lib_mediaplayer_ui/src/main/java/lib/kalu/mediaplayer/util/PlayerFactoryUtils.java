package lib.kalu.mediaplayer.util;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.factory.PlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.impl.VideoPlayerImpl;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.exo.ExoPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.ijk.IjkPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.platfrom.media.MediaPlayerFactory;
import lib.kalu.mediaplayer.config.PlayerType;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class PlayerFactoryUtils {

    /**
     * 获取PlayerFactory具体实现类，获取内核
     * TYPE_IJK                 IjkPlayer，基于IjkPlayer封装播放器
     * TYPE_NATIVE              MediaPlayer，基于原生自带的播放器控件
     * TYPE_EXO                 基于谷歌视频播放器
     * TYPE_RTC                 基于RTC视频播放器
     *
     * @param type 类型
     * @return
     */
    public static PlayerFactory getPlayer(@PlayerType.PlatformType int type) {
        if (type == PlayerType.PlatformType.IJK) {
            return IjkPlayerFactory.create();
        } else if (type == PlayerType.PlatformType.NATIVE) {
            return MediaPlayerFactory.create();
        } else {
            return ExoPlayerFactory.create();
        }
    }

    /**
     * 获取PlayerFactory具体实现类，获取内核
     * 创建对象的时候只需要传递类型type，而不需要对应的工厂，即可创建具体的产品对象
     * TYPE_IJK                 IjkPlayer，基于IjkPlayer封装播放器
     * TYPE_NATIVE              MediaPlayer，基于原生自带的播放器控件
     * TYPE_EXO                 基于谷歌视频播放器
     * TYPE_RTC                 基于RTC视频播放器
     *
     * @param type 类型
     * @return
     */
    public static VideoPlayerImpl getVideoPlayer(@NonNull Context context, @PlayerType.PlatformType.Value int type) {
        if (type == PlayerType.PlatformType.IJK) {
            return IjkPlayerFactory.create().createPlayer(context);
        } else if (type == PlayerType.PlatformType.NATIVE) {
            return MediaPlayerFactory.create().createPlayer(context);
        } else {
            return ExoPlayerFactory.create().createPlayer(context);
        }
    }
}
