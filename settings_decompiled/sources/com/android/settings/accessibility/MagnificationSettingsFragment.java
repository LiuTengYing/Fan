package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.Context;
import com.android.settings.DialogCreatable;
import com.android.settings.R$xml;
import com.android.settings.accessibility.MagnificationModePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class MagnificationSettingsFragment extends DashboardFragment implements MagnificationModePreferenceController.DialogHelper {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.accessibility_magnification_service_settings);
    private DialogCreatable mDialogDelegate;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MagnificationSettingsFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1815;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MagnificationModePreferenceController) use(MagnificationModePreferenceController.class)).setDialogHelper(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.MagnificationModePreferenceController.DialogHelper
    public void showDialog(int i) {
        super.showDialog(i);
    }

    @Override // com.android.settings.accessibility.MagnificationModePreferenceController.DialogHelper
    public void setDialogDelegate(DialogCreatable dialogCreatable) {
        this.mDialogDelegate = dialogCreatable;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        DialogCreatable dialogCreatable = this.mDialogDelegate;
        if (dialogCreatable != null) {
            return dialogCreatable.getDialogMetricsCategory(i);
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_magnification_service_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        Dialog onCreateDialog;
        DialogCreatable dialogCreatable = this.mDialogDelegate;
        if (dialogCreatable == null || (onCreateDialog = dialogCreatable.onCreateDialog(i)) == null) {
            throw new IllegalArgumentException("Unsupported dialogId " + i);
        }
        return onCreateDialog;
    }
}
