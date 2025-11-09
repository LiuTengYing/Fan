package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class PreventRingingParentPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    static final int KEY_CHORD_POWER_VOLUME_UP_MUTE_TOGGLE = 1;
    final String SECURE_KEY;
    private PrimarySwitchPreference mPreference;
    private SettingObserver mSettingObserver;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PreventRingingParentPreferenceController(Context context, String str) {
        super(context, str);
        this.SECURE_KEY = "volume_hush_gesture";
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (PrimarySwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSettingObserver = new SettingObserver(this.mPreference);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return isVolumePowerKeyChordSetToHush() && Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1) != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1);
        int i2 = i != 0 ? i : 1;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (!z) {
            i2 = 0;
        }
        return Settings.Secure.putInt(contentResolver, "volume_hush_gesture", i2);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        CharSequence text;
        super.updateState(preference);
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1);
        if (isVolumePowerKeyChordSetToHush()) {
            if (i == 1) {
                text = this.mContext.getText(R$string.prevent_ringing_option_vibrate_summary);
            } else if (i == 2) {
                text = this.mContext.getText(R$string.prevent_ringing_option_mute_summary);
            } else {
                text = this.mContext.getText(R$string.switch_off_text);
            }
            preference.setEnabled(true);
            this.mPreference.setSwitchEnabled(true);
        } else {
            text = this.mContext.getText(R$string.prevent_ringing_option_unavailable_lpp_summary);
            preference.setEnabled(false);
            this.mPreference.setSwitchEnabled(false);
        }
        preference.setSummary(text);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mContext.getResources().getBoolean(17891828)) {
            if (isVolumePowerKeyChordSetToHush()) {
                return 0;
            }
            return this.mContext.getResources().getBoolean(17891698) ? 5 : 3;
        }
        return 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_sound;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver());
            this.mSettingObserver.onChange(false, null);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.unregister(this.mContext.getContentResolver());
        }
    }

    private boolean isVolumePowerKeyChordSetToHush() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "key_chord_power_volume_up", this.mContext.getResources().getInteger(17694842)) == 1;
    }

    /* loaded from: classes.dex */
    private class SettingObserver extends ContentObserver {
        private final Uri mKeyChordVolumePowerUpUri;
        private final Preference mPreference;
        private final Uri mVolumeHushGestureUri;

        SettingObserver(Preference preference) {
            super(new Handler());
            this.mVolumeHushGestureUri = Settings.Secure.getUriFor("volume_hush_gesture");
            this.mKeyChordVolumePowerUpUri = Settings.Global.getUriFor("key_chord_power_volume_up");
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(this.mKeyChordVolumePowerUpUri, false, this);
            contentResolver.registerContentObserver(this.mVolumeHushGestureUri, false, this);
        }

        public void unregister(ContentResolver contentResolver) {
            contentResolver.unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (uri == null || this.mVolumeHushGestureUri.equals(uri) || this.mKeyChordVolumePowerUpUri.equals(uri)) {
                PreventRingingParentPreferenceController.this.updateState(this.mPreference);
            }
        }
    }
}
