package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class AccessibilityQuickSettingsTooltipWindow extends PopupWindow {
    private final View.AccessibilityDelegate mAccessibilityDelegate;
    private long mCloseDelayTimeMillis;
    private final Context mContext;
    private Handler mHandler;

    public AccessibilityQuickSettingsTooltipWindow(Context context) {
        super(context);
        this.mAccessibilityDelegate = new View.AccessibilityDelegate() { // from class: com.android.settings.accessibility.AccessibilityQuickSettingsTooltipWindow.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, AccessibilityQuickSettingsTooltipWindow.this.mContext.getString(R$string.accessibility_quick_settings_tooltip_dismiss)));
            }

            @Override // android.view.View.AccessibilityDelegate
            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                if (i == 16) {
                    AccessibilityQuickSettingsTooltipWindow.this.dismiss();
                    return true;
                }
                return super.performAccessibilityAction(view, i, bundle);
            }
        };
        this.mContext = context;
    }

    public void setup(CharSequence charSequence, int i) {
        setup(charSequence, i, 0L);
    }

    public void setup(CharSequence charSequence, int i, long j) {
        this.mCloseDelayTimeMillis = j;
        setBackgroundDrawable(new ColorDrawable(this.mContext.getColor(17170445)));
        View inflate = ((LayoutInflater) this.mContext.getSystemService(LayoutInflater.class)).inflate(R$layout.accessibility_qs_tooltip, (ViewGroup) null);
        inflate.setFocusable(true);
        inflate.setAccessibilityDelegate(this.mAccessibilityDelegate);
        setContentView(inflate);
        ((ImageView) getContentView().findViewById(R$id.qs_illustration)).setImageResource(i);
        TextView textView = (TextView) getContentView().findViewById(R$id.qs_content);
        textView.setText(charSequence);
        setWidth(getWindowWidthWith(textView));
        setHeight(-2);
        setFocusable(true);
        setOutsideTouchable(true);
    }

    public void showAtTopCenter(View view) {
        showAtLocation(view, 49, 0, 0);
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        super.dismiss();
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
        scheduleAutoCloseAction();
    }

    private void scheduleAutoCloseAction() {
        if (this.mCloseDelayTimeMillis <= 0) {
            return;
        }
        if (this.mHandler == null) {
            this.mHandler = new Handler(this.mContext.getMainLooper());
        }
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.AccessibilityQuickSettingsTooltipWindow$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AccessibilityQuickSettingsTooltipWindow.this.dismiss();
            }
        }, this.mCloseDelayTimeMillis);
    }

    private int getWindowWidthWith(TextView textView) {
        textView.measure(View.MeasureSpec.makeMeasureSpec(getAvailableWindowWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        return textView.getMeasuredWidth();
    }

    int getAvailableWindowWidth() {
        Resources resources = this.mContext.getResources();
        return resources.getDisplayMetrics().widthPixels - (resources.getDimensionPixelSize(R$dimen.accessibility_qs_tooltip_margin) * 2);
    }
}
