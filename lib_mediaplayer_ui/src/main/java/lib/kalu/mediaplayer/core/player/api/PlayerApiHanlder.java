package lib.kalu.mediaplayer.core.player.api;

import android.os.Handler;
import android.os.Looper;

public interface PlayerApiHanlder {

    Handler mHandler = new Handler(Looper.getMainLooper());

    default void postDelayedHanlder(int delayTime, Runnable runnable) {
        removeCallbacksAndMessagesHanlder();
        mHandler.postDelayed(runnable, delayTime);
    }

    default void removeCallbacksAndMessagesHanlder() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
