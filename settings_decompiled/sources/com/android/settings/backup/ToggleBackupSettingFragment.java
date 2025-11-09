package com.android.settings.backup;

import android.app.Dialog;
import android.app.backup.IBackupManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SettingsMainSwitchBar;
/* loaded from: classes.dex */
public class ToggleBackupSettingFragment extends SettingsPreferenceFragment implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private IBackupManager mBackupManager;
    private Dialog mConfirmDialog;
    private Preference mSummaryPreference;
    protected SettingsMainSwitchBar mSwitchBar;
    private boolean mWaitingForConfirmationDialog = false;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 81;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
        setPreferenceScreen(createPreferenceScreen);
        Preference preference = new Preference(getPrefContext()) { // from class: com.android.settings.backup.ToggleBackupSettingFragment.1
            @Override // androidx.preference.Preference
            public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
                super.onBindViewHolder(preferenceViewHolder);
                ((TextView) preferenceViewHolder.findViewById(16908304)).setText(getSummary());
            }
        };
        this.mSummaryPreference = preference;
        preference.setPersistent(false);
        this.mSummaryPreference.setLayoutResource(R$layout.text_description_preference);
        createPreferenceScreen.addPreference(this.mSummaryPreference);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mSwitchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        if (Settings.Secure.getInt(getContentResolver(), "user_full_data_backup_aware", 0) != 0) {
            this.mSummaryPreference.setSummary(R$string.fullbackup_data_summary);
        } else {
            this.mSummaryPreference.setSummary(R$string.backup_data_summary);
        }
        try {
            IBackupManager iBackupManager = this.mBackupManager;
            this.mSwitchBar.setCheckedInternal(iBackupManager == null ? false : iBackupManager.isBackupEnabled());
        } catch (RemoteException unused) {
            this.mSwitchBar.setEnabled(false);
        }
        getActivity().setTitle(R$string.backup_data_title);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.setOnBeforeCheckedChangeListener(null);
        this.mSwitchBar.hide();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mSwitchBar.setOnBeforeCheckedChangeListener(new SettingsMainSwitchBar.OnBeforeCheckedChangeListener() { // from class: com.android.settings.backup.ToggleBackupSettingFragment.2
            @Override // com.android.settings.widget.SettingsMainSwitchBar.OnBeforeCheckedChangeListener
            public boolean onBeforeCheckedChanged(Switch r1, boolean z) {
                if (!z) {
                    ToggleBackupSettingFragment.this.showEraseBackupDialog();
                    return true;
                }
                ToggleBackupSettingFragment.this.setBackupEnabled(true);
                ToggleBackupSettingFragment.this.mSwitchBar.setCheckedInternal(true);
                return true;
            }
        });
        this.mSwitchBar.show();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        Dialog dialog = this.mConfirmDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mConfirmDialog.dismiss();
        }
        this.mConfirmDialog = null;
        super.onStop();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mWaitingForConfirmationDialog = false;
            setBackupEnabled(false);
            this.mSwitchBar.setCheckedInternal(false);
        } else if (i == -2) {
            this.mWaitingForConfirmationDialog = false;
            setBackupEnabled(true);
            this.mSwitchBar.setCheckedInternal(true);
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        if (this.mWaitingForConfirmationDialog) {
            setBackupEnabled(true);
            this.mSwitchBar.setCheckedInternal(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showEraseBackupDialog() {
        CharSequence text;
        if (Settings.Secure.getInt(getContentResolver(), "user_full_data_backup_aware", 0) != 0) {
            text = getResources().getText(R$string.fullbackup_erase_dialog_message);
        } else {
            text = getResources().getText(R$string.backup_erase_dialog_message);
        }
        this.mWaitingForConfirmationDialog = true;
        this.mConfirmDialog = new AlertDialog.Builder(getActivity()).setMessage(text).setTitle(R$string.backup_erase_dialog_title).setPositiveButton(17039370, this).setNegativeButton(17039360, this).setOnDismissListener(this).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBackupEnabled(boolean z) {
        IBackupManager iBackupManager = this.mBackupManager;
        if (iBackupManager != null) {
            try {
                iBackupManager.setBackupEnabled(z);
            } catch (RemoteException e) {
                Log.e("ToggleBackupSettingFragment", "Error communicating with BackupManager", e);
            }
        }
    }
}
