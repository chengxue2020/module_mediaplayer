package lib.kalu.mediaplayer.message;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.Random;

import lib.kalu.mediaplayer.util.MPLogUtil;

public final class PlayerMessage {

    public static final class Holder {

        private static final HandlerThread thread = new HandlerThread("playerThread" + new Random().nextInt(1000));

        static {
            thread.start();
        }

        private static final Handler mHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                MPLogUtil.log("PlayerMessage => handleMessage => what = " + msg.what + ", thread = " + Thread.currentThread().getName());
                if (null != onMessageChangeListener) {
                    onMessageChangeListener.onMessage(msg);
                }
            }
        };
    }

    public static void clearMessage() {
        MPLogUtil.log("PlayerMessage => clearMessage => thread = " + Thread.currentThread().getName());
        Holder.mHandler.removeCallbacksAndMessages(null);
    }

    public static void clearMessage(int what) {
        MPLogUtil.log("PlayerMessage => clearMessage => what = " + what + ", thread = " + Thread.currentThread().getName());
        Holder.mHandler.removeMessages(what);
    }

    public static void sendMessage(@NonNull Message message) {
        MPLogUtil.log("PlayerMessage => sendMessage => what = " + message.what + ", thread = " + Thread.currentThread().getName());
        Holder.mHandler.sendMessage(message);
    }

    public static void sendMessageDelayed(@NonNull Message message, long delayMillis) {
        MPLogUtil.log("PlayerMessage => sendMessageDelayed => what = " + message.what + ", delayMillis = " + delayMillis + ", thread = " + Thread.currentThread().getName());
        Holder.mHandler.sendMessageDelayed(message, delayMillis);
    }

    /*******/

    private static OnMessageChangeListener onMessageChangeListener;

    public static void regist(@NonNull OnMessageChangeListener listener) {
        onMessageChangeListener = listener;
    }

    public interface OnMessageChangeListener {
        void onMessage(@NonNull Message msg);
    }
}
