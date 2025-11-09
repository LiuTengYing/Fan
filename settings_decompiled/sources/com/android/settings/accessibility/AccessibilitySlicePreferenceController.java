package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.accessibility.AccessibilityUtils;
/* loaded from: classes.dex */
public class AccessibilitySlicePreferenceController extends TogglePreferenceController {
    private static final String EMPTY_STRING = "";
    private final ComponentName mComponentName;

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
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AccessibilitySlicePreferenceController(Context context, String str) {
        super(context, str);
        ComponentName unflattenFromString = ComponentName.unflattenFromString(getPreferenceKey());
        this.mComponentName = unflattenFromString;
        if (unflattenFromString != null) {
            return;
        }
        throw new IllegalArgumentException("Illegal Component Name from: " + str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        return accessibilityServiceInfo == null ? EMPTY_STRING : AccessibilitySettings.getServiceSummary(this.mContext, accessibilityServiceInfo, isChecked());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "accessibility_enabled", 0) == 1) {
            return AccessibilityUtils.getEnabledServicesFromSettings(this.mContext).contains(this.mComponentName);
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (getAccessibilityServiceInfo() == null) {
            return false;
        }
        AccessibilityUtils.setAccessibilityServiceState(this.mContext, this.mComponentName, z);
        return z == isChecked();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return getAccessibilityServiceInfo() == null ? 3 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }

    private AccessibilityServiceInfo getAccessibilityServiceInfo() {
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            if (this.mComponentName.equals(accessibilityServiceInfo.getComponentName())) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }
}
