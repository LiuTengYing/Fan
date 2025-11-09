package com.android.settings.gestures;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$color;
/* loaded from: classes.dex */
public class BackGestureIndicatorDrawable extends Drawable {
    private Context mContext;
    private float mCurrentWidth;
    private float mFinalWidth;
    private boolean mReversed;
    private float mWidthChangePerMs;
    private Paint mPaint = new Paint();
    private TimeAnimator mTimeAnimator = new TimeAnimator();
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.gestures.BackGestureIndicatorDrawable.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                BackGestureIndicatorDrawable.this.mTimeAnimator.end();
                BackGestureIndicatorDrawable.this.mFinalWidth = message.arg1;
                BackGestureIndicatorDrawable backGestureIndicatorDrawable = BackGestureIndicatorDrawable.this;
                backGestureIndicatorDrawable.mWidthChangePerMs = Math.abs(backGestureIndicatorDrawable.mCurrentWidth - BackGestureIndicatorDrawable.this.mFinalWidth) / 200.0f;
                BackGestureIndicatorDrawable.this.mTimeAnimator.start();
            } else if (i != 3) {
            } else {
                BackGestureIndicatorDrawable backGestureIndicatorDrawable2 = BackGestureIndicatorDrawable.this;
                backGestureIndicatorDrawable2.mCurrentWidth = backGestureIndicatorDrawable2.mFinalWidth;
                removeMessages(1);
                sendMessageDelayed(obtainMessage(1, 0, 0), 700L);
                BackGestureIndicatorDrawable.this.invalidateSelf();
            }
        }
    };

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public BackGestureIndicatorDrawable(Context context, boolean z) {
        this.mContext = context;
        this.mReversed = z;
        this.mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() { // from class: com.android.settings.gestures.BackGestureIndicatorDrawable$$ExternalSyntheticLambda0
            @Override // android.animation.TimeAnimator.TimeListener
            public final void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2) {
                BackGestureIndicatorDrawable.this.lambda$new$0(timeAnimator, j, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TimeAnimator timeAnimator, long j, long j2) {
        updateCurrentWidth(j, j2);
        invalidateSelf();
    }

    private void updateCurrentWidth(long j, long j2) {
        synchronized (this.mTimeAnimator) {
            float f = ((float) j2) * this.mWidthChangePerMs;
            if (j < 200 && f < Math.abs(this.mFinalWidth - this.mCurrentWidth)) {
                float f2 = this.mCurrentWidth;
                this.mCurrentWidth = f2 + ((f2 < this.mFinalWidth ? 1.0f : -1.0f) * f);
            }
            this.mCurrentWidth = this.mFinalWidth;
            this.mTimeAnimator.end();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mContext.getResources().getColor(R$color.back_gesture_indicator));
        this.mPaint.setAlpha(64);
        int height = canvas.getHeight();
        int i = (int) this.mCurrentWidth;
        Rect rect = new Rect(0, 0, i, height);
        if (this.mReversed) {
            rect.offset(canvas.getWidth() - i, 0);
        }
        canvas.drawRect(rect, this.mPaint);
    }

    public void setWidth(int i) {
        if (i == 0) {
            this.mHandler.sendEmptyMessage(3);
            return;
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1, i, 0));
    }

    @VisibleForTesting
    public int getWidth() {
        return (int) this.mFinalWidth;
    }
}
