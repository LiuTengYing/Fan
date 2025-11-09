package com.android.settings.biometrics.fingerprint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.util.Log;
import java.util.List;
/* loaded from: classes.dex */
public class FingerprintReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String name;
        Log.v("FingerprintReceiver", " onReceive---- intent = " + intent);
        if ("com.sprd.fingerprint.startBIOManager".equals(intent.getAction())) {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService("fingerprint");
            Intent intent2 = new Intent();
            if (fingerprintManager != null) {
                List enrolledFingerprints = fingerprintManager.getEnrolledFingerprints();
                if ((enrolledFingerprints != null ? enrolledFingerprints.size() : 0) > 0) {
                    name = FingerprintSettings.class.getName();
                } else {
                    name = FingerprintEnrollIntroduction.class.getName();
                }
                intent2.setClassName("com.android.settings", name);
                intent2.setFlags(276824064);
                context.startActivity(intent2);
            }
        }
    }
}
