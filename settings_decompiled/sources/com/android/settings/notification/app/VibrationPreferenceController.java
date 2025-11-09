package com.android.settings.notification.app;

import android.content.Context;
import android.os.Vibrator;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class VibrationPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final Vibrator mVibrator;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "vibrate";
    }

    public VibrationPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mVibrator = (Vibrator) context.getSystemService(Vibrator.class);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        Vibrator vibrator;
        return super.isAvailable() && this.mChannel != null && checkCanBeVisible(3) && !isDefaultChannel() && (vibrator = this.mVibrator) != null && vibrator.hasVibrator();
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("vibration");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mChannel != null) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            restrictedSwitchPreference.setEnabled(!restrictedSwitchPreference.isDisabledByAdmin());
            restrictedSwitchPreference.setChecked(this.mChannel.shouldVibrate());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel != null) {
            this.mChannel.enableVibration(((Boolean) obj).booleanValue());
            saveChannel();
            return true;
        }
        return true;
    }
}
