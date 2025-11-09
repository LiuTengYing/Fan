package com.android.settingslib.wifi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.MessageFormat;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.SystemClock;
import android.util.Log;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes2.dex */
public class WifiUtils {
    static final String ACTION_WIFI_DIALOG = "com.android.settings.WIFI_DIALOG";
    static final String EXTRA_CHOSEN_WIFI_ENTRY_KEY = "key_chosen_wifientry_key";
    static final String EXTRA_CONNECT_FOR_CALLER = "connect_for_caller";
    static final int[] WIFI_PIE = {R$drawable.ic_wifi_signal_0, R$drawable.ic_wifi_signal_1, R$drawable.ic_wifi_signal_2, R$drawable.ic_wifi_signal_3, R$drawable.ic_wifi_signal_4};
    static final int[] NO_INTERNET_WIFI_PIE = {R$drawable.ic_no_internet_wifi_signal_0, R$drawable.ic_no_internet_wifi_signal_1, R$drawable.ic_no_internet_wifi_signal_2, R$drawable.ic_no_internet_wifi_signal_3, R$drawable.ic_no_internet_wifi_signal_4};

    static String getVisibilityStatus(AccessPoint accessPoint) {
        String str;
        StringBuilder sb;
        WifiInfo info = accessPoint.getInfo();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        StringBuilder sb4 = new StringBuilder();
        StringBuilder sb5 = new StringBuilder();
        int i = 0;
        if (!accessPoint.isActive() || info == null) {
            str = null;
        } else {
            str = info.getBSSID();
            if (str != null) {
                sb2.append(" ");
                sb2.append(str);
            }
            sb2.append(" standard = ");
            sb2.append(info.getWifiStandard());
            sb2.append(" rssi=");
            sb2.append(info.getRssi());
            sb2.append(" ");
            sb2.append(" score=");
            sb2.append(info.getScore());
            if (accessPoint.getSpeed() != 0) {
                sb2.append(" speed=");
                sb2.append(accessPoint.getSpeedLabel());
            }
            sb2.append(String.format(" tx=%.1f,", Double.valueOf(info.getSuccessfulTxPacketsPerSecond())));
            sb2.append(String.format("%.1f,", Double.valueOf(info.getRetriedTxPacketsPerSecond())));
            sb2.append(String.format("%.1f ", Double.valueOf(info.getLostTxPacketsPerSecond())));
            sb2.append(String.format("rx=%.1f", Double.valueOf(info.getSuccessfulRxPacketsPerSecond())));
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Iterator<ScanResult> it = accessPoint.getScanResults().iterator();
        int i2 = 0;
        int i3 = -127;
        int i4 = -127;
        int i5 = -127;
        int i6 = 0;
        while (true) {
            sb = sb2;
            if (!it.hasNext()) {
                break;
            }
            ScanResult next = it.next();
            if (next == null) {
                sb2 = sb;
            } else {
                int i7 = next.frequency;
                Iterator<ScanResult> it2 = it;
                if (i7 >= 4900 && i7 <= 5900) {
                    i6++;
                    int i8 = next.level;
                    if (i8 > i4) {
                        i4 = i8;
                    }
                    if (i6 <= 4) {
                        sb4.append(verboseScanResultSummary(accessPoint, next, str, elapsedRealtime));
                    }
                } else if (i7 >= 2400 && i7 <= 2500) {
                    i++;
                    int i9 = next.level;
                    if (i9 > i3) {
                        i3 = i9;
                    }
                    if (i <= 4) {
                        sb3.append(verboseScanResultSummary(accessPoint, next, str, elapsedRealtime));
                    }
                } else if (i7 >= 58320 && i7 <= 70200) {
                    i2++;
                    int i10 = next.level;
                    if (i10 > i5) {
                        i5 = i10;
                    }
                    if (i2 <= 4) {
                        sb5.append(verboseScanResultSummary(accessPoint, next, str, elapsedRealtime));
                    }
                }
                sb2 = sb;
                it = it2;
            }
        }
        sb.append(" [");
        if (i > 0) {
            sb.append("(");
            sb.append(i);
            sb.append(")");
            if (i > 4) {
                sb.append("max=");
                sb.append(i3);
                sb.append(",");
            }
            sb.append(sb3.toString());
        }
        sb.append(";");
        if (i6 > 0) {
            sb.append("(");
            sb.append(i6);
            sb.append(")");
            if (i6 > 4) {
                sb.append("max=");
                sb.append(i4);
                sb.append(",");
            }
            sb.append(sb4.toString());
        }
        sb.append(";");
        if (i2 > 0) {
            sb.append("(");
            sb.append(i2);
            sb.append(")");
            if (i2 > 4) {
                sb.append("max=");
                sb.append(i5);
                sb.append(",");
            }
            sb.append(sb5.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    static String verboseScanResultSummary(AccessPoint accessPoint, ScanResult scanResult, String str, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        if (scanResult.BSSID.equals(str)) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        int specificApSpeed = getSpecificApSpeed(scanResult, accessPoint.getScoredNetworkCache());
        if (specificApSpeed != 0) {
            sb.append(",");
            sb.append(accessPoint.getSpeedLabel(specificApSpeed));
        }
        int i = ((int) (j - (scanResult.timestamp / 1000))) / AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP;
        sb.append(",");
        sb.append(i);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }

    private static int getSpecificApSpeed(ScanResult scanResult, Map<String, TimestampedScoredNetwork> map) {
        TimestampedScoredNetwork timestampedScoredNetwork = map.get(scanResult.BSSID);
        if (timestampedScoredNetwork == null) {
            return 0;
        }
        return timestampedScoredNetwork.getScore().calculateBadge(scanResult.level);
    }

    public static int getInternetIconResource(int i, boolean z) {
        if (i < 0) {
            Log.e("WifiUtils", "Wi-Fi level is out of range! level:" + i);
            i = 0;
        } else {
            int[] iArr = WIFI_PIE;
            if (i >= iArr.length) {
                Log.e("WifiUtils", "Wi-Fi level is out of range! level:" + i);
                i = iArr.length + (-1);
            }
        }
        return z ? NO_INTERNET_WIFI_PIE[i] : WIFI_PIE[i];
    }

    /* loaded from: classes2.dex */
    public static class InternetIconInjector {
        protected final Context mContext;

        public InternetIconInjector(Context context) {
            this.mContext = context;
        }

        public Drawable getIcon(boolean z, int i) {
            return this.mContext.getDrawable(WifiUtils.getInternetIconResource(i, z));
        }
    }

    public static String getWifiTetherSummaryForConnectedDevices(Context context, int i) {
        MessageFormat messageFormat = new MessageFormat(context.getResources().getString(R$string.wifi_tether_connected_summary), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put("count", Integer.valueOf(i));
        return messageFormat.format(hashMap);
    }
}
