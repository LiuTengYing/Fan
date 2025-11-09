package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.ColorDisplayManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.R$string;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class ReduceBrightColorsPreferenceController extends AccessibilityQuickSettingsPrimarySwitchPreferenceController implements OnStart, OnStop {
    private final ColorDisplayManager mColorDisplayManager;
    private final Context mContext;
    private PrimarySwitchPreference mPreference;
    private ContentObserver mSettingsContentObserver;

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ReduceBrightColorsPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mSettingsContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.accessibility.ReduceBrightColorsPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                if (TextUtils.equals(uri == null ? null : uri.getLastPathSegment(), "reduce_bright_colors_activated")) {
                    ReduceBrightColorsPreferenceController reduceBrightColorsPreferenceController = ReduceBrightColorsPreferenceController.this;
                    reduceBrightColorsPreferenceController.updateState(reduceBrightColorsPreferenceController.mPreference);
                }
            }
        };
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mColorDisplayManager.isReduceBrightColorsActivated();
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        super.setChecked(z);
        return this.mColorDisplayManager.setReduceBrightColorsActivated(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getText(R$string.reduce_bright_colors_preference_summary);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(preference);
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ColorDisplayManager.isReduceBrightColorsAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (PrimarySwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("reduce_bright_colors_activated"), false, this.mSettingsContentObserver, -2);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsContentObserver);
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController
    protected ComponentName getTileComponentName() {
        return AccessibilityShortcutController.REDUCE_BRIGHT_COLORS_TILE_SERVICE_COMPONENT_NAME;
    }

    @Override // com.android.settings.accessibility.AccessibilityQuickSettingsPrimarySwitchPreferenceController
    CharSequence getTileTooltipContent() {
        return this.mContext.getText(R$string.accessibility_reduce_bright_colors_auto_added_qs_tooltip_content);
    }
}
