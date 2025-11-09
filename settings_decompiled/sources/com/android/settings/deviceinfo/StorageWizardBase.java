package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settingslib.Utils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import java.text.NumberFormat;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class StorageWizardBase extends FragmentActivity {
    private FooterButton mBack;
    protected DiskInfo mDisk;
    private FooterBarMixin mFooterBarMixin;
    private FooterButton mNext;
    protected StorageManager mStorage;
    private final StorageEventListener mStorageListener = new StorageEventListener() { // from class: com.android.settings.deviceinfo.StorageWizardBase.1
        public void onDiskDestroyed(DiskInfo diskInfo) {
            if (StorageWizardBase.this.mDisk.id.equals(diskInfo.id)) {
                StorageWizardBase.this.finish();
            }
        }
    };
    protected VolumeInfo mVolume;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mStorage = (StorageManager) getSystemService(StorageManager.class);
        String stringExtra = getIntent().getStringExtra("android.os.storage.extra.VOLUME_ID");
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mVolume = this.mStorage.findVolumeById(stringExtra);
        }
        String stringExtra2 = getIntent().getStringExtra("android.os.storage.extra.DISK_ID");
        if (!TextUtils.isEmpty(stringExtra2)) {
            this.mDisk = this.mStorage.findDiskById(stringExtra2);
        } else {
            VolumeInfo volumeInfo = this.mVolume;
            if (volumeInfo != null) {
                this.mDisk = volumeInfo.getDisk();
            }
        }
        if (this.mDisk != null) {
            this.mStorage.registerListener(this.mStorageListener);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void setContentView(int i) {
        super.setContentView(i);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getGlifLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.wizard_back).setListener(new View.OnClickListener() { // from class: com.android.settings.deviceinfo.StorageWizardBase$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StorageWizardBase.this.onNavigateBack(view);
            }
        }).setButtonType(0).setTheme(R$style.SudGlifButton_Secondary).build());
        this.mFooterBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R$string.wizard_next).setListener(new View.OnClickListener() { // from class: com.android.settings.deviceinfo.StorageWizardBase$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StorageWizardBase.this.onNavigateNext(view);
            }
        }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
        this.mBack = this.mFooterBarMixin.getSecondaryButton();
        this.mNext = this.mFooterBarMixin.getPrimaryButton();
        setIcon(17302840);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        this.mStorage.unregisterListener(this.mStorageListener);
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    protected GlifLayout getGlifLayout() {
        return (GlifLayout) requireViewById(R$id.setup_wizard_layout);
    }

    protected ProgressBar getProgressBar() {
        return (ProgressBar) requireViewById(R$id.storage_wizard_progress);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCurrentProgress(int i) {
        getProgressBar().setProgress(i);
        ((TextView) requireViewById(R$id.storage_wizard_progress_summary)).setText(NumberFormat.getPercentInstance().format(i / 100.0d));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderText(int i, CharSequence... charSequenceArr) {
        CharSequence expandTemplate = TextUtils.expandTemplate(getText(i), charSequenceArr);
        getGlifLayout().setHeaderText(expandTemplate);
        setTitle(expandTemplate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setBodyText(int i, CharSequence... charSequenceArr) {
        TextView textView = (TextView) requireViewById(R$id.storage_wizard_body);
        textView.setText(TextUtils.expandTemplate(getText(i), charSequenceArr));
        textView.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAuxChecklist() {
        FrameLayout frameLayout = (FrameLayout) requireViewById(R$id.storage_wizard_aux);
        frameLayout.addView(LayoutInflater.from(frameLayout.getContext()).inflate(R$layout.storage_wizard_checklist, (ViewGroup) frameLayout, false));
        frameLayout.setVisibility(0);
        ((TextView) frameLayout.requireViewById(R$id.storage_wizard_migrate_v2_checklist_media)).setText(TextUtils.expandTemplate(getText(R$string.storage_wizard_migrate_v2_checklist_media), getDiskShortDescription()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setBackButtonText(int i, CharSequence... charSequenceArr) {
        this.mBack.setText(TextUtils.expandTemplate(getText(i), charSequenceArr));
        this.mBack.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNextButtonText(int i, CharSequence... charSequenceArr) {
        this.mNext.setText(TextUtils.expandTemplate(getText(i), charSequenceArr));
        this.mNext.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setBackButtonVisibility(int i) {
        this.mBack.setVisibility(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNextButtonVisibility(int i) {
        this.mNext.setVisibility(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setIcon(int i) {
        GlifLayout glifLayout = getGlifLayout();
        Drawable mutate = getDrawable(i).mutate();
        mutate.setTintList(Utils.getColorAccent(glifLayout.getContext()));
        glifLayout.setIcon(mutate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setKeepScreenOn(boolean z) {
        getGlifLayout().setKeepScreenOn(z);
    }

    public void onNavigateBack(View view) {
        throw new UnsupportedOperationException();
    }

    public void onNavigateNext(View view) {
        throw new UnsupportedOperationException();
    }

    private void copyStringExtra(Intent intent, Intent intent2, String str) {
        if (!intent.hasExtra(str) || intent2.hasExtra(str)) {
            return;
        }
        intent2.putExtra(str, intent.getStringExtra(str));
    }

    private void copyBooleanExtra(Intent intent, Intent intent2, String str) {
        if (!intent.hasExtra(str) || intent2.hasExtra(str)) {
            return;
        }
        intent2.putExtra(str, intent.getBooleanExtra(str, false));
    }

    @Override // android.app.Activity, android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        Intent intent2 = getIntent();
        copyStringExtra(intent2, intent, "android.os.storage.extra.DISK_ID");
        copyStringExtra(intent2, intent, "android.os.storage.extra.VOLUME_ID");
        copyStringExtra(intent2, intent, "format_forget_uuid");
        copyBooleanExtra(intent2, intent, "format_private");
        copyBooleanExtra(intent2, intent, "format_slow");
        copyBooleanExtra(intent2, intent, "migrate_skip");
        super.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VolumeInfo findFirstVolume(int i) {
        return findFirstVolume(i, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VolumeInfo findFirstVolume(int i, int i2) {
        while (true) {
            for (VolumeInfo volumeInfo : this.mStorage.getVolumes()) {
                if (Objects.equals(this.mDisk.getId(), volumeInfo.getDiskId()) && volumeInfo.getType() == i && volumeInfo.getState() == 2) {
                    return volumeInfo;
                }
            }
            i2--;
            if (i2 <= 0) {
                return null;
            }
            Log.w("StorageWizardBase", "Missing mounted volume of type " + i + " hosted by disk " + this.mDisk.getId() + "; trying again");
            SystemClock.sleep(250L);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CharSequence getDiskDescription() {
        DiskInfo diskInfo = this.mDisk;
        if (diskInfo != null) {
            return diskInfo.getDescription();
        }
        VolumeInfo volumeInfo = this.mVolume;
        if (volumeInfo != null) {
            return volumeInfo.getDescription();
        }
        return getText(R$string.unknown);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CharSequence getDiskShortDescription() {
        DiskInfo diskInfo = this.mDisk;
        if (diskInfo != null) {
            return diskInfo.getShortDescription();
        }
        VolumeInfo volumeInfo = this.mVolume;
        if (volumeInfo != null) {
            return volumeInfo.getDescription();
        }
        return getText(R$string.unknown);
    }
}
