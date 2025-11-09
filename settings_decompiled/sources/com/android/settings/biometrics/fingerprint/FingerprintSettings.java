package com.android.settings.biometrics.fingerprint;

import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImeAwareEditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar;
import com.android.settings.biometrics.fingerprint.FingerprintRemoveSidecar;
import com.android.settings.biometrics.fingerprint.FingerprintSettings;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.TwoTargetPreference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintSettings extends SubSettings {
    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", FingerprintSettingsFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SubSettings, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return FingerprintSettingsFragment.class.getName().equals(str);
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(getText(R$string.security_settings_fingerprint_preference_title));
    }

    /* loaded from: classes.dex */
    public static class FingerprintSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, FingerprintPreference.OnDeleteClickListener {
        private FingerprintAuthenticateSidecar mAuthenticateSidecar;
        private long mChallenge;
        private boolean mEnrollClicked;
        private FingerprintManager mFingerprintManager;
        private FingerprintUpdater mFingerprintUpdater;
        private HashMap<Integer, String> mFingerprintsRenaming;
        private Drawable mHighlightDrawable;
        private boolean mInFingerprintLockout;
        private boolean mLaunchedConfirm;
        private FingerprintRemoveSidecar mRemovalSidecar;
        private List<FingerprintSensorPropertiesInternal> mSensorProperties;
        private byte[] mToken;
        private int mUserId;
        private final List<FooterColumn> mFooterColumns = new ArrayList();
        private boolean mNeedRetryFingerprint = false;
        FingerprintAuthenticateSidecar.Listener mAuthenticateListener = new FingerprintAuthenticateSidecar.Listener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.1
            @Override // com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar.Listener
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1001, authenticationResult.getFingerprint().getBiometricId(), 0).sendToTarget();
            }

            @Override // com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar.Listener
            public void onAuthenticationFailed() {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1002).sendToTarget();
            }

            @Override // com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar.Listener
            public void onAuthenticationError(int i, CharSequence charSequence) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1003, i, 0, charSequence).sendToTarget();
            }

            @Override // com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar.Listener
            public void onAuthenticationHelp(int i, CharSequence charSequence) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1004, i, 0, charSequence).sendToTarget();
            }
        };
        FingerprintRemoveSidecar.Listener mRemovalListener = new FingerprintRemoveSidecar.Listener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.2
            @Override // com.android.settings.biometrics.fingerprint.FingerprintRemoveSidecar.Listener
            public void onRemovalSucceeded(Fingerprint fingerprint) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP, fingerprint.getBiometricId(), 0).sendToTarget();
                updateDialog();
            }

            @Override // com.android.settings.biometrics.fingerprint.FingerprintRemoveSidecar.Listener
            public void onRemovalError(Fingerprint fingerprint, int i, CharSequence charSequence) {
                FragmentActivity activity = FingerprintSettingsFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, charSequence, 0);
                }
                updateDialog();
            }

            private void updateDialog() {
                RenameDialog renameDialog = (RenameDialog) FingerprintSettingsFragment.this.getFragmentManager().findFragmentByTag(RenameDialog.class.getName());
                if (renameDialog != null) {
                    renameDialog.enableDelete();
                }
            }
        };
        private final Handler mHandler = new Handler() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.3
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1000) {
                    FingerprintSettingsFragment.this.removeFingerprintPreference(message.arg1);
                    FingerprintSettingsFragment.this.updateAddPreference();
                    FingerprintSettingsFragment.this.retryFingerprint();
                } else if (i == 1001) {
                    FingerprintSettingsFragment.this.highlightFingerprintItem(message.arg1);
                    FingerprintSettingsFragment.this.retryFingerprint();
                } else if (i != 1003) {
                } else {
                    FingerprintSettingsFragment.this.handleError(message.arg1, (CharSequence) message.obj);
                }
            }
        };
        private final Runnable mFingerprintLockoutReset = new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.5
            @Override // java.lang.Runnable
            public void run() {
                FingerprintSettingsFragment.this.mInFingerprintLockout = false;
                if (!FingerprintSettingsFragment.this.mNeedRetryFingerprint) {
                    Log.d("FingerprintSettings", "not in fingerprintSetting activity,do not retryFingerprint");
                } else {
                    FingerprintSettingsFragment.this.retryFingerprint();
                }
            }
        };

        /* renamed from: -$$Nest$smgetFilters  reason: not valid java name */
        static /* bridge */ /* synthetic */ InputFilter[] m448$$Nest$smgetFilters() {
            return getFilters();
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 49;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class FooterColumn {
            View.OnClickListener mLearnMoreClickListener;
            CharSequence mLearnMoreOverrideText;
            CharSequence mTitle;

            private FooterColumn() {
                this.mTitle = null;
                this.mLearnMoreOverrideText = null;
                this.mLearnMoreClickListener = null;
            }
        }

        protected void handleError(int i, CharSequence charSequence) {
            FragmentActivity activity;
            if (i != 5) {
                if (i == 7) {
                    this.mInFingerprintLockout = true;
                    if (!this.mHandler.hasCallbacks(this.mFingerprintLockoutReset)) {
                        this.mHandler.postDelayed(this.mFingerprintLockoutReset, 30000L);
                    }
                } else if (i == 9) {
                    this.mInFingerprintLockout = true;
                } else if (i == 10) {
                    return;
                }
                if (this.mInFingerprintLockout && (activity = getActivity()) != null) {
                    Toast.makeText(activity, charSequence, 0).show();
                }
                retryFingerprint();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void retryFingerprint() {
            if (isUdfps() || this.mRemovalSidecar.inProgress() || this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() == 0 || this.mLaunchedConfirm || this.mInFingerprintLockout) {
                return;
            }
            this.mAuthenticateSidecar.startAuthentication(this.mUserId);
            this.mAuthenticateSidecar.setListener(this.mAuthenticateListener);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FragmentActivity activity = getActivity();
            FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(activity);
            this.mFingerprintManager = fingerprintManagerOrNull;
            this.mFingerprintUpdater = new FingerprintUpdater(activity, fingerprintManagerOrNull);
            this.mSensorProperties = this.mFingerprintManager.getSensorPropertiesInternal();
            this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
            this.mChallenge = activity.getIntent().getLongExtra("challenge", -1L);
            FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = (FingerprintAuthenticateSidecar) getFragmentManager().findFragmentByTag("authenticate_sidecar");
            this.mAuthenticateSidecar = fingerprintAuthenticateSidecar;
            if (fingerprintAuthenticateSidecar == null) {
                this.mAuthenticateSidecar = new FingerprintAuthenticateSidecar();
                getFragmentManager().beginTransaction().add(this.mAuthenticateSidecar, "authenticate_sidecar").commit();
            }
            this.mAuthenticateSidecar.setFingerprintManager(this.mFingerprintManager);
            FingerprintRemoveSidecar fingerprintRemoveSidecar = (FingerprintRemoveSidecar) getFragmentManager().findFragmentByTag("removal_sidecar");
            this.mRemovalSidecar = fingerprintRemoveSidecar;
            if (fingerprintRemoveSidecar == null) {
                this.mRemovalSidecar = new FingerprintRemoveSidecar();
                getFragmentManager().beginTransaction().add(this.mRemovalSidecar, "removal_sidecar").commit();
            }
            this.mRemovalSidecar.setFingerprintUpdater(this.mFingerprintUpdater);
            this.mRemovalSidecar.setListener(this.mRemovalListener);
            RenameDialog renameDialog = (RenameDialog) getFragmentManager().findFragmentByTag(RenameDialog.class.getName());
            if (renameDialog != null) {
                renameDialog.setDeleteInProgress(this.mRemovalSidecar.inProgress());
                renameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        FingerprintSettings.FingerprintSettingsFragment.this.lambda$onCreate$0(dialogInterface);
                    }
                });
            }
            this.mFingerprintsRenaming = new HashMap<>();
            if (bundle != null) {
                this.mFingerprintsRenaming = (HashMap) bundle.getSerializable("mFingerprintsRenaming");
                this.mToken = bundle.getByteArray("hw_auth_token");
                this.mLaunchedConfirm = bundle.getBoolean("launched_confirm", false);
            }
            this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
            if (this.mToken == null && !this.mLaunchedConfirm) {
                this.mLaunchedConfirm = true;
                launchChooseOrConfirmLock();
            }
            updateFooterColumns(activity);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreate$0(DialogInterface dialogInterface) {
            retryFingerprint();
        }

        private void updateFooterColumns(final Activity activity) {
            final RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(activity, 32, this.mUserId);
            final Intent helpIntent = HelpUtils.getHelpIntent(activity, getString(getHelpResource()), activity.getClass().getName());
            View.OnClickListener onClickListener = helpIntent != null ? new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    activity.startActivityForResult(helpIntent, 0);
                }
            } : null;
            this.mFooterColumns.clear();
            if (checkIfKeyguardFeaturesDisabled != null) {
                FooterColumn footerColumn = new FooterColumn();
                footerColumn.mTitle = ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString("Settings.FINGERPRINT_UNLOCK_DISABLED_EXPLANATION", new Supplier() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda4
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateFooterColumns$2;
                        lambda$updateFooterColumns$2 = FingerprintSettings.FingerprintSettingsFragment.this.lambda$updateFooterColumns$2();
                        return lambda$updateFooterColumns$2;
                    }
                });
                footerColumn.mLearnMoreClickListener = new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(activity, checkIfKeyguardFeaturesDisabled);
                    }
                };
                footerColumn.mLearnMoreOverrideText = getText(R$string.admin_support_more_info);
                this.mFooterColumns.add(footerColumn);
                FooterColumn footerColumn2 = new FooterColumn();
                footerColumn2.mTitle = getText(R$string.security_fingerprint_disclaimer_lockscreen_disabled_2);
                footerColumn2.mLearnMoreClickListener = onClickListener;
                this.mFooterColumns.add(footerColumn2);
                return;
            }
            FooterColumn footerColumn3 = new FooterColumn();
            footerColumn3.mTitle = getText(R$string.security_settings_fingerprint_v2_home_screen_text);
            footerColumn3.mLearnMoreClickListener = onClickListener;
            this.mFooterColumns.add(footerColumn3);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$updateFooterColumns$2() {
            return getString(R$string.security_fingerprint_disclaimer_lockscreen_disabled_1);
        }

        private boolean isUdfps() {
            for (FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal : this.mSensorProperties) {
                if (fingerprintSensorPropertiesInternal.isAnyUdfpsType()) {
                    return true;
                }
            }
            return false;
        }

        protected void removeFingerprintPreference(int i) {
            String genKey = genKey(i);
            Preference findPreference = findPreference(genKey);
            if (findPreference != null) {
                if (getPreferenceScreen().removePreference(findPreference)) {
                    return;
                }
                Log.w("FingerprintSettings", "Failed to remove preference with key " + genKey);
                return;
            }
            Log.w("FingerprintSettings", "Can't find preference to remove: " + genKey);
        }

        private PreferenceScreen createPreferenceHierarchy() {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                preferenceScreen.removeAll();
            }
            addPreferencesFromResource(R$xml.security_settings_fingerprint);
            PreferenceScreen preferenceScreen2 = getPreferenceScreen();
            addFingerprintItemPreferences(preferenceScreen2);
            setPreferenceScreen(preferenceScreen2);
            return preferenceScreen2;
        }

        private void addFingerprintItemPreferences(PreferenceGroup preferenceGroup) {
            preferenceGroup.removeAll();
            List enrolledFingerprints = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId);
            int size = enrolledFingerprints.size();
            for (int i = 0; i < size; i++) {
                Fingerprint fingerprint = (Fingerprint) enrolledFingerprints.get(i);
                FingerprintPreference fingerprintPreference = new FingerprintPreference(preferenceGroup.getContext(), this);
                fingerprintPreference.setKey(genKey(fingerprint.getBiometricId()));
                fingerprintPreference.setTitle(fingerprint.getName());
                fingerprintPreference.setFingerprint(fingerprint);
                fingerprintPreference.setPersistent(false);
                fingerprintPreference.setIcon(R$drawable.ic_fingerprint_24dp);
                if (this.mRemovalSidecar.isRemovingFingerprint(fingerprint.getBiometricId())) {
                    fingerprintPreference.setEnabled(false);
                }
                if (this.mFingerprintsRenaming.containsKey(Integer.valueOf(fingerprint.getBiometricId()))) {
                    fingerprintPreference.setTitle(this.mFingerprintsRenaming.get(Integer.valueOf(fingerprint.getBiometricId())));
                }
                preferenceGroup.addPreference(fingerprintPreference);
                fingerprintPreference.setOnPreferenceChangeListener(this);
            }
            Preference preference = new Preference(preferenceGroup.getContext());
            preference.setKey("key_fingerprint_add");
            preference.setTitle(R$string.fingerprint_add_title);
            preference.setIcon(R$drawable.ic_add_24dp);
            preferenceGroup.addPreference(preference);
            preference.setOnPreferenceChangeListener(this);
            updateAddPreference();
            createFooterPreference(preferenceGroup);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateAddPreference() {
            if (getActivity() == null) {
                return;
            }
            Preference findPreference = findPreference("key_fingerprint_add");
            int integer = getContext().getResources().getInteger(17694832);
            boolean z = true;
            boolean z2 = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() >= integer;
            boolean inProgress = this.mRemovalSidecar.inProgress();
            findPreference.setSummary(z2 ? getContext().getString(R$string.fingerprint_add_max, Integer.valueOf(integer)) : "");
            if (z2 || inProgress || this.mToken == null) {
                z = false;
            }
            findPreference.setEnabled(z);
        }

        private void createFooterPreference(PreferenceGroup preferenceGroup) {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }
            for (int i = 0; i < this.mFooterColumns.size(); i++) {
                FooterColumn footerColumn = this.mFooterColumns.get(i);
                FooterPreference build = new FooterPreference.Builder(activity).setTitle(footerColumn.mTitle).build();
                if (i > 0) {
                    build.setIconVisibility(8);
                }
                View.OnClickListener onClickListener = footerColumn.mLearnMoreClickListener;
                if (onClickListener != null) {
                    build.setLearnMoreAction(onClickListener);
                    if (!TextUtils.isEmpty(footerColumn.mLearnMoreOverrideText)) {
                        build.setLearnMoreText(footerColumn.mLearnMoreOverrideText);
                    }
                }
                preferenceGroup.addPreference(build);
            }
        }

        private static String genKey(int i) {
            return "key_fingerprint_item_" + i;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            this.mInFingerprintLockout = false;
            this.mNeedRetryFingerprint = true;
            updatePreferences();
            FingerprintRemoveSidecar fingerprintRemoveSidecar = this.mRemovalSidecar;
            if (fingerprintRemoveSidecar != null) {
                fingerprintRemoveSidecar.setListener(this.mRemovalListener);
            }
        }

        private void updatePreferences() {
            createPreferenceHierarchy();
            retryFingerprint();
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            FingerprintRemoveSidecar fingerprintRemoveSidecar = this.mRemovalSidecar;
            if (fingerprintRemoveSidecar != null) {
                fingerprintRemoveSidecar.setListener(null);
            }
            FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = this.mAuthenticateSidecar;
            if (fingerprintAuthenticateSidecar != null) {
                fingerprintAuthenticateSidecar.setListener(null);
                this.mAuthenticateSidecar.stopAuthentication();
            }
            this.mNeedRetryFingerprint = false;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStop() {
            super.onStop();
            if (getActivity().isChangingConfigurations() || this.mLaunchedConfirm || this.mEnrollClicked) {
                return;
            }
            getActivity().finish();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            bundle.putByteArray("hw_auth_token", this.mToken);
            bundle.putBoolean("launched_confirm", this.mLaunchedConfirm);
            bundle.putSerializable("mFingerprintsRenaming", this.mFingerprintsRenaming);
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            if ("key_fingerprint_add".equals(preference.getKey())) {
                this.mEnrollClicked = true;
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", FingerprintEnrollEnrolling.class.getName());
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                intent.putExtra("hw_auth_token", this.mToken);
                startActivityForResult(intent, 10);
            } else if (preference instanceof FingerprintPreference) {
                showRenameDialog(((FingerprintPreference) preference).getFingerprint());
            }
            return super.onPreferenceTreeClick(preference);
        }

        @Override // com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintPreference.OnDeleteClickListener
        public void onDeleteClick(FingerprintPreference fingerprintPreference) {
            boolean z = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() > 1;
            Parcelable fingerprint = fingerprintPreference.getFingerprint();
            if (z) {
                if (this.mRemovalSidecar.inProgress()) {
                    Log.d("FingerprintSettings", "Fingerprint delete in progress, skipping");
                    return;
                } else {
                    DeleteFingerprintDialog.newInstance(fingerprint, this).show(getFragmentManager(), DeleteFingerprintDialog.class.getName());
                    return;
                }
            }
            ConfirmLastDeleteDialog confirmLastDeleteDialog = new ConfirmLastDeleteDialog();
            boolean isManagedProfile = UserManager.get(getContext()).isManagedProfile(this.mUserId);
            Bundle bundle = new Bundle();
            bundle.putParcelable("fingerprint", fingerprint);
            bundle.putBoolean("isProfileChallengeUser", isManagedProfile);
            confirmLastDeleteDialog.setArguments(bundle);
            confirmLastDeleteDialog.setTargetFragment(this, 0);
            confirmLastDeleteDialog.show(getFragmentManager(), ConfirmLastDeleteDialog.class.getName());
        }

        private void showRenameDialog(Fingerprint fingerprint) {
            RenameDialog renameDialog = new RenameDialog();
            Bundle bundle = new Bundle();
            if (this.mFingerprintsRenaming.containsKey(Integer.valueOf(fingerprint.getBiometricId()))) {
                bundle.putParcelable("fingerprint", new Fingerprint(this.mFingerprintsRenaming.get(Integer.valueOf(fingerprint.getBiometricId())), fingerprint.getGroupId(), fingerprint.getBiometricId(), fingerprint.getDeviceId()));
            } else {
                bundle.putParcelable("fingerprint", fingerprint);
            }
            renameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    FingerprintSettings.FingerprintSettingsFragment.this.lambda$showRenameDialog$4(dialogInterface);
                }
            });
            renameDialog.setDeleteInProgress(this.mRemovalSidecar.inProgress());
            renameDialog.setArguments(bundle);
            renameDialog.setTargetFragment(this, 0);
            renameDialog.show(getFragmentManager(), RenameDialog.class.getName());
            this.mAuthenticateSidecar.stopAuthentication();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showRenameDialog$4(DialogInterface dialogInterface) {
            retryFingerprint();
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String key = preference.getKey();
            if ("fingerprint_enable_keyguard_toggle".equals(key)) {
                return true;
            }
            Log.v("FingerprintSettings", "Unknown key:" + key);
            return true;
        }

        @Override // com.android.settings.support.actionbar.HelpResourceProvider
        public int getHelpResource() {
            return R$string.help_url_fingerprint;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, final Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i != 101 && i != 102) {
                if (i == 10) {
                    this.mEnrollClicked = false;
                    if (i2 == 3) {
                        FragmentActivity activity = getActivity();
                        activity.setResult(i2);
                        activity.finish();
                        return;
                    }
                    return;
                }
                return;
            }
            this.mLaunchedConfirm = false;
            if (i2 == 1 || i2 == -1) {
                if (intent != null && BiometricUtils.containsGatekeeperPasswordHandle(intent)) {
                    this.mFingerprintManager.generateChallenge(this.mUserId, new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda1
                        public final void onChallengeGenerated(int i3, int i4, long j) {
                            FingerprintSettings.FingerprintSettingsFragment.this.lambda$onActivityResult$5(intent, i3, i4, j);
                        }
                    });
                    return;
                }
                Log.d("FingerprintSettings", "Data null or GK PW missing");
                finish();
                return;
            }
            Log.d("FingerprintSettings", "Password not confirmed");
            finish();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onActivityResult$5(Intent intent, int i, int i2, long j) {
            this.mToken = BiometricUtils.requestGatekeeperHat(getActivity(), intent, this.mUserId, j);
            this.mChallenge = j;
            BiometricUtils.removeGatekeeperPasswordHandle(getActivity(), intent);
            updateAddPreference();
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            if (getActivity().isFinishing()) {
                this.mFingerprintManager.revokeChallenge(this.mUserId, this.mChallenge);
            }
        }

        private Drawable getHighlightDrawable() {
            FragmentActivity activity;
            if (this.mHighlightDrawable == null && (activity = getActivity()) != null) {
                this.mHighlightDrawable = activity.getDrawable(R$drawable.preference_highlight);
            }
            return this.mHighlightDrawable;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void highlightFingerprintItem(int i) {
            final View view;
            FingerprintPreference fingerprintPreference = (FingerprintPreference) findPreference(genKey(i));
            Drawable highlightDrawable = getHighlightDrawable();
            if (highlightDrawable == null || fingerprintPreference == null || (view = fingerprintPreference.getView()) == null) {
                return;
            }
            highlightDrawable.setHotspot(view.getWidth() / 2, view.getHeight() / 2);
            view.setBackground(highlightDrawable);
            view.setPressed(true);
            view.setPressed(false);
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.4
                @Override // java.lang.Runnable
                public void run() {
                    view.setBackground(null);
                }
            }, 500L);
        }

        private void launchChooseOrConfirmLock() {
            Intent intent = new Intent();
            if (new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(101).setTitle(getString(R$string.security_settings_fingerprint_preference_title)).setRequestGatekeeperPasswordHandle(true).setUserId(this.mUserId).setForegroundOnly(true).setReturnCredentials(true).show()) {
                return;
            }
            intent.setClassName("com.android.settings", ChooseLockGeneric.class.getName());
            intent.putExtra("hide_insecure_options", true);
            intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
            intent.putExtra("request_gk_pw_handle", true);
            intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
            startActivityForResult(intent, 102);
        }

        void deleteFingerPrint(Fingerprint fingerprint) {
            this.mRemovalSidecar.startRemove(fingerprint, this.mUserId);
            Preference findPreference = findPreference(genKey(fingerprint.getBiometricId()));
            if (findPreference != null) {
                findPreference.setEnabled(false);
            }
            updateAddPreference();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void renameFingerPrint(int i, String str) {
            this.mFingerprintManager.rename(i, this.mUserId, str);
            if (!TextUtils.isEmpty(str)) {
                this.mFingerprintsRenaming.put(Integer.valueOf(i), str);
            }
            updatePreferences();
        }

        /* loaded from: classes.dex */
        public static class DeleteFingerprintDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
            private AlertDialog mAlertDialog;
            private Fingerprint mFp;

            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 570;
            }

            public static DeleteFingerprintDialog newInstance(Fingerprint fingerprint, FingerprintSettingsFragment fingerprintSettingsFragment) {
                DeleteFingerprintDialog deleteFingerprintDialog = new DeleteFingerprintDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("fingerprint", fingerprint);
                deleteFingerprintDialog.setArguments(bundle);
                deleteFingerprintDialog.setTargetFragment(fingerprintSettingsFragment, 0);
                return deleteFingerprintDialog;
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                Fingerprint parcelable = getArguments().getParcelable("fingerprint");
                this.mFp = parcelable;
                AlertDialog create = new AlertDialog.Builder(getActivity()).setTitle(getString(R$string.fingerprint_delete_title, parcelable.getName())).setMessage(getString(R$string.fingerprint_v2_delete_message, this.mFp.getName())).setPositiveButton(R$string.security_settings_fingerprint_enroll_dialog_delete, this).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).create();
                this.mAlertDialog = create;
                return create;
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    int biometricId = this.mFp.getBiometricId();
                    Log.v("FingerprintSettings", "Removing fpId=" + biometricId);
                    this.mMetricsFeatureProvider.action(getContext(), 253, biometricId);
                    ((FingerprintSettingsFragment) getTargetFragment()).deleteFingerPrint(this.mFp);
                }
            }
        }

        private static InputFilter[] getFilters() {
            return new InputFilter[]{new InputFilter() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.6
                @Override // android.text.InputFilter
                public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                    while (i < i2) {
                        if (charSequence.charAt(i) < ' ') {
                            return "";
                        }
                        i++;
                    }
                    return null;
                }
            }, new InputFilter.LengthFilter(50)};
        }

        /* loaded from: classes.dex */
        public static class RenameDialog extends InstrumentedDialogFragment {
            private AlertDialog mAlertDialog;
            private boolean mDeleteInProgress;
            private ImeAwareEditText mDialogTextField;
            private DialogInterface.OnDismissListener mDismissListener;
            private Fingerprint mFp;
            private Button mPositiveButton;

            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 570;
            }

            public void setDeleteInProgress(boolean z) {
                this.mDeleteInProgress = z;
            }

            @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                super.onCancel(dialogInterface);
                this.mDismissListener.onDismiss(dialogInterface);
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                final String str;
                final int i;
                this.mFp = getArguments().getParcelable("fingerprint");
                final int i2 = -1;
                if (bundle != null) {
                    str = bundle.getString("fingerName");
                    int i3 = bundle.getInt("startSelection", -1);
                    i = bundle.getInt("endSelection", -1);
                    i2 = i3;
                } else {
                    str = null;
                    i = -1;
                }
                AlertDialog create = new AlertDialog.Builder(getActivity()).setView(R$layout.fingerprint_rename_dialog).setPositiveButton(R$string.security_settings_fingerprint_enroll_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.RenameDialog.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i4) {
                        String obj = RenameDialog.this.mDialogTextField.getText().toString();
                        CharSequence name = RenameDialog.this.mFp.getName();
                        if (!TextUtils.equals(obj, name)) {
                            Log.d("FingerprintSettings", "rename " + ((Object) name) + " to " + obj);
                            ((InstrumentedDialogFragment) RenameDialog.this).mMetricsFeatureProvider.action(RenameDialog.this.getContext(), 254, RenameDialog.this.mFp.getBiometricId());
                            ((FingerprintSettingsFragment) RenameDialog.this.getTargetFragment()).renameFingerPrint(RenameDialog.this.mFp.getBiometricId(), obj);
                        }
                        RenameDialog.this.mDismissListener.onDismiss(dialogInterface);
                        dialogInterface.dismiss();
                    }
                }).create();
                this.mAlertDialog = create;
                create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.RenameDialog.2
                    @Override // android.content.DialogInterface.OnShowListener
                    public void onShow(DialogInterface dialogInterface) {
                        RenameDialog renameDialog = RenameDialog.this;
                        renameDialog.mDialogTextField = renameDialog.mAlertDialog.findViewById(R$id.fingerprint_rename_field);
                        CharSequence charSequence = str;
                        if (charSequence == null) {
                            charSequence = RenameDialog.this.mFp.getName();
                        }
                        RenameDialog.this.mDialogTextField.setText(charSequence);
                        if (RenameDialog.this.mPositiveButton == null) {
                            RenameDialog renameDialog2 = RenameDialog.this;
                            renameDialog2.mPositiveButton = renameDialog2.mAlertDialog.getButton(-1);
                        }
                        RenameDialog.this.mPositiveButton.setEnabled(!TextUtils.isEmpty(charSequence.toString()));
                        RenameDialog.this.mDialogTextField.addTextChangedListener(new TextWatcher() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.RenameDialog.2.1
                            @Override // android.text.TextWatcher
                            public void beforeTextChanged(CharSequence charSequence2, int i4, int i5, int i6) {
                            }

                            @Override // android.text.TextWatcher
                            public void onTextChanged(CharSequence charSequence2, int i4, int i5, int i6) {
                            }

                            @Override // android.text.TextWatcher
                            public void afterTextChanged(Editable editable) {
                                RenameDialog.this.mPositiveButton.setEnabled(!TextUtils.isEmpty(editable.toString()));
                            }
                        });
                        RenameDialog.this.mDialogTextField.setFilters(FingerprintSettingsFragment.m448$$Nest$smgetFilters());
                        if (i2 != -1 && i != -1) {
                            RenameDialog.this.mDialogTextField.setSelection(i2, i);
                        } else {
                            RenameDialog.this.mDialogTextField.selectAll();
                        }
                        if (RenameDialog.this.mDeleteInProgress) {
                            RenameDialog.this.mAlertDialog.getButton(-2).setEnabled(false);
                        }
                        RenameDialog.this.mDialogTextField.requestFocus();
                        RenameDialog.this.mDialogTextField.scheduleShowSoftInput();
                    }
                });
                return this.mAlertDialog;
            }

            public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
                this.mDismissListener = onDismissListener;
            }

            public void enableDelete() {
                this.mDeleteInProgress = false;
                AlertDialog alertDialog = this.mAlertDialog;
                if (alertDialog != null) {
                    alertDialog.getButton(-2).setEnabled(true);
                }
            }

            @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
            public void onSaveInstanceState(Bundle bundle) {
                super.onSaveInstanceState(bundle);
                ImeAwareEditText imeAwareEditText = this.mDialogTextField;
                if (imeAwareEditText != null) {
                    bundle.putString("fingerName", imeAwareEditText.getText().toString());
                    bundle.putInt("startSelection", this.mDialogTextField.getSelectionStart());
                    bundle.putInt("endSelection", this.mDialogTextField.getSelectionEnd());
                }
            }
        }

        /* loaded from: classes.dex */
        public static class ConfirmLastDeleteDialog extends InstrumentedDialogFragment {
            private Fingerprint mFp;

            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 571;
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                final int i;
                this.mFp = getArguments().getParcelable("fingerprint");
                boolean z = getArguments().getBoolean("isProfileChallengeUser");
                String string = getString(R$string.fingerprint_delete_title, this.mFp.getName());
                final String string2 = getString(R$string.fingerprint_v2_delete_message, this.mFp.getName());
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class);
                String str = z ? "Settings.WORK_PROFILE_FINGERPRINT_LAST_DELETE_MESSAGE" : "UNDEFINED";
                if (z) {
                    i = R$string.fingerprint_last_delete_message_profile_challenge;
                } else {
                    i = R$string.fingerprint_last_delete_message;
                }
                return new AlertDialog.Builder(getActivity()).setTitle(string).setMessage(devicePolicyManager.getResources().getString(str, new Supplier() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$ConfirmLastDeleteDialog$$ExternalSyntheticLambda0
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$onCreateDialog$0;
                        lambda$onCreateDialog$0 = FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.this.lambda$onCreateDialog$0(string2, i);
                        return lambda$onCreateDialog$0;
                    }
                })).setPositiveButton(R$string.security_settings_fingerprint_enroll_dialog_delete, new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        ((FingerprintSettingsFragment) ConfirmLastDeleteDialog.this.getTargetFragment()).deleteFingerPrint(ConfirmLastDeleteDialog.this.mFp);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R$string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.ConfirmLastDeleteDialog.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        dialogInterface.dismiss();
                    }
                }).create();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ String lambda$onCreateDialog$0(String str, int i) {
                return str + "\n\n" + getContext().getString(i);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class FingerprintPreference extends TwoTargetPreference {
        private View mDeleteView;
        private Fingerprint mFingerprint;
        private final OnDeleteClickListener mOnDeleteClickListener;
        private View mView;

        /* loaded from: classes.dex */
        public interface OnDeleteClickListener {
            void onDeleteClick(FingerprintPreference fingerprintPreference);
        }

        public FingerprintPreference(Context context, OnDeleteClickListener onDeleteClickListener) {
            super(context);
            this.mOnDeleteClickListener = onDeleteClickListener;
        }

        public View getView() {
            return this.mView;
        }

        public void setFingerprint(Fingerprint fingerprint) {
            this.mFingerprint = fingerprint;
        }

        public Fingerprint getFingerprint() {
            return this.mFingerprint;
        }

        @Override // com.android.settingslib.widget.TwoTargetPreference
        protected int getSecondTargetResId() {
            return R$layout.preference_widget_delete;
        }

        @Override // com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            View view = preferenceViewHolder.itemView;
            this.mView = view;
            View findViewById = view.findViewById(R$id.delete_button);
            this.mDeleteView = findViewById;
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (FingerprintPreference.this.mOnDeleteClickListener != null) {
                        FingerprintPreference.this.mOnDeleteClickListener.onDeleteClick(FingerprintPreference.this);
                    }
                }
            });
        }
    }
}
