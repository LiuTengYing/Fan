package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.os.UserManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class ColorTemperaturePreferenceController extends BasePreferenceController {
    private static final String KEY_COLORS_CONTRAST = "colors_contrast";
    private static final String TAG = "ColorTemperaturePreferenceController";
    private final boolean mIsSupportPQ;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_COLORS_CONTRAST;
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

    public ColorTemperaturePreferenceController(Context context, String str) {
        super(context, str);
        this.mIsSupportPQ = SystemProperties.getBoolean("ro.vendor.displayenhance", false);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mIsSupportPQ && this.mContext.getResources().getBoolean(R$bool.config_support_colorTemperatureAdjusting)) {
            Log.i(TAG, "ColorTemperatureAdjusting is support");
            return 0;
        }
        return 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        final String name = ColorTemperatureActivity.class.getName();
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.display.ColorTemperaturePreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$updateState$0;
                lambda$updateState$0 = ColorTemperaturePreferenceController.lambda$updateState$0(name, preference2);
                return lambda$updateState$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateState$0(String str, Preference preference) {
        Context context = preference.getContext();
        UserManager.get(context);
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", str);
        context.startActivity(intent);
        return true;
    }
}
