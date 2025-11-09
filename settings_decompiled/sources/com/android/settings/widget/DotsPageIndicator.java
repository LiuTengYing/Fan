package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R$styleable;
import java.util.Arrays;
/* loaded from: classes.dex */
public class DotsPageIndicator extends View implements ViewPager.OnPageChangeListener {
    public static final String TAG = DotsPageIndicator.class.getSimpleName();
    private long animDuration;
    private long animHalfDuration;
    private boolean attachedState;
    private final Path combinedUnselectedPath;
    float controlX1;
    float controlX2;
    float controlY1;
    float controlY2;
    private int currentPage;
    private float dotBottomY;
    private float[] dotCenterX;
    private float dotCenterY;
    private int dotDiameter;
    private float dotRadius;
    private float[] dotRevealFractions;
    private float dotTopY;
    float endX1;
    float endX2;
    float endY1;
    float endY2;
    private int gap;
    private float halfDotRadius;
    private final Interpolator interpolator;
    private AnimatorSet joiningAnimationSet;
    private ValueAnimator[] joiningAnimations;
    private float[] joiningFractions;
    private ValueAnimator moveAnimation;
    private ViewPager.OnPageChangeListener pageChangeListener;
    private int pageCount;
    private final RectF rectF;
    private PendingRetreatAnimator retreatAnimation;
    private float retreatingJoinX1;
    private float retreatingJoinX2;
    private PendingRevealAnimator[] revealAnimations;
    private int selectedColour;
    private boolean selectedDotInPosition;
    private float selectedDotX;
    private final Paint selectedPaint;
    private int unselectedColour;
    private final Path unselectedDotLeftPath;
    private final Path unselectedDotPath;
    private final Path unselectedDotRightPath;
    private final Paint unselectedPaint;
    private ViewPager viewPager;

    public DotsPageIndicator(Context context) {
        this(context, null, 0);
    }

    public DotsPageIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DotsPageIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int i2 = (int) context.getResources().getDisplayMetrics().scaledDensity;
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.DotsPageIndicator, i, 0);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.DotsPageIndicator_dotDiameter, i2 * 8);
        this.dotDiameter = dimensionPixelSize;
        float f = dimensionPixelSize / 2;
        this.dotRadius = f;
        this.halfDotRadius = f / 2.0f;
        this.gap = obtainStyledAttributes.getDimensionPixelSize(R$styleable.DotsPageIndicator_dotGap, i2 * 12);
        long integer = obtainStyledAttributes.getInteger(R$styleable.DotsPageIndicator_animationDuration, 400);
        this.animDuration = integer;
        this.animHalfDuration = integer / 2;
        this.unselectedColour = obtainStyledAttributes.getColor(R$styleable.DotsPageIndicator_pageIndicatorColor, -2130706433);
        this.selectedColour = obtainStyledAttributes.getColor(R$styleable.DotsPageIndicator_currentPageIndicatorColor, -1);
        obtainStyledAttributes.recycle();
        Paint paint = new Paint(1);
        this.unselectedPaint = paint;
        paint.setColor(this.unselectedColour);
        Paint paint2 = new Paint(1);
        this.selectedPaint = paint2;
        paint2.setColor(this.selectedColour);
        this.interpolator = AnimationUtils.loadInterpolator(context, 17563661);
        this.combinedUnselectedPath = new Path();
        this.unselectedDotPath = new Path();
        this.unselectedDotLeftPath = new Path();
        this.unselectedDotRightPath = new Path();
        this.rectF = new RectF();
        addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.settings.widget.DotsPageIndicator.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
                DotsPageIndicator.this.attachedState = true;
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
                DotsPageIndicator.this.attachedState = false;
            }
        });
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.setOnPageChangeListener(this);
        setPageCount(viewPager.getAdapter().getCount());
        viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() { // from class: com.android.settings.widget.DotsPageIndicator.2
            @Override // android.database.DataSetObserver
            public void onChanged() {
                DotsPageIndicator dotsPageIndicator = DotsPageIndicator.this;
                dotsPageIndicator.setPageCount(dotsPageIndicator.viewPager.getAdapter().getCount());
            }
        });
        setCurrentPageImmediate();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.pageChangeListener = onPageChangeListener;
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
        ViewPager.OnPageChangeListener onPageChangeListener = this.pageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(i, f, i2);
        }
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        if (this.attachedState) {
            setSelectedPage(i);
        } else {
            setCurrentPageImmediate();
        }
        ViewPager.OnPageChangeListener onPageChangeListener = this.pageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(i);
        }
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
        ViewPager.OnPageChangeListener onPageChangeListener = this.pageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPageCount(int i) {
        this.pageCount = i;
        calculateDotPositions();
        resetState();
    }

    private void calculateDotPositions() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        float width = paddingLeft + ((((getWidth() - getPaddingRight()) - paddingLeft) - getRequiredWidth()) / 2) + this.dotRadius;
        this.dotCenterX = new float[this.pageCount];
        for (int i = 0; i < this.pageCount; i++) {
            this.dotCenterX[i] = ((this.dotDiameter + this.gap) * i) + width;
        }
        float f = paddingTop;
        this.dotTopY = f;
        this.dotCenterY = f + this.dotRadius;
        this.dotBottomY = paddingTop + this.dotDiameter;
        setCurrentPageImmediate();
    }

    private void setCurrentPageImmediate() {
        ViewPager viewPager = this.viewPager;
        if (viewPager != null) {
            this.currentPage = viewPager.getCurrentItem();
        } else {
            this.currentPage = 0;
        }
        if (this.pageCount > 0) {
            this.selectedDotX = this.dotCenterX[this.currentPage];
        }
    }

    private void resetState() {
        int i = this.pageCount;
        if (i > 0) {
            float[] fArr = new float[i - 1];
            this.joiningFractions = fArr;
            Arrays.fill(fArr, 0.0f);
            float[] fArr2 = new float[this.pageCount];
            this.dotRevealFractions = fArr2;
            Arrays.fill(fArr2, 0.0f);
            this.retreatingJoinX1 = -1.0f;
            this.retreatingJoinX2 = -1.0f;
            this.selectedDotInPosition = true;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int desiredHeight = getDesiredHeight();
        int mode = View.MeasureSpec.getMode(i2);
        if (mode == Integer.MIN_VALUE) {
            desiredHeight = Math.min(desiredHeight, View.MeasureSpec.getSize(i2));
        } else if (mode == 1073741824) {
            desiredHeight = View.MeasureSpec.getSize(i2);
        }
        int desiredWidth = getDesiredWidth();
        int mode2 = View.MeasureSpec.getMode(i);
        if (mode2 == Integer.MIN_VALUE) {
            desiredWidth = Math.min(desiredWidth, View.MeasureSpec.getSize(i));
        } else if (mode2 == 1073741824) {
            desiredWidth = View.MeasureSpec.getSize(i);
        }
        setMeasuredDimension(desiredWidth, desiredHeight);
        calculateDotPositions();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        setMeasuredDimension(i, i2);
        calculateDotPositions();
    }

    @Override // android.view.View
    public void clearAnimation() {
        super.clearAnimation();
        cancelRunningAnimations();
    }

    private int getDesiredHeight() {
        return getPaddingTop() + this.dotDiameter + getPaddingBottom();
    }

    private int getRequiredWidth() {
        int i = this.pageCount;
        return (this.dotDiameter * i) + ((i - 1) * this.gap);
    }

    private int getDesiredWidth() {
        return getPaddingLeft() + getRequiredWidth() + getPaddingRight();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.viewPager == null || this.pageCount == 0) {
            return;
        }
        drawUnselected(canvas);
        drawSelected(canvas);
    }

    private void drawUnselected(Canvas canvas) {
        this.combinedUnselectedPath.rewind();
        int i = 0;
        while (true) {
            int i2 = this.pageCount;
            if (i >= i2) {
                break;
            }
            int i3 = i == i2 + (-1) ? i : i + 1;
            float[] fArr = this.dotCenterX;
            this.combinedUnselectedPath.op(getUnselectedPath(i, fArr[i], fArr[i3], i == i2 + (-1) ? -1.0f : this.joiningFractions[i], this.dotRevealFractions[i]), Path.Op.UNION);
            i++;
        }
        if (this.retreatingJoinX1 != -1.0f) {
            this.combinedUnselectedPath.op(getRetreatingJoinPath(), Path.Op.UNION);
        }
        canvas.drawPath(this.combinedUnselectedPath, this.unselectedPaint);
    }

    private Path getUnselectedPath(int i, float f, float f2, float f3, float f4) {
        this.unselectedDotPath.rewind();
        int i2 = (f3 > 0.0f ? 1 : (f3 == 0.0f ? 0 : -1));
        if ((i2 == 0 || f3 == -1.0f) && f4 == 0.0f && (i != this.currentPage || !this.selectedDotInPosition)) {
            this.unselectedDotPath.addCircle(this.dotCenterX[i], this.dotCenterY, this.dotRadius, Path.Direction.CW);
        }
        if (i2 > 0 && f3 < 0.5f && this.retreatingJoinX1 == -1.0f) {
            this.unselectedDotLeftPath.rewind();
            this.unselectedDotLeftPath.moveTo(f, this.dotBottomY);
            RectF rectF = this.rectF;
            float f5 = this.dotRadius;
            rectF.set(f - f5, this.dotTopY, f5 + f, this.dotBottomY);
            this.unselectedDotLeftPath.arcTo(this.rectF, 90.0f, 180.0f, true);
            float f6 = this.dotRadius + f + (this.gap * f3);
            this.endX1 = f6;
            float f7 = this.dotCenterY;
            this.endY1 = f7;
            float f8 = this.halfDotRadius;
            float f9 = f + f8;
            this.controlX1 = f9;
            float f10 = this.dotTopY;
            this.controlY1 = f10;
            this.controlX2 = f6;
            float f11 = f7 - f8;
            this.controlY2 = f11;
            this.unselectedDotLeftPath.cubicTo(f9, f10, f6, f11, f6, f7);
            this.endX2 = f;
            float f12 = this.dotBottomY;
            this.endY2 = f12;
            float f13 = this.endX1;
            this.controlX1 = f13;
            float f14 = this.endY1;
            float f15 = this.halfDotRadius;
            float f16 = f14 + f15;
            this.controlY1 = f16;
            float f17 = f + f15;
            this.controlX2 = f17;
            this.controlY2 = f12;
            this.unselectedDotLeftPath.cubicTo(f13, f16, f17, f12, f, f12);
            this.unselectedDotPath.op(this.unselectedDotLeftPath, Path.Op.UNION);
            this.unselectedDotRightPath.rewind();
            this.unselectedDotRightPath.moveTo(f2, this.dotBottomY);
            RectF rectF2 = this.rectF;
            float f18 = this.dotRadius;
            rectF2.set(f2 - f18, this.dotTopY, f18 + f2, this.dotBottomY);
            this.unselectedDotRightPath.arcTo(this.rectF, 90.0f, -180.0f, true);
            float f19 = (f2 - this.dotRadius) - (this.gap * f3);
            this.endX1 = f19;
            float f20 = this.dotCenterY;
            this.endY1 = f20;
            float f21 = this.halfDotRadius;
            float f22 = f2 - f21;
            this.controlX1 = f22;
            float f23 = this.dotTopY;
            this.controlY1 = f23;
            this.controlX2 = f19;
            float f24 = f20 - f21;
            this.controlY2 = f24;
            this.unselectedDotRightPath.cubicTo(f22, f23, f19, f24, f19, f20);
            this.endX2 = f2;
            float f25 = this.dotBottomY;
            this.endY2 = f25;
            float f26 = this.endX1;
            this.controlX1 = f26;
            float f27 = this.endY1;
            float f28 = this.halfDotRadius;
            float f29 = f27 + f28;
            this.controlY1 = f29;
            float f30 = f2 - f28;
            this.controlX2 = f30;
            this.controlY2 = f25;
            this.unselectedDotRightPath.cubicTo(f26, f29, f30, f25, f2, f25);
            this.unselectedDotPath.op(this.unselectedDotRightPath, Path.Op.UNION);
        }
        if (f3 > 0.5f && f3 < 1.0f && this.retreatingJoinX1 == -1.0f) {
            this.unselectedDotPath.moveTo(f, this.dotBottomY);
            RectF rectF3 = this.rectF;
            float f31 = this.dotRadius;
            rectF3.set(f - f31, this.dotTopY, f31 + f, this.dotBottomY);
            this.unselectedDotPath.arcTo(this.rectF, 90.0f, 180.0f, true);
            float f32 = this.dotRadius;
            float f33 = f + f32 + (this.gap / 2);
            this.endX1 = f33;
            float f34 = this.dotCenterY - (f3 * f32);
            this.endY1 = f34;
            float f35 = f33 - (f3 * f32);
            this.controlX1 = f35;
            float f36 = this.dotTopY;
            this.controlY1 = f36;
            float f37 = 1.0f - f3;
            float f38 = f33 - (f32 * f37);
            this.controlX2 = f38;
            this.controlY2 = f34;
            this.unselectedDotPath.cubicTo(f35, f36, f38, f34, f33, f34);
            this.endX2 = f2;
            float f39 = this.dotTopY;
            this.endY2 = f39;
            float f40 = this.endX1;
            float f41 = this.dotRadius;
            float f42 = (f37 * f41) + f40;
            this.controlX1 = f42;
            float f43 = this.endY1;
            this.controlY1 = f43;
            float f44 = f40 + (f41 * f3);
            this.controlX2 = f44;
            this.controlY2 = f39;
            this.unselectedDotPath.cubicTo(f42, f43, f44, f39, f2, f39);
            RectF rectF4 = this.rectF;
            float f45 = this.dotRadius;
            rectF4.set(f2 - f45, this.dotTopY, f45 + f2, this.dotBottomY);
            this.unselectedDotPath.arcTo(this.rectF, 270.0f, 180.0f, true);
            float f46 = this.dotCenterY;
            float f47 = this.dotRadius;
            float f48 = f46 + (f3 * f47);
            this.endY1 = f48;
            float f49 = this.endX1;
            float f50 = f49 + (f3 * f47);
            this.controlX1 = f50;
            float f51 = this.dotBottomY;
            this.controlY1 = f51;
            float f52 = (f47 * f37) + f49;
            this.controlX2 = f52;
            this.controlY2 = f48;
            this.unselectedDotPath.cubicTo(f50, f51, f52, f48, f49, f48);
            this.endX2 = f;
            float f53 = this.dotBottomY;
            this.endY2 = f53;
            float f54 = this.endX1;
            float f55 = this.dotRadius;
            float f56 = f54 - (f37 * f55);
            this.controlX1 = f56;
            float f57 = this.endY1;
            this.controlY1 = f57;
            float f58 = f54 - (f55 * f3);
            this.controlX2 = f58;
            this.controlY2 = f53;
            this.unselectedDotPath.cubicTo(f56, f57, f58, f53, f, f53);
        }
        if (f3 == 1.0f && this.retreatingJoinX1 == -1.0f) {
            RectF rectF5 = this.rectF;
            float f59 = this.dotRadius;
            rectF5.set(f - f59, this.dotTopY, f59 + f2, this.dotBottomY);
            Path path = this.unselectedDotPath;
            RectF rectF6 = this.rectF;
            float f60 = this.dotRadius;
            path.addRoundRect(rectF6, f60, f60, Path.Direction.CW);
        }
        if (f4 > 1.0E-5f) {
            this.unselectedDotPath.addCircle(f, this.dotCenterY, this.dotRadius * f4, Path.Direction.CW);
        }
        return this.unselectedDotPath;
    }

    private Path getRetreatingJoinPath() {
        this.unselectedDotPath.rewind();
        this.rectF.set(this.retreatingJoinX1, this.dotTopY, this.retreatingJoinX2, this.dotBottomY);
        Path path = this.unselectedDotPath;
        RectF rectF = this.rectF;
        float f = this.dotRadius;
        path.addRoundRect(rectF, f, f, Path.Direction.CW);
        return this.unselectedDotPath;
    }

    private void drawSelected(Canvas canvas) {
        canvas.drawCircle(this.selectedDotX, this.dotCenterY, this.dotRadius, this.selectedPaint);
    }

    private void setSelectedPage(int i) {
        int i2 = this.currentPage;
        if (i == i2 || this.pageCount == 0) {
            return;
        }
        this.currentPage = i;
        cancelRunningAnimations();
        int abs = Math.abs(i - i2);
        this.moveAnimation = createMoveSelectedAnimator(this.dotCenterX[i], i2, i, abs);
        this.joiningAnimations = new ValueAnimator[abs];
        for (int i3 = 0; i3 < abs; i3++) {
            this.joiningAnimations[i3] = createJoiningAnimator(i > i2 ? i2 + i3 : (i2 - 1) - i3, i3 * (this.animDuration / 8));
        }
        this.moveAnimation.start();
        startJoiningAnimations();
    }

    private ValueAnimator createMoveSelectedAnimator(float f, int i, int i2, int i3) {
        StartPredicate leftwardStartPredicate;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.selectedDotX, f);
        if (i2 > i) {
            leftwardStartPredicate = new RightwardStartPredicate(f - ((f - this.selectedDotX) * 0.25f));
        } else {
            leftwardStartPredicate = new LeftwardStartPredicate(f + ((this.selectedDotX - f) * 0.25f));
        }
        this.retreatAnimation = new PendingRetreatAnimator(i, i2, i3, leftwardStartPredicate);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.DotsPageIndicator.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DotsPageIndicator.this.selectedDotX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                DotsPageIndicator.this.retreatAnimation.startIfNecessary(DotsPageIndicator.this.selectedDotX);
                DotsPageIndicator.this.postInvalidateOnAnimation();
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.widget.DotsPageIndicator.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                DotsPageIndicator.this.selectedDotInPosition = false;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                DotsPageIndicator.this.selectedDotInPosition = true;
            }
        });
        ofFloat.setStartDelay(this.selectedDotInPosition ? this.animDuration / 4 : 0L);
        ofFloat.setDuration((this.animDuration * 3) / 4);
        ofFloat.setInterpolator(this.interpolator);
        return ofFloat;
    }

    private ValueAnimator createJoiningAnimator(final int i, long j) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.DotsPageIndicator.5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DotsPageIndicator.this.setJoiningFraction(i, valueAnimator.getAnimatedFraction());
            }
        });
        ofFloat.setDuration(this.animHalfDuration);
        ofFloat.setStartDelay(j);
        ofFloat.setInterpolator(this.interpolator);
        return ofFloat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setJoiningFraction(int i, float f) {
        this.joiningFractions[i] = f;
        postInvalidateOnAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearJoiningFractions() {
        Arrays.fill(this.joiningFractions, 0.0f);
        postInvalidateOnAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDotRevealFraction(int i, float f) {
        this.dotRevealFractions[i] = f;
        postInvalidateOnAnimation();
    }

    private void cancelRunningAnimations() {
        cancelMoveAnimation();
        cancelJoiningAnimations();
        cancelRetreatAnimation();
        cancelRevealAnimations();
        resetState();
    }

    private void cancelMoveAnimation() {
        ValueAnimator valueAnimator = this.moveAnimation;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            return;
        }
        this.moveAnimation.cancel();
    }

    private void startJoiningAnimations() {
        AnimatorSet animatorSet = new AnimatorSet();
        this.joiningAnimationSet = animatorSet;
        animatorSet.playTogether(this.joiningAnimations);
        this.joiningAnimationSet.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelJoiningAnimations() {
        AnimatorSet animatorSet = this.joiningAnimationSet;
        if (animatorSet == null || !animatorSet.isRunning()) {
            return;
        }
        this.joiningAnimationSet.cancel();
    }

    private void cancelRetreatAnimation() {
        PendingRetreatAnimator pendingRetreatAnimator = this.retreatAnimation;
        if (pendingRetreatAnimator == null || !pendingRetreatAnimator.isRunning()) {
            return;
        }
        this.retreatAnimation.cancel();
    }

    private void cancelRevealAnimations() {
        PendingRevealAnimator[] pendingRevealAnimatorArr = this.revealAnimations;
        if (pendingRevealAnimatorArr != null) {
            for (PendingRevealAnimator pendingRevealAnimator : pendingRevealAnimatorArr) {
                pendingRevealAnimator.cancel();
            }
        }
    }

    int getUnselectedColour() {
        return this.unselectedColour;
    }

    int getSelectedColour() {
        return this.selectedColour;
    }

    float getDotCenterY() {
        return this.dotCenterY;
    }

    float getSelectedDotX() {
        return this.selectedDotX;
    }

    int getCurrentPage() {
        return this.currentPage;
    }

    /* loaded from: classes.dex */
    public abstract class PendingStartAnimator extends ValueAnimator {
        protected boolean hasStarted = false;
        protected StartPredicate predicate;

        public PendingStartAnimator(StartPredicate startPredicate) {
            this.predicate = startPredicate;
        }

        public void startIfNecessary(float f) {
            if (this.hasStarted || !this.predicate.shouldStart(f)) {
                return;
            }
            start();
            this.hasStarted = true;
        }
    }

    /* loaded from: classes.dex */
    public class PendingRetreatAnimator extends PendingStartAnimator {
        public PendingRetreatAnimator(int i, int i2, int i3, StartPredicate startPredicate) {
            super(startPredicate);
            float f;
            float f2;
            float f3;
            float f4;
            float max;
            float f5;
            float f6;
            float f7;
            setDuration(DotsPageIndicator.this.animHalfDuration);
            setInterpolator(DotsPageIndicator.this.interpolator);
            if (i2 > i) {
                f = Math.min(DotsPageIndicator.this.dotCenterX[i], DotsPageIndicator.this.selectedDotX);
                f2 = DotsPageIndicator.this.dotRadius;
            } else {
                f = DotsPageIndicator.this.dotCenterX[i2];
                f2 = DotsPageIndicator.this.dotRadius;
            }
            final float f8 = f - f2;
            if (i2 > i) {
                f3 = DotsPageIndicator.this.dotCenterX[i2];
                f4 = DotsPageIndicator.this.dotRadius;
            } else {
                f3 = DotsPageIndicator.this.dotCenterX[i2];
                f4 = DotsPageIndicator.this.dotRadius;
            }
            float f9 = f3 - f4;
            if (i2 > i) {
                max = DotsPageIndicator.this.dotCenterX[i2];
                f5 = DotsPageIndicator.this.dotRadius;
            } else {
                max = Math.max(DotsPageIndicator.this.dotCenterX[i], DotsPageIndicator.this.selectedDotX);
                f5 = DotsPageIndicator.this.dotRadius;
            }
            final float f10 = max + f5;
            if (i2 > i) {
                f6 = DotsPageIndicator.this.dotCenterX[i2];
                f7 = DotsPageIndicator.this.dotRadius;
            } else {
                f6 = DotsPageIndicator.this.dotCenterX[i2];
                f7 = DotsPageIndicator.this.dotRadius;
            }
            float f11 = f6 + f7;
            DotsPageIndicator.this.revealAnimations = new PendingRevealAnimator[i3];
            final int[] iArr = new int[i3];
            int i4 = 0;
            if (f8 != f9) {
                setFloatValues(f8, f9);
                while (i4 < i3) {
                    int i5 = i + i4;
                    DotsPageIndicator.this.revealAnimations[i4] = new PendingRevealAnimator(i5, new RightwardStartPredicate(DotsPageIndicator.this.dotCenterX[i5]));
                    iArr[i4] = i5;
                    i4++;
                }
                addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.DotsPageIndicator.PendingRetreatAnimator.1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        DotsPageIndicator.this.retreatingJoinX1 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        DotsPageIndicator.this.postInvalidateOnAnimation();
                        for (PendingRevealAnimator pendingRevealAnimator : DotsPageIndicator.this.revealAnimations) {
                            pendingRevealAnimator.startIfNecessary(DotsPageIndicator.this.retreatingJoinX1);
                        }
                    }
                });
            } else {
                setFloatValues(f10, f11);
                while (i4 < i3) {
                    int i6 = i - i4;
                    DotsPageIndicator.this.revealAnimations[i4] = new PendingRevealAnimator(i6, new LeftwardStartPredicate(DotsPageIndicator.this.dotCenterX[i6]));
                    iArr[i4] = i6;
                    i4++;
                }
                addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.DotsPageIndicator.PendingRetreatAnimator.2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        DotsPageIndicator.this.retreatingJoinX2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        DotsPageIndicator.this.postInvalidateOnAnimation();
                        for (PendingRevealAnimator pendingRevealAnimator : DotsPageIndicator.this.revealAnimations) {
                            pendingRevealAnimator.startIfNecessary(DotsPageIndicator.this.retreatingJoinX2);
                        }
                    }
                });
            }
            addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.widget.DotsPageIndicator.PendingRetreatAnimator.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    DotsPageIndicator.this.cancelJoiningAnimations();
                    DotsPageIndicator.this.clearJoiningFractions();
                    for (int i7 : iArr) {
                        DotsPageIndicator.this.setDotRevealFraction(i7, 1.0E-5f);
                    }
                    DotsPageIndicator.this.retreatingJoinX1 = f8;
                    DotsPageIndicator.this.retreatingJoinX2 = f10;
                    DotsPageIndicator.this.postInvalidateOnAnimation();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    DotsPageIndicator.this.retreatingJoinX1 = -1.0f;
                    DotsPageIndicator.this.retreatingJoinX2 = -1.0f;
                    DotsPageIndicator.this.postInvalidateOnAnimation();
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public class PendingRevealAnimator extends PendingStartAnimator {
        private final int dot;

        public PendingRevealAnimator(int i, StartPredicate startPredicate) {
            super(startPredicate);
            this.dot = i;
            setFloatValues(1.0E-5f, 1.0f);
            setDuration(DotsPageIndicator.this.animHalfDuration);
            setInterpolator(DotsPageIndicator.this.interpolator);
            addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.DotsPageIndicator.PendingRevealAnimator.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PendingRevealAnimator pendingRevealAnimator = PendingRevealAnimator.this;
                    DotsPageIndicator.this.setDotRevealFraction(pendingRevealAnimator.dot, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.widget.DotsPageIndicator.PendingRevealAnimator.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PendingRevealAnimator pendingRevealAnimator = PendingRevealAnimator.this;
                    DotsPageIndicator.this.setDotRevealFraction(pendingRevealAnimator.dot, 0.0f);
                    DotsPageIndicator.this.postInvalidateOnAnimation();
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public abstract class StartPredicate {
        protected float thresholdValue;

        abstract boolean shouldStart(float f);

        public StartPredicate(float f) {
            this.thresholdValue = f;
        }
    }

    /* loaded from: classes.dex */
    public class RightwardStartPredicate extends StartPredicate {
        public RightwardStartPredicate(float f) {
            super(f);
        }

        @Override // com.android.settings.widget.DotsPageIndicator.StartPredicate
        boolean shouldStart(float f) {
            return f > this.thresholdValue;
        }
    }

    /* loaded from: classes.dex */
    public class LeftwardStartPredicate extends StartPredicate {
        public LeftwardStartPredicate(float f) {
            super(f);
        }

        @Override // com.android.settings.widget.DotsPageIndicator.StartPredicate
        boolean shouldStart(float f) {
            return f < this.thresholdValue;
        }
    }
}
