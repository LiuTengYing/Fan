package com.android.settings.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import java.util.List;
/* loaded from: classes.dex */
public class LabeledSeekBar extends SeekBar {
    private final ExploreByTouchHelper mAccessHelper;
    private String[] mLabels;
    private int mLastProgress;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private final SeekBar.OnSeekBarChangeListener mProxySeekBarListener;

    public LabeledSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842875);
    }

    public LabeledSeekBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public LabeledSeekBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mLastProgress = -1;
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.widget.LabeledSeekBar.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (LabeledSeekBar.this.mOnSeekBarChangeListener != null) {
                    LabeledSeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (LabeledSeekBar.this.mOnSeekBarChangeListener != null) {
                    LabeledSeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i3, boolean z) {
                if (LabeledSeekBar.this.mOnSeekBarChangeListener != null) {
                    LabeledSeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i3, z);
                    LabeledSeekBar.this.sendClickEventForAccessibility(i3);
                }
                if (i3 != LabeledSeekBar.this.mLastProgress) {
                    seekBar.performHapticFeedback(4);
                    LabeledSeekBar.this.mLastProgress = i3;
                }
            }
        };
        this.mProxySeekBarListener = onSeekBarChangeListener;
        LabeledSeekBarExploreByTouchHelper labeledSeekBarExploreByTouchHelper = new LabeledSeekBarExploreByTouchHelper(this);
        this.mAccessHelper = labeledSeekBarExploreByTouchHelper;
        ViewCompat.setAccessibilityDelegate(this, labeledSeekBarExploreByTouchHelper);
        super.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int i) {
        ExploreByTouchHelper exploreByTouchHelper = this.mAccessHelper;
        if (exploreByTouchHelper != null) {
            exploreByTouchHelper.invalidateRoot();
        }
        super.setProgress(i);
    }

    public void setLabels(String[] strArr) {
        this.mLabels = strArr;
    }

    @Override // android.widget.SeekBar
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    @Override // android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        return this.mAccessHelper.dispatchHoverEvent(motionEvent) || super.dispatchHoverEvent(motionEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendClickEventForAccessibility(int i) {
        this.mAccessHelper.invalidateRoot();
        this.mAccessHelper.sendEventForVirtualView(i, 1);
    }

    /* loaded from: classes.dex */
    private class LabeledSeekBarExploreByTouchHelper extends ExploreByTouchHelper {
        private boolean mIsLayoutRtl;

        public LabeledSeekBarExploreByTouchHelper(LabeledSeekBar labeledSeekBar) {
            super(labeledSeekBar);
            this.mIsLayoutRtl = labeledSeekBar.getResources().getConfiguration().getLayoutDirection() == 1;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float f, float f2) {
            return getVirtualViewIdIndexFromX(f);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(List<Integer> list) {
            int max = LabeledSeekBar.this.getMax();
            for (int i = 0; i <= max; i++) {
                list.add(Integer.valueOf(i));
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (i != -1 && i2 == 16) {
                LabeledSeekBar.this.setProgress(i);
                sendEventForVirtualView(i, 1);
                return true;
            }
            return false;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setClassName(RadioButton.class.getName());
            accessibilityNodeInfoCompat.setBoundsInParent(getBoundsInParentFromVirtualViewId(i));
            accessibilityNodeInfoCompat.addAction(16);
            accessibilityNodeInfoCompat.setContentDescription(LabeledSeekBar.this.mLabels[i]);
            accessibilityNodeInfoCompat.setClickable(true);
            accessibilityNodeInfoCompat.setCheckable(true);
            accessibilityNodeInfoCompat.setChecked(i == LabeledSeekBar.this.getProgress());
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setClassName(RadioButton.class.getName());
            accessibilityEvent.setContentDescription(LabeledSeekBar.this.mLabels[i]);
            accessibilityEvent.setChecked(i == LabeledSeekBar.this.getProgress());
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForHost(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setClassName(RadioGroup.class.getName());
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateEventForHost(AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setClassName(RadioGroup.class.getName());
        }

        private int getHalfVirtualViewWidth() {
            return Math.max(0, ((LabeledSeekBar.this.getWidth() - LabeledSeekBar.this.getPaddingStart()) - LabeledSeekBar.this.getPaddingEnd()) / (LabeledSeekBar.this.getMax() * 2));
        }

        private int getVirtualViewIdIndexFromX(float f) {
            int min = Math.min((Math.max(0, (((int) f) - LabeledSeekBar.this.getPaddingStart()) / getHalfVirtualViewWidth()) + 1) / 2, LabeledSeekBar.this.getMax());
            return this.mIsLayoutRtl ? LabeledSeekBar.this.getMax() - min : min;
        }

        private Rect getBoundsInParentFromVirtualViewId(int i) {
            if (this.mIsLayoutRtl) {
                i = LabeledSeekBar.this.getMax() - i;
            }
            int i2 = i * 2;
            int halfVirtualViewWidth = ((i2 - 1) * getHalfVirtualViewWidth()) + LabeledSeekBar.this.getPaddingStart();
            int halfVirtualViewWidth2 = ((i2 + 1) * getHalfVirtualViewWidth()) + LabeledSeekBar.this.getPaddingStart();
            if (i == 0) {
                halfVirtualViewWidth = 0;
            }
            if (i == LabeledSeekBar.this.getMax()) {
                halfVirtualViewWidth2 = LabeledSeekBar.this.getWidth();
            }
            Rect rect = new Rect();
            rect.set(halfVirtualViewWidth, 0, halfVirtualViewWidth2, LabeledSeekBar.this.getHeight());
            return rect;
        }
    }
}
