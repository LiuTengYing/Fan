package com.android.settingslib.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;
/* loaded from: classes2.dex */
public class SelectorWithWidgetPreference extends CheckBoxPreference {
    private View mAppendix;
    private int mAppendixVisibility;
    private ImageView mExtraWidget;
    private View mExtraWidgetContainer;
    private View.OnClickListener mExtraWidgetOnClickListener;
    private boolean mIsCheckBox;
    private OnClickListener mListener;

    /* loaded from: classes2.dex */
    public interface OnClickListener {
        void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference);
    }

    public SelectorWithWidgetPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListener = null;
        this.mAppendixVisibility = -1;
        this.mIsCheckBox = false;
        init();
    }

    public SelectorWithWidgetPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListener = null;
        this.mAppendixVisibility = -1;
        this.mIsCheckBox = false;
        init();
    }

    public SelectorWithWidgetPreference(Context context, boolean z) {
        super(context, null);
        this.mListener = null;
        this.mAppendixVisibility = -1;
        this.mIsCheckBox = z;
        init();
    }

    public SelectorWithWidgetPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }

    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        OnClickListener onClickListener = this.mListener;
        if (onClickListener != null) {
            onClickListener.onRadioButtonClicked(this);
        }
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        int i;
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R$id.summary_container);
        if (findViewById != null) {
            findViewById.setVisibility(TextUtils.isEmpty(getSummary()) ? 8 : 0);
            View findViewById2 = preferenceViewHolder.findViewById(R$id.appendix);
            this.mAppendix = findViewById2;
            if (findViewById2 != null && (i = this.mAppendixVisibility) != -1) {
                findViewById2.setVisibility(i);
            }
        }
        this.mExtraWidget = (ImageView) preferenceViewHolder.findViewById(R$id.selector_extra_widget);
        this.mExtraWidgetContainer = preferenceViewHolder.findViewById(R$id.selector_extra_widget_container);
        setExtraWidgetOnClickListener(this.mExtraWidgetOnClickListener);
    }

    public void setAppendixVisibility(int i) {
        View view = this.mAppendix;
        if (view != null) {
            view.setVisibility(i);
        }
        this.mAppendixVisibility = i;
    }

    public void setExtraWidgetOnClickListener(View.OnClickListener onClickListener) {
        this.mExtraWidgetOnClickListener = onClickListener;
        ImageView imageView = this.mExtraWidget;
        if (imageView == null || this.mExtraWidgetContainer == null) {
            return;
        }
        imageView.setOnClickListener(onClickListener);
        this.mExtraWidgetContainer.setVisibility(this.mExtraWidgetOnClickListener != null ? 0 : 8);
    }

    public boolean isCheckBox() {
        return this.mIsCheckBox;
    }

    private void init() {
        if (this.mIsCheckBox) {
            setWidgetLayoutResource(R$layout.preference_widget_checkbox);
        } else {
            setWidgetLayoutResource(R$layout.preference_widget_radiobutton);
        }
        setLayoutResource(R$layout.preference_selector_with_widget);
        setIconSpaceReserved(false);
    }
}
