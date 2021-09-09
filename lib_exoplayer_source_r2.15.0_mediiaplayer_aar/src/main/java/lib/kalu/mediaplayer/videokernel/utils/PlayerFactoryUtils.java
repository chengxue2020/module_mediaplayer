package lib.kalu.mediaplayer.videokernel.utils;

import android.content.Context;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.videokernel.factory.PlayerFactory;
import lib.kalu.mediaplayer.videokernel.platfrom.exo.ExoPlayerFactory;
import lib.kalu.mediaplayer.videokernel.platfrom.ijk.IjkPlayerFactory;
import lib.kalu.mediaplayer.videokernel.platfrom.media.MediaPlayerFactory;
import lib.kalu.mediaplayer.videokernel.impl.VideoPlayerImpl;

/**
 * @description: 工具类
 * @date:  2021-05-12 14:41
 */
@Keep
public final class PlayerFactoryUtils {

    /**
     * 获取PlayerFactory具体实现类，获取内核
     * TYPE_IJK                 IjkPlayer，基于IjkPlayer封装播放器
     * TYPE_NATIVE              MediaPlayer，基于原生自带的播放器控件
     * TYPE_EXO                 基于谷歌视频播放器
     * TYPE_RTC                 基于RTC视频播放器
     * @param type                              类型
     * @return
     */
    public static PlayerFactory getPlayer(@PlayerConstant.PlayerType int type){
        if (type == PlayerConstant.PlayerType.TYPE_EXO){
            return ExoPlayerFactory.create();
        } else if (type == PlayerConstant.PlayerType.TYPE_IJK){
            return IjkPlayerFactory.create();
        } else if (type == PlayerConstant.PlayerType.TYPE_NATIVE){
            return MediaPlayerFactory.create();
        } else if (type == PlayerConstant.PlayerType.TYPE_RTC){
            return IjkPlayerFactory.create();
        } else {
            return IjkPlayerFactory.create();
        }
    }

    /**
     * 获取PlayerFactory具体实现类，获取内核
     * 创建对象的时候只需要传递类型type，而不需要对应的工厂，即可创建具体的产品对象
     * TYPE_IJK                 IjkPlayer，基于IjkPlayer封装播放器
     * TYPE_NATIVE              MediaPlayer，基于原生自带的播放器控件
     * TYPE_EXO                 基于谷歌视频播放器
     * TYPE_RTC                 基于RTC视频播放器
     * @param type                              类型
     * @return
     */
    public static VideoPlayerImpl getVideoPlayer(@PlayerConstant.PlayerType int type){
        if (type == PlayerConstant.PlayerType.TYPE_EXO){
            return ExoPlayerFactory.create().createPlayer();
        } else if (type == PlayerConstant.PlayerType.TYPE_IJK){
            return IjkPlayerFactory.create().createPlayer();
        } else if (type == PlayerConstant.PlayerType.TYPE_NATIVE){
            return MediaPlayerFactory.create().createPlayer();
        } else if (type == PlayerConstant.PlayerType.TYPE_RTC){
            return IjkPlayerFactory.create().createPlayer();
        } else {
            return IjkPlayerFactory.create().createPlayer();
        }
    }


}
