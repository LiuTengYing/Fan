package com.android.settings.notification.zen;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.ScheduleCalendar;
import android.service.notification.ZenModeConfig;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes.dex */
public abstract class AbstractZenModePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause {
    protected static ZenModeConfigWrapper mZenModeConfigWrapper;
    final String KEY;
    protected final ZenModeBackend mBackend;
    protected MetricsFeatureProvider mMetricsFeatureProvider;
    private final NotificationManager mNotificationManager;
    protected PreferenceScreen mScreen;
    protected SettingObserver mSettingObserver;

    public AbstractZenModePreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context);
        mZenModeConfigWrapper = new ZenModeConfigWrapper(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.KEY = str;
        this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        this.mBackend = ZenModeBackend.getInstance(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        Preference findPreference = preferenceScreen.findPreference(this.KEY);
        if (findPreference != null) {
            if (this.mSettingObserver == null) {
                this.mSettingObserver = new SettingObserver();
            }
            this.mSettingObserver.setPreference(findPreference);
        }
    }

    public void onResume() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver());
            this.mSettingObserver.onChange(false, null);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.unregister(this.mContext.getContentResolver());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public NotificationManager.Policy getPolicy() {
        return this.mNotificationManager.getNotificationPolicy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ZenModeConfig getZenModeConfig() {
        return this.mNotificationManager.getZenModeConfig();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getZenMode() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", this.mBackend.mZenMode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getZenDuration() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "zen_duration", 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SettingObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_DURATION_URI;
        private final Uri ZEN_MODE_URI;
        private Preference mPreference;

        public SettingObserver() {
            super(new Handler());
            this.ZEN_MODE_URI = Settings.Global.getUriFor("zen_mode");
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
            this.ZEN_MODE_DURATION_URI = Settings.Secure.getUriFor("zen_duration");
        }

        public void setPreference(Preference preference) {
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(this.ZEN_MODE_URI, false, this, -1);
            contentResolver.registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this, -1);
            contentResolver.registerContentObserver(this.ZEN_MODE_DURATION_URI, false, this, -1);
        }

        public void unregister(ContentResolver contentResolver) {
            contentResolver.unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (uri == null || this.ZEN_MODE_URI.equals(uri) || this.ZEN_MODE_CONFIG_ETAG_URI.equals(uri) || this.ZEN_MODE_DURATION_URI.equals(uri)) {
                AbstractZenModePreferenceController.this.mBackend.updatePolicy();
                AbstractZenModePreferenceController.this.mBackend.updateZenMode();
                AbstractZenModePreferenceController abstractZenModePreferenceController = AbstractZenModePreferenceController.this;
                PreferenceScreen preferenceScreen = abstractZenModePreferenceController.mScreen;
                if (preferenceScreen != null) {
                    abstractZenModePreferenceController.displayPreference(preferenceScreen);
                }
                AbstractZenModePreferenceController.this.updateState(this.mPreference);
            }
        }
    }

    /* loaded from: classes.dex */
    static class ZenModeConfigWrapper {
        private final Context mContext;

        public ZenModeConfigWrapper(Context context) {
            this.mContext = context;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public String getOwnerCaption(String str) {
            return ZenModeConfig.getOwnerCaption(this.mContext, str);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public boolean isTimeRule(Uri uri) {
            return ZenModeConfig.isValidEventConditionId(uri) || ZenModeConfig.isValidScheduleConditionId(uri);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public CharSequence getFormattedTime(long j, int i) {
            return ZenModeConfig.getFormattedTime(this.mContext, j, isToday(j), i);
        }

        private boolean isToday(long j) {
            return ZenModeConfig.isToday(j);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public long parseManualRuleTime(Uri uri) {
            return ZenModeConfig.tryParseCountdownConditionId(uri);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public long parseAutomaticRuleEndTime(Uri uri) {
            if (ZenModeConfig.isValidEventConditionId(uri)) {
                return Long.MAX_VALUE;
            }
            if (ZenModeConfig.isValidScheduleConditionId(uri)) {
                ScheduleCalendar scheduleCalendar = ZenModeConfig.toScheduleCalendar(uri);
                long nextChangeTime = scheduleCalendar.getNextChangeTime(System.currentTimeMillis());
                if (scheduleCalendar.exitAtAlarm()) {
                    long nextAlarm = AbstractZenModePreferenceController.getNextAlarm(this.mContext);
                    scheduleCalendar.maybeSetNextAlarm(System.currentTimeMillis(), nextAlarm);
                    if (scheduleCalendar.shouldExitForAlarm(nextChangeTime)) {
                        return nextAlarm;
                    }
                }
                return nextChangeTime;
            }
            return -1L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getNextAlarm(Context context) {
        AlarmManager.AlarmClockInfo nextAlarmClock = ((AlarmManager) context.getSystemService("alarm")).getNextAlarmClock(ActivityManager.getCurrentUser());
        if (nextAlarmClock != null) {
            return nextAlarmClock.getTriggerTime();
        }
        return 0L;
    }
}
