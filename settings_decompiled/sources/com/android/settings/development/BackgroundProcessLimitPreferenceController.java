package com.android.settings.development;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.os.RemoteException;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class BackgroundProcessLimitPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final String[] mListSummaries;
    private final String[] mListValues;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "app_process_limit";
    }

    public BackgroundProcessLimitPreferenceController(Context context) {
        super(context);
        this.mListValues = context.getResources().getStringArray(R$array.app_process_limit_values);
        this.mListSummaries = context.getResources().getStringArray(R$array.app_process_limit_entries);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeAppProcessLimitOptions(obj);
        updateAppProcessLimitOptions();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateAppProcessLimitOptions();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeAppProcessLimitOptions(null);
    }

    private void updateAppProcessLimitOptions() {
        try {
            int processLimit = getActivityManagerService().getProcessLimit();
            int i = 0;
            int i2 = 0;
            while (true) {
                String[] strArr = this.mListValues;
                if (i2 >= strArr.length) {
                    break;
                } else if (Integer.parseInt(strArr[i2]) >= processLimit) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            ListPreference listPreference = (ListPreference) this.mPreference;
            listPreference.setValue(this.mListValues[i]);
            listPreference.setSummary(this.mListSummaries[i]);
        } catch (RemoteException unused) {
        }
    }

    private void writeAppProcessLimitOptions(Object obj) {
        int parseInt;
        if (obj != null) {
            try {
                parseInt = Integer.parseInt(obj.toString());
            } catch (RemoteException unused) {
                return;
            }
        } else {
            parseInt = -1;
        }
        getActivityManagerService().setProcessLimit(parseInt);
        updateAppProcessLimitOptions();
    }

    IActivityManager getActivityManagerService() {
        return ActivityManager.getService();
    }
}
