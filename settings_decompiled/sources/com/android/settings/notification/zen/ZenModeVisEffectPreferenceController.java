package com.android.settings.notification.zen;

import android.app.ActivityManager;
import android.content.Context;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import com.android.settings.widget.DisabledCheckBoxPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class ZenModeVisEffectPreferenceController extends AbstractZenModePreferenceController implements Preference.OnPreferenceChangeListener {
    protected final int mEffect;
    protected final String mKey;
    protected final int mMetricsCategory;
    protected final int[] mParentSuppressedEffects;
    private PreferenceScreen mScreen;

    public ZenModeVisEffectPreferenceController(Context context, Lifecycle lifecycle, String str, int i, int i2, int[] iArr) {
        super(context, str, lifecycle);
        this.mKey = str;
        this.mEffect = i;
        this.mMetricsCategory = i2;
        this.mParentSuppressedEffects = iArr;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        int i = this.mEffect;
        if (i == 8) {
            return this.mContext.getResources().getBoolean(17891684) && Utils.ledFileIsExists();
        } else if (i == 64) {
            return !ActivityManager.isLowRamDeviceStatic();
        } else {
            return true;
        }
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z;
        super.updateState(preference);
        boolean isVisualEffectSuppressed = this.mBackend.isVisualEffectSuppressed(this.mEffect);
        int[] iArr = this.mParentSuppressedEffects;
        if (iArr != null) {
            z = false;
            for (int i : iArr) {
                z |= this.mBackend.isVisualEffectSuppressed(i);
            }
        } else {
            z = false;
        }
        if (z) {
            ((CheckBoxPreference) preference).setChecked(z);
            onPreferenceChange(preference, Boolean.valueOf(z));
            ((DisabledCheckBoxPreference) preference).enableCheckbox(false);
            return;
        }
        ((DisabledCheckBoxPreference) preference).enableCheckbox(true);
        ((CheckBoxPreference) preference).setChecked(isVisualEffectSuppressed);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        this.mMetricsFeatureProvider.action(this.mContext, this.mMetricsCategory, booleanValue);
        this.mBackend.saveVisualEffectsPolicy(this.mEffect, booleanValue);
        return true;
    }
}
