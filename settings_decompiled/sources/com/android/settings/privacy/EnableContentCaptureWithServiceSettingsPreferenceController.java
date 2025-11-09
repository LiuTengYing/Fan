package com.android.settings.privacy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.dashboard.profileselector.ProfileSelectDialog;
import com.android.settings.dashboard.profileselector.UserAdapter;
import com.android.settings.utils.ContentCaptureUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class EnableContentCaptureWithServiceSettingsPreferenceController extends TogglePreferenceController {
    private static final String TAG = "ContentCaptureController";

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

    public EnableContentCaptureWithServiceSettingsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return ContentCaptureUtils.isEnabledForUser(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ContentCaptureUtils.setEnabledForUser(this.mContext, z);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ComponentName serviceSettingsComponentName = ContentCaptureUtils.getServiceSettingsComponentName();
        if (serviceSettingsComponentName != null) {
            preference.setIntent(new Intent("android.intent.action.MAIN").setComponent(serviceSettingsComponentName));
            return;
        }
        Log.w(TAG, "No component name for custom service settings");
        preference.setSelectable(false);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ContentCaptureUtils.isFeatureAvailable() && ContentCaptureUtils.getServiceSettingsComponentName() != null ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_privacy;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            show(preference);
            return true;
        }
        return false;
    }

    private void show(Preference preference) {
        List<UserInfo> users = UserManager.get(this.mContext).getUsers();
        final ArrayList arrayList = new ArrayList(users.size());
        for (UserInfo userInfo : users) {
            arrayList.add(userInfo.getUserHandle());
        }
        final Intent addFlags = preference.getIntent().addFlags(32768);
        if (arrayList.size() == 1) {
            this.mContext.startActivityAsUser(addFlags, (UserHandle) arrayList.get(0));
        } else {
            ProfileSelectDialog.createDialog(this.mContext, arrayList, new UserAdapter.OnClickListener() { // from class: com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController$$ExternalSyntheticLambda0
                @Override // com.android.settings.dashboard.profileselector.UserAdapter.OnClickListener
                public final void onClick(int i) {
                    EnableContentCaptureWithServiceSettingsPreferenceController.this.lambda$show$0(addFlags, arrayList, i);
                }
            }).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$0(Intent intent, ArrayList arrayList, int i) {
        this.mContext.startActivityAsUser(intent, (UserHandle) arrayList.get(i));
    }
}
