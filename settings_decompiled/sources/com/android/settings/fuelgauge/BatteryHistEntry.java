package com.android.settings.fuelgauge;

import android.content.ContentValues;
import java.time.Duration;
/* loaded from: classes.dex */
public class BatteryHistEntry {
    public final String mAppLabel;
    public final long mBackgroundUsageTimeInMs;
    public final int mBatteryHealth;
    public final int mBatteryLevel;
    public final int mBatteryStatus;
    public final long mBootTimestamp;
    public final double mConsumePower;
    public final int mConsumerType;
    public final int mDrainType;
    public final long mForegroundUsageTimeInMs;
    public final boolean mIsHidden;
    public final String mPackageName;
    public final double mPercentOfTotal;
    public final long mTimestamp;
    public final double mTotalPower;
    public final long mUid;
    public final long mUserId;
    public final String mZoneId;
    private String mKey = null;
    private boolean mIsValidEntry = true;

    public BatteryHistEntry(ContentValues contentValues) {
        this.mUid = getLong(contentValues, "uid");
        this.mUserId = getLong(contentValues, "userId");
        this.mAppLabel = getString(contentValues, "appLabel");
        this.mPackageName = getString(contentValues, "packageName");
        this.mIsHidden = getBoolean(contentValues, "isHidden");
        this.mBootTimestamp = getLong(contentValues, "bootTimestamp");
        this.mTimestamp = getLong(contentValues, "timestamp");
        this.mZoneId = getString(contentValues, "zoneId");
        this.mTotalPower = getDouble(contentValues, "totalPower");
        this.mConsumePower = getDouble(contentValues, "consumePower");
        this.mPercentOfTotal = getDouble(contentValues, "percentOfTotal");
        this.mForegroundUsageTimeInMs = getLong(contentValues, "foregroundUsageTimeInMs");
        this.mBackgroundUsageTimeInMs = getLong(contentValues, "backgroundUsageTimeInMs");
        this.mDrainType = getInteger(contentValues, "drainType");
        this.mConsumerType = getInteger(contentValues, "consumerType");
        this.mBatteryLevel = getInteger(contentValues, "batteryLevel");
        this.mBatteryStatus = getInteger(contentValues, "batteryStatus");
        this.mBatteryHealth = getInteger(contentValues, "batteryHealth");
    }

    public boolean isUserEntry() {
        return this.mConsumerType == 2;
    }

    public boolean isAppEntry() {
        return this.mConsumerType == 1;
    }

    public String getKey() {
        if (this.mKey == null) {
            int i = this.mConsumerType;
            if (i == 1) {
                this.mKey = Long.toString(this.mUid);
            } else if (i == 2) {
                this.mKey = "U|" + this.mUserId;
            } else if (i == 3) {
                this.mKey = "S|" + this.mDrainType;
            }
        }
        return this.mKey;
    }

    public String toString() {
        String utcToLocalTime = ConvertUtils.utcToLocalTime(null, this.mTimestamp);
        return "\nBatteryHistEntry{" + String.format("\n\tpackage=%s|label=%s|uid=%d|userId=%d|isHidden=%b", this.mPackageName, this.mAppLabel, Long.valueOf(this.mUid), Long.valueOf(this.mUserId), Boolean.valueOf(this.mIsHidden)) + String.format("\n\ttimestamp=%s|zoneId=%s|bootTimestamp=%d", utcToLocalTime, this.mZoneId, Long.valueOf(Duration.ofMillis(this.mBootTimestamp).getSeconds())) + String.format("\n\tusage=%f|total=%f|consume=%f|elapsedTime=%d|%d", Double.valueOf(this.mPercentOfTotal), Double.valueOf(this.mTotalPower), Double.valueOf(this.mConsumePower), Long.valueOf(Duration.ofMillis(this.mForegroundUsageTimeInMs).getSeconds()), Long.valueOf(Duration.ofMillis(this.mBackgroundUsageTimeInMs).getSeconds())) + String.format("\n\tdrainType=%d|consumerType=%d", Integer.valueOf(this.mDrainType), Integer.valueOf(this.mConsumerType)) + String.format("\n\tbattery=%d|status=%d|health=%d\n}", Integer.valueOf(this.mBatteryLevel), Integer.valueOf(this.mBatteryStatus), Integer.valueOf(this.mBatteryHealth));
    }

    private int getInteger(ContentValues contentValues, String str) {
        if (contentValues != null && contentValues.containsKey(str)) {
            return contentValues.getAsInteger(str).intValue();
        }
        this.mIsValidEntry = false;
        return 0;
    }

    private long getLong(ContentValues contentValues, String str) {
        if (contentValues != null && contentValues.containsKey(str)) {
            return contentValues.getAsLong(str).longValue();
        }
        this.mIsValidEntry = false;
        return 0L;
    }

    private double getDouble(ContentValues contentValues, String str) {
        if (contentValues != null && contentValues.containsKey(str)) {
            return contentValues.getAsDouble(str).doubleValue();
        }
        this.mIsValidEntry = false;
        return 0.0d;
    }

    private String getString(ContentValues contentValues, String str) {
        if (contentValues != null && contentValues.containsKey(str)) {
            return contentValues.getAsString(str);
        }
        this.mIsValidEntry = false;
        return null;
    }

    private boolean getBoolean(ContentValues contentValues, String str) {
        if (contentValues != null && contentValues.containsKey(str)) {
            return contentValues.getAsBoolean(str).booleanValue();
        }
        this.mIsValidEntry = false;
        return false;
    }
}
