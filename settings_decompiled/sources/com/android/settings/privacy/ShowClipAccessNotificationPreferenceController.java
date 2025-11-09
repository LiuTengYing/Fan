package com.android.settings.privacy;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.DeviceConfig;
import android.provider.Settings;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
/* loaded from: classes.dex */
public class ShowClipAccessNotificationPreferenceController extends TogglePreferenceController implements LifecycleObserver {
    private static final String KEY_SHOW_CLIP_ACCESS_NOTIFICATION = "show_clip_access_notification";
    private boolean mDefault;
    private final DeviceConfig.OnPropertiesChangedListener mDeviceConfigListener;
    private Preference mPreference;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DeviceConfig.Properties properties) {
        updateConfig();
    }

    public ShowClipAccessNotificationPreferenceController(Context context) {
        super(context, KEY_SHOW_CLIP_ACCESS_NOTIFICATION);
        this.mDeviceConfigListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.settings.privacy.ShowClipAccessNotificationPreferenceController$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                ShowClipAccessNotificationPreferenceController.this.lambda$new$0(properties);
            }
        };
        updateConfig();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "clipboard_show_access_notifications", this.mDefault ? 1 : 0) != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "clipboard_show_access_notifications", z ? 1 : 0);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_privacy;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        DeviceConfig.addOnPropertiesChangedListener("clipboard", this.mContext.getMainExecutor(), this.mDeviceConfigListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        DeviceConfig.removeOnPropertiesChangedListener(this.mDeviceConfigListener);
    }

    private void updateConfig() {
        this.mDefault = DeviceConfig.getBoolean("clipboard", "show_access_notifications", true);
        updateState(this.mPreference);
    }
}
