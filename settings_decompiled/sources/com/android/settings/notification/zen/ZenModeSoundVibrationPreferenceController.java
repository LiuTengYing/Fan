package com.android.settings.notification.zen;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class ZenModeSoundVibrationPreferenceController extends AbstractZenModePreferenceController {
    private final String mKey;
    private final ZenModeSettings.SummaryBuilder mSummaryBuilder;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeSoundVibrationPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
        this.mKey = str;
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(context);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int zenMode = getZenMode();
        if (zenMode == 2) {
            preference.setEnabled(false);
            preference.setSummary(this.mContext.getString(R$string.zen_mode_sounds_none));
        } else if (zenMode == 3) {
            preference.setEnabled(false);
            preference.setSummary(this.mContext.getString(R$string.zen_mode_behavior_alarms_only));
        } else {
            preference.setEnabled(true);
            preference.setSummary(this.mSummaryBuilder.getOtherSoundCategoriesSummary(getPolicy()));
        }
    }
}
