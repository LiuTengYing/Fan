package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$attr;
import com.android.settings.R$color;
import com.android.settings.network.helper.OnSwitchItemClickLInstener;
import com.android.settings.utils.FastDoubleClickUtils;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class RestrictedSwitchPreferences extends RestrictedSwitchPreference {
    private Context mContext;

    /* loaded from: classes.dex */
    public interface onItemClickListener {
        void onItemClick();

        void onSwitchCheck(boolean z);
    }

    public RestrictedSwitchPreferences(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
    }

    public RestrictedSwitchPreferences(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
        this.mContext = context;
    }

    public RestrictedSwitchPreferences(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.switchPreferenceStyle, 16843629));
        this.mContext = context;
    }

    public RestrictedSwitchPreferences(Context context) {
        this(context, null);
        this.mContext = context;
    }

    @Override // com.android.settingslib.RestrictedSwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908352);
        if (findViewById != null && (findViewById instanceof Checkable) && (findViewById instanceof Switch)) {
            Switch r1 = (Switch) findViewById;
            r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.widget.RestrictedSwitchPreferences.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    Log.d("fangli", "onCheckedChanged: " + z);
                    OnSwitchItemClickLInstener.getInstance().setOnWifiSwitchCheck(z);
                }
            });
            r1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.RestrictedSwitchPreferences.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    FastDoubleClickUtils.isFastDoubleClick();
                }
            });
            findViewById.getRootView().setFilterTouchesWhenObscured(true);
        }
        View findViewById2 = preferenceViewHolder.findViewById(16908304);
        if (findViewById2 == null || !(findViewById2 instanceof TextView)) {
            return;
        }
        ((TextView) findViewById2).setTextColor(this.mContext.getResources().getColor(R$color.time_controller_text_color));
    }

    @Override // com.android.settingslib.RestrictedSwitchPreference, androidx.preference.Preference
    public void performClick() {
        if (FastDoubleClickUtils.isFastDoubleClick()) {
            return;
        }
        OnSwitchItemClickLInstener.getInstance().setOnWifiSwitchClick();
    }
}
