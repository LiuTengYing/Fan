package com.android.settings.volume.controller;

import android.content.Context;
import android.content.IntentFilter;
import androidx.fragment.app.Fragment;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.volume.dialog.SoundBalanceSettingsDialogFragment;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class SoundBalanceSettingsController extends BasePreferenceController {
    private static final String KEY = "volbalance";
    private RestrictedPreference mPreference;
    private int[] values;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public SoundBalanceSettingsController(Context context, String str) {
        super(context, str);
        this.values = new int[48];
    }

    public void setSoundValues(int i, int i2) {
        this.values[i] = i2;
    }

    public void showfragment(Fragment fragment, String str) {
        SoundBalanceSettingsDialogFragment.show(fragment, str, this.values);
    }
}
