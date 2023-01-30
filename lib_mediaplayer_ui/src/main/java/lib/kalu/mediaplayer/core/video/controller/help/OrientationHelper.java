package lib.kalu.mediaplayer.core.video.controller.help;

import android.content.Context;
import android.view.OrientationEventListener;

public class OrientationHelper extends OrientationEventListener {

    private long mLastTime;

    private OnOrientationChangeListener mOnOrientationChangeListener;

    public OrientationHelper(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTime < 500) {
            return;
        }
        //500毫秒检测一次
        if (mOnOrientationChangeListener != null) {
            mOnOrientationChangeListener.onOrientationChanged(orientation);
        }
        mLastTime = currentTime;
    }


    public interface OnOrientationChangeListener {
        void onOrientationChanged(int orientation);
    }

    public void setOnOrientationChangeListener(OnOrientationChangeListener onOrientationChangeListener) {
        mOnOrientationChangeListener = onOrientationChangeListener;
    }
}
