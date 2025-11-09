package com.android.settings.development;

import android.app.backup.IBackupManager;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class LocalBackupPasswordPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private final IBackupManager mBackupManager;
    private final UserManager mUserManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "local_backup_password";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public LocalBackupPasswordPreferenceController(Context context) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updatePasswordSummary(preference);
    }

    private void updatePasswordSummary(Preference preference) {
        preference.setEnabled(isAdminUser() && this.mBackupManager != null);
        IBackupManager iBackupManager = this.mBackupManager;
        if (iBackupManager == null) {
            return;
        }
        try {
            if (iBackupManager.hasBackupPassword()) {
                preference.setSummary(R$string.local_backup_password_summary_change);
            } else {
                preference.setSummary(R$string.local_backup_password_summary_none);
            }
        } catch (RemoteException unused) {
        }
    }

    boolean isAdminUser() {
        return this.mUserManager.isAdminUser();
    }
}
