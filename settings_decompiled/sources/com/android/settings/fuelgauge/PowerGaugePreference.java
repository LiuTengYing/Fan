package com.android.settings.fuelgauge;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settingslib.Utils;
import com.android.settingslib.widget.AppPreference;
/* loaded from: classes.dex */
public class PowerGaugePreference extends AppPreference {
    private BatteryDiffEntry mBatteryDiffEntry;
    private CharSequence mContentDescription;
    private BatteryEntry mInfo;
    private CharSequence mProgress;
    private boolean mShowAnomalyIcon;

    public PowerGaugePreference(Context context, Drawable drawable, CharSequence charSequence, BatteryEntry batteryEntry) {
        this(context, null, drawable, charSequence, batteryEntry);
    }

    public PowerGaugePreference(Context context) {
        this(context, null, null, null, null);
    }

    public PowerGaugePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, null, null, null);
    }

    private PowerGaugePreference(Context context, AttributeSet attributeSet, Drawable drawable, CharSequence charSequence, BatteryEntry batteryEntry) {
        super(context, attributeSet);
        if (drawable != null) {
            setIcon(drawable);
        }
        setWidgetLayoutResource(R$layout.preference_widget_summary);
        this.mInfo = batteryEntry;
        this.mContentDescription = charSequence;
        this.mShowAnomalyIcon = false;
    }

    public void setContentDescription(String str) {
        this.mContentDescription = str;
        notifyChanged();
    }

    public void setPercent(double d) {
        this.mProgress = Utils.formatPercentage(d, true);
        notifyChanged();
    }

    public String getPercent() {
        return this.mProgress.toString();
    }

    public void shouldShowAnomalyIcon(boolean z) {
        this.mShowAnomalyIcon = z;
        notifyChanged();
    }

    public void setBatteryDiffEntry(BatteryDiffEntry batteryDiffEntry) {
        this.mBatteryDiffEntry = batteryDiffEntry;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BatteryEntry getInfo() {
        return this.mInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BatteryDiffEntry getBatteryDiffEntry() {
        return this.mBatteryDiffEntry;
    }

    @Override // com.android.settingslib.widget.AppPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(R$id.widget_summary);
        textView.setText(this.mProgress);
        if (this.mShowAnomalyIcon) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R$drawable.ic_warning_24dp, 0, 0, 0);
        } else {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }
        if (this.mContentDescription != null) {
            ((TextView) preferenceViewHolder.findViewById(16908310)).setContentDescription(this.mContentDescription);
        }
    }
}
