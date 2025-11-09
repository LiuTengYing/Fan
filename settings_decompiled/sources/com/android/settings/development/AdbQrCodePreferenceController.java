package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.debug.IAdbManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class AdbQrCodePreferenceController extends BasePreferenceController {
    private static final String TAG = "AdbQrCodePrefCtrl";
    private IAdbManager mAdbManager;
    private Fragment mParentFragment;

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

    public AdbQrCodePreferenceController(Context context, String str) {
        super(context, str);
        this.mAdbManager = IAdbManager.Stub.asInterface(ServiceManager.getService("adb"));
    }

    public void setParentFragment(Fragment fragment) {
        this.mParentFragment = fragment;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        try {
            return this.mAdbManager.isAdbWifiQrSupported() ? 0 : 3;
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to check if adb wifi QR code scanning is supported.", e);
            return 3;
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            this.mParentFragment.startActivityForResult(new Intent(this.mContext, AdbQrCodeActivity.class), 1);
            return true;
        }
        return false;
    }
}
