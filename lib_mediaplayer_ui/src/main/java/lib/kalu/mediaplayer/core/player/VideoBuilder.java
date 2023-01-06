package lib.kalu.mediaplayer.core.player;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;

/**
 * @description: 播放器设置属性builder类
 */
@Keep
public class VideoBuilder {

    public static Builder newBuilder() {
        return new Builder();
    }

    @Keep
    public final static class Builder {

        private int mColor = 0;
        private int[] mTinyScreenSize;
        private int mCurrentPosition = -1;

        /**
         * 设置视频播放器的背景色
         *
         * @param color color
         * @return Builder
         */
        public Builder setPlayerBackgroundColor(@ColorInt int color) {
            //使用注解限定福
            if (color == 0) {
                this.mColor = Color.BLACK;
            } else {
                this.mColor = color;
            }
            return this;
        }

        /**
         * 设置小屏的宽高
         *
         * @param tinyScreenSize 其中tinyScreenSize[0]是宽，tinyScreenSize[1]是高
         * @return Builder
         */
        public Builder setTinyScreenSize(int[] tinyScreenSize) {
            this.mTinyScreenSize = tinyScreenSize;
            return this;
        }

        /**
         * 一开始播放就seek到预先设置好的位置
         *
         * @param position 位置
         * @return Builder
         */
        public Builder skipPositionWhenPlay(int position) {
            this.mCurrentPosition = position;
            return this;
        }

        public VideoBuilder build() {
            //创建builder对象
            return new VideoBuilder(this);
        }
    }


    public final int mColor;
    public final int[] mTinyScreenSize;
    public final int mCurrentPosition;

    public VideoBuilder(Builder builder) {
        mColor = builder.mColor;
        mTinyScreenSize = builder.mTinyScreenSize;
        mCurrentPosition = builder.mCurrentPosition;
    }
}
