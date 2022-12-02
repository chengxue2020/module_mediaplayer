package lib.kalu.mediaplayer.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TimerUtil {

    private Handler mHandler;

    private TimerUtil() {
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (null == mListener)
                    return;
                Set<Map.Entry<Integer, OnMessageChangeListener>> entrySet = mListener.entrySet();
                if (null == entrySet)
                    return;
                for (Map.Entry<Integer, OnMessageChangeListener> entry : entrySet) {
                    Integer k = entry.getKey();
                    OnMessageChangeListener v = entry.getValue();
                    MPLogUtil.log("TimerUtil => handleMessage => key = " + k + ", listener = " + v);
                    if (null == v)
                        continue;
                    v.onMessage(msg);
                }
            }
        };
    }

    private static final class Holder {
        static final TimerUtil mTimer = new TimerUtil();
    }

    public static TimerUtil getInstance() {
        return Holder.mTimer;
    }

    public boolean containsListener(int key) {
        boolean containsKey = false;
        if (null != mListener) {
            containsKey = mListener.containsKey(key);
        }
        return containsKey;
    }

    public void clearListener(int key) {
        boolean containsKey = mListener.containsKey(key);
        MPLogUtil.log("TimerUtil => clearListener => key = " + key + ", containsKey = " + containsKey);
        mListener.remove(key);
    }

    public void clearMessage(int what) {
        MPLogUtil.log("TimerUtil => clearMessage => what = " + what + ", thread = " + Thread.currentThread().getName());
        mHandler.removeMessages(what);
    }

    public void sendMessage(@NonNull Message message) {
        MPLogUtil.log("TimerUtil => sendMessage => what = " + message.what + ", thread = " + Thread.currentThread().getName());
        mHandler.sendMessage(message);
    }

    public void sendMessageDelayed(@NonNull Message message, long delayMillis) {
        MPLogUtil.log("TimerUtil => sendMessageDelayed => what = " + message.what + ", delayMillis = " + delayMillis + ", thread = " + Thread.currentThread().getName());
        mHandler.sendMessageDelayed(message, delayMillis);
    }

    /*******/

    private Map<Integer, OnMessageChangeListener> mListener = null;

    public void registTimer(@NonNull int key, @NonNull OnMessageChangeListener listener) {
        if (null == mListener) {
            mListener = new HashMap<>();
        }
        boolean contains = containsListener(key);
        if (contains)
            return;
        mListener.put(key, listener);
    }

    public interface OnMessageChangeListener {
        void onMessage(@NonNull Message msg);
    }
}
