package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.wifi.SoftApConfiguration;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Pair;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.ValidatedEditTextPreference;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.UUID;
/* loaded from: classes2.dex */
public class WifiTetherPasswordPreferenceController extends WifiTetherBasePreferenceController implements ValidatedEditTextPreference.Validator {
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private String mPassword;
    private int mSecurityType;

    WifiTetherPasswordPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener, MetricsFeatureProvider metricsFeatureProvider) {
        super(context, onTetherConfigUpdateListener);
        this.mMetricsFeatureProvider = metricsFeatureProvider;
    }

    public WifiTetherPasswordPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_network_password_2" : "wifi_tether_network_password";
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mSoftApConfig;
        if (softApConfiguration == null) {
            softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        }
        if (softApConfiguration.getSecurityType() != 0 && TextUtils.isEmpty(softApConfiguration.getPassphrase())) {
            this.mPassword = generateRandomPassword();
        } else if (TextUtils.isEmpty(softApConfiguration.getPassphrase())) {
            this.mPassword = TextUtils.isEmpty(this.mPassword) ? generateRandomPassword() : this.mPassword;
        } else {
            this.mPassword = softApConfiguration.getPassphrase();
        }
        this.mSecurityType = softApConfiguration.getSecurityType();
        ((ValidatedEditTextPreference) this.mPreference).setValidator(this);
        ((ValidatedEditTextPreference) this.mPreference).setIsPassword(true);
        ((ValidatedEditTextPreference) this.mPreference).setIsSummaryPassword(true);
        updatePasswordDisplay((EditTextPreference) this.mPreference);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str = (String) obj;
        if (TextUtils.equals(this.mPassword, str)) {
            return true;
        }
        this.mMetricsFeatureProvider.action(this.mContext, 1737, new Pair[0]);
        this.mPassword = str;
        updatePasswordDisplay((EditTextPreference) this.mPreference);
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    public String getPasswordValidated(int i) {
        if (i == 0) {
            return "";
        }
        if (!WifiUtils.isHotspotPasswordValid(this.mPassword, i)) {
            this.mPassword = generateRandomPassword();
            updatePasswordDisplay((EditTextPreference) this.mPreference);
        }
        return this.mPassword;
    }

    public void setSecurityType(int i) {
        this.mSecurityType = i;
        updatePasswordDisplay((EditTextPreference) this.mPreference);
    }

    @Override // com.android.settings.widget.ValidatedEditTextPreference.Validator
    public boolean isTextValid(String str) {
        return WifiUtils.isHotspotPasswordValid(str, this.mSecurityType);
    }

    private static String generateRandomPassword() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13);
    }

    private void updatePasswordDisplay(EditTextPreference editTextPreference) {
        ValidatedEditTextPreference validatedEditTextPreference = (ValidatedEditTextPreference) editTextPreference;
        validatedEditTextPreference.setText(this.mPassword);
        if (!TextUtils.isEmpty(this.mPassword) && this.mSecurityType != 0) {
            validatedEditTextPreference.setIsSummaryPassword(true);
            validatedEditTextPreference.setSummary(this.mPassword);
            validatedEditTextPreference.setVisible(true);
            return;
        }
        validatedEditTextPreference.setIsSummaryPassword(false);
        validatedEditTextPreference.setSummary(R$string.wifi_hotspot_no_password_subtext);
        validatedEditTextPreference.setVisible(false);
    }

    public void updateEnabled(boolean z) {
        ((ValidatedEditTextPreference) this.mPreference).setEnabled(z);
    }
}
