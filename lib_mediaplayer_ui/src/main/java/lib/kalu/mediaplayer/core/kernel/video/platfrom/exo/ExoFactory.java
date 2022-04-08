package lib.kalu.mediaplayer.core.kernel.video.platfrom.exo;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.KernelFactory;

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
public class ExoFactory extends KernelFactory<ExoMediaPlayer> {

    private ExoFactory() {
    }

    private static class Holder {
        static final ExoMediaPlayer mP = new ExoMediaPlayer();
    }

    public static ExoFactory build() {
        return new ExoFactory();
    }

    @Override
    public ExoMediaPlayer createKernel(@NonNull Context context) {
        return Holder.mP;
    }
}
