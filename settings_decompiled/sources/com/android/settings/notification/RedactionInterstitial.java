package com.android.settings.notification;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.RestrictedRadioButton;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SetupRedactionInterstitial;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.notification.RedactionInterstitial;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class RedactionInterstitial extends SettingsActivity {
    @Override // com.android.settings.core.SettingsBaseActivity
    protected boolean isToolbarEnabled() {
        return false;
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", RedactionInterstitialFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return RedactionInterstitialFragment.class.getName().equals(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        findViewById(R$id.content_parent).setFitsSystemWindows(false);
    }

    public static Intent createStartIntent(Context context, int i) {
        int i2;
        Intent intent = new Intent(context, RedactionInterstitial.class);
        if (UserManager.get(context).isManagedProfile(i)) {
            i2 = R$string.lock_screen_notifications_interstitial_title_profile;
        } else {
            i2 = R$string.lock_screen_notifications_interstitial_title;
        }
        return intent.putExtra(":settings:show_fragment_title_resid", i2).putExtra("android.intent.extra.USER_ID", i);
    }

    /* loaded from: classes.dex */
    public static class RedactionInterstitialFragment extends SettingsPreferenceFragment implements RadioGroup.OnCheckedChangeListener {
        private RadioGroup mRadioGroup;
        private RestrictedRadioButton mRedactSensitiveButton;
        private RestrictedRadioButton mShowAllButton;
        private int mUserId;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 74;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(R$layout.redaction_interstitial, viewGroup, false);
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
            this.mRadioGroup = (RadioGroup) view.findViewById(R$id.radio_group);
            this.mShowAllButton = (RestrictedRadioButton) view.findViewById(R$id.show_all);
            this.mRedactSensitiveButton = (RestrictedRadioButton) view.findViewById(R$id.redact_sensitive);
            this.mRadioGroup.setOnCheckedChangeListener(this);
            this.mUserId = Utils.getUserIdFromBundle(getContext(), getActivity().getIntent().getExtras());
            if (UserManager.get(getContext()).isManagedProfile(this.mUserId)) {
                ((TextView) view.findViewById(R$id.sud_layout_description)).setText(R$string.lock_screen_notifications_interstitial_message_profile);
                this.mShowAllButton.setText(devicePolicyManager.getResources().getString("Settings.LOCK_SCREEN_SHOW_WORK_NOTIFICATION_CONTENT", new Supplier() { // from class: com.android.settings.notification.RedactionInterstitial$RedactionInterstitialFragment$$ExternalSyntheticLambda0
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$onViewCreated$0;
                        lambda$onViewCreated$0 = RedactionInterstitial.RedactionInterstitialFragment.this.lambda$onViewCreated$0();
                        return lambda$onViewCreated$0;
                    }
                }));
                this.mRedactSensitiveButton.setText(devicePolicyManager.getResources().getString("Settings.LOCK_SCREEN_HIDE_WORK_NOTIFICATION_CONTENT", new Supplier() { // from class: com.android.settings.notification.RedactionInterstitial$RedactionInterstitialFragment$$ExternalSyntheticLambda1
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$onViewCreated$1;
                        lambda$onViewCreated$1 = RedactionInterstitial.RedactionInterstitialFragment.this.lambda$onViewCreated$1();
                        return lambda$onViewCreated$1;
                    }
                }));
                ((RadioButton) view.findViewById(R$id.hide_all)).setVisibility(8);
            }
            ((FooterBarMixin) ((GlifLayout) view.findViewById(R$id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(new FooterButton.Builder(getContext()).setText(R$string.app_notifications_dialog_done).setListener(new View.OnClickListener() { // from class: com.android.settings.notification.RedactionInterstitial$RedactionInterstitialFragment$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    RedactionInterstitial.RedactionInterstitialFragment.this.onDoneButtonClicked(view2);
                }
            }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$onViewCreated$0() {
            return getString(R$string.lock_screen_notifications_summary_show_profile);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$onViewCreated$1() {
            return getString(R$string.lock_screen_notifications_summary_hide_profile);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onDoneButtonClicked(View view) {
            if (!WizardManagerHelper.isAnySetupWizard(getIntent())) {
                SetupRedactionInterstitial.setEnabled(getContext(), false);
            }
            RedactionInterstitial redactionInterstitial = (RedactionInterstitial) getActivity();
            if (redactionInterstitial != null) {
                redactionInterstitial.setResult(0, null);
                finish();
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            checkNotificationFeaturesAndSetDisabled(this.mShowAllButton, 12);
            checkNotificationFeaturesAndSetDisabled(this.mRedactSensitiveButton, 4);
            loadFromSettings();
        }

        private void checkNotificationFeaturesAndSetDisabled(RestrictedRadioButton restrictedRadioButton, int i) {
            restrictedRadioButton.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(getActivity(), i, this.mUserId));
        }

        private void loadFromSettings() {
            boolean z = UserManager.get(getContext()).isManagedProfile(this.mUserId) || Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_show_notifications", 0, this.mUserId) != 0;
            boolean z2 = Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_allow_private_notifications", 1, this.mUserId) != 0;
            int i = R$id.hide_all;
            if (z) {
                if (z2 && !this.mShowAllButton.isDisabledByAdmin()) {
                    i = R$id.show_all;
                } else if (!this.mRedactSensitiveButton.isDisabledByAdmin()) {
                    i = R$id.redact_sensitive;
                }
            }
            this.mRadioGroup.check(i);
        }

        @Override // android.widget.RadioGroup.OnCheckedChangeListener
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int i2 = i == R$id.show_all ? 1 : 0;
            int i3 = i == R$id.hide_all ? 0 : 1;
            Settings.Secure.putIntForUser(getContentResolver(), "lock_screen_allow_private_notifications", i2, this.mUserId);
            Settings.Secure.putIntForUser(getContentResolver(), "lock_screen_show_notifications", i3, this.mUserId);
        }
    }
}
