package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.TwoTargetPreference;
/* loaded from: classes.dex */
public abstract class DefaultAppPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final PackageManager mPackageManager;
    protected int mUserId;
    protected final UserManager mUserManager;

    protected abstract DefaultAppInfo getDefaultAppInfo();

    protected Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        return null;
    }

    protected boolean showLabelAsTitle() {
        return false;
    }

    public DefaultAppPreferenceController(Context context) {
        super(context);
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mUserId = UserHandle.myUserId();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
        CharSequence defaultAppLabel = getDefaultAppLabel();
        if (preference instanceof TwoTargetPreference) {
            ((TwoTargetPreference) preference).setIconSize(1);
        }
        if (!TextUtils.isEmpty(defaultAppLabel)) {
            if (showLabelAsTitle()) {
                preference.setTitle(defaultAppLabel);
            } else {
                preference.setSummary(defaultAppLabel);
            }
            preference.setIcon(Utils.getSafeIcon(getDefaultAppIcon()));
        } else {
            Log.d("DefaultAppPrefControl", "No default app");
            if (showLabelAsTitle()) {
                preference.setTitle(R$string.app_list_preference_none);
            } else {
                preference.setSummary(R$string.app_list_preference_none);
            }
            preference.setIcon((Drawable) null);
        }
        mayUpdateGearIcon(defaultAppInfo, preference);
    }

    private void mayUpdateGearIcon(DefaultAppInfo defaultAppInfo, Preference preference) {
        if (preference instanceof GearPreference) {
            final Intent settingIntent = getSettingIntent(defaultAppInfo);
            if (settingIntent != null) {
                ((GearPreference) preference).setOnGearClickListener(new GearPreference.OnGearClickListener() { // from class: com.android.settings.applications.defaultapps.DefaultAppPreferenceController$$ExternalSyntheticLambda0
                    @Override // com.android.settings.widget.GearPreference.OnGearClickListener
                    public final void onGearClick(GearPreference gearPreference) {
                        DefaultAppPreferenceController.this.lambda$mayUpdateGearIcon$0(settingIntent, gearPreference);
                    }
                });
            } else {
                ((GearPreference) preference).setOnGearClickListener(null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$mayUpdateGearIcon$0(Intent intent, GearPreference gearPreference) {
        startActivity(intent);
    }

    protected void startActivity(Intent intent) {
        this.mContext.startActivity(intent);
    }

    public Drawable getDefaultAppIcon() {
        DefaultAppInfo defaultAppInfo;
        if (isAvailable() && (defaultAppInfo = getDefaultAppInfo()) != null) {
            return defaultAppInfo.loadIcon();
        }
        return null;
    }

    public CharSequence getDefaultAppLabel() {
        DefaultAppInfo defaultAppInfo;
        if (isAvailable() && (defaultAppInfo = getDefaultAppInfo()) != null) {
            return defaultAppInfo.loadLabel();
        }
        return null;
    }
}
