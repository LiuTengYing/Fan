package com.android.settings.location;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.location.LocationEnabler;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
/* loaded from: classes.dex */
public class LocationSettings extends DashboardFragment implements LocationEnabler.LocationModeChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.location_settings);
    private ContentObserver mContentObserver;
    private RecentLocationAccessPreferenceController mController;
    private LocationEnabler mLocationEnabler;
    private LocationSwitchBarController mSwitchBarController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LocationSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 63;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        switchBar.setTitle(getContext().getString(R$string.location_settings_primary_switch_title));
        switchBar.show();
        this.mSwitchBarController = new LocationSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
        this.mLocationEnabler = new LocationEnabler(getContext(), this, getSettingsLifecycle());
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.location.LocationSettings.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                LocationSettings.this.mController.updateShowSystem();
            }
        };
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("locationShowSystemOps"), false, this.mContentObserver);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AppLocationPermissionPreferenceController) use(AppLocationPermissionPreferenceController.class)).init(this);
        RecentLocationAccessPreferenceController recentLocationAccessPreferenceController = (RecentLocationAccessPreferenceController) use(RecentLocationAccessPreferenceController.class);
        this.mController = recentLocationAccessPreferenceController;
        recentLocationAccessPreferenceController.init(this);
        ((RecentLocationAccessSeeAllButtonPreferenceController) use(RecentLocationAccessSeeAllButtonPreferenceController.class)).init(this);
        ((LocationForWorkPreferenceController) use(LocationForWorkPreferenceController.class)).init(this);
        ((LocationSettingsFooterPreferenceController) use(LocationSettingsFooterPreferenceController.class)).init(this);
        ((LocationAssistGnssPreferenceController) use(LocationAssistGnssPreferenceController.class)).init(this);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.location_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        replaceEnterpriseStringTitle("managed_profile_location_switch", "Settings.WORK_PROFILE_LOCATION_SWITCH_TITLE", R$string.managed_profile_location_switch_title);
    }

    @Override // com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        if (this.mLocationEnabler.isEnabled(i)) {
            scrollToPreference("recent_location_access");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addPreferencesSorted(List<Preference> list, PreferenceGroup preferenceGroup) {
        Collections.sort(list, Comparator.comparing(new Function() { // from class: com.android.settings.location.LocationSettings$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$addPreferencesSorted$0;
                lambda$addPreferencesSorted$0 = LocationSettings.lambda$addPreferencesSorted$0((Preference) obj);
                return lambda$addPreferencesSorted$0;
            }
        }));
        for (Preference preference : list) {
            preferenceGroup.addPreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$addPreferencesSorted$0(Preference preference) {
        return preference.getTitle().toString();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_location_access;
    }
}
