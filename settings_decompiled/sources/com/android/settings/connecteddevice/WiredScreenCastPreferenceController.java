package com.android.settings.connecteddevice;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class WiredScreenCastPreferenceController extends BasePreferenceController {
    private boolean ismutiScreen;
    private boolean mIsSupportWiredProjection;
    private Preference mPreference;

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

    public WiredScreenCastPreferenceController(Context context, String str) {
        super(context, str);
        this.mIsSupportWiredProjection = SystemProperties.getBoolean("ro.vendor.display.multidisplay", false);
        this.ismutiScreen = SystemProperties.getBoolean("ro.fyt.dual_mipi", false);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        updatePreferenceVisible(preference);
    }

    public void updatePreferenceVisible(Preference preference) {
        if (preference != null) {
            preference.setVisible(isAvailable());
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mIsSupportWiredProjection && !this.ismutiScreen && this.mContext.getResources().getBoolean(R$bool.config_multi_screen_cast)) ? 0 : 3;
    }
}
