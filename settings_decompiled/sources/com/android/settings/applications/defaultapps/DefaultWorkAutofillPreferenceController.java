package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.Utils;
import com.android.settings.applications.defaultapps.DefaultAutofillPicker;
import com.android.settingslib.applications.DefaultAppInfo;
/* loaded from: classes.dex */
public class DefaultWorkAutofillPreferenceController extends DefaultAutofillPreferenceController {
    private final UserHandle mUserHandle;

    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_autofill_work";
    }

    public DefaultWorkAutofillPreferenceController(Context context) {
        super(context);
        this.mUserHandle = Utils.getManagedProfile(this.mUserManager);
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mUserHandle == null) {
            return false;
        }
        return super.isAvailable();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "autofill_service", this.mUserHandle.getIdentifier());
        if (TextUtils.isEmpty(stringForUser)) {
            return null;
        }
        return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserHandle.getIdentifier(), ComponentName.unflattenFromString(stringForUser));
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        if (defaultAppInfo == null) {
            return null;
        }
        return new DefaultAutofillPicker.AutofillSettingIntentProvider(this.mContext, this.mUserHandle.getIdentifier(), defaultAppInfo.getKey()).getIntent();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected void startActivity(Intent intent) {
        this.mContext.startActivityAsUser(intent, this.mUserHandle);
    }
}
