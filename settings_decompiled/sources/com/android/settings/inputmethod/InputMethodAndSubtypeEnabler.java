package com.android.settings.inputmethod;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
/* loaded from: classes.dex */
public class InputMethodAndSubtypeEnabler extends DashboardFragment {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "InputMethodAndSubtypeEnabler";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 60;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.input_methods_subtype;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((InputMethodAndSubtypePreferenceController) use(InputMethodAndSubtypePreferenceController.class)).initialize(this, getStringExtraFromIntentOrArguments("input_method_id"));
    }

    private String getStringExtraFromIntentOrArguments(String str) {
        String stringExtra = getActivity().getIntent().getStringExtra(str);
        if (stringExtra != null) {
            return stringExtra;
        }
        Bundle arguments = getArguments();
        if (arguments == null) {
            return null;
        }
        return arguments.getString(str);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        String stringExtraFromIntentOrArguments = getStringExtraFromIntentOrArguments("android.intent.extra.TITLE");
        Log.d("InputMethodAndSubtypeEnabler", "onActivityCreated: " + stringExtraFromIntentOrArguments);
        if (TextUtils.isEmpty(stringExtraFromIntentOrArguments)) {
            return;
        }
        getActivity().setTitle(stringExtraFromIntentOrArguments);
    }
}
