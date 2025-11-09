package com.android.settings.system;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemUpdateManager;
import android.os.UserManager;
import android.telephony.CarrierConfigManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
/* loaded from: classes.dex */
public class SystemUpdatePreferenceController extends BasePreferenceController {
    private static final String KEY_SYSTEM_UPDATE_SETTINGS = "system_update_settings";
    private static final String TAG = "SysUpdatePrefContr";
    private final UserManager mUm;
    private final SystemUpdateManager mUpdateManager;

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

    public SystemUpdatePreferenceController(Context context) {
        super(context, KEY_SYSTEM_UPDATE_SETTINGS);
        this.mUm = UserManager.get(context);
        this.mUpdateManager = (SystemUpdateManager) context.getSystemService("system_update");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mContext.getResources().getBoolean(R$bool.config_show_system_update_settings) && this.mUm.isAdminUser()) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            Utils.updatePreferenceToSpecificActivityOrRemove(this.mContext, preferenceScreen, getPreferenceKey(), 1);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        PersistableBundle config;
        if (TextUtils.equals(getPreferenceKey(), preference.getKey()) && (config = ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfig()) != null && config.getBoolean("ci_action_on_sys_update_bool")) {
            ciActionOnSysUpdate(config);
            return false;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        Context context = this.mContext;
        int i = R$string.android_version_summary;
        String string = context.getString(i, Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY);
        FutureTask futureTask = new FutureTask(new Callable() { // from class: com.android.settings.system.SystemUpdatePreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Bundle lambda$getSummary$0;
                lambda$getSummary$0 = SystemUpdatePreferenceController.this.lambda$getSummary$0();
                return lambda$getSummary$0;
            }
        });
        try {
            futureTask.run();
            Bundle bundle = (Bundle) futureTask.get();
            int i2 = bundle.getInt("status");
            if (i2 == 0) {
                Log.d(TAG, "Update statue unknown");
            } else if (i2 != 1) {
                return (i2 == 2 || i2 == 3 || i2 == 4 || i2 == 5) ? this.mContext.getText(R$string.android_version_pending_update_summary) : string;
            }
            String string2 = bundle.getString("title");
            return !TextUtils.isEmpty(string2) ? this.mContext.getString(i, string2) : string;
        } catch (InterruptedException | ExecutionException unused) {
            Log.w(TAG, "Error getting system update info.");
            return string;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Bundle lambda$getSummary$0() throws Exception {
        return this.mUpdateManager.retrieveSystemUpdateInfo();
    }

    private void ciActionOnSysUpdate(PersistableBundle persistableBundle) {
        String string = persistableBundle.getString("ci_action_on_sys_update_intent_string");
        if (TextUtils.isEmpty(string)) {
            return;
        }
        String string2 = persistableBundle.getString("ci_action_on_sys_update_extra_string");
        String string3 = persistableBundle.getString("ci_action_on_sys_update_extra_val_string");
        Intent intent = new Intent(string);
        if (!TextUtils.isEmpty(string2)) {
            intent.putExtra(string2, string3);
        }
        Log.d(TAG, "ciActionOnSysUpdate: broadcasting intent " + string + " with extra " + string2 + ", " + string3);
        intent.addFlags(16777216);
        this.mContext.getApplicationContext().sendBroadcast(intent);
    }
}
