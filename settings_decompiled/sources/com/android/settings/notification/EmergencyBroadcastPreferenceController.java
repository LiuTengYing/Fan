package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.internal.telephony.CellBroadcastUtils;
import com.android.settings.accounts.AccountRestrictionHelper;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class EmergencyBroadcastPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private AccountRestrictionHelper mHelper;
    private Intent mIntent;
    private PackageManager mPm;
    private final String mPrefKey;
    private UserManager mUserManager;

    public EmergencyBroadcastPreferenceController(Context context, String str) {
        this(context, new AccountRestrictionHelper(context), str);
    }

    EmergencyBroadcastPreferenceController(Context context, AccountRestrictionHelper accountRestrictionHelper, String str) {
        super(context);
        this.mPrefKey = str;
        this.mHelper = accountRestrictionHelper;
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mPm = this.mContext.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            ((RestrictedPreference) preference).checkRestrictionAndSetDisabled("no_config_cell_broadcasts");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey()) || (intent = this.mIntent) == null) {
            return false;
        }
        this.mContext.startActivity(intent);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPrefKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mUserManager.isAdminUser() && isCellBroadcastAppLinkEnabled() && !this.mHelper.hasBaseUserRestriction("no_config_cell_broadcasts", UserHandle.myUserId());
    }

    private boolean isCellBroadcastAppLinkEnabled() {
        boolean z = this.mContext.getResources().getBoolean(17891577) && !this.mContext.getResources().getBoolean(17891600);
        if (z) {
            try {
                String defaultCellBroadcastReceiverPackageName = CellBroadcastUtils.getDefaultCellBroadcastReceiverPackageName(this.mContext);
                if (defaultCellBroadcastReceiverPackageName != null && this.mPm.getApplicationEnabledSetting(defaultCellBroadcastReceiverPackageName) != 2) {
                    Intent intent = new Intent();
                    this.mIntent = intent;
                    intent.setClassName(defaultCellBroadcastReceiverPackageName, "com.android.cellbroadcastreceiver.CellBroadcastSettings");
                }
                return false;
            } catch (IllegalArgumentException unused) {
                return false;
            }
        }
        return z;
    }
}
