package com.android.settings.users;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class AutoSyncDataPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final PreferenceFragmentCompat mParentFragment;
    protected UserHandle mUserHandle;
    protected final UserManager mUserManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_sync_account_data";
    }

    public AutoSyncDataPreferenceController(Context context, PreferenceFragmentCompat preferenceFragmentCompat) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mParentFragment = preferenceFragmentCompat;
        this.mUserHandle = Process.myUserHandle();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) preference).setChecked(ContentResolver.getMasterSyncAutomaticallyAsUser(this.mUserHandle.getIdentifier()));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean isChecked = switchPreference.isChecked();
            switchPreference.setChecked(!isChecked);
            if (ActivityManager.isUserAMonkey()) {
                Log.d("AutoSyncDataController", "ignoring monkey's attempt to flip sync state");
                return true;
            }
            ConfirmAutoSyncChangeFragment.newInstance(isChecked, this.mUserHandle.getIdentifier(), getPreferenceKey()).show(this.mParentFragment);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mUserManager.isManagedProfile() && (this.mUserManager.isRestrictedProfile() || this.mUserManager.getProfiles(UserHandle.myUserId()).size() == 1);
    }

    /* loaded from: classes.dex */
    public static class ConfirmAutoSyncChangeFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 535;
        }

        static ConfirmAutoSyncChangeFragment newInstance(boolean z, int i, String str) {
            ConfirmAutoSyncChangeFragment confirmAutoSyncChangeFragment = new ConfirmAutoSyncChangeFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("enabling", z);
            bundle.putInt("userId", i);
            bundle.putString("key", str);
            confirmAutoSyncChangeFragment.setArguments(bundle);
            return confirmAutoSyncChangeFragment;
        }

        void show(PreferenceFragmentCompat preferenceFragmentCompat) {
            if (preferenceFragmentCompat.isAdded()) {
                setTargetFragment(preferenceFragmentCompat, 0);
                show(preferenceFragmentCompat.getParentFragmentManager(), "confirmAutoSyncChange");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (!requireArguments().getBoolean("enabling")) {
                builder.setTitle(R$string.data_usage_auto_sync_off_dialog_title);
                builder.setMessage(R$string.data_usage_auto_sync_off_dialog);
            } else {
                builder.setTitle(R$string.data_usage_auto_sync_on_dialog_title);
                builder.setMessage(R$string.data_usage_auto_sync_on_dialog);
            }
            builder.setPositiveButton(17039370, this);
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                Bundle requireArguments = requireArguments();
                boolean z = requireArguments.getBoolean("enabling");
                ContentResolver.setMasterSyncAutomaticallyAsUser(z, requireArguments.getInt("userId"));
                Fragment targetFragment = getTargetFragment();
                if (targetFragment instanceof PreferenceFragmentCompat) {
                    Preference findPreference = ((PreferenceFragmentCompat) targetFragment).findPreference(requireArguments.getString("key"));
                    if (findPreference instanceof SwitchPreference) {
                        ((SwitchPreference) findPreference).setChecked(z);
                    }
                }
            }
        }
    }
}
