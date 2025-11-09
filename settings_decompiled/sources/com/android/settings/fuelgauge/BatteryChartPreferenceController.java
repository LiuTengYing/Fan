package com.android.settings.fuelgauge;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.fuelgauge.BatteryChartPreferenceController;
import com.android.settings.fuelgauge.BatteryChartView;
import com.android.settings.fuelgauge.ExpandDividerPreference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class BatteryChartPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnCreate, OnDestroy, OnSaveInstanceState, BatteryChartView.OnSelectListener, OnResume, ExpandDividerPreference.OnExpandListener {
    private static int sUiMode;
    private final SettingsActivity mActivity;
    PreferenceGroup mAppListPrefGroup;
    BatteryChartView mBatteryChartView;
    long[] mBatteryHistoryKeys;
    int[] mBatteryHistoryLevels;
    Map<Integer, List<BatteryDiffEntry>> mBatteryIndexedMap;
    BatteryUtils mBatteryUtils;
    ExpandDividerPreference mExpandDividerPreference;
    private FooterPreference mFooterPreference;
    private final InstrumentedPreferenceFragment mFragment;
    private final Handler mHandler;
    private boolean mIs24HourFormat;
    boolean mIsExpanded;
    private boolean mIsFooterPrefAdded;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final CharSequence[] mNotAllowShowEntryPackages;
    private final CharSequence[] mNotAllowShowSummaryPackages;
    Context mPrefContext;
    final Map<String, Preference> mPreferenceCache;
    private final String mPreferenceKey;
    private PreferenceScreen mPreferenceScreen;
    final List<BatteryDiffEntry> mSystemEntries;
    int mTrapezoidIndex;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public BatteryChartPreferenceController(Context context, String str, Lifecycle lifecycle, SettingsActivity settingsActivity, InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        super(context);
        this.mIsExpanded = false;
        this.mTrapezoidIndex = -2;
        this.mIs24HourFormat = false;
        this.mIsFooterPrefAdded = false;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mPreferenceCache = new HashMap();
        this.mSystemEntries = new ArrayList();
        this.mActivity = settingsActivity;
        this.mFragment = instrumentedPreferenceFragment;
        this.mPreferenceKey = str;
        this.mIs24HourFormat = DateFormat.is24HourFormat(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        this.mNotAllowShowEntryPackages = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).getHideApplicationEntries(context);
        this.mNotAllowShowSummaryPackages = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).getHideApplicationSummary(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        this.mTrapezoidIndex = bundle.getInt("current_time_slot", this.mTrapezoidIndex);
        this.mIsExpanded = bundle.getBoolean("expand_system_info", this.mIsExpanded);
        Log.d("BatteryChartPreferenceController", String.format("onCreate() slotIndex=%d isExpanded=%b", Integer.valueOf(this.mTrapezoidIndex), Boolean.valueOf(this.mIsExpanded)));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        int i = this.mContext.getResources().getConfiguration().uiMode & 48;
        if (sUiMode != i) {
            sUiMode = i;
            BatteryDiffEntry.clearCache();
            Log.d("BatteryChartPreferenceController", "clear icon and label cache since uiMode is changed");
        }
        this.mIs24HourFormat = DateFormat.is24HourFormat(this.mContext);
        this.mMetricsFeatureProvider.action(this.mPrefContext, 1880, new Pair[0]);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        bundle.putInt("current_time_slot", this.mTrapezoidIndex);
        bundle.putBoolean("expand_system_info", this.mIsExpanded);
        Log.d("BatteryChartPreferenceController", String.format("onSaveInstanceState() slotIndex=%d isExpanded=%b", Integer.valueOf(this.mTrapezoidIndex), Boolean.valueOf(this.mIsExpanded)));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        if (this.mActivity.isChangingConfigurations()) {
            BatteryDiffEntry.clearCache();
        }
        this.mHandler.removeCallbacksAndMessages(null);
        this.mPreferenceCache.clear();
        PreferenceGroup preferenceGroup = this.mAppListPrefGroup;
        if (preferenceGroup != null) {
            preferenceGroup.removeAll();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPrefContext = preferenceScreen.getContext();
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mAppListPrefGroup = preferenceGroup;
        preferenceGroup.setOrderingAsAdded(false);
        this.mAppListPrefGroup.setTitle(this.mPrefContext.getString(R$string.battery_app_usage_for_past_24));
        FooterPreference footerPreference = (FooterPreference) preferenceScreen.findPreference("battery_graph_footer");
        this.mFooterPreference = footerPreference;
        if (footerPreference != null) {
            preferenceScreen.removePreference(footerPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference instanceof PowerGaugePreference) {
            PowerGaugePreference powerGaugePreference = (PowerGaugePreference) preference;
            BatteryDiffEntry batteryDiffEntry = powerGaugePreference.getBatteryDiffEntry();
            BatteryHistEntry batteryHistEntry = batteryDiffEntry.mBatteryHistEntry;
            String str = batteryHistEntry.mPackageName;
            this.mMetricsFeatureProvider.action(1880, batteryHistEntry.isAppEntry() ? 1768 : 1769, 1880, TextUtils.isEmpty(str) ? "none" : str, (int) Math.round(batteryDiffEntry.getPercentOfTotal()));
            Log.d("BatteryChartPreferenceController", String.format("handleClick() label=%s key=%s package=%s", batteryDiffEntry.getAppLabel(), batteryHistEntry.getKey(), batteryHistEntry.mPackageName));
            AdvancedPowerUsageDetail.startBatteryDetailPage(this.mActivity, this.mFragment, batteryDiffEntry, powerGaugePreference.getPercent(), isValidToShowSummary(str), getSlotInformation());
            return true;
        }
        return false;
    }

    @Override // com.android.settings.fuelgauge.BatteryChartView.OnSelectListener
    public void onSelect(int i) {
        Log.d("BatteryChartPreferenceController", "onChartSelect:" + i);
        refreshUi(i, false);
        this.mMetricsFeatureProvider.action(this.mPrefContext, i == -1 ? 1767 : 1766, new Pair[0]);
    }

    @Override // com.android.settings.fuelgauge.ExpandDividerPreference.OnExpandListener
    public void onExpand(boolean z) {
        this.mIsExpanded = z;
        this.mMetricsFeatureProvider.action(this.mPrefContext, 1770, z);
        refreshExpandUi();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBatteryHistoryMap(Map<Long, Map<String, BatteryHistEntry>> map) {
        long j;
        Iterator<BatteryHistEntry> it;
        if (map == null || map.isEmpty()) {
            this.mBatteryIndexedMap = null;
            this.mBatteryHistoryKeys = null;
            this.mBatteryHistoryLevels = null;
            addFooterPreferenceIfNeeded(false);
            return;
        }
        this.mBatteryHistoryKeys = getBatteryHistoryKeys(map);
        this.mBatteryHistoryLevels = new int[13];
        for (int i = 0; i < 13; i++) {
            Map<String, BatteryHistEntry> map2 = map.get(Long.valueOf(this.mBatteryHistoryKeys[i * 2]));
            if (map2 == null || map2.isEmpty()) {
                Log.e("BatteryChartPreferenceController", "abnormal entry list in the timestamp:" + ConvertUtils.utcToLocalTime(this.mPrefContext, j));
            } else {
                float f = 0.0f;
                while (map2.values().iterator().hasNext()) {
                    f += it.next().mBatteryLevel;
                }
                this.mBatteryHistoryLevels[i] = Math.round(f / map2.size());
            }
        }
        forceRefreshUi();
        Context context = this.mPrefContext;
        long[] jArr = this.mBatteryHistoryKeys;
        Log.d("BatteryChartPreferenceController", String.format("setBatteryHistoryMap() size=%d key=%s\nlevels=%s", Integer.valueOf(map.size()), ConvertUtils.utcToLocalTime(context, jArr[jArr.length - 1]), Arrays.toString(this.mBatteryHistoryLevels)));
        new LoadAllItemsInfoTask(map).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBatteryChartView(final BatteryChartView batteryChartView) {
        if (this.mBatteryChartView != batteryChartView) {
            this.mHandler.post(new Runnable() { // from class: com.android.settings.fuelgauge.BatteryChartPreferenceController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BatteryChartPreferenceController.this.lambda$setBatteryChartView$0(batteryChartView);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: setBatteryChartViewInner */
    public void lambda$setBatteryChartView$0(BatteryChartView batteryChartView) {
        this.mBatteryChartView = batteryChartView;
        batteryChartView.setOnSelectListener(this);
        forceRefreshUi();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceRefreshUi() {
        int i = this.mTrapezoidIndex;
        if (i == -2) {
            i = -1;
        }
        BatteryChartView batteryChartView = this.mBatteryChartView;
        if (batteryChartView != null) {
            batteryChartView.setLevels(this.mBatteryHistoryLevels);
            this.mBatteryChartView.setSelectedIndex(i);
            setTimestampLabel();
        }
        refreshUi(i, true);
    }

    boolean refreshUi(int i, boolean z) {
        if (this.mBatteryIndexedMap == null || this.mBatteryChartView == null || (this.mTrapezoidIndex == i && !z)) {
            return false;
        }
        Log.d("BatteryChartPreferenceController", String.format("refreshUi: index=%d size=%d isForce:%b", Integer.valueOf(i), Integer.valueOf(this.mBatteryIndexedMap.size()), Boolean.valueOf(z)));
        this.mTrapezoidIndex = i;
        this.mHandler.post(new Runnable() { // from class: com.android.settings.fuelgauge.BatteryChartPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                BatteryChartPreferenceController.this.lambda$refreshUi$1();
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshUi$1() {
        long currentTimeMillis = System.currentTimeMillis();
        removeAndCacheAllPrefs();
        addAllPreferences();
        refreshCategoryTitle();
        Log.d("BatteryChartPreferenceController", String.format("refreshUi is finished in %d/ms", Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
    }

    private void addAllPreferences() {
        List<BatteryDiffEntry> list = this.mBatteryIndexedMap.get(Integer.valueOf(this.mTrapezoidIndex));
        addFooterPreferenceIfNeeded((list == null || list.isEmpty()) ? false : true);
        if (list == null) {
            Log.w("BatteryChartPreferenceController", "cannot find BatteryDiffEntry for:" + this.mTrapezoidIndex);
            return;
        }
        final ArrayList arrayList = new ArrayList();
        this.mSystemEntries.clear();
        list.forEach(new Consumer() { // from class: com.android.settings.fuelgauge.BatteryChartPreferenceController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                BatteryChartPreferenceController.this.lambda$addAllPreferences$2(arrayList, (BatteryDiffEntry) obj);
            }
        });
        Comparator<BatteryDiffEntry> comparator = BatteryDiffEntry.COMPARATOR;
        Collections.sort(arrayList, comparator);
        Collections.sort(this.mSystemEntries, comparator);
        Log.d("BatteryChartPreferenceController", String.format("addAllPreferences() app=%d system=%d", Integer.valueOf(arrayList.size()), Integer.valueOf(this.mSystemEntries.size())));
        if (!arrayList.isEmpty()) {
            addPreferenceToScreen(arrayList);
        }
        if (!this.mSystemEntries.isEmpty()) {
            if (this.mExpandDividerPreference == null) {
                ExpandDividerPreference expandDividerPreference = new ExpandDividerPreference(this.mPrefContext);
                this.mExpandDividerPreference = expandDividerPreference;
                expandDividerPreference.setOnExpandListener(this);
                this.mExpandDividerPreference.setIsExpanded(this.mIsExpanded);
            }
            this.mExpandDividerPreference.setOrder(this.mAppListPrefGroup.getPreferenceCount());
            this.mAppListPrefGroup.addPreference(this.mExpandDividerPreference);
        }
        refreshExpandUi();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addAllPreferences$2(List list, BatteryDiffEntry batteryDiffEntry) {
        String packageName = batteryDiffEntry.getPackageName();
        if (!isValidToShowEntry(packageName)) {
            Log.w("BatteryChartPreferenceController", "ignore showing item:" + packageName);
            return;
        }
        if (batteryDiffEntry.isSystemEntry()) {
            this.mSystemEntries.add(batteryDiffEntry);
        } else {
            list.add(batteryDiffEntry);
        }
        if (this.mTrapezoidIndex >= 0) {
            validateUsageTime(batteryDiffEntry);
        }
    }

    void addPreferenceToScreen(List<BatteryDiffEntry> list) {
        if (this.mAppListPrefGroup == null || list.isEmpty()) {
            return;
        }
        int preferenceCount = this.mAppListPrefGroup.getPreferenceCount();
        for (BatteryDiffEntry batteryDiffEntry : list) {
            boolean z = false;
            String appLabel = batteryDiffEntry.getAppLabel();
            Drawable appIcon = batteryDiffEntry.getAppIcon();
            if (TextUtils.isEmpty(appLabel) || appIcon == null) {
                Log.w("BatteryChartPreferenceController", "cannot find app resource for:" + batteryDiffEntry.getPackageName());
            } else {
                String key = batteryDiffEntry.mBatteryHistEntry.getKey();
                PowerGaugePreference powerGaugePreference = (PowerGaugePreference) this.mAppListPrefGroup.findPreference(key);
                if (powerGaugePreference != null) {
                    Log.w("BatteryChartPreferenceController", "preference should be removed for:" + batteryDiffEntry.getPackageName());
                    z = true;
                } else {
                    powerGaugePreference = (PowerGaugePreference) this.mPreferenceCache.get(key);
                }
                if (powerGaugePreference == null) {
                    powerGaugePreference = new PowerGaugePreference(this.mPrefContext);
                    powerGaugePreference.setKey(key);
                    this.mPreferenceCache.put(key, powerGaugePreference);
                }
                powerGaugePreference.setIcon(appIcon);
                powerGaugePreference.setTitle(appLabel);
                powerGaugePreference.setOrder(preferenceCount);
                powerGaugePreference.setPercent(batteryDiffEntry.getPercentOfTotal());
                powerGaugePreference.setSingleLineTitle(true);
                powerGaugePreference.setBatteryDiffEntry(batteryDiffEntry);
                powerGaugePreference.setEnabled(batteryDiffEntry.validForRestriction());
                setPreferenceSummary(powerGaugePreference, batteryDiffEntry);
                if (!z) {
                    this.mAppListPrefGroup.addPreference(powerGaugePreference);
                }
                preferenceCount++;
            }
        }
    }

    private void removeAndCacheAllPrefs() {
        PreferenceGroup preferenceGroup = this.mAppListPrefGroup;
        if (preferenceGroup == null || preferenceGroup.getPreferenceCount() == 0) {
            return;
        }
        int preferenceCount = this.mAppListPrefGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = this.mAppListPrefGroup.getPreference(i);
            if (!TextUtils.isEmpty(preference.getKey())) {
                this.mPreferenceCache.put(preference.getKey(), preference);
            }
        }
        this.mAppListPrefGroup.removeAll();
    }

    private void refreshExpandUi() {
        if (this.mIsExpanded) {
            addPreferenceToScreen(this.mSystemEntries);
            return;
        }
        for (BatteryDiffEntry batteryDiffEntry : this.mSystemEntries) {
            Preference findPreference = this.mAppListPrefGroup.findPreference(batteryDiffEntry.mBatteryHistEntry.getKey());
            if (findPreference != null) {
                this.mAppListPrefGroup.removePreference(findPreference);
                this.mPreferenceCache.put(findPreference.getKey(), findPreference);
            }
        }
    }

    void refreshCategoryTitle() {
        String slotInformation = getSlotInformation();
        Log.d("BatteryChartPreferenceController", String.format("refreshCategoryTitle:%s", slotInformation));
        PreferenceGroup preferenceGroup = this.mAppListPrefGroup;
        if (preferenceGroup != null) {
            preferenceGroup.setTitle(getSlotInformation(true, slotInformation));
        }
        ExpandDividerPreference expandDividerPreference = this.mExpandDividerPreference;
        if (expandDividerPreference != null) {
            expandDividerPreference.setTitle(getSlotInformation(false, slotInformation));
        }
    }

    private String getSlotInformation(boolean z, String str) {
        if (str != null) {
            return z ? this.mPrefContext.getString(R$string.battery_app_usage_for, str) : this.mPrefContext.getString(R$string.battery_system_usage_for, str);
        } else if (z) {
            return this.mPrefContext.getString(R$string.battery_app_usage_for_past_24);
        } else {
            return this.mPrefContext.getString(R$string.battery_system_usage_for_past_24);
        }
    }

    private String getSlotInformation() {
        int i = this.mTrapezoidIndex;
        if (i < 0) {
            return null;
        }
        String utcToLocalTimeHour = ConvertUtils.utcToLocalTimeHour(this.mPrefContext, this.mBatteryHistoryKeys[i * 2], this.mIs24HourFormat);
        String utcToLocalTimeHour2 = ConvertUtils.utcToLocalTimeHour(this.mPrefContext, this.mBatteryHistoryKeys[(this.mTrapezoidIndex + 1) * 2], this.mIs24HourFormat);
        if (this.mIs24HourFormat) {
            return String.format("%s–%s", utcToLocalTimeHour, utcToLocalTimeHour2);
        }
        return String.format("%s – %s", utcToLocalTimeHour, utcToLocalTimeHour2);
    }

    void setPreferenceSummary(PowerGaugePreference powerGaugePreference, BatteryDiffEntry batteryDiffEntry) {
        long j = batteryDiffEntry.mForegroundUsageTimeInMs;
        long j2 = batteryDiffEntry.mBackgroundUsageTimeInMs;
        long j3 = j + j2;
        String str = null;
        if (!isValidToShowSummary(batteryDiffEntry.getPackageName())) {
            powerGaugePreference.setSummary((CharSequence) null);
            return;
        }
        if (j3 == 0) {
            powerGaugePreference.setSummary((CharSequence) null);
        } else if (j == 0 && j2 != 0) {
            str = buildUsageTimeInfo(j2, true);
        } else if (j3 < 60000) {
            str = buildUsageTimeInfo(j3, false);
        } else {
            str = buildUsageTimeInfo(j3, false);
            if (j2 > 0) {
                str = str + "\n" + buildUsageTimeInfo(j2, true);
            }
        }
        powerGaugePreference.setSummary(str);
    }

    private String buildUsageTimeInfo(long j, boolean z) {
        int i;
        int i2;
        if (j < 60000) {
            Context context = this.mPrefContext;
            if (z) {
                i2 = R$string.battery_usage_background_less_than_one_minute;
            } else {
                i2 = R$string.battery_usage_total_less_than_one_minute;
            }
            return context.getString(i2);
        }
        CharSequence formatElapsedTime = StringUtil.formatElapsedTime(this.mPrefContext, j, false, false);
        if (z) {
            i = R$string.battery_usage_for_background_time;
        } else {
            i = R$string.battery_usage_for_total_time;
        }
        return this.mPrefContext.getString(i, formatElapsedTime);
    }

    boolean isValidToShowSummary(String str) {
        return !contains(str, this.mNotAllowShowSummaryPackages);
    }

    boolean isValidToShowEntry(String str) {
        return !contains(str, this.mNotAllowShowEntryPackages);
    }

    void setTimestampLabel() {
        long[] jArr;
        BatteryChartView batteryChartView = this.mBatteryChartView;
        if (batteryChartView == null || (jArr = this.mBatteryHistoryKeys) == null) {
            return;
        }
        batteryChartView.setLatestTimestamp(jArr[jArr.length - 1]);
    }

    private void addFooterPreferenceIfNeeded(boolean z) {
        FooterPreference footerPreference;
        int i;
        if (this.mIsFooterPrefAdded || (footerPreference = this.mFooterPreference) == null) {
            return;
        }
        this.mIsFooterPrefAdded = true;
        Context context = this.mPrefContext;
        if (z) {
            i = R$string.battery_usage_screen_footer;
        } else {
            i = R$string.battery_usage_screen_footer_empty;
        }
        footerPreference.setTitle(context.getString(i));
        this.mHandler.post(new Runnable() { // from class: com.android.settings.fuelgauge.BatteryChartPreferenceController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                BatteryChartPreferenceController.this.lambda$addFooterPreferenceIfNeeded$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addFooterPreferenceIfNeeded$3() {
        this.mPreferenceScreen.addPreference(this.mFooterPreference);
    }

    private static boolean contains(String str, CharSequence[] charSequenceArr) {
        if (str != null && charSequenceArr != null) {
            for (CharSequence charSequence : charSequenceArr) {
                if (TextUtils.equals(str, charSequence)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean validateUsageTime(BatteryDiffEntry batteryDiffEntry) {
        long j = batteryDiffEntry.mForegroundUsageTimeInMs;
        long j2 = batteryDiffEntry.mBackgroundUsageTimeInMs;
        long j3 = j + j2;
        if (j > 7200000 || j2 > 7200000 || j3 > 7200000) {
            Log.e("BatteryChartPreferenceController", "validateUsageTime() fail for\n" + batteryDiffEntry);
            return false;
        }
        return true;
    }

    public static List<BatteryDiffEntry> getBatteryLast24HrUsageData(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        Map<Long, Map<String, BatteryHistEntry>> batteryHistory = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).getBatteryHistory(context);
        if (batteryHistory == null || batteryHistory.isEmpty()) {
            return null;
        }
        Log.d("BatteryChartPreferenceController", String.format("getBatteryLast24HrData() size=%d time=&d/ms", Integer.valueOf(batteryHistory.size()), Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
        return ConvertUtils.getIndexedUsageMap(context, 12, getBatteryHistoryKeys(batteryHistory), batteryHistory, true).get(-1);
    }

    public static BatteryDiffEntry getBatteryLast24HrUsageData(Context context, String str, int i) {
        List<BatteryDiffEntry> batteryLast24HrUsageData;
        if (str == null || (batteryLast24HrUsageData = getBatteryLast24HrUsageData(context)) == null) {
            return null;
        }
        for (BatteryDiffEntry batteryDiffEntry : batteryLast24HrUsageData) {
            BatteryHistEntry batteryHistEntry = batteryDiffEntry.mBatteryHistEntry;
            if (batteryHistEntry != null && batteryHistEntry.mConsumerType == 1 && batteryHistEntry.mUserId == i && str.equals(batteryDiffEntry.getPackageName())) {
                return batteryDiffEntry;
            }
        }
        return null;
    }

    private static long[] getBatteryHistoryKeys(Map<Long, Map<String, BatteryHistEntry>> map) {
        ArrayList arrayList = new ArrayList(map.keySet());
        Collections.sort(arrayList);
        long[] jArr = new long[25];
        for (int i = 0; i < 25; i++) {
            jArr[i] = ((Long) arrayList.get(i)).longValue();
        }
        return jArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class LoadAllItemsInfoTask extends AsyncTask<Void, Void, Map<Integer, List<BatteryDiffEntry>>> {
        private long[] mBatteryHistoryKeysCache;
        private Map<Long, Map<String, BatteryHistEntry>> mBatteryHistoryMap;

        private LoadAllItemsInfoTask(Map<Long, Map<String, BatteryHistEntry>> map) {
            this.mBatteryHistoryMap = map;
            this.mBatteryHistoryKeysCache = BatteryChartPreferenceController.this.mBatteryHistoryKeys;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Map<Integer, List<BatteryDiffEntry>> doInBackground(Void... voidArr) {
            if (BatteryChartPreferenceController.this.mPrefContext == null || this.mBatteryHistoryKeysCache == null) {
                return null;
            }
            long currentTimeMillis = System.currentTimeMillis();
            Map<Integer, List<BatteryDiffEntry>> indexedUsageMap = ConvertUtils.getIndexedUsageMap(BatteryChartPreferenceController.this.mPrefContext, 12, this.mBatteryHistoryKeysCache, this.mBatteryHistoryMap, true);
            for (List<BatteryDiffEntry> list : indexedUsageMap.values()) {
                list.forEach(new Consumer() { // from class: com.android.settings.fuelgauge.BatteryChartPreferenceController$LoadAllItemsInfoTask$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((BatteryDiffEntry) obj).loadLabelAndIcon();
                    }
                });
            }
            Log.d("BatteryChartPreferenceController", String.format("execute LoadAllItemsInfoTask in %d/ms", Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
            return indexedUsageMap;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(final Map<Integer, List<BatteryDiffEntry>> map) {
            this.mBatteryHistoryMap = null;
            this.mBatteryHistoryKeysCache = null;
            if (map == null) {
                return;
            }
            BatteryChartPreferenceController.this.mHandler.post(new Runnable() { // from class: com.android.settings.fuelgauge.BatteryChartPreferenceController$LoadAllItemsInfoTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BatteryChartPreferenceController.LoadAllItemsInfoTask.this.lambda$onPostExecute$1(map);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$1(Map map) {
            BatteryChartPreferenceController batteryChartPreferenceController = BatteryChartPreferenceController.this;
            batteryChartPreferenceController.mBatteryIndexedMap = map;
            batteryChartPreferenceController.forceRefreshUi();
        }
    }
}
