package com.android.settings.widget.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;
import com.android.settings.R$drawable;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class ExpandTextView extends TextView implements View.OnClickListener {
    private int arrowAlign;
    private int arrowDrawablePadding;
    private int arrowPosition;
    private boolean isDrawablePaddingResolved;
    private float mAnimAlphaStart;
    private boolean mAnimating;
    private int mAnimationDuration;
    private Drawable mCollapseDrawable;
    private boolean mCollapsed;
    private int mCollapsedHeight;
    private int mDrawableSize;
    private Drawable mExpandDrawable;
    private OnExpandStateChangeListener mListener;
    private int mMaxCollapsedLines;
    private int mTextHeightWithMaxLines;
    private boolean needCollapse;

    /* loaded from: classes.dex */
    public interface OnExpandStateChangeListener {
        void onChangeStateStart(boolean z);

        void onExpandStateChanged(TextView textView, boolean z);
    }

    private boolean isPostHoneycomb() {
        return true;
    }

    private boolean isPostLolipop() {
        return true;
    }

    public ExpandTextView(Context context) {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ExpandTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMaxCollapsedLines = 8;
        this.mCollapsed = true;
        this.mAnimating = false;
        this.needCollapse = true;
        this.mDrawableSize = 0;
        this.arrowAlign = 0;
        this.arrowPosition = 0;
        this.arrowDrawablePadding = 0;
        this.isDrawablePaddingResolved = false;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ExpandTextView, i, 0);
        this.mMaxCollapsedLines = obtainStyledAttributes.getInt(R$styleable.ExpandTextView_maxCollapsedLines, 2);
        this.mAnimationDuration = obtainStyledAttributes.getInt(R$styleable.ExpandTextView_animDuration, 300);
        this.mAnimAlphaStart = obtainStyledAttributes.getFloat(R$styleable.ExpandTextView_animAlphaStart, 0.7f);
        this.mExpandDrawable = obtainStyledAttributes.getDrawable(R$styleable.ExpandTextView_expandDrawable);
        this.mCollapseDrawable = obtainStyledAttributes.getDrawable(R$styleable.ExpandTextView_collapseDrawable);
        this.arrowAlign = obtainStyledAttributes.getInteger(R$styleable.ExpandTextView_arrowAlign, 0);
        this.arrowPosition = obtainStyledAttributes.getInteger(R$styleable.ExpandTextView_arrowPosition, 0);
        this.arrowDrawablePadding = (int) obtainStyledAttributes.getDimension(R$styleable.ExpandTextView_arrowPadding, 2.0f);
        obtainStyledAttributes.recycle();
        if (this.mExpandDrawable == null) {
            this.mExpandDrawable = getDrawable(getContext(), R$drawable.ic_expand_small_holo_light);
        }
        if (this.mCollapseDrawable == null) {
            this.mCollapseDrawable = getDrawable(getContext(), R$drawable.ic_collapse_small_holo_light);
        }
        setClickable(true);
        setOnClickListener(this);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        if (getVisibility() == 8 || this.mAnimating) {
            super.onMeasure(i, i2);
            return;
        }
        getLayoutParams().height = -2;
        setMaxLines(Integer.MAX_VALUE);
        super.onMeasure(i, i2);
        if (getLineCount() <= this.mMaxCollapsedLines) {
            this.needCollapse = false;
            return;
        }
        this.needCollapse = true;
        this.mTextHeightWithMaxLines = getRealTextViewHeight(this);
        if (this.mCollapsed) {
            setMaxLines(this.mMaxCollapsedLines);
        }
        this.mDrawableSize = this.mExpandDrawable.getIntrinsicWidth();
        if (!this.isDrawablePaddingResolved) {
            if (this.arrowPosition == 0) {
                setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + this.mDrawableSize + this.arrowDrawablePadding, getPaddingBottom());
            } else {
                setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom() + this.mExpandDrawable.getIntrinsicHeight() + this.arrowDrawablePadding);
            }
            this.isDrawablePaddingResolved = true;
        }
        super.onMeasure(i, i2);
        if (this.mCollapsed) {
            this.mCollapsedHeight = getMeasuredHeight();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        int totalPaddingLeft;
        int i;
        int i2;
        super.onDraw(canvas);
        if (this.needCollapse) {
            if (this.arrowPosition == 0) {
                i2 = (getWidth() - getTotalPaddingRight()) + this.arrowDrawablePadding;
                int i3 = this.arrowAlign;
                if (i3 == 1) {
                    i = getTotalPaddingTop();
                } else if (i3 == 2) {
                    i = (getHeight() - this.mExpandDrawable.getIntrinsicHeight()) / 2;
                } else {
                    i = (getHeight() - getTotalPaddingBottom()) - this.mExpandDrawable.getIntrinsicHeight();
                }
            } else {
                int height = (getHeight() - getTotalPaddingBottom()) + this.arrowDrawablePadding;
                int i4 = this.arrowAlign;
                if (i4 == 1) {
                    totalPaddingLeft = getTotalPaddingLeft();
                } else if (i4 == 2) {
                    totalPaddingLeft = (getWidth() - this.mExpandDrawable.getIntrinsicWidth()) / 2;
                } else {
                    totalPaddingLeft = (getWidth() - getTotalPaddingRight()) - this.mExpandDrawable.getIntrinsicWidth();
                }
                int i5 = totalPaddingLeft;
                i = height;
                i2 = i5;
            }
            canvas.translate(i2, i);
            if (this.mCollapsed) {
                Drawable drawable = this.mExpandDrawable;
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), this.mExpandDrawable.getIntrinsicHeight());
                this.mExpandDrawable.draw(canvas);
                return;
            }
            Drawable drawable2 = this.mCollapseDrawable;
            drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), this.mCollapseDrawable.getIntrinsicHeight());
            this.mCollapseDrawable.draw(canvas);
        }
    }

    @Override // android.widget.TextView
    public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
        setCollapsed(true);
        super.setText(charSequence, bufferType);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        ExpandCollapseAnimation expandCollapseAnimation;
        if (this.needCollapse) {
            this.mCollapsed = !this.mCollapsed;
            Bitmap createBitmap = Bitmap.createBitmap(this.mCollapseDrawable.getIntrinsicWidth(), this.mCollapseDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Drawable drawable = this.mCollapseDrawable;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), this.mCollapseDrawable.getIntrinsicHeight());
            this.mCollapseDrawable.draw(canvas);
            ImageSpan imageSpan = new ImageSpan(this.mExpandDrawable);
            ImageSpan imageSpan2 = new ImageSpan(getContext(), createBitmap);
            SpannableString spannableString = new SpannableString("icon");
            if (!this.mCollapsed) {
                imageSpan = imageSpan2;
            }
            spannableString.setSpan(imageSpan, 0, 4, 33);
            this.mAnimating = true;
            if (this.mCollapsed) {
                expandCollapseAnimation = new ExpandCollapseAnimation(this, getHeight(), this.mCollapsedHeight);
            } else {
                expandCollapseAnimation = new ExpandCollapseAnimation(this, getHeight(), this.mTextHeightWithMaxLines);
            }
            expandCollapseAnimation.setFillAfter(true);
            expandCollapseAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.settings.widget.view.ExpandTextView.1
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                    if (ExpandTextView.this.mListener != null) {
                        ExpandTextView.this.mListener.onChangeStateStart(!ExpandTextView.this.mCollapsed);
                    }
                    ExpandTextView expandTextView = ExpandTextView.this;
                    expandTextView.applyAlphaAnimation(expandTextView, expandTextView.mAnimAlphaStart);
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    ExpandTextView.this.clearAnimation();
                    ExpandTextView.this.mAnimating = false;
                    if (ExpandTextView.this.mListener != null) {
                        OnExpandStateChangeListener onExpandStateChangeListener = ExpandTextView.this.mListener;
                        ExpandTextView expandTextView = ExpandTextView.this;
                        onExpandStateChangeListener.onExpandStateChanged(expandTextView, !expandTextView.mCollapsed);
                    }
                }
            });
            clearAnimation();
            startAnimation(expandCollapseAnimation);
        }
    }

    /* loaded from: classes.dex */
    private class ExpandCollapseAnimation extends Animation {
        private final int mEndHeight;
        private final int mStartHeight;
        private final View mTargetView;

        @Override // android.view.animation.Animation
        public boolean willChangeBounds() {
            return true;
        }

        public ExpandCollapseAnimation(View view, int i, int i2) {
            this.mTargetView = view;
            this.mStartHeight = i;
            this.mEndHeight = i2;
            setDuration(ExpandTextView.this.mAnimationDuration);
        }

        @Override // android.view.animation.Animation
        protected void applyTransformation(float f, Transformation transformation) {
            int i = this.mEndHeight;
            int i2 = this.mStartHeight;
            int i3 = (int) (((i - i2) * f) + i2);
            this.mTargetView.getLayoutParams().height = i3;
            ExpandTextView.this.setMaxHeight(i3);
            if (Float.compare(ExpandTextView.this.mAnimAlphaStart, 1.0f) != 0) {
                ExpandTextView expandTextView = ExpandTextView.this;
                expandTextView.applyAlphaAnimation(expandTextView, expandTextView.mAnimAlphaStart + (f * (1.0f - ExpandTextView.this.mAnimAlphaStart)));
            }
        }

        @Override // android.view.animation.Animation
        public void initialize(int i, int i2, int i3, int i4) {
            super.initialize(i, i2, i3, i4);
        }
    }

    private Drawable getDrawable(Context context, int i) {
        Resources resources = context.getResources();
        if (isPostLolipop()) {
            return resources.getDrawable(i, context.getTheme());
        }
        return resources.getDrawable(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(11)
    public void applyAlphaAnimation(View view, float f) {
        if (isPostHoneycomb()) {
            view.setAlpha(f);
            return;
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f);
        alphaAnimation.setDuration(0L);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }

    private int getRealTextViewHeight(TextView textView) {
        return textView.getLayout().getLineTop(textView.getLineCount()) + textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
    }

    public void setCollapsed(boolean z) {
        this.mCollapsed = z;
    }

    public void setOnExpandStateChangeListener(OnExpandStateChangeListener onExpandStateChangeListener) {
        this.mListener = onExpandStateChangeListener;
    }
}
