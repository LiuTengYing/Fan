package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.widget.Switch;
import androidx.fragment.app.Fragment;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class PocketModeFragment extends DashboardFragment implements OnMainSwitchChangeListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.unisoc.settings.smartcontrols.PocketModeFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (UserHandle.myUserId() == 0 && Utils.isSupportSmartControl(context)) {
                SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
                searchIndexableResource.xmlResId = R$xml.pocket_mode;
                arrayList.add(searchIndexableResource);
            }
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> getPreferenceControllers(Context context) {
            return PocketModeFragment.buildPreferenceControllers(context, null);
        }
    };
    private static PowerSavingPreferenceController mPowerSavingPreferenceController;
    private static SmartBellPreferenceController mSmartBellPreferenceController;
    private static TouchDisablePreferenceController mTouchDisablePreferenceController;
    private SettingsMainSwitchBar mSwitchBar;
    private boolean mValidListener = false;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PocketModeFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50953;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsMainSwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(R$string.pocket_mode_switch_title));
        this.mSwitchBar.show();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (!this.mValidListener) {
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mValidListener = true;
        }
        this.mSwitchBar.setChecked(isPocketModeEnabled(getActivity()));
        getPreferenceScreen().setEnabled(isPocketModeEnabled(getActivity()));
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mValidListener) {
            this.mSwitchBar.removeOnSwitchChangeListener(this);
            this.mValidListener = false;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.hide();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.pocket_mode;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        SmartBellPreferenceController smartBellPreferenceController = new SmartBellPreferenceController(context, fragment);
        mSmartBellPreferenceController = smartBellPreferenceController;
        arrayList.add(smartBellPreferenceController);
        TouchDisablePreferenceController touchDisablePreferenceController = new TouchDisablePreferenceController(context, fragment);
        mTouchDisablePreferenceController = touchDisablePreferenceController;
        arrayList.add(touchDisablePreferenceController);
        PowerSavingPreferenceController powerSavingPreferenceController = new PowerSavingPreferenceController(context, fragment);
        mPowerSavingPreferenceController = powerSavingPreferenceController;
        arrayList.add(powerSavingPreferenceController);
        return arrayList;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        Settings.Global.putInt(getContentResolver(), "pocket_mode_enabled", z ? 1 : 0);
        getPreferenceScreen().setEnabled(z);
        mSmartBellPreferenceController.updateOnPocketModeChange(z);
        mTouchDisablePreferenceController.updateOnPocketModeChange(z);
        mPowerSavingPreferenceController.updateOnPocketModeChange(z);
    }

    public static final boolean isPocketModeEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "pocket_mode_enabled", 0) == 1;
    }
}
