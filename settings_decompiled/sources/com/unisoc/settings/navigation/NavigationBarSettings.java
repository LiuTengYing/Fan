package com.unisoc.settings.navigation;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.SearchIndexableData;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Settings;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.NavBarPullPreference;
import com.android.settings.widget.NavBarRadioPreference;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class NavigationBarSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new SprdNavigationSearchIndexProvider();
    private int mCurrentSelected;
    private RestrictedSwitchPreference mHideNotiBar;
    private NavBarPullPreference mNavBarPullPreference;
    private NavBarRadioPreference mNavigationBarModeLeft;
    private NavBarRadioPreference mNavigationBarModeLeftPull;
    private NavBarRadioPreference mNavigationBarModeRight;
    private NavBarRadioPreference mNavigationBarModeRightPull;

    public static boolean hasNavigationBar(Context context) {
        return false;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.navigation_bar_settings_fragment);
        initAllPreferences();
    }

    void initAllPreferences() {
        this.mHideNotiBar = (RestrictedSwitchPreference) findPreference("hide_navigation_bar_switch");
        NavBarRadioPreference navBarRadioPreference = (NavBarRadioPreference) findPreference("navigation_bar_mode_right");
        this.mNavigationBarModeRight = navBarRadioPreference;
        navBarRadioPreference.setIcon(R$drawable.navigation_bar_style_layout1);
        NavBarRadioPreference navBarRadioPreference2 = (NavBarRadioPreference) findPreference("navigation_bar_mode_left");
        this.mNavigationBarModeLeft = navBarRadioPreference2;
        navBarRadioPreference2.setIcon(R$drawable.navigation_bar_style_layout2);
        NavBarRadioPreference navBarRadioPreference3 = (NavBarRadioPreference) findPreference("navigation_bar_mode_right_pull");
        this.mNavigationBarModeRightPull = navBarRadioPreference3;
        navBarRadioPreference3.setIcon(R$drawable.navigation_bar_style_layout3);
        NavBarRadioPreference navBarRadioPreference4 = (NavBarRadioPreference) findPreference("navigation_bar_mode_left_pull");
        this.mNavigationBarModeLeftPull = navBarRadioPreference4;
        navBarRadioPreference4.setIcon(R$drawable.navigation_bar_style_layout4);
        this.mNavBarPullPreference = (NavBarPullPreference) findPreference("navigation_bar_pull_notification");
        updateCurrentState();
        this.mHideNotiBar.setOnPreferenceChangeListener(this);
        this.mNavigationBarModeRight.setOnPreferenceClickListener(this);
        this.mNavigationBarModeLeft.setOnPreferenceClickListener(this);
        this.mNavigationBarModeRightPull.setOnPreferenceClickListener(this);
        this.mNavigationBarModeLeftPull.setOnPreferenceClickListener(this);
    }

    void updateCurrentState() {
        int i = Settings.System.getInt(getContentResolver(), "navigationbar_config", 0);
        this.mCurrentSelected = i & 15;
        this.mHideNotiBar.setChecked((i & 16) != 0);
        new NavBarRadioPreference[]{this.mNavigationBarModeRight, this.mNavigationBarModeLeft, this.mNavigationBarModeRightPull, this.mNavigationBarModeLeftPull}[this.mCurrentSelected].setChecked(true);
        Log.d("NavigationBarSettings", "updateCurrentState: mHideNotiBar.isChecked= " + this.mHideNotiBar.isChecked() + ", and mode " + this.mCurrentSelected + " is choosed.");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("hide_navigation_bar_switch")) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            Log.d("NavigationBarSettings", "onPreferenceChange: mHideNotiBar.isChecked= " + booleanValue);
            int intForUser = Settings.System.getIntForUser(getContentResolver(), "navigationbar_config", 0, ActivityManager.getCurrentUser());
            if (booleanValue) {
                Settings.System.putIntForUser(getContentResolver(), "navigationbar_config", intForUser | 16, ActivityManager.getCurrentUser());
            } else {
                Settings.System.putIntForUser(getContentResolver(), "navigationbar_config", intForUser & 15, ActivityManager.getCurrentUser());
            }
            Log.d("NavigationBarSettings", "lastConfig = " + intForUser + " :newConfig = " + Settings.System.getIntForUser(getContentResolver(), "navigationbar_config", 0, ActivityManager.getCurrentUser()));
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Log.d("NavigationBarSettings", "onPreferenceClick: key= " + key);
        if (preference instanceof NavBarRadioPreference) {
            ((NavBarRadioPreference) preference).setChecked(true);
            key.hashCode();
            char c = 65535;
            switch (key.hashCode()) {
                case -974688307:
                    if (key.equals("navigation_bar_mode_right_pull")) {
                        c = 0;
                        break;
                    }
                    break;
                case -903147252:
                    if (key.equals("navigation_bar_mode_left")) {
                        c = 1;
                        break;
                    }
                    break;
                case -184476264:
                    if (key.equals("navigation_bar_mode_left_pull")) {
                        c = 2;
                        break;
                    }
                    break;
                case 2072867255:
                    if (key.equals("navigation_bar_mode_right")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.mCurrentSelected = 2;
                    this.mNavigationBarModeRight.setChecked(false);
                    this.mNavigationBarModeLeft.setChecked(false);
                    this.mNavigationBarModeLeftPull.setChecked(false);
                    break;
                case 1:
                    this.mCurrentSelected = 1;
                    this.mNavigationBarModeRight.setChecked(false);
                    this.mNavigationBarModeRightPull.setChecked(false);
                    this.mNavigationBarModeLeftPull.setChecked(false);
                    break;
                case 2:
                    this.mCurrentSelected = 3;
                    this.mNavigationBarModeRight.setChecked(false);
                    this.mNavigationBarModeLeft.setChecked(false);
                    this.mNavigationBarModeRightPull.setChecked(false);
                    break;
                case 3:
                    this.mCurrentSelected = 0;
                    this.mNavigationBarModeLeft.setChecked(false);
                    this.mNavigationBarModeRightPull.setChecked(false);
                    this.mNavigationBarModeLeftPull.setChecked(false);
                    break;
            }
            int intForUser = Settings.System.getIntForUser(getContentResolver(), "navigationbar_config", 0, ActivityManager.getCurrentUser());
            Settings.System.putIntForUser(getContentResolver(), "navigationbar_config", (intForUser & 16) + this.mCurrentSelected, ActivityManager.getCurrentUser());
            Log.d("NavigationBarSettings", "Style...lastConfig: " + intForUser + "; newConfig: " + Settings.System.getIntForUser(getContentResolver(), "navigationbar_config", 0, ActivityManager.getCurrentUser()) + "; mCurrentSelected: " + this.mCurrentSelected);
            return true;
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    /* loaded from: classes2.dex */
    private static class SprdNavigationSearchIndexProvider extends BaseSearchIndexProvider {
        private SprdNavigationSearchIndexProvider() {
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (NavigationBarSettings.hasNavigationBar(context) && !ActivityManager.isLowRamDeviceStatic() && context.getResources().getInteger(17694880) == 0) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                String string = context.getResources().getString(R$string.navigation_bar_title);
                searchIndexableRaw.title = string;
                searchIndexableRaw.screenTitle = string;
                ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
                ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = context.getPackageName();
                ((SearchIndexableData) searchIndexableRaw).intentTargetClass = Settings.NavigationBarSettingsActivity.class.getName();
                ((SearchIndexableData) searchIndexableRaw).key = "hide_navigation_bar";
                arrayList.add(searchIndexableRaw);
            }
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!NavigationBarSettings.hasNavigationBar(context) || ActivityManager.isLowRamDeviceStatic() || context.getResources().getInteger(17694880) != 0) {
                nonIndexableKeys.add("hide_navigation_bar");
            }
            return nonIndexableKeys;
        }
    }
}
