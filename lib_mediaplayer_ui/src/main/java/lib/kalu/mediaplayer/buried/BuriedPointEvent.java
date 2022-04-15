package lib.kalu.mediaplayer.buried;

import androidx.annotation.Keep;

@Keep
public interface BuriedPointEvent {

    /**
     * 进入视频播放
     *
     * @param url 视频url
     */
    void playerIn(CharSequence url);

    /**
     * 退出视频播放
     *
     * @param url 视频url
     */
    void playerDestroy(CharSequence url);

    /**
     * 视频播放完成
     *
     * @param url 视频url
     */
    void playerCompletion(CharSequence url);

    /**
     * 视频播放异常
     *
     * @param url        视频url
     * @param isNetError 是否是网络异常
     */
    void onError(CharSequence url, boolean isNetError);

    /**
     * 点击了视频广告
     *
     * @param url 视频url
     */
    void clickAd(CharSequence url);

    /**
     * 视频试看点击
     *
     * @param url 视频url
     */
    void playerAndProved(CharSequence url);


    /**
     * 退出视频播放时候的播放进度百度分
     *
     * @param url      视频url
     * @param progress 视频进度，计算百分比【退出时候进度 / 总进度】
     */
    void playerOutProgress(CharSequence url, float progress);

    /**
     * 退出视频播放时候的播放进度
     *
     * @param url             视频url
     * @param duration        总时长
     * @param currentPosition 当前进度时长
     */
    void playerOutProgress(CharSequence url, long duration, long currentPosition);

    /**
     * 视频切换音频
     *
     * @param url 视频url
     */
    void videoToMusic(CharSequence url);
}