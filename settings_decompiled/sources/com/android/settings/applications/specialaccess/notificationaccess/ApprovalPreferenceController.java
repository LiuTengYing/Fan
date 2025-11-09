package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class ApprovalPreferenceController extends BasePreferenceController {
    private static final String TAG = "ApprovalPrefController";
    private ComponentName mCn;
    private NotificationManager mNm;
    private PreferenceFragmentCompat mParent;
    private PackageInfo mPkgInfo;
    private PackageManager mPm;

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

    public ApprovalPreferenceController(Context context, String str) {
        super(context, str);
    }

    public ApprovalPreferenceController setPkgInfo(PackageInfo packageInfo) {
        this.mPkgInfo = packageInfo;
        return this;
    }

    public ApprovalPreferenceController setCn(ComponentName componentName) {
        this.mCn = componentName;
        return this;
    }

    public ApprovalPreferenceController setParent(PreferenceFragmentCompat preferenceFragmentCompat) {
        this.mParent = preferenceFragmentCompat;
        return this;
    }

    public ApprovalPreferenceController setNm(NotificationManager notificationManager) {
        this.mNm = notificationManager;
        return this;
    }

    public ApprovalPreferenceController setPm(PackageManager packageManager) {
        this.mPm = packageManager;
        return this;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
        final CharSequence loadLabel = this.mPkgInfo.applicationInfo.loadLabel(this.mPm);
        boolean isServiceEnabled = isServiceEnabled(this.mCn);
        restrictedSwitchPreference.setChecked(isServiceEnabled);
        restrictedSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.ApprovalPreferenceController$$ExternalSyntheticLambda1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference2, Object obj) {
                boolean lambda$updateState$0;
                lambda$updateState$0 = ApprovalPreferenceController.this.lambda$updateState$0(loadLabel, preference2, obj);
                return lambda$updateState$0;
            }
        });
        restrictedSwitchPreference.updateState(this.mCn.getPackageName(), this.mPkgInfo.applicationInfo.uid, isServiceEnabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateState$0(CharSequence charSequence, Preference preference, Object obj) {
        if (!((Boolean) obj).booleanValue()) {
            if (isServiceEnabled(this.mCn)) {
                new FriendlyWarningDialogFragment().setServiceInfo(this.mCn, charSequence, this.mParent).show(this.mParent.getFragmentManager(), "friendlydialog");
                return false;
            }
            return true;
        } else if (isServiceEnabled(this.mCn)) {
            return true;
        } else {
            new ScaryWarningDialogFragment().setServiceInfo(this.mCn, charSequence, this.mParent).show(this.mParent.getFragmentManager(), "dialog");
            return false;
        }
    }

    public void disable(final ComponentName componentName) {
        logSpecialPermissionChange(true, componentName.getPackageName());
        this.mNm.setNotificationListenerAccessGranted(componentName, false);
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.applications.specialaccess.notificationaccess.ApprovalPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ApprovalPreferenceController.this.lambda$disable$1(componentName);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disable$1(ComponentName componentName) {
        if (this.mNm.isNotificationPolicyAccessGrantedForPackage(componentName.getPackageName())) {
            return;
        }
        this.mNm.removeAutomaticZenRules(componentName.getPackageName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void enable(ComponentName componentName) {
        logSpecialPermissionChange(true, componentName.getPackageName());
        this.mNm.setNotificationListenerAccessGranted(componentName, true);
    }

    protected boolean isServiceEnabled(ComponentName componentName) {
        return this.mNm.isNotificationListenerAccessGranted(componentName);
    }

    void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, z ? 776 : 777, str);
    }
}
