package com.android.settings;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.EncryptionInterstitial;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import java.util.List;
/* loaded from: classes.dex */
public class EncryptionInterstitial extends SettingsActivity {
    private static final String TAG = "EncryptionInterstitial";

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", EncryptionInterstitialFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, android.app.Activity, android.view.ContextThemeWrapper
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        super.onApplyThemeResource(theme, SetupWizardUtils.getTheme(this, getIntent()), z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return EncryptionInterstitialFragment.class.getName().equals(str);
    }

    public static Intent createStartIntent(Context context, int i, boolean z, Intent intent) {
        return new Intent(context, EncryptionInterstitial.class).putExtra("extra_password_quality", i).putExtra(":settings:show_fragment_title_resid", R$string.encryption_interstitial_header).putExtra("extra_require_password", z).putExtra("extra_unlock_method_intent", intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        findViewById(R$id.content_parent).setFitsSystemWindows(false);
    }

    /* loaded from: classes.dex */
    public static class EncryptionInterstitialFragment extends InstrumentedFragment {
        private boolean mPasswordRequired;
        private int mRequestedPasswordQuality;
        private Intent mUnlockMethodIntent;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 48;
        }

        @Override // androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(R$layout.encryption_interstitial, viewGroup, false);
        }

        @Override // androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            int i;
            super.onViewCreated(view, bundle);
            boolean booleanExtra = getActivity().getIntent().getBooleanExtra("for_fingerprint", false);
            boolean booleanExtra2 = getActivity().getIntent().getBooleanExtra("for_face", false);
            boolean booleanExtra3 = getActivity().getIntent().getBooleanExtra("for_biometrics", false);
            Intent intent = getActivity().getIntent();
            this.mRequestedPasswordQuality = intent.getIntExtra("extra_password_quality", 0);
            this.mUnlockMethodIntent = (Intent) intent.getParcelableExtra("extra_unlock_method_intent");
            int i2 = this.mRequestedPasswordQuality;
            if (i2 != 65536) {
                if (i2 == 131072 || i2 == 196608) {
                    if (booleanExtra) {
                        i = R$string.encryption_interstitial_message_pin_for_fingerprint;
                    } else if (booleanExtra2) {
                        i = R$string.encryption_interstitial_message_pin_for_face;
                    } else if (booleanExtra3) {
                        i = R$string.encryption_interstitial_message_pin_for_biometrics;
                    } else {
                        i = R$string.encryption_interstitial_message_pin;
                    }
                } else if (booleanExtra) {
                    i = R$string.encryption_interstitial_message_password_for_fingerprint;
                } else if (booleanExtra2) {
                    i = R$string.encryption_interstitial_message_password_for_face;
                } else if (booleanExtra3) {
                    i = R$string.encryption_interstitial_message_password_for_biometrics;
                } else {
                    i = R$string.encryption_interstitial_message_password;
                }
            } else if (booleanExtra) {
                i = R$string.encryption_interstitial_message_pattern_for_fingerprint;
            } else if (booleanExtra2) {
                i = R$string.encryption_interstitial_message_pattern_for_face;
            } else if (booleanExtra3) {
                i = R$string.encryption_interstitial_message_pattern_for_biometrics;
            } else {
                i = R$string.encryption_interstitial_message_pattern;
            }
            ((TextView) getActivity().findViewById(R$id.sud_layout_description)).setText(i);
            setRequirePasswordState(getActivity().getIntent().getBooleanExtra("extra_require_password", true));
            GlifLayout glifLayout = (GlifLayout) view;
            glifLayout.setHeaderText(getActivity().getTitle());
            FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
            footerBarMixin.setSecondaryButton(new FooterButton.Builder(getContext()).setText(R$string.encryption_interstitial_no).setListener(new View.OnClickListener() { // from class: com.android.settings.EncryptionInterstitial$EncryptionInterstitialFragment$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    EncryptionInterstitial.EncryptionInterstitialFragment.this.onNoButtonClicked(view2);
                }
            }).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
            footerBarMixin.setPrimaryButton(new FooterButton.Builder(getContext()).setText(R$string.encryption_interstitial_yes).setListener(new View.OnClickListener() { // from class: com.android.settings.EncryptionInterstitial$EncryptionInterstitialFragment$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    EncryptionInterstitial.EncryptionInterstitialFragment.this.onYesButtonClicked(view2);
                }
            }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
        }

        protected void startLockIntent() {
            Intent intent = this.mUnlockMethodIntent;
            if (intent != null) {
                intent.putExtra("extra_require_password", this.mPasswordRequired);
                startActivityForResult(this.mUnlockMethodIntent, 100);
                return;
            }
            Log.wtf(EncryptionInterstitial.TAG, "no unlock intent to start");
            finish();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i != 100 || i2 == 0) {
                return;
            }
            getActivity().setResult(i2, intent);
            finish();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onYesButtonClicked(View view) {
            if (AccessibilityManager.getInstance(getActivity()).isEnabled() && !this.mPasswordRequired) {
                setRequirePasswordState(false);
                AccessibilityWarningDialogFragment.newInstance(this.mRequestedPasswordQuality).show(getChildFragmentManager(), "AccessibilityWarningDialog");
                return;
            }
            setRequirePasswordState(true);
            startLockIntent();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onNoButtonClicked(View view) {
            setRequirePasswordState(false);
            startLockIntent();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRequirePasswordState(boolean z) {
            this.mPasswordRequired = z;
        }

        public void finish() {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                activity.finish();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class AccessibilityWarningDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 581;
        }

        public static AccessibilityWarningDialogFragment newInstance(int i) {
            AccessibilityWarningDialogFragment accessibilityWarningDialogFragment = new AccessibilityWarningDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putInt("extra_password_quality", i);
            accessibilityWarningDialogFragment.setArguments(bundle);
            return accessibilityWarningDialogFragment;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            int i;
            int i2;
            int i3 = getArguments().getInt("extra_password_quality");
            if (i3 == 65536) {
                i = R$string.encrypt_talkback_dialog_require_pattern;
                i2 = R$string.encrypt_talkback_dialog_message_pattern;
            } else if (i3 == 131072 || i3 == 196608) {
                i = R$string.encrypt_talkback_dialog_require_pin;
                i2 = R$string.encrypt_talkback_dialog_message_pin;
            } else {
                i = R$string.encrypt_talkback_dialog_require_password;
                i2 = R$string.encrypt_talkback_dialog_message_password;
            }
            FragmentActivity activity = getActivity();
            List<AccessibilityServiceInfo> enabledAccessibilityServiceList = AccessibilityManager.getInstance(activity).getEnabledAccessibilityServiceList(-1);
            return new AlertDialog.Builder(activity).setTitle(i).setMessage(getString(i2, enabledAccessibilityServiceList.isEmpty() ? "" : enabledAccessibilityServiceList.get(0).getResolveInfo().loadLabel(activity.getPackageManager()))).setCancelable(true).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            EncryptionInterstitialFragment encryptionInterstitialFragment = (EncryptionInterstitialFragment) getParentFragment();
            if (encryptionInterstitialFragment != null) {
                if (i == -1) {
                    encryptionInterstitialFragment.setRequirePasswordState(true);
                    encryptionInterstitialFragment.startLockIntent();
                } else if (i == -2) {
                    encryptionInterstitialFragment.setRequirePasswordState(false);
                }
            }
        }
    }
}
