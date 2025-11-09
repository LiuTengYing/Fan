package com.android.settings.datetime;

import android.app.Dialog;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.datetime.DatePreferenceController;
import com.android.settings.datetime.TimePreferenceController;
import com.android.settings.datetime.UnisocLocationTimeDetectionPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DateTimeSettings extends DashboardFragment implements TimePreferenceController.TimePreferenceHost, DatePreferenceController.DatePreferenceHost, UnisocLocationTimeDetectionPreferenceController.UnisocAutoTimePreferenceHost {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.date_time_prefs);

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 0) {
            return i != 1 ? 0 : 608;
        }
        return 607;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DateTimeSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 38;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.date_time_prefs;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        getSettingsLifecycle().addObserver(new TimeChangeListenerMixin(context, this));
        ((LocationTimeZoneDetectionPreferenceController) use(LocationTimeZoneDetectionPreferenceController.class)).setFragment(this);
        ((UnisocLocationTimeDetectionPreferenceController) use(UnisocLocationTimeDetectionPreferenceController.class)).setFragmentAndUpdatePreference(this, this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        FragmentActivity activity = getActivity();
        boolean booleanExtra = activity.getIntent().getBooleanExtra("firstRun", false);
        AutoTimeZonePreferenceController autoTimeZonePreferenceController = new AutoTimeZonePreferenceController(activity, this, booleanExtra);
        AutoTimePreferenceController autoTimePreferenceController = new AutoTimePreferenceController(activity, this);
        AutoTimeFormatPreferenceController autoTimeFormatPreferenceController = new AutoTimeFormatPreferenceController(activity, this);
        arrayList.add(autoTimeZonePreferenceController);
        arrayList.add(autoTimePreferenceController);
        arrayList.add(autoTimeFormatPreferenceController);
        arrayList.add(new TimeFormatPreferenceController(activity, this, booleanExtra));
        arrayList.add(new TimeZonePreferenceController(activity, autoTimeZonePreferenceController));
        arrayList.add(new TimePreferenceController(activity, this, autoTimePreferenceController));
        arrayList.add(new DatePreferenceController(activity, this, autoTimePreferenceController));
        return arrayList;
    }

    @Override // com.android.settings.datetime.UpdateTimeAndDateCallback
    public void updateTimeAndDateDisplay(Context context) {
        updatePreferenceStates();
    }

    @Override // com.android.settings.datetime.UnisocLocationTimeDetectionPreferenceController.UnisocAutoTimePreferenceHost
    public void updatePreference() {
        updatePreferenceStates();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 0) {
            if (i == 1) {
                return ((TimePreferenceController) use(TimePreferenceController.class)).buildTimePicker(getActivity());
            }
            throw new IllegalArgumentException();
        }
        return ((DatePreferenceController) use(DatePreferenceController.class)).buildDatePicker(getActivity());
    }

    @Override // com.android.settings.datetime.TimePreferenceController.TimePreferenceHost
    public void showTimePicker() {
        removeDialog(1);
        showDialog(1);
    }

    @Override // com.android.settings.datetime.DatePreferenceController.DatePreferenceHost
    public void showDatePicker() {
        showDialog(0);
    }
}
