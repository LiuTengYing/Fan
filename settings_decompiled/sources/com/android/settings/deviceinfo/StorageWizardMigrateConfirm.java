package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;
import com.android.settings.R$drawable;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockSettingsHelper;
import java.util.Objects;
/* loaded from: classes.dex */
public class StorageWizardMigrateConfirm extends StorageWizardBase {
    private MigrateEstimateTask mEstimate;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.deviceinfo.StorageWizardBase, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.storage_wizard_generic);
        if (this.mVolume == null) {
            this.mVolume = findFirstVolume(1);
        }
        if (getPackageManager().getPrimaryStorageCurrentVolume() == null || this.mVolume == null) {
            Log.d("StorageWizardMigrateConfirm", "Missing either source or target volume");
            finish();
            return;
        }
        setIcon(R$drawable.ic_swap_horiz);
        setHeaderText(R$string.storage_wizard_migrate_v2_title, getDiskShortDescription());
        setBodyText(R$string.memory_calculating_size, new CharSequence[0]);
        setAuxChecklist();
        MigrateEstimateTask migrateEstimateTask = new MigrateEstimateTask(this) { // from class: com.android.settings.deviceinfo.StorageWizardMigrateConfirm.1
            @Override // com.android.settings.deviceinfo.MigrateEstimateTask
            public void onPostExecute(String str, String str2) {
                StorageWizardMigrateConfirm storageWizardMigrateConfirm = StorageWizardMigrateConfirm.this;
                storageWizardMigrateConfirm.setBodyText(R$string.storage_wizard_migrate_v2_body, storageWizardMigrateConfirm.getDiskDescription(), str, str2);
            }
        };
        this.mEstimate = migrateEstimateTask;
        migrateEstimateTask.copyFrom(getIntent());
        this.mEstimate.execute(new Void[0]);
        setBackButtonText(R$string.storage_wizard_migrate_v2_later, new CharSequence[0]);
        setNextButtonText(R$string.storage_wizard_migrate_v2_now, new CharSequence[0]);
    }

    @Override // com.android.settings.deviceinfo.StorageWizardBase
    public void onNavigateBack(View view) {
        FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, 1413, new Pair[0]);
        if (this.mDisk != null) {
            Intent intent = new Intent(this, StorageWizardReady.class);
            intent.putExtra("migrate_skip", true);
            startActivity(intent);
            return;
        }
        finishAffinity();
    }

    @Override // com.android.settings.deviceinfo.StorageWizardBase
    public void onNavigateNext(View view) {
        if (StorageManager.isFileEncryptedNativeOrEmulated()) {
            for (UserInfo userInfo : ((UserManager) getSystemService(UserManager.class)).getUsers()) {
                if (!StorageManager.isUserKeyUnlocked(userInfo.id)) {
                    Log.d("StorageWizardMigrateConfirm", "User " + userInfo.id + " is currently locked; requesting unlock");
                    new ChooseLockSettingsHelper.Builder(this).setRequestCode(100).setDescription(TextUtils.expandTemplate(getText(R$string.storage_wizard_move_unlock), userInfo.name)).setUserId(userInfo.id).setAllowAnyUserId(true).setForceVerifyPath(true).show();
                    return;
                }
            }
        }
        try {
            int movePrimaryStorage = getPackageManager().movePrimaryStorage(this.mVolume);
            FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, 1412, new Pair[0]);
            Intent intent = new Intent(this, StorageWizardMigrateProgress.class);
            intent.putExtra("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
            intent.putExtra("android.content.pm.extra.MOVE_ID", movePrimaryStorage);
            startActivity(intent);
            finishAffinity();
        } catch (IllegalArgumentException e) {
            if (Objects.equals(this.mVolume.getFsUuid(), ((StorageManager) getSystemService("storage")).getPrimaryStorageVolume().getUuid())) {
                Intent intent2 = new Intent(this, StorageWizardReady.class);
                intent2.putExtra("android.os.storage.extra.DISK_ID", getIntent().getStringExtra("android.os.storage.extra.DISK_ID"));
                startActivity(intent2);
                finishAffinity();
                return;
            }
            throw e;
        } catch (IllegalStateException unused) {
            Toast.makeText(this, getString(R$string.another_migration_already_in_progress), 1).show();
            finishAffinity();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 100) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == -1) {
            onNavigateNext(null);
        } else {
            Log.w("StorageWizardMigrateConfirm", "Failed to confirm credentials");
        }
    }
}
