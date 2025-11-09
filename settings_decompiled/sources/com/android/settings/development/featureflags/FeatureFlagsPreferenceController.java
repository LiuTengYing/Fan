package com.android.settings.development.featureflags;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.FeatureFlagUtils;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class FeatureFlagsPreferenceController extends BasePreferenceController {
    private PreferenceGroup mGroup;

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

    public FeatureFlagsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return Build.IS_DEBUGGABLE ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        Map allFeatureFlags = FeatureFlagUtils.getAllFeatureFlags();
        if (allFeatureFlags == null) {
            return;
        }
        this.mGroup.removeAll();
        final Context context = this.mGroup.getContext();
        allFeatureFlags.keySet().stream().sorted().forEach(new Consumer() { // from class: com.android.settings.development.featureflags.FeatureFlagsPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                FeatureFlagsPreferenceController.this.lambda$displayPreference$0(context, (String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(Context context, String str) {
        this.mGroup.addPreference(new FeatureFlagPreference(context, str));
    }
}
