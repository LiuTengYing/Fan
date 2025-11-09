package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.gestures.OneHandedSettingsUtils;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class OneHandedActionPullDownPrefController extends BasePreferenceController implements OneHandedSettingsUtils.TogglesCallback, LifecycleObserver, OnStart, OnStop {
    private Preference mPreference;
    private final OneHandedSettingsUtils mUtils;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OneHandedActionPullDownPrefController(Context context, String str) {
        super(context, str);
        this.mUtils = new OneHandedSettingsUtils(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference instanceof SelectorWithWidgetPreference) {
            ((SelectorWithWidgetPreference) preference).setChecked(!OneHandedSettingsUtils.isSwipeDownNotificationEnabled(this.mContext));
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (OneHandedSettingsUtils.isSupportOneHandedMode() && OneHandedSettingsUtils.canEnableController(this.mContext)) ? 0 : 5;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            OneHandedSettingsUtils.setSwipeDownNotificationEnabled(this.mContext, false);
            if (preference instanceof SelectorWithWidgetPreference) {
                ((SelectorWithWidgetPreference) preference).setChecked(true);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
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
        if (this.mPreference == null) {
            return;
        }
        if (uri.equals(OneHandedSettingsUtils.ONE_HANDED_MODE_ENABLED_URI) || uri.equals(OneHandedSettingsUtils.SOFTWARE_SHORTCUT_ENABLED_URI) || uri.equals(OneHandedSettingsUtils.HARDWARE_SHORTCUT_ENABLED_URI)) {
            this.mPreference.setEnabled(OneHandedSettingsUtils.canEnableController(this.mContext));
        } else if (uri.equals(OneHandedSettingsUtils.SHOW_NOTIFICATION_ENABLED_URI)) {
            updateState(this.mPreference);
        }
    }
}
