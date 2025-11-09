package com.android.settings.backup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchIndexableData;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class UserBackupSettingsActivity extends FragmentActivity {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.backup.UserBackupSettingsActivity.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            int i = R$string.privacy_settings_title;
            searchIndexableRaw.title = context.getString(i);
            searchIndexableRaw.screenTitle = context.getString(i);
            searchIndexableRaw.keywords = context.getString(R$string.keywords_backup);
            ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = context.getPackageName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetClass = UserBackupSettingsActivity.class.getName();
            ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
            ((SearchIndexableData) searchIndexableRaw).key = "Backup";
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!new BackupSettingsHelper(context).isBackupServiceActive()) {
                nonIndexableKeys.add("Backup");
            }
            return nonIndexableKeys;
        }
    };
    private FragmentManager mFragmentManager;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BackupSettingsHelper backupSettingsHelper = new BackupSettingsHelper(this);
        if (!backupSettingsHelper.isBackupProvidedByManufacturer()) {
            if (Log.isLoggable("BackupSettingsActivity", 3)) {
                Log.d("BackupSettingsActivity", "No manufacturer settings found, launching the backup settings directly");
            }
            Intent intentForBackupSettings = backupSettingsHelper.getIntentForBackupSettings();
            try {
                getPackageManager().setComponentEnabledSetting(intentForBackupSettings.getComponent(), 1, 1);
            } catch (SecurityException e) {
                Log.w("BackupSettingsActivity", "Trying to enable activity " + intentForBackupSettings.getComponent() + " but couldn't: " + e.getMessage());
            }
            startActivityForResult(intentForBackupSettings, 1);
            finish();
            return;
        }
        if (Log.isLoggable("BackupSettingsActivity", 3)) {
            Log.d("BackupSettingsActivity", "Manufacturer provided backup settings, showing the preference screen");
        }
        if (this.mFragmentManager == null) {
            this.mFragmentManager = getSupportFragmentManager();
        }
        this.mFragmentManager.beginTransaction().replace(16908290, new BackupSettingsFragment()).commit();
    }

    void setFragmentManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }
}
