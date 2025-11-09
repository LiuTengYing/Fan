package com.android.settings.enterprise;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import java.util.Objects;
/* loaded from: classes.dex */
public class FinancedPrivacyPreferenceController extends BasePreferenceController implements PreferenceControllerMixin {
    private final PrivacyPreferenceControllerHelper mPrivacyPreferenceControllerHelper;

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

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public FinancedPrivacyPreferenceController(Context context, String str) {
        this(context, new PrivacyPreferenceControllerHelper(context), str);
        Objects.requireNonNull(context);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @VisibleForTesting
    FinancedPrivacyPreferenceController(Context context, PrivacyPreferenceControllerHelper privacyPreferenceControllerHelper, String str) {
        super(context, str);
        Objects.requireNonNull(context);
        Objects.requireNonNull(privacyPreferenceControllerHelper);
        this.mPrivacyPreferenceControllerHelper = privacyPreferenceControllerHelper;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPrivacyPreferenceControllerHelper.updateState(preference);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mPrivacyPreferenceControllerHelper.isFinancedDevice() ? 0 : 2;
    }
}
