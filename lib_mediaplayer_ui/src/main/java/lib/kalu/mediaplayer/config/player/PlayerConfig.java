package lib.kalu.mediaplayer.config.player;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.buried.BuriedPointEvent;
import lib.kalu.mediaplayer.keycode.KeycodeApi;
import lib.kalu.mediaplayer.keycode.KeycodeTV;

/**
 * @description: 播放器全局配置
 * @date: 2021-05-12 14:43
 */
@Keep
public class PlayerConfig {

    public final boolean mPlayOnMobileNetwork;
    public final boolean mEnableOrientation;
    public final boolean mIsEnableLog;
    @PlayerType.KernelType.Value
    public final int mKernel;
    @PlayerType.RenderType.Value
    public final int mRender;
    public final BuriedPointEvent mBuriedPointEvent;
    public final int mScreenScaleType;
    public final boolean mAdaptCutout;
    public final boolean mIsShowToast;
    public final long mShowToastTime;
    public final KeycodeApi mKeycode;

//    @PlayerType.PlatformType
//    public final int getKernel() {
//        return mType;
//    }
//
//    public final void setKernel(@PlayerType.PlatformType int type) {
//        this.mType = type;
//    }

    private PlayerConfig(Builder builder) {
        mIsEnableLog = builder.mIsEnableLog;
        mEnableOrientation = builder.mEnableOrientation;
        mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        mScreenScaleType = builder.mScreenScaleType;
        if (null == builder.mKeycode) {
            mKeycode = new KeycodeTV();
        } else {
            mKeycode = builder.mKeycode;
        }
        mKernel = builder.mKernel;
        mRender = builder.mRender;
        mBuriedPointEvent = builder.mBuriedPointEvent;
        mAdaptCutout = builder.mAdaptCutout;
        mIsShowToast = builder.mIsShowToast;
        mShowToastTime = builder.mShowToastTime;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Keep
    public final static class Builder {

        /**
         * 默认是关闭日志的
         */
        private boolean mIsEnableLog = false;
        /**
         * 在移动环境下调用start()后是否继续播放，默认不继续播放
         */
        private boolean mPlayOnMobileNetwork;
        /**
         * 是否监听设备方向来切换全屏/半屏， 默认不开启
         */
        private boolean mEnableOrientation;
        /**
         * 自定义播放核心
         */
        @PlayerType.KernelType.Value
        private int mKernel = PlayerType.KernelType.ANDROID;
        /**
         * 自定义RenderView
         */
        @PlayerType.RenderType.Value
        public int mRender = PlayerType.RenderType.TEXTURE;
        /**
         * 自定义视频全局埋点事件
         */
        private BuriedPointEvent mBuriedPointEvent;
        /**
         * 设置视频比例
         */
        private int mScreenScaleType;
        /**
         * 是否适配刘海屏，默认适配
         */
        private boolean mAdaptCutout = true;
        /**
         * 是否设置倒计时n秒吐司
         */
        private boolean mIsShowToast = false;
        /**
         * 倒计时n秒时间
         */
        private long mShowToastTime = 5;

        private KeycodeApi mKeycode;

        public KeycodeApi getKeycode() {
            return mKeycode;
        }

        public Builder setKeycode(KeycodeApi mKeycode) {
            this.mKeycode = mKeycode;
            return this;
        }

        /**
         * 是否监听设备方向来切换全屏/半屏， 默认不开启
         */
        public Builder setEnableOrientation(boolean enableOrientation) {
            mEnableOrientation = enableOrientation;
            return this;
        }

        /**
         * 在移动环境下调用start()后是否继续播放，默认不继续播放
         */
        public Builder setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
            mPlayOnMobileNetwork = playOnMobileNetwork;
            return this;
        }

        /**
         * 是否打印日志
         */
        public Builder setLogEnabled(boolean enableLog) {
            mIsEnableLog = enableLog;
            return this;
        }

        /**
         * 自定义播放核心
         */
        public Builder setKernel(@PlayerType.KernelType.Value int type) {
            mKernel = type;
            return this;
        }

        /**
         * 自定义视频全局埋点事件
         */
        public Builder setBuriedPointEvent(BuriedPointEvent buriedPointEvent) {
            mBuriedPointEvent = buriedPointEvent;
            return this;
        }

        /**
         * 设置视频比例
         */
        public Builder setScreenScaleType(int screenScaleType) {
            mScreenScaleType = screenScaleType;
            return this;
        }

        /**
         * 自定义RenderView
         */
        public Builder setRender(@PlayerType.RenderType.Value int render) {
            mRender = render;
            return this;
        }

        /**
         * 是否适配刘海屏，默认适配
         */
        public Builder setAdaptCutout(boolean adaptCutout) {
            mAdaptCutout = adaptCutout;
            return this;
        }

        /**
         * 是否设置倒计时n秒吐司
         */
        public Builder setIsShowToast(boolean isShowToast) {
            mIsShowToast = isShowToast;
            return this;
        }

        /**
         * 倒计时n秒时间
         */
        public Builder setShowToastTime(long showToastTime) {
            mShowToastTime = showToastTime;
            return this;
        }

        public PlayerConfig build() {
            //创建builder对象
            return new PlayerConfig(this);
        }
    }
}
