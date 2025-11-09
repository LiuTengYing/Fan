package com.android.settings.deviceinfo.syudevice;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class SnCodeController extends BasePreferenceController {
    private static String TAG = "SnCodeController";
    private String KEY;
    private Activity mActivity;
    private Fragment mFragment;

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

    public SnCodeController(Fragment fragment, Context context, String str) {
        super(context, str);
        this.KEY = str;
        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
    }

    public void showDialog(Fragment fragment, String str) {
        SnCodeFragment.show(fragment, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getQrCodeStr();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.KEY.contains(preference.getKey())) {
            showDialog(this.mFragment, this.mActivity.getResources().getString(R$string.quick_response_code));
            return true;
        }
        return false;
    }

    private String getQrCodeStr() {
        return SystemProperties.get("serialno");
    }
}
