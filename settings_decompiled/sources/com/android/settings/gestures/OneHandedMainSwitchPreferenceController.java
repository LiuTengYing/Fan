package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.gestures.OneHandedSettingsUtils;
import com.android.settings.widget.SettingsMainSwitchPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.MainSwitchPreference;
/* loaded from: classes.dex */
public class OneHandedMainSwitchPreferenceController extends SettingsMainSwitchPreferenceController implements OneHandedSettingsUtils.TogglesCallback, LifecycleObserver, OnStart, OnStop {
    private MainSwitchPreference mPreference;
    private final OneHandedSettingsUtils mUtils;

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OneHandedMainSwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mUtils = new OneHandedSettingsUtils(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!OneHandedSettingsUtils.isSupportOneHandedMode() || OneHandedSettingsUtils.getNavigationBarMode(this.mContext) == 0) ? 5 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return OneHandedSettingsUtils.isOneHandedModeEnabled(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            OneHandedSettingsUtils.setTapsAppToExitEnabled(this.mContext, true);
            OneHandedSettingsUtils.setTimeoutValue(this.mContext, OneHandedSettingsUtils.OneHandedTimeout.MEDIUM.getValue());
        }
        OneHandedSettingsUtils.setOneHandedModeEnabled(this.mContext, z);
        return true;
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mUtils.registerToggleAwareObserver(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mUtils.unregisterToggleAwareObserver();
    }

    @Override // com.android.settings.gestures.OneHandedSettingsUtils.TogglesCallback
    public void onChange(Uri uri) {
        if (this.mPreference != null && uri.equals(OneHandedSettingsUtils.ONE_HANDED_MODE_ENABLED_URI)) {
            this.mPreference.setChecked(isChecked());
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_system;
    }
}
