package com.android.settings.wifidialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.util.Log;
/* loaded from: classes2.dex */
public class WifiReceiver extends BroadcastReceiver {
    private WifiStateListener wifiStateListener;

    /* loaded from: classes2.dex */
    public interface WifiStateListener {
        void connectError();

        void connectSuccessful();

        void connecting();

        void onHomeClick();

        void onWifiClose();

        void onWifiOpen();

        void scanComplete();
    }

    public void setWifiStateListener(WifiStateListener wifiStateListener) {
        this.wifiStateListener = wifiStateListener;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        WifiStateListener wifiStateListener;
        WifiStateListener wifiStateListener2;
        if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            int intExtra = intent.getIntExtra("wifi_state", 4);
            if (intExtra == 1) {
                setWifiState(false);
            } else if (intExtra == 3) {
                setWifiState(true);
            }
        }
        if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE")) {
            SupplicantState supplicantState = (SupplicantState) intent.getParcelableExtra("newState");
            Log.d("fangli", "onReceive: " + supplicantState);
            if (supplicantState == null) {
                WifiStateListener wifiStateListener3 = this.wifiStateListener;
                if (wifiStateListener3 != null) {
                    wifiStateListener3.connectError();
                    return;
                }
                return;
            }
            switch (AnonymousClass1.$SwitchMap$android$net$wifi$SupplicantState[supplicantState.ordinal()]) {
                case 1:
                    WifiStateListener wifiStateListener4 = this.wifiStateListener;
                    if (wifiStateListener4 != null) {
                        wifiStateListener4.connectSuccessful();
                        break;
                    }
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    WifiStateListener wifiStateListener5 = this.wifiStateListener;
                    if (wifiStateListener5 != null) {
                        wifiStateListener5.connecting();
                        break;
                    }
                    break;
                case 6:
                    WifiStateListener wifiStateListener6 = this.wifiStateListener;
                    if (wifiStateListener6 != null) {
                        wifiStateListener6.connectError();
                        break;
                    }
                    break;
            }
        }
        if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.SCAN_RESULTS") && (wifiStateListener2 = this.wifiStateListener) != null) {
            wifiStateListener2.scanComplete();
        }
        if (intent.getAction() == null || !intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS") || (wifiStateListener = this.wifiStateListener) == null) {
            return;
        }
        wifiStateListener.onHomeClick();
    }

    /* renamed from: com.android.settings.wifidialog.WifiReceiver$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$net$wifi$SupplicantState;

        static {
            int[] iArr = new int[SupplicantState.values().length];
            $SwitchMap$android$net$wifi$SupplicantState = iArr;
            try {
                iArr[SupplicantState.COMPLETED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.FOUR_WAY_HANDSHAKE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.SCANNING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.ASSOCIATING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.ASSOCIATED.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DISCONNECTED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private void setWifiState(boolean z) {
        WifiStateListener wifiStateListener = this.wifiStateListener;
        if (wifiStateListener != null) {
            if (z) {
                wifiStateListener.onWifiOpen();
            } else {
                wifiStateListener.onWifiClose();
            }
        }
    }
}
