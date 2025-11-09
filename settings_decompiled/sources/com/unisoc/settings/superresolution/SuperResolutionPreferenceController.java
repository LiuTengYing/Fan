package com.unisoc.settings.superresolution;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import androidx.preference.Preference;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class SuperResolutionPreferenceController extends BasePreferenceController {
    private static final String KEY_SUPER_RESOLUTION = "super_resolution";
    private static final int SUPER_RESOLUTION_STATE_OFF = 0;
    private static final int SUPER_RESOLUTION_STATE_ON = 1;
    private static final String TAG = "SuperResolutionPreferenceController";
    private Preference mPreference;
    private List<String[]> mResolutionMode;
    private IWindowManager mWindowManager;

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

    public SuperResolutionPreferenceController(Context context, String str) {
        super(context, str);
        this.mResolutionMode = new ArrayList();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (isSupportSuperResolution() && this.mContext.getResources().getBoolean(R$bool.config_support_superResolution) && isAdminUser()) ? 0 : 3;
    }

    public boolean isSupportSuperResolution() {
        IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        this.mWindowManager = windowManagerService;
        try {
            List<String[]> resolutions = windowManagerService.getResolutions();
            this.mResolutionMode = resolutions;
            if (resolutions != null) {
                int size = resolutions.size();
                Log.d(TAG, "resolution mode list num = " + size);
                return size >= 2;
            }
            return false;
        } catch (RemoteException unused) {
            Log.d(TAG, "RemoteException : cannot get resolution mode");
            return false;
        }
    }

    public boolean isAdminUser() {
        return UserHandle.myUserId() == 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        Activity activity = (Activity) this.mContext;
        this.mPreference = preference;
        if (activity != null && preference != null) {
            Log.d(TAG, " activity isInMultiWindowMode : " + activity.isInMultiWindowMode());
            preference.setEnabled(activity.isInMultiWindowMode() ^ true);
        }
        final String name = SuperResolutionSettingsActivity.class.getName();
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.unisoc.settings.superresolution.SuperResolutionPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$updateState$0;
                lambda$updateState$0 = SuperResolutionPreferenceController.lambda$updateState$0(name, preference2);
                return lambda$updateState$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateState$0(String str, Preference preference) {
        Context context = preference.getContext();
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", str);
        context.startActivity(intent);
        return true;
    }

    public boolean isSuperResolutionStateOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "sprd.action.super_resolution_state", 0) == 1;
    }

    public void setSuperResolutionStateOff() {
        boolean putInt = Settings.Global.putInt(this.mContext.getContentResolver(), "sprd.action.super_resolution_state", 0);
        Log.d(TAG, "set ACTION_SUPER_RESOLUTION_STATE SUPER_RESOLUTION_STATE_OFF = " + putInt);
    }

    public void setPreferenceEnabled(boolean z) {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setEnabled(z);
        }
    }
}
