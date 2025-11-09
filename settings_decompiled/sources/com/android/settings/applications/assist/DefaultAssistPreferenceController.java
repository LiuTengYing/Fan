package com.android.settings.applications.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.service.voice.VoiceInteractionServiceInfo;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.internal.app.AssistUtils;
import com.android.settings.R$bool;
import com.android.settings.applications.defaultapps.DefaultAppPreferenceController;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.List;
/* loaded from: classes.dex */
public class DefaultAssistPreferenceController extends DefaultAppPreferenceController {
    private final AssistUtils mAssistUtils;
    private final Intent mIntent;
    private final String mPrefKey;
    private final boolean mShowSetting;

    public DefaultAssistPreferenceController(Context context, String str, boolean z) {
        super(context);
        this.mPrefKey = str;
        this.mShowSetting = z;
        this.mAssistUtils = new AssistUtils(context);
        String permissionControllerPackageName = this.mPackageManager.getPermissionControllerPackageName();
        if (permissionControllerPackageName != null) {
            this.mIntent = new Intent("android.intent.action.MANAGE_DEFAULT_APP").setPackage(permissionControllerPackageName).putExtra("android.intent.extra.ROLE_NAME", "android.app.role.ASSISTANT");
        } else {
            this.mIntent = null;
        }
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        ComponentName assistComponentForUser;
        String assistSettingsActivity;
        if (this.mShowSetting && (assistComponentForUser = this.mAssistUtils.getAssistComponentForUser(this.mUserId)) != null) {
            List<ResolveInfo> queryIntentServices = this.mPackageManager.queryIntentServices(new Intent("android.service.voice.VoiceInteractionService").setPackage(assistComponentForUser.getPackageName()), 128);
            if (queryIntentServices == null || queryIntentServices.isEmpty() || (assistSettingsActivity = getAssistSettingsActivity(assistComponentForUser, queryIntentServices.get(0), this.mPackageManager)) == null) {
                return null;
            }
            return new Intent("android.intent.action.MAIN").setComponent(new ComponentName(assistComponentForUser.getPackageName(), assistSettingsActivity));
        }
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "default_assist")) {
            Intent intent = this.mIntent;
            if (intent != null) {
                this.mContext.startActivity(intent);
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R$bool.config_show_assist_and_voice_input);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPrefKey;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        ComponentName assistComponentForUser = this.mAssistUtils.getAssistComponentForUser(this.mUserId);
        if (assistComponentForUser == null) {
            return null;
        }
        return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, assistComponentForUser);
    }

    String getAssistSettingsActivity(ComponentName componentName, ResolveInfo resolveInfo, PackageManager packageManager) {
        VoiceInteractionServiceInfo voiceInteractionServiceInfo = new VoiceInteractionServiceInfo(packageManager, resolveInfo.serviceInfo);
        if (voiceInteractionServiceInfo.getSupportsAssist()) {
            return voiceInteractionServiceInfo.getSettingsActivity();
        }
        return null;
    }
}
