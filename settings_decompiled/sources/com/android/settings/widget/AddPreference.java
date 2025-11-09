package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class AddPreference extends RestrictedPreference implements View.OnClickListener {
    private View mAddWidget;
    private OnAddClickListener mListener;
    private View mWidgetFrame;

    /* loaded from: classes.dex */
    public interface OnAddClickListener {
        void onAddClick(AddPreference addPreference);
    }

    public AddPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    int getAddWidgetResId() {
        return R$id.add_preference_widget;
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.mListener = onAddClickListener;
        View view = this.mWidgetFrame;
        if (view != null) {
            view.setVisibility(shouldHideSecondTarget() ? 8 : 0);
        }
    }

    public void setAddWidgetEnabled(boolean z) {
        View view = this.mAddWidget;
        if (view != null) {
            view.setEnabled(z);
        }
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R$layout.preference_widget_add;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return this.mListener == null;
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mWidgetFrame = preferenceViewHolder.findViewById(16908312);
        View findViewById = preferenceViewHolder.findViewById(getAddWidgetResId());
        this.mAddWidget = findViewById;
        findViewById.setEnabled(true);
        this.mAddWidget.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        OnAddClickListener onAddClickListener;
        if (view.getId() != getAddWidgetResId() || (onAddClickListener = this.mListener) == null) {
            return;
        }
        onAddClickListener.onAddClick(this);
    }
}
