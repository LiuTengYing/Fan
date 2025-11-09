package com.android.settings.deviceinfo;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.UserHandle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
/* loaded from: classes.dex */
public class LocalSystemUpdatePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private DialogInterface.OnClickListener mDialogClickListener;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "RecoverySystemUpdate";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        int i = R$string.recovery_update_message;
        getPreferenceKey();
        preference.getKey();
        getPreferenceKey().equals(preference.getKey());
        Resources resources = this.mContext.getResources();
        int i2 = R$string.recovery_update_title;
        resources.getString(i2);
        if (getPreferenceKey().equals(preference.getKey())) {
            new AlertDialog.Builder(this.mContext).setTitle(i2).setMessage(this.mContext.getResources().getString(i)).setPositiveButton(17039370, this.mDialogClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setCancelable(false).show();
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R$bool.config_support_otaupdate) && isAdminUser();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            preferenceScreen.addPreference(initLocalSystemUpdatePreference());
        }
    }

    public boolean isAdminUser() {
        return UserHandle.myUserId() == 0;
    }

    public Preference initLocalSystemUpdatePreference() {
        Preference preference = new Preference(this.mContext);
        preference.setTitle(R$string.recovery_update_title);
        preference.setKey("RecoverySystemUpdate");
        preference.setOrder(0);
        return preference;
    }
}
