package lib.kalu.mediaplayer.kernel.video.platfrom.exo;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.kernel.video.factory.PlayerFactory;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/11/9
 *     desc  : exo视频播放器Factory
 *     revise: 抽象工厂具体实现类
 * </pre>
 */
@Keep
public class ExoPlayerFactory extends PlayerFactory<ExoMediaPlayer> {

    public static ExoPlayerFactory create() {
        return new ExoPlayerFactory();
    }

    @Override
    public ExoMediaPlayer createPlayer() {
        return new ExoMediaPlayer();
    }
}
