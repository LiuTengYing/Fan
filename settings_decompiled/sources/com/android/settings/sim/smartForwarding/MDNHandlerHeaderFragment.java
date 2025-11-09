package com.android.settings.sim.smartForwarding;

import android.os.Bundle;
import android.widget.EditText;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.R$xml;
import com.android.settingslib.core.instrumentation.Instrumentable;
/* loaded from: classes.dex */
public class MDNHandlerHeaderFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, EditTextPreference.OnBindEditTextListener, Instrumentable {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1571;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R$xml.smart_forwarding_mdn_handler_header, str);
        EditTextPreference editTextPreference = (EditTextPreference) findPreference("slot0_phone_number");
        editTextPreference.setOnBindEditTextListener(this);
        editTextPreference.setOnPreferenceChangeListener(this);
        String phoneNumber = SmartForwardingUtils.getPhoneNumber(getContext(), 0);
        editTextPreference.setSummary(phoneNumber);
        editTextPreference.setText(phoneNumber);
        EditTextPreference editTextPreference2 = (EditTextPreference) findPreference("slot1_phone_number");
        editTextPreference2.setOnPreferenceChangeListener(this);
        editTextPreference2.setOnBindEditTextListener(this);
        String phoneNumber2 = SmartForwardingUtils.getPhoneNumber(getContext(), 1);
        editTextPreference2.setSummary(phoneNumber2);
        editTextPreference2.setText(phoneNumber2);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        preference.setSummary(obj.toString());
        return true;
    }

    @Override // androidx.preference.EditTextPreference.OnBindEditTextListener
    public void onBindEditText(EditText editText) {
        editText.setInputType(3);
        editText.setSingleLine(true);
    }
}
