package com.android.settings.accounts;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class WorkModePreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "WorkModeController";
    private DevicePolicyManager mDevicePolicyManager;
    private IntentFilter mIntentFilter;
    private UserHandle mManagedUser;
    private Preference mPreference;
    final BroadcastReceiver mReceiver;
    private UserManager mUserManager;

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

    @Override // com.android.settings.core.BasePreferenceController
    public int getSliceType() {
        return 1;
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

    public WorkModePreferenceController(Context context, String str) {
        super(context, str);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.accounts.WorkModePreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent == null) {
                    return;
                }
                String action = intent.getAction();
                Log.v(WorkModePreferenceController.TAG, "Received broadcast: " + action);
                if ("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action)) {
                    if (intent.getIntExtra("android.intent.extra.user_handle", -10000) == WorkModePreferenceController.this.mManagedUser.getIdentifier()) {
                        WorkModePreferenceController workModePreferenceController = WorkModePreferenceController.this;
                        workModePreferenceController.updateState(workModePreferenceController.mPreference);
                        return;
                    }
                    return;
                }
                Log.w(WorkModePreferenceController.TAG, "Cannot handle received broadcast: " + intent.getAction());
            }
        };
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        this.mIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
    }

    public void setManagedUser(UserHandle userHandle) {
        this.mManagedUser = userHandle;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter, 2);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mManagedUser != null ? 0 : 4;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (isChecked()) {
            return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_SETTING_ON_SUMMARY", new Supplier() { // from class: com.android.settings.accounts.WorkModePreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getSummary$0;
                    lambda$getSummary$0 = WorkModePreferenceController.this.lambda$getSummary$0();
                    return lambda$getSummary$0;
                }
            });
        }
        return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_SETTING_OFF_SUMMARY", new Supplier() { // from class: com.android.settings.accounts.WorkModePreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getSummary$1;
                lambda$getSummary$1 = WorkModePreferenceController.this.lambda$getSummary$1();
                return lambda$getSummary$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getSummary$0() {
        return this.mContext.getString(R$string.work_mode_on_summary);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getSummary$1() {
        return this.mContext.getString(R$string.work_mode_off_summary);
    }

    private boolean isChecked() {
        UserHandle userHandle;
        UserManager userManager = this.mUserManager;
        if (userManager == null || (userHandle = this.mManagedUser) == null) {
            return false;
        }
        return !userManager.isQuietModeEnabled(userHandle);
    }

    private boolean setChecked(boolean z) {
        UserHandle userHandle;
        UserManager userManager = this.mUserManager;
        if (userManager != null && (userHandle = this.mManagedUser) != null) {
            userManager.requestQuietModeEnabled(!z, userHandle);
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference instanceof TwoStatePreference) {
            ((TwoStatePreference) preference).setChecked(isChecked());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return setChecked(((Boolean) obj).booleanValue());
    }
}
