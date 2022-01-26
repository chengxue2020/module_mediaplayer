package lib.kalu.mediaplayer.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@Keep
@SuppressLint("AppCompatCustomView")
public class MediaProgressBar3 extends TextView implements Handler.Callback {

    public MediaProgressBar3(@NonNull Context context) {
        super(context);
        init();
    }

    public MediaProgressBar3(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private final void init() {
//        setEnabled(true);
        setHint(String.valueOf(0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if (View.VISIBLE != getVisibility())
//            return;
//
//        boolean enabled = isEnabled();
//        if (!enabled)
//            return;
//
//        setEnabled(false);

        // 循环次数
        int num;
        int length = 10;
        try {
            num = Integer.parseInt(String.valueOf(getHint()));
            if (num + 1 >= length) {
                num = 0;
            }
        } catch (Exception e) {
            num = 0;
        }
        setHint(String.valueOf(num + 1));

        // 画笔
        TextPaint paint = getPaint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(0f);
        paint.setFakeBoldText(true);

        float cx = getWidth() * 0.5f;
        float cy = getHeight() * 0.5f;
        float v = Math.min(cx, cy) / 8;
        float left = cx - v * 0.6f;
        float top = Math.max(cx, cy) - Math.min(cx, cy) + v * 2f;
        float right = cx + v * 0.6f;
        float bottom = cy - v * 3f;
        float rx = v * 0.4f;
        float ry = rx;

        // init
        paint.setColor(Color.parseColor("#00000000"));
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawCircle(cx, cy, Math.min(cx, cy), paint);

        // 椭圆
        for (int i = num; i < num + 10; i++) {
            if (i == num) {
                if (num != 0) {
                    canvas.save();
                    canvas.rotate(36 * (i % 10), cx, cy);
                }
                int color = Color.parseColor("#FF333333");
                paint.setColor(color);
            } else {
                int ff = Integer.parseInt("FF", 16);
                int temp = (ff / 11) * (i - num);
                String hex = Integer.toHexString(ff - temp);
                int color = Color.parseColor("#" + hex + "333333");
                paint.setColor(color);
            }
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rectF, rx, ry, paint);
            canvas.save();
            canvas.rotate(36, cx, cy);
        }

//        Message message = Message.obtain();
//        message.what = 1002;
//        mH.removeCallbacksAndMessages(null);
//        mH.sendMessageDelayed(message, 120);
    }

    private final Handler mH = new Handler(this);

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != View.VISIBLE) {
            mH.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == 1002) {
            setEnabled(true);
            invalidate();
        }
        return false;
    }
}
