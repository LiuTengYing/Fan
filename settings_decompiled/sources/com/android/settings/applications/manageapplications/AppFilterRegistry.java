package com.android.settings.applications.manageapplications;

import com.android.settings.R$string;
import com.android.settings.applications.AppStateAlarmsAndRemindersBridge;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settings.applications.AppStateLocaleBridge;
import com.android.settings.applications.AppStateManageExternalStorageBridge;
import com.android.settings.applications.AppStateMediaManagementAppsBridge;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.applications.AppStatePowerBridge;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.applications.AppStateWriteSettingsBridge;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class AppFilterRegistry {
    private static AppFilterRegistry sRegistry;
    private final AppFilterItem[] mFilters;

    public int getDefaultFilterType(int i) {
        switch (i) {
            case 1:
                return 2;
            case 2:
            case 3:
            case 9:
            default:
                return 4;
            case 4:
                return 10;
            case 5:
                return 0;
            case 6:
                return 11;
            case 7:
                return 12;
            case 8:
                return 13;
            case 10:
                return 15;
            case 11:
                return 17;
            case 12:
                return 18;
            case 13:
                return 19;
            case 14:
                return 20;
        }
    }

    private AppFilterRegistry() {
        AppFilterItem[] appFilterItemArr = new AppFilterItem[21];
        this.mFilters = appFilterItemArr;
        ApplicationsState.AppFilter appFilter = AppStatePowerBridge.FILTER_POWER_ALLOWLISTED;
        ApplicationsState.AppFilter appFilter2 = ApplicationsState.FILTER_ALL_ENABLED;
        appFilterItemArr[0] = new AppFilterItem(new ApplicationsState.CompoundFilter(appFilter, appFilter2), 0, R$string.high_power_filter_on);
        ApplicationsState.CompoundFilter compoundFilter = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, appFilter2);
        int i = R$string.filter_all_apps;
        appFilterItemArr[1] = new AppFilterItem(compoundFilter, 1, i);
        appFilterItemArr[4] = new AppFilterItem(ApplicationsState.FILTER_EVERYTHING, 4, i);
        appFilterItemArr[5] = new AppFilterItem(appFilter2, 5, R$string.filter_enabled_apps);
        appFilterItemArr[7] = new AppFilterItem(ApplicationsState.FILTER_DISABLED, 7, R$string.filter_apps_disabled);
        appFilterItemArr[6] = new AppFilterItem(ApplicationsState.FILTER_INSTANT, 6, R$string.filter_instant_apps);
        appFilterItemArr[2] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_RECENCY, 2, R$string.sort_order_recent_notification);
        appFilterItemArr[3] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_FREQUENCY, 3, R$string.sort_order_frequent_notification);
        appFilterItemArr[8] = new AppFilterItem(ApplicationsState.FILTER_PERSONAL, 8, R$string.category_personal);
        appFilterItemArr[9] = new AppFilterItem(ApplicationsState.FILTER_WORK, 9, R$string.category_work);
        appFilterItemArr[10] = new AppFilterItem(AppStateUsageBridge.FILTER_APP_USAGE, 10, i);
        appFilterItemArr[11] = new AppFilterItem(AppStateOverlayBridge.FILTER_SYSTEM_ALERT_WINDOW, 11, R$string.filter_overlay_apps);
        ApplicationsState.AppFilter appFilter3 = AppStateWriteSettingsBridge.FILTER_WRITE_SETTINGS;
        int i2 = R$string.filter_write_settings_apps;
        appFilterItemArr[12] = new AppFilterItem(appFilter3, 12, i2);
        appFilterItemArr[13] = new AppFilterItem(AppStateInstallAppsBridge.FILTER_APP_SOURCES, 13, R$string.filter_install_sources_apps);
        appFilterItemArr[15] = new AppFilterItem(AppStateChangeWifiStateBridge.FILTER_CHANGE_WIFI_STATE, 15, i2);
        appFilterItemArr[16] = new AppFilterItem(AppStateNotificationBridge.FILTER_APP_NOTIFICATION_BLOCKED, 16, R$string.filter_notif_blocked_apps);
        appFilterItemArr[17] = new AppFilterItem(AppStateManageExternalStorageBridge.FILTER_MANAGE_EXTERNAL_STORAGE, 17, R$string.filter_manage_external_storage);
        appFilterItemArr[18] = new AppFilterItem(AppStateAlarmsAndRemindersBridge.FILTER_CLOCK_APPS, 18, R$string.alarms_and_reminders_title);
        appFilterItemArr[19] = new AppFilterItem(AppStateMediaManagementAppsBridge.FILTER_MEDIA_MANAGEMENT_APPS, 19, R$string.media_management_apps_title);
        appFilterItemArr[20] = new AppFilterItem(AppStateLocaleBridge.FILTER_APPS_LOCALE, 20, R$string.app_locale_picker_title);
    }

    public static AppFilterRegistry getInstance() {
        if (sRegistry == null) {
            sRegistry = new AppFilterRegistry();
        }
        return sRegistry;
    }

    public AppFilterItem get(int i) {
        return this.mFilters[i];
    }
}
