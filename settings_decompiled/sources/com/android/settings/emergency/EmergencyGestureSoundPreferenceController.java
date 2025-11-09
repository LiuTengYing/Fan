package com.android.settings.emergency;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.R$bool;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.emergencynumber.EmergencyNumberUtils;
/* loaded from: classes.dex */
public class EmergencyGestureSoundPreferenceController extends TogglePreferenceController {
    EmergencyNumberUtils mEmergencyNumberUtils;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EmergencyGestureSoundPreferenceController(Context context, String str) {
        super(context, str);
        this.mEmergencyNumberUtils = new EmergencyNumberUtils(context);
    }

    private static boolean isGestureAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_show_emergency_gesture_settings);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isGestureAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mEmergencyNumberUtils.getEmergencyGestureSoundEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mEmergencyNumberUtils.setEmergencySoundEnabled(z);
        return true;
    }
}
