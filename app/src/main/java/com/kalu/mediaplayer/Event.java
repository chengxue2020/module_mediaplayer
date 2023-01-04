package com.kalu.mediaplayer;

import android.util.Log;

import lib.kalu.mediaplayer.config.buried.BuriedEvent;

public class Event implements BuriedEvent {

    /**
     * 进入视频播放
     *
     * @param url 视频url
     */
    @Override
    public void playerIn(String url) {
        Log.e("BuriedEvent", "playerIn => url = "+url);
    }


    /**
     * 退出视频播放
     *
     * @param url 视频url
     */
    @Override
    public void playerDestroy(String url) {
        Log.e("BuriedEvent", "playerDestroy => url = "+url);
    }

    /**
     * 视频播放完成
     *
     * @param url 视频url
     */
    @Override
    public void playerCompletion(String url) {
        Log.e("BuriedEvent", "playerCompletion => url = "+url);
    }

    /**
     * 视频播放异常
     *
     * @param url        视频url
     * @param isNetError 是否是网络异常
     */
    @Override
    public void playerError(String url, boolean isNetError) {
        Log.e("BuriedEvent", "playerError => url = "+url);
    }

    /**
     * 点击了视频广告
     *
     * @param url 视频url
     */
    @Override
    public void clickAd(String url) {
        Log.e("BuriedEvent", "clickAd => url = "+url);
    }

    /**
     * 视频试看点击
     *
     * @param url 视频url
     */
    @Override
    public void playerAndProved(String url) {
        Log.e("BuriedEvent", "playerAndProved => url = "+url);
    }

    /**
     * 退出视频播放时候的播放进度百度比
     *
     * @param url      视频url
     * @param progress 视频进度，计算百分比【退出时候进度 / 总进度】
     */
    @Override
    public void playerOutProgress(String url, float progress) {
        Log.e("BuriedEvent", "playerOutProgress => url = "+url+", progress = "+progress);
    }

    /**
     * 退出视频播放时候的播放进度
     *
     * @param url             视频url
     * @param duration        总时长
     * @param currentPosition 当前进度时长
     */
    @Override
    public void playerOutProgress(String url, long duration, long currentPosition) {
        Log.e("BuriedEvent", "playerOutProgress => url = "+url+", duration = "+duration+", currentPosition = "+currentPosition);
    }

    /**
     * 视频切换音频
     *
     * @param url 视频url
     */
    @Override
    public void videoToMusic(String url) {
        Log.e("BuriedEvent", "videoToMusic => url = "+url);
    }
}
