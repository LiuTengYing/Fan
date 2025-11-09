package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.R$bool;
import com.android.settings.fuelgauge.BatteryEntry;
import com.android.settingslib.utils.StringUtil;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes.dex */
public class BatteryDiffEntry {
    static Locale sCurrentLocale;
    int mAppIconId;
    public long mBackgroundUsageTimeInMs;
    public final BatteryHistEntry mBatteryHistEntry;
    public double mConsumePower;
    private Context mContext;
    public long mForegroundUsageTimeInMs;
    private double mPercentOfTotal;
    private double mTotalConsumePower;
    private UserManager mUserManager;
    static final Map<String, BatteryEntry.NameAndIcon> sResourceCache = new HashMap();
    public static final Map<String, Boolean> sValidForRestriction = new HashMap();
    public static final Comparator<BatteryDiffEntry> COMPARATOR = new Comparator() { // from class: com.android.settings.fuelgauge.BatteryDiffEntry$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = BatteryDiffEntry.lambda$static$0((BatteryDiffEntry) obj, (BatteryDiffEntry) obj2);
            return lambda$static$0;
        }
    };
    private String mDefaultPackageName = null;
    String mAppLabel = null;
    Drawable mAppIcon = null;
    boolean mIsLoaded = false;
    boolean mValidForRestriction = true;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(BatteryDiffEntry batteryDiffEntry, BatteryDiffEntry batteryDiffEntry2) {
        return Double.compare(batteryDiffEntry2.getPercentOfTotal(), batteryDiffEntry.getPercentOfTotal());
    }

    public BatteryDiffEntry(Context context, long j, long j2, double d, BatteryHistEntry batteryHistEntry) {
        this.mContext = context;
        this.mConsumePower = d;
        this.mForegroundUsageTimeInMs = j;
        this.mBackgroundUsageTimeInMs = j2;
        this.mBatteryHistEntry = batteryHistEntry;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    public void setTotalConsumePower(double d) {
        this.mTotalConsumePower = d;
        this.mPercentOfTotal = d != 0.0d ? (this.mConsumePower / d) * 100.0d : 0.0d;
    }

    public double getPercentOfTotal() {
        return this.mPercentOfTotal;
    }

    /* renamed from: clone */
    public BatteryDiffEntry m1093clone() {
        return new BatteryDiffEntry(this.mContext, this.mForegroundUsageTimeInMs, this.mBackgroundUsageTimeInMs, this.mConsumePower, this.mBatteryHistEntry);
    }

    public String getAppLabel() {
        loadLabelAndIcon();
        String str = this.mAppLabel;
        if (str == null || str.length() == 0) {
            return this.mBatteryHistEntry.mAppLabel;
        }
        return this.mAppLabel;
    }

    public Drawable getAppIcon() {
        loadLabelAndIcon();
        Drawable drawable = this.mAppIcon;
        if (drawable == null || drawable.getConstantState() == null) {
            return null;
        }
        return this.mAppIcon.getConstantState().newDrawable();
    }

    public int getAppIconId() {
        loadLabelAndIcon();
        return this.mAppIconId;
    }

    public String getPackageName() {
        String[] split;
        String str = this.mDefaultPackageName;
        if (str == null) {
            str = this.mBatteryHistEntry.mPackageName;
        }
        return (str == null || (split = str.split(":")) == null || split.length <= 0) ? str : split[0];
    }

    public boolean validForRestriction() {
        loadLabelAndIcon();
        return this.mValidForRestriction;
    }

    public boolean isSystemEntry() {
        BatteryHistEntry batteryHistEntry = this.mBatteryHistEntry;
        int i = batteryHistEntry.mConsumerType;
        if (i != 1) {
            return i == 2 || i == 3;
        }
        int i2 = (int) batteryHistEntry.mUid;
        if (batteryHistEntry.mIsHidden || i2 == -4 || i2 == -5) {
            return true;
        }
        return this.mContext.getResources().getBoolean(R$bool.config_battery_combine_system_components) && isSystemUid(i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadLabelAndIcon() {
        BatteryEntry.NameAndIcon nameAndIconFromPowerComponent;
        if (this.mIsLoaded) {
            return;
        }
        BatteryEntry.NameAndIcon cache = getCache();
        if (cache != null) {
            this.mAppLabel = cache.mName;
            this.mAppIcon = cache.mIcon;
            this.mAppIconId = cache.mIconId;
        }
        Map<String, Boolean> map = sValidForRestriction;
        Boolean bool = map.get(getKey());
        if (bool != null) {
            this.mValidForRestriction = bool.booleanValue();
        }
        if (cache == null || bool == null) {
            this.mIsLoaded = true;
            updateRestrictionFlagState();
            map.put(getKey(), Boolean.valueOf(this.mValidForRestriction));
            BatteryHistEntry batteryHistEntry = this.mBatteryHistEntry;
            int i = batteryHistEntry.mConsumerType;
            if (i == 1) {
                loadNameAndIconForUid();
                if (this.mAppIcon == null) {
                    this.mAppIcon = this.mContext.getPackageManager().getDefaultActivityIcon();
                }
                Drawable badgeIconForUser = getBadgeIconForUser(this.mAppIcon);
                this.mAppIcon = badgeIconForUser;
                if (this.mAppLabel == null && badgeIconForUser == null) {
                    return;
                }
                sResourceCache.put(getKey(), new BatteryEntry.NameAndIcon(this.mAppLabel, this.mAppIcon, 0));
            } else if (i == 2) {
                BatteryEntry.NameAndIcon nameAndIconFromUserId = BatteryEntry.getNameAndIconFromUserId(this.mContext, (int) batteryHistEntry.mUserId);
                if (nameAndIconFromUserId != null) {
                    this.mAppIcon = nameAndIconFromUserId.mIcon;
                    this.mAppLabel = nameAndIconFromUserId.mName;
                    sResourceCache.put(getKey(), new BatteryEntry.NameAndIcon(this.mAppLabel, this.mAppIcon, 0));
                }
            } else if (i == 3 && (nameAndIconFromPowerComponent = BatteryEntry.getNameAndIconFromPowerComponent(this.mContext, batteryHistEntry.mDrainType)) != null) {
                this.mAppLabel = nameAndIconFromPowerComponent.mName;
                int i2 = nameAndIconFromPowerComponent.mIconId;
                if (i2 != 0) {
                    this.mAppIconId = i2;
                    this.mAppIcon = this.mContext.getDrawable(i2);
                }
                sResourceCache.put(getKey(), new BatteryEntry.NameAndIcon(this.mAppLabel, this.mAppIcon, this.mAppIconId));
            }
        }
    }

    String getKey() {
        return this.mBatteryHistEntry.getKey();
    }

    void updateRestrictionFlagState() {
        this.mValidForRestriction = true;
        if (this.mBatteryHistEntry.isAppEntry()) {
            if (!(BatteryUtils.getInstance(this.mContext).getPackageUid(getPackageName()) != -1)) {
                this.mValidForRestriction = false;
                return;
            }
            try {
                this.mValidForRestriction = this.mContext.getPackageManager().getPackageInfo(getPackageName(), 4198976) != null;
            } catch (Exception e) {
                Log.e("BatteryDiffEntry", String.format("getPackageInfo() error %s for package=%s", e.getCause(), getPackageName()));
                this.mValidForRestriction = false;
            }
        }
    }

    private BatteryEntry.NameAndIcon getCache() {
        Locale locale = Locale.getDefault();
        Locale locale2 = sCurrentLocale;
        if (locale2 != locale) {
            Log.d("BatteryDiffEntry", String.format("clearCache() locale is changed from %s to %s", locale2, locale));
            sCurrentLocale = locale;
            clearCache();
        }
        return sResourceCache.get(getKey());
    }

    private void loadNameAndIconForUid() {
        String packageName = getPackageName();
        PackageManager packageManager = this.mContext.getPackageManager();
        if (packageName != null && packageName.length() != 0) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                if (applicationInfo != null) {
                    this.mAppLabel = packageManager.getApplicationLabel(applicationInfo).toString();
                    this.mAppIcon = packageManager.getApplicationIcon(applicationInfo);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("BatteryDiffEntry", "failed to retrieve ApplicationInfo for: " + packageName);
                this.mAppLabel = packageName;
            }
        }
        if (this.mAppLabel == null || this.mAppIcon == null) {
            int i = (int) this.mBatteryHistEntry.mUid;
            String[] packagesForUid = packageManager.getPackagesForUid(i);
            if (packagesForUid == null || packagesForUid.length == 0) {
                BatteryEntry.NameAndIcon nameAndIconFromUid = BatteryEntry.getNameAndIconFromUid(this.mContext, this.mAppLabel, i);
                this.mAppLabel = nameAndIconFromUid.mName;
                this.mAppIcon = nameAndIconFromUid.mIcon;
            }
            BatteryEntry.NameAndIcon loadNameAndIcon = BatteryEntry.loadNameAndIcon(this.mContext, i, null, null, packageName, this.mAppLabel, this.mAppIcon);
            BatteryEntry.clearUidCache();
            if (loadNameAndIcon != null) {
                this.mAppLabel = loadNameAndIcon.mName;
                this.mAppIcon = loadNameAndIcon.mIcon;
                String str = loadNameAndIcon.mPackageName;
                this.mDefaultPackageName = str;
                if (str == null || str.equals(str)) {
                    return;
                }
                Log.w("BatteryDiffEntry", String.format("found different package: %s | %s", this.mDefaultPackageName, loadNameAndIcon.mPackageName));
            }
        }
    }

    public String toString() {
        return "BatteryDiffEntry{" + String.format("\n\tname=%s restrictable=%b", this.mAppLabel, Boolean.valueOf(this.mValidForRestriction)) + String.format("\n\tconsume=%.2f%% %f/%f", Double.valueOf(this.mPercentOfTotal), Double.valueOf(this.mConsumePower), Double.valueOf(this.mTotalConsumePower)) + String.format("\n\tforeground:%s background:%s", StringUtil.formatElapsedTime(this.mContext, this.mForegroundUsageTimeInMs, true, false), StringUtil.formatElapsedTime(this.mContext, this.mBackgroundUsageTimeInMs, true, false)) + String.format("\n\tpackage:%s|%s uid:%d userId:%d", this.mBatteryHistEntry.mPackageName, getPackageName(), Long.valueOf(this.mBatteryHistEntry.mUid), Long.valueOf(this.mBatteryHistEntry.mUserId));
    }

    public static void clearCache() {
        sResourceCache.clear();
        sValidForRestriction.clear();
    }

    private Drawable getBadgeIconForUser(Drawable drawable) {
        int userId = UserHandle.getUserId((int) this.mBatteryHistEntry.mUid);
        return userId == 0 ? drawable : this.mUserManager.getBadgedIconForUser(drawable, new UserHandle(userId));
    }

    private static boolean isSystemUid(int i) {
        int appId = UserHandle.getAppId(i);
        return appId >= 1000 && appId < 10000;
    }
}
