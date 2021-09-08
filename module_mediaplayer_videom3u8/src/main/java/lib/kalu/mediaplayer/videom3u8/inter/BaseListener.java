package lib.kalu.mediaplayer.videom3u8.inter;


import androidx.annotation.Keep;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/11/9
 *     desc  : 监听基类
 *     revise:
 * </pre>
 */
@Keep
public interface BaseListener {
    /**
     * 开始的时候回调
     */
    void onStart();

    /**
     * 错误的时候回调
     * @param errorMsg              错误异常
     */
    void onError(Throwable errorMsg);
}
