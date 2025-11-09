package com.android.settings.development;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class CachedAppsFreezerPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private static final String CACHED_APPS_FREEZER_KEY = "cached_apps_freezer";
    private final String[] mListSummaries;
    private final String[] mListValues;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return CACHED_APPS_FREEZER_KEY;
    }

    public CachedAppsFreezerPreferenceController(Context context) {
        super(context);
        this.mListValues = context.getResources().getStringArray(R$array.cached_apps_freezer_values);
        this.mListSummaries = context.getResources().getStringArray(R$array.cached_apps_freezer_entries);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        try {
            return ActivityManager.getService().isAppFreezerSupported();
        } catch (RemoteException unused) {
            Log.w("PrefControllerMixin", "Unable to obtain freezer support status from ActivityManager");
            return false;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (obj.equals(Settings.Global.getString(this.mContext.getContentResolver(), CACHED_APPS_FREEZER_KEY))) {
            return true;
        }
        new AlertDialog.Builder(this.mContext).setMessage(R$string.cached_apps_freezer_reboot_dialog_text).setPositiveButton(17039370, getRebootDialogOkListener(obj)).setNegativeButton(17039360, getRebootDialogCancelListener()).create().show();
        return true;
    }

    private DialogInterface.OnClickListener getRebootDialogOkListener(final Object obj) {
        return new DialogInterface.OnClickListener() { // from class: com.android.settings.development.CachedAppsFreezerPreferenceController$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CachedAppsFreezerPreferenceController.this.lambda$getRebootDialogOkListener$0(obj, dialogInterface, i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getRebootDialogOkListener$0(Object obj, DialogInterface dialogInterface, int i) {
        Settings.Global.putString(this.mContext.getContentResolver(), CACHED_APPS_FREEZER_KEY, obj.toString());
        updateState(this.mPreference);
        ((PowerManager) this.mContext.getSystemService(PowerManager.class)).reboot(null);
    }

    private DialogInterface.OnClickListener getRebootDialogCancelListener() {
        return new DialogInterface.OnClickListener() { // from class: com.android.settings.development.CachedAppsFreezerPreferenceController$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CachedAppsFreezerPreferenceController.this.lambda$getRebootDialogCancelListener$1(dialogInterface, i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getRebootDialogCancelListener$1(DialogInterface dialogInterface, int i) {
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ListPreference listPreference = (ListPreference) preference;
        String string = Settings.Global.getString(this.mContext.getContentResolver(), CACHED_APPS_FREEZER_KEY);
        int i = 0;
        int i2 = 0;
        while (true) {
            String[] strArr = this.mListValues;
            if (i2 >= strArr.length) {
                break;
            } else if (TextUtils.equals(string, strArr[i2])) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        listPreference.setValue(this.mListValues[i]);
        listPreference.setSummary(this.mListSummaries[i]);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsDisabled() {
        super.onDeveloperOptionsDisabled();
        Settings.Global.putString(this.mContext.getContentResolver(), CACHED_APPS_FREEZER_KEY, this.mListValues[0].toString());
    }
}
