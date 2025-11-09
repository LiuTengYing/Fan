package com.android.settings.deviceinfo;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.deviceinfo.storage.StorageUtils;
import com.android.settings.overlay.FeatureFactory;
/* loaded from: classes.dex */
public class StorageWizardInit extends StorageWizardBase {
    private Button mInternal;
    private boolean mIsPermittedToAdopt;
    private final StorageEventListener mStorageEventListener = new StorageEventListener() { // from class: com.android.settings.deviceinfo.StorageWizardInit.1
        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (StorageUtils.isStorageSettingsInterestedVolume(volumeInfo) && volumeInfo.getState() == 0) {
                StorageWizardInit.this.finish();
            }
        }
    };
    private StorageManager mStorageManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.deviceinfo.StorageWizardBase, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mDisk == null) {
            finish();
            return;
        }
        setContentView(R$layout.storage_wizard_init);
        this.mIsPermittedToAdopt = UserManager.get(this).isAdminUser() && !ActivityManager.isUserAMonkey();
        setHeaderText(R$string.storage_wizard_init_v2_title, getDiskShortDescription());
        this.mInternal = (Button) requireViewById(R$id.storage_wizard_init_internal);
        this.mStorageManager = (StorageManager) getSystemService(StorageManager.class);
        setBackButtonText(R$string.storage_wizard_init_v2_later, new CharSequence[0]);
        setNextButtonVisibility(4);
        if (!this.mDisk.isAdoptable()) {
            this.mInternal.setEnabled(false);
            onNavigateExternal(null);
        } else if (!this.mIsPermittedToAdopt) {
            this.mInternal.setEnabled(false);
        }
        this.mStorageManager.registerListener(this.mStorageEventListener);
    }

    @Override // com.android.settings.deviceinfo.StorageWizardBase
    public void onNavigateBack(View view) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.deviceinfo.StorageWizardBase, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        StorageManager storageManager = this.mStorageManager;
        if (storageManager != null) {
            storageManager.unregisterListener(this.mStorageEventListener);
        }
    }

    public void onNavigateExternal(View view) {
        if (view != null) {
            FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, 1407, new Pair[0]);
        }
        VolumeInfo volumeInfo = this.mVolume;
        if (volumeInfo != null && volumeInfo.getType() == 0 && this.mVolume.getState() != 6) {
            this.mStorage.setVolumeInited(this.mVolume.getFsUuid(), true);
            Intent intent = new Intent(this, StorageWizardReady.class);
            intent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
            startActivity(intent);
            finish();
            return;
        }
        StorageWizardFormatConfirm.showPublic(this, this.mDisk.getId());
    }

    public void onNavigateInternal(View view) {
        if (view != null) {
            FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, 1408, new Pair[0]);
        }
        StorageWizardFormatConfirm.showPrivate(this, this.mDisk.getId());
    }
}
