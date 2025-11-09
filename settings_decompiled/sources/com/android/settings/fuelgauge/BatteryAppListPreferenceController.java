package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.BatteryConsumer;
import android.os.BatteryUsageStats;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UidBatteryConsumer;
import android.os.UserBatteryConsumer;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.internal.os.PowerProfile;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.utils.StringUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class BatteryAppListPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnPause, OnDestroy {
    static final boolean USE_FAKE_DATA = false;
    static Config sConfig = new Config() { // from class: com.android.settings.fuelgauge.BatteryAppListPreferenceController.1
        @Override // com.android.settings.fuelgauge.BatteryAppListPreferenceController.Config
        public boolean shouldShowBatteryAttributionList(Context context) {
            double averagePowerForOrdinal = new PowerProfile(context).getAveragePowerForOrdinal("screen.full.display", 0);
            boolean z = averagePowerForOrdinal >= 10.0d;
            if (!z) {
                Log.w("BatteryAppListPreferenceController", "shouldShowBatteryAttributionList(): " + averagePowerForOrdinal);
            }
            return z;
        }
    };
    private final SettingsActivity mActivity;
    PreferenceGroup mAppListGroup;
    private BatteryUsageStats mBatteryUsageStats;
    BatteryUtils mBatteryUtils;
    private final InstrumentedPreferenceFragment mFragment;
    private final Handler mHandler;
    private final Set<CharSequence> mNotAllowShowSummaryPackages;
    private final PackageManager mPackageManager;
    private Context mPrefContext;
    private ArrayMap<String, Preference> mPreferenceCache;
    private final String mPreferenceKey;
    private final UserManager mUserManager;

    /* loaded from: classes.dex */
    public interface Config {
        boolean shouldShowBatteryAttributionList(Context context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public BatteryAppListPreferenceController(Context context, String str, Lifecycle lifecycle, SettingsActivity settingsActivity, InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        super(context);
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.fuelgauge.BatteryAppListPreferenceController.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                SettingsActivity settingsActivity2;
                int i = message.what;
                if (i == 1) {
                    BatteryEntry batteryEntry = (BatteryEntry) message.obj;
                    PowerGaugePreference powerGaugePreference = (PowerGaugePreference) BatteryAppListPreferenceController.this.mAppListGroup.findPreference(batteryEntry.getKey());
                    if (powerGaugePreference != null) {
                        powerGaugePreference.setIcon(BatteryAppListPreferenceController.this.mUserManager.getBadgedIconForUser(batteryEntry.getIcon(), new UserHandle(UserHandle.getUserId(batteryEntry.getUid()))));
                        powerGaugePreference.setTitle(batteryEntry.mName);
                        if (batteryEntry.isAppEntry()) {
                            powerGaugePreference.setContentDescription(batteryEntry.mName);
                        }
                    }
                } else if (i == 2 && (settingsActivity2 = BatteryAppListPreferenceController.this.mActivity) != null) {
                    settingsActivity2.reportFullyDrawn();
                }
                super.handleMessage(message);
            }
        };
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mPreferenceKey = str;
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mPackageManager = context.getPackageManager();
        this.mActivity = settingsActivity;
        this.mFragment = instrumentedPreferenceFragment;
        this.mNotAllowShowSummaryPackages = Set.of((Object[]) FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).getHideApplicationSummary(context));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        BatteryEntry.stopRequestQueue();
        this.mHandler.removeMessages(1);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        if (this.mActivity.isChangingConfigurations()) {
            BatteryEntry.clearUidCache();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPrefContext = preferenceScreen.getContext();
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mAppListGroup = preferenceGroup;
        preferenceGroup.setTitle(this.mPrefContext.getString(R$string.power_usage_list_summary));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference instanceof PowerGaugePreference) {
            PowerGaugePreference powerGaugePreference = (PowerGaugePreference) preference;
            AdvancedPowerUsageDetail.startBatteryDetailPage(this.mActivity, this.mFragment, powerGaugePreference.getInfo(), powerGaugePreference.getPercent(), true);
            return true;
        }
        return false;
    }

    public void refreshAppListGroup(BatteryUsageStats batteryUsageStats, boolean z) {
        BatteryEntry batteryEntry;
        boolean z2;
        if (isAvailable()) {
            this.mBatteryUsageStats = batteryUsageStats;
            this.mAppListGroup.setTitle(R$string.power_usage_list_summary);
            cacheRemoveAllPrefs(this.mAppListGroup);
            boolean z3 = false;
            this.mAppListGroup.setOrderingAsAdded(false);
            int i = 1;
            if (sConfig.shouldShowBatteryAttributionList(this.mContext)) {
                int dischargePercentage = getDischargePercentage(batteryUsageStats);
                List<BatteryEntry> coalescedUsageList = getCoalescedUsageList(z, true);
                double consumedPower = batteryUsageStats.getConsumedPower();
                int size = coalescedUsageList.size();
                int i2 = 0;
                boolean z4 = false;
                while (true) {
                    if (i2 >= size) {
                        z3 = z4;
                        break;
                    }
                    BatteryEntry batteryEntry2 = coalescedUsageList.get(i2);
                    double calculateBatteryPercent = this.mBatteryUtils.calculateBatteryPercent(batteryEntry2.getConsumedPower(), consumedPower, dischargePercentage);
                    if (((int) (0.5d + calculateBatteryPercent)) < i) {
                        z2 = z3;
                    } else {
                        int uid = batteryEntry2.getUid();
                        UserHandle userHandle = new UserHandle(UserHandle.getUserId(uid));
                        Drawable badgedIconForUser = this.mUserManager.getBadgedIconForUser(batteryEntry2.getIcon(), userHandle);
                        CharSequence badgedLabelForUser = this.mUserManager.getBadgedLabelForUser(batteryEntry2.getLabel(), userHandle);
                        String key = batteryEntry2.getKey();
                        PowerGaugePreference powerGaugePreference = (PowerGaugePreference) getCachedPreference(key);
                        if (powerGaugePreference == null) {
                            batteryEntry = batteryEntry2;
                            powerGaugePreference = new PowerGaugePreference(this.mPrefContext, badgedIconForUser, badgedLabelForUser, batteryEntry);
                            powerGaugePreference.setKey(key);
                        } else {
                            batteryEntry = batteryEntry2;
                        }
                        batteryEntry.mPercent = calculateBatteryPercent;
                        powerGaugePreference.setTitle(batteryEntry.getLabel());
                        powerGaugePreference.setOrder(i2 + 1);
                        powerGaugePreference.setPercent(calculateBatteryPercent);
                        z2 = false;
                        powerGaugePreference.shouldShowAnomalyIcon(false);
                        powerGaugePreference.setEnabled((uid == -5 || uid == -4) ? false : true);
                        setUsageSummary(powerGaugePreference, batteryEntry);
                        this.mAppListGroup.addPreference(powerGaugePreference);
                        if (this.mAppListGroup.getPreferenceCount() - getCachedCount() > 21) {
                            z3 = true;
                            break;
                        }
                        z4 = true;
                    }
                    i2++;
                    z3 = z2;
                    i = 1;
                }
            }
            if (!z3) {
                addNotAvailableMessage();
            }
            removeCachedPrefs(this.mAppListGroup);
            BatteryEntry.startRequestQueue();
        }
    }

    private int getDischargePercentage(BatteryUsageStats batteryUsageStats) {
        int dischargePercentage = batteryUsageStats.getDischargePercentage();
        if (dischargePercentage < 0) {
            return 0;
        }
        return dischargePercentage;
    }

    private List<BatteryEntry> getCoalescedUsageList(boolean z, boolean z2) {
        boolean shouldHideUidBatteryConsumer;
        SparseArray sparseArray = new SparseArray();
        ArrayList arrayList = new ArrayList();
        List uidBatteryConsumers = this.mBatteryUsageStats.getUidBatteryConsumers();
        uidBatteryConsumers.sort(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.settings.fuelgauge.BatteryAppListPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int lambda$getCoalescedUsageList$0;
                lambda$getCoalescedUsageList$0 = BatteryAppListPreferenceController.this.lambda$getCoalescedUsageList$0((UidBatteryConsumer) obj);
                return lambda$getCoalescedUsageList$0;
            }
        }));
        int size = uidBatteryConsumers.size();
        for (int i = 0; i < size; i++) {
            BatteryConsumer batteryConsumer = (UidBatteryConsumer) uidBatteryConsumers.get(i);
            int realUid = getRealUid(batteryConsumer);
            String[] packagesForUid = this.mPackageManager.getPackagesForUid(realUid);
            if (!this.mBatteryUtils.shouldHideUidBatteryConsumerUnconditionally(batteryConsumer, packagesForUid) && (!(shouldHideUidBatteryConsumer = this.mBatteryUtils.shouldHideUidBatteryConsumer(batteryConsumer, packagesForUid)) || z)) {
                int indexOfKey = sparseArray.indexOfKey(realUid);
                if (indexOfKey < 0) {
                    sparseArray.put(realUid, new BatteryEntry(this.mContext, this.mHandler, this.mUserManager, batteryConsumer, shouldHideUidBatteryConsumer, realUid, packagesForUid, null, z2));
                } else {
                    ((BatteryEntry) sparseArray.valueAt(indexOfKey)).add(batteryConsumer);
                }
            }
        }
        BatteryConsumer aggregateBatteryConsumer = this.mBatteryUsageStats.getAggregateBatteryConsumer(0);
        BatteryConsumer aggregateBatteryConsumer2 = this.mBatteryUsageStats.getAggregateBatteryConsumer(1);
        for (int i2 = 0; i2 < 18; i2++) {
            if (z || !this.mBatteryUtils.shouldHideDevicePowerComponent(aggregateBatteryConsumer, i2)) {
                arrayList.add(new BatteryEntry(this.mContext, i2, aggregateBatteryConsumer.getConsumedPower(i2), aggregateBatteryConsumer2.getConsumedPower(i2), aggregateBatteryConsumer.getUsageDurationMillis(i2)));
            }
        }
        for (int i3 = 1000; i3 < aggregateBatteryConsumer.getCustomPowerComponentCount() + AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP; i3++) {
            if (z) {
                arrayList.add(new BatteryEntry(this.mContext, i3, aggregateBatteryConsumer.getCustomPowerComponentName(i3), aggregateBatteryConsumer.getConsumedPowerForCustomComponent(i3), aggregateBatteryConsumer2.getConsumedPowerForCustomComponent(i3)));
            }
        }
        if (z) {
            List userBatteryConsumers = this.mBatteryUsageStats.getUserBatteryConsumers();
            int size2 = userBatteryConsumers.size();
            for (int i4 = 0; i4 < size2; i4++) {
                arrayList.add(new BatteryEntry(this.mContext, this.mHandler, this.mUserManager, (UserBatteryConsumer) userBatteryConsumers.get(i4), true, -1, null, null, z2));
            }
        }
        int size3 = sparseArray.size();
        for (int i5 = 0; i5 < size3; i5++) {
            arrayList.add((BatteryEntry) sparseArray.valueAt(i5));
        }
        arrayList.sort(BatteryEntry.COMPARATOR);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$getCoalescedUsageList$0(UidBatteryConsumer uidBatteryConsumer) {
        return uidBatteryConsumer.getUid() == getRealUid(uidBatteryConsumer) ? 0 : 1;
    }

    private int getRealUid(UidBatteryConsumer uidBatteryConsumer) {
        int uid = uidBatteryConsumer.getUid();
        if (isSharedGid(uidBatteryConsumer.getUid())) {
            uid = UserHandle.getUid(0, UserHandle.getAppIdFromSharedAppGid(uidBatteryConsumer.getUid()));
        }
        return (!isSystemUid(uid) || "mediaserver".equals(uidBatteryConsumer.getPackageWithHighestDrain())) ? uid : AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP;
    }

    void setUsageSummary(Preference preference, BatteryEntry batteryEntry) {
        Set<CharSequence> set;
        if (BatteryEntry.isSystemUid(batteryEntry.getUid())) {
            return;
        }
        String defaultPackageName = batteryEntry.getDefaultPackageName();
        if (defaultPackageName == null || (set = this.mNotAllowShowSummaryPackages) == null || !set.contains(defaultPackageName)) {
            long timeInForegroundMs = batteryEntry.getTimeInForegroundMs();
            if (!shouldShowSummary(batteryEntry) || timeInForegroundMs < 60000) {
                return;
            }
            CharSequence formatElapsedTime = StringUtil.formatElapsedTime(this.mContext, timeInForegroundMs, false, false);
            if (!batteryEntry.isHidden()) {
                formatElapsedTime = TextUtils.expandTemplate(this.mContext.getText(R$string.battery_used_for), formatElapsedTime);
            }
            preference.setSummary(formatElapsedTime);
        }
    }

    private void cacheRemoveAllPrefs(PreferenceGroup preferenceGroup) {
        this.mPreferenceCache = new ArrayMap<>();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (!TextUtils.isEmpty(preference.getKey())) {
                this.mPreferenceCache.put(preference.getKey(), preference);
            }
        }
    }

    private boolean shouldShowSummary(BatteryEntry batteryEntry) {
        CharSequence[] hideApplicationSummary = FeatureFactory.getFactory(this.mContext).getPowerUsageFeatureProvider(this.mContext).getHideApplicationSummary(this.mContext);
        String defaultPackageName = batteryEntry.getDefaultPackageName();
        for (CharSequence charSequence : hideApplicationSummary) {
            if (TextUtils.equals(defaultPackageName, charSequence)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSharedGid(int i) {
        return UserHandle.getAppIdFromSharedAppGid(i) > 0;
    }

    private static boolean isSystemUid(int i) {
        int appId = UserHandle.getAppId(i);
        return appId >= 1000 && appId < 10000;
    }

    private Preference getCachedPreference(String str) {
        ArrayMap<String, Preference> arrayMap = this.mPreferenceCache;
        if (arrayMap != null) {
            return arrayMap.remove(str);
        }
        return null;
    }

    private void removeCachedPrefs(PreferenceGroup preferenceGroup) {
        for (Preference preference : this.mPreferenceCache.values()) {
            preferenceGroup.removePreference(preference);
        }
        this.mPreferenceCache = null;
    }

    private int getCachedCount() {
        ArrayMap<String, Preference> arrayMap = this.mPreferenceCache;
        if (arrayMap != null) {
            return arrayMap.size();
        }
        return 0;
    }

    private void addNotAvailableMessage() {
        if (getCachedPreference("not_available") == null) {
            Preference preference = new Preference(this.mPrefContext);
            preference.setKey("not_available");
            preference.setTitle(R$string.power_usage_not_available);
            preference.setSelectable(false);
            this.mAppListGroup.addPreference(preference);
        }
    }
}
