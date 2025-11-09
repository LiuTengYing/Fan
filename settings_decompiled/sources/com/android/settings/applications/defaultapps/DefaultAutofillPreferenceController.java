package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.autofill.AutofillManager;
import com.android.settings.applications.defaultapps.DefaultAutofillPicker;
import com.android.settingslib.applications.DefaultAppInfo;
/* loaded from: classes.dex */
public class DefaultAutofillPreferenceController extends DefaultAppPreferenceController {
    private final AutofillManager mAutofillManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_autofill_main";
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected boolean showLabelAsTitle() {
        return true;
    }

    public DefaultAutofillPreferenceController(Context context) {
        super(context);
        this.mAutofillManager = (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        AutofillManager autofillManager = this.mAutofillManager;
        return autofillManager != null && autofillManager.hasAutofillFeature() && this.mAutofillManager.isAutofillSupported();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        if (defaultAppInfo == null) {
            return null;
        }
        return new DefaultAutofillPicker.AutofillSettingIntentProvider(this.mContext, this.mUserId, defaultAppInfo.getKey()).getIntent();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "autofill_service");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, ComponentName.unflattenFromString(string));
    }
}
