package lib.kalu.mediaplayer.videoui.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.ColorInt;
import androidx.annotation.Keep;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.common.contentprovider.ContentProviderMediaplayer;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;


/**
 * @description: Toast工具类，模仿易车，抖音自定义吐司
 * *     revise: 具体看GitHub开源项目：https://github.com/yangchong211/YCDialog
 * @date: 2021-05-12 14:49
 */
@Keep
public final class BaseToast {

    @SuppressLint("StaticFieldLeak")
    private static int toastBackColor;
    private static SoftReference<Toast> mToast;

    /**
     * 初始化吐司工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final Context app) {
        toastBackColor = Color.BLACK;
    }

    public static void setToastBackColor(@ColorInt int color) {
        toastBackColor = color;
    }

    /**
     * 私有构造
     */
    private BaseToast() {
        //避免初始化
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 检查上下文不能为空，必须先进性初始化操作
     */
    private static void checkContext() {
        if (null == ContentProviderMediaplayer.getContextWeakReference()) {
            throw new NullPointerException("ToastUtils context is not null，please first init");
        }
    }


    /**
     * 吐司工具类    避免点击多次导致吐司多次，最后导致Toast就长时间关闭不掉了
     * 注意：这里如果传入context会报内存泄漏；传递activity..getApplicationContext()
     *
     * @param content       吐司内容
     */
    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void showToast(String content) {
        checkMainThread();
        checkContext();
        if (!checkNull(mToast)) {
            mToast.get().cancel();
        }
        Toast toast = Toast.makeText(ContentProviderMediaplayer.getContextWeakReference(), "", Toast.LENGTH_SHORT);
        toast.setText(content);
        toast.show();
        mToast = new SoftReference<>(toast);
    }


    /**
     * 某些系统可能屏蔽通知
     * 1:检查 SystemUtils.isEnableNotification(BaseApplication.getApplication());
     * 2:替代方案 SnackBarUtils.showSnack(topActivity, noticeStr);
     * 圆角
     * 屏幕中间
     *
     * @param notice 内容
     */
    public static void showRoundRectToast(CharSequence notice) {
        checkMainThread();
        checkContext();
        if (TextUtils.isEmpty(notice)) {
            return;
        }
        new Builder(ContentProviderMediaplayer.getContextWeakReference())
                .setDuration(Toast.LENGTH_SHORT)
                .setFill(false)
                .setGravity(Gravity.CENTER)
                .setOffset(0)
                .setTitle(notice)
                .setTextColor(Color.WHITE)
                .setBackgroundColor(toastBackColor)
                .setRadius(dip2px(ContentProviderMediaplayer.getContextWeakReference(), 10))
                .setElevation(dip2px(ContentProviderMediaplayer.getContextWeakReference(), 0))
                .build()
                .show();
    }


    public static void showRoundRectToast(CharSequence notice, CharSequence desc) {
        checkMainThread();
        checkContext();
        if (TextUtils.isEmpty(notice)) {
            return;
        }
        new Builder(ContentProviderMediaplayer.getContextWeakReference())
                .setDuration(Toast.LENGTH_SHORT)
                .setFill(false)
                .setGravity(Gravity.CENTER)
                .setOffset(0)
                .setDesc(desc)
                .setTitle(notice)
                .setTextColor(Color.WHITE)
                .setBackgroundColor(toastBackColor)
                .setRadius(dip2px(ContentProviderMediaplayer.getContextWeakReference(), 10))
                .setElevation(dip2px(ContentProviderMediaplayer.getContextWeakReference(), 0))
                .build()
                .show();
    }


    public static void showRoundRectToast(@LayoutRes int layout) {
        checkMainThread();
        checkContext();
        if (layout == 0) {
            return;
        }
        new Builder(ContentProviderMediaplayer.getContextWeakReference())
                .setDuration(Toast.LENGTH_SHORT)
                .setFill(false)
                .setGravity(Gravity.CENTER)
                .setOffset(0)
                .setLayout(layout)
                .build()
                .show();
    }


    public static final class Builder {

        private Context context;
        private CharSequence title;
        private CharSequence desc;
        private int gravity = Gravity.TOP;
        private boolean isFill;
        private int yOffset;
        private int duration = Toast.LENGTH_SHORT;
        private int textColor = Color.WHITE;
        private int backgroundColor = Color.BLACK;
        private float radius;
        private int elevation;
        private int layout;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setDesc(CharSequence desc) {
            this.desc = desc;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setFill(boolean fill) {
            isFill = fill;
            return this;
        }

        public Builder setOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setRadius(float radius) {
            this.radius = radius;
            return this;
        }

        public Builder setElevation(int elevation) {
            this.elevation = elevation;
            return this;
        }

        public Builder setLayout(@LayoutRes int layout) {
            this.layout = layout;
            return this;
        }

        public Toast build() {
            if (!checkNull(mToast)) {
                mToast.get().cancel();
            }
            Toast toast = new Toast(context);
            HookToast.hook(toast);
            if (isFill) {
                toast.setGravity(gravity | Gravity.FILL_HORIZONTAL, 0, yOffset);
            } else {
                toast.setGravity(gravity, 0, yOffset);
            }
            toast.setDuration(duration);
            toast.setMargin(0, 0);
            if (layout == 0) {
                RelativeLayout rootView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.moudle_mediaplayer_toast_view, null);
                TextView textView = rootView.findViewById(R.id.toastTextView);
                TextView descTv = rootView.findViewById(R.id.desc);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //rootView.setElevation(elevation);
//                    rootView.setCardElevation(elevation);
                }
//                rootView.setRadius(radius);
                rootView.setBackgroundColor(backgroundColor);
                //rootView.setBackgroundColor(backgroundColor);
                textView.setTextColor(textColor);
                textView.setText(title);
                if (TextUtils.isEmpty(desc)) {
                    descTv.setVisibility(View.GONE);
                } else {
                    descTv.setText(desc);
                    descTv.setVisibility(View.VISIBLE);
                }
                toast.setView(rootView);
            } else {
                View view = LayoutInflater.from(context).inflate(layout, null);
                toast.setView(view);
            }
            mToast = new SoftReference<>(toast);
            return toast;
        }
    }

    public static boolean checkNull(SoftReference softReference) {
        if (softReference == null || softReference.get() == null) {
            return true;
        }
        return false;
    }


    public static void checkMainThread() {
        if (!isMainThread()) {
            throw new IllegalStateException("请不要在子线程中做弹窗操作");
        }
    }

    private static boolean isMainThread() {
        //判断是否是主线程
        return Looper.getMainLooper() == Looper.myLooper();
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * <pre>
     *     @author yangchong
     *     email  : yangchong211@163.com
     *     time  : 20120/5/6
     *     desc  : 利用hook解决toast崩溃问题
     *     revise:
     * </pre>
     */
    public static class HookToast {

        private static Field sField_TN;
        private static Field sField_TN_Handler;

        static {
            try {
                Class<?> clazz = Toast.class;
                sField_TN = clazz.getDeclaredField("mTN");
                sField_TN.setAccessible(true);
                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void hook(Toast toast) {
            try {
                Object tn = sField_TN.get(toast);
                Handler preHandler = (Handler) sField_TN_Handler.get(tn);
                sField_TN_Handler.set(tn, new HookToast.SafelyHandler(preHandler));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static class SafelyHandler extends Handler {

            private Handler impl;

            public SafelyHandler(Handler impl) {
                this.impl = impl;
            }

            public void dispatchMessage(Message msg) {
                try {
                    super.dispatchMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void handleMessage(Message msg) {
                //需要委托给原Handler执行
                impl.handleMessage(msg);
            }
        }

    }
}
