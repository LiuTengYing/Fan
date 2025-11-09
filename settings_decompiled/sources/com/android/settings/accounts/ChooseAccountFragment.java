package com.android.settings.accounts;

import android.content.Context;
import android.os.UserManager;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
/* loaded from: classes.dex */
public class ChooseAccountFragment extends DashboardFragment {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ChooseAccountFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 10;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ChooseAccountPreferenceController) use(ChooseAccountPreferenceController.class)).initialize(getIntent().getStringArrayExtra("authorities"), getIntent().getStringArrayExtra("account_types"), Utils.getSecureTargetUser(getActivity().getActivityToken(), UserManager.get(getContext()), null, getIntent().getExtras()), getActivity());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.add_account_settings;
    }
}
