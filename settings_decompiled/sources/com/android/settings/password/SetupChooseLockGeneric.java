package com.android.settings.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$dimen;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SetupEncryptionInterstitial;
import com.android.settings.SetupWizardUtils;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.utils.SettingsDividerItemDecoration;
import com.google.android.setupdesign.GlifPreferenceLayout;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes.dex */
public class SetupChooseLockGeneric extends ChooseLockGeneric {
    @Override // com.android.settings.core.SettingsBaseActivity
    protected boolean isToolbarEnabled() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.password.ChooseLockGeneric, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return SetupChooseLockGenericFragment.class.getName().equals(str);
    }

    @Override // com.android.settings.password.ChooseLockGeneric
    Class<? extends PreferenceFragmentCompat> getFragmentClass() {
        return SetupChooseLockGenericFragment.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        if (getIntent().hasExtra("requested_min_complexity")) {
            IBinder activityToken = getActivityToken();
            if (!PasswordUtils.isCallingAppPermitted(this, activityToken, "android.permission.REQUEST_PASSWORD_COMPLEXITY")) {
                PasswordUtils.crashCallingApplication(activityToken, "Must have permission android.permission.REQUEST_PASSWORD_COMPLEXITY to use extra android.app.extra.PASSWORD_COMPLEXITY", 5);
                finish();
                return;
            }
        }
        findViewById(R$id.content_parent).setFitsSystemWindows(false);
    }

    /* loaded from: classes.dex */
    public static class SetupChooseLockGenericFragment extends ChooseLockGeneric.ChooseLockGenericFragment {
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        protected boolean alwaysHideInsecureScreenLockTypes() {
            return true;
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        protected boolean canRunBeforeDeviceProvisioned() {
            return true;
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            GlifPreferenceLayout glifPreferenceLayout = (GlifPreferenceLayout) view;
            glifPreferenceLayout.setDividerItemDecoration(new SettingsDividerItemDecoration(getContext()));
            glifPreferenceLayout.setDividerInset(getContext().getResources().getDimensionPixelSize(R$dimen.sud_items_glif_text_divider_inset));
            glifPreferenceLayout.setIcon(getContext().getDrawable(R$drawable.ic_lock));
            int i = isForBiometric() ? R$string.lock_settings_picker_title : R$string.setup_lock_settings_picker_title;
            if (getActivity() != null) {
                getActivity().setTitle(i);
            }
            glifPreferenceLayout.setHeaderText(i);
            setDivider(null);
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        protected void addHeaderView() {
            if (isForBiometric()) {
                setHeaderView(R$layout.setup_choose_lock_generic_biometrics_header);
            } else {
                setHeaderView(R$layout.setup_choose_lock_generic_header);
            }
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment, androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            if (intent == null) {
                intent = new Intent();
            }
            intent.putExtra(":settings:password_quality", new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
            super.onActivityResult(i, i2, intent);
        }

        @Override // androidx.preference.PreferenceFragmentCompat
        public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return ((GlifPreferenceLayout) viewGroup).onCreateRecyclerView(layoutInflater, viewGroup, bundle);
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        protected Class<? extends ChooseLockGeneric.InternalActivity> getInternalActivityClass() {
            return InternalActivity.class;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public void addPreferences() {
            if (isForBiometric()) {
                super.addPreferences();
            } else {
                addPreferencesFromResource(R$xml.setup_security_settings_picker);
            }
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            if ("unlock_set_do_later".equals(preference.getKey())) {
                SetupSkipDialog.newInstance(getActivity().getIntent().getBooleanExtra(":settings:frp_supported", false), false, false, false, false, false).show(getFragmentManager());
                return true;
            }
            return super.onPreferenceTreeClick(preference);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Intent getLockPasswordIntent(int i) {
            Intent modifyIntentForSetup = SetupChooseLockPassword.modifyIntentForSetup(getContext(), super.getLockPasswordIntent(i));
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), modifyIntentForSetup);
            return modifyIntentForSetup;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Intent getLockPatternIntent() {
            Intent modifyIntentForSetup = SetupChooseLockPattern.modifyIntentForSetup(getContext(), super.getLockPatternIntent());
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), modifyIntentForSetup);
            return modifyIntentForSetup;
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        protected Intent getEncryptionInterstitialIntent(Context context, int i, boolean z, Intent intent) {
            Intent createStartIntent = SetupEncryptionInterstitial.createStartIntent(context, i, z, intent);
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), createStartIntent);
            return createStartIntent;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Intent getBiometricEnrollIntent(Context context) {
            Intent biometricEnrollIntent = super.getBiometricEnrollIntent(context);
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), biometricEnrollIntent);
            return biometricEnrollIntent;
        }

        private boolean isForBiometric() {
            return this.mForFingerprint || this.mForFace || this.mForBiometrics;
        }
    }

    /* loaded from: classes.dex */
    public static class InternalActivity extends ChooseLockGeneric.InternalActivity {

        /* loaded from: classes.dex */
        public static class InternalSetupChooseLockGenericFragment extends ChooseLockGeneric.ChooseLockGenericFragment {
            @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
            protected boolean canRunBeforeDeviceProvisioned() {
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric, com.android.settings.SettingsActivity
        public boolean isValidFragment(String str) {
            return InternalSetupChooseLockGenericFragment.class.getName().equals(str);
        }

        @Override // com.android.settings.password.ChooseLockGeneric
        Class<? extends Fragment> getFragmentClass() {
            return InternalSetupChooseLockGenericFragment.class;
        }
    }
}
