package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
/* loaded from: classes.dex */
public abstract class AccessibilityQuickSettingsPrimarySwitchPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnCreate, OnSaveInstanceState {
    private static final String KEY_SAVED_QS_TOOLTIP_RESHOW = "qs_tooltip_reshow";
    private final Handler mHandler;
    private boolean mNeedsQSTooltipReshow;
    private PrimarySwitchPreference mPreference;
    private AccessibilityQuickSettingsTooltipWindow mTooltipWindow;

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

    abstract ComponentName getTileComponentName();

    abstract CharSequence getTileTooltipContent();

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AccessibilityQuickSettingsPrimarySwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mNeedsQSTooltipReshow = false;
        this.mHandler = new Handler(context.getMainLooper());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(KEY_SAVED_QS_TOOLTIP_RESHOW)) {
            return;
        }
        this.mNeedsQSTooltipReshow = bundle.getBoolean(KEY_SAVED_QS_TOOLTIP_RESHOW);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        AccessibilityQuickSettingsTooltipWindow accessibilityQuickSettingsTooltipWindow = this.mTooltipWindow;
        if (accessibilityQuickSettingsTooltipWindow != null) {
            bundle.putBoolean(KEY_SAVED_QS_TOOLTIP_RESHOW, accessibilityQuickSettingsTooltipWindow.isShowing());
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (PrimarySwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (this.mNeedsQSTooltipReshow) {
            this.mHandler.post(new Runnable() { // from class: com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityQuickSettingsPrimarySwitchPreferenceController.this.showQuickSettingsTooltipIfNeeded();
                }
            });
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            showQuickSettingsTooltipIfNeeded();
        }
        return z;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showQuickSettingsTooltipIfNeeded() {
        ComponentName tileComponentName = getTileComponentName();
        if (tileComponentName == null) {
            return;
        }
        if (this.mNeedsQSTooltipReshow || !AccessibilityQuickSettingUtils.hasValueInSharedPreferences(this.mContext, tileComponentName)) {
            AccessibilityQuickSettingsTooltipWindow accessibilityQuickSettingsTooltipWindow = new AccessibilityQuickSettingsTooltipWindow(this.mContext);
            this.mTooltipWindow = accessibilityQuickSettingsTooltipWindow;
            accessibilityQuickSettingsTooltipWindow.setup(getTileTooltipContent(), R$drawable.accessibility_auto_added_qs_tooltip_illustration);
            this.mTooltipWindow.showAtTopCenter(this.mPreference.getSwitch());
            AccessibilityQuickSettingUtils.optInValueToSharedPreferences(this.mContext, tileComponentName);
            this.mNeedsQSTooltipReshow = false;
        }
    }
}
