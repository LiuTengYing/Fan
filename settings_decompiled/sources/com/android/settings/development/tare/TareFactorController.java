package com.android.settings.development.tare;

import android.app.tare.EconomyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.KeyValueListParser;
import android.util.Slog;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class TareFactorController {
    private static TareFactorController sInstance;
    private String mAlarmManagerConstants;
    private final ContentResolver mContentResolver;
    private String mJobSchedulerConstants;
    private final Resources mResources;
    private final KeyValueListParser mParser = new KeyValueListParser(',');
    private final ArrayMap<String, TareFactorData> mAlarmManagerMap = new ArrayMap<>();
    private final ArrayMap<String, TareFactorData> mJobSchedulerMap = new ArrayMap<>();
    private final ArraySet<DataChangeListener> mDataChangeListeners = new ArraySet<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface DataChangeListener {
        void onDataChanged();
    }

    private TareFactorController(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        this.mContentResolver = contentResolver;
        this.mResources = context.getResources();
        new ConfigObserver(new Handler(Looper.getMainLooper())).start();
        this.mAlarmManagerConstants = Settings.Global.getString(contentResolver, "tare_alarm_manager_constants");
        this.mJobSchedulerConstants = Settings.Global.getString(contentResolver, "tare_job_scheduler_constants");
        initAlarmManagerMap();
        parseAlarmManagerGlobalSettings();
        initJobSchedulerMap();
        parseJobSchedulerGlobalSettings();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TareFactorController getInstance(Context context) {
        synchronized (TareFactorController.class) {
            if (sInstance == null) {
                sInstance = new TareFactorController(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    private void initAlarmManagerMap() {
        this.mAlarmManagerMap.put("am_min_satiated_balance_exempted", new TareFactorData(this.mResources.getString(R$string.tare_min_balance_exempted), EconomyManager.DEFAULT_AM_MIN_SATIATED_BALANCE_EXEMPTED_CAKES, 0));
        this.mAlarmManagerMap.put("am_min_satiated_balance_headless_system_app", new TareFactorData(this.mResources.getString(R$string.tare_min_balance_headless_app), EconomyManager.DEFAULT_AM_MIN_SATIATED_BALANCE_HEADLESS_SYSTEM_APP_CAKES, 0));
        this.mAlarmManagerMap.put("am_min_satiated_balance_other_app", new TareFactorData(this.mResources.getString(R$string.tare_min_balance_other_app), EconomyManager.DEFAULT_AM_MIN_SATIATED_BALANCE_OTHER_APP_CAKES, 0));
        this.mAlarmManagerMap.put("am_max_satiated_balance", new TareFactorData(this.mResources.getString(R$string.tare_max_satiated_balance), EconomyManager.DEFAULT_AM_MAX_SATIATED_BALANCE_CAKES, 0));
        this.mAlarmManagerMap.put("am_initial_consumption_limit", new TareFactorData(this.mResources.getString(R$string.tare_initial_consumption_limit), EconomyManager.DEFAULT_AM_INITIAL_CONSUMPTION_LIMIT_CAKES, 0));
        this.mAlarmManagerMap.put("am_hard_consumption_limit", new TareFactorData(this.mResources.getString(R$string.tare_hard_consumption_limit), EconomyManager.DEFAULT_AM_HARD_CONSUMPTION_LIMIT_CAKES, 0));
        ArrayMap<String, TareFactorData> arrayMap = this.mAlarmManagerMap;
        Resources resources = this.mResources;
        int i = R$string.tare_top_activity;
        arrayMap.put("am_reward_top_activity_instant", new TareFactorData(resources.getString(i), EconomyManager.DEFAULT_AM_REWARD_TOP_ACTIVITY_INSTANT_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_top_activity_ongoing", new TareFactorData(this.mResources.getString(i), 10000000L, 0));
        this.mAlarmManagerMap.put("am_reward_top_activity_max", new TareFactorData(this.mResources.getString(i), EconomyManager.DEFAULT_AM_REWARD_TOP_ACTIVITY_MAX_CAKES, 0));
        ArrayMap<String, TareFactorData> arrayMap2 = this.mAlarmManagerMap;
        Resources resources2 = this.mResources;
        int i2 = R$string.tare_notification_seen;
        arrayMap2.put("am_reward_notification_seen_instant", new TareFactorData(resources2.getString(i2), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_SEEN_INSTANT_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_notification_seen_ongoing", new TareFactorData(this.mResources.getString(i2), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_SEEN_ONGOING_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_notification_seen_max", new TareFactorData(this.mResources.getString(i2), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_SEEN_MAX_CAKES, 0));
        ArrayMap<String, TareFactorData> arrayMap3 = this.mAlarmManagerMap;
        Resources resources3 = this.mResources;
        int i3 = R$string.tare_notification_seen_15_min;
        arrayMap3.put("am_reward_notification_seen_within_15_instant", new TareFactorData(resources3.getString(i3), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_SEEN_WITHIN_15_INSTANT_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_notification_seen_within_15_ongoing", new TareFactorData(this.mResources.getString(i3), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_SEEN_WITHIN_15_ONGOING_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_notification_seen_within_15_max", new TareFactorData(this.mResources.getString(i3), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_SEEN_WITHIN_15_MAX_CAKES, 0));
        ArrayMap<String, TareFactorData> arrayMap4 = this.mAlarmManagerMap;
        Resources resources4 = this.mResources;
        int i4 = R$string.tare_notification_interaction;
        arrayMap4.put("am_reward_notification_interaction_instant", new TareFactorData(resources4.getString(i4), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_INTERACTION_INSTANT_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_notification_interaction_ongoing", new TareFactorData(this.mResources.getString(i4), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_INTERACTION_ONGOING_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_notification_interaction_max", new TareFactorData(this.mResources.getString(i4), EconomyManager.DEFAULT_AM_REWARD_NOTIFICATION_INTERACTION_MAX_CAKES, 0));
        ArrayMap<String, TareFactorData> arrayMap5 = this.mAlarmManagerMap;
        Resources resources5 = this.mResources;
        int i5 = R$string.tare_widget_interaction;
        arrayMap5.put("am_reward_widget_interaction_instant", new TareFactorData(resources5.getString(i5), EconomyManager.DEFAULT_AM_REWARD_WIDGET_INTERACTION_INSTANT_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_widget_interaction_ongoing", new TareFactorData(this.mResources.getString(i5), EconomyManager.DEFAULT_AM_REWARD_WIDGET_INTERACTION_ONGOING_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_widget_interaction_max", new TareFactorData(this.mResources.getString(i5), EconomyManager.DEFAULT_AM_REWARD_WIDGET_INTERACTION_MAX_CAKES, 0));
        ArrayMap<String, TareFactorData> arrayMap6 = this.mAlarmManagerMap;
        Resources resources6 = this.mResources;
        int i6 = R$string.tare_other_interaction;
        arrayMap6.put("am_reward_other_user_interaction_instant", new TareFactorData(resources6.getString(i6), EconomyManager.DEFAULT_AM_REWARD_OTHER_USER_INTERACTION_INSTANT_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_other_user_interaction_ongoing", new TareFactorData(this.mResources.getString(i6), EconomyManager.DEFAULT_AM_REWARD_OTHER_USER_INTERACTION_ONGOING_CAKES, 0));
        this.mAlarmManagerMap.put("am_reward_other_user_interaction_max", new TareFactorData(this.mResources.getString(i6), EconomyManager.DEFAULT_AM_REWARD_OTHER_USER_INTERACTION_MAX_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_exact_wakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_wakeup_exact_idle), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALLOW_WHILE_IDLE_EXACT_WAKEUP_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_inexact_wakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_wakeup_inexact_idle), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALLOW_WHILE_IDLE_INEXACT_WAKEUP_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_exact_wakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_wakeup_exact), EconomyManager.DEFAULT_AM_ACTION_ALARM_EXACT_WAKEUP_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_inexact_wakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_wakeup_inexact), EconomyManager.DEFAULT_AM_ACTION_ALARM_INEXACT_WAKEUP_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_exact_nonwakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_nonwakeup_exact_idle), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALLOW_WHILE_IDLE_EXACT_NONWAKEUP_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_exact_nonwakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_nonwakeup_exact), EconomyManager.DEFAULT_AM_ACTION_ALARM_EXACT_NONWAKEUP_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_inexact_nonwakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_nonwakeup_inexact_idle), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALLOW_WHILE_IDLE_INEXACT_NONWAKEUP_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_inexact_nonwakeup_ctp", new TareFactorData(this.mResources.getString(R$string.tare_nonwakeup_inexact), EconomyManager.DEFAULT_AM_ACTION_ALARM_INEXACT_NONWAKEUP_CTP_CAKES, 0));
        ArrayMap<String, TareFactorData> arrayMap7 = this.mAlarmManagerMap;
        Resources resources7 = this.mResources;
        int i7 = R$string.tare_alarm_clock;
        arrayMap7.put("am_action_alarm_alarmclock_ctp", new TareFactorData(resources7.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALARMCLOCK_CTP_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_exact_wakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALLOW_WHILE_IDLE_EXACT_WAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_inexact_wakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALLOW_WHILE_IDLE_INEXACT_WAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_exact_wakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_EXACT_WAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_inexact_wakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_EXACT_WAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_exact_nonwakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_EXACT_WAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_exact_nonwakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_EXACT_WAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_allow_while_idle_inexact_nonwakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALLOW_WHILE_IDLE_INEXACT_NONWAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_inexact_nonwakeup_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_INEXACT_NONWAKEUP_BASE_PRICE_CAKES, 0));
        this.mAlarmManagerMap.put("am_action_alarm_alarmclock_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_AM_ACTION_ALARM_ALARMCLOCK_BASE_PRICE_CAKES, 0));
    }

    private void initJobSchedulerMap() {
        this.mJobSchedulerMap.put("js_min_satiated_balance_exempted", new TareFactorData(this.mResources.getString(R$string.tare_min_balance_exempted), EconomyManager.DEFAULT_JS_MIN_SATIATED_BALANCE_EXEMPTED_CAKES, 1));
        this.mJobSchedulerMap.put("js_min_satiated_balance_headless_system_app", new TareFactorData(this.mResources.getString(R$string.tare_min_balance_headless_app), EconomyManager.DEFAULT_JS_MIN_SATIATED_BALANCE_HEADLESS_SYSTEM_APP_CAKES, 1));
        this.mJobSchedulerMap.put("js_min_satiated_balance_other_app", new TareFactorData(this.mResources.getString(R$string.tare_min_balance_other_app), EconomyManager.DEFAULT_JS_MIN_SATIATED_BALANCE_OTHER_APP_CAKES, 1));
        this.mJobSchedulerMap.put("js_max_satiated_balance", new TareFactorData(this.mResources.getString(R$string.tare_max_satiated_balance), EconomyManager.DEFAULT_JS_MAX_SATIATED_BALANCE_CAKES, 1));
        this.mJobSchedulerMap.put("js_initial_consumption_limit", new TareFactorData(this.mResources.getString(R$string.tare_initial_consumption_limit), EconomyManager.DEFAULT_JS_INITIAL_CONSUMPTION_LIMIT_CAKES, 1));
        this.mJobSchedulerMap.put("js_hard_consumption_limit", new TareFactorData(this.mResources.getString(R$string.tare_hard_consumption_limit), EconomyManager.DEFAULT_JS_HARD_CONSUMPTION_LIMIT_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap = this.mJobSchedulerMap;
        Resources resources = this.mResources;
        int i = R$string.tare_top_activity;
        arrayMap.put("js_reward_top_activity_instant", new TareFactorData(resources.getString(i), EconomyManager.DEFAULT_JS_REWARD_TOP_ACTIVITY_INSTANT_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_top_activity_ongoing", new TareFactorData(this.mResources.getString(i), 500000000L, 1));
        this.mJobSchedulerMap.put("js_reward_top_activity_max", new TareFactorData(this.mResources.getString(i), EconomyManager.DEFAULT_JS_REWARD_TOP_ACTIVITY_MAX_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap2 = this.mJobSchedulerMap;
        Resources resources2 = this.mResources;
        int i2 = R$string.tare_notification_seen;
        arrayMap2.put("js_reward_notification_seen_instant", new TareFactorData(resources2.getString(i2), EconomyManager.DEFAULT_JS_REWARD_NOTIFICATION_SEEN_INSTANT_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_notification_seen_ongoing", new TareFactorData(this.mResources.getString(i2), EconomyManager.DEFAULT_JS_REWARD_NOTIFICATION_SEEN_ONGOING_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_notification_seen_max", new TareFactorData(this.mResources.getString(i2), EconomyManager.DEFAULT_JS_REWARD_NOTIFICATION_SEEN_MAX_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap3 = this.mJobSchedulerMap;
        Resources resources3 = this.mResources;
        int i3 = R$string.tare_notification_interaction;
        arrayMap3.put("js_reward_notification_interaction_instant", new TareFactorData(resources3.getString(i3), EconomyManager.DEFAULT_JS_REWARD_NOTIFICATION_INTERACTION_INSTANT_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_notification_interaction_ongoing", new TareFactorData(this.mResources.getString(i3), EconomyManager.DEFAULT_JS_REWARD_NOTIFICATION_INTERACTION_ONGOING_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_notification_interaction_max", new TareFactorData(this.mResources.getString(i3), EconomyManager.DEFAULT_JS_REWARD_NOTIFICATION_INTERACTION_MAX_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap4 = this.mJobSchedulerMap;
        Resources resources4 = this.mResources;
        int i4 = R$string.tare_widget_interaction;
        arrayMap4.put("js_reward_widget_interaction_instant", new TareFactorData(resources4.getString(i4), EconomyManager.DEFAULT_JS_REWARD_WIDGET_INTERACTION_INSTANT_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_widget_interaction_ongoing", new TareFactorData(this.mResources.getString(i4), EconomyManager.DEFAULT_JS_REWARD_WIDGET_INTERACTION_ONGOING_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_widget_interaction_max", new TareFactorData(this.mResources.getString(i4), EconomyManager.DEFAULT_JS_REWARD_WIDGET_INTERACTION_MAX_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap5 = this.mJobSchedulerMap;
        Resources resources5 = this.mResources;
        int i5 = R$string.tare_other_interaction;
        arrayMap5.put("js_reward_other_user_interaction_instant", new TareFactorData(resources5.getString(i5), EconomyManager.DEFAULT_JS_REWARD_OTHER_USER_INTERACTION_INSTANT_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_other_user_interaction_ongoing", new TareFactorData(this.mResources.getString(i5), EconomyManager.DEFAULT_JS_REWARD_OTHER_USER_INTERACTION_ONGOING_CAKES, 1));
        this.mJobSchedulerMap.put("js_reward_other_user_interaction_max", new TareFactorData(this.mResources.getString(i5), EconomyManager.DEFAULT_JS_REWARD_OTHER_USER_INTERACTION_MAX_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap6 = this.mJobSchedulerMap;
        Resources resources6 = this.mResources;
        int i6 = R$string.tare_job_max_start;
        arrayMap6.put("js_action_job_max_start_ctp", new TareFactorData(resources6.getString(i6), EconomyManager.DEFAULT_JS_ACTION_JOB_MAX_START_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap7 = this.mJobSchedulerMap;
        Resources resources7 = this.mResources;
        int i7 = R$string.tare_job_max_running;
        arrayMap7.put("js_action_job_max_running_ctp", new TareFactorData(resources7.getString(i7), EconomyManager.DEFAULT_JS_ACTION_JOB_MAX_RUNNING_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap8 = this.mJobSchedulerMap;
        Resources resources8 = this.mResources;
        int i8 = R$string.tare_job_high_start;
        arrayMap8.put("js_action_job_high_start_ctp", new TareFactorData(resources8.getString(i8), EconomyManager.DEFAULT_JS_ACTION_JOB_HIGH_START_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap9 = this.mJobSchedulerMap;
        Resources resources9 = this.mResources;
        int i9 = R$string.tare_job_high_running;
        arrayMap9.put("js_action_job_high_running_ctp", new TareFactorData(resources9.getString(i9), EconomyManager.DEFAULT_JS_ACTION_JOB_HIGH_RUNNING_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap10 = this.mJobSchedulerMap;
        Resources resources10 = this.mResources;
        int i10 = R$string.tare_job_default_start;
        arrayMap10.put("js_action_job_default_start_ctp", new TareFactorData(resources10.getString(i10), EconomyManager.DEFAULT_JS_ACTION_JOB_DEFAULT_START_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap11 = this.mJobSchedulerMap;
        Resources resources11 = this.mResources;
        int i11 = R$string.tare_job_default_running;
        arrayMap11.put("js_action_job_default_running_ctp", new TareFactorData(resources11.getString(i11), EconomyManager.DEFAULT_JS_ACTION_JOB_DEFAULT_RUNNING_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap12 = this.mJobSchedulerMap;
        Resources resources12 = this.mResources;
        int i12 = R$string.tare_job_low_start;
        arrayMap12.put("js_action_job_low_start_ctp", new TareFactorData(resources12.getString(i12), EconomyManager.DEFAULT_JS_ACTION_JOB_LOW_START_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap13 = this.mJobSchedulerMap;
        Resources resources13 = this.mResources;
        int i13 = R$string.tare_job_low_running;
        arrayMap13.put("js_action_job_low_running_ctp", new TareFactorData(resources13.getString(i13), EconomyManager.DEFAULT_JS_ACTION_JOB_LOW_RUNNING_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap14 = this.mJobSchedulerMap;
        Resources resources14 = this.mResources;
        int i14 = R$string.tare_job_min_start;
        arrayMap14.put("js_action_job_min_start_ctp", new TareFactorData(resources14.getString(i14), EconomyManager.DEFAULT_JS_ACTION_JOB_MIN_START_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap15 = this.mJobSchedulerMap;
        Resources resources15 = this.mResources;
        int i15 = R$string.tare_job_min_running;
        arrayMap15.put("js_action_job_min_running_ctp", new TareFactorData(resources15.getString(i15), EconomyManager.DEFAULT_JS_ACTION_JOB_MIN_RUNNING_CTP_CAKES, 1));
        ArrayMap<String, TareFactorData> arrayMap16 = this.mJobSchedulerMap;
        Resources resources16 = this.mResources;
        int i16 = R$string.tare_job_timeout_penalty;
        arrayMap16.put("js_action_job_timeout_penalty_ctp", new TareFactorData(resources16.getString(i16), EconomyManager.DEFAULT_JS_ACTION_JOB_TIMEOUT_PENALTY_CTP_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_max_start_base_price", new TareFactorData(this.mResources.getString(i6), EconomyManager.DEFAULT_JS_ACTION_JOB_MAX_START_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_max_running_base_price", new TareFactorData(this.mResources.getString(i7), EconomyManager.DEFAULT_JS_ACTION_JOB_MAX_RUNNING_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_high_start_base_price", new TareFactorData(this.mResources.getString(i8), EconomyManager.DEFAULT_JS_ACTION_JOB_HIGH_START_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_high_running_base_price", new TareFactorData(this.mResources.getString(i9), EconomyManager.DEFAULT_JS_ACTION_JOB_HIGH_RUNNING_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_default_start_base_price", new TareFactorData(this.mResources.getString(i10), EconomyManager.DEFAULT_JS_ACTION_JOB_DEFAULT_START_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_default_running_base_price", new TareFactorData(this.mResources.getString(i11), EconomyManager.DEFAULT_JS_ACTION_JOB_DEFAULT_RUNNING_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_low_start_base_price", new TareFactorData(this.mResources.getString(i12), EconomyManager.DEFAULT_JS_ACTION_JOB_LOW_START_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_low_running_base_price", new TareFactorData(this.mResources.getString(i13), EconomyManager.DEFAULT_JS_ACTION_JOB_LOW_RUNNING_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_min_start_base_price", new TareFactorData(this.mResources.getString(i14), EconomyManager.DEFAULT_JS_ACTION_JOB_MIN_START_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_min_running_base_price", new TareFactorData(this.mResources.getString(i15), EconomyManager.DEFAULT_JS_ACTION_JOB_MIN_RUNNING_BASE_PRICE_CAKES, 1));
        this.mJobSchedulerMap.put("js_action_job_timeout_penalty_base_price", new TareFactorData(this.mResources.getString(i16), EconomyManager.DEFAULT_JS_ACTION_JOB_TIMEOUT_PENALTY_BASE_PRICE_CAKES, 1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseAlarmManagerGlobalSettings() {
        parseSettingsIntoMap(this.mAlarmManagerConstants, this.mAlarmManagerMap);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseJobSchedulerGlobalSettings() {
        parseSettingsIntoMap(this.mJobSchedulerConstants, this.mJobSchedulerMap);
    }

    private void parseSettingsIntoMap(String str, ArrayMap<String, TareFactorData> arrayMap) {
        try {
            this.mParser.setString(str);
        } catch (Exception e) {
            Slog.e("TareFactorController", "Bad string constants value", e);
        }
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            TareFactorData valueAt = arrayMap.valueAt(size);
            valueAt.currentValue = EconomyManager.parseCreditValue(this.mParser.getString(arrayMap.keyAt(size), (String) null), valueAt.defaultValue);
        }
    }

    private ArrayMap<String, TareFactorData> getMap(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.mJobSchedulerMap;
            }
            throw new IllegalArgumentException("Invalid factor policy given");
        }
        return this.mAlarmManagerMap;
    }

    private String getTitle(String str, int i) {
        return getMap(i).get(str).title;
    }

    private long getCurrentValue(String str, int i) {
        return getMap(i).get(str).currentValue;
    }

    private int getFactorType(String str) {
        ArrayMap<String, TareFactorData> arrayMap;
        if (this.mAlarmManagerMap.containsKey(str)) {
            arrayMap = this.mAlarmManagerMap;
        } else if (this.mJobSchedulerMap.containsKey(str)) {
            arrayMap = this.mJobSchedulerMap;
        } else {
            throw new IllegalArgumentException("Couldn't link key '" + str + "' to a policy");
        }
        return arrayMap.get(str).factorPolicy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getValue(String str) {
        return getCurrentValue(str, getFactorType(str));
    }

    public void updateValue(String str, long j, int i) {
        TareFactorData tareFactorData = getMap(i).get(str);
        if (tareFactorData.currentValue == j) {
            return;
        }
        tareFactorData.currentValue = j;
        rebuildPolicyConstants(i);
    }

    private void rebuildPolicyConstants(int i) {
        if (i == 0) {
            writeConstantsToSettings(this.mAlarmManagerMap, "tare_alarm_manager_constants");
        } else if (i != 1) {
        } else {
            writeConstantsToSettings(this.mJobSchedulerMap, "tare_job_scheduler_constants");
        }
    }

    private void writeConstantsToSettings(ArrayMap<String, TareFactorData> arrayMap, String str) {
        StringBuilder sb = new StringBuilder();
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            TareFactorData valueAt = arrayMap.valueAt(i);
            if (valueAt.currentValue != valueAt.defaultValue) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(arrayMap.keyAt(i));
                sb.append("=");
                long j = valueAt.currentValue;
                if (j % 1000000000 == 0) {
                    sb.append(j / 1000000000);
                    sb.append("A");
                } else {
                    sb.append(j);
                    sb.append("ck");
                }
            }
        }
        Settings.Global.putString(this.mContentResolver, str, sb.toString());
    }

    public TareFactorDialogFragment createDialog(String str) {
        int factorType = getFactorType(str);
        return new TareFactorDialogFragment(getTitle(str, factorType), str, getCurrentValue(str, factorType), factorType, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TareFactorData {
        public long currentValue;
        public final long defaultValue;
        public final int factorPolicy;
        public final String title;

        TareFactorData(String str, long j, int i) {
            this.title = str;
            this.defaultValue = j;
            this.factorPolicy = i;
            this.currentValue = j;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerListener(DataChangeListener dataChangeListener) {
        this.mDataChangeListeners.add(dataChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregisterListener(DataChangeListener dataChangeListener) {
        this.mDataChangeListeners.remove(dataChangeListener);
    }

    void notifyListeners() {
        for (int size = this.mDataChangeListeners.size() - 1; size >= 0; size--) {
            this.mDataChangeListeners.valueAt(size).onDataChanged();
        }
    }

    /* loaded from: classes.dex */
    private class ConfigObserver extends ContentObserver {
        ConfigObserver(Handler handler) {
            super(handler);
        }

        public void start() {
            TareFactorController.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("tare_alarm_manager_constants"), false, this);
            TareFactorController.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("tare_job_scheduler_constants"), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (uri.equals(Settings.Global.getUriFor("tare_alarm_manager_constants"))) {
                TareFactorController tareFactorController = TareFactorController.this;
                tareFactorController.mAlarmManagerConstants = Settings.Global.getString(tareFactorController.mContentResolver, "tare_alarm_manager_constants");
                TareFactorController.this.parseAlarmManagerGlobalSettings();
                TareFactorController.this.notifyListeners();
            } else if (uri.equals(Settings.Global.getUriFor("tare_job_scheduler_constants"))) {
                TareFactorController tareFactorController2 = TareFactorController.this;
                tareFactorController2.mJobSchedulerConstants = Settings.Global.getString(tareFactorController2.mContentResolver, "tare_job_scheduler_constants");
                TareFactorController.this.parseJobSchedulerGlobalSettings();
                TareFactorController.this.notifyListeners();
            }
        }
    }
}
