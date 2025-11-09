package com.android.settings.accounts;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import java.util.Set;
/* loaded from: classes.dex */
public class CrossProfileCalendarPreferenceController extends TogglePreferenceController {
    private static final String TAG = "CrossProfileCalendarPreferenceController";
    private UserHandle mManagedUser;

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
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CrossProfileCalendarPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void setManagedUser(UserHandle userHandle) {
        this.mManagedUser = userHandle;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        UserHandle userHandle = this.mManagedUser;
        return (userHandle == null || isCrossProfileCalendarDisallowedByAdmin(this.mContext, userHandle.getIdentifier())) ? 4 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mManagedUser != null && Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "cross_profile_calendar_enabled", 0, this.mManagedUser.getIdentifier()) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (this.mManagedUser == null) {
            return false;
        }
        return Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "cross_profile_calendar_enabled", z ? 1 : 0, this.mManagedUser.getIdentifier());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accounts;
    }

    static boolean isCrossProfileCalendarDisallowedByAdmin(Context context, int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) createPackageContextAsUser(context, i).getSystemService(DevicePolicyManager.class);
        if (devicePolicyManager == null) {
            return true;
        }
        Set crossProfileCalendarPackages = devicePolicyManager.getCrossProfileCalendarPackages();
        return crossProfileCalendarPackages != null && crossProfileCalendarPackages.isEmpty();
    }

    private static Context createPackageContextAsUser(Context context, int i) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.of(i));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to create user context", e);
            return null;
        }
    }
}
