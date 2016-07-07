package com.gloomyer.rippling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 涟漪动画View
 *
 * @author Gloomy
 * @date 2016年07月07日11:39:17
 */
public class RipplingView extends View {

    private int height;
    private int width;
    private int Bigstep;
    private int smallstep;
    private boolean isDraw;
    private Paint bigPaint;
    private Paint smallPaint;
    private int smallCircleSize;

    public RipplingView(Context context) {
        this(context, null);
    }

    public RipplingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RipplingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        //大圆的增长步长/16ms
        Bigstep = dip2px(context, 6);
        smallstep = Bigstep / 6;
        if (smallstep <= 0)
            smallstep = 1;

        //大圆的画笔
        bigPaint = new Paint();
        bigPaint.setAlpha(0);
        bigPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bigPaint.setAntiAlias(true);
        bigPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        //小圆的画笔
        smallPaint = new Paint();
        smallPaint.setColor(Color.rgb(0xff, 0xff, 0xff));
        smallPaint.setAntiAlias(true);

        setVisibility(View.VISIBLE);
    }

    private int bigCircleSize;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempcCanvas = new Canvas(bitmap);

        if (!isDraw) {
            bigCircleSize = (int) (canvas.getWidth() * 1.0f / 7);
            smallCircleSize = bigCircleSize;
            isDraw = true;
        }

        tempcCanvas.drawColor(Color.WHITE);
        tempcCanvas.drawCircle((width / 2),
                (height / 2), bigCircleSize, bigPaint);

        canvas.drawBitmap(bitmap, 0, 0, null);

        canvas.drawCircle((width / 2),
                (height / 2), smallCircleSize, smallPaint);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        new Thread() {
            @Override
            public void run() {

                while (bigCircleSize <= width || smallCircleSize >= 0) {
                    if (isDraw) {
                        bigCircleSize += Bigstep;
                        smallCircleSize -= smallstep;
                        SystemClock.sleep(16);
                        postInvalidate();
                    }
                }
            }
        }.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * dp 转 px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
